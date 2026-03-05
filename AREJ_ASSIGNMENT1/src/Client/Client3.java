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






public class Client3 implements Runnable {

    Socket cSocket;
   static ServerGUI s_GUI;
    static ArrayList<ClientInfo> clients = new ArrayList<ClientInfo>();

    public Client3(Socket cSocket) {
        this.cSocket = cSocket;
    }

    @Override

        public void run() {
        try {
            DataOutputStream out = new DataOutputStream(cSocket.getOutputStream());
            DataInputStream input = new DataInputStream(cSocket.getInputStream());
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
            ServerSocket sSocket = new ServerSocket(4000);
            s_GUI = new ServerGUI();
            s_GUI.setVisible(true);

            System.out.println(" Listening...");

            while (true) {
                Socket S = sSocket.accept();
                System.out.println("Connected");
                Thread Client = new Thread(new Client3(S));
                Client.start();
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
