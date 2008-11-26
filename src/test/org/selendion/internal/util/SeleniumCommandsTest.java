package org.selendion.internal.util;


public class SeleniumCommandsTest extends HtmlUnitCommandsTest {
    
    public void setUp(String baseUrl, String page)  {
        seleniumIdeReader= new SeleniumIdeReader();
        seleniumIdeReader.start( "localhost", 5555, "*firefox", baseUrl);
        seleniumIdeReader.execute("open", page, "");
    }
    public void stop() {
        seleniumIdeReader.stop();
    }

}
