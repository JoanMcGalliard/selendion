package org.selendion.internal.util;

import org.selendion.integration.concordion.SelendionTestCase;

public class TestDescription {
    private String htmlFile;
    private String title;
    private Class<? extends SelendionTestCase> clazz;

    public TestDescription(String htmlFile, String title, Class<? extends SelendionTestCase> clazz) {
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
    public Class<? extends SelendionTestCase> getClazz() {
        return clazz;
    }
}