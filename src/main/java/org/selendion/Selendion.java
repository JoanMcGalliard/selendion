package org.selendion;

import org.concordion.Concordion;
import org.concordion.api.SpecificationLocator;
import org.concordion.api.SpecificationReader;
import org.concordion.api.EvaluatorFactory;

public class Selendion extends Concordion {
    public Selendion(SpecificationLocator specificationLocator, SpecificationReader specificationReader, EvaluatorFactory evaluatorFactory) {
        super(specificationLocator, specificationReader, evaluatorFactory);
    }
}
