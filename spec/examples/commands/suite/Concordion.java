package examples.commands.suite;

import org.concordion.integration.junit3.ConcordionTestCase;

public class Concordion extends ConcordionTestCase {

    public String greetingFor(String firstName) {
        return String.format("Hello %s!", firstName);
    }
}
