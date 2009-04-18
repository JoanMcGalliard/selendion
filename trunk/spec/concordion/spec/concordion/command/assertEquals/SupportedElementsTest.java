package concordion.spec.concordion.command.assertEquals;

import org.selendion.integration.concordion.SelendionTestCase;

import concordion.test.concordion.TestRig;

public class SupportedElementsTest extends SelendionTestCase {
    
    public String process(String snippet) throws Exception {
        long successCount = new TestRig()
            .withStubbedEvaluationResult("Fred")
            .processFragment(snippet)
            .getSuccessCount();
        
        return successCount == 1 ? snippet : "Did not work";
    }

}
