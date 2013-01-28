package concordion.spec.concordion.command.execute;

import org.selendion.integration.concordion.SelendionTestCase;
import org.concordion.internal.command.AssertEqualsFailureEvent;

import concordion.test.concordion.ProcessingResult;
import concordion.test.concordion.TestRig;

public class ExecutingTablesTest extends SelendionTestCase {

    public Result process(String fragment) throws Exception {
        
        ProcessingResult r = new TestRig()
            .withFixture(this)
            .processFragment(fragment);
        
        Result result = new Result();
        result.successCount = r.getSuccessCount();
        result.failureCount = r.getFailureCount();
        result.exceptionCount = r.getExceptionCount();
        
        AssertEqualsFailureEvent lastEvent = r.getLastAssertEqualsFailureEvent();
        if (lastEvent != null) {
            result.lastActualValue = lastEvent.getActual();
            result.lastExpectedValue = lastEvent.getExpected();
        }
        
        return result;
    }
    
    public String generateUsername(String fullName) {
        return fullName.replaceAll(" ", "").toLowerCase();
    }

    class Result {
        public long successCount;
        public long failureCount;
        public long exceptionCount;
        public String lastExpectedValue;
        public Object lastActualValue;
    }
    
}
