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
import org.selendion.integration.concordion.SelendionTestCase;

import java.net.URL;
import java.io.File;

public class RunSelendionCommand extends AbstractCommand {
    private Announcer<RunSelendionListener> listeners = Announcer.to(RunSelendionListener.class);

    public void addRunSelendionListener(RunSelendionListener runSelendionListener) {
        listeners.addListener(runSelendionListener);
    }

    public void removeRunSelendionListener(RunSelendionListener runSelendionListener) {
        listeners.removeListener(runSelendionListener);
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
        if (f.isFile()) {
            try {
                Class clazz = loader.findSelendionClass(htmlResource);
                SelendionTestCase test = (SelendionTestCase) clazz.newInstance();
                clazz.getMethod("testProcessSpecification", Evaluator.class).invoke(test, evaluator);
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


//         if (!(Boolean)expectedToPass.get(test.getClazz())) {
//
//
//
//
//            }
    private void announceSuccess(Element element) {
        listeners.announce().successReported(new RunSelendionSuccessEvent(element));
    }

    private void announceFailure(Element element) {
        listeners.announce().failureReported(new RunSelendionFailureEvent(element));
    }
}
