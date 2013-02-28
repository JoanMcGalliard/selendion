/*
	Copyright Joan McGalliard, 2008-2013
*/

package org.selendion.integration.concordion;

import org.concordion.integration.junit4.ConcordionRunner;
import org.concordion.api.ResultSummary;
import org.concordion.internal.ConcordionBuilder;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.selendion.internal.SelendionBuilder;

public class SelendionRunner extends ConcordionRunner {
    public SelendionRunner(Class<?> aClass) throws InitializationError {
        super(aClass);
    }
    protected Statement specExecStatement(final Object fixture) {
        return new Statement() {
            public void evaluate() throws Throwable {
                ResultSummary resultSummary = new SelendionBuilder().build().process(fixture);
                resultSummary.print(System.out, fixture);
                resultSummary.assertIsSatisfied(fixture);
            }
        };
    }

}
