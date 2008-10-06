/*
        Copyright Joan McGalliard, 2008
*/

package org.selendion.internal;

import org.concordion.internal.ConcordionBuilder;
import org.concordion.internal.command.*;
import org.selendion.internal.command.*;
import org.selendion.internal.util.SeleniumIdeReader;


public class SelendionBuilder extends ConcordionBuilder {
    public static final String NAMESPACE_SELENDION = "http://www.selendion.org/2008";

    private SeleniumIdeReader seleniumIdeReader = new SeleniumIdeReader();
    private StartSeleniumCommand startSeleniumCommand = new StartSeleniumCommand(seleniumIdeReader);
    private RunSeleniumCommand runSeleniumCommand = new RunSeleniumCommand(seleniumIdeReader);
    private StopSeleniumCommand stopSeleniumCommand = new StopSeleniumCommand(seleniumIdeReader);
    private AddToSuite addToSuite = new AddToSuite();
    private RunSuite runSuite = new RunSuite();
    {
        withApprovedCommand(NAMESPACE_SELENDION, "run", runCommand);
        withApprovedCommand(NAMESPACE_SELENDION, "execute", executeCommand);
        withApprovedCommand(NAMESPACE_SELENDION, "set", new SetCommand());
        withApprovedCommand(NAMESPACE_SELENDION, "assertEquals", assertEqualsCommand);
        withApprovedCommand(NAMESPACE_SELENDION, "assertTrue", assertTrueCommand);
        withApprovedCommand(NAMESPACE_SELENDION, "assertFalse", assertFalseCommand);
        withApprovedCommand(NAMESPACE_SELENDION, "verifyRows", verifyRowsCommand);
        withApprovedCommand(NAMESPACE_SELENDION, "echo", echoCommand);
       
        withApprovedCommand(NAMESPACE_SELENDION, "startSelenium", startSeleniumCommand);
        withApprovedCommand(NAMESPACE_SELENDION, "runSelenium", runSeleniumCommand);
        withApprovedCommand(NAMESPACE_SELENDION, "stopSelenium", stopSeleniumCommand);
        withApprovedCommand(NAMESPACE_SELENDION, "addToSuite", addToSuite);
        withApprovedCommand(NAMESPACE_SELENDION, "runSuite", runSuite);
    }
}
