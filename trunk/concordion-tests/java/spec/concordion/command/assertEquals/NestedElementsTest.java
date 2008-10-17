package spec.concordion.command.assertEquals;

import org.selendion.integration.concordion.SelendionTestCase;

import test.concordion.TestRig;

public class NestedElementsTest extends SelendionTestCase {
    
    public String matchOrNotMatch(String snippet, String evaluationResult) throws Exception {
        return new TestRig()
            .withStubbedEvaluationResult(evaluationResult)
            .processFragment(snippet)
            .hasFailures() ? "not match" : "match";
    }

}
