/*
        Copyright Joan McGalliard, 2008
*/

package org.selendion.internal.command;

import org.concordion.internal.command.AbstractCommand;
import org.concordion.internal.CommandCallList;
import org.selendion.internal.util.SeleniumIdeReader;
import ognl.Ognl;
import ognl.OgnlException;

public class StartSeleniumCommand extends AbstractCommand {
    private SeleniumIdeReader seleniumIdeReader;

    public StartSeleniumCommand(SeleniumIdeReader seleniumIdeReader) {
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
        try

        {
            // This is hacky, but it works.  It needs more work.
            ognl.Node params = ((ognl.Node) Ognl.parseExpression(commandCall.getExpression()));

            seleniumServerHost = evaluator.evaluate(params.jjtGetChild(0).toString()).toString();
            seleniumServerPort = Integer.parseInt(evaluator.evaluate(params.jjtGetChild(1).toString()).toString());

            browser = evaluator.evaluate(params.jjtGetChild(2).toString()).toString();
            baseUrl = evaluator.evaluate(params.jjtGetChild(3).toString()).toString();


        }

        catch (
                OgnlException e
                )

        {
            throw new RuntimeException(e);
        }

        seleniumIdeReader.start(seleniumServerHost, seleniumServerPort, browser, baseUrl);

    }
}
