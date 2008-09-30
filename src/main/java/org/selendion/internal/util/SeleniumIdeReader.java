/*
        Copyright Joan McGalliard, 2008
*/

package org.selendion.internal.util;

import org.concordion.internal.util.ResourceFinder;
import org.concordion.api.Evaluator;
import org.selendion.integration.selenium.SeleniumDriver;

import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class SeleniumIdeReader {
    private SeleniumDriver selenium;
    private boolean started=false;


    public void start(String seleniumHost, int seleniumPort, String browser, String baseUrl) {
        selenium = new SeleniumDriver(
        seleniumHost,seleniumPort,browser,
        baseUrl);
        selenium.start();
        started=true;
    }
    public void stop() {
        if (started)  {
            selenium.stop();
        }
    }

    String[][] readSelenium(String htmlFile) {
        String contents = new ResourceFinder(this.getClass()).getResourceAsString(htmlFile);
        String table = contents.substring(contents.indexOf("<table"), contents
                .indexOf("</table"));
        if (table.indexOf("<tbody") > -1 ) {
             table = table.substring(table.indexOf("<tbody"), table
                .indexOf("</tbody"));
        }
        String[] rows = table.split("[ 	]*<[tT][rR][^>]*>");

        String[][] return_val = new String[rows.length - 1][3];
        for (int i = 1; i < rows.length; i++) {
            rows[i] = rows[i].replaceFirst("</[tT][rR].*>.*", "").trim();
            String[] cols = rows[i].split("[ 	]*<[tT][Dd][^>]*>");
            for (int j = 1; j < 4; j++) {

                if (cols.length > j) {
                    return_val[i - 1][j - 1] = cols[j].replaceFirst(
                            "</[tT][dD][^>]*>.*$", "").trim();
                } else {
                    return_val[i - 1][j - 1] = null;
                }
            }

        }
        return return_val;
    }

    
    public boolean runSeleniumScript(String filepath, Evaluator evaluator) throws Exception {
        String[][] seleniumCommands = readSelenium(filepath);
        boolean result = true;
        try {
        for (int i = 0; i < seleniumCommands.length; i++) {
            String[] command = seleniumCommands[i];
            if (command[1] == null || command[2] == null) {
                System.out.println("Skipping " + command[0]);
            } else if (execute(command[0], command[1], command[2], evaluator) == false) {
                result = false;
            }
        }
        }
        catch (Exception e) {
            throw new Exception ("Error while running " + filepath + ": " + e.toString());
        }
        return result;
    }

    private boolean execute(String command, String arg1, String arg2, Evaluator evaluator)
            throws Exception {
        if (!started) {
            throw new Exception("Please start selenium before running scripts." );
        }
        command=replaceVariables(command, evaluator);
        arg1=replaceVariables(arg1, evaluator);
        arg2=replaceVariables(arg2, evaluator);
        if (command.equals("click")) {
            selenium.click(arg1);
        } else if (command.equals("open")) {
            selenium.open(arg1);
        } else if (command.equals("type")) {
            selenium.type(arg1, arg2);
        } else if (command.equals("clickAndWait")) {
            selenium.clickAndWait(arg1);
        } else if (command.equals("pause")) {
            selenium.pause(Integer.parseInt(arg1));
        } else if (command.equals("waitForElementPresent")) {
            selenium.waitForElementPresent(arg1);
        } else if (command.equals("verifyElementPresent")) {
            return selenium.isElementPresent(arg1);
        } else if (command.equals("select")) {
             selenium.select(arg1, arg2);
        } else if (command.equals("store")) {
            evaluator.setVariable("#"+arg2, arg1);
        } else if (command.equals("storeText")) {
            evaluator.setVariable("#"+arg2, selenium.getText(arg1));
        } else if (command.equals("XXX")) {
        } else if (command.equals("XXX")) {
        } else if (command.equals("XXX")) {
        } else if (command.equals("XXX")) {
        } else if (command.equals("XXX")) {

        } else {
            throw new Exception("Unsupported selenium command: " + command);
        }
        return true;
    }

    private Pattern variablePattern = Pattern.compile("(.*)\\$\\{([^}]*)\\}(.*)");

    private String replaceVariables(String string, Evaluator evaluator) {

        Matcher m=variablePattern.matcher(string);

        while (m.matches()) {
            string = m.group(1)+evaluator.getVariable("#"+m.group(2))+m.group(3);
            m=variablePattern.matcher(string);
        }
        return string;
    }

}
