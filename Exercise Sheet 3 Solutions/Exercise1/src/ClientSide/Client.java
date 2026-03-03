package ClientSide;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import ServerSide.Student;

public class Client {

    public static void main(String args[]){
    try
    {
        Socket s1 = new Socket("localhost", 2000);
        DataOutputStream out = new DataOutputStream(s1.getOutputStream());
        ObjectInputStream input = new ObjectInputStream(s1.getInputStream());
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter id:");
        int id = sc.nextInt();
        out.writeInt(id);
        Student s = (Student)input.readObject();
        System.out.println("The Student info is:"+s);
        input.close();
        out.close();
        s1.close();
    }catch (Exception e)
    {   
        System.out.println("An error has occurred.");
    }
    }
}
