/*
        Copyright Joan McGalliard, 2008
*/

package org.selendion.integration.concordion;

import org.concordion.integration.junit3.ConcordionTestCase;
import org.concordion.api.ResultSummary;
import org.concordion.api.Evaluator;
import org.selendion.internal.SelendionBuilder;
import org.selendion.internal.SelendionEvaluatorFactory;
import org.selendion.Selendion;


public abstract class SelendionTestCase extends ConcordionTestCase {
    public boolean isExpectedToPass() {
        return expectedToPass;
    }

    private boolean expectedToPass = true;

    public void setExpectedToPass(boolean expectedToPass) {
        this.expectedToPass = expectedToPass;
    }
    public void testProcessSpecification() throws Throwable {
        testProcessSpecification(null);
    }
public void testProcessSpecification(Evaluator evaluator) throws Throwable {
        Selendion selendion= new SelendionBuilder().withEvaluatorFactory(new SelendionEvaluatorFactory()).withEvaluator(evaluator).build();
        ResultSummary resultSummary = selendion.process(this);
        resultSummary.print(System.out);
        if (expectedToPass) {
            resultSummary.assertIsSatisfied();
        } else {
            assertTrue("Test is not expected to pass, yet is passing", resultSummary.getExceptionCount() + resultSummary.getFailureCount() > 0);
        }
    }


}
