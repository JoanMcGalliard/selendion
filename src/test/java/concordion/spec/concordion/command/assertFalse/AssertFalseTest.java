package concordion.spec.concordion.command.assertFalse;

import org.selendion.integration.concordion.SelendionTestCase;

import concordion.test.concordion.TestRig;

public class AssertFalseTest extends SelendionTestCase {
    
    public String successOrFailure(String fragment, String evaluationResult) {
        return new TestRig()
            .withStubbedEvaluationResult(new Boolean(evaluationResult))
            .processFragment(fragment)
            .successOrFailureInWords();
    }
}
