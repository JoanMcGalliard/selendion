/*
        Copyright Joan McGalliard, 2008-2013
*/

package org.selendion.internal.command;

import org.concordion.internal.command.AbstractCommand;
import org.concordion.internal.util.Check;
import org.concordion.internal.*;
import org.concordion.api.*;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class ForEachCommand extends AbstractCommand {

    // This class is taken from Xcordion

    private final DocumentParser documentParser;

    public ForEachCommand(DocumentParser documentParser) {
        this.documentParser = documentParser;
    }


    public void execute(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
        Pattern pattern = Pattern.compile("(#.+?) *: *(.+)");
        Matcher matcher = pattern.matcher(commandCall.getExpression());
        if (!matcher.matches()) {
            throw new RuntimeException("The expression for a \"forEach\" should be of the form: #var : collectionExpr");
        }
        String loopVariableName = matcher.group(1);
        String iterableExpression = matcher.group(2);

        Object obj = evaluator.evaluate(iterableExpression);
        Check.notNull(obj, "Expression returned null (should be an Iterable).");
        Check.isTrue(obj instanceof Iterable, obj.getClass().getCanonicalName() + " is not Iterable");
        Check.isTrue(!(obj instanceof HashSet) || (obj instanceof LinkedHashSet), obj.getClass().getCanonicalName() + " does not have a predictable iteration order");
        Iterable<Object> iterable = (Iterable<Object>) obj;

        Element element = commandCall.getElement();

        Element placeholder = new Element("span");

        Element prototype = element.copy();
        element.insertAfter(placeholder);
        
        for (Object loopVar : iterable) {
            Element newContent = prototype.copy();
            placeholder.appendChild(newContent);

            CommandCall dummy = new CommandCall(ForEachCommand.this, newContent, commandCall.getExpression(), commandCall.getResource());
            documentParser.generateCommandCallTree(newContent.getXomElement(), dummy, commandCall.getResource());
            CommandCall duplicateOfThisCommand = dummy.getChildren().get(0);
            CommandCallList children = duplicateOfThisCommand.getChildren();

            evaluator.setVariable(loopVariableName, loopVar);
            children.processSequentially(evaluator, resultRecorder);
        }
        element.addAttribute("class", "invisible");
    }
}