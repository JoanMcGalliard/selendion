package org.selendion.internal.command;

import org.concordion.internal.command.AbstractCommand;
import org.concordion.internal.CommandCall;
import org.concordion.api.Evaluator;
import org.concordion.api.ResultRecorder;
import org.concordion.api.Resource;
import org.selendion.internal.util.TestDescription;
import org.selendion.internal.util.ExceptionThrowingSelendionTestCase;
import org.selendion.internal.util.SelendionClassFinder;
import org.selendion.integration.concordion.SelendionTestCase;


import java.util.Vector;
import java.util.Hashtable;
import java.io.File;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.net.URL;

import junit.runner.TestCaseClassLoader;
import nu.xom.Document;
import nu.xom.Builder;
import javassist.*;


public class AddToSuiteCommand extends AbstractCommand {
    private Vector<TestDescription> suite;
    private static Hashtable classes = new Hashtable();


    public AddToSuiteCommand(Vector<TestDescription> suite) {
        this.suite = suite;
    }

    public void execute(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
        String htmlFilename = evaluator.evaluate(commandCall.getExpression()).toString();
        String htmlResource=commandCall.getResource().getRelativeResource(htmlFilename)
                .getPath().replaceFirst("^/", "");
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        URL url = contextClassLoader.getResource(htmlResource);
        if (url == null) {
            throw new RuntimeException(String.format("Can't add %s to suite: file does not exist.", htmlFilename));
        }
        File f = new File(contextClassLoader.getResource(htmlResource).getPath());
        if (f.isFile()) {
            suite.add(new TestDescription(htmlFilename, getTitleOfPage(f.getAbsolutePath()), SelendionClassFinder.findSelendionClass(htmlResource) ));
        } else if (f.isDirectory()) {
            walk(htmlFilename, htmlResource);
        }
    }

    private void walk(String htmlFilename, String htmlResource) {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        File f = new File(contextClassLoader.getResource(htmlResource).getPath());
        if (f.isFile() && f.toString().matches(".*\\.html")) {
            suite.add(new TestDescription(htmlFilename, getTitleOfPage(f.getAbsolutePath()), SelendionClassFinder.findSelendionClass(htmlResource) ));

        }  else if (f.isDirectory()) {
            for (String sub : f.list()) {
                walk(htmlFilename.replaceFirst("/$", "") + "/" + sub, htmlResource.replaceFirst("/$", "") + "/" + sub);
            }
        }
    }


    private String getTitleOfPage(String path)  {
        String title;
        try {

            Document doc = new Builder().build(new FileInputStream(path));
             title = doc.query("//title").get(0).getValue();
        } catch (Exception e) {
            title="";
        }

        if (title.length() > 0) {
            return title;
        } else {
            return path.replaceFirst(".*/", "");
        }
    }



}
