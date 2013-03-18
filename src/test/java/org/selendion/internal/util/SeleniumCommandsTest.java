/*
	Copyright Joan McGalliard, 2008-9
*/

package org.selendion.internal.util;

import org.concordion.api.Evaluator;
import org.selendion.internal.SelendionEvaluator;
import org.selendion.integration.concordion.SelendionTestCase;

public class SeleniumCommandsTest extends SelendionTestCase {

    private Evaluator evaluator;

    public void setUp(String browser, String baseUrl, String page) {
        evaluator=new SelendionEvaluator(this);
        if (browser.equals("SELENIUM")) {
            seleniumIdeReader = new SeleniumIdeReader();
            seleniumIdeReader.start("localhost", 5555, System.getProperty("selendion.browser"), baseUrl);
            seleniumIdeReader.execute("open", page, "",evaluator);
            seleniumIdeReader.getBrowser().passVariablesIn(evaluator);
        } else if (browser.equals("HTMLUNIT")) {
            seleniumIdeReader = new SeleniumIdeReader();
            seleniumIdeReader.start(baseUrl);
            seleniumIdeReader.getBrowser().passVariablesIn(evaluator);
            seleniumIdeReader.execute("open", page, "", evaluator);
        } else {
            throw new RuntimeException("Unknown browser type " + browser);

        }

    }

    public void stop() {
        seleniumIdeReader.stop();
    }


    protected SeleniumIdeReader seleniumIdeReader;
    private boolean exception=false;
    private SeleniumIdeReader.CommandResult result;

    public void runSeleniumIde(String command, String arg1, String arg2)  {
        exception=false;
        command = seleniumIdeReader.getBrowser().replaceVariables(command);
        arg1 = seleniumIdeReader.getBrowser().replaceVariables(arg1);
        arg2 = seleniumIdeReader.getBrowser().replaceVariables(arg2);

        try {

            result = seleniumIdeReader.execute(command, arg1.trim(),
                    arg2.trim(), evaluator);
        } catch (Throwable e) {
            exception = true;
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
        return !exception && result.getSuccess();

    }

}
