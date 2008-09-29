/*
        Copyright Joan McGalliard, 2008
*/

package org.selendion.internal.util;

import org.selendion.internal.util.SeleniumIdeReader;
        import org.junit.*;
        import static org.junit.Assert.*;
import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: jem
 * Date: Sep 29, 2008
 * Time: 5:03:55 AM
 * To change this template use File | Settings | File Templates.
 */
public class TestSeleniumIdeReader {

    public TestSeleniumIdeReader() {
    } // constructor

    @BeforeClass
    public static void unitSetup() {
    } // unitSetup()

    @AfterClass
    public static void unitCleanup() {
    } // unitCleanup()

    @Before
    public void methodSetup() {
    } // methodSetup()

    @After
    public void methodCleanup() {
    } // methodCleanup()

     @org.junit.Test
    public void testReadSelenium() {
        SeleniumIdeReader seleniumIdeReader = new SeleniumIdeReader();
          String[][] actualResult = seleniumIdeReader.readSelenium("test_data/testcase.html");
        fail();

    } // testReadSelenium()
}
