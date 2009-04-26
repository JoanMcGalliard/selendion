/*
        Copyright Joan McGalliard, 2008-9
*/

package org.selendion.integration.concordion;

import org.concordion.integration.junit3.ConcordionTestCase;
import org.concordion.api.Evaluator;
import org.concordion.api.Element;
import org.selendion.internal.SelendionBuilder;
import org.selendion.internal.SelendionEvaluatorFactory;
import org.selendion.internal.SelendionResultRecorder;
import org.selendion.Selendion;
import org.selendion.integration.BrowserDriver;

import java.util.ArrayList;


public abstract class SelendionTestCase extends ConcordionTestCase {
    public boolean isExpectedToPass() {
        return expectedToPass;
    }

    private boolean expectedToPass = true;
    private SelendionResultRecorder resultSummary;

    public void lastExecutionResult() {
        if (expectedToPass) {
            resultSummary.assertIsSatisfied(this);
        } else {
            assertTrue("Test is not expected to pass, yet is passing", resultSummary.getExceptionCount() + resultSummary.getFailureCount() > 0);
        }

    }


    public void setExpectedToPass(boolean expectedToPass) {
        this.expectedToPass = expectedToPass;
    }

    public void testProcessSpecification() throws Throwable {
        testProcessSpecification(null,null);
        lastExecutionResult();
    }

    public Element[] testProcessSpecification(Evaluator evaluator, BrowserDriver browser) throws Throwable {
        Selendion selendion = new SelendionBuilder().withEvaluatorFactory(new SelendionEvaluatorFactory())
                .withEvaluator(evaluator).withBrowser(browser).withBaseClass(this.getClass()).build();
        resultSummary = selendion.process(this);
        resultSummary.print(System.out, this);
        return resultSummary.getResultSpecification().getCommandCall().getElement().getChildElements("body")[0].getChildElements();
    }
    public ArrayList newList() {
        return new java.util.ArrayList();
    }



}
