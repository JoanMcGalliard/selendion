/*
        Copyright Joan McGalliard, 2008
*/

package org.selendion.internal;

import org.concordion.internal.ConcordionBuilder;
import org.concordion.internal.DocumentParser;
import org.concordion.internal.listener.StylesheetEmbedder;
import org.concordion.internal.listener.DocumentStructureImprover;
import org.concordion.internal.util.IOUtil;
import org.concordion.internal.command.*;
import org.selendion.internal.command.*;
import org.selendion.internal.util.SeleniumIdeReader;
import org.selendion.internal.util.TestDescription;
import org.selendion.internal.listener.RunSuiteResultRenderer;
import org.selendion.internal.listener.RunSeleniumResultRenderer;

import java.util.Vector;


public class SelendionBuilder extends ConcordionBuilder {
    public static final String NAMESPACE_SELENDION = "http://www.selendion.org/2008";

    private SeleniumIdeReader seleniumIdeReader = new SeleniumIdeReader();
    private Vector<TestDescription> suite = new Vector<TestDescription>();
    private static final String EMBEDDED_STYLESHEET_RESOURCE = "/org/selendion/internal/resource/embedded.css";

    private StartSeleniumCommand startSeleniumCommand = new StartSeleniumCommand(seleniumIdeReader);
    private RunSeleniumCommand runSeleniumCommand = new RunSeleniumCommand(seleniumIdeReader);
    private StopSeleniumCommand stopSeleniumCommand = new StopSeleniumCommand(seleniumIdeReader);
    private AddToSuiteCommand addToSuiteCommand = new AddToSuiteCommand(suite);
    private RunSuiteCommand runSuiteCommand = new RunSuiteCommand(suite);
    {
        withApprovedCommand(NAMESPACE_SELENDION, "run", runCommand);
        withApprovedCommand(NAMESPACE_SELENDION, "execute", executeCommand);
        withApprovedCommand(NAMESPACE_SELENDION, "set", new SetCommand());
        withApprovedCommand(NAMESPACE_SELENDION, "assertEquals", assertEqualsCommand);
        withApprovedCommand(NAMESPACE_SELENDION, "assertTrue", assertTrueCommand);
        withApprovedCommand(NAMESPACE_SELENDION, "assertFalse", assertFalseCommand);
        withApprovedCommand(NAMESPACE_SELENDION, "verifyRows", verifyRowsCommand);
        withApprovedCommand(NAMESPACE_SELENDION, "echo", echoCommand);
       
        withApprovedCommand(NAMESPACE_SELENDION, "startBrowser", startSeleniumCommand);
        withApprovedCommand(NAMESPACE_SELENDION, "runSelenium", runSeleniumCommand);
        withApprovedCommand(NAMESPACE_SELENDION, "stopBrowser", stopSeleniumCommand);
        withApprovedCommand(NAMESPACE_SELENDION, "addToSuite", addToSuiteCommand);
        withApprovedCommand(NAMESPACE_SELENDION, "runSuite", runSuiteCommand);

        runSuiteCommand.addRunSuiteListener(new RunSuiteResultRenderer());
        runSeleniumCommand.addRunSeleniumListener(new RunSeleniumResultRenderer());
        documentParser = new DocumentParser(commandRegistry);
        documentParser.addDocumentParsingListener(new DocumentStructureImprover());
        String stylesheetContent = IOUtil.readResourceAsString(EMBEDDED_STYLESHEET_RESOURCE);
        documentParser.addDocumentParsingListener(new StylesheetEmbedder(stylesheetContent));

    }
}
