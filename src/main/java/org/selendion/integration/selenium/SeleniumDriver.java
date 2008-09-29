/*
        Copyright Joan McGalliard, 2008
*/

package org.selendion.integration.selenium;

import com.thoughtworks.selenium.DefaultSelenium;
import org.concordion.api.Evaluator;

public class SeleniumDriver extends DefaultSelenium {





    private String timeout = "600000";

    public SeleniumDriver(String string, int i, String string2,
            String url) {
        super(string, i, string2, url);
    }

    public void clickAndWait(String locator) {
        click(locator);
        waitForPageToLoad(timeout);
    }

    public void waitForElementPresent(String locator) throws Exception {
        long start = System.currentTimeMillis();
        while (!isElementPresent(locator)) {
            if (System.currentTimeMillis() - start > Integer.parseInt(timeout)) {
                throw new Exception("Timeout while waiting for " + locator
                        + " to appear.");
            }
            pause(200);
        }

    }

    public void waitForPageToLoad() {
        waitForPageToLoad(timeout);
    }

    public void pause(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }




}
