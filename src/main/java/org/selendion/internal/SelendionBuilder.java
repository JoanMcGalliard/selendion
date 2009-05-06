/*
        Copyright Joan McGalliard, 2008-9
*/

package org.selendion.internal;

import org.concordion.internal.*;
import org.concordion.api.*;
import org.concordion.internal.listener.*;
import org.concordion.internal.util.IOUtil;
import org.concordion.internal.command.*;
import org.selendion.internal.command.*;
import org.selendion.internal.command.AssertEqualsCommand;
import org.selendion.internal.command.SetCommand;
import org.selendion.internal.util.SeleniumIdeReader;
import org.selendion.internal.listener.RunSuiteResultRenderer;
import org.selendion.internal.listener.RunSeleniumResultRenderer;
import org.selendion.internal.listener.RunSelendionResultRenderer;
import org.selendion.Selendion;
import org.selendion.integration.BrowserDriver;
import org.selendion.integration.concordion.SelendionTestCase;

import java.util.Hashtable;
import java.io.File;



public class SelendionBuilder extends ConcordionBuilder {
    public static final String NAMESPACE_SELENDION = "http://www.selendion.org/2008";
    private static final String PROPERTY_OUTPUT_DIR = "selendion.output.dir";
    private SeleniumIdeReader seleniumIdeReader = new SeleniumIdeReader();

    private Hashtable suites = new Hashtable();
    private static final String EMBEDDED_STYLESHEET_RESOURCE = "/org/selendion/internal/resource/embedded.css";
    private Evaluator evaluator;

    private AssertEqualsCommand assertEqualsCommand = new AssertEqualsCommand();

    private StartBrowserCommand startBrowserCommand = new StartBrowserCommand(seleniumIdeReader);
    private RunSeleniumCommand runSeleniumCommand = new RunSeleniumCommand(seleniumIdeReader);
    private StopSeleniumCommand stopSeleniumCommand = new StopSeleniumCommand(seleniumIdeReader);
    private SpecificationCommand specificationCommand = new SpecificationCommand();
    private AddToSuiteCommand addToSuiteCommand = new AddToSuiteCommand(suites);
    private RunSuiteCommand runSuiteCommand = new RunSuiteCommand(suites);
    private ClearSuiteCommand clearSuiteCommand = new ClearSuiteCommand(suites);
    private ForEachCommand forEachCommand = new ForEachCommand(documentParser);
    private RunSelendionCommand runSelendionCommand = new RunSelendionCommand(seleniumIdeReader);
    private SwitchJavaScriptCommand switchJavaScriptCommand = new SwitchJavaScriptCommand(seleniumIdeReader);

    public SelendionBuilder() {
    }

    {
        withApprovedCommand("", "specification", specificationCommand);
        withApprovedCommand(NAMESPACE_SELENDION, "run", runCommand);
        withApprovedCommand(NAMESPACE_SELENDION, "execute", executeCommand);
        withApprovedCommand(NAMESPACE_SELENDION, "set", new SetCommand());
        withApprovedCommand(NAMESPACE_SELENDION, "assertTrue", assertTrueCommand);
        withApprovedCommand(NAMESPACE_SELENDION, "assertFalse", assertFalseCommand);
        withApprovedCommand(NAMESPACE_SELENDION, "verifyRows", verifyRowsCommand);
        withApprovedCommand(NAMESPACE_SELENDION, "echo", echoCommand);

        withApprovedCommand(NAMESPACE_SELENDION, "assertEquals", assertEqualsCommand);
        
       
        withApprovedCommand(NAMESPACE_SELENDION, "startBrowser", startBrowserCommand);
        withApprovedCommand(NAMESPACE_SELENDION, "runSelenium", runSeleniumCommand);
        withApprovedCommand(NAMESPACE_SELENDION, "stopBrowser", stopSeleniumCommand);
        withApprovedCommand(NAMESPACE_SELENDION, "addToSuite", addToSuiteCommand);
        withApprovedCommand(NAMESPACE_SELENDION, "runSuite", runSuiteCommand);
        withApprovedCommand(NAMESPACE_SELENDION, "clearSuite", clearSuiteCommand);
        withApprovedCommand(NAMESPACE_SELENDION, "forEach", forEachCommand);
        withApprovedCommand(NAMESPACE_SELENDION, "runSelendion", runSelendionCommand);
        withApprovedCommand(NAMESPACE_SELENDION, "switchJavaScript", switchJavaScriptCommand);

        assertEqualsCommand.addAssertEqualsListener(new AssertEqualsResultRenderer());
        runSuiteCommand.addRunSuiteListener(new RunSuiteResultRenderer());
        runSeleniumCommand.addRunSeleniumListener(new RunSeleniumResultRenderer());
        runSelendionCommand.addRunSelendionListener(new RunSelendionResultRenderer());
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

        return new Selendion (specificationLocator, specificationReader, evaluatorFactory, evaluator);
    }
    protected File getBaseOutputDir() {
        String outputPath = System.getProperty(PROPERTY_OUTPUT_DIR);
        if (outputPath == null) {
            return new File(System.getProperty("java.io.tmpdir"), "selendion");
        }
        return new File(outputPath);
    }
    public SelendionBuilder withEvaluatorFactory(EvaluatorFactory evaluatorFactory) {
        return (SelendionBuilder) super.withEvaluatorFactory(evaluatorFactory);
    }
    public SelendionBuilder withEvaluator(Evaluator evaluator) {
        this.evaluator = evaluator;
        return this;
    }
    public SelendionBuilder withBrowser(BrowserDriver browser) {
        this.seleniumIdeReader.setBrowser(browser);
        return this;
    }

    public SelendionBuilder withAssertEqualsListener(AssertEqualsListener listener) {
        assertEqualsCommand.addAssertEqualsListener(listener);
        return this;
    }
    public SelendionBuilder withThrowableListener(ThrowableCaughtListener throwableListener) {
        super.withThrowableListener(throwableListener);
        return this;
    }

    public SelendionBuilder withSource(Source source) {
        this.source = source;
        return this;
    }
    public SelendionBuilder withTarget(Target target) {
        super.withTarget(target);
        return this;
    }


    public SelendionBuilder withBaseClass(Class<? extends SelendionTestCase> aClass) {
        addToSuiteCommand.setBaseClass(aClass);
        runSelendionCommand.setBaseClass(aClass);
        return this;
    }
}
