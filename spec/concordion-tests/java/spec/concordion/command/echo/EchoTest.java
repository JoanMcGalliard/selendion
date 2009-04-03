package spec.concordion.command.echo;

import org.selendion.integration.concordion.SelendionTestCase;

import test.concordion.TestRig;


public class EchoTest extends SelendionTestCase {

    private String nextResult;

    public void setNextResult(String nextResult) {
        this.nextResult = nextResult;
    }
    
    public String render(String fragment) throws Exception {
        return new TestRig()
            .withStubbedEvaluationResult(nextResult)
            .processFragment(fragment)
            .getOutputFragmentXML();
    }
    
}    