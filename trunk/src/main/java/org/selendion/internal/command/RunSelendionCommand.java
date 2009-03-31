/*
        Copyright Joan McGalliard, 2008-9
*/

package org.selendion.internal.command;

import org.concordion.internal.command.*;
import org.concordion.internal.*;
import org.concordion.internal.util.Announcer;
import org.concordion.api.*;
import org.selendion.internal.util.SelendionClassLoader;
import org.selendion.internal.RunSelendionListener;
import org.selendion.internal.SelendionResultRecorder;
import org.selendion.integration.concordion.SelendionTestCase;

import java.net.URL;
import java.io.File;
import java.util.List;
import java.util.ArrayList;

public class RunSelendionCommand extends AbstractTogglingCommand {
    private Announcer<RunSelendionListener> listeners = Announcer.to(RunSelendionListener.class);

    public void addRunSelendionListener(RunSelendionListener runSelendionListener) {
        listeners.addListener(runSelendionListener);
    }

    public void removeRunSelendionListener(RunSelendionListener runSelendionListener) {
        listeners.removeListener(runSelendionListener);
    }


    public void x(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {

//
//        Object evaluatedExpression = evaluator.evaluate(commandCall.getExpression());
//        String testName = "";
//
//        if (evaluatedExpression.getClass().equals(String.class)) {
//            testName = ((String) evaluatedExpression).replaceAll(" *\\n *", " ").replaceFirst(".*/", "").replaceFirst(".html*$","").trim();
//        } else {
//            testName = "run script";
//        }
//        evaluator.evaluate(commandCall.getExpression());
//        Element resultElement;
//        try {
//            if (!element.getParent().getLocalName().equals("table")) {
//                if (element.getLocalName().equals("td")) {
//                    resultElement = new Element("td");
//
//                } else {
//                resultElement = new Element("span");
//                }
//                testName = element.getText().replaceAll(" *\\n *", " ").trim();
//
//                seleniumIdeReader.runSeleniumScript(seleniumFileNames, evaluator, testName, resultElement, listeners, buttonId++, resultRecorder);
//                element.insertAfter(resultElement);
//                element.addAttribute("class", "invisible");
//            } else {
//                resultElement = new Element("td");
//                element.appendChild(resultElement);
//                seleniumIdeReader.runSeleniumScript(seleniumFileNames, evaluator, testName, resultElement, listeners, buttonId++, resultRecorder);
//            }
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//
//        childCommands.execute(evaluator, resultRecorder);
//        childCommands.verify(evaluator, resultRecorder);


    }

    public void execute(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {

        CommandCallList childCommands = commandCall.getChildren();
        childCommands.setUp(evaluator, resultRecorder);
        String htmlFilename = evaluator.evaluate(commandCall.getExpression()).toString();
        String htmlResource = commandCall.getResource().getRelativeResource(htmlFilename)
                .getPath().replaceFirst("^/", "");
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
            try {
                Class clazz = loader.findSelendionClass(htmlResource);
                SelendionTestCase test = (SelendionTestCase) clazz.newInstance();
                SelendionResultRecorder childResultRecorder = (SelendionResultRecorder)clazz.getMethod("testProcessSpecification", Evaluator.class).invoke(test, evaluator);


//                Specification specification = childResultRecorder.getResultSpecification();
//                Element[] body = specification.getCommandCall().getElement().getChildElements()[1].getChildElements();
//                Element resultElement = new Element("div");
//                for (int x = 0; x < body.length; x++) {
//                    resultElement.appendChild(body[x].copy());
//                }
//                resultElement.addAttribute("class", "includedPassingTest");
//                  element.insertAfter(resultElement);
//                    element.addAttribute("class", "invisible");

                resultRecorder.record(Result.SUCCESS);
                announceSuccess(element);
                if (!(Boolean) clazz.getMethod("isExpectedToPass").invoke(test)) {
                    Element b = new Element("b");
                    b.addAttribute("class", "attention");
                    b.appendText(" (This test is not expected to pass) ");
                    element.appendChild(b);
                }
            } catch (Exception e) {
                announceFailure(element);
                resultRecorder.record(Result.FAILURE);
                   Element b = new Element("b");
                    b.addAttribute("class", "attention");
                    b.appendText(String.format(" (%s) ", e.getCause().getMessage()) );
                    element.appendChild(b);
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
