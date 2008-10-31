package examples.commands;

import org.selendion.integration.concordion.SelendionTestCase;

import test.concordion.TestRig;

import java.util.ArrayList;

public class SuiteTest extends SelendionTestCase {
    public String render(String fragment) throws Exception {
        return new TestRig()
            .withFixture(this)
            .processFragment(fragment)
            .getOutputFragmentXML();
    }
}
