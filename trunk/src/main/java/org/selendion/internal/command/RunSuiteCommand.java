package org.selendion.internal.command;

import org.concordion.internal.command.AbstractCommand;
import org.concordion.internal.CommandCall;
import org.concordion.internal.util.Announcer;
import org.concordion.api.*;
import org.concordion.api.Element;
import org.selendion.internal.RunSuiteListener;
import org.selendion.internal.util.TestDescription;
import org.selendion.internal.util.ActiveTestSuiteRestricted;


import java.util.Vector;
import java.util.Enumeration;

import junit.framework.TestSuite;
import junit.framework.TestResult;
import junit.framework.TestFailure;


public class RunSuiteCommand extends AbstractCommand {
    private Announcer<RunSuiteListener> listeners = Announcer.to(RunSuiteListener.class);
    private Vector<TestDescription> suite;


    public RunSuiteCommand(Vector<TestDescription> suite) {
        this.suite = suite;
    }

    public void addRunSuiteListener(RunSuiteListener runSuiteListener) {
        listeners.addListener(runSuiteListener);
    }

    public void execute(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
        TestSuite testSuite;
        int threads = Integer.parseInt(evaluator.evaluate(commandCall.getExpression()).toString());

        if (threads == 1 ) {
            testSuite = new TestSuite();
        } else {
            testSuite = new ActiveTestSuiteRestricted(threads);
        }
        for (TestDescription test : suite) {
            testSuite.addTestSuite(test.getClazz());
        }
        TestResult testResult = new TestResult();
        testSuite.run(testResult);

        Vector<Class> failures = new Vector<Class>();
        Enumeration<TestFailure> errors = testResult.errors();
        while (errors.hasMoreElements()) {
            failures.add(errors.nextElement().failedTest().getClass());
        }
        Element element = commandCall.getElement();
        Element list;
        if (threads != 1) {
            list = new Element("ul");
        } else {
            list = new Element("ol");
        }
        for (TestDescription test : suite) {
            Element testElement = new Element("a");
            testElement.addAttribute("href", test.getFile());
            testElement.appendText(test.getTitle());
            if (failures.contains(test.getClazz())) {
                resultRecorder.record(Result.FAILURE);
                announceFailure(testElement);
            } else {
                resultRecorder.record(Result.SUCCESS);
                announceSuccess(testElement);
            }
            Element li = new Element("li");
            li.appendChild(testElement);
            list.appendChild(li);
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