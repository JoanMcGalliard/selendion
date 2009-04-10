package spec.concordion.results.assertEquals.failure;

import org.selendion.integration.concordion.SelendionTestCase;

import test.concordion.TestRig;

public class FailureTest extends SelendionTestCase {
    
    public String acronym;

    public String renderAsFailure(String fragment, String acronym) throws Exception {
        this.acronym = acronym;
        return new TestRig()
            .withFixture(this)
            .processFragment(fragment)
            .getOutputFragmentXML();
    }
}
