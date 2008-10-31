package examples.commands.suite;

import org.selendion.integration.concordion.SelendionTestCase;

public class Simple extends SelendionTestCase {

    public String greetingFor(String firstName) {
        return String.format("Hello %s!", firstName);
    }
}
