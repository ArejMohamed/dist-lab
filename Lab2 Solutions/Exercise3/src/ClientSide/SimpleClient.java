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
    DataInputStream input = new DataInputStream(s1.getInputStream());
    DataOutputStream output = new DataOutputStream (s1.getOutputStream());
    Scanner sc = new Scanner(System.in);
    System.out.println("Enter your letter grade:");
    char grade = sc.next().charAt(0);
    output.writeChar(grade);
    String response = input.readUTF();
    System.out.println(response);
    // When done, just close the connection and exit
    input.close();
    output.close();
    s1.close();
  }
}

