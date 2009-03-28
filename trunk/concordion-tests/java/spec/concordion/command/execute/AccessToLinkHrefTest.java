package spec.concordion.command.execute;

import org.junit.runner.RunWith;
import org.selendion.integration.concordion.SelendionRunner;

import test.concordion.ProcessingResult;
import test.concordion.TestRig;

@RunWith(SelendionRunner.class)
public class AccessToLinkHrefTest  {

    public boolean fragmentSucceeds(String fragment) {
        ProcessingResult result = new TestRig()
            .withFixture(this)
            .processFragment(fragment);
        
        return result.isSuccess() && result.getSuccessCount() > 0;
    }
    
    public String myMethod(String in) {
        return in;
    }
}
