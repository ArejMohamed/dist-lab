/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Client;


import Client.ClientInfo;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author mroja
 */

public class Client4 implements Runnable {

    Socket cSocket;
   static ClientGUI c_GUI;
    static ArrayList<ClientInfo> clients = new ArrayList<ClientInfo>();

    public Client4(Socket cSocket) {
        this.cSocket = cSocket;
    }

    @Override

        public void run() {
        try {
            DataOutputStream out = new DataOutputStream(cSocket.getOutputStream());
            DataInputStream input = new DataInputStream(cSocket.getInputStream());
            int code = input.readInt(); // sender presents code obtained from Finder
            if (code != c_GUI.getMyCode()) {
                // invalid code - reject connection
                out.writeBoolean(false);
                out.flush();
                c_GUI.appendText("Connection rejected: invalid code " + code);
            } else {
                String senderName = input.readUTF();
                String message = input.readUTF();
                out.writeBoolean(true);
                out.flush();
                c_GUI.appendText(senderName + " sent you a message saying " + message);
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
            ServerSocket sSocket = new ServerSocket(5000);
            c_GUI = new ClientGUI();
            c_GUI.setIPAndPort("localhost", 5000);
            c_GUI.setVisible(true);

            System.out.println(" Listening...");

            while (true) {
                Socket S = sSocket.accept();
                System.out.println("Connected");
                Thread Client = new Thread(new Client4(S));
                Client.start();
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
