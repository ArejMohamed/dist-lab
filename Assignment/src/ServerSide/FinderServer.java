package ServerSide;

import java.net.*;
import java.io.*;
import java.util.ArrayList;

/*
 * Handles one client connection to the Finder Server.
 * Reads one command string and replies, then closes.
 * Commands:
 *   REGISTER name host port  -> OK code  |  ERROR message
 *   FIND name                -> FOUND host port code  |  NOTFOUND
 *   UNREGISTER name code     -> OK  |  ERROR
 */
public class FinderServer implements Runnable {

    Socket cSocket;
    static ArrayList<String[]> clients = new ArrayList<String[]>();
    // each entry: {name, host, port, code}

    public FinderServer(Socket cSocket) {
        this.cSocket = cSocket;
    }

    @Override
    public void run() {
        try {
            DataInputStream input = new DataInputStream(cSocket.getInputStream());
            DataOutputStream output = new DataOutputStream(cSocket.getOutputStream());

            String command = input.readUTF();

            if (command.startsWith("REGISTER")) {
                // REGISTER name host port
                String[] p = command.split(" ");
                if (p.length < 4) { output.writeUTF("ERROR Invalid format"); input.close(); output.close(); cSocket.close(); return; }
                String name = p[1], host = p[2], port = p[3];
                boolean exists = false;
                for (String[] c : clients) {
                    if (c[0].equals(name)) { exists = true; break; }
                }
                if (exists) {
                    output.writeUTF("ERROR Name already registered");
                } else {
                    String code = String.valueOf((int)(Math.random() * 9000) + 1000);
                    clients.add(new String[]{name, host, port, code});
                    output.writeUTF("OK " + code);
                    FinderServerForm.log("Registered: " + name + " at " + host + ":" + port + "  code=" + code);
                }

            } else if (command.startsWith("FIND")) {
                // FIND name
                String[] p = command.split(" ");
                if (p.length < 2) { output.writeUTF("ERROR Invalid format"); input.close(); output.close(); cSocket.close(); return; }
                String name = p[1];
                boolean found = false;
                for (String[] c : clients) {
                    if (c[0].equals(name)) {
                        output.writeUTF("FOUND " + c[1] + " " + c[2] + " " + c[3]);
                        FinderServerForm.log("Find: " + name + " -> found");
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    output.writeUTF("NOTFOUND");
                    FinderServerForm.log("Find: " + name + " -> not registered");
                }

            } else if (command.startsWith("UNREGISTER")) {
                // UNREGISTER name code
                String[] p = command.split(" ");
                if (p.length < 3) { output.writeUTF("ERROR Invalid format"); input.close(); output.close(); cSocket.close(); return; }
                String name = p[1], code = p[2];
                boolean removed = false;
                for (int i = 0; i < clients.size(); i++) {
                    if (clients.get(i)[0].equals(name) && clients.get(i)[3].equals(code)) {
                        clients.remove(i);
                        output.writeUTF("OK");
                        FinderServerForm.log("Unregistered: " + name);
                        removed = true;
                        break;
                    }
                }
                if (!removed) {
                    output.writeUTF("ERROR");
                }
            }

            input.close();
            output.close();
            cSocket.close();

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
