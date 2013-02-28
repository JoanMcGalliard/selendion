/*
	Copyright Joan McGalliard, 2008-2013
*/

package org.selendion.internal.command;

import org.concordion.internal.command.AbstractCommand;
import org.concordion.internal.CommandCall;
import org.concordion.api.Evaluator;
import org.concordion.api.ResultRecorder;
import org.selendion.internal.util.SeleniumIdeReader;


public class SwitchJavaScriptCommand extends AbstractCommand {

     private final SeleniumIdeReader seleniumIdeReader;

    public SwitchJavaScriptCommand(SeleniumIdeReader seleniumIdeReader) {
        this.seleniumIdeReader = seleniumIdeReader;

    }

    public void execute(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
        String command = evaluator.evaluate(commandCall.getExpression()).toString();
        if (command.equalsIgnoreCase("off")) {
            this.seleniumIdeReader.setJavaScriptEnabled(false);

        } else if (command.equalsIgnoreCase("on")) {
            this.seleniumIdeReader.setJavaScriptEnabled(true);
        } else {
            throw new RuntimeException(String.format("Unknown parameter %s to SwitchJavaScript. Should be \"on\" or \"off\".", command));
        }
    }

}
