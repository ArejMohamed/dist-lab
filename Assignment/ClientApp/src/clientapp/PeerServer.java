package clientapp;

import java.io.*;
import java.net.*;

/**
 * Background thread that listens for direct peer connections.
 * Other clients connect here after obtaining this client's details
 * from the Finder Server. They must supply the correct unique code
 * (assigned by the Finder) to be accepted.
 */
public class PeerServer implements Runnable {

    private int port;
    private String myName;
    private String myCode;
    private ClientGUI gui;
    private ServerSocket serverSocket;
    private volatile boolean running;

    public PeerServer(int port, String myName, String myCode, ClientGUI gui) {
        this.port = port;
        this.myName = myName;
        this.myCode = myCode;
        this.gui = gui;
    }

    public void stop() {
        running = false;
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void run() {
        running = true;
        try {
            serverSocket = new ServerSocket(port);
            gui.log("Peer server listening on port " + port);
            while (running) {
                try {
                    Socket s = serverSocket.accept();
                    final Socket peer = s;
                    new Thread(new Runnable() {
                        public void run() {
                            handlePeer(peer);
                        }
                    }).start();
                } catch (Exception e) {
                    if (running) {
                        gui.log("Peer accept error: " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            gui.log("Peer server error: " + e.getMessage());
        }
    }

    private void handlePeer(Socket s) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            PrintWriter out = new PrintWriter(s.getOutputStream(), true);

            String line = in.readLine();
            if (line != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 3 && parts[0].equals("CONNECT")) {
                    String remoteName = parts[1];
                    String code = parts[2];
                    if (code.equals(myCode)) {
                        out.println("ACCEPTED|Hello " + remoteName + "! You are now connected to " + myName);
                        gui.log("Incoming connection ACCEPTED from: " + remoteName);
                    } else {
                        out.println("REJECTED|Invalid unique code");
                        gui.log("Incoming connection REJECTED from: " + remoteName + " (wrong code)");
                    }
                } else {
                    out.println("REJECTED|Invalid protocol");
                }
            }
        } catch (Exception e) {
            gui.log("Peer connection error: " + e.getMessage());
        } finally {
            try {
                s.close();
            } catch (Exception e) {
            }
        }
    }
}
