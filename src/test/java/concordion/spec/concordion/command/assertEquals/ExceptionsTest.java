package concordion.spec.concordion.command.assertEquals;

import org.selendion.integration.concordion.SelendionTestCase;

import concordion.test.concordion.TestRig;

public class ExceptionsTest extends SelendionTestCase {
    
    public Object countsFromExecutingSnippetWithSimulatedEvaluationResult(String snippet, String simulatedResult) {
        TestRig harness = new TestRig();
        if (simulatedResult.equals("(An exception)")) {
            harness.withStubbedEvaluationResult(new RuntimeException("simulated exception"));
        } else {
            harness.withStubbedEvaluationResult(simulatedResult);
        }
        return harness.processFragment(snippet);
    }
}
