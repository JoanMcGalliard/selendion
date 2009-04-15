/*
        Copyright Joan McGalliard, 2008-9
*/

package org.selendion.internal.command;

import org.concordion.api.Evaluator;
import org.concordion.api.ResultRecorder;
import org.concordion.api.Element;
import org.concordion.internal.*;
import org.concordion.internal.util.Announcer;
import org.selendion.internal.util.SeleniumIdeReader;
import org.selendion.internal.RunSeleniumListener;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

public class RunSeleniumCommand extends AbstractTogglingCommand {

    private SeleniumIdeReader seleniumIdeReader;
    private Announcer<RunSeleniumListener> listeners = Announcer.to(RunSeleniumListener.class);
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



        public void execute(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
            Element element = commandCall.getElement();
            ensureDocumentHasSeleniumTogglingScript(element);
            CommandCallList childCommands = commandCall.getChildren();

            childCommands.setUp(evaluator, resultRecorder);

            Object evaluatedExpression = evaluator.evaluate(commandCall.getExpression());
            List<String> seleniumFileNames = new ArrayList<String>();
            String testName = "";
            String htmlFilename;
            String htmlResource;

            if (evaluatedExpression.getClass().equals(String.class)) {
               htmlFilename=(String) evaluatedExpression;
                if (htmlFilename.startsWith("/")) {
                    htmlResource=htmlFilename;
                } else {
                    htmlResource=commandCall.getResource().getRelativeResource(htmlFilename).getPath();
                }

                seleniumFileNames.add(htmlResource);
                testName = ((String) evaluatedExpression).replaceAll(" *\\n *", " ").replaceFirst(".*/", "").replaceFirst(".html*$","").trim();
            } else {
                Object[] files = (Object[]) evaluatedExpression;
                for (Object obj : files) {
                    if (obj != null && ((String) obj).length() > 0) {
                        htmlFilename = (String) obj;
                        if (htmlFilename.startsWith("/")) {
                            htmlResource=htmlFilename;
                        } else {
                            htmlResource=commandCall.getResource().getRelativeResource(htmlFilename).getPath();
                        }
                        seleniumFileNames.add(htmlResource);
                        if (testName.length() > 0) {
                            testName = testName + "|";
                        }
                        testName = testName + ((String) obj).replaceFirst("^.*/", "").replaceFirst("\\.htm[l]?$", "").trim();
                    }
                }
            }
            evaluator.evaluate(commandCall.getExpression());
            Element resultElement;
            try {
                if (!element.getParent().getLocalName().equals("table")) {
                    if (element.getLocalName().equals("td")) {
                        resultElement = new Element("td");

                    } else {
                        resultElement = new Element("span");
                    }
                    testName = getTitle(element);


                    Element seleniumResult = seleniumIdeReader.runSeleniumScript(seleniumFileNames, evaluator, testName, listeners, resultRecorder);
                    seleniumResult.addAttribute("class", "seleniumTable");


                    wrapElementInTogglingButton(seleniumResult, resultElement, testName, "seleniumButton", seleniumIdeReader.getLastRunResult(), true);
                    element.insertAfter(resultElement);
                    element.addAttribute("class", "invisible");
                } else {
                    resultElement = new Element("td");
                    element.appendChild(resultElement);
                    Element seleniumResult = seleniumIdeReader.runSeleniumScript(seleniumFileNames, evaluator, testName, listeners, resultRecorder);
                    seleniumResult.addAttribute("class", "seleniumTable");                    
                    wrapElementInTogglingButton(seleniumResult, resultElement, testName, "seleniumButton", seleniumIdeReader.getLastRunResult(), true);

                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            childCommands.execute(evaluator, resultRecorder);
            childCommands.verify(evaluator, resultRecorder);
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

}




