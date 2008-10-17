package spec.concordion.command.run;

import org.selendion.integration.concordion.SelendionTestCase;

import test.concordion.TestRig;

public class RunTest extends SelendionTestCase{
	public String successOrFailure(String fragment, String expectedResult, String evaluationResult) {
		
		System.setProperty("concordion.runner.testrunner", RunTestRunner.class.getName());
		RunTestRunner.result = new Boolean(expectedResult);
        return new TestRig()
            .withStubbedEvaluationResult(evaluationResult)
            .processFragment(fragment)
            .successOrFailureInWords();
    }
}
