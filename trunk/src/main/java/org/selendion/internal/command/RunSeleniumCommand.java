/*
        Copyright Joan McGalliard, 2008
*/

package org.selendion.internal.command;

import org.concordion.api.Evaluator;
import org.concordion.api.ResultRecorder;
import org.concordion.internal.*;
import org.concordion.internal.command.AbstractCommand;
import org.selendion.internal.util.SeleniumIdeReader;

public class RunSeleniumCommand extends AbstractCommand {

    private DocumentParser documentParser;
    private SeleniumIdeReader seleniumIdeReader;


    public RunSeleniumCommand(DocumentParser documentParser, SeleniumIdeReader seleniumIdeReader) {
        this.documentParser = documentParser;
        this.seleniumIdeReader = seleniumIdeReader;

    }

    @Override
    public void execute(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
        Strategy strategy;
        CommandCallList childCommands = commandCall.getChildren();

        childCommands.setUp(evaluator, resultRecorder);
        childCommands.execute(evaluator, resultRecorder);
        childCommands.verify(evaluator, resultRecorder);

        if (commandCall.getElement().isNamed("table")) {
            strategy = new TableStrategy();
        } else {
            strategy = new DefaultStrategy();
        }
        strategy.execute(commandCall, evaluator, resultRecorder);
    }

    private interface Strategy {
        void execute(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder);
    }

    private class DefaultStrategy implements Strategy {

        void DefaultStrategy() {
        }


        public void execute(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
            CommandCallList childCommands = commandCall.getChildren();

            childCommands.setUp(evaluator, resultRecorder);
            String seleniumFile = commandCall.getResource().getRelativeResource(evaluator.evaluate(commandCall.getExpression()).toString()).getPath();
            evaluator.evaluate(commandCall.getExpression());

            try {
                seleniumIdeReader.runSeleniumScript(seleniumFile, evaluator);
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }


            childCommands.execute(evaluator, resultRecorder);
            childCommands.verify(evaluator, resultRecorder);
        }


    }


    private class TableStrategy implements Strategy {


        public void execute(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
               TableSupport tableSupport = new TableSupport(commandCall, documentParser);
               Row[] detailRows = tableSupport.getDetailRows();
               for (Row detailRow : detailRows) {
                   commandCall.setElement(detailRow.getElement());
                   commandCall.setChildren(tableSupport.getCommandCallsFor(detailRow, commandCall.getResource()));
                   commandCall.execute(evaluator, resultRecorder);
               }
           }

    }
}
