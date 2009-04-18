package concordion.spec.concordion.command.echo;

import org.selendion.integration.concordion.SelendionTestCase;

import concordion.test.concordion.TestRig;


public class EscapingHtmlCharactersTest extends SelendionTestCase {

    public String render(String fragment, String evalResult) throws Exception {
        return new TestRig()
            .withStubbedEvaluationResult(evalResult)
            .processFragment(fragment)
            .getOutputFragmentXML()
            .replaceFirst(" concordion:echo=\"username\">", ">");
    }
    
}    