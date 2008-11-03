/*
        Copyright Joan McGalliard, 2008
*/

package org.selendion.internal;

import org.concordion.internal.*;
import org.concordion.api.SpecificationReader;
import org.concordion.api.EvaluatorFactory;
import org.concordion.internal.listener.*;
import org.concordion.internal.util.IOUtil;
import org.concordion.internal.command.*;
import org.selendion.internal.command.*;
import org.selendion.internal.util.SeleniumIdeReader;
import org.selendion.internal.util.TestDescription;
import org.selendion.internal.listener.RunSuiteResultRenderer;
import org.selendion.internal.listener.RunSeleniumResultRenderer;
import org.selendion.Selendion;

import java.util.Vector;
import java.io.File;


public class SelendionBuilder extends ConcordionBuilder {
    public static final String NAMESPACE_SELENDION = "http://www.selendion.org/2008";
    private static final String PROPERTY_OUTPUT_DIR = "selendion.output.dir";
    private File baseOutputDir;
    private SeleniumIdeReader seleniumIdeReader = new SeleniumIdeReader();
    private Vector<TestDescription> suite = new Vector<TestDescription>();
    private static final String EMBEDDED_STYLESHEET_RESOURCE = "/org/selendion/internal/resource/embedded.css";

    private StartSeleniumCommand startSeleniumCommand = new StartSeleniumCommand(seleniumIdeReader);
    private RunSeleniumCommand runSeleniumCommand = new RunSeleniumCommand(seleniumIdeReader);
    private StopSeleniumCommand stopSeleniumCommand = new StopSeleniumCommand(seleniumIdeReader);
    private SpecificationCommand specificationCommand = new SpecificationCommand();    
    private AddToSuiteCommand addToSuiteCommand = new AddToSuiteCommand(suite);
    private RunSuiteCommand runSuiteCommand = new RunSuiteCommand(suite);
    private ClearSuiteCommand clearSuiteCommand = new ClearSuiteCommand(suite);
    private ForEachCommand forEachCommand = new ForEachCommand(documentParser);
    private RunSelendionCommand runSelendionCommand = new RunSelendionCommand();
    {
        withApprovedCommand("", "specification", specificationCommand);
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
        withApprovedCommand(NAMESPACE_SELENDION, "clearSuite", clearSuiteCommand);
        withApprovedCommand(NAMESPACE_SELENDION, "forEach", forEachCommand);
        withApprovedCommand(NAMESPACE_SELENDION, "runConcordion", runSelendionCommand);

        runSuiteCommand.addRunSuiteListener(new RunSuiteResultRenderer());
        runSeleniumCommand.addRunSeleniumListener(new RunSeleniumResultRenderer());
        documentParser = new DocumentParser(commandRegistry);
        documentParser.addDocumentParsingListener(new DocumentStructureImprover());
        String stylesheetContent = IOUtil.readResourceAsString(EMBEDDED_STYLESHEET_RESOURCE);
        documentParser.addDocumentParsingListener(new StylesheetEmbedder(stylesheetContent));

    }
    //@Override
    public Selendion build() {
        if (target == null) {
            target = new FileTarget(getBaseOutputDir());
        }
        XMLParser xmlParser = new XMLParser();

        specificationCommand.addSpecificationListener(new BreadcrumbRenderer(source, xmlParser));
        specificationCommand.addSpecificationListener(new PageFooterRenderer(target));
        specificationCommand.addSpecificationListener(new SpecificationExporter(target));

        SpecificationReader specificationReader = new XMLSpecificationReader(source, xmlParser, documentParser);

        return new Selendion (specificationLocator, specificationReader, evaluatorFactory);
    }
    protected File getBaseOutputDir() {
        if (baseOutputDir != null) {
            return baseOutputDir;
        }
        String outputPath = System.getProperty(PROPERTY_OUTPUT_DIR);
        if (outputPath == null) {
            return new File(System.getProperty("java.io.tmpdir"), "selendion");
        }
        return new File(outputPath);
    }
    public SelendionBuilder withEvaluatorFactory(EvaluatorFactory evaluatorFactory) {
        return (SelendionBuilder) super.withEvaluatorFactory(evaluatorFactory);
    }


    

}
