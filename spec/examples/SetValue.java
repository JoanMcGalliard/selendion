package examples;

import org.selendion.integration.concordion.SelendionTestCase;
import org.concordion.integration.junit3.ConcordionTestCase;
import junit.framework.TestCase;

public class SetValue extends SelendionTestCase {
    public void somewhereToStop() {
        System.out.println("somewhereToStop");
        if (this instanceof TestCase) {
            System.out.println("TestCase");
        }
        if (this instanceof ConcordionTestCase) {
            System.out.println("ConcordionTestCase");
        }
        if (this instanceof SelendionTestCase) {
            System.out.println("SelendionTestCase");
        }

    }
}
