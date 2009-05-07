package examples;

import org.selendion.integration.concordion.SelendionTestCase;

import java.io.*;
import java.net.URL;

public class temp extends SelendionTestCase {


    private class AccountDetails {
        public String getAccountNumber() {
            return accountNumber;
        }

        public String getViewingCard() {
            return viewingCard;
        }

        private String accountNumber;
        private String viewingCard;



        public AccountDetails(String line) {
            String[] split = line.split(" ");
             viewingCard =split[0];
            accountNumber=split[1];
        }

    }

    public AccountDetails readFile(String filename) throws Exception {
        String firstLine = null;
        BufferedWriter bw = null;
        // open input file
        URL url = this.getClass().getResource(filename);
        if (url == null) {
            throw new RuntimeException("couldn't find " + filename);
        }
        String result;
        File file = new File (url.getPath());
        BufferedReader br = new BufferedReader(new FileReader(url.getPath()));
        // get first line
        firstLine = br.readLine();
        // setup a buffer to read rest of the file
        int len = (int) file.length();
        char[] buf = new char[len];
        // read rest of file into buffer
        int cnt = br.read(buf, 0, len);
        // if the count is -1 then we have reached the last line
        // set it to 0, so the file can be emptied
        if (cnt == -1)
            cnt = 0;
        // close input file
        br.close();
        br = null;
        // reopen file as output file
        FileOutputStream fos = new FileOutputStream(file);
        bw = new BufferedWriter(new OutputStreamWriter(fos));
        // write the buffer back out to file
        bw.write(buf, 0, cnt);
        bw.flush();
        // close output file
        bw.close();
        bw = null;
        return new AccountDetails(firstLine);
    }
}
