package examples.pure_concordion;

import org.selendion.integration.concordion.SelendionTestCase;

public class PureConcordionSelendionDemoTest extends SelendionTestCase {

    public String greetingFor(String firstName) {
        return String.format("Hello %s!", firstName);
    }
}