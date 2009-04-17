package org.selendion.internal.command;

import org.concordion.internal.command.AbstractCommand;
import org.concordion.internal.util.Check;
import org.concordion.internal.util.IOUtil;
import org.concordion.api.Element;

import java.util.Set;
import java.util.HashSet;

public abstract class AbstractTogglingCommand extends AbstractCommand {
    private static final String TOGGLING_SCRIPT_RESOURCE_PATH = "/org/selendion/internal/resource/selenium-visibility-toggler.js";
    private Set<Element> rootElementsWithScript = new HashSet<Element>();
    private static int buttonId = 1;

    public static void resetButtonCount() {
        buttonId = 1;
    }
    protected enum Hide { IMMEDIATE, CLICKABLE, LINK }


    void ensureDocumentHasSeleniumTogglingScript(Element element) {
        Element rootElement = element.getRootElement();
        if (!rootElementsWithScript.contains(rootElement)) {
            rootElementsWithScript.add(rootElement);
            Element head = rootElement.getFirstDescendantNamed("head");
            if (head == null) {
                System.out.println(rootElement.toXML());
            }
            Check.notNull(head, "Document <head> section is missing");
            Element script = new Element("script").addAttribute("type", "text/javascript");
            if (head != null) {
                head.prependChild(script);
            }
            script.appendText(IOUtil.readResourceAsString(TOGGLING_SCRIPT_RESOURCE_PATH, "UTF-8"));
        }

    }

    String getTitle(Element element) {
        return element.getText().replaceAll(" *\\n *", " ").trim();
    }

    void wrapElementInTogglingButton(Element elementToAdd, Element element, String title, String clazz, boolean result, Hide hide) {
        if (hide.equals(Hide.CLICKABLE)) {
            String label = title.replaceFirst("\\|.*", "...");
            Element input = new Element("input")
                    .addStyleClass("selendionHideViewButton")
                    .setId("selendionHideViewButton" + buttonId)
                    .addAttribute("type", "button")
                    .addAttribute("class", (result ? "success" : "failure") + " " + clazz)
                    .addAttribute("onclick", "javascript:toggleSelendionElement('" + buttonId + "', '" + label + "')")
                    .addAttribute("value", label);

            element.appendChild(input);
            elementToAdd.setId("selendionHideViewElement" + buttonId++);
            element.appendChild(elementToAdd);
        } else if (hide.equals(Hide.IMMEDIATE)) {
            element.appendChild(elementToAdd);
        }
    }
}
