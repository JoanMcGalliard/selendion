package org.selendion.internal.command;


import org.concordion.api.Evaluator;
import org.concordion.api.ResultRecorder;
import org.concordion.internal.CommandCall;
import org.concordion.internal.CommandCallList;
import org.concordion.internal.command.AbstractCommand;
import org.concordion.internal.util.Check;

public class SetCommand extends AbstractCommand {

    @Override
    public void setUp(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
        CommandCallList childCommands = commandCall.getChildren();

        childCommands.setUp(evaluator, resultRecorder);
        childCommands.execute(evaluator, resultRecorder);
        childCommands.verify(evaluator, resultRecorder);
        
        evaluator.setVariable(commandCall.getExpression(), commandCall.getElement().getText());
    }
}
