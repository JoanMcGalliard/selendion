/*
	Copyright Joan McGalliard, 2008-9
*/

package org.selendion.integration.HtmlUnit;

import com.thoughtworks.selenium.SeleniumException;

public class HtmlUnitException extends SeleniumException {
    public HtmlUnitException(String s) {
        super(s);
    }

    public HtmlUnitException(Exception e) {
        super(e);
    }

//    public HtmlUnitException(Throwable e) {
//        super(e);
//    }
}
