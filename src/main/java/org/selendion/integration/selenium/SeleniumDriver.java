/*
        Copyright Joan McGalliard, 2008
*/

package org.selendion.integration.selenium;

import com.thoughtworks.selenium.DefaultSelenium;

public class SeleniumDriver extends DefaultSelenium {





    private String timeout = "600000";

    public void setTimeout(String timeout) {
        super.setTimeout(timeout);
        this.timeout=timeout;
    }
    public String getTimeout() {
        return timeout;
    }

    public SeleniumDriver(String string, int i, String string2,
            String url) {
        super(string, i, string2, url);
    }

    public void clickAndWait(String locator) {
        click(locator);
        waitForPageToLoad(timeout);
    }


    public void waitForPageToLoad() {
        waitForPageToLoad(timeout);
    }

    public void pause(int milliseconds) throws InterruptedException {
            Thread.sleep(milliseconds);
    }




}
