/*
	Copyright Joan McGalliard, 2008-9
*/

package commands.support;

import org.selendion.integration.concordion.SelendionTestCase;

public class Simple extends SelendionTestCase {
    public void sleep() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

}
