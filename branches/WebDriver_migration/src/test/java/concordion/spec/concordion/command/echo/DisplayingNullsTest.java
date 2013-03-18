package concordion.spec.concordion.command.echo;

import org.selendion.integration.concordion.SelendionTestCase;

import concordion.test.concordion.TestRig;


public class DisplayingNullsTest extends SelendionTestCase {

    public String render(String fragment) throws Exception {
        return new TestRig()
            .withStubbedEvaluationResult(null)
            .processFragment(fragment)
            .getOutputFragmentXML()
            .replaceFirst(" concordion:echo=\"username\">", ">");
    }
    
}    