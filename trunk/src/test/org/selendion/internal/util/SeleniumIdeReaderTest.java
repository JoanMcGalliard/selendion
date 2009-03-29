/*
        Copyright Joan McGalliard, 2008-9
*/

package org.selendion.internal.util;


import static org.junit.Assert.*;

import java.util.Vector;


public class SeleniumIdeReaderTest {
    @org.junit.Test
    public void testReadSelenium() throws Exception {
        SeleniumIdeReader seleniumIdeReader = new SeleniumIdeReader();
        Vector<String[]>  expectedResult = new Vector<String[] >();
        expectedResult.add(new String [] {"all", "three", "columns"});
        expectedResult.add(new String [] {"three columns, 2 empty", "", ""});
        expectedResult.add(new String [] {"three columns", "1 empty", ""});
        expectedResult.add(new String [] {"three columns", "", "middle one empty"});

        Vector<String[]> actualResult;
        actualResult = seleniumIdeReader.readSelenium("test_data/seleniumIdeExample.html");

        assertVectorEquals(expectedResult, actualResult);
                    
        actualResult = seleniumIdeReader.readSelenium("test_data/simpleExample.html");
        assertVectorEquals(expectedResult, actualResult);
        actualResult = seleniumIdeReader.readSelenium("test_data/commentExample.html");
        assertVectorEquals(expectedResult, actualResult);


    } // testReadSelenium()
    void assertVectorEquals(Vector <String[]> v1, Vector <String[]> v2) {
        assertEquals(v1.size(), v2.size());
        for (int i=0; i < v1.size(); i++) {
            assertArrayEquals(v1.get(i),v2.get(i));
        }
    }
}
