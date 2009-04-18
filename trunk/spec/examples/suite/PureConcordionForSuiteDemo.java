/*
	Copyright Joan McGalliard, 2008-9
*/

package examples.suite;

import org.selendion.integration.concordion.SelendionTestCase;

public class PureConcordionForSuiteDemo extends SelendionTestCase {

    public String greetingFor(String firstName) {
        return String.format("Hello %s!", firstName);
    }
}
