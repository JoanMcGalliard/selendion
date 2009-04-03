package spec.concordion.command.echo;

import org.selendion.integration.concordion.SelendionTestCase;

import test.concordion.TestRig;


public class DisplayingNullsTest extends SelendionTestCase {

    public String render(String fragment) throws Exception {
        return new TestRig()
            .withStubbedEvaluationResult(null)
            .processFragment(fragment)
            .getOutputFragmentXML()
            .replaceFirst(" concordion:echo=\"username\">", ">");
    }
    
}    