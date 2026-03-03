package Server;

import java.net.*;
import java.io.*;

public class Server {
    public static void main(String args[]) throws IOException {
        ServerSocket ss = new ServerSocket(3000);
        int count = 1;
        System.out.println("Server started, waiting for clients...");
        while (true) {
            Socket s1 = ss.accept();
            DataInputStream input = new DataInputStream(s1.getInputStream());
            DataOutputStream output = new DataOutputStream(s1.getOutputStream());
            String name = input.readUTF();
            output.writeUTF("Welcome " + name + ", you are client number " + count);
            count++;
            System.out.println(name + " connected to server");
            input.close();
            output.close();
            s1.close();
        }
    }
}
