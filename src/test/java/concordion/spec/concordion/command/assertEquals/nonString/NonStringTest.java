package concordion.spec.concordion.command.assertEquals.nonString;

import org.selendion.integration.concordion.SelendionTestCase;

import concordion.test.concordion.TestRig;

public class NonStringTest extends SelendionTestCase {
    
    public String outcomeOfPerformingAssertEquals(String fragment, String expectedString, String result, String resultType) {
        
        Object simulatedResult;
        if (resultType.equals("String")) {
            simulatedResult = result;
        } else if (resultType.equals("Integer")) {
            simulatedResult = new Integer(result);
        } else if (resultType.equals("Double")) {
            simulatedResult = new Double(result);
        } else {
            throw new RuntimeException("Unsupported result-type '" + resultType + "'.");
        }
        
        fragment = fragment.replaceAll("\\(some expectation\\)", expectedString);
        
        return new TestRig()
            .withStubbedEvaluationResult(simulatedResult)
            .processFragment(fragment)
            .successOrFailureInWords();
    }
}
