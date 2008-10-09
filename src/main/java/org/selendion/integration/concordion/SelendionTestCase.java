/*
        Copyright Joan McGalliard, 2008
*/

package org.selendion.integration.concordion;

import org.concordion.integration.junit3.ConcordionTestCase;
import org.concordion.api.ResultSummary;
import org.selendion.internal.SelendionBuilder;
import org.openqa.selenium.server.SeleniumServer;


public abstract class SelendionTestCase extends ConcordionTestCase {
    private boolean expectedToPass = true;

    public void setExpectedToPass(boolean expectedToPass) {
        this.expectedToPass = expectedToPass;
    }

     public void testProcessSpecification() throws Throwable {
        ResultSummary resultSummary = new SelendionBuilder().build().process(this);
        resultSummary.print(System.out);
        if (expectedToPass) {
            resultSummary.assertIsSatisfied();
        } else {
            assertTrue("Test is not expected to pass, yet is passing", resultSummary.getExceptionCount() + resultSummary.getFailureCount() > 0);
        }
    }


}
