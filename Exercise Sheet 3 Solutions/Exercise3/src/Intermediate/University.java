package Intermediate;

import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class University implements Runnable
{
    Socket cSocket;
    ArrayList<Student> students;
    
    public University (Socket cSocket)
    {
        this.cSocket = cSocket; 
       
    }   
       
    @Override
    public void run() {
        try {
            ObjectInputStream in = new ObjectInputStream(cSocket .getInputStream());
            Student s = (Student)in.readObject();
             
             //opening a client socket with the main Server 
             Socket s1 = new Socket("localhost",3000);
             
             ObjectInputStream input = new ObjectInputStream(s1.getInputStream());
             ObjectOutputStream output = new ObjectOutputStream(s1.getOutputStream());

             output.writeObject(s);
             ArrayList<Student> students = (ArrayList<Student>)input.readObject();
                
             System.out.println("The new list is:" + students);
    input.close();
    in.close();
    output.close();
    s1.close();

    cSocket.close();             
            
        }
        catch (Exception e)
        {
            System.out.println(e);       
         }
    }
    
    public static void main (String[] args) throws IOException
    {
       ServerSocket sSocket = new ServerSocket (2000);
       System.out.println("Listening");
       while (true)
       {
           Socket S = sSocket.accept();
           System.out.println ("Connected");
           Thread Client  =  new Thread(new University(S));      
           Client.start();
               }
    }    
    
}