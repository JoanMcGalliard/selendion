/*
	Copyright Joan McGalliard, 2008-2013
*/

package org.selendion.internal.util;

import org.selendion.integration.concordion.SelendionTestCase;

public class ExceptionThrowingSelendionTestCase extends SelendionTestCase {
    public void testProcessSpecification() throws Throwable {
       throw new Exception(this.getClass().toString());
    }
}
