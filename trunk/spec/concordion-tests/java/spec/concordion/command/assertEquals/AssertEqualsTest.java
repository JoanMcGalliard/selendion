package spec.concordion.command.assertEquals;

import org.selendion.integration.concordion.SelendionTestCase;

import test.concordion.TestRig;

public class AssertEqualsTest extends SelendionTestCase {
    
    public String successOrFailure(String fragment, String evaluationResult) {
        return new TestRig()
            .withStubbedEvaluationResult(evaluationResult)
            .processFragment(fragment)
            .successOrFailureInWords();
    }
}
