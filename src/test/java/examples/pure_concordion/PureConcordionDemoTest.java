/*
	Copyright Joan McGalliard, 2008-9
*/

package examples.pure_concordion;

import org.concordion.integration.junit3.ConcordionTestCase;

public class PureConcordionDemoTest extends ConcordionTestCase {

    public String greetingFor(String firstName) {
        return String.format("Hello %s!", firstName);
    }
}
