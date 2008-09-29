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
    private SeleniumIdeReader seleniumIdeReader = new SeleniumIdeReader();
    private StartSeleniumCommand startSeleniumCommand = new StartSeleniumCommand(seleniumIdeReader);
    private RunSeleniumCommand runSeleniumCommand = new RunSeleniumCommand(documentParser, seleniumIdeReader);
    private StopSeleniumCommand stopSeleniumCommand = new StopSeleniumCommand(seleniumIdeReader);
    {
        withApprovedCommand(NAMESPACE_CONCORDION_2007, "startSelenium",  startSeleniumCommand);
        withApprovedCommand(NAMESPACE_CONCORDION_2007, "runSelenium",  runSeleniumCommand);
        withApprovedCommand(NAMESPACE_CONCORDION_2007, "stopSelenium",  stopSeleniumCommand);

    }
    

}
