package org.selendion;

import org.concordion.Concordion;
import org.concordion.internal.SummarizingResultRecorder;
import org.concordion.api.*;
import org.selendion.integration.concordion.SelendionTestCase;

import java.io.IOException;

public class Selendion extends Concordion {
    private final SpecificationLocator specificationLocator;
       private final EvaluatorFactory evaluatorFactory;
       private final SpecificationReader specificationReader;
    private Evaluator evaluator;



    public static void main(String[] args) throws Exception {
        // parse parameters
        boolean startSelenium = false;
        int port=4444;
        int threads=1;
        String tests="";
        String host="";
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
            throw new Exception ("Running tests and starting selenium from the command line not supported YET.");
        }
        if (startSelenium && tests.length() == 0) {
            org.openqa.selenium.server.SeleniumServer.main(new String[]{"-port", Integer.toString(port),
            "-host", host});
        } else if (tests.length() > 0) {
            SelendionTestCase testCase=new MainSelendionTestCase();
            for (String test : tests.split(",")) {

            }
        }
    }


    public Selendion(SpecificationLocator specificationLocator, SpecificationReader specificationReader, EvaluatorFactory evaluatorFactory) {
        super(specificationLocator, specificationReader,  evaluatorFactory);
        this.specificationLocator = specificationLocator;
        this.specificationReader = specificationReader;
        this.evaluatorFactory = evaluatorFactory;    
        this.evaluator = null;
    }
    public Selendion(SpecificationLocator specificationLocator, SpecificationReader specificationReader, Evaluator evaluator) {

        super(specificationLocator, specificationReader,  null);
        this.specificationLocator = specificationLocator;
        this.specificationReader = specificationReader;
        this.evaluatorFactory = null;
        this.evaluator = evaluator;
    }
    public ResultSummary process(Object fixture) throws IOException {
        return process(specificationLocator.locateSpecification(fixture), fixture);
    }

    public ResultSummary process(Resource resource, Object fixture) throws IOException {
        Specification specification = specificationReader.readSpecification(resource);
        SummarizingResultRecorder resultRecorder = new SummarizingResultRecorder();
        if (evaluator == null ) {
            evaluator  = evaluatorFactory.createEvaluator(fixture);
        }
        specification.process(evaluator, resultRecorder);
        return resultRecorder;
    }
    public Evaluator getEvaluator() {
        return evaluator;
    }
}
