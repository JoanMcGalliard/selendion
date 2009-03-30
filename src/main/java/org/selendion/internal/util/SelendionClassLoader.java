/*
	Copyright Joan McGalliard, 2008-9
*/

package org.selendion.internal.util;

import org.selendion.integration.concordion.SelendionTestCase;
import javassist.*;

import java.util.Hashtable;

public class SelendionClassLoader extends ClassLoader {
    private static Hashtable classes = new Hashtable();


    @SuppressWarnings("unchecked")
    public  Class<? extends SelendionTestCase> findSelendionClass (String htmlResource) {
        String className = htmlResource;

        if (className.endsWith(".html")) {
            className = className.substring(0, className.length() - 5);
        }
        className = className.replace("/", ".");
        if (classes.get(className) != null) {
            return (Class<? extends SelendionTestCase>)classes.get(className);
        }

        Class testClass;
        try {
            testClass = loadClass(className + "Test");
        } catch (ClassNotFoundException e) {
            try {
                testClass = loadClass(className);
            } catch (ClassNotFoundException e1) {
                ClassPool pool = ClassPool.getDefault();
                CtClass parent;
                try {
                    parent = pool.getCtClass("org.selendion.integration.concordion.SelendionTestCase");
                } catch (NotFoundException e2) {
                    throw new RuntimeException("Can't find org.selendion.integration.concordion.SelendionTestCase",e);
                }
                try {
                    testClass = pool.makeClass(className, parent).toClass(this);

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
                    if (subclassOfSelendionTestCase(clazz.toClass(this, parent.toClass().getProtectionDomain()))) {

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
                testClass = newClass.toClass(this);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        classes.put(className, testClass);
        return testClass;
    }
    private static boolean subclassOfSelendionTestCase(Class testClass) {
        if (testClass == null) {
            return false;
        } else if (testClass.getName().equals("org.selendion.integration.concordion.SelendionTestCase")) {
            return true;
        } else {
            return subclassOfSelendionTestCase(testClass.getSuperclass());
        }

    }


}
