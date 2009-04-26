/*
	Copyright Joan McGalliard, 2008-9
*/

package org.selendion.internal.command;

import org.concordion.internal.command.AbstractCommand;
import org.concordion.internal.CommandCall;
import org.concordion.internal.util.Announcer;
import org.concordion.api.*;
import org.concordion.api.Element;
import org.selendion.internal.RunSuiteListener;
import org.selendion.internal.util.TestDescription;
import org.selendion.internal.util.ActiveTestSuiteRestricted;
import org.selendion.integration.concordion.SelendionTestCase;


import java.util.Vector;
import java.util.Enumeration;
import java.util.Hashtable;

import junit.framework.*;


public class RunSuiteCommand extends AbstractCommand {
    private Hashtable expectedToPass = new Hashtable();

    private class SuiteListener implements TestListener {

        public void addError(Test test, Throwable throwable) {
        }

        public void addFailure(Test test, AssertionFailedError assertionFailedError) {
        }

        public void endTest(Test test) {
            expectedToPass.put(test.getClass(), ((SelendionTestCase) test).isExpectedToPass());
        }

        public void startTest(Test test) {
        }
    }

    private final Announcer<RunSuiteListener> listeners = Announcer.to(RunSuiteListener.class);
    private final Hashtable suites;


    public RunSuiteCommand(Hashtable suites) {
        this.suites = suites;
    }

    public void addRunSuiteListener(RunSuiteListener runSuiteListener) {
        listeners.addListener(runSuiteListener);
    }

    public void execute(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
        Object evaluatedExpression = evaluator.evaluate(commandCall.getExpression());
        Object[] params = (Object[]) evaluatedExpression;
        String suiteName = (String) params[0];
        int threads;

        if (params[1].getClass().equals(Integer.class)) {
            threads = (Integer) params[1];
        } else {
            threads = Integer.parseInt((String) params[1]);
        }

        TestSuite testSuite;
        expectedToPass = new Hashtable();


        if (threads == 1) {
            testSuite = new TestSuite();
        } else {
            testSuite = new ActiveTestSuiteRestricted(threads);
        }
        Vector<TestDescription> suite;
        if (suites.get(suiteName) == null) {
            suite = new Vector<TestDescription>();
        } else {
            suite = (Vector<TestDescription>) suites.get(suiteName);
        }
        for (TestDescription test : suite) {
            testSuite.addTestSuite(test.getClazz());
        }
        TestResult testResult = new TestResult();
        SuiteListener suiteListener = new SuiteListener();
        testResult.addListener(suiteListener);
        testSuite.run(testResult);

        Hashtable failures = new Hashtable();
        Enumeration<TestFailure> errors = testResult.errors();
        while (errors.hasMoreElements()) {
            TestFailure nextError = errors.nextElement();
            failures.put(nextError.failedTest().getClass(), nextError.exceptionMessage());
        }
        errors = testResult.failures();
        while (errors.hasMoreElements()) {
            TestFailure nextError = errors.nextElement();
            failures.put(nextError.failedTest().getClass(), nextError.exceptionMessage());
        }
        Element element = commandCall.getElement();
        Element list;
        if (threads != 1) {
            list = new Element("ul");
        } else {
            list = new Element("ol");
        }
        for (TestDescription test : suite) {
            Element anchor = new Element("a");
            anchor.addAttribute("href", test.getFile());
            anchor.appendText(test.getTitle());
            Element li = new Element("li");
            li.appendChild(anchor);
            list.appendChild(li);
            if (!(Boolean) expectedToPass.get(test.getClazz())) {
                Element b = new Element("b");
                b.addAttribute("class", "attention");
                b.appendText(" This test is not expected to pass. ");
                li.appendChild(b);
            }
            if (failures.containsKey(test.getClazz())) {
                resultRecorder.record(Result.FAILURE);
                announceFailure(anchor);
                li.appendText(" " + failures.get(test.getClazz()));
            } else {
                resultRecorder.record(Result.SUCCESS);
                announceSuccess(anchor);
            }

        }
        element.insertAfter(list);
    }

    private void announceSuccess(Element element) {
        listeners.announce().successReported(new RunSuiteSuccessEvent(element));
    }

    private void announceFailure(Element element) {
        listeners.announce().failureReported(new RunSuiteFailureEvent(element));
    }
}
