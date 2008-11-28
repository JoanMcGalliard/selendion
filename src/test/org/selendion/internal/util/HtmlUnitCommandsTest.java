package org.selendion.internal.util;

import org.selendion.integration.concordion.SelendionTestCase;
import org.selendion.internal.SelendionEvaluator;

public class HtmlUnitCommandsTest extends SelendionTestCase {
    protected SeleniumIdeReader seleniumIdeReader;
    private boolean exception=false;
    private SeleniumIdeReader.CommandResult result;

    public void setUp(String baseUrl, String page)  {
        seleniumIdeReader= new SeleniumIdeReader();
        seleniumIdeReader.start(baseUrl );
        seleniumIdeReader.execute("open", page, "");
        seleniumIdeReader.getBrowser().passVariablesIn(new SelendionEvaluator(this));
    }
    public void runSeleniumIde(String command, String arg1, String arg2)  {
        exception=false;
        try {
        result = seleniumIdeReader.execute(command, arg1.replaceAll("\\\n *", "\\\n").trim(),
                arg2.replaceAll("\\\n *", "\\\n").trim()); }
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
    public void action(String action) throws Exception {
        if (action.length() == 0 ) {
            return;
        }
        if (action.equals("wait then go back")) {
            seleniumIdeReader.execute("waitForPageToLoad", "", "");
             seleniumIdeReader.execute("goBack", "", "");
             seleniumIdeReader.execute("waitForPageToLoad", "", "");
        } else {
            throw new Exception ("Unknown action: " + action);
        }
    }
}
