package ClientSide;

import java.net.*;
import java.io.*;

public class Client {

    public static String register(String host, int port, String name, String myPort) throws IOException {
        Socket s1 = new Socket(host, port);
        DataOutputStream out = new DataOutputStream(s1.getOutputStream());
        DataInputStream input = new DataInputStream(s1.getInputStream());
        out.writeUTF("REGISTER " + name + " localhost " + myPort);
        String result = input.readUTF();
        input.close();
        out.close();
        s1.close();
        return result;
    }

    public static String unregister(String host, int port, String name) throws IOException {
        Socket s1 = new Socket(host, port);
        DataOutputStream out = new DataOutputStream(s1.getOutputStream());
        DataInputStream input = new DataInputStream(s1.getInputStream());
        out.writeUTF("UNREGISTER " + name);
        String result = input.readUTF();
        input.close();
        out.close();
        s1.close();
        return result;
    }

    public static String find(String host, int port, String name) throws IOException {
        Socket s1 = new Socket(host, port);
        DataOutputStream out = new DataOutputStream(s1.getOutputStream());
        DataInputStream input = new DataInputStream(s1.getInputStream());
        out.writeUTF("FIND " + name);
        String result = input.readUTF();
        input.close();
        out.close();
        s1.close();
        return result;
    }

    public static String connectToPeer(String host, int port, String code) throws IOException {
        Socket s1 = new Socket(host, port);
        DataOutputStream out = new DataOutputStream(s1.getOutputStream());
        DataInputStream input = new DataInputStream(s1.getInputStream());
        out.writeUTF(code);
        String result = input.readUTF();
        input.close();
        out.close();
        s1.close();
        return result;
    }
}
