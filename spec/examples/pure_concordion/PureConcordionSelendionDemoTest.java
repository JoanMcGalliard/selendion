/*
	Copyright Joan McGalliard, 2008-9
*/

package pure_concordion;

import org.selendion.integration.concordion.SelendionTestCase;

public class PureConcordionSelendionDemoTest extends SelendionTestCase {

    public String greetingFor(String firstName) {
        return String.format("Hello %s!", firstName);
    }
}
