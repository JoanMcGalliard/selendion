/*
	Copyright Joan McGalliard, 2008-9
*/

package org.selendion.internal;

import org.concordion.internal.SimpleEvaluator;
import org.selendion.internal.command.IncludeDisposition;

import java.util.List;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Enumeration;

import ognl.Ognl;

public class SelendionEvaluator extends SimpleEvaluator {
    private static final String PREFIX = "selendion.";
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
        String CONSTANTS = "IMMEDIATE|CLICKABLE|LINK";
         String  VAR_PATTERN = "(" + CONSTANTS + "|" + "#[a-z][a-zA-Z0-9_]*|#TEXT|#HREF|true|false|'[^']*'|-?[0-9]+\\.?[0-9]*|\\-?.[0-9]*"
                 +  ")";
         String VAR_LIST = VAR_PATTERN + "(, *"+VAR_PATTERN+")++";
        if (expression.matches("(" + CONSTANTS + ")")) {
            if (expression.equals("IMMEDIATE") )  {
                return IncludeDisposition.IMMEDIATE;
            }
            if (expression.equals("CLICKABLE") )  {
                return IncludeDisposition.CLICKABLE;
            }
            if (expression.equals("LINK") )  {
                return IncludeDisposition.LINK;
            }
        }
        if (expression.matches(VAR_LIST)) {
            List<Object> returnValue = new ArrayList<Object>();
            for (String subExpression : expression.split(",")) {
                returnValue.add(evaluate(subExpression.trim()));
            }
            return returnValue.toArray();
        } else {
            return super.evaluate(expression);
        }
    }
    
}
