package Intermediate;

import java.net.*;
import java.io.*;

public class Intermediate implements Runnable {

    Socket cSocket;

    public Intermediate(Socket cSocket) {
        this.cSocket = cSocket;

    }

    @Override
    public void run() {
        try {
            DataInputStream in = new DataInputStream(cSocket.getInputStream());
            String name = in.readUTF();

            //opening a client socket with the main Server 
            Socket s1 = new Socket("localhost", 5000);

            DataOutputStream output = new DataOutputStream(s1.getOutputStream());
            output.writeUTF(name);

            in.close();
            output.close();
            s1.close();

            cSocket.close();

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket sSocket = new ServerSocket(4000);
        System.out.println("Listening");
        while (true) {
            Socket S = sSocket.accept();
            System.out.println("Connected");
            Thread Client = new Thread(new Intermediate(S));
            Client.start();
        }
    }

}
