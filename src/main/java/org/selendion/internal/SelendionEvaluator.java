package org.selendion.internal;

import org.concordion.internal.SimpleEvaluator;

import java.util.List;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Enumeration;

public class SelendionEvaluator extends SimpleEvaluator {
    private static String PREFIX = "selendion.";
    public SelendionEvaluator(Object o) {
        super(o);
        Enumeration<?> propertyNames = System.getProperties().propertyNames();
        while (propertyNames.hasMoreElements()) {
            String name = (String) propertyNames.nextElement();
            if (name.startsWith(PREFIX)) {
                setVariable("#" + name.replaceFirst(PREFIX, ""), System.getProperty(name));
            }
        }
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
