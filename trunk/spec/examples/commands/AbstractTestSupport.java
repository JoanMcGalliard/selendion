package examples.commands;

import org.selendion.integration.concordion.SelendionTestCase;
import test.concordion.TestRig;

public abstract class AbstractTestSupport extends SelendionTestCase {
    public String render(String fragment) throws Exception {
        return trimException( new TestRig()
                .withFixture(this)
                .withResourceName("/examples/commands/")
                .processFragment(fragment)
                .getOutputFragmentXML().replaceAll("><", "> <"));
    }
    private String trimException(String message)  {
        return message.replaceFirst("<input class=\"stackTraceButton\".*",
                "<input class=\"stackTraceButton\" ...");
    }

}