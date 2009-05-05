/*
	Copyright Joan McGalliard, 2008-9
*/

package org.selendion.integration.HtmlUnit;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;
import org.concordion.api.Evaluator;
import org.selendion.integration.BrowserDriver;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlUnitDriver implements BrowserDriver {
    private Page page = null;
    private Page old_page;
    private String timeout="30000";
    static private final String nbsp = String.format("%c", 160);
    private final String IMPLEMENTATION_REQUIRED = "Not yet implemented (please report to selendion.org): ";
    private final List collectedAlerts = new ArrayList();

    @SuppressWarnings("unchecked")
    public HtmlUnitDriver(String baseUrl) {
        webClient = new WebClient();
        webClient.setAlertHandler(new CollectingAlertHandler(collectedAlerts));

        this.baseUrl = baseUrl;
    }

    private final WebClient webClient;
    private final String baseUrl;
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
        System.out.println("INFO: Starting HtmlUnit.");
    }

    public boolean isStarted() {
        return true;
    }


    public String replaceVariables(String string) {
        Matcher m = variablePattern.matcher(string);
        while (m.matches()) {
            string = m.group(1) + evaluator.getVariable("#" + m.group(2)) + m.group(3);
            m = variablePattern.matcher(string);
        }
        return string.replaceAll("\\\\n", "\n");
    }

    private final Pattern variablePattern = Pattern.compile("(.*)\\$\\{([^}]*)\\}(.*)");

    public void stop() {

//        final List topWindows = new ArrayList();
//        for (final Iterator iter = webClient.getWebWindows().iterator();
//             iter.hasNext();) {
//            final WebWindow window = (WebWindow) iter.next();
//            if (window instanceof TopLevelWindow) {
//                topWindows.add(window);
//            }
//        }
//        for (final Iterator iter = topWindows.iterator(); iter.hasNext();) {
//            final TopLevelWindow window = (TopLevelWindow) iter.next();
//            window.close();
//        }
        webClient.closeAllWindows();

    }

    public String getEval(String s) {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "getEval");
    }


    public void waitForCondition(String arg1, String arg2) {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "waitForCondition");
    }

    public void waitForFrameToLoad(String arg1, String arg2) {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "waitForFrameToLoad");
    }

    public void waitForPageToLoad() {
    }

    public boolean isVisible(String arg1) {
//        StyledElement element = (StyledElement) getHtmlElement(arg1);
//        return ! element.getStyleAttribute().contains("display: none;");
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "isVisible");

    }

    public void setTimeout(String timeout) {
        Integer.parseInt(timeout);
        this.timeout=timeout;
    }
    public String getTimeout() {
        return timeout;
    }

    public void waitForPopUp(String arg1, String arg2) {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "waitForPopUp");
    }
    public void pauseInWaitFor (int milliseconds) throws InterruptedException {
        long start = System.currentTimeMillis();
        try {
            page = ((HtmlPage)page).refresh();
        } catch (IOException e) {
            throw new HtmlUnitException(e);
        }
            Thread.sleep(System.currentTimeMillis()-start+milliseconds);
    }

    public void pause(int milliseconds) throws InterruptedException {
            Thread.sleep(milliseconds);
    }

    public void addSelection(String arg1, String arg2) {
        select(arg1, arg2);
    }

    public void allowNativeXpath(String arg1) {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "allowNativeXpath");
    }

    public void altKeyDown() {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "altKeyDown");
    }

    public void altKeyUp() {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "altKeyUp");
    }

    public void answerOnNextPrompt(String arg1) {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "answerOnNextPrompt");
    }

    public void assignId(String arg1, String arg2) {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "assignId");
    }

    public void check(String arg1) {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "check");
    }

    public void chooseCancelOnNextConfirmation() {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "chooseCancelOnNextConfirmation");
    }

    public void chooseOkOnNextConfirmation() {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "chooseOkOnNextConfirmation");
    }

    private HtmlElement getHtmlElement(String key) {
        HtmlElement element =  getHtmlElement((HtmlPage) page, key);
        if (element != null) {
            return element;
        }
        else {
             List<FrameWindow> window = ((HtmlPage) page).getFrames();
             for (int i =0; i < window.size(); i++) {
                 element = getHtmlElement((HtmlPage) window.get(i).getEnclosedPage(), key);
                 if (element != null) {
                     return element;
                 }
             }
        }
        throw new HtmlUnitException("ERROR: Element " + key + " not found");
    }
    private HtmlElement getHtmlElement(HtmlPage page, String key) {

        if (key.startsWith("xPath=")) {
            List<?> list = ((HtmlPage) page).getByXPath(key.replaceFirst("xPath=", ""));
            if (list.size() == 0) {
                return null;
            }
            return (HtmlElement) list.get(0);
        } else if (key.startsWith("identifier=") || key.startsWith("id=")) {
            try {
                return page.getHtmlElementById(key.replaceFirst("^id=", "").replaceFirst("^identifier=", ""));
            }
            catch (ElementNotFoundException e) {
                return null;
            }
        } else if (key.startsWith("name=")) {
            List<HtmlElement> list = ((HtmlPage) page).getElementsByIdAndOrName(key.replaceFirst("^name=", ""));
            if (list.size() == 0) {
                return null;
            }

            return list.get(0);
        } else if (key.startsWith("link=")) {
            List<HtmlAnchor> anchors = ((HtmlPage) page).getAnchors();
            int i = 0;
            while (i < anchors.size()) {
                if (anchors.get(i).getTextContent().equals(key.replaceFirst("link=", ""))) {
                    return anchors.get(i);
                }
                i++;
            }
            return null;
        } else if (key.startsWith("dom=") || key.startsWith("css=")
                || key.startsWith("document")) {
            throw new RuntimeException(IMPLEMENTATION_REQUIRED + key);
        } else if (key.startsWith("//")) {

            List<?> list = ((HtmlPage) page).getByXPath(key);
            if (list.size() == 0) {
                return null;
            }
            return (HtmlElement) list.get(0);
        }
        try {
            return page.getHtmlElementById(key);
        }
        catch (ElementNotFoundException e) {
            //pass through
        }
        try {
            return page.getElementsByIdAndOrName(key).get(0);
        }
        catch (Throwable e) {
            return null;
        }
    }

    public void click(String arg1) {
        HtmlElement element = getHtmlElement(arg1);
        try {

            old_page = page;
            if (element.getClass().equals(HtmlSubmitInput.class)) {
                page = ((HtmlSubmitInput) element).click();

            } else if (element.getClass().equals(HtmlCheckBoxInput.class)) {
                page = ((HtmlCheckBoxInput) element).click();
            } else if (element.getClass().equals(HtmlImage.class)) {
                page = ((HtmlImage) element).click();
            } else if (element.getClass().equals(HtmlAnchor.class)) {
                page = ((HtmlAnchor) element).click();
            } else if (element.getClass().equals(HtmlTextInput.class)) {
                page = ((HtmlTextInput) element).click();
            } else if (element.getClass().equals(HtmlRadioButtonInput.class)) {
                page = ((HtmlRadioButtonInput) element).click();
            } else if (element.getClass().equals(HtmlImageInput.class)) {
                page = ((HtmlImageInput) element).click();
            } else if (element.getClass().equals(HtmlOption.class)) {
                page = ((HtmlOption) element).click();
            } else {
                throw new RuntimeException(IMPLEMENTATION_REQUIRED + "click " + element.getClass().getSimpleName());
            }
        } catch (IOException e) {
            throw new HtmlUnitException(e);
        }


    }

    public void clickAt(String arg1, String arg2) {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "clickAt");
    }

    public void close() {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "close");
    }

    public void controlKeyDown() {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "controlKeyDown");
    }

    public void controlKeyUp() {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "controlKeyUp");
    }

    public void createCookie(String arg1, String arg2) {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "createCookie");
    }

    public void deleteAllVisibleCookies() {
        webClient.getCookieManager().clearCookies();
    }

    public void deleteCookie(String arg1, String arg2) {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "deleteCookie");
    }

    public void doubleClick(String arg1) {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "doubleClick");
    }

    public void doubleClickAt(String arg1, String arg2) {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "doubleClickAt");
    }

    public void dragAndDrop(String arg1, String arg2) {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "dragAndDrop");
    }

    public void dragAndDropToObject(String arg1, String arg2) {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "dragAndDropToObject");
    }

    public void dragdrop(String arg1, String arg2) {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "dragdrop");
    }

    public String echo(String arg1) {
        return replaceVariables(arg1);
    }

    public void fireEvent(String arg1, String arg2) {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "fireEvent");
    }

    public String getSpeed() {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "getSpeed");
    }

    public void goBack() {
        page = old_page;

    }

    public void highlight(String arg1) {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "highlight");
    }

    public void keyDown(String arg1, String arg2) {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "keyDown");
    }

    public void keyPress(String arg1, String arg2) {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "keyPress");
    }

    public void keyUp(String arg1, String arg2) {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "keyUp");
    }

    public void metaKeyDown() {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "metaKeyDown");
    }

    public void metaKeyUp() {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "metaKeyUp");
    }

    public void mouseDown(String arg1) {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "mouseDown");
    }

    public void mouseDownAt(String arg1, String arg2) {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "mouseDownAt");
    }

    public void mouseMove(String arg1) {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "mouseMove");
    }

    public void mouseMoveAt(String arg1, String arg2) {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "mouseMoveAt");
    }

    public void mouseOut(String arg1) {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "mouseOut");
    }

    public void mouseOver(String arg1) {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "mouseOver");
    }

    public void mouseUp(String arg1) {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "mouseUp");
    }

    public void mouseUpAt(String arg1, String arg2) {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "mouseUpAt");
    }

    public void open(String arg1) {
        try {

            if (arg1.startsWith("http://")) {
                page = webClient.getPage(arg1);
            } else if (!baseUrl.endsWith("/") && !arg1.startsWith("/")){
                page = webClient.getPage(baseUrl + "/" + arg1);

            } else {
                page = webClient.getPage(baseUrl + arg1);
            }
        } catch (IOException e) {
            throw new HtmlUnitException(e);
        }

    }

    public void openWindow(String arg1, String arg2) {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "openWindow");
    }

    public void refresh() {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "refresh");
    }

    public void removeAllSelections(String arg1) {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "removeAllSelections");
    }

    public void removeSelection(String arg1, String arg2) {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "removeSelection");
    }

    public void runScript(String arg1) {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "runScript");
    }

    public void select(String arg1, String arg2) {
        String label = arg2.replaceFirst("label=", "").replaceFirst("value=", "");
        HtmlSelect select = (HtmlSelect) getHtmlElement(arg1);
        for (HtmlElement htmlElement : select.getAllHtmlChildElements()) {
            HtmlOption option = (HtmlOption) htmlElement;
            if (option.getTextContent().equals(label)) {
                old_page = page;

                page = select.setSelectedAttribute(option.getValueAttribute(), true);
                return;
            }

        }
        throw new HtmlUnitException("ERROR: Option with label '" + label + "' not found");
    }

    public void selectFrame(String arg1) {
        if (arg1.startsWith("index=")) {
            List<FrameWindow> window = ((HtmlPage) page).getFrames();
            page = window.get(Integer.parseInt(arg1.replaceFirst("^index=", ""))).getEnclosedPage();
        } else if (arg1.equals("relative=top")) {
            System.out.println(page.getEnclosingWindow().getTopWindow().toString());
            page = page.getEnclosingWindow().getTopWindow().getEnclosedPage();
        } else {
            throw new RuntimeException(IMPLEMENTATION_REQUIRED + "selectFrame with parameter " + arg1);
        }


    }

    public void selectWindow(String arg1) {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "selectWindow");
    }

    public void setBrowserLogLevel(String arg1) {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "setBrowserLogLevel");
    }

    public void setCursorPosition(String arg1, String arg2) {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "setCursorPosition");
    }

    public void setMouseSpeed(String arg1) {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "setMouseSpeed");
    }

    public void setSpeed(String arg1) {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "setSpeed");
    }


    public void shiftKeyDown() {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "shiftKeyDown");
    }

    public void shiftKeyUp() {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "shiftKeyUp");
    }

    public void submit(String arg1) {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "submit");
    }

    public void type(String arg1, String arg2) {
        HtmlElement element = getHtmlElement(arg1);
        if (element.getClass().equals(HtmlTextInput.class)) {
            ((HtmlTextInput)element).setValueAttribute(arg2);
        } else if (element.getClass().equals(HtmlPasswordInput.class)) {
            ((HtmlPasswordInput)element).setValueAttribute(arg2);
        } else if (element.getClass().equals(HtmlTextArea.class)) {
            ((HtmlTextArea)element).setText(arg2);
        } else {
            throw new RuntimeException(IMPLEMENTATION_REQUIRED + "type for " + element.getClass());
        }

    }

    public void typeKeys(String arg1, String arg2) {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "typeKeys");
    }

    public void uncheck(String arg1) {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "uncheck");
    }

    public void windowFocus() {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "windowFocus");
    }

    public void windowMaximize() {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "windowMaximize");
    }

    public Object getAlert() {
        if (collectedAlerts.isEmpty() ) {
            throw new RuntimeException("ERROR: There were no alerts");
        }
        return  collectedAlerts.remove(collectedAlerts.size()-1);

    }

    public Object getAllButtons() {
        return walkAndFind((HtmlPage)page, "button").replaceFirst(",$","") ;
    }

    private String walkAndFind(DomNode node, String type) {
        Iterator<DomNode> children = node.getChildren().iterator();
        String list = "";
        if (type.equals("button") && (node.getClass().equals(HtmlSubmitInput.class)
                || node.getClass().equals(HtmlButtonInput.class) || node.getClass().equals(HtmlButton.class))) {
            if (node.getAttributes().getNamedItem("id") != null) {
                list = node.getAttributes().getNamedItem("id").getNodeValue() + ",";
            }
            else {
                list =",";
            }
        }
        while (children.hasNext()) {
            list += walkAndFind(children.next(), type);
        }
//        list = list.replaceFirst(",$", "");
        return list;

    }

    public Object getAllFields() {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "getAllFields");
    }

    public Object getAllLinks() {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "getAllLinks");
    }

    public Object getAllWindowIds() {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "getAllWindowIds");
    }

    public Object getAllWindowNames() {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "getAllWindowNames");
    }

    public Object getAllWindowTitles() {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "getAllWindowTitles");
    }

    public Object getBodyText() {
        if (page.getClass().equals(UnexpectedPage.class)) {
             return page.getWebResponse().getContentAsString();
        }  else if (page.getClass().equals(HtmlPage.class)) {
            return ((HtmlPage)page).getBody().asText().replaceAll("\\n", " ").replaceAll("\\t", " ").replaceAll("  *", " ").trim();
        }
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "getBodyText for " + page.getClass());
    }

    public Object getConfirmation() {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "getConfirmation");
    }

    public Object getCookie() {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "getCookie");
    }

    public Object getHtmlSource() {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "getHtmlSource");
    }

    public Object getLocation() {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "getLocation");
    }

    public Object getMouseSpeed() {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "getMouseSpeed");
    }

    public Object getPrompt() {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "getPrompt");
    }

    public boolean isAlertPresent() {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "isAlertPresent");
    }

    public Object getTitle() {
        return ((HtmlPage) page).getTitleText();
    }

    public boolean isConfirmationPresent() {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "isConfirmationPresent");
    }

    public boolean isPromptPresent() {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "isPromptPresent");
    }

    private String getAttribute(HtmlElement element, String attribute) {
        String result = element.getAttribute(attribute);
        if (result.length() > 0) {
            return result;
        } else {
            throw new HtmlUnitException("ERROR: Could not find element attribute: "
                    + element + attribute);

        }

    }

    public Object getAttribute(String arg1) {
        Matcher m = Pattern.compile("(.*)@([^@]*)$").matcher(arg1);
        if (!m.matches()) {
            throw new HtmlUnitException("ERROR: Attribute " + arg1 + " not found");
        }
        HtmlElement element = getHtmlElement(m.group(1));
        try {
            return getAttribute(element, m.group(2));
        }
        catch (HtmlUnitException e) {
            throw new HtmlUnitException("ERROR: Could not find element attribute: "
                    + arg1);

        }
    }

    public Object getAttributeFromAllWindows(String arg1) {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "getAttributeFromAllWindows");
    }

    public Object getCursorPosition(String arg1) {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "getCursorPosition");
    }

    public Object getElementHeight(String arg1) {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "getElementHeight");
    }

    public Object getElementIndex(String arg1) {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "getElementIndex");
    }

    public Object getElementPositionLeft(String arg1) {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "getElementPositionLeft");
    }

    public Object getElementPositionTop(String arg1) {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "getElementPositionTop");
    }

    public Object getElementWidth(String arg1) {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "getElementWidth");
    }

    public Object getExpression(String arg1) {
        Matcher m = Pattern.compile("javascript\\{(.*)\\}").matcher(arg1);
        if (!m.matches()) {
            throw new RuntimeException(IMPLEMENTATION_REQUIRED + "Can't handle getExpression(\""+arg1+"\")");
        }
        return ((HtmlPage) page).executeJavaScript(m.group(1)).getJavaScriptResult();
    }

    public Object getSelectedId(String arg1) {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "getSelectedId");
    }

    public Object getSelectedIds(String arg1) {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "getSelectedIds");
    }

    public Object getSelectedIndex(String arg1) {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "getSelectedIndex");
    }

    public Object getSelectedIndexes(String arg1) {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "getSelectedIndexes");
    }

    public Object getSelectedLabel(String arg1) {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "getSelectedLabel");
    }

    public Object getSelectedLabels(String arg1) {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "getSelectedLabels");
    }

    public Object getSelectedValue(String arg1) {
        HtmlElement element = getHtmlElement(arg1);
        List<HtmlOption> options = ((HtmlSelect) element).getSelectedOptions();
        if (options.size() == 1) {
            return options.get(0).getAttribute("value");
        } else if (options.size() == 0) {
            throw new HtmlUnitException("ERROR: No option selected");
        } else {
            throw new HtmlUnitException("ERROR: More than one selected option!");
        }
    }

    public Object getSelectedValues(String arg1) {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "getSelectedValues");
    }

    public Object getSelectOptions(String arg1) {
                  HtmlElement element = getHtmlElement(arg1);
                  if (element.getClass().equals(HtmlSelect.class)) {
                      Iterator it =  getHtmlElement(arg1).getAllHtmlChildElements().iterator();
                      String str="";
                      while (it.hasNext()) {
                          str += ((HtmlElement) it.next()).getTextContent() + ",";
                      }
                      return str.replaceFirst(",$", "");
                  }
                      throw new RuntimeException(IMPLEMENTATION_REQUIRED + "getSelectOptions for " +
                                               element.getClass());

              }
    public Object getTable(String arg1) {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "getTable");
    }

    public String getText(String arg1) {
        HtmlElement element = getHtmlElement(arg1);
        Iterator<DomNode> it = element.getChildren().iterator();
        String str="";
        while (it.hasNext()) {
            DomNode node=it.next();
            if (node.getClass().equals(HtmlBreak.class)) {
                str += "\n";
            } else {
                str += node.getTextContent() + " ";
            }
        }
        return str.replaceAll(nbsp," ").replaceAll("[    \\t]+", " ").trim();


    }

    public Object getValue(String arg1) {
        HtmlElement element = getHtmlElement(arg1);
        if (element.getClass().equals(HtmlRadioButtonInput.class)) {
            HtmlRadioButtonInput button = (HtmlRadioButtonInput) element;
            if (button.getCheckedAttribute().length() == 0) {
                return "off";
            } else {
                return "on";
            }

        }
        if (element.getClass().equals(HtmlCheckBoxInput.class)) {
            HtmlCheckBoxInput button = (HtmlCheckBoxInput) element;
            if (button.getCheckedAttribute().length() == 0) {
                return "off";
            } else {
                return "on";
            }

        }
        try {
            return getAttribute(element, "value");
        }
        catch (HtmlUnitException e) {
            return "";
        }
    }

    public Object getXpathCount(String arg1) {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "getXpathCount");
    }

    public boolean isChecked(String arg1) {
        HtmlElement element = getHtmlElement(arg1);
        if (element.getClass().equals(HtmlCheckBoxInput.class)) {
            HtmlCheckBoxInput checkbox = (HtmlCheckBoxInput) element;
            return checkbox.isChecked();
        } else if (element.getClass().equals(HtmlRadioButtonInput.class)) {
            HtmlRadioButtonInput checkbox = (HtmlRadioButtonInput) element;
            return checkbox.isChecked();
        } 

        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "isChecked for " + element.getClass());
    }

    public boolean isEditable(String arg1) {
        
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "isEditable");
    }

    public boolean isElementPresent(String arg1) {
        try {
            getHtmlElement(arg1);
            return true;
        } catch (HtmlUnitException e) {
            return false;
        }
    }

    public boolean isSomethingSelected(String arg1) {
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "isSomethingSelected");
    }

    public boolean isTextPresent(String arg1) {
        try {
            String body = ((HtmlPage) page).getBody().asText().replaceAll("\\n\\n*", " ").
                    replaceAll("\\t\\t*", " ").replaceAll("\\r\\r*", " ").replaceAll("  *", " ").trim();
            return body.contains(arg1.replaceAll("\\\n", " "));
        }
        catch (ClassCastException cce) {
            // pass through
        }
        try {
            InputStream stream = ((UnexpectedPage) page).getInputStream();
            return slurp(stream).contains(arg1);
        } catch (IOException e) {
            // pass thru
        }
        throw new RuntimeException(IMPLEMENTATION_REQUIRED + "Can't handle isTextPresent " + arg1);
    }

    private static String slurp(InputStream in) throws IOException {
        StringBuffer out = new StringBuffer();
        byte[] b = new byte[4096];
        for (int n; (n = in.read(b)) != -1;) {
            out.append(new String(b, 0, n));
        }
        return out.toString();
    }
    public void setJavaScriptEnabled(boolean bool) {
        webClient.setJavaScriptEnabled(bool);
    }


}
