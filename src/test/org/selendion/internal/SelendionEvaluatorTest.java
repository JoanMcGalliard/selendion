/*
        Copyright Joan McGalliard, 2008-9
*/

package org.selendion.internal;

import junit.framework.TestCase;


public class SelendionEvaluatorTest extends TestCase {
    SelendionEvaluator se = new SelendionEvaluator(new Object());

    public void testValidateSelendionEvaluationExpression() throws Exception {
        assertInvalid("@ jafo ajofea fea");
        assertValid("'/aoifjae/faoejfa/hta.html'");
        assertValid("'/aoifjae/faoejfa/hta.html',#test,#TEXT,#HREF");
        assertValid("#HREF");
        assertValid("#test");
        assertValid("#rowNameScript,#rowValueScript");
        assertInvalid("'/aoifjae/faoejfa/hta.html',#test,#NOTTEXT");
    }

    private void assertValid(String expression) {
        se.evaluate(expression);
    }

    private void assertInvalid(String expression) throws Exception {
        try {
            se.evaluate(expression);
            throw new Exception("Expression \"" + expression + "\" should have thrown an exception.");
        }
        catch (RuntimeException re) {

        }
    }
    public void testPropertyValuesIncludedInEvaluator()  {
        System.setProperty("blah", "value");
        System.setProperty("selendion.property", "value 1");
        System.setProperty("selendion.property2", "value 2");
        SelendionEvaluator evaluator = new SelendionEvaluator(this);
        assertEquals(null, evaluator.getVariable("#selendion.property"));
        assertEquals(null, evaluator.getVariable("#blah"));
        assertEquals("value 1", evaluator.getVariable("#property"));
        assertEquals("value 2", evaluator.getVariable("#property2"));

    }


}