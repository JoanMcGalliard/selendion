package concordion.test.concordion;

import java.io.IOException;


import org.selendion.internal.SelendionBuilder;
import org.selendion.internal.SelendionEvaluatorFactory;
import org.selendion.internal.SelendionResultRecorder;
import org.selendion.Selendion;
import org.selendion.integration.concordion.SelendionTestCase;
import org.concordion.api.EvaluatorFactory;
import org.concordion.api.Resource;
import nu.xom.Document;
import nu.xom.Nodes;
import selendion.commands.Suite.SuiteTest;
import com.sun.corba.se.impl.encoding.BufferManagerRead;


public class TestRig {

    private Object fixture = null;
    private EvaluatorFactory evaluatorFactory = new SelendionEvaluatorFactory();
    private concordion.test.concordion.StubSource stubSource = new StubSource();
    private String resourceName="/testrig";
    private Class<? extends SelendionTestCase> baseClass;

    public TestRig withFixture(Object fixture) {
        this.fixture = fixture;
        return this;
    }
    public TestRig withResourceName(String resourceName) {
        this.resourceName=resourceName;
        return this;
    }

    public ProcessingResult processFragment(String fragment) {
        return process(wrapFragment(fragment));
    }

    public ProcessingResult process(Resource resource) {
        EventRecorder eventRecorder = new EventRecorder();
        StubTarget stubTarget = new StubTarget();
        Selendion selendion = new SelendionBuilder()
            .withAssertEqualsListener(eventRecorder)
            .withThrowableListener(eventRecorder)
            .withSource(stubSource)
            .withEvaluatorFactory(evaluatorFactory)
            .withTarget(stubTarget)
            .withBaseClass(baseClass)
            .build();

        try {
            SelendionResultRecorder resultSummary = selendion.process(resource, fixture);
            String xml = stubTarget.getWrittenString(resource);
            return new ProcessingResult(resultSummary, eventRecorder, xml);
        } catch (IOException e) {
            throw new RuntimeException("Test rig failed to process specification", e);
        }
    }
    public Nodes unwrapFragment(Document document) {
        return document.query("/html/body/fragment/*");

    }

    public ProcessingResult process(String html) {
        Resource resource = new Resource(resourceName);
        withResource(resource, html);
        return process(resource);
    }

    private String wrapFragment(String fragment) {
        fragment = "<body><fragment>" + fragment + "</fragment></body>";
        return wrapWithNamespaceDeclaration(fragment);
    }

    private String wrapWithNamespaceDeclaration(String fragment) {
        return "<html xmlns:concordion='"
            + SelendionBuilder.NAMESPACE_SELENDION + "'>"
            + fragment
            + "</html>";
    }

    public TestRig withStubbedEvaluationResult(Object evaluationResult) {
        this.evaluatorFactory = new StubEvaluator().withStubbedResult(evaluationResult);
        return this;
    }

    public TestRig withResource(Resource resource, String content) {
        stubSource.addResource(resource, content);
        return this;
    }

    public TestRig withBaseClass(Class<? extends SelendionTestCase> aClass) {
        this.baseClass=aClass;
        return this;
    }
}
