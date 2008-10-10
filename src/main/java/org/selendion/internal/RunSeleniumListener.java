package org.selendion.internal;

import org.concordion.internal.command.ThrowableCaughtListener;
import org.selendion.internal.command.RunSeleniumSuccessEvent;
import org.selendion.internal.command.RunSeleniumFailureEvent;

public interface RunSeleniumListener extends ThrowableCaughtListener{

	void successReported(RunSeleniumSuccessEvent runSeleniumSuccessEvent);

	void failureReported(RunSeleniumFailureEvent runSeleniumFailureEvent);

}
