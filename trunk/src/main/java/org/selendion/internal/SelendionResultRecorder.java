package org.selendion.internal;

import org.concordion.internal.SummarizingResultRecorder;
import org.concordion.api.Element;
import org.concordion.api.Specification;

public class SelendionResultRecorder extends SummarizingResultRecorder {
    private Specification specification;

    public Specification getResultSpecification() {
        return specification;
    }
    public void setResultSpecification(Specification specification) {
        this.specification = specification;
    }

}
