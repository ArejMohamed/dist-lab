package ClientSide;

import java.net.*;
import java.io.*;

/*
 * Listens for direct incoming connections from other clients.
 * Accepts a connection only if the caller supplies the correct unique code
 * that was assigned by the Finder Server at registration time.
 */
public class PeerServer implements Runnable {

    int port;
    String myCode;

    public PeerServer(int port, String myCode) {
        this.port = port;
        this.myCode = myCode;
    }

    @Override
    public void run() {
        try {
            ServerSocket ss = new ServerSocket(port);
            ClientForm.log("Peer server listening on port " + port);
            while (true) {
                Socket s = ss.accept();
                DataInputStream input = new DataInputStream(s.getInputStream());
                DataOutputStream output = new DataOutputStream(s.getOutputStream());
                // command: CONNECT senderName code
                String cmd = input.readUTF();
                String[] parts = cmd.split(" ");
                if (parts.length >= 3 && parts[2].equals(myCode)) {
                    output.writeUTF("ACCEPTED Hello " + parts[1] + "!");
                    ClientForm.log("Connection ACCEPTED from: " + parts[1]);
                } else {
                    output.writeUTF("REJECTED Invalid code");
                    ClientForm.log("Connection REJECTED");
                }
                input.close();
                output.close();
                s.close();
            }
        } catch (Exception e) {
            System.out.println(e);
            ClientForm.log("Peer server error: " + e.getMessage());
        }
    }
}
