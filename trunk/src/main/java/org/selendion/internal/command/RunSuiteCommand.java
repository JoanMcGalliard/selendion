package org.selendion.internal.command;

import org.concordion.internal.command.AbstractCommand;
import org.concordion.internal.CommandCall;
import org.concordion.internal.util.Announcer;
import org.concordion.api.*;
import org.concordion.integration.junit3.ConcordionTestCase;
import org.selendion.internal.RunSuiteListener;
import org.junit.Test;

import java.util.Vector;

import junit.framework.TestSuite;
import junit.framework.TestResult;
import junit.extensions.ActiveTestSuite;


public class RunSuiteCommand extends AbstractCommand {
    private Announcer<RunSuiteListener> listeners = Announcer.to(RunSuiteListener.class);
    private Vector<Class<? extends ConcordionTestCase>> suite;


    public RunSuiteCommand(Vector<Class<? extends ConcordionTestCase>> suite) {
        this.suite = suite;
    }

    public void addRunSuiteListener(RunSuiteListener runSuiteListener) {
		listeners.addListener(runSuiteListener);
	}

    public void execute(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
        TestSuite testSuite;
        if (commandCall.getExpression().equals("active")) {
            testSuite =new ActiveTestSuite();
        } else {
            testSuite =new TestSuite();

        }
        for (Class<? extends ConcordionTestCase> test : suite) {
            testSuite.addTestSuite(test);
        }
        TestResult testResult=new TestResult();
        testSuite.run(testResult);


        String result=commandCall.getExpression();
        Element element = commandCall.getElement();
        if (result.equals("pass"))  {


            resultRecorder.record(Result.SUCCESS);
            announceSuccess(element);

        }   else if (result.equals("fail"))  {
             resultRecorder.record(Result.FAILURE);
            announceFailure(element);

        }   else if (result.equals("exception"))  {
            throw new RuntimeException ("Blah!");

        }
        
//


    }

    private void announceSuccess(Element element) {
        listeners.announce().successReported(new RunSuiteSuccessEvent(element));
    }

	private void announceFailure(Element element) {
		listeners.announce().failureReported(new RunSuiteFailureEvent(element));
	}
}




          /*

    private Announcer<AssertEqualsListener> listeners = Announcer.to(AssertEqualsListener.class);
    private final Comparator<Object> comparator;

    public AssertEqualsCommand() {
        this(new BrowserStyleWhitespaceComparator());
    }

    public AssertEqualsCommand(Comparator<Object> comparator) {
        this.comparator = comparator;
    }

    public void addAssertEqualsListener(AssertEqualsListener listener) {
        listeners.addListener(listener);
    }

    public void removeAssertEqualsListener(AssertEqualsListener listener) {
        listeners.removeListener(listener);
    }

    @Override
    public void verify(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
        Check.isFalse(commandCall.hasChildCommands(), "Nesting commands inside an 'assertEquals' is not supported");

        Element element = commandCall.getElement();

        Object actual = evaluator.evaluate(commandCall.getExpression());
        String expected = element.getText();

        if (comparator.compare(actual, expected) == 0) {

        } else {

        }
    }

    private void announceSuccess(Element element) {
        listeners.announce().successReported(new AssertEqualsSuccessEvent(element));
    }

    private void announceFailure(Element element, String expected, Object actual) {
        listeners.announce().failureReported(new AssertEqualsFailureEvent(element, expected, actual));
    }
}
*/