package spec.concordion.command.assertEquals.nonString;

import org.selendion.integration.concordion.SelendionTestCase;

import test.concordion.ProcessingResult;
import test.concordion.TestRig;

public class VoidResultTest extends SelendionTestCase {

    public String process(String snippet) {
        ProcessingResult r = new TestRig()
            .withFixture(this)
            .processFragment(snippet);
        
        if (r.getExceptionCount() != 0) {
            return "exception";
        }
        
        return r.successOrFailureInWords();
    }

    public void myVoidMethod() {
    }

}
