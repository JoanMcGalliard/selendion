package org.selendion.internal.command;

import org.concordion.internal.command.AbstractCommand;
import org.concordion.internal.CommandCall;
import org.concordion.api.Evaluator;
import org.concordion.api.ResultRecorder;
import org.concordion.api.Resource;
import org.selendion.internal.util.TestDescription;
import org.selendion.internal.util.ExceptionThrowingSelendionTestCase;
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
            suite.add(new TestDescription(htmlFilename, getTitleOfPage(f.getAbsolutePath()), getClass(htmlResource) ));
        } else if (f.isDirectory()) {
            walk(htmlFilename, htmlResource);
        }
    }

    private void walk(String htmlFilename, String htmlResource) {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        File f = new File(contextClassLoader.getResource(htmlResource).getPath());
        if (f.isFile() && f.toString().matches(".*\\.html")) {
            suite.add(new TestDescription(htmlFilename, getTitleOfPage(f.getAbsolutePath()), getClass(htmlResource) ));

        }  else if (f.isDirectory()) {
            for (String sub : f.list()) {
                walk(htmlFilename.replaceFirst("/$", "") + "/" + sub, htmlResource.replaceFirst("/$", "") + "/" + sub);
            }
        }
    }
    @SuppressWarnings("unchecked")    
    private Class<? extends SelendionTestCase> getClass(String htmlResource) {
        String className = htmlResource;

        if (className.endsWith(".html")) {
            className = className.substring(0, className.length() - 5);
        }
        className = className.replace("/", ".");
        if (classes.get(className) != null) {
            return (Class<? extends SelendionTestCase>)classes.get(className);
        }

        TestCaseClassLoader loader = new TestCaseClassLoader();
        Class testClass;
        try {
            testClass = loader.loadClass(className + "Test");
        } catch (ClassNotFoundException e) {
            try {
                testClass = loader.loadClass(className);
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


        if (!subclassOfSelendionTestCase(testClass)) {



            ClassPool pool = ClassPool.getDefault();
            CtClass parent;
            try {
                parent = pool.getCtClass("org.selendion.integration.concordion.SelendionTestCase");
            } catch (NotFoundException e) {
                throw new RuntimeException("Can't find org.selendion.integration.concordion.SelendionTestCase");
            }
            try {
                try {
                    CtClass clazz = pool.getCtClass(testClass.getName());
                    if (subclassOfSelendionTestCase(clazz.toClass(loader, parent.toClass().getProtectionDomain()))) {

                }
                } catch (Exception e) {
                    //do nothing
                }
                CtClass newClass = pool.makeClass(testClass.getName(), parent);
                CtMethod method = CtNewMethod.make(CtClass.voidType,
                        "testProcessSpecification",
                        new CtClass[]{},
                        new CtClass[]{pool.getCtClass("java.lang.Throwable")},
                        "throw new Exception(this.getClass().toString() + \" is not a SelendionTestCase.\");",
                        newClass);
                newClass.addMethod(method);
                testClass = newClass.toClass();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        classes.put(className, testClass);
        return testClass;
    }

    private boolean subclassOfSelendionTestCase(Class testClass) {
        if (testClass == null) {
            return false;
        } else if (testClass.getName().equals("org.selendion.integration.concordion.SelendionTestCase")) {
            return true;
        } else {
            return subclassOfSelendionTestCase(testClass.getSuperclass());
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
