// SimpleServer.java: a simple server program
import java.net.*;
import java.io.*;

public class SimpleServer {
  public static void main(String args[]) throws IOException {

    // Register service on port 2000
    ServerSocket s = new ServerSocket(2000);
    int count = 1;
    System.out.println("The server started, Waiting for your letter grade");
    while (true)
    {
        Socket s1=s.accept(); // Wait and accept a connection
        // Get a communication stream associated with the socket
        DataInputStream input = new DataInputStream (s1.getInputStream());
        DataOutputStream output = new DataOutputStream (s1.getOutputStream());
        // read a String from the client
        char grade = input.readChar();
        String response;
        switch (grade)
        {
            case 'A':
            case 'a': response = "Excellent";
                      break;
            case 'B':
            case 'b': response = "Very good";
                      break;
                     
            case 'c':
            case 'C': response = "good";
                      break;
                      
            case 'D':
            case 'd': response = "You could do better";
                      break;
            
            case 'F':
            case 'f': response = "Try harder next time";
                      break;
            
            default: response = "Invalid character";
        }
        // Send a string!
        output.writeUTF(response);
        
        // Close the connection, but not the server socket
        input.close();
        output.close();
        s1.close();
        }
  }
}
