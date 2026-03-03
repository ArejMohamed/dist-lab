package ClientSide;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static void main(String args[]) throws IOException {

        Socket s1 = new Socket("localhost", 2000);
        DataOutputStream out = new DataOutputStream(s1.getOutputStream());
        DataInputStream input = new DataInputStream(s1.getInputStream());
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter first number");
        int num1 = sc.nextInt();
        System.out.println("Select Operator (+,-,*,/)");
        char operator = sc.next().charAt(0);
        System.out.println("Enter Second number");
        int num2 = sc.nextInt();
        out.writeInt(num1);
        out.writeChar(operator);
        out.writeInt(num2);
        int result = input.readInt();
        System.out.println("The result is:" + result);
        input.close();
        out.close();
        s1.close();
    }
}
