package concordion.spec.concordion.command.assertTrue;

import org.selendion.integration.concordion.SelendionTestCase;

import concordion.test.concordion.TestRig;

public class AssertTrueTest extends SelendionTestCase {
    
    public String successOrFailure(String fragment, String evaluationResult) {
        return new TestRig()
            .withStubbedEvaluationResult(new Boolean(evaluationResult))
            .processFragment(fragment)
            .successOrFailureInWords();
    }
}
