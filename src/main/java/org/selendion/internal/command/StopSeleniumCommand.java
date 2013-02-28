/*
        Copyright Joan McGalliard, 2008-2013
*/

package org.selendion.internal.command;

import org.concordion.internal.command.AbstractCommand;
import org.concordion.internal.CommandCall;
import org.concordion.api.Evaluator;
import org.concordion.api.ResultRecorder;
import org.selendion.internal.util.SeleniumIdeReader;

public class StopSeleniumCommand extends AbstractCommand {
    private final SeleniumIdeReader seleniumIdeReader;

    public StopSeleniumCommand(SeleniumIdeReader seleniumIdeReader) {
        this.seleniumIdeReader=seleniumIdeReader;

    }

    @Override
    public void setUp(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
        seleniumIdeReader.stop();

    }
}
