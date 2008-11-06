/*
        Copyright Joan McGalliard, 2008
*/

package org.selendion.internal.command;

import org.concordion.internal.command.AbstractCommand;
import org.concordion.internal.*;
import org.concordion.api.*;
import org.selendion.internal.util.SelendionClassLoader;
import org.selendion.integration.concordion.SelendionTestCase;

import java.util.Set;
import java.net.URL;
import java.io.File;

public class RunSelendionCommand extends AbstractCommand {


    public void execute(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
        String htmlFilename = evaluator.evaluate(commandCall.getExpression()).toString();
        String htmlResource=commandCall.getResource().getRelativeResource(htmlFilename)
                .getPath().replaceFirst("^/", "");
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        SelendionClassLoader loader = new SelendionClassLoader();
        URL url = contextClassLoader.getResource(htmlResource);
        if (url == null) {
            throw new RuntimeException(String.format("Can't run %s: file does not exist.", htmlFilename));
        }
        File f = new File(contextClassLoader.getResource(htmlResource).getPath().replaceAll("%20", " "));
        if (f.isFile()) {
            try {
                Class clazz = loader.findSelendionClass(htmlResource);
               SelendionTestCase test =  (SelendionTestCase) clazz.newInstance();
                clazz.getMethod("testProcessSpecification", Evaluator.class).invoke(test, evaluator);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }  else {
            throw new RuntimeException(String.format("Can't open %s", htmlFilename));
        }
    }

}