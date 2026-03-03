package ClientSide;

import java.net.*;
import java.io.*;

public class PeerListener implements Runnable {

    Socket socket;
    String myCode;

    public PeerListener(Socket socket, String myCode) {
        this.socket = socket;
        this.myCode = myCode;
    }

    @Override
    public void run() {
        try {
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            String code = input.readUTF();
            if (code.equals(myCode)) {
                output.writeUTF("Connected");
                ClientForm.log("Peer connected with valid code");
            } else {
                output.writeUTF("Invalid code");
                ClientForm.log("Peer rejected: invalid code");
            }
            input.close();
            output.close();
            socket.close();
        } catch (Exception e) {
            ClientForm.log("PeerListener error: " + e.getMessage());
        }
    }
}
