/*
        Copyright Joan McGalliard, 2008
*/

package org.selendion.internal;

import org.concordion.internal.ConcordionBuilder;
import org.selendion.internal.command.RunSeleniumCommand;
import org.selendion.internal.command.StopSeleniumCommand;
import org.selendion.internal.command.StartSeleniumCommand;
import org.selendion.internal.util.SeleniumIdeReader;


public class SelendionBuilder extends ConcordionBuilder {
    public static final String NAMESPACE_SELENDION = "http://www.selendion.org/2008";

    private SeleniumIdeReader seleniumIdeReader = new SeleniumIdeReader();
    private StartSeleniumCommand startSeleniumCommand = new StartSeleniumCommand(seleniumIdeReader);
    private RunSeleniumCommand runSeleniumCommand = new RunSeleniumCommand(documentParser, seleniumIdeReader);
    private StopSeleniumCommand stopSeleniumCommand = new StopSeleniumCommand(seleniumIdeReader);

    {
        withApprovedCommand(NAMESPACE_SELENDION, "execute", executeCommand);
        withApprovedCommand(NAMESPACE_SELENDION, "set", setCommand);
        withApprovedCommand(NAMESPACE_SELENDION, "assertEquals", assertEqualsCommand);
        withApprovedCommand(NAMESPACE_SELENDION, "verifyRows", verifyRowsCommand);
        withApprovedCommand(NAMESPACE_SELENDION, "assertFalse", assertFalseCommand);
//        withApprovedCommand(NAMESPACE_SELENDION, "assertTrue", assertTrueCommand);

        withApprovedCommand(NAMESPACE_SELENDION, "startSelenium", startSeleniumCommand);
        withApprovedCommand(NAMESPACE_SELENDION, "runSelenium", runSeleniumCommand);
        withApprovedCommand(NAMESPACE_SELENDION, "stopSelenium", stopSeleniumCommand);

    }
}
