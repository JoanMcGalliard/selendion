package org.selendion.internal.util;

import org.selendion.integration.concordion.SelendionTestCase;

public class HtmlUnitCommandsTest extends SelendionTestCase {
    private SeleniumIdeReader seleniumIdeReader;
    private boolean exception=false;
    private SeleniumIdeReader.CommandResult result;

    public void setUpHtmlUnit(String baseUrl, String page)  {
        seleniumIdeReader= new SeleniumIdeReader();
        seleniumIdeReader.start(baseUrl );
        seleniumIdeReader.execute("open", page, "");
    }
    public void setUpSelenium(String baseUrl, String page)  {
        seleniumIdeReader= new SeleniumIdeReader();
        seleniumIdeReader.start( "localhost", 5555, "*firefox", baseUrl);
        seleniumIdeReader.execute("open", page, "");
    }
    public void runSeleniumIde(String command, String arg1, String arg2)  {
        exception=false;
        try {
        result = seleniumIdeReader.execute(command, arg1,arg2); }
        catch (Throwable e) {
            exception=true;
        }
    }
    public boolean getException() {
        return exception;
    }
    public String getMessage() {
        if (exception) {
            return "";
        } else {
            return result.getMessage();
        }

    }
    public boolean getSuccess() {
        if (exception) {
            return false;
        } else {
            return result.getSuccess();
        }

    }
    public void action(String action) {
        if (action.equals("wait then go back")) {
            seleniumIdeReader.execute("waitForPageToLoad", "", "");
             seleniumIdeReader.execute("goBack", "", "");
             seleniumIdeReader.execute("waitForPageToLoad", "", "");
        }
    }
}
