/*
	Copyright Joan McGalliard, 2008-9
*/

package commands.runSelendionScripts;

import org.selendion.integration.concordion.SelendionTestCase;

public class NotExpectedToPassButPassing extends SelendionTestCase {
    public String greetingFor(String firstName) {
        return String.format("Hello %s!", firstName);
    }
    
}
