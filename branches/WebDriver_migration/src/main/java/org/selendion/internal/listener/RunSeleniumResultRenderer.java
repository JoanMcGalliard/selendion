/*
	Copyright Joan McGalliard, 2008-2013
*/

package org.selendion.internal.listener;

import org.concordion.internal.listener.ThrowableRenderer;
import org.selendion.internal.RunSeleniumListener;
import org.selendion.internal.command.RunSeleniumSuccessEvent;
import org.selendion.internal.command.RunSeleniumFailureEvent;

public class RunSeleniumResultRenderer extends ThrowableRenderer implements RunSeleniumListener {

	public void successReported(RunSeleniumSuccessEvent event) {
		event.getElement()
	        .addStyleClass("success")
	        .appendNonBreakingSpaceIfBlank();
	}

	public void failureReported(RunSeleniumFailureEvent event) {
		event.getElement()
	        .addStyleClass("failure")
	        .appendNonBreakingSpaceIfBlank();
	}

}
