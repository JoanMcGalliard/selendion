/*
        Copyright Joan McGalliard, 2008-9
*/
package org.selendion.internal.util;

import com.thoughtworks.selenium.SeleniumException;
import org.ccil.cowan.tagsoup.Parser;
import org.concordion.api.Element;
import org.concordion.api.Evaluator;
import org.concordion.api.Result;
import org.concordion.api.ResultRecorder;
import org.concordion.internal.util.Announcer;
import org.selendion.integration.selenium.SeleniumDriver;
import org.selendion.integration.BrowserDriver;
import org.selendion.integration.HtmlUnit.HtmlUnitDriver;
import org.selendion.integration.HtmlUnit.HtmlUnitException;
import org.selendion.internal.RunSeleniumListener;
import org.selendion.internal.command.RunSeleniumFailureEvent;
import org.selendion.internal.command.RunSeleniumSuccessEvent;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.*;
import java.util.List;
import java.util.Vector;

public class SeleniumIdeReader extends junit.framework.TestCase {
    private BrowserDriver browser;
    private boolean started = false;
    private static String VARIABLE_PATTERN = "[a-z][A-Za-z0-9_]*";

    public void start(String seleniumHost, int seleniumPort, String browser, String baseUrl) {
        if (started) {
            stop();
        }
        this.browser = new SeleniumDriver(
                seleniumHost, seleniumPort, browser,
                baseUrl);
        this.browser.start();
        started = true;
    }
    public void start(String baseUrl) {
        if (started) {
            stop();
        }
        browser = new HtmlUnitDriver(baseUrl);
        this.browser.start();        
        started = true;
    }

    public void stop() {
        if (started) {
            browser.stop();
            started=false;
        }
    }


    public void runSeleniumScript(List<String> filepaths, Evaluator evaluator, String title, Element resultElement, Announcer<RunSeleniumListener> listeners, int buttonId, ResultRecorder resultRecorder) throws Exception {
        Vector<String[]> seleniumCommands = new Vector<String[]>();
        for (String filepath : filepaths) {
            seleniumCommands.addAll(readSelenium(filepath));
        }
        browser.passVariablesIn(evaluator) ;
        boolean result = true;
        Element table = new Element("table");
        Element tr = new Element("tr");
        Element td = new Element("th");
        td.addAttribute("colspan", "4");
        String[] titles = title.split("\\|");
        for (String t : titles)  {
            td.appendText(t);
            td.appendChild(new Element("br"));
        }
        tr.appendChild(td);
        table.appendChild(tr);
        boolean exception = false;
        for (String[] command : seleniumCommands) {
            command[0] = browser.replaceVariables(replaceCharacterEntities(command[0]));
            command[1] = browser.replaceVariables(replaceCharacterEntities(command[1]));
            command[2] = browser.replaceVariables(replaceCharacterEntities(command[2]));
            if (command[1] != null && command[2] != null) {
                tr = new Element("tr");
                td = new Element("td").appendText(command[0]);
                tr.appendChild(td);
                td = new Element("td").appendText(command[1]);
                tr.appendChild(td);
                td = new Element("td").appendText(command[2]);
                tr.appendChild(td);
                td = new Element("td");
                if (!exception) {
                    CommandResult rowResponse;
                    try {
                        rowResponse = execute(command[0], command[1], command[2]);

                        td.appendText(rowResponse.getMessage());
                        if (rowResponse.getSuccess()) {
                            listeners.announce().successReported(new RunSeleniumSuccessEvent(tr));
                            resultRecorder.record(Result.SUCCESS);
                        } else {
                            result = false;
                            listeners.announce().failureReported(new RunSeleniumFailureEvent(tr));
                            resultRecorder.record(Result.FAILURE);
                        }
                    }
                    catch (Throwable e) {
                        exception = true;
                        result = false;
                        td.appendText(e.toString());
                        listeners.announce().failureReported(new RunSeleniumFailureEvent(tr));
                        resultRecorder.record(Result.FAILURE);
                    }
                }
                tr.appendChild(td);
                table.appendChild(tr);
            }
        }
        String label = title.replaceFirst("\\|.*", "...");
        Element input = new Element("input")
                .addStyleClass("seleniumTableButton")
                .setId("seleniumTableButton" + buttonId)
                .addAttribute("type", "button")
                .addAttribute("class", result ? "success" : "failure")
                .addAttribute("onclick", "javascript:toggleSeleniumTable('" + buttonId + "', '" + label + "')")
                .addAttribute("value", label);

        resultElement.appendChild(input);
        table.setId("seleniumTable" + buttonId);
        table.addAttribute("class", "seleniumTable");
        resultElement.appendChild(table);
        browser.passVariablesOut(evaluator);
    }



    private String seleniumObjectToString(Object obj) throws Exception {
        if (obj.getClass() == String.class) {
            return (String) obj;
        } else if (obj.getClass() == Number.class) {
            return  obj.toString();
        } else if (obj.getClass() == String[].class) {
            String ret = null;
            for (String str : (String[]) obj) {
                if (ret == null) {
                    ret = "";
                } else {
                    ret = ret + ",";
                }
                ret = ret + str;
            }
            return ret;
        } else {
            throw new Exception("Unhandled return type " + obj.getClass());
        }
    }

    protected CommandResult execute(String command, String arg1, String arg2) {
        if (!started) {
            throw new RuntimeException("Please start browser before running scripts.");
        }
        try {

            if (command.equals("echo")) {
                return new CommandResult(true, browser.echo(arg1));
            }
            if (command.equals("store")) {
                browser.store(arg2, arg1);
                return new CommandResult(true, "");
            }
            if (command.equals("waitForCondition")) {
                browser.waitForCondition(arg1, arg2);
                return new CommandResult(true, "");

            }
            if (command.equals("waitForFrameToLoad")) {
                browser.waitForFrameToLoad(arg1, arg2);
                return new CommandResult(true, "");

            }
            if (command.equals("waitForPageToLoad")) {
                browser.waitForPageToLoad();
                return new CommandResult(true, "");

            }
            if (command.equals("waitForPageToLoadfLoading")) {
                browser.waitForPageToLoad();
                return new CommandResult(true, "");

            }
            if (command.equals("waitForPopUp")) {
                browser.waitForPopUp(arg1, arg2);
                return new CommandResult(true, "");
            }


            if (command.matches(".*AndWait$")) {
                try {
                    seleniumAction(command.replaceFirst("AndWait$", ""), arg1, arg2);
                    browser.waitForPageToLoad();
                    return new CommandResult(true, "");
                } catch (SeleniumException se) {
                    return new CommandResult(false, se.getMessage());
                } catch (HtmlUnitException se) {
                    return new CommandResult(false, se.getMessage());
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }
            if (command.matches("^storeIfAvailable[A-Z].*")) {
                String varName = arg2.length() > 0 ? arg2 : arg1;

                if (!varName.matches(VARIABLE_PATTERN)) {
                    return new CommandResult(false, "Illegal variable name: " + varName);
                }
                try {
                    browser.store(varName, browserGet(command.replaceFirst("^storeIfAvailable", ""), arg1, arg2));
                } catch (SeleniumException se) {
                    //ignore
                } catch (HtmlUnitException re) {
                    // fall through
                }
                catch (SeleniumIdeException e) {
                    return new CommandResult(false, "Unimplemented command " + command);
                }
                return new CommandResult(true, "");
            }
            if (command.matches("^storeIfVisible[A-Z].*")) {

                if (!arg2.matches(VARIABLE_PATTERN)) {
                    return new CommandResult(false, "Illegal variable name: " + arg2);
                }
                try {
                    Object value = browserGet(command.replaceFirst("^storeIfVisible", ""), arg1, arg2);
                    if (browser.isVisible(arg1)) {
                        browser.store(arg2, value);
                    }
                } catch (SeleniumException se) {
                    //ignore
                } catch (HtmlUnitException se) {
                    //ignore
                }
                catch (SeleniumIdeException e) {
                    return new CommandResult(false, "Unimplemented command " + command);
                }
                return new CommandResult(true, "");
            }
            if (command.matches("^storeNot[A-Z].*")) {
                String varName = arg2.length() > 0 ? arg2 : arg1;

                if (!varName.matches(VARIABLE_PATTERN)) {
                    return new CommandResult(false, "Illegal variable name: " + varName);
                }
                try {
                    Object answer= browserGet(command.replaceFirst("^storeNot", ""), arg1, arg2);
                    if (answer.getClass().equals(Boolean.class)) {
                    browser.store(varName, !(Boolean)answer);
                    }
                } catch (SeleniumIdeException e) {
                    return new CommandResult(false, "Unimplemented command " + command);
                }
                return new CommandResult(true, "");
            }
            if (command.matches("^store[A-Z].*")) {
                String varName = arg2.length() > 0 ? arg2 : arg1;

                if (!varName.matches(VARIABLE_PATTERN)) {
                    return new CommandResult(false, "Illegal variable name: " + varName);
                }
                try {
                    browser.store(varName, browserGet(command.replaceFirst("^store", ""), arg1, arg2));
                } catch (SeleniumIdeException e) {
                    return new CommandResult(false, "Unimplemented command " + command);
                }
                return new CommandResult(true, "");
            }
            if (command.matches("^assertNot[A-Z].*")) {
                Object actualObject;
                String expected = arg2.length() > 0 ? arg2 : arg1;
                try {
                    actualObject = browserGet(command.replaceFirst("^assertNot", ""), arg1, arg2);
                } catch (SeleniumIdeException e) {
                    return new CommandResult(false, "Unimplemented command " + command);
                }
                if (actualObject.getClass().equals(Boolean.class)) {
                    assertTrue((Boolean) actualObject);
                } else {
                     assertFalse(seleniumObjectToString(actualObject).equals(expected));
                }
                return new CommandResult(true, "");
            }
            if (command.matches("^assertTextNotPresent")) {
                try {
                    assertFalse((Boolean) browserGet("TextPresent", arg1, arg2));
                } catch (SeleniumIdeException e) {
                    return new CommandResult(false, "Unimplemented command " + command);
                }
                return new CommandResult(true, "");
            }
            if (command.matches("^assert[A-Z].*")) {
                Object actualObject;
                String expected = arg2.length() > 0 ? arg2 : arg1;
                try {
                    actualObject = browserGet(command.replaceFirst("^assert", ""), arg1, arg2);
                } catch (SeleniumIdeException e) {
                    return new CommandResult(false, "Unimplemented command " + command);
                }
                if (actualObject.getClass().equals(Boolean.class)) {
                    assertTrue((Boolean) actualObject);
                } else {
                     assertEquals(expected, seleniumObjectToString(actualObject));
                }
                return new CommandResult(true, "");
            }
            if (command.matches("^verifyNot[A-Z].*")) {
                Object actualObject;
                String seleniumCommand = command.replaceFirst("^verifyNot", "") ;
                              String expected;
                              if (seleniumCommand.equals("Alert") ||
                                      seleniumCommand.equals("AllButtons") ||
                                      seleniumCommand.equals("AllFields") ||
                                      seleniumCommand.equals("AllLinks") ||
                                      seleniumCommand.equals("AllWindowIds") ||
                                      seleniumCommand.equals("AllWindowNames") ||
                                      seleniumCommand.equals("AllWindowTitles") ||
                                      seleniumCommand.equals("BodyText") ||
                                      seleniumCommand.equals("Confirmation") ||
                                      seleniumCommand.equals("Cookie") ||
                                      seleniumCommand.equals("HtmlSource") ||
                                      seleniumCommand.equals("Location") ||
                                      seleniumCommand.equals("MouseSpeed") ||
                                      seleniumCommand.equals("Prompt") ||
                                      seleniumCommand.equals("Speed") ||
                                      seleniumCommand.equals("Title")) {
                                  expected = arg1;
                              } else {
                                  expected = arg2;
                              }
              
                try {
                    actualObject = browserGet(seleniumCommand, arg1, arg2);
                } catch (SeleniumIdeException e) {
                    return new CommandResult(false, "Unimplemented command " + command);
                }
                if (actualObject.getClass().equals(Boolean.class))   {
                           return new CommandResult(!(Boolean) actualObject, "");
               }
                String actual = seleniumObjectToString(actualObject);
                if (!actual.equals(expected)) {
                    return new CommandResult(true, "");
                } else {
                    return new CommandResult(false, "Failed");
                }


            }
            if (command.matches("^verify[A-Z][a-z][a-z]*Not[A-Z].*")) {
                Object actualObject;
                String seleniumCommand = command.replaceFirst("^verify", "").replaceFirst("Not", "");
                              String expected;
                              if (seleniumCommand.equals("Alert") ||
                                      seleniumCommand.equals("AllButtons") ||
                                      seleniumCommand.equals("AllFields") ||
                                      seleniumCommand.equals("AllLinks") ||
                                      seleniumCommand.equals("AllWindowIds") ||
                                      seleniumCommand.equals("AllWindowNames") ||
                                      seleniumCommand.equals("AllWindowTitles") ||
                                      seleniumCommand.equals("BodyText") ||
                                      seleniumCommand.equals("Confirmation") ||
                                      seleniumCommand.equals("Cookie") ||
                                      seleniumCommand.equals("HtmlSource") ||
                                      seleniumCommand.equals("Location") ||
                                      seleniumCommand.equals("MouseSpeed") ||
                                      seleniumCommand.equals("Prompt") ||
                                      seleniumCommand.equals("Speed") ||
                                      seleniumCommand.equals("Title")) {
                                  expected = arg1;
                              } else {
                                  expected = arg2;
                              }

                try {
                    actualObject = browserGet(seleniumCommand, arg1, arg2);
                } catch (SeleniumIdeException e) {
                    return new CommandResult(false, "Unimplemented command " + command);
                }
                if (actualObject.getClass().equals(Boolean.class))   {
                           return new CommandResult(!(Boolean) actualObject, "");
               }
                String actual = seleniumObjectToString(actualObject);
                if (!actual.equals(expected)) {
                    return new CommandResult(true, "");
                } else {
                    return new CommandResult(false, "Failed");
                }


            }

            if (command.matches("^verify[A-Z].*")) {
                Object actualObject;
                String seleniumCommand = command.replaceFirst("^verify", "");
                String expected;
                if (seleniumCommand.equals("Alert") ||
                        seleniumCommand.equals("AllButtons") ||
                        seleniumCommand.equals("AllFields") ||
                        seleniumCommand.equals("AllLinks") ||
                        seleniumCommand.equals("AllWindowIds") ||
                        seleniumCommand.equals("AllWindowNames") ||
                        seleniumCommand.equals("AllWindowTitles") ||
                        seleniumCommand.equals("BodyText") ||
                        seleniumCommand.equals("Confirmation") ||
                        seleniumCommand.equals("Cookie") ||
                        seleniumCommand.equals("HtmlSource") ||
                        seleniumCommand.equals("Location") ||
                        seleniumCommand.equals("MouseSpeed") ||
                        seleniumCommand.equals("Prompt") ||
                        seleniumCommand.equals("Speed") ||
                        seleniumCommand.equals("Title")) {
                    expected = arg1;
                } else {
                    expected = arg2;
                }

                expected = expected.replaceAll("([A-Za-z])\\n+([A-Za-z])","$1 $2").replaceAll("\\n", "");
                try {
                    actualObject = browserGet(seleniumCommand, arg1, arg2);
                } catch (SeleniumIdeException e) {
                    return new CommandResult(false, "Unimplemented command " + command);
                }
                if (actualObject.getClass().equals(Boolean.class)) {
                    return new CommandResult((Boolean) actualObject, "");
                }
                String actual = seleniumObjectToString(actualObject);
                actual = replaceCharacterEntities(actual)
                        .replaceAll("([A-Za-z])\\n+([A-Za-z])","$1 $2").replaceAll("\\n", "");
                if (actual.equals(expected)) {
                    return new CommandResult(true, "");
                } else {
                    return new CommandResult(false, "Expected: " + expected + "; Actual: " + actual.replaceAll("\\n", ""));
                }
            }
            if (command.matches("^waitForPageToLoadIfNot[A-Z].*")) {
                Object actualObject;
                String expected = arg2.length() > 0 ? arg2 : arg1;
                try {
                    actualObject = browserGet(command.replaceFirst("^waitForPageToLoadIfNot", ""), arg1, arg2);
                } catch (SeleniumIdeException e) {
                    return new CommandResult(false, "Unimplemented command " + command);
                }
                boolean condition;
                if (actualObject.getClass().equals(Boolean.class)) {
                    condition = (Boolean) actualObject;
                } else {
                    String actual = seleniumObjectToString(actualObject);
                    condition = actual.equals(expected);
                }
                if (!condition) {
                    browser.waitForPageToLoad();
                }
                return new CommandResult(true, "");
            }
            if (command.matches("^waitForPageToLoadIf[A-Z].*")) {
                Object actualObject;
                String expected = arg2.length() > 0 ? arg2 : arg1;
                try {
                    actualObject = browserGet(command.replaceFirst("^waitForPageToLoadIf", ""), arg1, arg2);
                } catch (SeleniumIdeException e) {
                    return new CommandResult(false, "Unimplemented command " + command);
                }
                boolean condition;
                if (actualObject.getClass().equals(Boolean.class)) {
                    condition = (Boolean) actualObject;
                } else {
                    String actual = seleniumObjectToString(actualObject);
                    condition = actual.equals(expected);
                }
                if (condition) {
                    browser.waitForPageToLoad();
                }
                return new CommandResult(true, "");
            }
            if (command.matches("^waitForNot[A-Z].*")) {
                Object actualObject;
                String expected = arg2.length() > 0 ? arg2 : arg1;
                long start = System.currentTimeMillis();
                while (true) {
                    try {
                        actualObject = browserGet(command.replaceFirst("^waitForNot", ""), arg1, arg2);
                        if (actualObject.getClass().equals(Boolean.class) && !(Boolean)actualObject) {
                            return new CommandResult(true, "");
                        }
                        String actual = seleniumObjectToString(actualObject);
                        if (!actual.equals(expected)) {
                            return new CommandResult(true, "");
                        }
                    } catch (SeleniumIdeException se) {
                        throw se;
                    }
                    catch (Exception e) {
                        // try again
                    }
                    if (System.currentTimeMillis() - start > Integer.parseInt(browser.getTimeout())) {
                        return new CommandResult(false, String.format("ERROR: Timed out after %sms", browser.getTimeout()));
                    }
                    browser.pauseInWaitFor(200);

                }

            }
            if (command.matches("^waitFor[A-Z].*")) {
                Object actualObject;
                String expected = arg2.length() > 0 ? arg2 : arg1;
                long start = System.currentTimeMillis();
                while (true) {
                    try {
                        actualObject = browserGet(command.replaceFirst("^waitFor", ""), arg1, arg2);
                        if (actualObject.getClass().equals(Boolean.class) &&
                                (Boolean) actualObject)   {
                            return new CommandResult(true, "");
                        }
                        String actual = seleniumObjectToString(actualObject);
                        if (actual.equals(expected)) {
                            return new CommandResult(true, "");
                        }
                    } catch (SeleniumIdeException se) {
                        throw se;
                    }
                    catch (Exception e) {
                        //try again
                    }
                    if (System.currentTimeMillis() - start > Integer.parseInt(browser.getTimeout())) {
                        return new CommandResult(false, String.format("ERROR: Timed out after %sms", browser.getTimeout()));
                    }
                    browser.pauseInWaitFor(200);

                }

            }
            seleniumAction(command, arg1, arg2);

            return new CommandResult(true, "");
        } catch (SeleniumIdeException e) {
            return new CommandResult(false, e.getMessage());
        } catch (SeleniumException e) {
            return new CommandResult(false, e.getMessage());
        } catch (HtmlUnitException e) {
            return new CommandResult(false, e.getMessage());
        } catch (Exception e) {
            return new CommandResult(false, e.toString());

        }
    }

    private void seleniumAction(String command, String arg1, String arg2) throws SeleniumIdeException {
        if (command.equals("addSelection")) {
            browser.addSelection(arg1, arg2);
        } else if (command.equals("allowNativeXpath")) {
            browser.allowNativeXpath(arg1);
        } else if (command.equals("altKeyDown")) {
            browser.altKeyDown();
        } else if (command.equals("altKeyUp")) {
            browser.altKeyUp();
        } else if (command.equals("answerOnNextPrompt")) {
            browser.answerOnNextPrompt(arg1);
        } else if (command.equals("assignId")) {
            browser.assignId(arg1, arg2);
        } else if (command.equals("break")) {
            //ignore breaks in automated tests
        } else if (command.equals("check")) {
            browser.check(arg1);
        } else if (command.equals("chooseCancelOnNextConfirmation")) {
            browser.chooseCancelOnNextConfirmation();
        } else if (command.equals("chooseOkOnNextConfirmation")) {
            browser.chooseOkOnNextConfirmation();
        } else if (command.equals("click")) {
            browser.click(arg1);
        } else if (command.equals("clickAt")) {
            browser.clickAt(arg1, arg2);
        } else if (command.equals("close")) {
            browser.close();
        } else if (command.equals("controlKeyDown")) {
            browser.controlKeyDown();
        } else if (command.equals("controlKeyUp")) {
            browser.controlKeyUp();
        } else if (command.equals("createCookie")) {
            browser.createCookie(arg1, arg2);
        } else if (command.equals("deleteAllVisibleCookies")) {
            browser.deleteAllVisibleCookies();
        } else if (command.equals("deleteCookie")) {
            browser.deleteCookie(arg1, arg2);
        } else if (command.equals("doubleClick")) {
            browser.doubleClick(arg1);
        } else if (command.equals("doubleClickAt")) {
            browser.doubleClickAt(arg1, arg2);
        } else if (command.equals("dragAndDrop")) {
            browser.dragAndDrop(arg1, arg2);
        } else if (command.equals("dragAndDropToObject")) {
            browser.dragAndDropToObject(arg1, arg2);
        } else if (command.equals("dragdrop")) {
            browser.dragdrop(arg1, arg2);
        } else if (command.equals("fireEvent")) {
            browser.fireEvent(arg1, arg2);
        } else if (command.equals("getSpeed")) {
            browser.getSpeed();
        } else if (command.equals("goBack")) {
            browser.goBack();
        } else if (command.equals("highlight")) {
            browser.highlight(arg1);
        } else if (command.equals("keyDown")) {
            browser.keyDown(arg1, arg2);
        } else if (command.equals("keyPress")) {
            browser.keyPress(arg1, arg2);
        } else if (command.equals("keyUp")) {
            browser.keyUp(arg1, arg2);
        } else if (command.equals("metaKeyDown")) {
            browser.metaKeyDown();
        } else if (command.equals("metaKeyUp")) {
            browser.metaKeyUp();
        } else if (command.equals("mouseDown")) {
            browser.mouseDown(arg1);
        } else if (command.equals("mouseDownAt")) {
            browser.mouseDownAt(arg1, arg2);
        } else if (command.equals("mouseMove")) {
            browser.mouseMove(arg1);
        } else if (command.equals("mouseMoveAt")) {
            browser.mouseMoveAt(arg1, arg2);
        } else if (command.equals("mouseOut")) {
            browser.mouseOut(arg1);
        } else if (command.equals("mouseOver")) {
            browser.mouseOver(arg1);
        } else if (command.equals("mouseUp")) {
            browser.mouseUp(arg1);
        } else if (command.equals("mouseUpAt")) {
            browser.mouseUpAt(arg1, arg2);
        } else if (command.equals("open")) {
            browser.open(arg1);
        } else if (command.equals("openWindow")) {
            browser.openWindow(arg1, arg2);
        } else if (command.equals("pause")) {
            try {
                browser.pause(new Integer(arg1));
            } catch (InterruptedException e1) {
                //ignore
            }
        } else if (command.equals("refresh")) {
            browser.refresh();
        } else if (command.equals("removeAllSelections")) {
            browser.removeAllSelections(arg1);
        } else if (command.equals("removeSelection")) {
            browser.removeSelection(arg1, arg2);
        } else if (command.equals("runScript")) {
            browser.runScript(arg1);
        } else if (command.equals("select")) {
            browser.select(arg1, arg2);
        } else if (command.equals("selectFrame")) {
            browser.selectFrame(arg1);
        } else if (command.equals("selectWindow")) {
            browser.selectWindow(arg1);
        } else if (command.equals("setBrowserLogLevel")) {
            browser.setBrowserLogLevel(arg1);
        } else if (command.equals("setCursorPosition")) {
            browser.setCursorPosition(arg1, arg2);
        } else if (command.equals("setMouseSpeed")) {
            browser.setMouseSpeed(arg1);
        } else if (command.equals("setSpeed")) {
            browser.setSpeed(arg1);
        } else if (command.equals("setTimeout")) {
            browser.setTimeout(arg1);
        } else if (command.equals("shiftKeyDown")) {
            browser.shiftKeyDown();
        } else if (command.equals("shiftKeyUp")) {
            browser.shiftKeyUp();
        } else if (command.equals("submit")) {
            browser.submit(arg1);
        } else if (command.equals("type")) {
            browser.type(arg1, arg2);
        } else if (command.equals("typeKeys")) {
            browser.typeKeys(arg1, arg2);
        } else if (command.equals("uncheck")) {
            browser.uncheck(arg1);
        } else if (command.equals("windowFocus")) {
            browser.windowFocus();
        } else if (command.equals("windowMaximize")) {
            browser.windowMaximize();
        } else {
            // maybe it's a js extension
            String js = "Selenium.prototype.do" + command.substring(0, 1).toUpperCase() + command.substring(1);
            try {
                browser.getEval(String.format("%s('%s','%s')", js, arg1.replaceAll("'", "\\\\'"), arg2.replaceAll("'", "\\\\'")));
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



    private Object browserGet(String command, String arg1, String arg2) throws SeleniumIdeException {
        if (command.equals("Alert")) {
            return browser.getAlert();
        }
        if (command.equals("AllButtons")) {
            return browser.getAllButtons();
        }
        if (command.equals("AllFields")) {
            return browser.getAllFields();
        }
        if (command.equals("AllLinks")) {
            return browser.getAllLinks();
        }
        if (command.equals("AllWindowIds")) {
            return browser.getAllWindowIds();
        }
        if (command.equals("AllWindowNames")) {
            return browser.getAllWindowNames();
        }
        if (command.equals("AllWindowTitles")) {
            return browser.getAllWindowTitles();
        }
        if (command.equals("BodyText")) {
            return browser.getBodyText();
        }
        if (command.equals("Confirmation")) {
            return browser.getConfirmation();
        }
        if (command.equals("Cookie")) {
            return browser.getCookie();
        }
        if (command.equals("HtmlSource")) {
            return browser.getHtmlSource();
        }
        if (command.equals("Location")) {
            return browser.getLocation();
        }
        if (command.equals("MouseSpeed")) {
            return browser.getMouseSpeed();
        }
        if (command.equals("Prompt")) {
            return browser.getPrompt();
        }
        if (command.equals("AlertPresent")) {
            return browser.isAlertPresent();
        }
        if (command.equals("Title")) {
            return browser.getTitle();
        }
        if (command.equals("ConfirmationPresent")) {
            return browser.isConfirmationPresent();
        }
        if (command.equals("PromptPresent")) {
            return browser.isPromptPresent();
        }
        if (command.equals("Attribute")) {
            return browser.getAttribute(arg1);
        }
        if (command.equals("AttributeFromAllWindows")) {
            return browser.getAttributeFromAllWindows(arg1);
        }
        if (command.equals("CursorPosition")) {
            return browser.getCursorPosition(arg1);
        }
        if (command.equals("ElementHeight")) {
            return browser.getElementHeight(arg1);
        }
        if (command.equals("ElementIndex")) {
            return browser.getElementIndex(arg1);
        }
        if (command.equals("ElementPositionLeft")) {
            return browser.getElementPositionLeft(arg1);
        }
        if (command.equals("ElementPositionTop")) {
            return browser.getElementPositionTop(arg1);
        }
        if (command.equals("ElementWidth")) {
            return browser.getElementWidth(arg1);
        }
        if (command.equals("Eval")) {
            return browser.getEval(arg1);
        }
        if (command.equals("Expression")) {
            return browser.getExpression(arg1);
        }
        if (command.equals("SelectedId")) {
            return browser.getSelectedId(arg1);
        }
        if (command.equals("SelectedIds")) {
            return browser.getSelectedIds(arg1);
        }
        if (command.equals("SelectedIndex")) {
            return browser.getSelectedIndex(arg1);
        }
        if (command.equals("SelectedIndexes")) {
            return browser.getSelectedIndexes(arg1);
        }
        if (command.equals("SelectedLabel")) {
            return browser.getSelectedLabel(arg1);
        }
        if (command.equals("SelectedLabels")) {
            return browser.getSelectedLabels(arg1);
        }
        if (command.equals("SelectedValue")) {
            return browser.getSelectedValue(arg1);
        }
        if (command.equals("SelectedValues")) {
            return browser.getSelectedValues(arg1);
        }
        if (command.equals("SelectOptions")) {
            return browser.getSelectOptions(arg1);
        }
        if (command.equals("Table")) {
            return browser.getTable(arg1);
        }
        if (command.equals("Text")) {
            return browser.getText(arg1);
        }
        if (command.equals("Value")) {
            return browser.getValue(arg1);
        }
        if (command.equals("XpathCount")) {
            return browser.getXpathCount(arg1);
        }
        if (command.equals("Checked")) {
            return browser.isChecked(arg1);
        }
        if (command.equals("Editable")) {
            return browser.isEditable(arg1);
        }
        if (command.equals("ElementPresent")) {
            return browser.isElementPresent(arg1);
        }
        if (command.equals("SomethingSelected")) {
            return browser.isSomethingSelected(arg1);
        }
        if (command.equals("TextPresent")) {
            return browser.isTextPresent(arg1);
        }
        if (command.equals("Visible")) {
            return browser.isVisible(arg1);
        }
        // maybe it's a js extension
        String js = "Selenium.prototype.is" + command.substring(0, 1).toUpperCase() + command.substring(1);
        try {
            return browser.getEval(String.format("%s('%s', '%s')", js, arg1, arg2));
        } catch (SeleniumException se) {
            if (se.getMessage().contains(js + " is not a function")) {
                js = "Selenium.prototype.get" + command.substring(0, 1).toUpperCase() + command.substring(1);
                try {
                    return browser.getEval(String.format("%s('%s', '%s')", js, arg1, arg2));
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
//        string = string.replaceAll("\\n", "");
        string = string.replaceAll("&apos;", "'");
        string = string.replaceAll("  *", " ");
        return string;
    }

    Vector<String[]> readSelenium(String htmlFileName) throws UnsupportedEncodingException {
        InputStream stream = this.getClass().getResourceAsStream(htmlFileName);
        if (stream == null) {
            throw new RuntimeException(String.format("Could not read selenium file %s.", htmlFileName));
        }
        Reader reader = new InputStreamReader(stream, "UTF-8");
        nu.xom.Builder builder;
        nu.xom.Document doc;
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
        Vector<String[]> return_val = new Vector<String[]>();
        for (int r = 0; r < rows.size(); r++) {
            nu.xom.Node rowNode = rows.get(r);
            String[] row = new String[3];
            int col = 0;
            for (int i = 0; i < rowNode.getChildCount(); i++) {
                nu.xom.Node colNode = rowNode.getChild(i);
                if (colNode.getClass().equals(nu.xom.Element.class)) {
                    String str="";
                    for (int j = 0; j < colNode.getChildCount(); j++) {
                        str+=colNode.getChild(j).toXML();
                    }
                    row[col] = str.replaceAll("<br clear=\"none\" />", "\\\n").trim();
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

    protected BrowserDriver getBrowser() {
        return browser;
    }

    protected class CommandResult {
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
    public void setJavaScriptEnabled(boolean bool) {
        if (browser.getClass().equals(HtmlUnitDriver.class)) {
                   ((HtmlUnitDriver)browser).setJavaScriptEnabled(bool);
        } else {
            throw new RuntimeException("Can only set JavaScript for HtmlUnit tests.");
        }
    }


}
