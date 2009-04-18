/*
	Copyright Joan McGalliard, 2008-9
*/

package selendion.commands.Suite.suite;

import org.selendion.integration.concordion.SelendionTestCase;

public class Sleep extends SelendionTestCase {
    public void sleep() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
    
}
