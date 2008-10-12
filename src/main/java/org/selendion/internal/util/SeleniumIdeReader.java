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
                array = String.format("%s %s: '%s', ", array, keyString, evaluator.getVariable("#" + keyString));

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
        try {

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
                selenium.getEval(String.format("storedVars['%s']='%s'", arg2, arg1));
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
            if (command.equals("storeAlert")) {
                String var = selenium.getAlert();
                selenium.getEval(String.format("storedVars['%s']='%s'", arg1, var));
                return new CommandResult(true, "");
            }
            if (command.equals("storeAllButtons")) {
                String [] var = selenium.getAllButtons();
                selenium.getEval(String.format("storedVars['%s']='%s'", arg1, var));
                return new CommandResult(true, "");
            }
            if (command.equals("storeAllFields")) {
                String [] var = selenium.getAllFields();
                selenium.getEval(String.format("storedVars['%s']='%s'", arg1, var));
                return new CommandResult(true, "");
            }
            if (command.equals("storeAllLinks")) {
                String  [] var = selenium.getAllLinks();
                selenium.getEval(String.format("storedVars['%s']='%s'", arg1, var));
                return new CommandResult(true, "");
            }
            if (command.equals("storeAllWindowIds")) {
                String  [] var = selenium.getAllWindowIds();
                selenium.getEval(String.format("storedVars['%s']='%s'", arg1, var));
                return new CommandResult(true, "");
            }
            if (command.equals("storeAllWindowNames")) {
                String [] var = selenium.getAllWindowNames();
                selenium.getEval(String.format("storedVars['%s']='%s'", arg1, var));
                return new CommandResult(true, "");
            }
            if (command.equals("storeAllWindowTitles")) {
                String [] var = selenium.getAllWindowTitles();
                selenium.getEval(String.format("storedVars['%s']='%s'", arg1, var));
                return new CommandResult(true, "");
            }
            if (command.equals("storeAttribute")) {
                String var = selenium.getAttribute(arg1);
                selenium.getEval(String.format("storedVars['%s']='%s'", arg2, var));
                return new CommandResult(true, "");
            }
            if (command.equals("storeAttributeFromAllWindows")) {
                String  [] var = selenium.getAttributeFromAllWindows(arg1);
                selenium.getEval(String.format("storedVars['%s']='%s'", arg2, var));
                return new CommandResult(true, "");
            }
            if (command.equals("storeBodyText")) {
                String var = selenium.getBodyText();
                System.out.println(String.format("storedVars['%s']='%s'", arg1, var));
                selenium.getEval(String.format("storedVars['%s']='%s'", arg1, var));
                return new CommandResult(true, "");
            }
            if (command.equals("storeConfirmation")) {
                String var = selenium.getConfirmation();
                selenium.getEval(String.format("storedVars['%s']='%s'", arg1, var));
                return new CommandResult(true, "");
            }
            if (command.equals("storeCookie")) {
                String var = selenium.getCookie();
                selenium.getEval(String.format("storedVars['%s']='%s'", arg1, var));
                return new CommandResult(true, "");
            }
            if (command.equals("storeCursorPosition")) {
                String var = selenium.getCursorPosition(arg1).toString();
                selenium.getEval(String.format("storedVars['%s']=%s", arg2, var));
                return new CommandResult(true, "");
            }
            if (command.equals("storeElementHeight")) {
                String var = selenium.getElementHeight(arg1).toString();
                selenium.getEval(String.format("storedVars['%s']=%s", arg2, var));
                return new CommandResult(true, "");
            }
            if (command.equals("storeElementIndex")) {
                String var = selenium.getElementIndex(arg1).toString();
                selenium.getEval(String.format("storedVars['%s']=%s", arg2, var));
                return new CommandResult(true, "");
            }
            if (command.equals("storeElementPositionLeft")) {
                String var = selenium.getElementPositionLeft(arg1).toString();
                selenium.getEval(String.format("storedVars['%s']=%s", arg2, var));
                return new CommandResult(true, "");
            }
            if (command.equals("storeElementPositionTop")) {
                String var = selenium.getElementPositionTop(arg1).toString();
                selenium.getEval(String.format("storedVars['%s']=%s", arg2, var));
                return new CommandResult(true, "");
            }
            if (command.equals("storeElementWidth")) {
                String var = selenium.getElementWidth(arg1).toString();
                selenium.getEval(String.format("storedVars['%s']=%s", arg2, var));
                return new CommandResult(true, "");
            }
            if (command.equals("storeEval")) {
                String var = selenium.getEval(arg1) ;
                selenium.getEval(String.format("storedVars['%s']='%s'", arg2, var));
                return new CommandResult(true, "");
            }
            if (command.equals("storeExpression")) {
                String var = selenium.getExpression(arg1);
                selenium.getEval(String.format("storedVars['%s']='%s'", arg2, var));
                return new CommandResult(true, "");
            }
            if (command.equals("storeHtmlSource")) {
                String var = selenium.getHtmlSource();
                selenium.getEval(String.format("storedVars['%s']='%s'", arg1, var));
                return new CommandResult(true, "");
            }
            if (command.equals("storeLocation")) {
                String var = selenium.getLocation();
                selenium.getEval(String.format("storedVars['%s']='%s'", arg1, var));
                return new CommandResult(true, "");
            }
            if (command.equals("storeMouseSpeed")) {
                String var = selenium.getMouseSpeed().toString();
                selenium.getEval(String.format("storedVars['%s']=%s", arg1, var));
                return new CommandResult(true, "");
            }
            if (command.equals("storePrompt")) {
                String var = selenium.getPrompt();
                selenium.getEval(String.format("storedVars['%s']='%s'", arg1, var));
                return new CommandResult(true, "");
            }
            if (command.equals("storeSelectedId")) {
                String var = selenium.getSelectedId(arg1);
                selenium.getEval(String.format("storedVars['%s']='%s'", arg2, var));
                return new CommandResult(true, "");
            }
            if (command.equals("storeSelectedIds")) {
                String var = selenium.getSelectedIds(arg1).toString();
                selenium.getEval(String.format("storedVars['%s']=%s", arg2, var));
                return new CommandResult(true, "");
            }
            if (command.equals("storeSelectedIndex")) {
                String var = selenium.getSelectedIndex(arg1);
                selenium.getEval(String.format("storedVars['%s']='%s'", arg2, var));
                return new CommandResult(true, "");
            }
            if (command.equals("storeSelectedIndexes")) {
                String var = selenium.getSelectedIndexes(arg1).toString();
                selenium.getEval(String.format("storedVars['%s']=%s", arg2, var));
                return new CommandResult(true, "");
            }
            if (command.equals("storeSelectedLabel")) {
                String var = selenium.getSelectedLabel(arg1);
                selenium.getEval(String.format("storedVars['%s']='%s'", arg2, var));
                return new CommandResult(true, "");
            }
            if (command.equals("storeSelectedLabels")) {
                String var = selenium.getSelectedLabels(arg1).toString();
                selenium.getEval(String.format("storedVars['%s']=%s", arg2, var));
                return new CommandResult(true, "");
            }
            if (command.equals("storeSelectedValue")) {
                String var = selenium.getSelectedValue(arg1);
                selenium.getEval(String.format("storedVars['%s']='%s'", arg2, var));
                return new CommandResult(true, "");
            }
            if (command.equals("storeSelectedValues")) {
                String var = selenium.getSelectedValues(arg1).toString();
                selenium.getEval(String.format("storedVars['%s']=%s", arg2, var));
                return new CommandResult(true, "");
            }
            if (command.equals("storeSelectOptions")) {
                String var = selenium.getSelectOptions(arg1).toString();
                selenium.getEval(String.format("storedVars['%s']=%s", arg2, var));
                return new CommandResult(true, "");
            }
            if (command.equals("storeTable")) {
                String var = selenium.getTable(arg1);
                selenium.getEval(String.format("storedVars['%s']='%s'", arg2, var));
                return new CommandResult(true, "");
            }
            if (command.equals("storeText")) {
                String var = selenium.getText(arg1);
                selenium.getEval(String.format("storedVars['%s']='%s'", arg2, var));
                return new CommandResult(true, "");
            }
            if (command.equals("storeTitle")) {
                String var = selenium.getTitle();
                selenium.getEval(String.format("storedVars['%s']='%s'", arg1, var));
                return new CommandResult(true, "");
            }
            if (command.equals("storeValue")) {
                String var = selenium.getValue(arg1);
                selenium.getEval(String.format("storedVars['%s']='%s'", arg2, var));
                return new CommandResult(true, "");
            }
            // @TODO
//            if (command.equals("storeWhetherThisFrameMatchFrameExpression")) {
//                String var = selenium.getWhetherThisFrameMatchFrameExpression();
//                selenium.getEval(String.format("storedVars['%s']='%s'", arg2, var));
//                return new CommandResult(true, "");
//            }
//            if (command.equals("storeWhetherThisWindowMatchWindowExpression")) {
//                String var = selenium.getWhetherThisWindowMatchWindowExpression();
//                selenium.getEval(String.format("storedVars['%s']='%s'", arg2, var));
//                return new CommandResult(true, "");
//            }
            if (command.equals("storeXpathCount")) {
                String var = selenium.getXpathCount(arg1).toString();
                selenium.getEval(String.format("storedVars['%s']=%s", arg2, var));
                return new CommandResult(true, "");
            }
            if (command.equals("storeAlertPresent")) {
                String var = selenium.isAlertPresent() ? "true" : "false" ;
                selenium.getEval(String.format("storedVars['%s']=%s", arg1, var));
                return new CommandResult(true, "");
            }
            if (command.equals("storeChecked")) {
                String var = (selenium.isChecked(arg1) ? "true" : "false");
                selenium.getEval(String.format("storedVars['%s']='%s'", arg2, var));
                return new CommandResult(true, "");
            }
            if (command.equals("storeConfirmationPresent")) {
                String var = (selenium.isConfirmationPresent()  ? "true" : "false");
                selenium.getEval(String.format("storedVars['%s']='%s'", arg1, var));
                return new CommandResult(true, "");
            }
            if (command.equals("storeEditable")) {
                String var = selenium.isEditable(arg1)  ? "true" : "false";
                selenium.getEval(String.format("storedVars['%s']=%s", arg2, var));
                return new CommandResult(true, "");
            }
            if (command.equals("storeElementPresent")) {
                String var = selenium.isElementPresent(arg1)   ? "true" : "false";
                selenium.getEval(String.format("storedVars['%s']=%s", arg2, var));
                return new CommandResult(true, "");
            }
//            @TODO
//            if (command.equals("storeOrdered")) {
//                String var = selenium.isOrdered();
//                selenium.getEval(String.format("storedVars['%s']='%s'", arg1, var));
//                return new CommandResult(true, "");
//            }
            if (command.equals("storePromptPresent")) {
                String var = selenium.isPromptPresent()   ? "true" : "false";
                selenium.getEval(String.format("storedVars['%s']=%s", arg1, var));
                return new CommandResult(true, "");
            }
            if (command.equals("storeSomethingSelected")) {
                String var = selenium.isSomethingSelected(arg1)   ? "true" : "false";
                selenium.getEval(String.format("storedVars['%s']=%s", arg2, var));
                return new CommandResult(true, "");
            }
            if (command.equals("storeTextPresent")) {
                String var = selenium.isTextPresent(arg1)   ? "true" : "false";
                selenium.getEval(String.format("storedVars['%s']=%s", arg2, var));
                return new CommandResult(true, "");
            }
            if (command.equals("storeVisible")) {
                String var = selenium.isVisible(arg1)  ? "true" : "false";
                selenium.getEval(String.format("storedVars['%s']=%s", arg2, var));
                return new CommandResult(true, "");
            }

            //@TODO - remove this when related are done
            if (command.equals("assertConfirmation")) {
                String var = selenium.getConfirmation();
                return new CommandResult(true, "");
            }
            

        }
        catch (Exception se) {
            //@TODO
            return new CommandResult(false, "tried to run selenium command: " + se.toString());

        }
        // maybe it's a js extension
        String js = "Selenium.prototype.do" + command.substring(0, 1).toUpperCase() + command.substring(1);
        try {

            selenium.getEval(String.format("%s('%s','%s')", js, arg1, arg2));
            return null;
        } catch (Exception e) {
            //@TODO
            // It's not an extension, so throw an exception
//                throw new Exception(String.format("Unsupported selenium command: %s in %s.", command, filename));
            return new CommandResult(false, "geteval failed " + e.toString());

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
}

