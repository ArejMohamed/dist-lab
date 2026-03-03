package ServerSide;
import java.net.*;
import java.io.*;

public class SimpleServer {
  public static void main(String args[]) throws IOException {

    // Register service on port 2000
    ServerSocket s = new ServerSocket(2000);
    int count = 1;
    System.out.println("The server started, Waiting for a String");
    while (true)
    {
        Socket s1=s.accept(); // Wait and accept a connection
        // Get a communication stream associated with the socket
        DataInputStream input = new DataInputStream (s1.getInputStream());
        DataOutputStream output = new DataOutputStream (s1.getOutputStream());
        // read a String from the client
        String lower = input.readUTF();
        String upper = lower.toUpperCase();
        // Send a string!
        output.writeUTF("Your String in uppercase is " + upper);
        
        // Close the connection, but not the server socket
        input.close();
        output.close();
        s1.close();
        }
  }
}
