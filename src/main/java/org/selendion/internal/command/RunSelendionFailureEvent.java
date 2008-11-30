package org.selendion.internal.command;

import org.concordion.api.Element;

public class RunSelendionFailureEvent {

	private final Element element;

	public RunSelendionFailureEvent(Element element) {
		this.element = element;
	}

	public Element getElement() {
		return element;
	}

}