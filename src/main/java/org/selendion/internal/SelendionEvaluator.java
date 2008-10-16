package org.selendion.internal;

import org.concordion.internal.SimpleEvaluator;

import java.util.List;
import java.util.ArrayList;

public class SelendionEvaluator extends SimpleEvaluator {
    public SelendionEvaluator(Object o) {
        super(o);
    }

    public Object evaluate(String expression) {
        if (expression.contains(",")) {
            List<Object> returnValue = new ArrayList<Object>();
            for (String subExpression : expression.split(",")) {
                returnValue.add(super.evaluate(subExpression));
            }
            return returnValue.toArray();
        } else {
            return super.evaluate(expression);
        }
    }

}
