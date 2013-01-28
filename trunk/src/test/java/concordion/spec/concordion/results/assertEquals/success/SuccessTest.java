package concordion.spec.concordion.results.assertEquals.success;

import org.selendion.integration.concordion.SelendionTestCase;

import concordion.test.concordion.TestRig;

public class SuccessTest extends SelendionTestCase {
    
    public String username = "fred";
    
    public String renderAsSuccess(String fragment) throws Exception {
        return new TestRig()
            .withFixture(this)
            .processFragment(fragment)
            .getOutputFragmentXML();
    }

}
