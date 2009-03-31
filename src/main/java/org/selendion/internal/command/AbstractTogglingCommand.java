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


    void ensureDocumentHasSeleniumTogglingScript(Element element) {
        Element rootElement = element.getRootElement();
        if (!rootElementsWithScript.contains(rootElement)) {
            rootElementsWithScript.add(rootElement);
            Element head = rootElement.getFirstDescendantNamed("head");
            Check.notNull(head, "Document <head> section is missing");
            Element script = new Element("script").addAttribute("type", "text/javascript");
            if (head != null) {
                head.prependChild(script);
            }
            script.appendText(IOUtil.readResourceAsString(TOGGLING_SCRIPT_RESOURCE_PATH, "UTF-8"));
        }

    }

    void wrapElementInTogglingButton(Element elementToAdd, Element resultElement, String title, boolean result) {
        String label = title.replaceFirst("\\|.*", "...");
        Element input = new Element("input")
                .addStyleClass("selendionHideViewButton")
                .setId("selendionHideViewButton" + buttonId)
                .addAttribute("type", "button")
                .addAttribute("class", result ? "success" : "failure")
                .addAttribute("onclick", "javascript:toggleSeleniumTable('" + buttonId + "', '" + label + "')")
                .addAttribute("value", label);

        resultElement.appendChild(input);
        elementToAdd.setId("selendionHideViewElement" + buttonId++);
        elementToAdd.addAttribute("class", "selendionHideViewElement");
        resultElement.appendChild(elementToAdd);
    }
}
