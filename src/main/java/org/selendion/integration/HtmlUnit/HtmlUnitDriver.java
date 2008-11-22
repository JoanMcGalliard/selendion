package org.selendion.integration.HtmlUnit;

import org.selendion.integration.BrowserDriver;
import org.concordion.api.Evaluator;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.html.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlUnitDriver implements BrowserDriver {
    Page page = null;

    public HtmlUnitDriver(String baseUrl) {
        webClient = new WebClient();
        this.baseUrl = baseUrl;
    }

    private WebClient webClient;
    private String baseUrl;
    private Evaluator evaluator;

    public void passVariablesIn(Evaluator evaluator) {
        this.evaluator = evaluator;
    }

    public void passVariablesOut(Evaluator evaluator) {
    }

    public void store(String name, Object value) {
        evaluator.setVariable("#" + name, value);
    }

    public void start() {
    }


    public String replaceVariables(String string) {
        Matcher m = variablePattern.matcher(string);
        while (m.matches()) {
            string = m.group(1) + evaluator.getVariable("#" + m.group(2)) + m.group(3);
            m = variablePattern.matcher(string);
        }
        return string;
    }

    private Pattern variablePattern = Pattern.compile("(.*)\\$\\{([^}]*)\\}(.*)");

    public void stop() {

    }

    public String getEval(String s) {
        throw new RuntimeException("Not yet implemented: " + "getEval");
    }


    public void waitForCondition(String arg1, String arg2) {
        throw new RuntimeException("Not yet implemented: " + "waitForCondition");
    }

    public void waitForFrameToLoad(String arg1, String arg2) {
        throw new RuntimeException("Not yet implemented: " + "waitForFrameToLoad");
    }

    public void waitForPageToLoad() {
        //nothing to do
    }

    public boolean isVisible(String arg1) {
        throw new RuntimeException("Not yet implemented: " + "isVisible");
    }

    public String getTimeout() {
        throw new RuntimeException("Not yet implemented: " + "getTimeout");
    }

    public void waitForPopUp(String arg1, String arg2) {
        throw new RuntimeException("Not yet implemented: " + "waitForPopUp");
    }

    public void pause(int i) throws InterruptedException {
        throw new RuntimeException("Not yet implemented: " + "pause");
    }

    public void addSelection(String arg1, String arg2) {
        throw new RuntimeException("Not yet implemented: " + "addSelection");
    }

    public void allowNativeXpath(String arg1) {
        throw new RuntimeException("Not yet implemented: " + "allowNativeXpath");
    }

    public void altKeyDown() {
        throw new RuntimeException("Not yet implemented: " + "altKeyDown");
    }

    public void altKeyUp() {
        throw new RuntimeException("Not yet implemented: " + "altKeyUp");
    }

    public void answerOnNextPrompt(String arg1) {
        throw new RuntimeException("Not yet implemented: " + "answerOnNextPrompt");
    }

    public void assignId(String arg1, String arg2) {
        throw new RuntimeException("Not yet implemented: " + "assignId");
    }

    public void check(String arg1) {
        throw new RuntimeException("Not yet implemented: " + "check");
    }

    public void chooseCancelOnNextConfirmation() {
        throw new RuntimeException("Not yet implemented: " + "chooseCancelOnNextConfirmation");
    }

    public void chooseOkOnNextConfirmation() {
        throw new RuntimeException("Not yet implemented: " + "chooseOkOnNextConfirmation");
    }

    private HtmlElement getHtmlElement(String key) {

        if (key.startsWith("xPath=")) {
            return (HtmlElement)((HtmlPage) page).getByXPath(key.replaceFirst("xPath=", "")).get(0);
        }
        try {
            return ((HtmlPage) page).getHtmlElementById(key);
        }
        catch (ElementNotFoundException e) {
            //pass through
        }
        try {
        return  ((HtmlPage) page).getHtmlElementsByName(key).get(0);
        }
        catch (Exception e) {
            throw new HtmlUnitException(e);
        }
    }

    public void click(String arg1) {
        HtmlElement element = getHtmlElement(arg1);
        try {

        if (element.getClass().equals(HtmlSubmitInput.class )) {
                page =  ((HtmlSubmitInput)element).click();

        } else if (element.getClass().equals(HtmlCheckBoxInput.class )) {
              page =  ((HtmlCheckBoxInput)element).click();
        }  else if (element.getClass().equals(HtmlImage.class )) {
              page =  ((HtmlImage)element).click();
        }
          } catch (IOException e) {
                throw new HtmlUnitException(e);
            }



    }

    public void clickAt(String arg1, String arg2) {
        throw new RuntimeException("Not yet implemented: " + "clickAt");
    }

    public void close() {
        throw new RuntimeException("Not yet implemented: " + "close");
    }

    public void controlKeyDown() {
        throw new RuntimeException("Not yet implemented: " + "controlKeyDown");
    }

    public void controlKeyUp() {
        throw new RuntimeException("Not yet implemented: " + "controlKeyUp");
    }

    public void createCookie(String arg1, String arg2) {
        throw new RuntimeException("Not yet implemented: " + "createCookie");
    }

    public void deleteAllVisibleCookies() {
        throw new RuntimeException("Not yet implemented: " + "deleteAllVisibleCookies");
    }

    public void deleteCookie(String arg1, String arg2) {
        throw new RuntimeException("Not yet implemented: " + "deleteCookie");
    }

    public void doubleClick(String arg1) {
        throw new RuntimeException("Not yet implemented: " + "doubleClick");
    }

    public void doubleClickAt(String arg1, String arg2) {
        throw new RuntimeException("Not yet implemented: " + "doubleClickAt");
    }

    public void dragAndDrop(String arg1, String arg2) {
        throw new RuntimeException("Not yet implemented: " + "dragAndDrop");
    }

    public void dragAndDropToObject(String arg1, String arg2) {
        throw new RuntimeException("Not yet implemented: " + "dragAndDropToObject");
    }

    public void dragdrop(String arg1, String arg2) {
        throw new RuntimeException("Not yet implemented: " + "dragdrop");
    }

    public void fireEvent(String arg1, String arg2) {
        throw new RuntimeException("Not yet implemented: " + "fireEvent");
    }

    public String getSpeed() {
        throw new RuntimeException("Not yet implemented: " + "getSpeed");
    }

    public void goBack() {
        throw new RuntimeException("Not yet implemented: " + "goBack");
    }

    public void highlight(String arg1) {
        throw new RuntimeException("Not yet implemented: " + "highlight");
    }

    public void keyDown(String arg1, String arg2) {
        throw new RuntimeException("Not yet implemented: " + "keyDown");
    }

    public void keyPress(String arg1, String arg2) {
        throw new RuntimeException("Not yet implemented: " + "keyPress");
    }

    public void keyUp(String arg1, String arg2) {
        throw new RuntimeException("Not yet implemented: " + "keyUp");
    }

    public void metaKeyDown() {
        throw new RuntimeException("Not yet implemented: " + "metaKeyDown");
    }

    public void metaKeyUp() {
        throw new RuntimeException("Not yet implemented: " + "metaKeyUp");
    }

    public void mouseDown(String arg1) {
        throw new RuntimeException("Not yet implemented: " + "mouseDown");
    }

    public void mouseDownAt(String arg1, String arg2) {
        throw new RuntimeException("Not yet implemented: " + "mouseDownAt");
    }

    public void mouseMove(String arg1) {
        throw new RuntimeException("Not yet implemented: " + "mouseMove");
    }

    public void mouseMoveAt(String arg1, String arg2) {
        throw new RuntimeException("Not yet implemented: " + "mouseMoveAt");
    }

    public void mouseOut(String arg1) {
        throw new RuntimeException("Not yet implemented: " + "mouseOut");
    }

    public void mouseOver(String arg1) {
        throw new RuntimeException("Not yet implemented: " + "mouseOver");
    }

    public void mouseUp(String arg1) {
        throw new RuntimeException("Not yet implemented: " + "mouseUp");
    }

    public void mouseUpAt(String arg1, String arg2) {
        throw new RuntimeException("Not yet implemented: " + "mouseUpAt");
    }

    public void open(String arg1) {
        try {

            if (arg1.startsWith("http://")) {
                page = webClient.getPage(arg1);
            } else {
                page = webClient.getPage(baseUrl + arg1);
            }
        } catch (IOException e) {
            throw new HtmlUnitException(e);
        }

    }

    public void openWindow(String arg1, String arg2) {
        throw new RuntimeException("Not yet implemented: " + "openWindow");
    }

    public void refresh() {
        throw new RuntimeException("Not yet implemented: " + "refresh");
    }

    public void removeAllSelections(String arg1) {
        throw new RuntimeException("Not yet implemented: " + "removeAllSelections");
    }

    public void removeSelection(String arg1, String arg2) {
        throw new RuntimeException("Not yet implemented: " + "removeSelection");
    }

    public void runScript(String arg1) {
        throw new RuntimeException("Not yet implemented: " + "runScript");
    }

    public void select(String arg1, String arg2) {
        HtmlSelect select = (HtmlSelect) ((HtmlPage) page).getHtmlElementsByName(arg1).get(0);
        try {
            select.click();
        } catch (IOException e) {
            throw new HtmlUnitException(e);
        }


    }

    public void selectFrame(String arg1) {
        throw new RuntimeException("Not yet implemented: " + "selectFrame");
    }

    public void selectWindow(String arg1) {
        throw new RuntimeException("Not yet implemented: " + "selectWindow");
    }

    public void setBrowserLogLevel(String arg1) {
        throw new RuntimeException("Not yet implemented: " + "setBrowserLogLevel");
    }

    public void setCursorPosition(String arg1, String arg2) {
        throw new RuntimeException("Not yet implemented: " + "setCursorPosition");
    }

    public void setMouseSpeed(String arg1) {
        throw new RuntimeException("Not yet implemented: " + "setMouseSpeed");
    }

    public void setSpeed(String arg1) {
        throw new RuntimeException("Not yet implemented: " + "setSpeed");
    }

    public void setTimeout(String arg1) {
        throw new RuntimeException("Not yet implemented: " + "setTimeout");
    }

    public void shiftKeyDown() {
        throw new RuntimeException("Not yet implemented: " + "shiftKeyDown");
    }

    public void shiftKeyUp() {
        throw new RuntimeException("Not yet implemented: " + "shiftKeyUp");
    }

    public void submit(String arg1) {
        throw new RuntimeException("Not yet implemented: " + "submit");
    }

    public void type(String arg1, String arg2) {
        try {
            HtmlTextInput textField = (HtmlTextInput) ((HtmlPage) page).getHtmlElementsByName(arg1).get(0);
            textField.setValueAttribute(arg2);
            return;
        }
        catch (Exception e) {
            // passthru

        }
        try {
            HtmlPasswordInput textField = (HtmlPasswordInput) ((HtmlPage) page).getHtmlElementsByName(arg1).get(0);
            textField.setValueAttribute(arg2);
            return;
        }
        catch (Exception e) {
            throw new RuntimeException("Can't handle type(" + arg1 + ", " + arg2 + ")");

        }

    }

    public void typeKeys(String arg1, String arg2) {
        throw new RuntimeException("Not yet implemented: " + "typeKeys");
    }

    public void uncheck(String arg1) {
        throw new RuntimeException("Not yet implemented: " + "uncheck");
    }

    public void windowFocus() {
        throw new RuntimeException("Not yet implemented: " + "windowFocus");
    }

    public void windowMaximize() {
        throw new RuntimeException("Not yet implemented: " + "windowMaximize");
    }

    public Object getAlert() {
        throw new RuntimeException("Not yet implemented: " + "getAlert");
    }

    public Object getAllButtons() {
        throw new RuntimeException("Not yet implemented: " + "getAllButtons");
    }

    public Object getAllFields() {
        throw new RuntimeException("Not yet implemented: " + "getAllFields");
    }

    public Object getAllLinks() {
        throw new RuntimeException("Not yet implemented: " + "getAllLinks");
    }

    public Object getAllWindowIds() {
        throw new RuntimeException("Not yet implemented: " + "getAllWindowIds");
    }

    public Object getAllWindowNames() {
        throw new RuntimeException("Not yet implemented: " + "getAllWindowNames");
    }

    public Object getAllWindowTitles() {
        throw new RuntimeException("Not yet implemented: " + "getAllWindowTitles");
    }

    public Object getBodyText() {
        throw new RuntimeException("Not yet implemented: " + "getBodyText");
    }

    public Object getConfirmation() {
        throw new RuntimeException("Not yet implemented: " + "getConfirmation");
    }

    public Object getCookie() {
        throw new RuntimeException("Not yet implemented: " + "getCookie");
    }

    public Object getHtmlSource() {
        throw new RuntimeException("Not yet implemented: " + "getHtmlSource");
    }

    public Object getLocation() {
        throw new RuntimeException("Not yet implemented: " + "getLocation");
    }

    public Object getMouseSpeed() {
        throw new RuntimeException("Not yet implemented: " + "getMouseSpeed");
    }

    public Object getPrompt() {
        throw new RuntimeException("Not yet implemented: " + "getPrompt");
    }

    public boolean isAlertPresent() {
        throw new RuntimeException("Not yet implemented: " + "isAlertPresent");
    }

    public Object getTitle() {
        throw new RuntimeException("Not yet implemented: " + "getTitle");
    }

    public boolean isConfirmationPresent() {
        throw new RuntimeException("Not yet implemented: " + "isConfirmationPresent");
    }

    public boolean isPromptPresent() {
        throw new RuntimeException("Not yet implemented: " + "isPromptPresent");
    }

    public Object getAttribute(String arg1) {
        throw new RuntimeException("Not yet implemented: " + "getAttribute");
    }

    public Object getAttributeFromAllWindows(String arg1) {
        throw new RuntimeException("Not yet implemented: " + "getAttributeFromAllWindows");
    }

    public Object getCursorPosition(String arg1) {
        throw new RuntimeException("Not yet implemented: " + "getCursorPosition");
    }

    public Object getElementHeight(String arg1) {
        throw new RuntimeException("Not yet implemented: " + "getElementHeight");
    }

    public Object getElementIndex(String arg1) {
        throw new RuntimeException("Not yet implemented: " + "getElementIndex");
    }

    public Object getElementPositionLeft(String arg1) {
        throw new RuntimeException("Not yet implemented: " + "getElementPositionLeft");
    }

    public Object getElementPositionTop(String arg1) {
        throw new RuntimeException("Not yet implemented: " + "getElementPositionTop");
    }

    public Object getElementWidth(String arg1) {
        throw new RuntimeException("Not yet implemented: " + "getElementWidth");
    }

    public Object getExpression(String arg1) {
        throw new RuntimeException("Not yet implemented: " + "getExpression");
    }

    public Object getSelectedId(String arg1) {
        throw new RuntimeException("Not yet implemented: " + "getSelectedId");
    }

    public Object getSelectedIds(String arg1) {
        throw new RuntimeException("Not yet implemented: " + "getSelectedIds");
    }

    public Object getSelectedIndex(String arg1) {
        throw new RuntimeException("Not yet implemented: " + "getSelectedIndex");
    }

    public Object getSelectedIndexes(String arg1) {
        throw new RuntimeException("Not yet implemented: " + "getSelectedIndexes");
    }

    public Object getSelectedLabel(String arg1) {
        throw new RuntimeException("Not yet implemented: " + "getSelectedLabel");
    }

    public Object getSelectedLabels(String arg1) {
        throw new RuntimeException("Not yet implemented: " + "getSelectedLabels");
    }

    public Object getSelectedValue(String arg1) {
        throw new RuntimeException("Not yet implemented: " + "getSelectedValue");
    }

    public Object getSelectedValues(String arg1) {
        throw new RuntimeException("Not yet implemented: " + "getSelectedValues");
    }

    public Object getSelectOptions(String arg1) {
        throw new RuntimeException("Not yet implemented: " + "getSelectOptions");
    }

    public Object getTable(String arg1) {
        throw new RuntimeException("Not yet implemented: " + "getTable");
    }

    public String getText(String arg1) {
        HtmlElement element = getHtmlElement(arg1);
        return element.getTextContent();


    }

    public Object getValue(String arg1) {
        throw new RuntimeException("Not yet implemented: " + "getValue");
    }

    public Object getXpathCount(String arg1) {
        throw new RuntimeException("Not yet implemented: " + "getXpathCount");
    }

    public boolean isChecked(String arg1) {
        throw new RuntimeException("Not yet implemented: " + "isChecked");
    }

    public boolean isEditable(String arg1) {
        throw new RuntimeException("Not yet implemented: " + "isEditable");
    }

    public boolean isElementPresent(String arg1) {
        throw new RuntimeException("Not yet implemented: " + "isElementPresent");
    }

    public boolean isSomethingSelected(String arg1) {
        throw new RuntimeException("Not yet implemented: " + "isSomethingSelected");
    }

    public boolean isTextPresent(String arg1) {
        try {
            return ((HtmlPage) page).getBody().getTextContent().contains(arg1);
        }
        catch (ClassCastException cce) {
            // pass through
        }
        try {
            InputStream stream = ((UnexpectedPage) page).getInputStream();
            return slurp(stream).contains(arg1);
        } catch (IOException e) {
        }
        throw new RuntimeException("Can't handle isTextPresent " + arg1);
    }

    private static String slurp(InputStream in) throws IOException {
        StringBuffer out = new StringBuffer();
        byte[] b = new byte[4096];
        for (int n; (n = in.read(b)) != -1;) {
            out.append(new String(b, 0, n));
        }
        return out.toString();
    }


}
