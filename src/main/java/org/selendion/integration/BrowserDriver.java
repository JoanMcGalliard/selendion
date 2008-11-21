package org.selendion.integration;

import org.concordion.api.Evaluator;

/**
 * Created by IntelliJ IDEA.
 * User: jem
 * Date: Nov 21, 2008
 * Time: 7:43:44 AM
 * To change this template use File | Settings | File Templates.
 */
public interface BrowserDriver {
    public abstract void passVariablesIn(Evaluator evaluator);
    public abstract void passVariablesOut(Evaluator evaluator);
    public abstract void store(String name, Object value);
    public abstract void start();

    public String replaceVariables(String string);


    public abstract void stop();

    public abstract String getEval(String s);


    public abstract void waitForCondition(String arg1, String arg2);

    public abstract void waitForFrameToLoad(String arg1, String arg2);

    public abstract void waitForPageToLoad();

    public abstract boolean isVisible(String arg1);

    public abstract String getTimeout();

    public abstract void waitForPopUp(String arg1, String arg2);

    public abstract void pause(int i) throws InterruptedException;
    public abstract void addSelection(String arg1, String arg2);
    public abstract void allowNativeXpath(String arg1);
    public abstract void altKeyDown();
    public abstract void altKeyUp();
    public abstract void answerOnNextPrompt(String arg1);
    public abstract void assignId(String arg1, String arg2);
    public abstract void check(String arg1);
    public abstract void chooseCancelOnNextConfirmation();
    public abstract void chooseOkOnNextConfirmation();
    public abstract void click(String arg1);
    public abstract void clickAt(String arg1, String arg2);
    public abstract void close();
    public abstract void controlKeyDown();
    public abstract void controlKeyUp();
    public abstract void createCookie(String arg1, String arg2);
    public abstract void deleteAllVisibleCookies();
    public abstract void deleteCookie(String arg1, String arg2);
    public abstract void doubleClick(String arg1);
    public abstract void doubleClickAt(String arg1, String arg2);
    public abstract void dragAndDrop(String arg1, String arg2);
    public abstract void dragAndDropToObject(String arg1, String arg2);
    public abstract void dragdrop(String arg1, String arg2);
    public abstract void fireEvent(String arg1, String arg2);
    public abstract String getSpeed();
    public abstract void goBack();
    public abstract void highlight(String arg1);
    public abstract void keyDown(String arg1, String arg2);
    public abstract void keyPress(String arg1, String arg2);
    public abstract void keyUp(String arg1, String arg2);
    public abstract void metaKeyDown();
    public abstract void metaKeyUp();
    public abstract void mouseDown(String arg1);
    public abstract void mouseDownAt(String arg1, String arg2);
    public abstract void mouseMove(String arg1);
    public abstract void mouseMoveAt(String arg1, String arg2);
    public abstract void mouseOut(String arg1);
    public abstract void mouseOver(String arg1);
    public abstract void mouseUp(String arg1);
    public abstract void mouseUpAt(String arg1, String arg2);
    public abstract void open(String arg1);
    public abstract void openWindow(String arg1, String arg2);
    public abstract void refresh();
    public abstract void removeAllSelections(String arg1);
    public abstract void removeSelection(String arg1, String arg2);
    public abstract void runScript(String arg1);
    public abstract void select(String arg1, String arg2);
    public abstract void selectFrame(String arg1);
    public abstract void selectWindow(String arg1);
    public abstract void setBrowserLogLevel(String arg1);
    public abstract void setCursorPosition(String arg1, String arg2);
    public abstract void setMouseSpeed(String arg1);
    public abstract void setSpeed(String arg1);
    public abstract void setTimeout(String arg1);
    public abstract void shiftKeyDown();
    public abstract void shiftKeyUp();
    public abstract void submit(String arg1);
    public abstract void type(String arg1, String arg2);
    public abstract void typeKeys(String arg1, String arg2);
    public abstract void uncheck(String arg1);
    public abstract void windowFocus();
    public abstract void windowMaximize();
    public abstract Object getAlert();
    public abstract Object getAllButtons();
    public abstract Object getAllFields();
    public abstract Object getAllLinks();
    public abstract Object getAllWindowIds();
    public abstract Object getAllWindowNames();
    public abstract Object getAllWindowTitles();
    public abstract Object getBodyText();
    public abstract Object getConfirmation();
    public abstract Object getCookie();
    public abstract Object getHtmlSource();
    public abstract Object getLocation();
    public abstract Object getMouseSpeed();
    public abstract Object getPrompt();
    public abstract boolean isAlertPresent();
    public abstract Object getTitle();
    public abstract boolean isConfirmationPresent();
    public abstract boolean isPromptPresent();
    public abstract Object getAttribute(String arg1);
    public abstract Object getAttributeFromAllWindows(String arg1);
    public abstract Object getCursorPosition(String arg1);
    public abstract Object getElementHeight(String arg1);
    public abstract Object getElementIndex(String arg1);
    public abstract Object getElementPositionLeft(String arg1);
    public abstract Object getElementPositionTop(String arg1);
    public abstract Object getElementWidth(String arg1);
    public abstract Object getExpression(String arg1);
    public abstract Object getSelectedId(String arg1);
    public abstract Object getSelectedIds(String arg1);
    public abstract Object getSelectedIndex(String arg1);
    public abstract Object getSelectedIndexes(String arg1);
    public abstract Object getSelectedLabel(String arg1);
    public abstract Object getSelectedLabels(String arg1);
    public abstract Object getSelectedValue(String arg1);
    public abstract Object getSelectedValues(String arg1);
    public abstract Object getSelectOptions(String arg1);
    public abstract Object getTable(String arg1);
    public abstract Object getText(String arg1);
    public abstract Object getValue(String arg1);
    public abstract Object getXpathCount(String arg1);
    public abstract boolean isChecked(String arg1);
    public abstract boolean isEditable(String arg1);
    public abstract boolean isElementPresent(String arg1);
    public abstract boolean isSomethingSelected(String arg1);
    public abstract boolean isTextPresent(String arg1);
    
    
}
