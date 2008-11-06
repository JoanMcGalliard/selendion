package org.selendion.internal.command;

import org.concordion.internal.command.AbstractCommand;
import org.concordion.internal.CommandCall;
import org.concordion.api.Evaluator;
import org.concordion.api.ResultRecorder;
import org.selendion.internal.util.TestDescription;
import org.selendion.internal.util.SelendionClassLoader;


import java.util.Vector;
import java.util.Hashtable;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;

import nu.xom.Document;
import nu.xom.Builder;


public class AddToSuiteCommand extends AbstractCommand {
    private Vector<TestDescription> suite;
    private static Hashtable classes = new Hashtable();
    private SelendionClassLoader loader = new SelendionClassLoader();


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
        File f = new File(contextClassLoader.getResource(htmlResource).getPath().replaceAll("%20", " "));
        if (f.isFile()) {
            suite.add(new TestDescription(htmlFilename, getTitleOfPage(f.getAbsolutePath()), loader.findSelendionClass(htmlResource),evaluator ));
        } else if (f.isDirectory()) {
            walk(htmlFilename, htmlResource, evaluator);
        } else {
            throw new RuntimeException(String.format("Can't add %s to suite: it is not a file or directory", htmlFilename));

        }
    }

    private void walk(String htmlFilename, String htmlResource, Evaluator evaluator) {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        File f = new File(contextClassLoader.getResource(htmlResource).getPath().replaceAll("%20", " "));
        if (f.isFile() && f.toString().matches(".*\\.html")) {
            suite.add(new TestDescription(htmlFilename, getTitleOfPage(f.getAbsolutePath()), loader.findSelendionClass(htmlResource), evaluator));

        }  else if (f.isDirectory()) {
            for (String sub : f.list()) {
                walk(htmlFilename.replaceFirst("/$", "") + "/" + sub, htmlResource.replaceFirst("/$", "") + "/" + sub, evaluator);
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
