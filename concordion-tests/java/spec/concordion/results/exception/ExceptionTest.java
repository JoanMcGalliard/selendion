package spec.concordion.results.exception;

import java.util.ArrayList;
import java.util.List;

import org.concordion.api.Element;
import org.selendion.integration.concordion.SelendionTestCase;
import org.concordion.internal.command.ThrowableCaughtEvent;
import org.concordion.internal.listener.ThrowableRenderer;

import test.concordion.TestRig;
import nu.xom.Document;
import nu.xom.Nodes;

public class ExceptionTest extends SelendionTestCase {
    
    private List<StackTraceElement> stackTraceElements = new ArrayList<StackTraceElement>();
    
    public void addStackTraceElement(String declaringClassName, String methodName, String filename, int lineNumber) {
        if (filename.equals("null")) {
            filename = null;
        }
        stackTraceElements.add(new StackTraceElement(declaringClassName, methodName, filename, lineNumber));
    }
    
    public String markAsException(String fragment, String expression, String errorMessage) {
        Throwable t = new Throwable(errorMessage);
        t.setStackTrace(stackTraceElements.toArray(new StackTraceElement[0]));

        Document document =  new TestRig()
            .processFragment(fragment)
            .getXOMDocument();

        Element element = new Element((nu.xom.Element) document
            .query("//p")
            .get(0));
            
        new ThrowableRenderer().throwableCaught(new ThrowableCaughtEvent(t, element, expression));
        
        Nodes nodes = new TestRig().unwrapFragment(document);
        String ret = "";
        for (int i = 0; i< nodes.size(); i++) {
            ret = ret + nodes.get(i).toXML();
        }
        return ret;
    }
}
