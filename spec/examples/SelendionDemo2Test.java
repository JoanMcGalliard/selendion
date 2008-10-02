/*
	Copyright Joan McGalliard, 2008
*/
package examples;

import org.selendion.integration.concordion.SelendionTestCase;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.MalformedURLException;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

public class SelendionDemo2Test extends SelendionTestCase {

/*
    public SelendionDemo2Test()  {
        super();
        File file = new File("/Users/jem/selendion/lib/selenium-server.jar");
        String lcStr = "org.openqa.selenium.server.SeleniumServer";
        URL jarfile = null;
        try {
            jarfile = new URL("jar", "", "file:" + file.getAbsolutePath() + "!/");
        } catch (MalformedURLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        URLClassLoader cl = URLClassLoader.newInstance(new URL[]{jarfile});
        try {
            Class loadedClass = cl.loadClass(lcStr);

            Method main=loadedClass.getMethod("main", new Class[] { String[].class });
            String [] param = new String [] {"-firefoxProfileTemplate","/Users/jem/selendion/FirefoxProfile"};
            main.invoke(null, (Object)param);
            System.out.println();

        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }



    }
*/
    
    public SelendionDemo2Test() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {

//        String path = "SelendionDemoTest";
//        SelendionTestCase testCase;
//               testCase
//                        = (SelendionTestCase) Class.forName(path).getConstructor().newInstance();
//
//               /* Run the modules 'getText()' method defined in the interface. */
//               System.out.println(testCase.run());
//


    }


}