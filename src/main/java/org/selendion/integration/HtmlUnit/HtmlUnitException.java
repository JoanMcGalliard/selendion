package org.selendion.integration.HtmlUnit;

public class HtmlUnitException extends RuntimeException {
    public HtmlUnitException(String s) {
        super(s);
    }

    public HtmlUnitException(Exception e) {
        super(e);
    }
}
