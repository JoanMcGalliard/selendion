/*
        Copyright Joan McGalliard, 2008-9
*/

package org.selendion.internal.command;

import org.concordion.internal.command.*;
import org.concordion.internal.*;
import org.concordion.internal.util.Announcer;
import org.concordion.api.*;
import org.selendion.internal.util.SelendionClassLoader;
import org.selendion.internal.util.SeleniumIdeReader;
import org.selendion.internal.RunSelendionListener;
import org.selendion.integration.concordion.SelendionTestCase;
import org.selendion.integration.BrowserDriver;

import java.net.URL;
import java.io.File;

public class RunSelendionCommand extends AbstractTogglingCommand {
    private Announcer<RunSelendionListener> listeners = Announcer.to(RunSelendionListener.class);
    private SeleniumIdeReader seleniumIdeReader;
    public RunSelendionCommand(SeleniumIdeReader seleniumIdeReader) {
        super();
        this.seleniumIdeReader = seleniumIdeReader ;
    }

    public void addRunSelendionListener(RunSelendionListener runSelendionListener) {
        listeners.addListener(runSelendionListener);
    }

    public void removeRunSelendionListener(RunSelendionListener runSelendionListener) {
        listeners.removeListener(runSelendionListener);
    }

    public void execute(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {

        CommandCallList childCommands = commandCall.getChildren();
        childCommands.setUp(evaluator, resultRecorder);
        String htmlFilename;
        Object evaluatedExpression = evaluator.evaluate(commandCall.getExpression());
        boolean hide = true;
        if (evaluatedExpression.getClass().equals(String.class)) {
            htmlFilename = (String) evaluatedExpression;
            hide =true;
        } else {
            Object[] params = (Object[]) evaluatedExpression;
            if (params.length > 2) {
                throw new RuntimeException("Too many params");
            }
            htmlFilename = (String) params[0];
            hide = (Boolean) params[1];
        }
        String htmlResource;
        if (htmlFilename.startsWith("/")) {
            htmlResource = htmlFilename.replaceFirst("^/", "");
        } else {
            htmlResource = commandCall.getResource().getRelativeResource(htmlFilename)
                    .getPath().replaceFirst("^/", "");
        }
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        SelendionClassLoader loader = new SelendionClassLoader();
        URL url = contextClassLoader.getResource(htmlResource);
        if (url == null) {
            throw new RuntimeException(String.format("Can't run %s: file does not exist.", htmlFilename));
        }
        File f = new File(contextClassLoader.getResource(htmlResource).getPath().replaceAll("%20", " "));
        Element element = commandCall.getElement();
        ensureDocumentHasSeleniumTogglingScript(element);

        if (f.isFile()) {
            Element div = new Element("div");

            try {
                Class clazz = loader.findSelendionClass(htmlResource);
                SelendionTestCase test = (SelendionTestCase) clazz.newInstance();


                Element[] body = (Element[]) clazz.getMethod("testProcessSpecification",
                        Evaluator.class, BrowserDriver.class).invoke(test, evaluator, seleniumIdeReader.getBrowser());

                for (int x = 0; x < body.length; x++) {
                    Element e = body[x];
                    if (e.getAttributeValue("class") == null || !e.getAttributeValue("class").equals("footer")) {
                        div.appendChild(e.copy());
                    }
                }
                Element resultElement = new Element("span");

                element.insertAfter(resultElement);
                element.addAttribute("class", "invisible");
                try {
                    clazz.getMethod("lastExecutionResult").invoke(test);     //throws exception if failed
                        wrapElementInTogglingButton(div, resultElement, getTitle(element), "selendionButton", true, hide);
                    resultRecorder.record(Result.SUCCESS);
                    if (hide) {
                        div.addAttribute("class", "includedHiddenPassingTest");
                    } else {
                        div.addAttribute("class", "includedPassingTest");
                    }

                    announceSuccess(element);
                    if (!(Boolean) clazz.getMethod("isExpectedToPass").invoke(test)) {
                        Element b = new Element("b");
                        b.addAttribute("class", "attention");
                        b.appendText(" (This test is not expected to pass) ");
                        element.appendChild(b);
                    }

                } catch (Exception e) {

                    wrapElementInTogglingButton(div, resultElement, getTitle(element), "selendionButton", false, hide);

                    resultRecorder.record(Result.FAILURE);
                    announceFailure(element);
                    if (hide) {
                        div.addAttribute("class", "includedHiddenFailingTest");
                    } else {
                        div.addAttribute("class", "includedFailingTest");
                    }
                    Element b = new Element("b");
                    b.addAttribute("class", "attention");
                    b.appendText(String.format(" (%s) ", e.getCause().getMessage()));
                    resultElement.appendChild(b);
                }
            }
            catch (Exception e) {
                throw new RuntimeException(e);

            }
        } else {
            throw new RuntimeException(String.format("Can't open %s", htmlFilename));
        }
    }

    private void announceSuccess(Element element) {
        listeners.announce().successReported(new RunSelendionSuccessEvent(element));
    }

    private void announceFailure(Element element) {
        listeners.announce().failureReported(new RunSelendionFailureEvent(element));
    }

}
