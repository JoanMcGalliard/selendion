import org.selendion.integration.concordion.SelendionTestCase;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: McGalliJ
 * Date: 08-Apr-2009
 * Time: 17:12:45
 * To change this template use File | Settings | File Templates.
 */
public class PaymentDetails extends SelendionTestCase {
    public String readFile(String pathname) throws IOException {
        URL url = this.getClass().getResource(pathname);
        if (url != null) {
            String result;

            url.getFile();

            BufferedReader inputStream = new BufferedReader(new FileReader(url.getPath()));
            result = "";
            String line;
            while ((line = inputStream.readLine()) != null) {
                result += line;
            }
            return cleanup(result);
        }
        return null;
    }
    private String cleanup(String string) {
        return string.replaceAll("[\\n\\r ][\\n\\r ]*", " ").replaceAll(" *<", "<").replaceAll("> *", ">").trim();
    }
    
}
