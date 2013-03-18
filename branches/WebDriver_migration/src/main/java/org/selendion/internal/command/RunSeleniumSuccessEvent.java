/*
	Copyright Joan McGalliard, 2008-2013
*/

package org.selendion.internal.command;

import org.concordion.api.Element;

public class RunSeleniumSuccessEvent {

	private final Element element;

	public RunSeleniumSuccessEvent(Element element) {
		this.element = element;
	}

	public Element getElement() {
		return element;
	}

}
