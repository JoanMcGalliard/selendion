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


import java.util.Vector;
import java.util.Enumeration;
import java.util.Hashtable;

import junit.framework.TestSuite;
import junit.framework.TestResult;
import junit.framework.TestFailure;


public class ClearSuiteCommand extends AbstractCommand {
    private final Hashtable suites;


    public ClearSuiteCommand(Hashtable suites) {
        this.suites = suites;
    }

    public void setUp(org.concordion.internal.CommandCall commandCall, org.concordion.api.Evaluator evaluator, org.concordion.api.ResultRecorder resultRecorder) {
        String suiteName = evaluator.evaluate(commandCall.getExpression()).toString();

        ((Vector<TestDescription>)this.suites.get(suiteName)).clear();
    }

}
