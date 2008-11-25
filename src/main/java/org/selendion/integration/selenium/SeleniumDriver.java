/*
        Copyright Joan McGalliard, 2008
*/

package org.selendion.integration.selenium;

import com.thoughtworks.selenium.DefaultSelenium;
import org.selendion.integration.BrowserDriver;
import org.concordion.api.Evaluator;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SeleniumDriver extends DefaultSelenium implements BrowserDriver {

    private String timeout = "30000";

    public void setTimeout(String timeout) {
        Integer.parseInt(timeout);
        super.setTimeout(timeout);
        this.timeout=timeout;
    }
    public String getTimeout() {
        return timeout;
    }

    public SeleniumDriver(String string, int i, String string2,
            String url) {
        super(string, i, string2, url);
    }

    public void clickAndWait(String locator) {
        click(locator);
        waitForPageToLoad(timeout);
    }


    public void waitForPageToLoad() {
        waitForPageToLoad(timeout);
    }

    public void pause(int milliseconds) throws InterruptedException {
            Thread.sleep(milliseconds);
    }
    private static String VARIABLE_PATTERN = "[a-z][A-Za-z0-9_]*";

    public void passVariablesIn(Evaluator evaluator) {
           Set keys = evaluator.getKeys();
           String array = "";
           for (Object key : keys) {
               if (!key.getClass().equals(String.class)) {
                   throw new RuntimeException("Unexpected key " + key);
               }
               String keyString = (String) key;
               if (keyString.matches(VARIABLE_PATTERN)) {
                   array = String.format("%s %s: '%s', ", array, keyString, evaluator.getVariable("#" + keyString).toString().replaceFirst("\\\\|$", "").replaceAll("'", "\\\\'").replaceAll("\\n", " "));
               }
           }
           array = array.replaceFirst("[, ]*$", "");
           if (array.length() > 0) {
               getEval("storedVars = {" + array + "}");
           }
       }

       public void passVariablesOut(Evaluator evaluator) {
           String[] storedVars = getEval("var arr = [];for (var name in storedVars) {arr.push('#'+name+' '+storedVars[name]);};arr").split(",");
           for (String var : storedVars) {
               String[] nvp = var.split(" ", 2);
               if (nvp[0].matches("#" + VARIABLE_PATTERN)) {
                   evaluator.setVariable(nvp[0], nvp[1]);
               }
           }
       }

    public void store(String name, Object value) {
        Class clazz = value.getClass();
        if (clazz == String.class) {
            getEval(String.format("storedVars['%s']='%s'", name, ((String) value).replaceAll("\\n", " ").replaceAll("\\\\", "\\\\\\\\").replaceAll("'", "\\\\'")));
        } else if (clazz == Boolean.class) {
            getEval(String.format("storedVars['%s']=%s", name, (Boolean) value ? "true" : "false"));
        } else if (clazz == String[].class) {
            String valueStr = "";
            for (String str : (String[]) value) {
                valueStr = valueStr + "'" + str + "', ";
            }
            valueStr = valueStr.replaceFirst("[ ,]*$", "");
            getEval(String.format("storedVars['%s']=%s", name, "[" + valueStr + "]"));
        } else if (clazz == Number.class) {
            getEval(String.format("storedVars['%s']=%s", name, value.toString()));
            getEval(String.format("storedVars['%s']=%s", name, value.toString()));
        }

    }
    public String replaceVariables(String string) {
        Matcher m = variablePattern.matcher(string);
        while (m.matches()) {
            string = m.group(1) + getEval(String.format("storedVars['%s']", m.group(2))) + m.group(3);
            m = variablePattern.matcher(string);
        }
        return string;
    }


    private Pattern variablePattern = Pattern.compile("(.*)\\$\\{([^}]*)\\}(.*)");

}
