/*
	Copyright Joan McGalliard, 2008-9
*/

package selendion.commands.Suite;

import org.selendion.integration.concordion.SelendionTestCase;

import concordion.test.concordion.TestRig;

import java.util.Date;
import java.util.ArrayList;

public class SuiteTest extends SelendionTestCase {
    public ArrayList getList() {
        return  new java.util.ArrayList();
    }
    
    private long runningTime=-1;
    private TestRig testRig;
    public SuiteTest () {
        super();
        testRig = new TestRig()
            .withFixture(this)
            .withResourceName("/selendion/commands/Suite/")
            .withBaseClass(this.getClass());
    }
    public String render(String fragment) throws Exception {
        Date start = new Date();
        String result= testRig
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
    public void method() {
    }
    
}
