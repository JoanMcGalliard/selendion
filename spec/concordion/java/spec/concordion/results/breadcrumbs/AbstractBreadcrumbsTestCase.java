package spec.concordion.results.breadcrumbs;

import org.concordion.api.Element;
import org.concordion.api.Resource;
import org.selendion.integration.concordion.SelendionTestCase;

import test.concordion.TestRig;


public abstract class AbstractBreadcrumbsTestCase extends SelendionTestCase {

    private TestRig testRig = new TestRig();

    protected void setUpResource(String resourceName, String content) {
        testRig.withResource(new Resource(resourceName), content);
    }

    protected Result getBreadcrumbsFor(String resourceName) throws Exception {
        Element[] spanElements = testRig
            .process(new Resource(resourceName))
            .getRootElement()
            .getDescendantElements("span");
        
        Result result = new Result();
        for (Element span : spanElements) {
            if ("breadcrumbs".equals(span.getAttributeValue("class"))) {
                result.html = span.toXML();
                result.text = span.getText();
            }
        }
        return result;
    }
    
    class Result {
        public String text = "";
        public String html = "";
    }
}