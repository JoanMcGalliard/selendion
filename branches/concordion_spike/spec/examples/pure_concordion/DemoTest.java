package examples.pure_concordion;

import org.concordion.integration.junit3.ConcordionTestCase;

public class DemoTest extends ConcordionTestCase {

    public String greetingFor(String firstName) {
        return String.format("Hello %s!", firstName);
    }
}
