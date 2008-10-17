package org.selendion.internal.util;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

/**
 * A TestSuite for active Tests. It runs each
 * test in a separate thread and waits until all
 * threads have terminated.
 * -- Aarhus Radisson Scandinavian Center 11th floor
 */
public class ActiveTestSuiteRestricted extends TestSuite {
	private volatile int fActiveTestDeathCount;
	private volatile int fActiveTestCount;
    private int maxThreads;

    public ActiveTestSuiteRestricted(int maxThreads) {
        this.maxThreads=maxThreads;
    }

	public ActiveTestSuiteRestricted(Class<? extends TestCase> theClass) {
		super(theClass);
	}

	public ActiveTestSuiteRestricted(String name) {
		super (name);
	}

	public ActiveTestSuiteRestricted(Class<? extends TestCase> theClass, String name) {
		super(theClass, name);
	}

	@Override
	public void run(TestResult result) {
		fActiveTestDeathCount= 0;
		super.run(result);
		waitUntilFinished();
	}

	@Override
	public void runTest(final Test test, final TestResult result) {
        fActiveTestCount++;
        while (maxThreads > 0 && fActiveTestCount > maxThreads) {
            try {
                wait();
            } catch (Exception e) {
                //ignore
            }
        }
        Thread t= new Thread() {
			@Override
			public void run() {
				try {
					// inlined due to limitation in VA/Java
					//ActiveTestSuite.super.runTest(test, result);
					test.run(result);
				} finally {
					ActiveTestSuiteRestricted.this.runFinished();
				}
			}
		};
		t.start();
	}

	synchronized void waitUntilFinished() {
		while (fActiveTestDeathCount < testCount()) {
			try {
				wait();
			} catch (InterruptedException e) {
				return; // ignore
			}
		}
	}

	synchronized public void runFinished() {
		fActiveTestDeathCount++;
        fActiveTestCount--;
        notifyAll();
	}
}
