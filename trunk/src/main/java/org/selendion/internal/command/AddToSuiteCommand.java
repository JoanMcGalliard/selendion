package org.selendion.internal.command;

import org.concordion.internal.command.AbstractCommand;
import org.concordion.internal.CommandCall;
import org.concordion.api.Evaluator;
import org.concordion.api.ResultRecorder;
import org.concordion.integration.junit3.ConcordionTestCase;
import org.selendion.internal.util.TestDescription;


import java.util.Vector;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;

import junit.runner.TestCaseClassLoader;
import nu.xom.Document;
import nu.xom.Builder;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.CannotCompileException;


public class AddToSuiteCommand extends AbstractCommand {
    private Vector<TestDescription> suite;

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
            suite.add(new TestDescription(htmlFilename, getTitleOfPage(htmlFilename), getClass(htmlResource) ));
        } else if (f.isDirectory()) {
            walk(htmlFilename, htmlResource);
        }
    }

    private void walk(String htmlFilename, String htmlResource) {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        File f = new File(contextClassLoader.getResource(htmlResource).getPath());
        if (f.isFile() && f.toString().matches(".*\\.html")) {
            suite.add(new TestDescription(htmlFilename, getTitleOfPage(htmlResource), getClass(htmlResource) ));

        }  else if (f.isDirectory()) {
            for (String sub : f.list()) {
                walk(htmlFilename.replaceFirst("/$", "") + "/" + sub, htmlResource.replaceFirst("/$", "") + "/" + sub);
            }
        }
    }
    private Class<? extends ConcordionTestCase> getClass(String htmlResource) {
        String className = htmlResource;

        if (className.endsWith(".html")) {
            className = className.substring(0, className.length() - 5);
        }
        className = className.replace("/", ".");
        TestCaseClassLoader loader = new TestCaseClassLoader();
        Class<? extends ConcordionTestCase> testClass = null;
        try {
            testClass = (Class<? extends ConcordionTestCase>) loader.loadClass(className + "Test");
        } catch (ClassNotFoundException e) {
            try {
                testClass = (Class<? extends ConcordionTestCase>) loader.loadClass(className);
            } catch (ClassNotFoundException e1) {
                ClassPool pool = ClassPool.getDefault();
                CtClass parent;
                try {
                    parent = pool.getCtClass("org.selendion.integration.concordion.SelendionTestCase");
                } catch (NotFoundException e2) {
                    throw new RuntimeException("Can't find org.selendion.integration.concordion.SelendionTestCase",e);
                }
                try {
                    testClass = pool.makeClass(className, parent).toClass();
                } catch (CannotCompileException e2) {
                    throw new RuntimeException(e);
                }
            }
        }
        return testClass;
    }
    private String getTitleOfPage(String test)  {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        String title;
        try {
            Document doc = new Builder().build(new InputStreamReader(contextClassLoader.getResourceAsStream(test)));
             title = doc.query("//title").get(0).getValue();
        } catch (Exception e) {
            title="";
        }

        if (title.length() > 0) {
            return title;
        } else {
            return test.replaceFirst(".*/", "");
        }
    }



}
