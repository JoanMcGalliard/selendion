/*
	Copyright Joan McGalliard, 2008-9
*/

package suite;

import org.concordion.integration.junit3.ConcordionTestCase;

public class PureConcordionForSuiteDemoTest extends ConcordionTestCase {

    public String greetingFor(String firstName) {
        return String.format("Hello %s!", firstName);
    }
}
