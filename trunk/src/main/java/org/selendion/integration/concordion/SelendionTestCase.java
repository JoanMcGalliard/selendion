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
    private boolean expectedToPass = true;
    private Evaluator evaluator=null;

    public void setExpectedToPass(boolean expectedToPass) {
        this.expectedToPass = expectedToPass;
    }
    public Evaluator getEvaluator () {
        return evaluator;
    }

    public void testProcessSpecification() throws Throwable {
        new SelendionBuilder().withEvaluatorFactory(new SelendionEvaluatorFactory()).build();
        Selendion selendion= new SelendionBuilder().withEvaluatorFactory(new SelendionEvaluatorFactory()).build();
        ResultSummary resultSummary = selendion.process(this);
        this.evaluator = selendion.getEvaluator();
        resultSummary.print(System.out);
        if (expectedToPass) {
            resultSummary.assertIsSatisfied();
        } else {
            assertTrue("Test is not expected to pass, yet is passing", resultSummary.getExceptionCount() + resultSummary.getFailureCount() > 0);
        }
    }


}
