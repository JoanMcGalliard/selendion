/*
	Copyright Joan McGalliard, 2008-9
*/

package org.selendion.internal.listener;

import org.concordion.internal.listener.ThrowableRenderer;
import org.selendion.internal.RunSuiteListener;
import org.selendion.internal.command.RunSuiteSuccessEvent;
import org.selendion.internal.command.RunSuiteFailureEvent;

public class RunSuiteResultRenderer extends ThrowableRenderer implements RunSuiteListener {

	public void successReported(RunSuiteSuccessEvent event) {
		event.getElement()
	        .addStyleClass("success")
	        .appendNonBreakingSpaceIfBlank();
	}

	public void failureReported(RunSuiteFailureEvent event) {
		event.getElement()
	        .addStyleClass("failure")
	        .appendNonBreakingSpaceIfBlank();
	}

}
