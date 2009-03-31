package examples;

import org.selendion.integration.concordion.SelendionTestCase;

public class DemoTest extends SelendionTestCase {

    public String greetingFor(String firstName) {
        return String.format("Hello %s!", firstName);
    }
}
