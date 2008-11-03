/*
        Copyright Joan McGalliard, 2008
*/

package org.selendion.internal.command;

import org.concordion.internal.command.AbstractCommand;
import org.concordion.internal.command.VerifyRowsListener;
import org.concordion.internal.util.Check;
import org.concordion.internal.util.Announcer;
import org.concordion.internal.*;
import org.concordion.api.*;
import org.concordion.integration.junit3.ConcordionTestCase;
import org.selendion.internal.util.TestDescription;
import org.selendion.internal.util.SelendionClassFinder;
import org.selendion.integration.concordion.SelendionTestCase;
import org.junit.Test;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.net.URL;
import java.io.File;

import junit.framework.TestResult;
import junit.framework.TestCase;

public class RunSelendionCommand extends AbstractCommand {


    public void execute(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
        String htmlFilename = evaluator.evaluate(commandCall.getExpression()).toString();
        String htmlResource=commandCall.getResource().getRelativeResource(htmlFilename)
                .getPath().replaceFirst("^/", "");
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        URL url = contextClassLoader.getResource(htmlResource);
        if (url == null) {
            throw new RuntimeException(String.format("Can't run %s: file does not exist.", htmlFilename));
        }
        File f = new File(contextClassLoader.getResource(htmlResource).getPath());
        if (f.isFile()) {
            try {
                Class clazz = SelendionClassFinder.findSelendionClass(htmlResource);
               SelendionTestCase test =  (SelendionTestCase) clazz.newInstance();
                clazz.getMethod("testProcessSpecification", Evaluator.class).invoke(test, evaluator);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
//            test.getEvalutor();
//            TestResult result = test.run();
//            System.out.println(result);
        }  else {
            throw new RuntimeException(String.format("Can't run %s: ", htmlFilename));
        }
    }

    private void transferValues(Evaluator evaluator1, Evaluator evaluator2) {
        Set keys = evaluator1.getKeys();
        for (Object key : keys) {
            evaluator2.setVariable((String) key, evaluator1.getVariable("#" + key));
        }

    }
}