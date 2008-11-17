/*
        Copyright Joan McGalliard, 2008
*/

package org.selendion.internal.command;

import org.concordion.api.Evaluator;
import org.concordion.api.ResultRecorder;
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
import java.util.List;
import java.util.ArrayList;

public class RunSeleniumCommand extends AbstractCommand {

    private SeleniumIdeReader seleniumIdeReader;
    private Announcer<RunSeleniumListener> listeners = Announcer.to(RunSeleniumListener.class);
    private int buttonId = 1;
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

    public void addRunSeleniumListener(RunSeleniumListener runSelendionListener) {
        listeners.addListener(runSelendionListener);
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

            Object evaluatedExpression = evaluator.evaluate(commandCall.getExpression());
            List<String> seleniumFileNames = new ArrayList<String>();
            String testName = "";

            if (evaluatedExpression.getClass().equals(String.class)) {
                seleniumFileNames.add(commandCall.getResource().getRelativeResource((String) evaluatedExpression).getPath());
                testName = ((String) evaluatedExpression).replaceAll(" *\\n *", " ").replaceFirst(".*/", "").replaceFirst(".html*$","").trim();
            } else {
                Object[] files = (Object[]) evaluatedExpression;
                for (Object obj : files) {
                    if (obj != null && ((String) obj).length() > 0) {
                        seleniumFileNames.add(commandCall.getResource().getRelativeResource((String) obj).getPath());
                        if (testName.length() > 0) {
                            testName = testName + "|";
                        }
                        testName = testName + ((String) obj).replaceFirst("^.*/", "").replaceFirst("\\.htm[l]?$", "").trim();
                    }
                }
            }
            evaluator.evaluate(commandCall.getExpression());
            boolean result;
            Element resultElement;
            try {
                if (!element.getParent().getLocalName().equals("table")) {
                    if (element.getLocalName().equals("td")) {
                        resultElement = new Element("td");

                    } else {
                    resultElement = new Element("span");
                    }
                    testName = element.getText().replaceAll(" *\\n *", " ").trim();

                    result = seleniumIdeReader.runSeleniumScript(seleniumFileNames, evaluator, testName, resultElement, listeners, buttonId++, resultRecorder);
                    element.insertAfter(resultElement);
                    element.addAttribute("class", "invisible");
                } else {
                    resultElement = new Element("td");
                    element.appendChild(resultElement);
                    result = seleniumIdeReader.runSeleniumScript(seleniumFileNames, evaluator, testName, resultElement, listeners, buttonId++, resultRecorder);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (result) {
                announceSuccess(resultElement);
            } else {
                announceFailure(resultElement);
            }
            childCommands.execute(evaluator, resultRecorder);
            childCommands.verify(evaluator, resultRecorder);
        }

        private void ensureDocumentHasSeleniumTogglingScript(Element element) {
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
            Element columnHeader = new Element("td");
            columnHeader.appendText("Selenium");
            tableSupport.getLastHeaderRow().getElement().appendChild(columnHeader);
        }


    }

    private void announceSuccess(Element element) {
        listeners.announce().successReported(new RunSeleniumSuccessEvent(element));
    }

    private void announceFailure(Element element) {
        listeners.announce().failureReported(new RunSeleniumFailureEvent(element));
    }

}




