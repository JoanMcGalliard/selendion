package org.selendion.internal.command;

import org.concordion.internal.command.AbstractCommand;
import org.concordion.internal.CommandCall;
import org.concordion.api.Evaluator;
import org.concordion.api.ResultRecorder;
import org.concordion.api.Resource;
import org.selendion.integration.concordion.SelendionTestCase;

import java.lang.reflect.InvocationTargetException;

import junit.framework.TestResult;


public class RunSuite extends AbstractCommand {
    public void execute(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {

        String selendionFile = commandCall.getResource().getRelativeResource(evaluator.evaluate(commandCall.getExpression()).toString()).getPath();
//        String package=selendionResource.getPackage();
        System.out.println("selendionFile " + selendionFile);
        if (selendionFile.startsWith("/")) {
            selendionFile= selendionFile.substring(1,selendionFile.length());
        }
         if (selendionFile.endsWith(".html")) {
            selendionFile= selendionFile.substring(0,selendionFile.length()-5);
        }
        selendionFile=selendionFile + "Test";
        String className = selendionFile.replace("/", ".");


        try {
            SelendionTestCase test = (SelendionTestCase)Class.forName(className).getConstructor().newInstance();
            test.testProcessSpecification() ;
            System.out.println(selendionFile);
        } catch (InstantiationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IllegalAccessException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InvocationTargetException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (NoSuchMethodException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ClassNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (Throwable throwable) {
            throwable.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


    }

}