package org.selendion.internal.util;

import org.concordion.integration.junit3.ConcordionTestCase;

public class TestDescription {
    private String htmlFile;
    private String title;
    private Class<? extends ConcordionTestCase> clazz;

    public TestDescription(String htmlFile, String title, Class<? extends ConcordionTestCase> clazz) {
        this.htmlFile = htmlFile;
        this.title = title;
        this.clazz = clazz;
    }

    public String getTitle() {
        return title;
    }

    public String getFile() {
        return htmlFile;
    }
    public Class<? extends ConcordionTestCase> getClazz() {
        return clazz;
    }
}