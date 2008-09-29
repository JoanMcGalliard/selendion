/*
        Copyright Joan McGalliard, 2008
*/

package org.selendion.integration.concordion;

import org.concordion.integration.junit3.ConcordionTestCase;
import org.concordion.api.ResultSummary;
import org.selendion.internal.SelendionBuilder;


public abstract class SelendionTestCase extends ConcordionTestCase {
    private boolean isExpectedToPass=true;
    protected void setIsExpectedToPass(boolean isExpectedToPass) {
        this.isExpectedToPass=isExpectedToPass;
    }
    protected boolean isExpectedToPass() {
        return isExpectedToPass;
    }
     public void testProcessSpecification() throws Throwable {
        ResultSummary resultSummary = new SelendionBuilder().build().process(this);
        resultSummary.print(System.out);
        resultSummary.assertIsSatisfied(isExpectedToPass());
    }
  

}
