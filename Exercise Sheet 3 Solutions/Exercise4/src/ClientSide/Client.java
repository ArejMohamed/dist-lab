package ClientSide;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static void main(String args[]){
    try
    {
        Socket s1 = new Socket("localhost", 4000);
        DataOutputStream out = new DataOutputStream(s1.getOutputStream());
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter name:");
        String name = sc.nextLine();
        System.out.println("Enter id:");
        int id = sc.nextInt();
        out.writeUTF(name+" " +id);        
        out.close();
        s1.close();
    }catch (Exception e)
    {   
        System.out.println("An error has occurred.");
    }
    }
}
