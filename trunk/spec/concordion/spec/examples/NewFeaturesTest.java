package concordion.spec.examples;

import org.selendion.integration.concordion.SelendionTestCase;

public class NewFeaturesTest extends SelendionTestCase {
    public String greetingFor(String firstName) {
        return String.format("Hello %s!", firstName);
    }

}