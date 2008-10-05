/*
        Copyright Joan McGalliard, 2008
*/

package org.selendion.internal.util;


import static org.junit.Assert.*;


public class SeleniumIdeReaderTest {
    @org.junit.Test
    public void testReadSelenium() throws Exception {
        SeleniumIdeReader seleniumIdeReader = new SeleniumIdeReader();
        String[][] expectedResult = {
                {"Only one column", null, null},
                {"two", "columns", null},
                {"all", "three", "columns"},
                {"three columns, 2 empty", "", ""},
                {"three columns", "1 empty", ""},
                {"three columns", "", "middle one empty"},
                {"Two columns, one used", "", null}};

        String[][] actualResult = seleniumIdeReader.readSelenium("test_data/seleniumIdeExample.html");
        assertArrayEquals(actualResult, expectedResult);
        actualResult = seleniumIdeReader.readSelenium("test_data/simpleExample.html");
        assertArrayEquals(actualResult, expectedResult);


    } // testReadSelenium()
}
