/*
	Copyright Joan McGalliard, 2008-2013
*/

package org.selendion.internal;

import org.concordion.internal.command.ThrowableCaughtListener;
import org.selendion.internal.command.RunSuiteSuccessEvent;
import org.selendion.internal.command.RunSuiteFailureEvent;

public interface RunSuiteListener extends ThrowableCaughtListener{

	void successReported(RunSuiteSuccessEvent runSuiteSuccessEvent);

	void failureReported(RunSuiteFailureEvent runSuiteFailureEvent);

}
