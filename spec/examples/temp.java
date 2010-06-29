package examples;

import org.selendion.integration.concordion.SelendionTestCase;

import java.sql.*;
import java.util.*;
import java.io.*;
import java.net.URL;


public class temp extends SelendionTestCase {
    static private Hashtable variables = new Hashtable<String, String>();
    private Random random;
    private static final String importDirectory = System.getProperty("selendion.directoryImport", "c:\\temp") ;
    public ArrayList newList(int n) {
        ArrayList array = new ArrayList(n);
        for ( int x=0;  x < n; x++)  {
            array.add(x+1);
        }
        return array;
    }

    public String removeCommas(String s) {
        return s.replaceAll(",", "");
    }

    public temp() {
        super();
        Calendar today = Calendar.getInstance();
        variables.put("TODAY", String.format("%1$tY%1$tm%1$td", today));
        random = new Random(today.getTimeInMillis());
        System.out.println("Running " + this.getClass().getName());
    }

    public void setVariable(String var, String value) {
        variables.put(var, value);
    }

    public String format(String fmt, String s1, String s2) {
        return String.format(fmt, s1, s2);
    }
    public String format(String fmt, String s1, String s2, String s3) {
        return String.format(fmt, s1, s2, s3);
    }

    public String format(String fmt, String s1) {
        return String.format(fmt, s1);
    }

    public String randomString(String base) {

        return base + Math.abs(random.nextInt(9999));
    }

    public String substituteVars(String s) {
        String text = s;
        for (Enumeration keys = variables.keys(); keys.hasMoreElements();) {
            String key = (String) keys.nextElement();
            text = text.replaceAll("%%" + key + "%%", (String) variables.get(key));
        }
        return text;
    }

    public String importExtractFile(String baseFile) throws IOException, InterruptedException {
        Calendar today = Calendar.getInstance();
        System.out.println("Processing " + baseFile);
        String filename = "AC_PIM~BASE_SETTLE_ADO.LDN_EOD."
                + String.format("%1$tY-%1$tm-%1$td", today) + "." + Math.abs(random.nextInt(9999)) + ".xml";

        String tempFilename = "./" + filename;

        File temp = new File(tempFilename);


        String text = readTextFile(baseFile);
        for (Enumeration keys = variables.keys(); keys.hasMoreElements();) {
            String key = (String) keys.nextElement();
            text = text.replaceAll("%%" + key + "%%", (String) variables.get(key));
        }


        FileWriter fstream = new FileWriter(tempFilename);
        BufferedWriter out = new BufferedWriter(fstream);
        out.write(text);
        out.close();
        fstream.close();
        return moveFile(temp.getCanonicalPath(), filename);
    }

    public String moveFile(String sourceFilename, String filename) throws InterruptedException, IOException {

        String outputFilename = importDirectory + "\\" + filename;
        String[] commands = new String[]{"cmd.exe", "/c", "copy",
                "\"" + sourceFilename + "\"",
                "\"" + outputFilename + "\""};

        systemCommand(commands);

        return outputFilename;


    }

    public File createImportFile(String contents, String filename) throws IOException {
        String inputFilename = importDirectory + "/" + filename;

        File input = new File(inputFilename);
        if (input.exists() && !input.delete()) {
            throw new RuntimeException("Failed to delete " + inputFilename);
        }
        String tempFilename = randomString("./tempfile");
        FileWriter fstream = new FileWriter(tempFilename);
        BufferedWriter out = new BufferedWriter(fstream);
        out.write(contents);
        out.close();
        fstream.close();


        File tempFile = new File(tempFilename);

        if (!tempFile.renameTo(input) || !input.exists()) {
            throw new RuntimeException("Failed to move " + tempFilename + " to " + inputFilename);
        }
        return input;
    }
    public boolean waitForFileToVanish(File input) throws InterruptedException {
        int timeout = Integer.parseInt(System.getProperty("selendion.timeout", "30000")) * 5 ;
        long start = Calendar.getInstance().getTimeInMillis();
        while (input.exists()) {
            if (Calendar.getInstance().getTimeInMillis() - start > timeout) {
                throw new RuntimeException("File " + input + " not processed in " + timeout/1000 + " seconds.");
            }
            Thread.sleep(100);
        }
        System.out.println("File " + input.getName() + " processed in " + (Calendar.getInstance().getTimeInMillis() - start)/1000 + " seconds");

        return true;


    }
    public boolean waitForFileToVanish(String fileName) throws InterruptedException {
        return waitForFileToVanish(new File(fileName));
    }

    private static boolean delete(File dir) {
        if (!dir.exists()) {
            return true;
        }
            if (dir.isDirectory()) {
                String[] children = dir.list();
                for (int i=0; i<children.length; i++) {
                    File child = new File(dir, children[i]);
                    boolean success = delete(child);
                    if (!success) {
                        throw new RuntimeException("Failed to delete " + child.getPath());
                    }
                }
            }

            // The directory is now empty so delete it
            return dir.delete();
    }

    protected String readTextFile(String pathname) throws IOException {
        URL url = this.getClass().getResource(pathname);
        BufferedReader inputStream;
        if (url != null) {
            url.getFile();
            inputStream = new BufferedReader(new FileReader(url.getPath()));
        }  else {
            inputStream = new BufferedReader(new FileReader(pathname));
        }
        String result;

        result = "";
        String line;
        while ((line = inputStream.readLine()) != null) {
            result += line + "\n";
        }
        return result;
    }

    // SQL utilities
    private Connection dbConnection = null;


    public String sqlGetColumn(ResultSet rs, String column) throws SQLException {
        ResultSetMetaData md = rs.getMetaData();
        int type = md.getColumnType(rs.findColumn(column));
        String retVal;
        if (type == java.sql.Types.INTEGER) {
            retVal = "" + rs.getInt(column);
        } else if (type == java.sql.Types.BOOLEAN) {
            retVal = "" + rs.getBoolean(column);
        } else if (type == java.sql.Types.TIMESTAMP) {
            retVal = "" + rs.getTimestamp(column);
        } else if (type == java.sql.Types.CHAR) {
            retVal = "" + rs.getString(column);
        } else {
            retVal = rs.getString(column);
        }
        if (rs.wasNull()) {
            return "(null)";
        }
        return retVal;
    }


    private class GetRowsIterator implements Iterable<ResultSet> {
        public Statement getStatement() {
            return statement;
        }

        private Statement statement;
        private ResultSet dbResultSet;

        public GetRowsIterator(Statement statement, ResultSet dbResultSet) {
            this.dbResultSet = dbResultSet;
            this.statement = statement;
        }

        protected void finalize() throws Throwable {
            super.finalize();
        }


        public Iterator<ResultSet> iterator() {
            return new RowsIterator();
        }

        private class RowsIterator implements Iterator<ResultSet> {
            public boolean hasNext() {
                try {
                    boolean value = dbResultSet.next();
                    if (value) {
                        dbResultSet.previous();
                    }
                    return value;
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

            public ResultSet next() {
                try {
                    dbResultSet.next();
                    return dbResultSet;
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            }

            public ResultSet getNext() {
                return next();
            }

            public void remove() {
            }
        }
    }





    public ResultSet sqlGetRow(Iterable<ResultSet> it) throws IOException, SQLException {
        return it.iterator().next();
    }


    public String today() {
        return today("%1$te %1$tb %1$tY");
    }
    public String today(String format) {
        return String.format(format, Calendar.getInstance());
    }
    public String tomorrow(String format) {
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DATE, 1);
        return String.format(format, tomorrow);
    }

    public void deleteFile(String filename) {
        File file = new File(filename);
        if (file.exists() && !file.delete()) {
            throw new RuntimeException("Failed to delete " + filename);
        }
    }

    protected static boolean systemCommand(String[] commands) throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec(commands);
        InputStream stream = process.getErrorStream();
        int length = 1000;
        byte[] b = new byte[length];
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            stream.read(b, 0, length);
            String cmdString = "";
            for (String command : commands) {
                cmdString += " " + command;
            }
            String errorMsg = "Command '" + cmdString + "' failed, error code " + exitCode +
                    ".  Message: '" + new String(b).trim() + "'\n";
            throw new RuntimeException(errorMsg);
        }
        return true;
    }

    public boolean isTrue(String actual) {
        return actual.equals("true");
    }
    public boolean isFalse(String actual) {
        return actual.equals("false");
    }
      // returns a date relative to today.  0 gives today, -1 is yesterday, 1 is tomorrow.  Etc etc
    // // Skips saturdays and sundays
    public String nthWorkingDay(Integer n) {
        Calendar day = Calendar.getInstance();
        while (n != 0) {
            if (n < 0) {
                day.add(Calendar.DAY_OF_MONTH, -1);
                if (day.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY &&
                        day.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                    n++;
                }
            }
            if (n > 0) {
                day.add(Calendar.DAY_OF_MONTH, 1);
                if (day.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY &&
                        day.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                    n--;
                }
            }
        }
        return String.format("%1$td/%1$tm/%1$tY", day);

    }

    //allow us to have the date field blank
    public String nthWorkingDay(String s) {
        if (s.equals("blank"))  {
            return "";
        }
       else return nthWorkingDay(Integer.parseInt( s ));
    }

    public boolean isEqual(String s1, String s2) {
        return s1.equals(s2);
    }

}
