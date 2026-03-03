package finderserver;

import java.io.*;
import java.net.Socket;

/**
 * Handles one client connection to the Finder Server.
 * Supported commands (newline-terminated, pipe-delimited):
 *   REGISTER|name|host|port  -> OK|uniqueCode  or  ERROR|message
 *   FIND|targetName          -> FOUND|host|port|uniqueCode  or  NOTFOUND
 *   UNREGISTER|name|code     -> OK  or  ERROR|message
 */
public class ClientHandler implements Runnable {

    private Socket socket;
    private FinderServerGUI gui;

    public ClientHandler(Socket socket, FinderServerGUI gui) {
        this.socket = socket;
        this.gui = gui;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            String line;
            while ((line = in.readLine()) != null) {
                String[] parts = line.split("\\|");
                String command = parts[0];

                if (command.equals("REGISTER")) {
                    if (parts.length >= 4) {
                        String name = parts[1];
                        String host = parts[2];
                        int port;
                        try {
                            port = Integer.parseInt(parts[3]);
                        } catch (NumberFormatException ex) {
                            out.println("ERROR|Invalid port number");
                            continue;
                        }
                        String code = gui.registerClient(name, host, port);
                        if (code != null) {
                            out.println("OK|" + code);
                        } else {
                            out.println("ERROR|Name already registered");
                        }
                    } else {
                        out.println("ERROR|Invalid REGISTER format");
                    }

                } else if (command.equals("FIND")) {
                    if (parts.length >= 2) {
                        String targetName = parts[1];
                        ClientRecord record = gui.findClient(targetName);
                        if (record != null) {
                            out.println("FOUND|" + record.getHost() + "|" + record.getPort() + "|" + record.getUniqueCode());
                        } else {
                            out.println("NOTFOUND");
                        }
                    } else {
                        out.println("ERROR|Invalid FIND format");
                    }

                } else if (command.equals("UNREGISTER")) {
                    if (parts.length >= 3) {
                        String name = parts[1];
                        String code = parts[2];
                        boolean success = gui.unregisterClient(name, code);
                        out.println(success ? "OK" : "ERROR|Invalid name or code");
                    } else {
                        out.println("ERROR|Invalid UNREGISTER format");
                    }

                } else {
                    out.println("ERROR|Unknown command");
                }
            }
        } catch (Exception e) {
            gui.log("Client disconnected: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (Exception e) {
            }
        }
    }
}
