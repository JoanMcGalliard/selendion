/*
	Copyright Joan McGalliard, 2008-9
*/

package org.selendion;

import org.concordion.Concordion;
import org.concordion.internal.SummarizingResultRecorder;
import org.concordion.api.*;
import org.selendion.integration.concordion.SelendionTestCase;

import java.io.IOException;
import java.util.Set;

public class Selendion extends Concordion {
    private final SpecificationLocator specificationLocator;
    private final EvaluatorFactory evaluatorFactory;
    private final SpecificationReader specificationReader;
    private Evaluator parentEvaluator;


    public static void main(String[] args) throws Exception {
        // parse parameters
        boolean startSelenium = false;
        int port = 4444;
        int threads = 1;
        String tests = "";
        String host = "";
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equals("-startSelenium")) {
                startSelenium = true;
            } else if (arg.equals("-port") && ++i < args.length) {
                port = Integer.parseInt(args[i]);
            } else if (arg.equals("-host") && ++i < args.length) {
                host = args[i];
            } else if (arg.equals("-threads") && ++i < args.length) {
                threads = Integer.parseInt(args[i]);
            } else if (arg.equals("-runTests") && ++i < args.length) {
                tests = args[i];
            } else {
                throw new Exception("Unknown parameter " + arg);
            }
        }
        if (startSelenium && tests.length() > 0) {
            throw new Exception("Running tests and starting selenium from the command line not supported YET.");
        }
        if (startSelenium && tests.length() == 0) {
            org.openqa.selenium.server.SeleniumServer.main(new String[]{"-port", Integer.toString(port),
                    "-host", host});
        } else if (tests.length() > 0) {
            SelendionTestCase testCase = new MainSelendionTestCase();
            for (String test : tests.split(",")) {

            }
        }
    }


    public Selendion(SpecificationLocator specificationLocator,
                     SpecificationReader specificationReader,
                     EvaluatorFactory evaluatorFactory,
                     Evaluator parentEvaluator) {
        super(specificationLocator, specificationReader, evaluatorFactory);
        this.specificationLocator = specificationLocator;
        this.specificationReader = specificationReader;
        this.evaluatorFactory = evaluatorFactory;
        this.parentEvaluator = parentEvaluator;
    }

    public ResultSummary process(Object fixture) throws IOException {
        return process(specificationLocator.locateSpecification(fixture), fixture);
    }

    public ResultSummary process(Resource resource, Object fixture) throws IOException {
        Specification specification = specificationReader.readSpecification(resource);
        SummarizingResultRecorder resultRecorder = new SummarizingResultRecorder();
        Evaluator evaluator = evaluatorFactory.createEvaluator(fixture);
        if (this.parentEvaluator != null) {
            transferValues(this.parentEvaluator, evaluator);
        }
        specification.process(evaluator, resultRecorder);
        if (this.parentEvaluator != null) {
            transferValues(evaluator, this.parentEvaluator);           
        }

        return resultRecorder;
    }

    private void transferValues(Evaluator evaluator1, Evaluator evaluator2) {
        Set keys = evaluator1.getKeys();
        for (Object key : keys) {
            evaluator2.setVariable("#" + key, evaluator1.getVariable("#" + key));
        }

    }

}
