package org.selendion.integration.HtmlUnit;

import org.selendion.integration.BrowserDriver;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.IOException;

public class HtmlUnitDriver implements BrowserDriver {
    public HtmlUnitDriver(String baseUrl) {
        webClient = new WebClient();
        this.baseUrl = baseUrl;
    }
    private WebClient webClient;
    private String baseUrl;

    public void start() {
    }

    public void stop(){ throw new RuntimeException("Not yet implemented"); }

    public String getEval(String s){ throw new RuntimeException("Not yet implemented"); }


    public void waitForCondition(String arg1, String arg2){ throw new RuntimeException("Not yet implemented"); }

    public void waitForFrameToLoad(String arg1, String arg2){ throw new RuntimeException("Not yet implemented"); }

    public void waitForPageToLoad(){ throw new RuntimeException("Not yet implemented"); }

    public boolean isVisible(String arg1){ throw new RuntimeException("Not yet implemented"); }

    public String getTimeout(){ throw new RuntimeException("Not yet implemented"); }

    public void waitForPopUp(String arg1, String arg2){ throw new RuntimeException("Not yet implemented"); }

    public void pause(int i) throws InterruptedException { throw new RuntimeException("Not yet implemented"); }
    public void addSelection(String arg1, String arg2){ throw new RuntimeException("Not yet implemented"); }
    public void allowNativeXpath(String arg1){ throw new RuntimeException("Not yet implemented"); }
    public void altKeyDown(){ throw new RuntimeException("Not yet implemented"); }
    public void altKeyUp(){ throw new RuntimeException("Not yet implemented"); }
    public void answerOnNextPrompt(String arg1){ throw new RuntimeException("Not yet implemented"); }
    public void assignId(String arg1, String arg2){ throw new RuntimeException("Not yet implemented"); }
    public void check(String arg1){ throw new RuntimeException("Not yet implemented"); }
    public void chooseCancelOnNextConfirmation(){ throw new RuntimeException("Not yet implemented"); }
    public void chooseOkOnNextConfirmation(){ throw new RuntimeException("Not yet implemented"); }
    public void click(String arg1){ throw new RuntimeException("Not yet implemented"); }
    public void clickAt(String arg1, String arg2){ throw new RuntimeException("Not yet implemented"); }
    public void close(){ throw new RuntimeException("Not yet implemented"); }
    public void controlKeyDown(){ throw new RuntimeException("Not yet implemented"); }
    public void controlKeyUp(){ throw new RuntimeException("Not yet implemented"); }
    public void createCookie(String arg1, String arg2){ throw new RuntimeException("Not yet implemented"); }
    public void deleteAllVisibleCookies(){ throw new RuntimeException("Not yet implemented"); }
    public void deleteCookie(String arg1, String arg2){ throw new RuntimeException("Not yet implemented"); }
    public void doubleClick(String arg1){ throw new RuntimeException("Not yet implemented"); }
    public void doubleClickAt(String arg1, String arg2){ throw new RuntimeException("Not yet implemented"); }
    public void dragAndDrop(String arg1, String arg2){ throw new RuntimeException("Not yet implemented"); }
    public void dragAndDropToObject(String arg1, String arg2){ throw new RuntimeException("Not yet implemented"); }
    public void dragdrop(String arg1, String arg2){ throw new RuntimeException("Not yet implemented"); }
    public void fireEvent(String arg1, String arg2){ throw new RuntimeException("Not yet implemented"); }
    public String getSpeed(){ throw new RuntimeException("Not yet implemented"); }
    public void goBack(){ throw new RuntimeException("Not yet implemented"); }
    public void highlight(String arg1){ throw new RuntimeException("Not yet implemented"); }
    public void keyDown(String arg1, String arg2){ throw new RuntimeException("Not yet implemented"); }
    public void keyPress(String arg1, String arg2){ throw new RuntimeException("Not yet implemented"); }
    public void keyUp(String arg1, String arg2){ throw new RuntimeException("Not yet implemented"); }
    public void metaKeyDown(){ throw new RuntimeException("Not yet implemented"); }
    public void metaKeyUp(){ throw new RuntimeException("Not yet implemented"); }
    public void mouseDown(String arg1){ throw new RuntimeException("Not yet implemented"); }
    public void mouseDownAt(String arg1, String arg2){ throw new RuntimeException("Not yet implemented"); }
    public void mouseMove(String arg1){ throw new RuntimeException("Not yet implemented"); }
    public void mouseMoveAt(String arg1, String arg2){ throw new RuntimeException("Not yet implemented"); }
    public void mouseOut(String arg1){ throw new RuntimeException("Not yet implemented"); }
    public void mouseOver(String arg1){ throw new RuntimeException("Not yet implemented"); }
    public void mouseUp(String arg1){ throw new RuntimeException("Not yet implemented"); }
    public void mouseUpAt(String arg1, String arg2){ throw new RuntimeException("Not yet implemented"); }
    public void open(String arg1){ throw new RuntimeException("Not yet implemented"); }
    public void openWindow(String arg1, String arg2){ throw new RuntimeException("Not yet implemented"); }
    public void refresh(){ throw new RuntimeException("Not yet implemented"); }
    public void removeAllSelections(String arg1){ throw new RuntimeException("Not yet implemented"); }
    public void removeSelection(String arg1, String arg2){ throw new RuntimeException("Not yet implemented"); }
    public void runScript(String arg1){ throw new RuntimeException("Not yet implemented"); }
    public void select(String arg1, String arg2){ throw new RuntimeException("Not yet implemented"); }
    public void selectFrame(String arg1){ throw new RuntimeException("Not yet implemented"); }
    public void selectWindow(String arg1){ throw new RuntimeException("Not yet implemented"); }
    public void setBrowserLogLevel(String arg1){ throw new RuntimeException("Not yet implemented"); }
    public void setCursorPosition(String arg1, String arg2){ throw new RuntimeException("Not yet implemented"); }
    public void setMouseSpeed(String arg1){ throw new RuntimeException("Not yet implemented"); }
    public void setSpeed(String arg1){ throw new RuntimeException("Not yet implemented"); }
    public void setTimeout(String arg1){ throw new RuntimeException("Not yet implemented"); }
    public void shiftKeyDown(){ throw new RuntimeException("Not yet implemented"); }
    public void shiftKeyUp(){ throw new RuntimeException("Not yet implemented"); }
    public void submit(String arg1){ throw new RuntimeException("Not yet implemented"); }
    public void type(String arg1, String arg2){ throw new RuntimeException("Not yet implemented"); }
    public void typeKeys(String arg1, String arg2){ throw new RuntimeException("Not yet implemented"); }
    public void uncheck(String arg1){ throw new RuntimeException("Not yet implemented"); }
    public void windowFocus(){ throw new RuntimeException("Not yet implemented"); }
    public void windowMaximize(){ throw new RuntimeException("Not yet implemented"); }
    public Object getAlert(){ throw new RuntimeException("Not yet implemented"); }
    public Object getAllButtons(){ throw new RuntimeException("Not yet implemented"); }
    public Object getAllFields(){ throw new RuntimeException("Not yet implemented"); }
    public Object getAllLinks(){ throw new RuntimeException("Not yet implemented"); }
    public Object getAllWindowIds(){ throw new RuntimeException("Not yet implemented"); }
    public Object getAllWindowNames(){ throw new RuntimeException("Not yet implemented"); }
    public Object getAllWindowTitles(){ throw new RuntimeException("Not yet implemented"); }
    public Object getBodyText(){ throw new RuntimeException("Not yet implemented"); }
    public Object getConfirmation(){ throw new RuntimeException("Not yet implemented"); }
    public Object getCookie(){ throw new RuntimeException("Not yet implemented"); }
    public Object getHtmlSource(){ throw new RuntimeException("Not yet implemented"); }
    public Object getLocation(){ throw new RuntimeException("Not yet implemented"); }
    public Object getMouseSpeed(){ throw new RuntimeException("Not yet implemented"); }
    public Object getPrompt(){ throw new RuntimeException("Not yet implemented"); }
    public boolean isAlertPresent(){ throw new RuntimeException("Not yet implemented"); }
    public Object getTitle(){ throw new RuntimeException("Not yet implemented"); }
    public boolean isConfirmationPresent(){ throw new RuntimeException("Not yet implemented"); }
    public boolean isPromptPresent(){ throw new RuntimeException("Not yet implemented"); }
    public Object getAttribute(String arg1){ throw new RuntimeException("Not yet implemented"); }
    public Object getAttributeFromAllWindows(String arg1){ throw new RuntimeException("Not yet implemented"); }
    public Object getCursorPosition(String arg1){ throw new RuntimeException("Not yet implemented"); }
    public Object getElementHeight(String arg1){ throw new RuntimeException("Not yet implemented"); }
    public Object getElementIndex(String arg1){ throw new RuntimeException("Not yet implemented"); }
    public Object getElementPositionLeft(String arg1){ throw new RuntimeException("Not yet implemented"); }
    public Object getElementPositionTop(String arg1){ throw new RuntimeException("Not yet implemented"); }
    public Object getElementWidth(String arg1){ throw new RuntimeException("Not yet implemented"); }
    public Object getExpression(String arg1){ throw new RuntimeException("Not yet implemented"); }
    public Object getSelectedId(String arg1){ throw new RuntimeException("Not yet implemented"); }
    public Object getSelectedIds(String arg1){ throw new RuntimeException("Not yet implemented"); }
    public Object getSelectedIndex(String arg1){ throw new RuntimeException("Not yet implemented"); }
    public Object getSelectedIndexes(String arg1){ throw new RuntimeException("Not yet implemented"); }
    public Object getSelectedLabel(String arg1){ throw new RuntimeException("Not yet implemented"); }
    public Object getSelectedLabels(String arg1){ throw new RuntimeException("Not yet implemented"); }
    public Object getSelectedValue(String arg1){ throw new RuntimeException("Not yet implemented"); }
    public Object getSelectedValues(String arg1){ throw new RuntimeException("Not yet implemented"); }
    public Object getSelectOptions(String arg1){ throw new RuntimeException("Not yet implemented"); }
    public Object getTable(String arg1){ throw new RuntimeException("Not yet implemented"); }
    public Object getText(String arg1){ throw new RuntimeException("Not yet implemented"); }
    public Object getValue(String arg1){ throw new RuntimeException("Not yet implemented"); }
    public Object getXpathCount(String arg1){ throw new RuntimeException("Not yet implemented"); }
    public boolean isChecked(String arg1){ throw new RuntimeException("Not yet implemented"); }
    public boolean isEditable(String arg1){ throw new RuntimeException("Not yet implemented"); }
    public boolean isElementPresent(String arg1){ throw new RuntimeException("Not yet implemented"); }
    public boolean isSomethingSelected(String arg1){ throw new RuntimeException("Not yet implemented"); }
    public boolean isTextPresent(String arg1){ throw new RuntimeException("Not yet implemented"); }

}
