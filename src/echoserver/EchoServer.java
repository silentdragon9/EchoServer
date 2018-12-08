/*
  This java coding will test the SSL connection and the msg can not be read by Wireshark because being encrypted.
  If the HTTPS_PORT is port 80, the data can be captured by Wireshark.
  The connection will be between EchoServer.java and EchoClient.java

Mohammad Ariff Bin Idris (2017430762)
*/
package echoserver;

import java.io.*;
import java.net.*;
import javax.net.ServerSocketFactory;
import javax.net.ssl.*;

public class EchoServer {

 public static final boolean DEBUG = true;
 public static final int HTTPS_PORT = 443;//8282
 public static final String KEYSTORE_LOCATION = "C:\\Program Files\\Java\\jdk1.8.0_181\\bin\\secAriff.jks";
 public static final String KEYSTORE_PASSWORD = "P@ssword.123";

 // main program
 public static void main(String argv[]) throws Exception {

 // set system properties, alternatively you can also pass them as
 // arguments like -Djavax.net.ssl.keyStore="keystore"....
 System.setProperty("javax.net.ssl.keyStore", KEYSTORE_LOCATION);
 System.setProperty("javax.net.ssl.keyStorePassword", KEYSTORE_PASSWORD);

 if (DEBUG)System.setProperty("javax.net.debug", "ssl:record");

 EchoServer server = new EchoServer();
 server.startServer();
 }

 // Start server
 public void startServer() {
 try {
 ServerSocketFactory ssf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
 SSLServerSocket serversocket = (SSLServerSocket) ssf.createServerSocket(HTTPS_PORT);
 
 while (true) {
 Socket client = serversocket.accept();
 ProcessRequest cc = new ProcessRequest(client);
 }
 } catch (Exception e) {
 System.out.println("Exception:" + e.getMessage());
 }
 }
}

class ProcessRequest extends Thread {
 
 Socket client;
 BufferedReader is;
 DataOutputStream out;

 public ProcessRequest(Socket s) { // constructor
 client = s;
 try {
 is = new BufferedReader(new InputStreamReader(client.getInputStream()));
 out = new DataOutputStream(client.getOutputStream());
 } catch (IOException e) {
 System.out.println("Exception: " + e.getMessage());
 }
 this.start(); // Thread starts here...this start() will call run()
 }

 public void run() {
 try {
 // get a request and parse it.
 String request = is.readLine();
 System.out.println("Received from Client: " + request);
 try {
 out.writeBytes("HTTP/1.0 200 OK\r\n");
 out.writeBytes("Content-Type: text/html\r\n");
 out.writeBytes("<html><head>Server Page: This is Server!</head>\r\n");
 out.writeBytes("<body><b/><p>Client sent: ");
 out.writeBytes(request + "</p></body></html>\r\n");
 out.flush();
 } catch (Exception e) {
 out.writeBytes("Content-Type: text/html\r\n");
 out.writeBytes("HTTP/1.0 400 " + e.getMessage() + "\r\n"); 
 out.flush();
 } finally {
 out.close();
 }
 client.close();
 } catch (Exception e) {
 System.out.println("Exception: " + e.getMessage());
 }
 }
}
