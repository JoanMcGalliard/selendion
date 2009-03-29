/*
	Copyright Joan McGalliard, 2008-9
*/

package org.selendion.internal.command;

import org.concordion.api.Element;

public class RunSeleniumFailureEvent {

	private final Element element;

	public RunSeleniumFailureEvent(Element element) {
		this.element = element;
	}

	public Element getElement() {
		return element;
	}

}
