/*
	Copyright Joan McGalliard, 2008-2013
*/

package org.selendion.internal.command;

import org.concordion.api.Element;

public class RunSuiteSuccessEvent {

	private final Element element;

	public RunSuiteSuccessEvent(Element element) {
		this.element = element;
	}

	public Element getElement() {
		return element;
	}

}
