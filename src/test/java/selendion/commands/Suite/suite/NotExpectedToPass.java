/*
	Copyright Joan McGalliard, 2008-9
*/
package selendion.commands.Suite.suite;

import org.selendion.integration.concordion.SelendionTestCase;

public class NotExpectedToPass extends SelendionTestCase {
    public String greetingFor(String firstName) {
        return String.format("Hello %s!", firstName);
    }
    public String fullName(String firstName, String surname) throws Exception {
        throw new Exception("Implement me!");
    }


}