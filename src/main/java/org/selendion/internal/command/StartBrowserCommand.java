/*
        Copyright Joan McGalliard, 2008-9
*/

package org.selendion.internal.command;

import org.concordion.internal.command.AbstractCommand;
import org.concordion.internal.CommandCallList;
import org.selendion.internal.util.SeleniumIdeReader;
import ognl.Ognl;
import ognl.OgnlException;

public class StartBrowserCommand extends AbstractCommand {
    private final SeleniumIdeReader seleniumIdeReader;

    public StartBrowserCommand(SeleniumIdeReader seleniumIdeReader) {
        this.seleniumIdeReader = seleniumIdeReader;
    }

    @Override
    public void execute(org.concordion.internal.CommandCall commandCall, org.concordion.api.Evaluator evaluator, org.concordion.api.ResultRecorder resultRecorder) {

        CommandCallList childCommands = commandCall.getChildren();

        childCommands.setUp(evaluator, resultRecorder);
        childCommands.execute(evaluator, resultRecorder);
        childCommands.verify(evaluator, resultRecorder);


        Object evaluatedExpression = evaluator.evaluate(commandCall.getExpression());
        Object[] params = (Object[]) evaluatedExpression;
        String simulator = (String) params[0];
        String seleniumServerHost = (String) params[1];
        int seleniumServerPort = Integer.parseInt((String) params[2]);
        String browser = (String) params[3];
        String baseUrl = (String) params[4];
        if (simulator.equals("SELENIUM")) {
            seleniumIdeReader.start(seleniumServerHost, seleniumServerPort, browser, baseUrl);
        } else if (simulator.equals("HTMLUNIT")) {
            seleniumIdeReader.start(baseUrl);
        } else {
            throw new RuntimeException("Unknown simulator type " + simulator);
        }
    }
}
