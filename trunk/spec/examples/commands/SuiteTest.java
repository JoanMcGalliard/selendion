package examples.commands;

import org.selendion.integration.concordion.SelendionTestCase;
import org.concordion.api.Resource;

import test.concordion.TestRig;

import java.util.ArrayList;
import java.util.Date;

public class SuiteTest extends SelendionTestCase {
    private long runningTime=-1;
    public String render(String fragment) throws Exception {
        Date start = new Date();
        String result= new TestRig()
            .withFixture(this)
            .withResourceName("/examples/commands/")
            .processFragment(fragment)
            .getOutputFragmentXML();
        runningTime=(new Date()).getTime() - start.getTime();
        return result.replaceAll("><", "> <");
    }
    public long getRunningTimeOfLastTest() throws Exception {
        if (this.runningTime < 0) {
            throw new Exception("Running time not initialized");
        }
        return runningTime;

    }
    public boolean greaterThan(long a, long b) {
        return a > b;
    }
}
