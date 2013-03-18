/*
	Copyright Joan McGalliard, 2008-9
*/
package selendion.commands.Suite.support;

import org.selendion.integration.concordion.SelendionTestCase;

public class NotExpectedToPassButPassing extends SelendionTestCase {
    public String greetingFor(String firstName) {
        return String.format("Hello %s!", firstName);
    }


}