package ClientSide;

// SimpleClient.java: a simple client program
import java.net.*;
import java.io.*;
import java.util.Scanner;
import javax.swing.JOptionPane;

public class SimpleClient {
  public static void main(String args[]) throws IOException {

    // Open your connection to a server, at port 2000
    Socket s1 = new Socket("localhost",2000);
    // Get an input file handle from the socket and read the input
    DataOutputStream output = new DataOutputStream(s1.getOutputStream());
    output.writeUTF("Ahmed 1234");    
    // When done, just close the connection and exit
    output.close();
    s1.close();
  }
}

