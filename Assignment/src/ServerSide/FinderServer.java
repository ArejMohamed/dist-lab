package ServerSide;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class FinderServer implements Runnable
{
    Socket cSocket;
    static ArrayList<String[]> clients = new ArrayList<String[]>();
    // each entry: {name, host, port, code}
    static AtomicInteger codeCounter = new AtomicInteger(1000);

    public FinderServer(Socket cSocket)
    {
        this.cSocket = cSocket;
    }

    @Override
    public void run() {
        try {
            DataInputStream input = new DataInputStream(cSocket.getInputStream());
            DataOutputStream output = new DataOutputStream(cSocket.getOutputStream());

            String command = input.readUTF();

            if (command.startsWith("REGISTER")) {
                // format: REGISTER name host port
                String[] p = command.split(" ");
                String name = p[1], host = p[2], port = p[3];
                boolean exists = false;
                for (String[] c : clients) {
                    if (c[0].equals(name)) { exists = true; break; }
                }
                if (exists) {
                    output.writeUTF("Already registered: " + name);
                } else {
                    String code = String.valueOf(codeCounter.getAndIncrement());
                    clients.add(new String[]{name, host, port, code});
                    output.writeUTF("Registered: " + name + " Code: " + code);
                    FinderServerForm.log("Registered: " + name + " at " + host + ":" + port + " code=" + code);
                }

            } else if (command.startsWith("FIND")) {
                // format: FIND name
                String name = command.split(" ")[1];
                String result = "Not found";
                for (String[] c : clients) {
                    if (c[0].equals(name)) {
                        result = c[1] + ":" + c[2] + ":" + c[3];
                        break;
                    }
                }
                output.writeUTF(result);
                FinderServerForm.log("Find: " + name + " -> " + result);

            } else if (command.startsWith("UNREGISTER")) {
                // format: UNREGISTER name
                String name = command.split(" ")[1];
                boolean removed = false;
                for (int i = 0; i < clients.size(); i++) {
                    if (clients.get(i)[0].equals(name)) {
                        clients.remove(i);
                        removed = true;
                        break;
                    }
                }
                if (removed) {
                    output.writeUTF("Unregistered: " + name);
                    FinderServerForm.log("Unregistered: " + name);
                } else {
                    output.writeUTF("Not found: " + name);
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
