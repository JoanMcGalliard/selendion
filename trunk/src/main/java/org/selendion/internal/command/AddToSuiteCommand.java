package org.selendion.internal.command;

import org.concordion.internal.command.AbstractCommand;
import org.concordion.internal.CommandCall;
import org.concordion.api.Evaluator;
import org.concordion.api.ResultRecorder;
import org.concordion.integration.junit3.ConcordionTestCase;
import org.selendion.internal.command.pure_concordion.PureConcordionDemoTest;
import org.selendion.internal.command.pure_concordion.PureConcordionSelendionDemoTest;


import java.util.Vector;

import junit.runner.TestCaseClassLoader;


public class AddToSuiteCommand extends AbstractCommand {
    private Vector<Class<? extends ConcordionTestCase>> suite;

    public AddToSuiteCommand(Vector<Class<? extends ConcordionTestCase>> suite) {
        this.suite = suite;
    }

    public void execute(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
        String htmlFilename=commandCall.getResource().getRelativeResource(evaluator.evaluate(commandCall.getExpression()).toString()).getPath();

      suite.add(getClass(htmlFilename));

    }
    private Class<? extends ConcordionTestCase> getClass (String htmlFilename) {
                        String className = htmlFilename;

              if (className.startsWith("/")) {
                  className= className.substring(1,className.length());
              }               if (className.endsWith(".html")) {
                   className= className.substring(0,className.length()-5);
               }
               className = className.replace("/", ".");
        System.out.println(className);
       TestCaseClassLoader loader = new TestCaseClassLoader();
        Class<? extends ConcordionTestCase> x =null;
        try {
            x = (Class<? extends ConcordionTestCase>)loader.loadClass(className+"Test");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return x;

    }
            
}
