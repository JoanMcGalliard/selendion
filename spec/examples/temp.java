/*
	Copyright Joan McGalliard, 2008-9
*/
package examples;

import org.selendion.integration.concordion.SelendionTestCase;

import java.util.Hashtable;

public class temp extends SelendionTestCase {
    public Hashtable hashtable() {
        Hashtable h = new Hashtable();
        h.put("blah", "blahresponse");
        return h;
    }
    public void doNothing() {
        //empty
    }
}