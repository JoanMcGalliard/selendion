package spec.concordion;


import org.selendion.integration.concordion.SelendionTestCase;

import test.concordion.TestRig;

public class ExampleTest extends SelendionTestCase {

    public String process(String html) {
        return new TestRig()
            .withFixture(this)
            .process(html)
            .successOrFailureInWords()
            .toLowerCase();
    }
    
    public String getGreeting() {
        return "Hello World!";
    }
}
