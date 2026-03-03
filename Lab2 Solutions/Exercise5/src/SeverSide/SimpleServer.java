// SimpleServer.java: a simple server program
import java.net.*;
import java.io.*;

public class SimpleServer {
  public static void main(String args[]) throws IOException {

    // Register service on port 2000
    ServerSocket s = new ServerSocket(2000);
    System.out.println("The server started");
    while (true)
    {
        Socket s1=s.accept(); // Wait and accept a connection
        // Get a communication stream associated with the socket
        DataInputStream input = new DataInputStream (s1.getInputStream());
        // read a String from the client
        String response = input.readUTF();
        System.out.println(response);
        // Close the connection, but not the server socket
        input.close();
        s1.close();
        }
  }
}
