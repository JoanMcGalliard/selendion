/*
	Copyright Joan McGalliard, 2008-9
*/

package selendion.commands.Suite.support;

import org.selendion.integration.concordion.SelendionTestCase;

public class Sleep extends SelendionTestCase {
    public void sleep() {
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
    
}
