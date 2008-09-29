/*
        Copyright Joan McGalliard, 2008
*/

package org.selendion.internal.command;

import org.concordion.internal.command.AbstractCommand;
import org.concordion.internal.CommandCall;
import org.concordion.api.Evaluator;
import org.concordion.api.ResultRecorder;
import org.selendion.internal.util.SeleniumIdeReader;

public class StartSeleniumCommand extends AbstractCommand {
    private SeleniumIdeReader seleniumIdeReader;

    public StartSeleniumCommand(SeleniumIdeReader seleniumIdeReader) {
        this.seleniumIdeReader=seleniumIdeReader;
    }
   
    @Override
    public void setUp(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
        seleniumIdeReader.start("localhost", 4444, "*firefox", "http://mcgalliard.org");

    }
}
