package spec.concordion.results.assertTrue.success;

import org.selendion.integration.concordion.SelendionTestCase;

import test.concordion.TestRig;

public class SuccessTest extends SelendionTestCase {
    
    public boolean isPalindrome(String s) {
        return new StringBuilder(s).reverse().toString().equals(s); 
    }

    public String render(String fragment) throws Exception {
        return new TestRig()
            .withFixture(this)
            .processFragment(fragment)
            .getOutputFragmentXML();
    }
}
