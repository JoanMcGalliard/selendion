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
import org.xml.sax.helpers.XMLReaderFactory;
import org.ccil.cowan.tagsoup.Parser;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Set;
import java.util.Vector;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.InputStream;

import com.thoughtworks.selenium.SeleniumException;

public class SeleniumIdeReader {
    private SeleniumDriver selenium;
    private boolean started = false;
    private static String VARIABLE_PATTERN ="[a-z][A-Za-z0-9_]*";

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

    public boolean runSeleniumScript(String filepath, Evaluator evaluator) {
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

    private void turnConcordionVarsToSeleniumVars(Evaluator evaluator) {
        Set keys = evaluator.getKeys();
        String array = "";
        for (Object key : keys) {
            if (!key.getClass().equals(String.class)) {
                throw new RuntimeException("Unexpected key " + key);
            }
            String keyString = (String) key;
            if (keyString.matches(VARIABLE_PATTERN)) {
                array = String.format("%s %s: '%s', ", array, keyString, evaluator.getVariable("#" + keyString).toString().replaceFirst("\\\\|$", "").replaceAll("'", "\\\\'"));
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
            if (nvp[0].matches("#"+ VARIABLE_PATTERN)) {
                evaluator.setVariable(nvp[0], nvp[1]);
            }
        }
    }

    private String seleniumObjectToString(Object obj) throws Exception {
        if (obj.getClass() == String.class) {
            return (String) obj;
        } else if (obj.getClass() == Number.class) {
            return ((Number) obj).toString();
        } else if (obj.getClass() == String[].class) {
            String ret = "";
            for (String str : (String[]) obj) {
                if (ret.length() > 0) {
                    ret=ret+",";
                }
                ret = ret + str;
            }
            return ret;
        } else {
            throw new Exception("Unhandled return type " + obj.getClass());
        }
    }

    private CommandResult execute(String command, String arg1, String arg2) {
        if (!started) {
            throw new RuntimeException("Please start selenium before running scripts.");
        }
        try {
            System.out.println(command);
            command = replaceVariables(replaceCharacterEntities(command));
            arg1 = replaceVariables(replaceCharacterEntities(arg1));
            arg2 = replaceVariables(replaceCharacterEntities(arg2));
            if (command.equals("echo")) {
                return new CommandResult(true, arg1);
            }
            if (command.equals("store")) {
                storeVar(arg2, arg1);
                return new CommandResult(true, "");
            }
            if (command.matches(".*AndWait$")) {
                try {
                    seleniumAction(command.replaceFirst("AndWait$", ""), arg1, arg2);
                    selenium.waitForPageToLoad();
                    return new CommandResult(true, "");
                } catch (SeleniumException se) {
                    return new CommandResult(false, se.getMessage());
                }
                catch (Exception e) {
                    return new CommandResult(false, "");
                }

            }
            //All getFoo and isFoo methods on the Selenium prototype automatically result in the availability
            // of storeFoo, assertFoo, assertNotFoo, verifyFoo, verifyNotFoo, waitForFoo, and waitForNotFoo commands.
            if (command.matches("^store[A-Z].*")) {
                String varName = arg2.length() > 0 ? arg2 : arg1;

                if (!varName.matches(VARIABLE_PATTERN)) {
                    return new CommandResult(false, "Illegal variable name: " + varName);
                }
                try {
                    Object jjj = seleniumGet(command.replaceFirst("^store", ""), arg1, arg2);
                        storeVar(varName, jjj);
//                        storeVar(varName, seleniumGet(command.replaceFirst("^store", ""), arg1, arg2));
                } catch (SeleniumIdeException e) {
                    return new CommandResult(false, "Unimplemented command " + command);
                }
                return new CommandResult(true, "");
            }
            if (command.matches("^assertNot[A-Z].*")) {
                Object actualObject;
                String expected = arg2.length() > 0 ? arg2 : arg1;
                try {
                    actualObject = seleniumGet(command.replaceFirst("^assertNot", ""), arg1, arg2);
                } catch (SeleniumIdeException e) {
                    return new CommandResult(false, "Unimplemented command " + command);
                }
                String actual=seleniumObjectToString(actualObject);

                assert (!actual.equals(expected));
                return new CommandResult(true, "");
            }
            if (command.matches("^assert[A-Z].*")) {
                Object actualObject;
                String expected = arg2.length() > 0 ? arg2 : arg1;
                try {
                    actualObject = seleniumGet(command.replaceFirst("^assert", ""), arg1, arg2);
                } catch (SeleniumIdeException e) {
                    return new CommandResult(false, "Unimplemented command " + command);
                }
                String actual=seleniumObjectToString(actualObject);
                assert (actual.equals(expected));
                return new CommandResult(true, "");
            }
            if (command.matches("^verifyNot[A-Z].*")) {
                Object actualObject;
                String expected = arg2.length() > 0 ? arg2 : arg1;
                try {
                    actualObject = seleniumGet(command.replaceFirst("^verifyNot", ""), arg1, arg2);
                } catch (SeleniumIdeException e) {
                    return new CommandResult(false, "Unimplemented command " + command);
                }
                String actual=seleniumObjectToString(actualObject);
                if (!actual.equals(expected)) {
                    return new CommandResult(true, "");
                } else {
                    return new CommandResult(false, "Failed");
                }


            }
            if (command.matches("^verify[A-Z].*")) {
                Object actualObject;
                String expected = arg2.length() > 0 ? arg2 : arg1;
                try {
                    actualObject = seleniumGet(command.replaceFirst("^verify", ""), arg1, arg2);
                } catch (SeleniumIdeException e) {
                    return new CommandResult(false, "Unimplemented command " + command);
                }
                String actual=seleniumObjectToString(actualObject);
                if (actual.equals(expected)) {
                    return new CommandResult(true, "");
                } else {
                    return new CommandResult(false, "Expected: " + expected + "; Actual: " + actual);
                }
            }
            if (command.matches("^waitForNot[A-Z].*")) {
                Object actualObject;
                String expected = arg2.length() > 0 ? arg2 : arg1;
                long start = System.currentTimeMillis();
                while (true) {
                    try {
                        actualObject = seleniumGet(command.replaceFirst("^waitForNot", ""), arg1, arg2);

                        String actual;
                        if (actualObject.getClass() == String.class) {
                            actual = (String) actualObject;
                        } else if (actualObject.getClass() == Number.class) {
                            actual = ((Number) actualObject).toString();
                        } else {
                            throw new Exception("");
                        }
                        if (!actual.equals(expected)) {
                            return new CommandResult(true, "");
                        }
                    }  catch (SeleniumIdeException se ) {
                        throw se;
                    }
                    catch (Exception e) {
                    }
                    if (System.currentTimeMillis() - start > Integer.parseInt(selenium.getTimeout())) {
                        throw new Exception("Timeout");
                    }
                    selenium.pause(200);

                }

            }
            if (command.matches("^waitFor[A-Z].*")) {
                Object actualObject;
                String expected = arg2.length() > 0 ? arg2 : arg1;
                long start = System.currentTimeMillis();
                while (true) {
                    try {
                        actualObject = seleniumGet(command.replaceFirst("^waitFor", ""), arg1, arg2);

                        String actual;
                        if (actualObject.getClass() == String.class) {
                            actual = (String) actualObject;
                        } else if (actualObject.getClass() == Number.class) {
                            actual = ((Number) actualObject).toString();
                        } else {
                            throw new Exception("");
                        }
                        if (actual.equals(expected)) {
                            return new CommandResult(true, "");
                        }
                    }  catch (SeleniumIdeException se ) {
                        throw se;
                    }
                    catch (Exception e) {
                    }
                    if (System.currentTimeMillis() - start > Integer.parseInt(selenium.getTimeout())) {
                        throw new Exception("Timeout");
                    }
                    selenium.pause(200);

                }

            }
            seleniumAction(command, arg1, arg2);

            return new CommandResult(true, "");
        } catch (SeleniumIdeException e) {
            return new CommandResult(false, e.getMessage());
        } catch (SeleniumException e) {
            return new CommandResult(false, e.getMessage());
        } catch (Exception e) {
            return new CommandResult(false, e.toString());

        }
    }

    private void seleniumAction(String command, String arg1, String arg2) throws SeleniumIdeException {
        if (command.equals("addSelection")) {
            selenium.addSelection(arg1, arg2);
        } else if (command.equals("allowNativeXpath")) {
            selenium.allowNativeXpath(arg1);
        } else if (command.equals("altKeyDown")) {
            selenium.altKeyDown();
        } else if (command.equals("altKeyUp")) {
            selenium.altKeyUp();
        } else if (command.equals("answerOnNextPrompt")) {
            selenium.answerOnNextPrompt(arg1);
        } else if (command.equals("assignId")) {
            selenium.assignId(arg1, arg2);
        } else if (command.equals("break")) {
            //ignore breaks in automated tests
        } else if (command.equals("check")) {
            selenium.check(arg1);
        } else if (command.equals("chooseCancelOnNextConfirmation")) {
            selenium.chooseCancelOnNextConfirmation();
        } else if (command.equals("chooseOkOnNextConfirmation")) {
            selenium.chooseOkOnNextConfirmation();
        } else if (command.equals("click")) {
            selenium.click(arg1);
        } else if (command.equals("clickAt")) {
            selenium.clickAt(arg1, arg2);
        } else if (command.equals("close")) {
            selenium.close();
        } else if (command.equals("controlKeyDown")) {
            selenium.controlKeyDown();
        } else if (command.equals("controlKeyUp")) {
            selenium.controlKeyUp();
        } else if (command.equals("createCookie")) {
            selenium.createCookie(arg1, arg2);
        } else if (command.equals("deleteCookie")) {
            selenium.deleteCookie(arg1, arg2);
        } else if (command.equals("doubleClick")) {
            selenium.doubleClick(arg1);
        } else if (command.equals("doubleClickAt")) {
            selenium.doubleClickAt(arg1, arg2);
        } else if (command.equals("dragAndDrop")) {
            selenium.dragAndDrop(arg1, arg2);
        } else if (command.equals("dragAndDropToObject")) {
            selenium.dragAndDropToObject(arg1, arg2);
        } else if (command.equals("dragdrop")) {
            selenium.dragdrop(arg1, arg2);
        } else if (command.equals("fireEvent")) {
            selenium.fireEvent(arg1, arg2);
        } else if (command.equals("getSpeed")) {
            selenium.getSpeed();
        } else if (command.equals("goBack")) {
            selenium.goBack();
        } else if (command.equals("highlight")) {
            selenium.highlight(arg1);
        } else if (command.equals("keyDown")) {
            selenium.keyDown(arg1, arg2);
        } else if (command.equals("keyPress")) {
            selenium.keyPress(arg1, arg2);
        } else if (command.equals("keyUp")) {
            selenium.keyUp(arg1, arg2);
        } else if (command.equals("metaKeyDown")) {
            selenium.metaKeyDown();
        } else if (command.equals("metaKeyUp")) {
            selenium.metaKeyUp();
        } else if (command.equals("mouseDown")) {
            selenium.mouseDown(arg1);
        } else if (command.equals("mouseDownAt")) {
            selenium.mouseDownAt(arg1, arg2);
        } else if (command.equals("mouseMove")) {
            selenium.mouseMove(arg1);
        } else if (command.equals("mouseMoveAt")) {
            selenium.mouseMoveAt(arg1, arg2);
        } else if (command.equals("mouseOut")) {
            selenium.mouseOut(arg1);
        } else if (command.equals("mouseOver")) {
            selenium.mouseOver(arg1);
        } else if (command.equals("mouseUp")) {
            selenium.mouseUp(arg1);
        } else if (command.equals("mouseUpAt")) {
            selenium.mouseUpAt(arg1, arg2);
        } else if (command.equals("open")) {
            selenium.open(arg1);
        } else if (command.equals("openWindow")) {
            selenium.openWindow(arg1, arg2);
        } else if (command.equals("pause")) {
            try {
                selenium.pause(new Integer(arg1));
            } catch (InterruptedException e1) {
            }
        } else if (command.equals("refresh")) {
            selenium.refresh();
        } else if (command.equals("removeAllSelections")) {
            selenium.removeAllSelections(arg1);
        } else if (command.equals("removeSelection")) {
            selenium.removeSelection(arg1, arg2);
        } else if (command.equals("runScript")) {
            selenium.runScript(arg1);
        } else if (command.equals("select")) {
            selenium.select(arg1, arg2);
        } else if (command.equals("selectFrame")) {
            selenium.selectFrame(arg1);
        } else if (command.equals("selectWindow")) {
            selenium.selectWindow(arg1);
        } else if (command.equals("setBrowserLogLevel")) {
            selenium.setBrowserLogLevel(arg1);
        } else if (command.equals("setCursorPosition")) {
            selenium.setCursorPosition(arg1, arg2);
        } else if (command.equals("setMouseSpeed")) {
            selenium.setMouseSpeed(arg1);
        } else if (command.equals("setSpeed")) {
            selenium.setSpeed(arg1);
        } else if (command.equals("setTimeout")) {
            selenium.setTimeout(arg1);
        } else if (command.equals("shiftKeyDown")) {
            selenium.shiftKeyDown();
        } else if (command.equals("shiftKeyUp")) {
            selenium.shiftKeyUp();
        } else if (command.equals("submit")) {
            selenium.submit(arg1);
        } else if (command.equals("type")) {
            selenium.type(arg1, arg2);
        } else if (command.equals("typeKeys")) {
            selenium.typeKeys(arg1, arg2);
        } else if (command.equals("uncheck")) {
            selenium.uncheck(arg1);
        } else if (command.equals("waitForCondition")) {
            selenium.waitForCondition(arg1, arg2);
        } else if (command.equals("waitForFrameToLoad")) {
            selenium.waitForFrameToLoad(arg1, arg2);
        } else if (command.equals("waitForPageToLoad")) {
            selenium.waitForPageToLoad();
        } else if (command.equals("waitForPopUp")) {
            selenium.waitForPopUp(arg1, arg2);
        } else if (command.equals("windowFocus")) {
            selenium.windowFocus();
        } else if (command.equals("windowMaximize")) {
            selenium.windowMaximize();
        } else {
            // maybe it's a js extension
            String js = "Selenium.prototype.do" + command.substring(0, 1).toUpperCase() + command.substring(1);
            try {
                selenium.getEval(String.format("%s('%s','%s')", js, arg1, arg2));
            } catch (SeleniumException se) {
                if (se.getMessage().contains(js + " is not a function")) {
                    throw new SeleniumIdeException("Command " + command + " not found.");
                } else {
                    throw se;
                }

            }
        }
    }

    //selenium accessors
    private Pattern variablePattern = Pattern.compile("(.*)\\$\\{([^}]*)\\}(.*)");

    private String replaceVariables(String string) {
        Matcher m = variablePattern.matcher(string);
        while (m.matches()) {
            string = m.group(1) + selenium.getEval(String.format("storedVars['%s']", m.group(2))) + m.group(3);
            m = variablePattern.matcher(string);
        }
        return string;
    }

    private void storeVar(String name, Object value) {
        Class clazz = value.getClass();
        if (clazz == String.class) {
            selenium.getEval(String.format("storedVars['%s']='%s'", name, ((String) value).replaceAll("\\n", " ").replaceAll("\\\\", "\\\\\\\\").replaceAll("'", "\\\\'")));
        } else if (clazz == boolean.class) {
            selenium.getEval(String.format("storedVars['%s']=%s", name, ((Boolean) value) ? "true" : "false"));
        } else if (clazz == String[].class) {
            String valueStr = "";
            for (String str : (String[]) value) {
                valueStr = valueStr + "'" + str + "', ";
            }
            valueStr = valueStr.replaceFirst("[ ,]*$", "");
            selenium.getEval(String.format("storedVars['%s']=%s", name, "[" + valueStr + "]"));
        } else if (clazz == Number.class) {
            selenium.getEval(String.format("storedVars['%s']=%s", name, value.toString()));
        }
    }

    private Object seleniumGet(String command, String arg1, String arg2) throws SeleniumIdeException {
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
        // maybe it's a js extension
        String js = "Selenium.prototype.is" + command.substring(0, 1).toUpperCase() + command.substring(1);
        try {
            return selenium.getEval(String.format("%s('%s', '%s')", js, arg1, arg2));
        } catch (SeleniumException se) {
            if (se.getMessage().contains(js + " is not a function")) {
                js = "Selenium.prototype.get" + command.substring(0, 1).toUpperCase() + command.substring(1);
                try {
                    return selenium.getEval(String.format("%s('%s', '%s')", js, arg1, arg2));
                } catch (SeleniumException se1) {
                    if (se1.getMessage().contains(js + " is not a function")) {
                        throw new SeleniumIdeException("Command " + command + " not found.");
                    } else {
                        throw se1;
                    }

                }
            } else {
                throw se;
            }

        }
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

    Vector<String[]> readSelenium(String htmlFileName) {
        InputStream stream = this.getClass().getResourceAsStream(htmlFileName);
        if (stream == null) {
            throw new RuntimeException(String.format("Could not read selenium file %s.", htmlFileName));
        }
        Reader reader = new InputStreamReader(stream);
        nu.xom.Builder builder = null;
        nu.xom.Document doc = null;
        try {
            builder = new nu.xom.Builder(XMLReaderFactory.createXMLReader(Parser.class.getName()));
            doc = builder.build(reader);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

    private class SeleniumIdeException extends Exception {
        public SeleniumIdeException(String s) {
            super(s);
        }
    }
}
