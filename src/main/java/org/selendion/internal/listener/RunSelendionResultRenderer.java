package org.selendion.internal.listener;

import org.concordion.internal.listener.ThrowableRenderer;
import org.selendion.internal.RunSeleniumListener;
import org.selendion.internal.RunSelendionListener;
import org.selendion.internal.command.RunSeleniumSuccessEvent;
import org.selendion.internal.command.RunSeleniumFailureEvent;
import org.selendion.internal.command.RunSelendionSuccessEvent;
import org.selendion.internal.command.RunSelendionFailureEvent;

public class RunSelendionResultRenderer extends ThrowableRenderer implements RunSelendionListener {

	public void successReported(RunSelendionSuccessEvent event) {
		event.getElement()
	        .addStyleClass("success")
	        .appendNonBreakingSpaceIfBlank();
	}

	public void failureReported(RunSelendionFailureEvent event) {
		event.getElement()
	        .addStyleClass("failure")
	        .appendNonBreakingSpaceIfBlank();
	}

}