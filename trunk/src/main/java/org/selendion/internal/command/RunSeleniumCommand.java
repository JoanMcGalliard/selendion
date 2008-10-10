/*
        Copyright Joan McGalliard, 2008
*/

package org.selendion.internal.command;

import org.concordion.api.Evaluator;
import org.concordion.api.ResultRecorder;
import org.concordion.api.Result;
import org.concordion.api.Element;
import org.concordion.internal.*;
import org.concordion.internal.util.Announcer;
import org.concordion.internal.util.IOUtil;
import org.concordion.internal.util.Check;
import org.concordion.internal.command.AbstractCommand;
import org.selendion.internal.util.SeleniumIdeReader;
import org.selendion.internal.RunSeleniumListener;

import java.util.Set;
import java.util.HashSet;

public class RunSeleniumCommand extends AbstractCommand {

    private SeleniumIdeReader seleniumIdeReader;
    private Announcer<RunSeleniumListener> listeners = Announcer.to(RunSeleniumListener.class);
    private int buttonId=1;
    private Set<Element> rootElementsWithScript = new HashSet<Element>();


    public RunSeleniumCommand(SeleniumIdeReader seleniumIdeReader) {
        this.seleniumIdeReader = seleniumIdeReader;

    }

    @Override
    public void execute(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
        Strategy strategy;
        if (commandCall.getElement().isNamed("table")) {
            strategy = new TableStrategy();
        } else {
            strategy = new DefaultStrategy();
        }
        strategy.execute(commandCall, evaluator, resultRecorder);
    }

    public void addRunSeleniumListener(RunSeleniumListener runSuiteListener) {
        listeners.addListener(runSuiteListener);
    }

    private interface Strategy {
        void execute(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder);
    }

    private class DefaultStrategy implements Strategy {
        private static final String TOGGLING_SCRIPT_RESOURCE_PATH = "/org/selendion/internal/resource/selenium-visibility-toggler.js";

        

        public void execute(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
            Element element = commandCall.getElement();
            ensureDocumentHasSeleniumTogglingScript(element);
            CommandCallList childCommands = commandCall.getChildren();

            childCommands.setUp(evaluator, resultRecorder);
            String seleniumFile = commandCall.getResource().getRelativeResource(evaluator.evaluate(commandCall.getExpression()).toString()).getPath();
            evaluator.evaluate(commandCall.getExpression());
            boolean result;
            try {
                if (!element.getParent().getLocalName().equals("table")) {

                result = seleniumIdeReader.runSeleniumScript(seleniumFile, evaluator, element,listeners, buttonId++);
                } else {
                    result = seleniumIdeReader.runSeleniumScript(seleniumFile, evaluator);

                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (!element.getParent().getLocalName().equals("table")) {
                if (result) {
                    resultRecorder.record(Result.SUCCESS);
                    announceSuccess(element);
                } else {
                    resultRecorder.record(Result.FAILURE);
                    announceFailure(element);
                }
            }
            childCommands.execute(evaluator, resultRecorder);
            childCommands.verify(evaluator, resultRecorder);
        }

        private void ensureDocumentHasSeleniumTogglingScript(Element element) {
                Element rootElement = element.getRootElement();
                if (!rootElementsWithScript.contains(rootElement)) {
                    rootElementsWithScript.add(rootElement);
                    Element head = rootElement.getFirstDescendantNamed("head");
                    if (head == null) {
                        System.out.println(rootElement.toXML());
                    }
                    Check.notNull(head, "Document <head> section is missing");
                    Element script = new Element("script").addAttribute("type", "text/javascript");
                    head.prependChild(script);
                    script.appendText(IOUtil.readResourceAsString(TOGGLING_SCRIPT_RESOURCE_PATH, "UTF-8"));
            }

        }
    }

    private class TableStrategy implements Strategy {


        public void execute(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {

            TableSupport tableSupport = new TableSupport(commandCall);

            Row[] detailRows = tableSupport.getDetailRows();

            for (Row detailRow : detailRows) {
                if (detailRow.getCells().length != tableSupport.getColumnCount()) {
                    throw new RuntimeException("The <table> 'runSelenium' command only supports rows with an equal number of columns.");
                }
                commandCall.setElement(detailRow.getElement());
                tableSupport.copyCommandCallsTo(detailRow);
                commandCall.execute(evaluator, resultRecorder);
            }
        }


    }

    private void announceSuccess(Element element) {
        listeners.announce().successReported(new RunSeleniumSuccessEvent(element));
    }

    private void announceFailure(Element element) {
        listeners.announce().failureReported(new RunSeleniumFailureEvent(element));
    }

}




