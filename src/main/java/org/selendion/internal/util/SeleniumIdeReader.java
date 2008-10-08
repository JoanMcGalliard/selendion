/*
        Copyright Joan McGalliard, 2008
*/

package org.selendion.internal.util;

import org.concordion.internal.util.IOUtil;
import org.concordion.api.Evaluator;
import org.selendion.integration.selenium.SeleniumDriver;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Set;
import java.io.IOException;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.InputStream;

import com.thoughtworks.selenium.SeleniumException;


public class SeleniumIdeReader {
    private SeleniumDriver selenium;
    private boolean started = false;


    public void start(String seleniumHost, int seleniumPort, String browser, String baseUrl) {
        selenium = new SeleniumDriver(
                seleniumHost, seleniumPort, browser,
                baseUrl);
        selenium.start();
        started = true;
    }

    public void stop() {
        if (started) {
            selenium.stop();
        }
    }

    String[][] readSelenium(String htmlFile) {
        InputStream stream = this.getClass().getResourceAsStream(htmlFile);
        if (stream == null) {
            throw new RuntimeException(String.format("Could not read selenium file %s.", htmlFile));
        }
        Reader reader = new InputStreamReader(stream);
        String contents = null;
        try {
            contents = IOUtil.readAsString(reader);
        } catch (IOException e) {
            System.out.println(e);
        }

        String table = contents.substring(contents.indexOf("<table"), contents
                .indexOf("</table"));
        if (table.indexOf("<tbody") > -1) {
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
        turnConcordionVarsToSeleniumVars(evaluator);
        boolean result = true;
        for (String[] command : seleniumCommands) {
            if (command[1] == null || command[2] == null) {
                System.out.println("Skipping " + command[0]);
            } else if (!execute(command[0], command[1], command[2], filepath)) {
                result = false;
            }
        }
        turnSeleniumVarsToConcordionVars(evaluator);
        return result;
    }
    private void turnConcordionVarsToSeleniumVars(Evaluator evaluator) throws Exception {
        Set keys=evaluator.getKeys();
        System.out.println("Number of concordion keys " + keys.size());
        for (Object key: keys) {
            if (!key.getClass().equals(String.class)) {
                throw new Exception ("Unexpected key " + key);
            }
            String keyString = (String) key;
            if (keyString.matches("^[a-z].*")) {
                selenium.getEval(String.format("storedVars['%s']='%s'", keyString,  evaluator.getVariable("#" + keyString)));
            }
        }
    }
    private void turnSeleniumVarsToConcordionVars(Evaluator evaluator) {
        String [] storedVars=selenium.getEval("var arr = [];for (var name in storedVars) {arr.push(name);};arr").split(",");
        System.out.println("Number of selenium vars " + storedVars.length);

        for (String var : storedVars) {
            if (var.matches("^[a-z].*")) {
                evaluator.setVariable("#"+var, selenium.getEval(String.format("storedVars['%s']", var)));
            }
        }
    }


    private boolean execute(String command, String arg1, String arg2, String filename)
            throws Exception {
        if (!started) {
            throw new Exception("Please start selenium before running scripts.");
        }
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
            selenium.getEval(String.format("storedVars['%s']='%s'", arg2, arg1));
        } else if (command.equals("storeText")) {
            selenium.getEval(String.format("storedVars['%s']='%s'", arg2, selenium.getText(arg1)));
        } else if (command.equals("XXX")) {
        } else if (command.equals("XXX")) {
        } else if (command.equals("XXX")) {
        } else if (command.equals("XXX")) {
        } else if (command.equals("XXX")) {
        } else {
            // maybe it's a js extension
            String js = "Selenium.prototype.do" + command.substring(0, 1).toUpperCase() + command.substring(1);
            try {
                selenium.getEval(String.format("%s('%s','%s')", js, arg1, arg2));
            } catch (SeleniumException e) {
                // It's not an extension, so throw an exception
                throw new Exception(String.format("Unsupported selenium command: %s in %s.", command, filename));
            }
        }
        return true;
    }

    private Pattern variablePattern = Pattern.compile("(.*)\\$\\{([^}]*)\\}(.*)");

    private String replaceVariables(String string, Evaluator evaluator) {

        Matcher m = variablePattern.matcher(string);

        while (m.matches()) {
            string = m.group(1) + evaluator.getVariable("#" + m.group(2)) + m.group(3);
            m = variablePattern.matcher(string);
        }
        return string;
    }

}
