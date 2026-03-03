// SimpleServer.java: a simple server program
import java.net.*;
import java.io.*;

public class SimpleServer {
  public static void main(String args[]) throws IOException {

    // Register service on port 2000
    ServerSocket s = new ServerSocket(2000);
    int count = 1;
    System.out.println("The server started");
    while (true)
    {
        Socket s1=s.accept(); // Wait and accept a connection
        // Get a communication stream associated with the socket
        DataOutputStream output = new DataOutputStream (s1.getOutputStream());
        // read a String from the client
        String response = java.time.LocalDate.now().toString();
        // Send a string!
        output.writeUTF(response);
        
        // Close the connection, but not the server socket
        output.close();
        s1.close();
        }
  }
}
