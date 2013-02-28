/*
	Copyright Joan McGalliard, 2008-2013
*/

package org.selendion.internal.util;

import org.selendion.integration.concordion.SelendionTestCase;
import org.selendion.internal.SelendionEvaluator;
import org.concordion.api.Evaluator;

public class TestDescription {
    private final String htmlFile;
    private final String title;
    private final Class<? extends SelendionTestCase> clazz;
    private final Evaluator evaluator;

    private static final String VARIABLE_PATTERN = "[a-z][A-Za-z0-9_]*";

    public TestDescription(String htmlFile, String title, Class<? extends SelendionTestCase> clazz, Evaluator evaluator) {
        this.htmlFile = htmlFile;
        this.title = title;
        this.clazz = clazz;
        this.evaluator = new SelendionEvaluator(clazz);
        for (Object key : evaluator.getKeys()) {
            String keyString = (String) key;
            if (keyString.matches(VARIABLE_PATTERN)) {
                this.evaluator.setVariable("#" + key, evaluator.getVariable("#" + key));
            }
        }
    }

    public String getTitle() {
        return title;
    }

    public Evaluator getEvaluator() {
        return evaluator;
    }

    public String getFile() {
        return htmlFile;
    }

    public Class<? extends SelendionTestCase> getClazz() {
        return clazz;
    }
}
