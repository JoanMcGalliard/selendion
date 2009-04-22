/*
	Copyright Joan McGalliard, 2008-9
*/

package org.selendion.internal.util;

import org.selendion.internal.SelendionEvaluator;
import org.selendion.integration.concordion.SelendionTestCase;

import java.util.ArrayList;


public class SeleniumCommandsTest extends SelendionTestCase {

    public void setUp(String browser, String baseUrl, String page)  {
        if (browser.equals("SELENIUM")) {
        seleniumIdeReader= new SeleniumIdeReader();
        seleniumIdeReader.start( "localhost", 5555, System.getProperty("selendion.browser") , baseUrl);
        seleniumIdeReader.execute("open", page, "");
        } else         if (browser.equals("HTMLUNIT")) {
            seleniumIdeReader= new SeleniumIdeReader();
            seleniumIdeReader.start(baseUrl );
            seleniumIdeReader.execute("open", page, "");
            seleniumIdeReader.getBrowser().passVariablesIn(new SelendionEvaluator(this));
        } else {
            throw new RuntimeException ("Unknown browser type " + browser);

        }

    }
    public ArrayList getList() {
        return  new java.util.ArrayList();
    }
    
    public void stop() {
        seleniumIdeReader.stop();
    }


    protected SeleniumIdeReader seleniumIdeReader;
    private boolean exception=false;
    private SeleniumIdeReader.CommandResult result;

    public void runSeleniumIde(String command, String arg1, String arg2)  {
        exception=false;
        try {
        result = seleniumIdeReader.execute(command, arg1.replaceAll("\n *", "\\\n").trim(),
                arg2.replaceAll("\n *", "\\\n").trim()); }
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
        return !exception && result.getSuccess();

    }

}