/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Client;


import Server.*;
import Client.ClientInfo;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author mroja
 */






public class Client4 implements Runnable {

    Socket cSocket;
   static ServerGUI s_GUI;
    static String myCode;
    static ArrayList<ClientInfo> clients = new ArrayList<ClientInfo>();

    public Client4(Socket cSocket) {
        this.cSocket = cSocket;
    }

    @Override

        public void run() {
        try {
            DataOutputStream out = new DataOutputStream(cSocket.getOutputStream());
            DataInputStream input = new DataInputStream(cSocket.getInputStream());
            
            String receivedCode = input.readUTF();
            if (myCode == null || !receivedCode.equals(myCode)) {
                s_GUI.appendText("Connection REJECTED: Invalid code");
                out.writeUTF("Rejected: Invalid code");
                input.close();
                out.close();
                cSocket.close();
                return;
            }
            s_GUI.appendText("Connection accepted. Code verified.");
            
            String message = input.readUTF();
            
           while (!message .equals("bye")){
           
           
           out.writeUTF("message");
               System.out.println("message");

           message = input.readUTF();
         
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
            Socket regSocket = new Socket("localhost", 1000);
            DataOutputStream regOut = new DataOutputStream(regSocket.getOutputStream());
            DataInputStream regIn = new DataInputStream(regSocket.getInputStream());
            regOut.writeInt(1);
            regOut.writeUTF("Client4");
            regOut.writeUTF("localhost");
            regOut.writeInt(5000);
            myCode = regIn.readUTF();
            System.out.println("Registered with Finder. Code: " + myCode);
            regIn.close();
            regOut.close();
            regSocket.close();

            ServerSocket sSocket = new ServerSocket(5000);
            s_GUI = new ServerGUI();
            s_GUI.setTitle("Client4 - Port 5000");
            s_GUI.appendText("Registered. Code: " + myCode);
            s_GUI.setVisible(true);

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
