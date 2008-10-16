package org.selendion.internal;

import org.concordion.api.EvaluatorFactory;
import org.concordion.api.Evaluator;

public class SelendionEvaluatorFactory implements EvaluatorFactory {
    public Evaluator createEvaluator(Object fixture) {
        return new SelendionEvaluator(fixture);
    }
}
