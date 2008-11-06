package testSupport.Fizmez;
/**
 * Instantiates a webserver on the port specified in the config file
 * (./conf/httpd.conf).
 *
 * @author David Bond of fizmez.com
 * @Version 1.3 - changed indenting to tabs, removed case sensitivity to "Accept" string after suggestion from Julian Skidmore
 */

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;

public class FizmezWebServer extends ErrorHandler implements Runnable {
    private ServerSocket serverSocket;
    private Hashtable<String, String> conf;
    private Hashtable<String, String> error;
    private static final String version = "1.3";
    private DataOutputStream dataOutputStream;

    // main()
    public static void main(String s[]) {
        new FizmezWebServer();
    }

    // Constructor
    FizmezWebServer() {
        this.setLoggingLevel(NOTICE);
        notice("FizmezWebServer Started");

        // Overwrite with user defined config
        getConfig();
        getErrors();
        getServerSocket();

        // Create and start thread
        Thread thread = new Thread(this);
        debug("Starting thread");
        thread.start();
    }

    // getServerSocket()
    private void getServerSocket() {
        try {
            String socketString = conf.get("Listen");
            serverSocket = new ServerSocket(new Integer(socketString));
            notice("Listening on port [" + socketString + "]");
        }
        catch (IOException e) {
            error("Could not get server socket.  This or similar program already open on port [" + conf.get("Listen") + "]?");
        }
    }

    private void getConfig() {
        // Default Config
        conf = new Hashtable<String, String>();
        conf.put("ServerRoot", this.getClass().getResource(".").getPath());
        conf.put("Listen", "55555");
        conf.put("DirectoryIndex", "index.html");
        conf.put("WapDirectoryIndex", "index.wml");
        conf.put("ServerString", "Fizmez/" + version + " (Java)");
        conf.put("DefaultType", "text/html");

        // DocumentRoot can be guessed at if not specified
        if (!conf.containsKey("DocumentRoot")) {
            conf.put("DocumentRoot", conf.get("ServerRoot") + "/html");
        }
    }


    private void updateClient(String s) {
        try {
            dataOutputStream.writeBytes(s);
        }
        catch (IOException e) {
            warning("Failed to update client");
        }
    } // end updateClient()

    // execute with environment variables

    private void getErrors() {
        // Default Config
        error = new Hashtable<String, String>();
        error.put("100", "Continue");
        error.put("101", "Switching Protocols");
        error.put("200", "OK");
        error.put("201", "Created");
        error.put("202", "Accepted");
        error.put("203", "Non-Authoritative Information");
        error.put("204", "No Content");
        error.put("205", "Reset Content");
        error.put("206", "Partial Content");
        error.put("300", "Multiple Choices");
        error.put("301", "Moved Permanently");
        error.put("302", "Found");
        error.put("303", "See Other");
        error.put("304", "Not Modified");
        error.put("305", "Use Proxy");
        error.put("307", "Temporary Redirect");
        error.put("400", "Bad Request");
        error.put("401", "Unauthorized");
        error.put("402", "Payment Required");
        error.put("403", "Forbidden");
        error.put("404", "Not Found");
        error.put("405", "Method Not Allowed");
        error.put("406", "Not Acceptable");
        error.put("407", "Proxy Authentication Required");
        error.put("408", "Request Timeout");
        error.put("409", "Conflict");
        error.put("410", "Gone");
        error.put("411", "Length Required");
        error.put("412", "Precondition Failed");
        error.put("413", "Request Entity Too Large");
        error.put("414", "Request-URI Too Long");
        error.put("415", "Unsupported Media Type");
        error.put("416", "Requested Range Not Satisfiable");
        error.put("417", "Expectation Failed");
        error.put("414", "Request-URI Too Long");
        error.put("500", "Internal Server Error");
        error.put("501", "Not Implemented");
        error.put("502", "Bad Gateway");
        error.put("503", "Service Unavailable");
        error.put("504", "Gateway Timeout");
        error.put("505", "HTTP Version Not Supported");
    }

    private String execute(String[] commandArray, boolean blocking, String[] envp) {
        String stdoutString = "";
        String stderrString = "";
        debug("Running system command: [" + commandArray[0] + "]");
        try {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(commandArray, envp, null);
            InputStream stderr = proc.getErrorStream();
            InputStream stdout = proc.getInputStream();
            InputStreamReader isre = new InputStreamReader(stderr);
            BufferedReader bre = new BufferedReader(isre);
            InputStreamReader isro = new InputStreamReader(stdout);
            BufferedReader bro = new BufferedReader(isro);
            String line;
            if (blocking) {
                int exitVal;
                while ((line = bre.readLine()) != null) {
                    stderrString += line + "\n";
                }
                while ((line = bro.readLine()) != null) {
                    stdoutString += line + "\n";
                }
                exitVal = proc.waitFor();
                debug("Process exitValue: " + exitVal + "Stdout was [" + stdoutString + "] Stderr was [" + stderrString + "]");
            }
        }
        catch (Throwable t) {
            sendCode("500");
            warning(t.toString() + "[" + stdoutString + "]");
        }
        return (stdoutString);
    }

    private void sendCode(String code) {
        updateClient("HTTP/1.0 " + code + " " + error.get(code) + "\n");
    }

    public void run() {
        debug("thread running");
        boolean running = true;

        // Main run loop
        while (running) {
            BufferedReader bufferedReader;
            Hashtable<String, String> requestHashtable = new Hashtable<String, String>();
            // Wait for incoming connection
            Socket socket;
            try {
                socket = serverSocket.accept();
                bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                debug("Socket open: [" + socket.toString() + "]");
            }
            catch (IOException e) {
                error("Could not accept incoming socket.");
                break;
            }

            // Loop to serve requests and provide something to break; out of.
            boolean stillServingRequests = true;
            while (stillServingRequests) {
                // Get request
                String line = "nowt";
                String requestLine = "";
                String requestHttpVersion;
                String requestUri = "";
                int lineNumber = 0;
                boolean headerError = false;

                // Get header
                while (line != null && !line.equals("") && socket != null && !headerError) {
                    try {
                        line = bufferedReader.readLine();
                        lineNumber++;
                    }
                    catch (IOException e) {
                        warning("Client disconnected prematurely");
                        socket = null;
                    }

                    if (line == null || socket == null) {
                        // The client aborted
                        notice("The client aborted");
                        headerError = true;
                    } else {
                        // The client has not aborted
                        debug("Line [" + lineNumber + "]: received input [" + line + "]");

                        if (lineNumber == 1) {
                            // Handle GET string
                            requestLine = line;

                            // Regular expression matching for the GET string
                            Pattern pattern;
                            Matcher matcher;

                            pattern = Pattern.compile("^GET .*$");
                            matcher = pattern.matcher(line);
                            if (!matcher.matches()) {
                                sendCode("501");
                                notice("Error 501 Not Implemented: Not a GET method - unsupported");
                                headerError = true;
                            } else {
                                pattern = Pattern.compile("^GET (.*) HTTP/(.*)$");
                                matcher = pattern.matcher(line);
                                if (!matcher.matches()) {
                                    sendCode("400");
                                    notice("400 Bad Request (Malformed GET string [" + requestLine + "])");
                                    headerError = true;
                                } else {
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                                    }
                                    requestUri = matcher.group(1);
                                    requestHttpVersion = matcher.group(2);
                                    debug("Valid HTTP GET request for [" + requestUri + "] using HTTP version [" + requestHttpVersion + "]");
                                }
                            }
                        } else {
                            //Hash out request information
                            int firstSpaceIndex = line.indexOf(" ");
                            if (firstSpaceIndex >= 0) {
                                // New in v1.3: now accepts case insensitive attributes from client. (Ref: RFC822)
                                // Thanks to Julian Skidmore
                                String attribute = line.substring(0, firstSpaceIndex).toLowerCase();
                                String variable = line.substring(firstSpaceIndex + 1);
                                debug("Found attribute/variable pair[" + attribute + "," + variable + "]");
                                requestHashtable.put(attribute, variable);
                            }
                        }
                    }
                } // end HTTP Request Header

                if (!headerError) {

                    // Determine Accept String
                    // New in v1.3: now accepts lower OR upper case "Accept" strings from client.
                    // Thanks to Julian Skidmore
                    String acceptString = requestHashtable.get("accept:");
                    if (acceptString == null) {
                        acceptString = "*/*";
                    }
                    debug("AcceptString=[" + acceptString + "]");

                    // Do we have parameters?
                    String parameters = "";
                    int parametersPosition = requestUri.indexOf("?") + 1;
                    if (parametersPosition > 0) {
                        parameters = requestUri.substring(parametersPosition);
                        requestUri = requestUri.substring(0, parametersPosition - 1);
                        debug("Has parameters: [" + parameters + "] requestUri now [" + requestUri + "]");
                    } else {
                        debug("No parameters, requestUri still [" + requestUri + "]");
                    }

                    // Get mime type
                    // Wap Browser?
                    boolean wapAgent = false;
                    if (acceptString.indexOf("text/vnd.wap.wml") >= 0) {
                        debug("Detected WML client");
                        wapAgent = true;
                    }
                    // Other Browser?
                    else
                    if (acceptString.indexOf("*/*") >= 0 || acceptString.indexOf("text/html") >= 0 || acceptString.length() == 0) {
                        debug("Detected Non-wap client");
                        wapAgent = false;
                    }

                    // Test for no request and replace with index.html(etc)
                    if (requestUri.equals("/")) {
                        if (wapAgent) {
                            requestUri = conf.get("WapDirectoryIndex");
                        } else {
                            requestUri = conf.get("DirectoryIndex");
                        }
                        debug("Nothing specified, delivering [" + requestUri + "]");
                    }

                    // Get mimetype
                    String mimeType = "text/html";

                    // Log this request
                    String dateString = new Date(System.currentTimeMillis()).toString();
                    notice(socket.getInetAddress().getHostName() + " - - [" + dateString + "] \"" + requestLine + "\" 200 -");

                    // Is it executable?
                    if (requestUri.indexOf(".cgi") >= 0) {
                        // Yes

                        String fullPath = conf.get("DocumentRoot") + "/" + requestUri;

                        // Execute it
                        String initialCommand = "/usr/bin/perl";
                        String c[] = {initialCommand, fullPath};
                        String e[] = {"QUERY_STRING=" + parameters,
                                "REMOTE_ADDR=" + socket.getInetAddress().getHostAddress(),
                                "REMOTE_HOST=" + socket.getInetAddress().getHostName(),
                                "REQUEST_METHOD=GET",
                                "PATH_TRANSLATED=" + requestUri
                        };
                        String result = execute(c, true, e);
                        sendCode("200");
                        updateClient(result);
                    } else {
                        // not executable, serve up a file
                        String filename = conf.get("DocumentRoot") + "/" + requestUri;
                        File file = new File(filename.replaceAll("%20", " "));

                        // Does it exist?
                        if (file.exists()) {
                            //Serve up content
                            debug("File [" + filename + "] exists.");

                            byte[] buffer = new byte[8000];

                            //Send file
                            try {
                                FileInputStream is = new FileInputStream(file);
                                // Deliver header
                                sendCode("200");
                                updateClient("Server: " + conf.get("ServerString") + "\n");
                                updateClient("Content-Type: " + mimeType + "\n\n");
                                debug("Mimetype is [" + mimeType + "]");
                                int readBytes = 0;
                                while (readBytes >= 0) {
                                    readBytes = is.read(buffer);
                                    if (readBytes >= 0) {
                                        dataOutputStream.write(buffer, 0, readBytes);
                                    }
                                }
                            }
                            catch (IOException e) {
                                sendCode("500");
                                warning("Failed to update client");
                            }
                        } else {
                            debug("File [" + filename + "] does not exist.	Sending client 404.");
                            sendCode("404");
                        }
                    } // end serving file
                } // end if(!erroredHeader)
                stillServingRequests = false;
            }    // end while(stillServingRequests)
            // Close Connection and Socket
            try {
                dataOutputStream.close();

                // New in 1.2 - close the socket only if it still exists!
                // Bug reported by D. Walters
                if (socket != null) {
                    socket.close();
                }

                debug("Connection closed");
            }
            catch (IOException e) {
                error("Failed to close connection/socket");
            }
        } // end while running
    } // end run()
} // end FizmezWebServer
