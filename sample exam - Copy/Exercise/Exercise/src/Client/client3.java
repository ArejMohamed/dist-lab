package Client;

import Server.*;
import java.net.*;
import java.io.*;

public class client3 implements Runnable {

    Socket cSocket;
    static ServerGUI s_GUI;

    public client3(Socket cSocket) {
        this.cSocket = cSocket;
    }

    @Override
    public void run() {
        try {
            DataOutputStream out = new DataOutputStream(cSocket.getOutputStream());
            DataInputStream input = new DataInputStream(cSocket.getInputStream());
            String msg = input.readUTF();
             //scanner
           while (msg != "bye"){
           
           //String msg =sc.nextln();
           out.writeUTF("msg");
               System.out.println("msg");

           msg = input.readUTF();
         
           }
            input.close();
            out.close();
            cSocket.close();

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void main(String[] args) {
        try {
            ServerSocket sSocket = new ServerSocket(5000
            );
            s_GUI = new ServerGUI();
            s_GUI.setVisible(true);
            System.out.println("Listening");
            while (true) {
                Socket S = sSocket.accept();
                System.out.println("Connected");
                Thread Client = new Thread(new client3(S));
                Client.start();
            }
        } catch (Exception e) {
            System.out.println(e);
        }

    }

}
