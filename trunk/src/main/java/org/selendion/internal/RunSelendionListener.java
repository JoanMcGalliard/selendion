package org.selendion.internal;

import org.concordion.internal.command.ThrowableCaughtListener;
import org.selendion.internal.command.RunSeleniumSuccessEvent;
import org.selendion.internal.command.RunSeleniumFailureEvent;
import org.selendion.internal.command.RunSelendionSuccessEvent;
import org.selendion.internal.command.RunSelendionFailureEvent;

public interface RunSelendionListener extends ThrowableCaughtListener{

	void successReported(RunSelendionSuccessEvent RunSelendionSuccessEvent);

	void failureReported(RunSelendionFailureEvent RunSelendionFailureEvent);

}