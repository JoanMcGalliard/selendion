package examples.commands;

import org.selendion.integration.concordion.SelendionTestCase;

import test.concordion.TestRig;

import java.util.ArrayList;

public class ForEachTest extends SelendionTestCase {

 public String greetingFor(String firstName) {
        if (firstName.equals("Bob") || firstName.equals("John") || firstName.equals("Brian") || firstName.equals("Allen") || firstName.equals("James"))
        {
            return "Hello Sir!";
        } else {
            return "Hello Madam!";
        }
    }
    public ArrayList getList() {
        return  new java.util.ArrayList();
    }

    public String render(String fragment) throws Exception {
        return new TestRig()
            .withFixture(this)
            .processFragment(fragment)
            .getOutputFragmentXML();
    }
}
