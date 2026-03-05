/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Server;


import Client.ClientInfo;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author mroja
 */






public class FinderServer implements Runnable {

    Socket cSocket;
   static ServerGUI s_GUI;
    static ArrayList<ClientInfo> clients = new ArrayList<ClientInfo>();
    static final Random random = new Random();

    public FinderServer(Socket cSocket) {
        this.cSocket = cSocket;
    }

    @Override
    public void run() {

        try {

           
          
DataInputStream input = new DataInputStream(cSocket.getInputStream());
DataOutputStream out = new DataOutputStream(cSocket.getOutputStream());
            int choice = input.readInt(); // 1 = Register , 2 = Find

            switch (choice) {

                // -------------------- OPTION 1 : REGISTER --------------------
                case 1:
                    String name = input.readUTF();
                    String ip = input.readUTF();
                    int port = input.readInt();
                    
                    int code;
                    do {
                        code = random.nextInt(900000) + 100000; // 6-digit unique code
                    } while (isCodeInUse(code));
                    ClientInfo c = new ClientInfo(name, ip, port, code);
                    clients.add(c);

                    s_GUI.appendText("Registered: " + name + " | code: " + code);
                    out.writeInt(code); // send code back to registering client
                    break;

                // -------------------- OPTION 2 : FIND CLIENT --------------------
                case 2:
                    String searchName = input.readUTF();
                    ClientInfo found = null;

                    for (ClientInfo cl : clients) {
                        if (cl.getName().equals(searchName)) {
                            found = cl;
                            break;
                        }
                    }
                  ObjectOutputStream Bout = new ObjectOutputStream(cSocket.getOutputStream());
                    if (found != null) {
                        Bout.writeBoolean(true);
                       Bout.writeObject(found);
                        s_GUI.appendText("Client Found: " + searchName);
                    } else {
                        Bout.writeBoolean(false);
                        s_GUI.appendText("Client Not Found: " + searchName);
                    }
                      Bout.close();
                    break;
            }

            input.close();
            out.close();
            cSocket.close();

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static boolean isCodeInUse(int code) {
        for (ClientInfo cl : clients) {
            if (cl.getUniqueCode() == code) return true;
        }
        return false;
    }

    public static void main(String[] args) {

        try {
            ServerSocket sSocket = new ServerSocket(1000);
            s_GUI = new ServerGUI();
            s_GUI.setVisible(true);

            System.out.println(" Listening...");

            while (true) {
                Socket S = sSocket.accept();
                System.out.println("Connected");
                Thread Client = new Thread(new FinderServer(S));
                Client.start();
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
