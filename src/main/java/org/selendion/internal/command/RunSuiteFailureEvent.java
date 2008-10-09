package org.selendion.internal.command;

import org.concordion.api.Element;

public class RunSuiteFailureEvent {

	private final Element element;

	public RunSuiteFailureEvent(Element element) {
		this.element = element;
	}

	public Element getElement() {
		return element;
	}

}
