package org.selendion.internal.command;

import java.util.Comparator;

import org.concordion.api.Element;
import org.concordion.api.Evaluator;
import org.concordion.api.Result;
import org.concordion.api.ResultRecorder;
import org.concordion.internal.BrowserStyleWhitespaceComparator;
import org.concordion.internal.CommandCall;
import org.concordion.internal.CommandCallList;
import org.concordion.internal.command.AbstractCommand;
import org.concordion.internal.command.AssertEqualsListener;
import org.concordion.internal.command.AssertEqualsFailureEvent;
import org.concordion.internal.command.AssertEqualsSuccessEvent;
import org.concordion.internal.util.Announcer;

public class AssertEqualsCommand extends AbstractCommand {

    private final Announcer<AssertEqualsListener> listeners = Announcer.to(AssertEqualsListener.class);
    private final Comparator<Object> comparator;

    public AssertEqualsCommand() {
        this(new BrowserStyleWhitespaceComparator());
    }

    public AssertEqualsCommand(Comparator<Object> comparator) {
        this.comparator = comparator;
    }

    public void addAssertEqualsListener(AssertEqualsListener listener) {
        listeners.addListener(listener);
    }

    public void removeAssertEqualsListener(AssertEqualsListener listener) {
        listeners.removeListener(listener);
    }

    @Override
    public void verify(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
        CommandCallList childCommands = commandCall.getChildren();

        childCommands.setUp(evaluator, resultRecorder);
        childCommands.execute(evaluator, resultRecorder);
        childCommands.verify(evaluator, resultRecorder);
        Element element = commandCall.getElement();

        Object actual = evaluator.evaluate(commandCall.getExpression());
        String expected = element.getText();

        if (comparator.compare(actual, expected) == 0) {
            resultRecorder.record(Result.SUCCESS);
            announceSuccess(element);
        } else {
            resultRecorder.record(Result.FAILURE);
            announceFailure(element, expected, actual);
        }
    }

    private void announceSuccess(Element element) {
        listeners.announce().successReported(new AssertEqualsSuccessEvent(element));
    }

    private void announceFailure(Element element, String expected, Object actual) {
        listeners.announce().failureReported(new AssertEqualsFailureEvent(element, expected, actual));
    }
}
