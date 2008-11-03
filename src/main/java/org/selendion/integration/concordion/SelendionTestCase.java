/*
        Copyright Joan McGalliard, 2008
*/

package org.selendion.integration.concordion;

import org.concordion.integration.junit3.ConcordionTestCase;
import org.concordion.api.ResultSummary;
import org.concordion.api.Evaluator;
import org.selendion.internal.SelendionBuilder;
import org.selendion.internal.SelendionEvaluatorFactory;
import org.selendion.Selendion;

import java.sql.*;


public abstract class SelendionTestCase extends ConcordionTestCase {
    private boolean expectedToPass = true;
    private Evaluator evaluator=null;

    public void setExpectedToPass(boolean expectedToPass) {
        this.expectedToPass = expectedToPass;
    }
    public Evaluator getEvaluator () {
        return evaluator;
    }

    public void testProcessSpecification() throws Throwable {
        Selendion selendion= new SelendionBuilder().withEvaluatorFactory(new SelendionEvaluatorFactory()).build();
        ResultSummary resultSummary = selendion.process(this);
        this.evaluator = selendion.getEvaluator();
        resultSummary.print(System.out);
        if (expectedToPass) {
            resultSummary.assertIsSatisfied();
        } else {
            assertTrue("Test is not expected to pass, yet is passing", resultSummary.getExceptionCount() + resultSummary.getFailureCount() > 0);
        }
    }
    public void testProcessSpecification(Evaluator evaluator) throws Throwable {
        Selendion selendion= new SelendionBuilder().withEvaluator(evaluator).build();
        ResultSummary resultSummary = selendion.process(this);
        this.evaluator = selendion.getEvaluator();
        resultSummary.print(System.out);
        if (expectedToPass) {
            resultSummary.assertIsSatisfied();
        } else {
            assertTrue("Test is not expected to pass, yet is passing", resultSummary.getExceptionCount() + resultSummary.getFailureCount() > 0);
        }
    }
    public ResultSet executeQueryAgainstE05(String query) throws Exception {

        DriverManager.registerDriver        // load driver
                (new oracle.jdbc.driver.OracleDriver());

        Connection con = DriverManager.getConnection
                ("jdbc:oracle:thin:@chorddbe05:1521:CCS051T", "readonly", "readonly");

        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        if (rs.next()) {
            return rs;
        }
        throw new Exception("oh no!");
    }
    public String getColumn(ResultSet rs, String column) throws SQLException {
        return rs.getString(column ) ;
    }
    


}
