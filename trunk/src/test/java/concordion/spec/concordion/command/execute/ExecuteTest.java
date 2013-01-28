package concordion.spec.concordion.command.execute;

import org.selendion.integration.concordion.SelendionTestCase;

import concordion.test.concordion.TestRig;

public class ExecuteTest extends SelendionTestCase {

    private boolean myMethodWasCalled = false;
    
    public boolean myMethodWasCalledProcessing(String fragment) {
        new TestRig()
            .withFixture(this)
            .processFragment(fragment);
        return myMethodWasCalled;
    }
    
    public void myMethod() {
        myMethodWasCalled = true;
    }
}
