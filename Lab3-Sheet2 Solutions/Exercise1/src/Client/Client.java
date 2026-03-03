package Client;

import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Client {
    public static void main(String args[]) {
        try {
            Socket s1 = new Socket("localhost", 3000);
            DataInputStream input = new DataInputStream(s1.getInputStream());
            DataOutputStream output = new DataOutputStream(s1.getOutputStream());
            Scanner sc = new Scanner(System.in);
            System.out.println("Enter your name:");
            String name = sc.nextLine();
            output.writeUTF(name);
            String response = input.readUTF();
            System.out.println(response);
            input.close();
            s1.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
