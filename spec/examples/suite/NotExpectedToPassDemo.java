/*
	Copyright Joan McGalliard, 2008
*/
package examples.suite;

import org.selendion.integration.concordion.SelendionTestCase;

public class NotExpectedToPassDemo extends SelendionTestCase {
    public String greetingFor(String firstName) {
        return String.format("Hello %s!", firstName);
    }
    public String fullName(String firstName, String surname) throws Exception {
        throw new Exception("Implement me!");
    }


}