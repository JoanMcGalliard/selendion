package concordion.spec.concordion.command.set;

import org.selendion.integration.concordion.SelendionTestCase;

import concordion.test.concordion.TestRig;

public class SetTest extends SelendionTestCase {

    private String param;

    public void process(String fragment) {
        new TestRig()
            .withFixture(this)
            .processFragment(fragment);
    }
    
    public String getParameterPassedIn() {
        return param;
    }
    
    public void setUpUser(String fullName) {
        this.param = fullName;
    }
}
