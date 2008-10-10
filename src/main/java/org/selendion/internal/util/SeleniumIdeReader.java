/*
        Copyright Joan McGalliard, 2008
*/

package org.selendion.internal.util;

import org.concordion.api.Evaluator;
import org.concordion.api.Element;
import org.concordion.api.Result;
import org.concordion.internal.util.Announcer;
import org.selendion.integration.selenium.SeleniumDriver;
import org.selendion.internal.command.RunSeleniumSuccessEvent;
import org.selendion.internal.command.RunSeleniumFailureEvent;
import org.selendion.internal.RunSeleniumListener;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLReaderFactory;
import org.ccil.cowan.tagsoup.Parser;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Set;
import java.util.Vector;
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

    public boolean runSeleniumScript(String filepath, Evaluator evaluator) throws Exception {
        Vector<String[]> seleniumCommands = readSelenium(filepath);
        turnConcordionVarsToSeleniumVars(evaluator);
        boolean result = true;
        for (String[] command : seleniumCommands) {

            if (command[1] != null && command[2] != null && !execute(command[0], command[1], command[2], filepath)) {

                result = false;
            }
        }
        turnSeleniumVarsToConcordionVars(evaluator);
        return result;


    }



    public boolean runSeleniumScript(String filepath, Evaluator evaluator, Element element, Announcer<RunSeleniumListener> listeners, int buttonId) throws Exception {
        Vector<String[]> seleniumCommands = readSelenium(filepath);
        turnConcordionVarsToSeleniumVars(evaluator);
        boolean result = true;
        Element table = new Element("table");
        Element tr = new Element("tr");
        Element td = new Element("th");
        td.addAttribute("colspan", "3");
        td.appendText(element.getText());
        tr.appendChild(td);
        table.appendChild(tr);

        for (String[] command : seleniumCommands) {


            if (command[1] != null && command[2] != null) {
                tr = new Element("tr");
                td = new Element("td").appendText(command[0]);
                tr.appendChild(td);
                td = new Element("td").appendText(command[1]);
                tr.appendChild(td);
                td = new Element("td").appendText(command[2]);
                tr.appendChild(td);

                if (!execute(command[0], command[1], command[2], filepath)) {

                    result = false;
                    listeners.announce().failureReported(new RunSeleniumFailureEvent(tr));
                } else {
                    listeners.announce().successReported(new RunSeleniumSuccessEvent(tr));

                }
                table.appendChild(tr);
            }
        }
//        Element span = new Element("span");
//        span.appendChild(new Element("input")
//                .addStyleClass("seleniumTableButton")
//                .setId("seleniumTableButton" + buttonId)
//                .addAttribute("type", "button")
//                .addAttribute("onclick", "javascript:toggleSeleniumTable('" + buttonId + "', '" + element.getText() + "')")
//                .addAttribute("value", "View " + element.getText()));
//        span.appendChild(table);
        element.insertChildAfter(table);
        turnSeleniumVarsToConcordionVars(evaluator);
        return result;
    }

    private void turnConcordionVarsToSeleniumVars(Evaluator evaluator) throws Exception {
        Set keys = evaluator.getKeys();
        String array = "storedVars = {";
        for (Object key : keys) {
            if (!key.getClass().equals(String.class)) {
                throw new Exception("Unexpected key " + key);
            }
            String keyString = (String) key;
            if (keyString.matches("^[a-z].*")) {
                array = String.format("%s %s: '%s', ", array, keyString, evaluator.getVariable("#" + keyString));

            }
        }
        array = array.replaceFirst(", $", "}");
        selenium.getEval(array);
    }

    private void turnSeleniumVarsToConcordionVars(Evaluator evaluator) {
        String[] storedVars = selenium.getEval("var arr = [];for (var name in storedVars) {arr.push('#'+name+' '+storedVars[name]);};arr").split(",");
        for (String var : storedVars) {
            String[] nvp = var.split(" ", 2);
            if (nvp[0].matches("^#[a-z].*")) {
                evaluator.setVariable(nvp[0], nvp[1]);
            }
        }
    }


    private boolean execute(String command, String arg1, String arg2, String filename)
            throws Exception {
        if (!started) {
            throw new Exception("Please start selenium before running scripts.");
        }
        command = replaceCharacterEntities(command);
        arg1 = replaceCharacterEntities(arg1);
        arg2 = replaceCharacterEntities(arg2);
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
        } else if (command.equals("deleteCookie")) {
            selenium.deleteCookie(arg1, arg2);
        } else if (command.equals("addSelection")) {
            selenium.addSelection(arg1, arg2);
        } else if (command.equals("verifyVisible")) {
            if (!selenium.isVisible(arg1)) {
                return false;
            }
        } else if (command.equals("verifyElementNotPresent")) {
            if (selenium.isElementPresent(arg1)) {
                return false;
            }
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

    private String replaceCharacterEntities(String string) {

        string = string.replaceAll("&nbsp;", " ");
        string = string.replaceAll("&lt;", "<");
        string = string.replaceAll("&gt;", ">");
        string = string.replaceAll("&amp;", "&");
        string = string.replaceAll("&quot;", "\"");
        string = string.replaceAll("&apos;", "'");
        return string;
    }

    Vector<String[]> readSelenium(String htmlFileName) throws IOException, nu.xom.ParsingException, SAXException {
        InputStream stream = this.getClass().getResourceAsStream(htmlFileName);
        if (stream == null) {
            throw new RuntimeException(String.format("Could not read selenium file %s.", htmlFileName));
        }
        Reader reader = new InputStreamReader(stream);
        nu.xom.Builder builder = new nu.xom.Builder(XMLReaderFactory.createXMLReader(Parser.class.getName()));
        nu.xom.Document doc = builder.build(reader);

        nu.xom.Nodes rows;
        String uri = doc.getRootElement().getNamespaceURI();
        if (uri.length() > 0) {
            rows = doc.query("//ns:tr", new nu.xom.XPathContext("ns", uri));

        } else {
            rows = doc.query("//tr");
        }

        Vector return_val = new Vector();
        for (int r = 0; r < rows.size(); r++) {
            nu.xom.Node rowNode = rows.get(r);
            String[] row = new String[3];
            int col = 0;
            for (int i = 0; i < rowNode.getChildCount(); i++) {
                nu.xom.Node colNode = rowNode.getChild(i);
                if (colNode.getClass().equals(nu.xom.Element.class)) {
                    row[col] = colNode.getValue().trim();
                    col++;
                    if (col > 3) {
                        break;
                    }
                }
            }
            if (col >= 3) {
                return_val.add(row);
            }
        }
        return return_val;
    }


}

