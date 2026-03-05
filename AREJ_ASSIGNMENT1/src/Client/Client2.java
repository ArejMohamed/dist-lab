/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author mroja
 */
public class Client2 implements Runnable {

    Socket cSocket;
    static ClientGUI c_GUI;
    static String myCode;

    public Client2(Socket cSocket) {
        this.cSocket = cSocket;
    }

    @Override
    public void run() {
        try {
            DataInputStream input = new DataInputStream(cSocket.getInputStream());
            DataOutputStream out = new DataOutputStream(cSocket.getOutputStream());

            String ip = input.readUTF(); // receive IP as identifier
            if (!ip.equals(myCode)) {
                out.writeUTF("Rejected");
                input.close();
                out.close();
                cSocket.close();
                return;
            }
            String message = input.readUTF();
            c_GUI.appendText("From " + ip + ": " + message);
            out.writeUTF("Message received");

            input.close();
            out.close();
            cSocket.close();

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void main(String[] args) {

        try {
            String myIP = "localhost";
            Socket regSocket = new Socket("localhost", 1000);
            DataOutputStream regOut = new DataOutputStream(regSocket.getOutputStream());
            regOut.writeInt(1);
            regOut.writeUTF("Client2");
            regOut.writeUTF(myIP);
            regOut.writeInt(3000);
            myCode = myIP;
            regOut.close();
            regSocket.close();

            ServerSocket sSocket = new ServerSocket(3000);
            c_GUI = new ClientGUI();
            c_GUI.setTitle("Client2");
            c_GUI.appendText("Registered as Client2 at " + myCode + ":3000");
            c_GUI.setVisible(true);

            while (true) {
                Socket S = sSocket.accept();
                new Thread(new Client2(S)).start();
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
