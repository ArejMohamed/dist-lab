package ClientSide;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import Intermediate.Student;

public class Client {

    public static void main(String args[]) throws IOException {

        Socket s1 = new Socket("localhost", 2000);
        ObjectOutputStream out = new ObjectOutputStream(s1.getOutputStream());
        DataInputStream input = new DataInputStream(s1.getInputStream());
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter name:");
        String name = sc.nextLine();
        System.out.println("Enter id:");
        int id = sc.nextInt();
        System.out.println("Enter gpa:");
        double gpa = sc.nextDouble();
        Student s = new Student(name, id, gpa);
        out.writeObject(s);
        System.out.println("The new Student is inserted.");
        input.close();
        out.close();
        s1.close();
    }
}
