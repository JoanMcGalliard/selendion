/*
	Copyright Joan McGalliard, 2008-9
*/

package selendion.commands.Suite;

import org.selendion.integration.concordion.SelendionTestCase;

import concordion.test.concordion.TestRig;

import java.util.Date;

public class SuiteTest extends SelendionTestCase {
    private long runningTime=-1;
    public String render(String fragment) throws Exception {
        Date start = new Date();
        String result= new TestRig()
            .withFixture(this)
            .withResourceName("/selendion/commands/Suite/")
            .withBaseClass(this.getClass())
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
