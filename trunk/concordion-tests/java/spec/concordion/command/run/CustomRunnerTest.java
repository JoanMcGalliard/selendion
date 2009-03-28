package spec.concordion.command.run;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.selendion.integration.concordion.SelendionRunner;

import test.concordion.TestRig;

@RunWith(SelendionRunner.class)
public class CustomRunnerTest {
    
    @BeforeClass
    public static void setUp() {
        System.setProperty("concordion.runner.exampleRunner", ExampleRunner.class.getName());
    }
    
    @Before
    public void initRunner() {
        ExampleRunner.clear();
    }
    
    public void setUpResult(String regex, String result) {
        ExampleRunner.addMapping(regex, result);
    }
    
	public String getResult(String fragment, String href) {
	    fragment = fragment.replaceFirst("href=\"href\"", "href=\"" + href + "\"");
	    
        String xml = new TestRig()
            .processFragment(fragment)
            .getOutputFragmentXML();
        
        if (xml.contains("class=\"ignored\"")) {
            return "IGNORED";
        }
        if (xml.contains("exceptionMessage")) {
            return "EXCEPTION";
        }
        if (xml.contains("class=\"failure\"")) {
            return "FAILURE";
        }
        if (xml.contains("class=\"success\"")) {
            return "SUCCESS";
        }
        
        System.out.println(xml);
        
        return "UNEXPECTED RESULT";
    }
}
