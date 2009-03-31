package org.selendion.internal.command;

import org.concordion.internal.command.AbstractCommand;
import org.concordion.internal.util.Check;
import org.concordion.internal.util.IOUtil;
import org.concordion.api.Element;

import java.util.Set;
import java.util.HashSet;

/**
 * Created by IntelliJ IDEA.
 * User: jem
 * Date: Mar 31, 2009
 * Time: 7:24:21 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractTogglingCommand extends AbstractCommand {
     private static final String TOGGLING_SCRIPT_RESOURCE_PATH = "/org/selendion/internal/resource/selenium-visibility-toggler.js";
    private Set<Element> rootElementsWithScript = new HashSet<Element>();

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
}
