/*
        Copyright Joan McGalliard, 2008
*/

package org.selendion.internal.command;

import org.concordion.internal.command.AbstractCommand;
import org.concordion.internal.CommandCallList;
import org.selendion.internal.util.SeleniumIdeReader;
import ognl.Ognl;
import ognl.OgnlException;

public class StartBrowserCommand extends AbstractCommand {
    private SeleniumIdeReader seleniumIdeReader;

    public StartBrowserCommand(SeleniumIdeReader seleniumIdeReader) {
        this.seleniumIdeReader = seleniumIdeReader;
    }

    @Override
    public void execute(org.concordion.internal.CommandCall commandCall, org.concordion.api.Evaluator evaluator, org.concordion.api.ResultRecorder resultRecorder) {

        CommandCallList childCommands = commandCall.getChildren();

        childCommands.setUp(evaluator, resultRecorder);
        childCommands.execute(evaluator, resultRecorder);
        childCommands.verify(evaluator, resultRecorder);

        String seleniumServerHost;
        int seleniumServerPort;
        String browser;
        String baseUrl;

        Object evaluatedExpression = evaluator.evaluate(commandCall.getExpression());
        Object[] params = (Object[]) evaluatedExpression;
        if (params[0].equals("SELENIUM")) {

            seleniumServerHost = (String)params[1];
            seleniumServerPort = Integer.parseInt((String)params[2]);
            browser = (String)params[3];
            baseUrl = (String)params[4];
            seleniumIdeReader.start(seleniumServerHost, seleniumServerPort, browser, baseUrl);
        } else {
            throw new RuntimeException("Unknown simulator type " + "blah");
        }
    }
}
