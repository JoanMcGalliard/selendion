package concordion.spec.concordion.results.assertTrue.failure;

import org.selendion.integration.concordion.SelendionTestCase;

import concordion.test.concordion.TestRig;

public class FailureTest extends SelendionTestCase {
    
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
