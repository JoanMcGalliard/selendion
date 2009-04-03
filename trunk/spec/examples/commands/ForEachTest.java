/*
	Copyright Joan McGalliard, 2008-9
*/

package commands;

import org.selendion.integration.concordion.SelendionTestCase;

import test.concordion.TestRig;

import java.util.ArrayList;

public class ForEachTest extends AbstractTestSupport {

    public String greetingFor(String firstName) {
        if (firstName.equals("Bob") || firstName.equals("John") || firstName.equals("Brian") || firstName.equals("Allen") || firstName.equals("James")) {
            return "Hello Sir!";
        } else {
            return "Hello Madam!";
        }
    }

    public ArrayList getList() {
        return new java.util.ArrayList();
    }

}
