package org.selendion.internal;

import org.concordion.internal.SimpleEvaluator;

import java.util.List;
import java.util.ArrayList;

public class SelendionEvaluator extends SimpleEvaluator {
    public SelendionEvaluator(Object o) {
        super(o);
    }

    public Object evaluate(String expression) {
         String  VAR_PATTERN = "(#[a-z][a-zA-Z0-9_]*|#TEXT|#HREF|'[^']*'|-?[0-9]+\\.?[0-9]*|\\-?.[0-9]*|true|false)";
         String VAR_LIST = VAR_PATTERN + "(, *"+VAR_PATTERN+")++";
        if (expression.matches(VAR_LIST)) {
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
