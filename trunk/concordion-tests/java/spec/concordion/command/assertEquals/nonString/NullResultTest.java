package spec.concordion.command.assertEquals.nonString;

import org.selendion.integration.concordion.SelendionTestCase;

import test.concordion.TestRig;

public class NullResultTest extends SelendionTestCase {
    
    public String outcomeOfPerformingAssertEquals(String snippet, String expectedString, String result) {
        if (result.equals("null")) {
            result = null;
        }
        snippet = snippet.replaceAll("\\(some expectation\\)", expectedString);
        
        return new TestRig()
            .withStubbedEvaluationResult(result)
            .processFragment(snippet)
            .successOrFailureInWords();
    }
}
