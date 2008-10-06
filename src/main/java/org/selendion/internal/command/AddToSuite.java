package org.selendion.internal.command;

import org.concordion.internal.command.AbstractCommand;
import org.concordion.internal.CommandCall;
import org.concordion.api.Evaluator;
import org.concordion.api.ResultRecorder;
import org.concordion.api.Resource;
import org.concordion.integration.junit3.ConcordionTestCase;
import org.selendion.integration.concordion.SelendionTestCase;


import java.lang.reflect.InvocationTargetException;
import java.io.File;

import javassist.Loader;
import com.sun.tools.javac.Main;
import junit.framework.TestSuite;
import junit.framework.TestCase;


public class AddToSuite extends AbstractCommand {
    static final String SUITE="#SUITE";
    public void execute(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {

        ConcordionTestCase testCase = loadClass(commandCall.getResource().getRelativeResource(evaluator.evaluate(commandCall.getExpression()).toString()).getPath());
        if (evaluator.getVariable(SUITE) == null ) {
            evaluator.setVariable(SUITE, new TestSuite());
        }
        TestSuite testSuite = (TestSuite) evaluator.getVariable(SUITE);
        TestSuite suite= new TestSuite();
        throw new RuntimeException("Implement me!");



    }
    private ConcordionTestCase loadClass(String htmlFilepath) {
/*
         We are looking for a class in the same package as htmlFilename with the basename + Test, or basename.  If neither exist,
         then we create a SelendionTestCase for that test.
*/
                String className = htmlFilepath;
               if (className.startsWith("/")) {
                   className= className.substring(1,className.length());
               }
                if (className.endsWith(".html")) {
                   className= className.substring(0,className.length()-5);
               }
                className = className.replace("/", ".");
        Loader loader = new Loader();
        // First try to load the *Test class
        ConcordionTestCase test = null;
        try {
            test = (ConcordionTestCase)loader.loadClass(className+"Test").newInstance();
        } catch (Exception e) {
            //Then try to load the class without the Test extension
            try {
                test = (ConcordionTestCase)loader.loadClass(className+"Test").newInstance();
            } catch (Exception e2) {
                // Can't do it?  build the class from scratch
                String packageName = className.replaceFirst("\\.[^.]*$", "");
                String baseName = className.replaceFirst(".*\\.", "")+"Test";
                String code = String.format("package %s;\n" +
                        "import org.selendion.integration.concordion.SelendionTestCase;\n" +
                        "public class %sTest extends SelendionTestCase {\n" +
                        "}", packageName, baseName);
               throw new RuntimeException("Implement me!");
            }


        }
    return null;
    }
            
}
