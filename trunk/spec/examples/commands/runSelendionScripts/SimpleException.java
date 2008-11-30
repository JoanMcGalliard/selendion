package examples.commands.runSelendionScripts;

import org.selendion.integration.concordion.SelendionTestCase;

public class SimpleException extends SelendionTestCase {
    public void throwException(String message) throws Exception {
                throw new Exception(message);
    }

}