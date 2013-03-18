/*
        Copyright Joan McGalliard, 2008-2013
*/

package org.selendion.integration.selenium;

import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.selendion.integration.BrowserDriver;
import org.concordion.api.Evaluator;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SeleniumDriver implements BrowserDriver {

    private String timeout = "30000";
    private boolean started = false;
    private final WebDriver driver;
//    private String baseUrl;
    private final String IMPLEMENTATION_REQUIRED = "Not yet implemented (please report to selendion.org): ";
    private final WebDriverBackedSelenium selenium;

    public SeleniumDriver(String seleniumHost, int seleniumPort, String browserName,
                          String baseUrl) {
        System.out.println("SeleniumDriver constructor: seleniumHost " + seleniumHost + "; seleniumPort " + seleniumPort + "; browserName " + browserName + "; baseUrl " + baseUrl);
        driver = new FirefoxDriver();
        WebDriver driver = new FirefoxDriver();
         selenium = new WebDriverBackedSelenium(driver, baseUrl);

    }
    public void start() {
        if (!isStarted()) {
            System.out.println("INFO: Starting Selenium.");
        }

    }
     public void stop(){
         started=false;
         driver.quit();
     }

    public String getEval(String s) {
        return selenium.getEval(s);
    }

    public void waitForCondition(String arg1, String arg2) {
        selenium.waitForCondition(arg1,arg2);
    }

    public void waitForFrameToLoad(String arg1, String arg2) {
        selenium.waitForFrameToLoad(arg1,arg2);
    }

    public boolean isStarted() {
        return started;
    }


    public void setTimeout(String timeout) {
        Integer.parseInt(timeout);
        selenium.setTimeout(timeout);
        this.timeout=timeout;
    }

    public void shiftKeyDown() {
        selenium.shiftKeyDown();
    }

    public void shiftKeyUp() {
        selenium.shiftKeyUp();
    }

    public void submit(String arg1) {
        selenium.submit(arg1);
    }

    public void type(String arg1, String arg2) {
        selenium.type(arg1, arg2);
    }

    public void typeKeys(String arg1, String arg2) {
        selenium.typeKeys(arg1, arg2);
    }

    public void uncheck(String arg1) {
        selenium.uncheck(arg1);
    }

    public void windowFocus() {
        selenium.windowFocus();
    }

    public void windowMaximize() {
        selenium.windowMaximize();
    }

    public Object getAlert() {
        return selenium.getAlert();
    }

    public Object getAllButtons() {
        return selenium.getAllButtons();
    }

    public Object getAllFields() {
        return selenium.getAllFields();
    }

    public Object getAllLinks() {
        return selenium.getAllLinks();
    }

    public Object getAllWindowIds() {
        return selenium.getAllWindowIds();
    }

    public Object getAllWindowNames() {
        return selenium.getAllWindowNames();
    }

    public Object getAllWindowTitles() {
        return selenium.getAllWindowTitles();
    }

    public Object getBodyText() {
        return selenium.getBodyText();
    }

    public Object getConfirmation() {
        return selenium.getConfirmation();
    }

    public Object getCookie() {
        return selenium.getCookie();
    }

    public Object getHtmlSource() {
        return selenium.getHtmlSource();
    }

    public Object getLocation() {
        return selenium.getLocation();
    }

    public Object getMouseSpeed() {
        return selenium.getMouseSpeed();
    }

    public Object getPrompt() {
        return selenium.getPrompt();
    }

    public boolean isAlertPresent() {
        return selenium.isAlertPresent();
    }

    public Object getTitle() {
        return selenium.getTitle();
    }

    public boolean isConfirmationPresent() {
        return selenium.isConfirmationPresent();
    }

    public boolean isPromptPresent() {
        return selenium.isPromptPresent();
    }

    public Object getAttribute(String arg1) {
        return selenium.getAttribute(arg1);
    }

    public Object getAttributeFromAllWindows(String arg1) {
        return selenium.getAttributeFromAllWindows(arg1);
    }

    public Object getCursorPosition(String arg1) {
        return selenium.getCursorPosition(arg1);
    }

    public Object getElementHeight(String arg1) {
        return selenium.getElementHeight(arg1);
    }

    public Object getElementIndex(String arg1) {
        return selenium.getElementIndex(arg1);
    }

    public Object getElementPositionLeft(String arg1) {
        return selenium.getElementPositionLeft(arg1);
    }

    public Object getElementPositionTop(String arg1) {
        return selenium.getElementPositionTop(arg1);
    }

    public Object getElementWidth(String arg1) {
        return selenium.getElementWidth(arg1);
    }

    public Object getExpression(String arg1) {
        return selenium.getExpression(arg1);
    }

    public Object getSelectedId(String arg1) {
        return selenium.getSelectedId(arg1);
    }

    public Object getSelectedIds(String arg1) {
        return selenium.getSelectedIds(arg1);
    }

    public Object getSelectedIndex(String arg1) {
        return selenium.getSelectedIndex(arg1);
    }

    public Object getSelectedIndexes(String arg1) {
        return selenium.getSelectedIndexes(arg1);
    }

    public Object getSelectedLabel(String arg1) {
        return selenium.getSelectedLabel(arg1);
    }

    public Object getSelectedLabels(String arg1) {
        return selenium.getSelectedLabels(arg1);
    }

    public Object getSelectedValue(String arg1) {
        return selenium.getSelectedValue(arg1);
    }

    public Object getSelectedValues(String arg1) {
        return selenium.getSelectedValues(arg1);
    }

    public Object getSelectOptions(String arg1) {
        return selenium.getSelectOptions(arg1);
    }

    public Object getTable(String arg1) {
        return selenium.getTable(arg1);
    }

    public Object getText(String arg1) {
        return selenium.getText(arg1);
    }

    public Object getValue(String arg1) {
        return selenium.getValue(arg1);
    }

    public Object getXpathCount(String arg1) {
        return selenium.getXpathCount(arg1);
    }

    public boolean isChecked(String arg1) {
        return selenium.isChecked(arg1);
    }

    public boolean isEditable(String arg1) {
        return selenium.isEditable(arg1);
    }

    public boolean isElementPresent(String arg1) {
        return selenium.isElementPresent(arg1);
    }

    public boolean isSomethingSelected(String arg1) {
        return selenium.isSomethingSelected(arg1);
    }

    public boolean isTextPresent(String arg1) {
        return selenium.isTextPresent(arg1);
    }


    public String getTimeout() {
        return timeout;
    }

    public void waitForPopUp(String arg1, String arg2) {
        selenium.waitForPopUp(arg1, arg2);
    }


    public void clickAndWait(String locator) {
        click(locator);
        selenium.waitForPageToLoad(timeout);
    }


    public void waitForPageToLoad() {
        selenium.waitForPageToLoad(timeout);
    }

    public boolean isVisible(String arg1) {
        return selenium.isVisible(arg1);
    }

    public void pause(int milliseconds) throws InterruptedException {
            Thread.sleep(milliseconds);
    }

    public void pauseInWaitFor(int i) throws InterruptedException {
        pause(i) ;
    }

    public void addSelection(String arg1, String arg2) {
        selenium.addSelection(arg1, arg2);
    }

    public void allowNativeXpath(String arg1) {
        selenium.allowNativeXpath(arg1);
    }

    public void altKeyDown() {
        selenium.altKeyDown();
    }

    public void altKeyUp() {
        selenium.altKeyUp();
    }

    public void answerOnNextPrompt(String arg1) {
        selenium.answerOnNextPrompt(arg1);
    }

    public void assignId(String arg1, String arg2) {
        selenium.assignId(arg1, arg2);
    }

    public void check(String arg1) {
        selenium.check(arg1);
    }

    public void chooseCancelOnNextConfirmation() {
        selenium.chooseCancelOnNextConfirmation();
    }

    public void chooseOkOnNextConfirmation() {
        selenium.chooseOkOnNextConfirmation();
    }

    public void click(String arg1) {
        selenium.click(arg1);
    }

    public void clickAt(String arg1, String arg2) {
        selenium.clickAt(arg1,arg2);
    }

    public void close() {
        selenium.close();
    }

    public void controlKeyDown() {
        selenium.controlKeyDown();
    }

    public void controlKeyUp() {
        selenium.controlKeyUp();
    }


    public void createCookie(String arg1, String arg2) {
        selenium.createCookie(arg1, arg2);
    }

    public void deleteAllVisibleCookies() {
        selenium.deleteAllVisibleCookies();
    }

    public void deleteCookie(String arg1, String arg2) {
        selenium.deleteCookie(arg1, arg2);
    }

    public void doubleClick(String arg1) {
        selenium.doubleClick(arg1);
    }

    public void doubleClickAt(String arg1, String arg2) {
        selenium.doubleClickAt(arg1, arg2);
    }

    public void dragAndDrop(String arg1, String arg2) {
        selenium.dragAndDrop(arg1, arg2);
    }

    public void dragAndDropToObject(String arg1, String arg2) {
        selenium.dragAndDropToObject(arg1, arg2);
    }

    public void dragdrop(String arg1, String arg2) {
        selenium.dragdrop(arg1, arg2);
    }

    public String echo(String arg1) {
        return replaceVariables(arg1) ;
    }

    public void fireEvent(String arg1, String arg2) {
        selenium.fireEvent(arg1, arg2);
    }

    public void focus(String arg1) {
        selenium.focus(arg1);
    }

    public String getSpeed() {
        return selenium.getSpeed();
    }

    public void goBack() {
        selenium.goBack();
    }

    public void highlight(String arg1) {
        selenium.highlight(arg1);
    }

    public void keyDown(String arg1, String arg2) {
        selenium.keyDown(arg1, arg2);
    }

    public void keyPress(String arg1, String arg2) {
        selenium.keyPress(arg1, arg2);
    }

    public void keyUp(String arg1, String arg2) {
        selenium.keyUp(arg1, arg2);
    }

    public void metaKeyDown() {
        selenium.metaKeyDown();
    }

    public void metaKeyUp() {
        selenium.metaKeyUp();
    }

    public void mouseDown(String arg1) {
        selenium.mouseDown(arg1);
    }

    public void mouseDownAt(String arg1, String arg2) {
        selenium.mouseDownAt(arg1, arg2);
    }

    public void mouseMove(String arg1) {
        selenium.mouseMove(arg1);
    }

    public void mouseMoveAt(String arg1, String arg2) {
        selenium.mouseMoveAt(arg1, arg2);
    }

    public void mouseOut(String arg1) {
        selenium.mouseOut(arg1);
    }

    public void mouseOver(String arg1) {
        selenium.mouseOver(arg1);
    }

    public void mouseUp(String arg1) {
        selenium.mouseUp(arg1);
    }

    public void mouseUpAt(String arg1, String arg2) {
        selenium.mouseUpAt(arg1, arg2);
    }

    public void open(String arg1) {
        selenium.open(arg1);
    }

    public void openWindow(String arg1, String arg2) {
        selenium.openWindow(arg1, arg2);
    }

    public void refresh() {
        selenium.refresh();
    }

    public void removeAllSelections(String arg1) {
        selenium.removeAllSelections(arg1);
    }

    public void removeSelection(String arg1, String arg2) {
        selenium.removeSelection(arg1, arg2);
    }

    public void runScript(String arg1) {
        selenium.runScript(arg1);
    }

    public void select(String arg1, String arg2) {
        selenium.select(arg1, arg2);
    }

    public void selectFrame(String arg1) {
        selenium.selectFrame(arg1);
    }

    public void selectWindow(String arg1) {
        selenium.selectWindow(arg1);
    }

    public void setBrowserLogLevel(String arg1) {
        selenium.setBrowserLogLevel(arg1);
    }

    public void setCursorPosition(String arg1, String arg2) {
        selenium.setCursorPosition(arg1, arg2);
    }

    public void setMouseSpeed(String arg1) {
        selenium.setMouseSpeed(arg1);
    }

    public void setSpeed(String arg1) {
        selenium.setSpeed(arg1);
    }



    private static final String VARIABLE_PATTERN = "[a-z][A-Za-z0-9_]*";

    public void passVariablesIn(Evaluator evaluator) {
           Set keys = evaluator.getKeys();
           String array = "";
           for (Object key : keys) {
               if (!key.getClass().equals(String.class)) {
                   throw new RuntimeException("Unexpected key " + key);
               }
               String keyString = (String) key;
               if (keyString.matches(VARIABLE_PATTERN)) {
                   Object value = evaluator.getVariable("#" + keyString);

                   String valueString;
                   if (value == null) {
                       valueString = "";
                   } else if (!value.getClass().equals(String.class) && !value.getClass().equals(Character.class)&& !value.getClass().equals(Integer.class)) {
                       continue;
                   } else {
                       valueString = value.toString();
                   }
                   array = String.format("%s %s: '%s', ", array, keyString, valueString.replaceFirst("\\\\|$", "").replaceAll("'", "\\\\'").replaceAll("\\n", " ").replaceAll("\\r", " "));
               }
           }
           array = array.replaceFirst("[, ]*$", "");
        if (array.length() > 0) {
            String evalString = "storedVars = {" + array + "}";
            try {
                getEval(evalString);
            } catch (Throwable e) {
                throw new RuntimeException("Failed trying to eval: " + evalString, e);
            }
        }
    }

       public void passVariablesOut(Evaluator evaluator) {
           String SEPARATOR = "###SEPARATOR###";
           String[] storedVars = getEval("var arr = '';for (var name in storedVars) {arr=arr+'#'+name+' '+storedVars[name]+'"+SEPARATOR+"';};arr").split(SEPARATOR);
           for (String var : storedVars) {
               String[] nvp = var.trim().split(" ", 2);
               if (nvp.length ==1 ) {
                   nvp = new String[] {nvp[0], ""};
               }
               if (nvp[0].matches("#" + VARIABLE_PATTERN)) {
                   evaluator.setVariable(nvp[0], nvp[1]);
               }
           }
       }

    public void store(String name, Object value) {
        Class clazz = value.getClass();
        if (clazz == String.class) {
            getEval(String.format("storedVars['%s']='%s'", name, ((String) value).replaceAll("\\n", "\\\\n").replaceAll("'", "\\\\'")));
        } else if (clazz == Boolean.class) {
            getEval(String.format("storedVars['%s']=%s", name, (Boolean) value ? "true" : "false"));
        } else if (clazz == String[].class) {
            String valueStr = "";
            for (String str : (String[]) value) {
                valueStr = valueStr + "'" + str + "', ";
            }
            valueStr = valueStr.replaceFirst("[ ,]*$", "");
            getEval(String.format("storedVars['%s']=%s", name, "[" + valueStr + "]"));
        } else if (clazz.getSuperclass() == Number.class ) {
            getEval(String.format("storedVars['%s']=%s", name, value.toString()));
            getEval(String.format("storedVars['%s']=%s", name, value.toString()));
        }

    }
    public String replaceVariables(String string) {
        Pattern listPattern = Pattern.compile("\\[.*\\]");

        Matcher m = variablePattern.matcher(string);
        while (m.matches()) {
            try {
                String val = getEval(String.format("storedVars['%s']", m.group(2)));
                if (listPattern.matcher(val).matches()) {
                    val=val.replaceAll("^\\[", "").replaceAll("\\]$","").replaceAll(", ", ",");
                }

            string = m.group(1) + val + m.group(3);
            m = variablePattern.matcher(string);
            }
            catch (Exception e )   {
                e.printStackTrace();

            }
        }
        return string.replaceAll("\\\\n", "\n");
    }


    private final Pattern variablePattern = Pattern.compile("(.*)\\$\\{([^}]*)\\}(.*)");
    public static String getMethodName()
    {
      final StackTraceElement[] ste = Thread.currentThread().getStackTrace();

      return ste[ste.length - 1].getMethodName();
    }
}
