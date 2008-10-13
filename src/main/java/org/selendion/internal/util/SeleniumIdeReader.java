/*
        Copyright Joan McGalliard, 2008
*/

package org.selendion.internal.util;

import org.concordion.api.Evaluator;
import org.concordion.api.Element;
import org.concordion.api.Result;
import org.concordion.api.ResultRecorder;
import org.concordion.internal.util.Announcer;
import org.selendion.integration.selenium.SeleniumDriver;
import org.selendion.internal.command.RunSeleniumSuccessEvent;
import org.selendion.internal.command.RunSeleniumFailureEvent;
import org.selendion.internal.RunSeleniumListener;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLReaderFactory;
import org.ccil.cowan.tagsoup.Parser;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Set;
import java.util.Vector;
import java.io.IOException;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.InputStream;

import com.thoughtworks.selenium.SeleniumException;


public class SeleniumIdeReader {
    private SeleniumDriver selenium;
    private boolean started = false;


    public void start(String seleniumHost, int seleniumPort, String browser, String baseUrl) {
        selenium = new SeleniumDriver(
                seleniumHost, seleniumPort, browser,
                baseUrl);
        selenium.start();
        started = true;
    }

    public void stop() {
        if (started) {
            selenium.stop();
        }
    }

    public boolean runSeleniumScript(String filepath, Evaluator evaluator) throws Exception {
        Vector<String[]> seleniumCommands = readSelenium(filepath);
        turnConcordionVarsToSeleniumVars(evaluator);
        boolean result = true;
        for (String[] command : seleniumCommands) {

            if (command[1] != null && command[2] != null && execute(command[0], command[1], command[2]) != null) {

                result = false;
            }
        }
        turnSeleniumVarsToConcordionVars(evaluator);
        return result;


    }


    public boolean runSeleniumScript(String filepath, Evaluator evaluator, Element element, Announcer<RunSeleniumListener> listeners, int buttonId, ResultRecorder resultRecorder) throws Exception {
        Vector<String[]> seleniumCommands = readSelenium(filepath);
        turnConcordionVarsToSeleniumVars(evaluator);
        boolean result = true;
        Element table = new Element("table");
        Element tr = new Element("tr");
        Element td = new Element("th");
        td.addAttribute("colspan", "4");
        td.appendText(element.getText());
        tr.appendChild(td);
        table.appendChild(tr);

        for (String[] command : seleniumCommands) {


            if (command[1] != null && command[2] != null) {
                tr = new Element("tr");
                td = new Element("td").appendText(command[0]);
                tr.appendChild(td);
                td = new Element("td").appendText(command[1]);
                tr.appendChild(td);
                td = new Element("td").appendText(command[2]);
                tr.appendChild(td);

                CommandResult rowResponse = execute(command[0], command[1], command[2]);
                td = new Element("td").appendText(rowResponse.getMessage());
                tr.appendChild(td);
                if (rowResponse.getSuccess()) {
                    listeners.announce().successReported(new RunSeleniumSuccessEvent(tr));
                    resultRecorder.record(Result.SUCCESS);
                } else {
                    result = false;
                    listeners.announce().failureReported(new RunSeleniumFailureEvent(tr));
                    resultRecorder.record(Result.FAILURE);
                }
                table.appendChild(tr);
            }
        }
        Element span = new Element("span");
        span.appendChild(new Element("input")
                .addStyleClass("seleniumTableButton")
                .setId("seleniumTableButton" + buttonId)
                .addAttribute("type", "button")
                .addAttribute("type", "button")
                .addAttribute("class", result ? "success" : "failure")
                .addAttribute("onclick", "javascript:toggleSeleniumTable('" + buttonId + "', '" + element.getText() + "')")
                .addAttribute("value", element.getText()));
        table.setId("seleniumTable" + buttonId);
        table.addAttribute("class", "seleniumTable");
        span.appendChild(table);
        element.insertAfter(span);
        element.addAttribute("class", "invisible");
        turnSeleniumVarsToConcordionVars(evaluator);
        return result;
    }

    private void turnConcordionVarsToSeleniumVars(Evaluator evaluator) throws Exception {
        Set keys = evaluator.getKeys();
        String array = "";
        for (Object key : keys) {
            if (!key.getClass().equals(String.class)) {
                throw new Exception("Unexpected key " + key);
            }
            String keyString = (String) key;
            if (keyString.matches("^[a-z].*")) {
                array = String.format("%s %s: '%s', ", array, keyString, evaluator.getVariable("#" + keyString).toString().replaceFirst("\\\\|$", "") .replaceAll("'", "\\\\'") );

            }
        }

        array = array.replaceFirst("[, ]$", "");
        if (array.length() > 0) {
            selenium.getEval("storedVars = {" + array + "}");
        }
    }

    private void turnSeleniumVarsToConcordionVars(Evaluator evaluator) {
        String[] storedVars = selenium.getEval("var arr = [];for (var name in storedVars) {arr.push('#'+name+' '+storedVars[name]);};arr").split(",");
        for (String var : storedVars) {
            String[] nvp = var.split(" ", 2);
            if (nvp[0].matches("^#[a-z].*")) {
                evaluator.setVariable(nvp[0], nvp[1]);
            }
        }
    }


    private CommandResult execute(String command, String arg1, String arg2)
            throws Exception {
        if (!started) {
            throw new Exception("Please start selenium before running scripts.");
        }
        try {
        System.out.println(command);
        command = replaceCharacterEntities(command);
        arg1 = replaceCharacterEntities(arg1);
        arg2 = replaceCharacterEntities(arg2);

        if (command.matches(".*AndWait$")) {
            CommandResult result=execute(command.replaceFirst("AndWait$", ""), arg1, arg2);
            if (result.getSuccess()) {

                selenium.waitForPageToLoad();
            }
            return result;
        }
        //All getFoo and isFoo methods on the Selenium prototype automatically result in the availability
        // of storeFoo, assertFoo, assertNotFoo, verifyFoo, verifyNotFoo, waitForFoo, and waitForNotFoo commands.

        if (command.matches("^store[A-Z].*")) {
            try {
                storeVar(arg1,seleniumGetNoParams(command.replaceFirst("^store", "")));
            } catch (SeleniumIdeException e) {
                try {
                    storeVar(arg2,seleniumGetOneParam(command.replaceFirst("^store", ""), arg1));
                } catch (SeleniumIdeException e1) {
                    return new CommandResult(false, "Unimplemented command " + command);
                }
            }
            return new CommandResult(true, ""); 
        }
        if (command.matches("^assertNot[A-Z].*")) {
            Object actualObject;
            String expected;
            try {
                actualObject = seleniumGetNoParams(command.replaceFirst("^assertNot", ""));
                expected = arg1;
            } catch (SeleniumIdeException e) {
                try {
                    actualObject = seleniumGetOneParam(command.replaceFirst("^assertNot", ""), arg1);
                } catch (SeleniumIdeException e1) {
                    return new CommandResult(false, "Unimplemented command " + command);
                }
                expected = arg2;
            }
            String actual;
            if (actualObject.getClass() == String.class) {
                actual=(String)actualObject;
            } else if (actualObject.getClass() == Number.class) {
                actual= ((Number)actualObject).toString();
            } else {
                throw new Exception ("@TODO");
            }

            if (actual.equals(expected)) {
                return new CommandResult(false, "Failed");

            } else {
                return new CommandResult(true, "");
                
            }
        }
        if (command.matches("^assert[A-Z].*")) {
            Object actualObject;
            String expected;
            try {
                actualObject = seleniumGetNoParams(command.replaceFirst("^assert", ""));
                expected = arg1;
            } catch (SeleniumIdeException e) {
                try {
                    actualObject = seleniumGetOneParam(command.replaceFirst("^assert", ""), arg1);
                } catch (SeleniumIdeException e1) {
                    return new CommandResult(false, "Unimplemented command " + command);
                }
                expected = arg2;
            }
            String actual;
            if (actualObject.getClass() == String.class) {
                actual=(String)actualObject;
            } else if (actualObject.getClass() == Number.class) {
                actual= ((Number)actualObject).toString();
            } else {
                throw new Exception ("@TODO");
            }

            if (actual.equals(expected)) {
                return new CommandResult(true, "");
            } else {
                return new CommandResult(false, "Expected: " + expected + "; Actual: " + actualObject);
            }
        }

            // Selenium actions
            if (command.equals("addSelection")) {
                selenium.addSelection(arg1, arg2);
                return new CommandResult(true, "");
            }
            if (command.equals("allowNativeXpath")) {
                selenium.allowNativeXpath(arg1);
                return new CommandResult(true, "");
            }
            if (command.equals("altKeyDown")) {
                selenium.altKeyDown();
                return new CommandResult(true, "");
            }
            if (command.equals("altKeyUp")) {
                selenium.altKeyUp();
                return new CommandResult(true, "");
            }
            if (command.equals("answerOnNextPrompt")) {
                selenium.answerOnNextPrompt(arg1);
                return new CommandResult(true, "");
            }
            if (command.equals("assignId")) {
                selenium.assignId(arg1, arg2);
                return new CommandResult(true, "");
            }
            if (command.equals("break")) {
                //ignore breaks in automated tests
                return new CommandResult(true, "");
            }
            if (command.equals("check")) {
                selenium.check(arg1);
                return new CommandResult(true, "");
            }
            if (command.equals("chooseCancelOnNextConfirmation")) {
                selenium.chooseCancelOnNextConfirmation();
                return new CommandResult(true, "");
            }
            if (command.equals("chooseOkOnNextConfirmation")) {
                selenium.chooseOkOnNextConfirmation();
                return new CommandResult(true, "");
            }
            if (command.equals("click")) {
                selenium.click(arg1);
                return new CommandResult(true, "");
            }
            if (command.equals("clickAt")) {
                selenium.clickAt(arg1, arg2);
                return new CommandResult(true, "");
            }
            if (command.equals("close")) {
                selenium.close();
                return new CommandResult(true, "");
            }
            if (command.equals("controlKeyDown")) {
                selenium.controlKeyDown();
                return new CommandResult(true, "");
            }
            if (command.equals("controlKeyUp")) {
                selenium.controlKeyUp();
                return new CommandResult(true, "");
            }
            if (command.equals("createCookie")) {
                selenium.createCookie(arg1, arg2);
                return new CommandResult(true, "");
            }
            if (command.equals("deleteCookie")) {
                selenium.deleteCookie(arg1, arg2);
                return new CommandResult(true, "");
            }
            if (command.equals("doubleClick")) {
                selenium.doubleClick(arg1);
                return new CommandResult(true, "");
            }
            if (command.equals("doubleClickAt")) {
                selenium.doubleClickAt(arg1, arg2);
                return new CommandResult(true, "");
            }
            if (command.equals("dragAndDrop")) {
                selenium.dragAndDrop(arg1, arg2);
                return new CommandResult(true, "");
            }
            if (command.equals("dragAndDropToObject")) {
                selenium.dragAndDropToObject(arg1, arg2);
                return new CommandResult(true, "");
            }
            if (command.equals("dragdrop")) {
                selenium.dragdrop(arg1, arg2);
                return new CommandResult(true, "");
            }
            if (command.equals("echo")) {
                return new CommandResult(true, replaceVariables(arg1));
            }
            if (command.equals("fireEvent")) {
                selenium.fireEvent(arg1, arg2);
                return new CommandResult(true, "");
            }
            if (command.equals("getSpeed")) {
                selenium.getSpeed();
                return new CommandResult(true, "");
            }
            if (command.equals("goBack")) {
                selenium.goBack();
                return new CommandResult(true, "");
            }
            if (command.equals("highlight")) {
                selenium.highlight(arg1);
                return new CommandResult(true, "");
            }
            if (command.equals("keyDown")) {
                selenium.keyDown(arg1, arg2);
                return new CommandResult(true, "");
            }
            if (command.equals("keyPress")) {
                selenium.keyPress(arg1, arg2);
                return new CommandResult(true, "");
            }
            if (command.equals("keyUp")) {
                selenium.keyUp(arg1, arg2);
                return new CommandResult(true, "");
            }
            if (command.equals("metaKeyDown")) {
                selenium.metaKeyDown();
                return new CommandResult(true, "");
            }
            if (command.equals("metaKeyUp")) {
                selenium.metaKeyUp();
                return new CommandResult(true, "");
            }
            if (command.equals("mouseDown")) {
                selenium.mouseDown(arg1);
                return new CommandResult(true, "");
            }
            if (command.equals("mouseDownAt")) {
                selenium.mouseDownAt(arg1, arg2);
                return new CommandResult(true, "");
            }
            if (command.equals("mouseMove")) {
                selenium.mouseMove(arg1);
                return new CommandResult(true, "");
            }
            if (command.equals("mouseMoveAt")) {
                selenium.mouseMoveAt(arg1, arg2);
                return new CommandResult(true, "");
            }
            if (command.equals("mouseOut")) {
                selenium.mouseOut(arg1);
                return new CommandResult(true, "");
            }
            if (command.equals("mouseOver")) {
                selenium.mouseOver(arg1);
                return new CommandResult(true, "");
            }
            if (command.equals("mouseUp")) {
                selenium.mouseUp(arg1);
                return new CommandResult(true, "");
            }
            if (command.equals("mouseUpAt")) {
                selenium.mouseUpAt(arg1, arg2);
                return new CommandResult(true, "");
            }
            if (command.equals("open")) {
                selenium.open(arg1);
                return new CommandResult(true, "");
            }
            if (command.equals("openWindow")) {
                selenium.openWindow(arg1, arg2);
                return new CommandResult(true, "");
            }
            if (command.equals("pause")) {
                selenium.pause(new Integer(arg1));
                return new CommandResult(true, "");
            }
            if (command.equals("refresh")) {
                selenium.refresh();
                return new CommandResult(true, "");
            }
            if (command.equals("removeAllSelections")) {
                selenium.removeAllSelections(arg1);
                return new CommandResult(true, "");
            }
            if (command.equals("removeSelection")) {
                selenium.removeSelection(arg1, arg2);
                return new CommandResult(true, "");
            }
            if (command.equals("runScript")) {
                selenium.runScript(arg1);
                return new CommandResult(true, "");
            }
            if (command.equals("select")) {
                selenium.select(arg1, arg2);
                return new CommandResult(true, "");
            }
            if (command.equals("selectFrame")) {
                selenium.selectFrame(arg1);
                return new CommandResult(true, "");
            }
            if (command.equals("selectWindow")) {
                selenium.selectWindow(arg1);
                return new CommandResult(true, "");
            }
            if (command.equals("setBrowserLogLevel")) {
                selenium.setBrowserLogLevel(arg1);
                return new CommandResult(true, "");
            }
            if (command.equals("setCursorPosition")) {
                selenium.setCursorPosition(arg1, arg2);
                return new CommandResult(true, "");
            }
            if (command.equals("setMouseSpeed")) {
                selenium.setMouseSpeed(arg1);
                return new CommandResult(true, "");
            }
            if (command.equals("setSpeed")) {
                selenium.setSpeed(arg1);
                return new CommandResult(true, "");
            }
            if (command.equals("setTimeout")) {
                selenium.setTimeout(arg1);
                return new CommandResult(true, "");
            }
            if (command.equals("shiftKeyDown")) {
                selenium.shiftKeyDown();
                return new CommandResult(true, "");
            }
            if (command.equals("shiftKeyUp")) {
                selenium.shiftKeyUp();
                return new CommandResult(true, "");
            }
            if (command.equals("store")) {
                storeVar(arg2,arg1);
                return new CommandResult(true, "");
            }
            if (command.equals("submit")) {
                selenium.submit(arg1);
                return new CommandResult(true, "");
            }
            if (command.equals("type")) {
                selenium.type(arg1, arg2);
                return new CommandResult(true, "");
            }
            if (command.equals("typeKeys")) {
                selenium.typeKeys(arg1, arg2);
                return new CommandResult(true, "");
            }
            if (command.equals("uncheck")) {
                selenium.uncheck(arg1);
                return new CommandResult(true, "");
            }
            if (command.equals("waitForCondition")) {
                selenium.waitForCondition(arg1, arg2);
                return new CommandResult(true, "");
            }
            if (command.equals("waitForFrameToLoad")) {
                selenium.waitForFrameToLoad(arg1, arg2);
                return new CommandResult(true, "");
            }
            if (command.equals("waitForPageToLoad")) {
                selenium.waitForPageToLoad();
                return new CommandResult(true, "");
            }
            if (command.equals("waitForPopUp")) {
                selenium.waitForPopUp(arg1, arg2);
                return new CommandResult(true, "");
            }
            if (command.equals("windowFocus")) {
                selenium.windowFocus();
                return new CommandResult(true, "");
            }
            if (command.equals("windowMaximize")) {
                selenium.windowMaximize();
                return new CommandResult(true, "");
            }

            //selenium accessors

            if (command.equals("assertSelected")) {
                return new CommandResult(false, "This deprecated command is not supported in Selendion");
            }


            //@TODO - remove this when related are done
            if (command.equals("assertConfirmation")) {
                String var = selenium.getConfirmation();
                return new CommandResult(true, "");
            }





        // maybe it's a js extension
        String js = "Selenium.prototype.do" + command.substring(0, 1).toUpperCase() + command.substring(1);
                                                                   
            selenium.getEval(String.format("%s('%s','%s')", js, arg1, arg2));
            return null;
             }
        catch (SeleniumException se) {
            //@TODO
            return new CommandResult(false, se.getMessage());

        }
         catch (Exception e) {
            //@TODO
            // It's not an extension, so throw an exception
//                throw new Exception(String.format("Unsupported selenium command: %s in %s.", command, filename));
            return new CommandResult(false, e.toString());

        }

    }

    private Pattern variablePattern = Pattern.compile("(.*)\\$\\{([^}]*)\\}(.*)");

    private String replaceVariables(String string) {

        Matcher m = variablePattern.matcher(string);

        while (m.matches()) {
            string = m.group(1) + selenium.getEval(String.format("storedVars['%s']", m.group(2))) + m.group(3);
            m = variablePattern.matcher(string);
        }
        return string;
    }

    private void storeVar (String name, Object value) {
        Class clazz=value.getClass();
        if (clazz == String.class) {
            selenium.getEval(String.format("storedVars['%s']='%s'", name, ((String)value).replaceAll("\\n", " ").replaceAll("'", "\\\\'")));
        } else if (clazz == boolean.class) {
        selenium.getEval(String.format("storedVars['%s']=%s", name, ((Boolean)value) ? "true" : "false"));
        } else if (clazz == String[].class) {
            String valueStr="";
            for (String str : (String []) value) {
                valueStr = valueStr +"'" + str + "', ";
            }
            valueStr=valueStr.replaceFirst("[ ,]*$", "");
            selenium.getEval(String.format("storedVars['%s']=%s", name, "["+valueStr+"]"));

        } else if (clazz == Number.class) {

        selenium.getEval(String.format("storedVars['%s']=%s", name, value.toString()));
        }
    }
    private Object seleniumGetNoParams(String command) throws SeleniumIdeException {
        String unimplemented = "This command is not currently supported in Selendion.  Please raise a request for its addition at http://code.google.com/p/selendion/issues/list, including an example.";
            if (command.equals("Alert")) {
                return selenium.getAlert();
            }
            if (command.equals("AllButtons")) {
                return selenium.getAllButtons();
            }
            if (command.equals("AllFields")) {
                return selenium.getAllFields();
            }
            if (command.equals("AllLinks")) {
                return selenium.getAllLinks();
            }
            if (command.equals("AllWindowIds")) {
                return selenium.getAllWindowIds();
            }
            if (command.equals("AllWindowNames")) {
                return selenium.getAllWindowNames();
            }
            if (command.equals("AllWindowTitles")) {
                return selenium.getAllWindowTitles();
            }
            if (command.equals("BodyText")) {
                return selenium.getBodyText();
            }
            if (command.equals("Confirmation")) {
                return selenium.getConfirmation();
            }
            if (command.equals("Cookie")) {
                return selenium.getCookie();
            }
            if (command.equals("HtmlSource")) {
                return selenium.getHtmlSource();
            }
            if (command.equals("Location")) {
                return selenium.getLocation();
            }
            if (command.equals("MouseSpeed")) {
                return selenium.getMouseSpeed();
            }
            if (command.equals("Prompt")) {
                return selenium.getPrompt();
            }
            if (command.equals("AlertPresent")) {
                return selenium.isAlertPresent();
            }
            if (command.equals("Title")) {
                return selenium.getTitle();
            }

            if (command.equals("ConfirmationPresent")) {
                return selenium.isConfirmationPresent();
            }
            if (command.equals("PromptPresent")) {
                return selenium.isPromptPresent();
            }
            //@TODO

        if (command.equals("WhetherThisWindowMatchWindowExpression")) {
            throw new RuntimeException(unimplemented);
        }
        if (command.equals("WhetherThisFrameMatchFrameExpression")) {
            throw new RuntimeException(unimplemented);
        }
        if (command.equals("Ordered")) {
            throw new RuntimeException(unimplemented);
        }
        throw new SeleniumIdeException ("Command " + command + " not found.");

    }
    private Object seleniumGetOneParam(String command, String arg1) throws SeleniumIdeException {

              if (command.equals("Attribute")) {
            return selenium.getAttribute(arg1);
        }
        if (command.equals("AttributeFromAllWindows")) {
            return selenium.getAttributeFromAllWindows(arg1);
        }
        if (command.equals("CursorPosition")) {
            return selenium.getCursorPosition(arg1);
        }
        if (command.equals("ElementHeight")) {
            return selenium.getElementHeight(arg1);
        }
        if (command.equals("ElementIndex")) {
            return selenium.getElementIndex(arg1);
        }
        if (command.equals("ElementPositionLeft")) {
            return selenium.getElementPositionLeft(arg1);
        }
        if (command.equals("ElementPositionTop")) {
            return selenium.getElementPositionTop(arg1);
        }
        if (command.equals("ElementWidth")) {
            return selenium.getElementWidth(arg1);
        }
        if (command.equals("Eval")) {
            return selenium.getEval(arg1);
        }
        if (command.equals("Expression")) {
            return selenium.getExpression(arg1);
        }

        if (command.equals("SelectedId")) {
            return selenium.getSelectedId(arg1);
        }
        if (command.equals("SelectedIds")) {
            return selenium.getSelectedIds(arg1);
        }
        if (command.equals("SelectedIndex")) {
            return selenium.getSelectedIndex(arg1);
        }
        if (command.equals("SelectedIndexes")) {
            return selenium.getSelectedIndexes(arg1);
        }
        if (command.equals("SelectedLabel")) {
            return selenium.getSelectedLabel(arg1);
        }
        if (command.equals("SelectedLabels")) {
            return selenium.getSelectedLabels(arg1);
        }
        if (command.equals("SelectedValue")) {
            return selenium.getSelectedValue(arg1);
        }
        if (command.equals("SelectedValues")) {
            return selenium.getSelectedValues(arg1);
        }
        if (command.equals("SelectOptions")) {
            return selenium.getSelectOptions(arg1);
        }
        if (command.equals("Table")) {
            return selenium.getTable(arg1);
        }
        if (command.equals("Text")) {
            return selenium.getText(arg1);
        }

        if (command.equals("Value")) {
            return selenium.getValue(arg1);
        }
        if (command.equals("XpathCount")) {
            return selenium.getXpathCount(arg1);
        }

        if (command.equals("Checked")) {
            return selenium.isChecked(arg1);
        }
        if (command.equals("Editable")) {
            return selenium.isEditable(arg1);
        }
        if (command.equals("ElementPresent")) {
            return selenium.isElementPresent(arg1);
        }

        if (command.equals("SomethingSelected")) {
            return selenium.isSomethingSelected(arg1);
        }
        if (command.equals("TextPresent")) {
            return selenium.isTextPresent(arg1);
        }
        if (command.equals("Visible")) {
            return selenium.isVisible(arg1);
        }

        throw new SeleniumIdeException ("Command " + command + " not found.");

    }

    private String replaceCharacterEntities(String string) {

        string = string.replaceAll("&nbsp;", " ");
        string = string.replaceAll("&lt;", "<");
        string = string.replaceAll("&gt;", ">");
        string = string.replaceAll("&amp;", "&");
        string = string.replaceAll("&quot;", "\"");
        string = string.replaceAll("&apos;", "'");
        return string;
    }

    Vector<String[]> readSelenium(String htmlFileName) throws IOException, nu.xom.ParsingException, SAXException {
        InputStream stream = this.getClass().getResourceAsStream(htmlFileName);
        if (stream == null) {
            throw new RuntimeException(String.format("Could not read selenium file %s.", htmlFileName));
        }
        Reader reader = new InputStreamReader(stream);
        nu.xom.Builder builder = new nu.xom.Builder(XMLReaderFactory.createXMLReader(Parser.class.getName()));
        nu.xom.Document doc = builder.build(reader);

        nu.xom.Nodes rows;
        String uri = doc.getRootElement().getNamespaceURI();
        if (uri.length() > 0) {
            rows = doc.query("//ns:tr", new nu.xom.XPathContext("ns", uri));

        } else {
            rows = doc.query("//tr");
        }

        Vector return_val = new Vector();
        for (int r = 0; r < rows.size(); r++) {
            nu.xom.Node rowNode = rows.get(r);
            String[] row = new String[3];
            int col = 0;
            for (int i = 0; i < rowNode.getChildCount(); i++) {
                nu.xom.Node colNode = rowNode.getChild(i);
                if (colNode.getClass().equals(nu.xom.Element.class)) {
                    row[col] = colNode.getValue().trim();
                    col++;
                    if (col > 3) {
                        break;
                    }
                }
            }
            if (col >= 3) {
                return_val.add(row);
            }
        }
        return return_val;
    }


    private class CommandResult {
        private boolean success;
        private String message;

        public CommandResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean getSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }
    }

    private class SeleniumIdeException extends Throwable {
        public SeleniumIdeException(String s) {
            super(s);
        }
    }
}

