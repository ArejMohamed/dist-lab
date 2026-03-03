package ServerSide;

import Intermediate.Student;
import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class MultiThreadServer implements Runnable
{
    Socket cSocket;
    static ArrayList<Student> students = new ArrayList<Student>();
        
    public MultiThreadServer (Socket cSocket)
    {
        this.cSocket = cSocket; 
      
    }   
       
    @Override
    public void run() {
        try {
            ObjectOutputStream out = new ObjectOutputStream(cSocket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(cSocket .getInputStream());
            Student s = (Student)input.readObject();
            students.add(s);
            System.out.println(students);
            out.writeObject(students);
            input.close();
            out.close();
            cSocket.close();             
      
        }
        catch (Exception e)
        {
            System.out.println(e);       
         }
    }
    
    public static void main (String[] args) throws IOException
    {
       ServerSocket sSocket = new ServerSocket (3000);
       System.out.println("Listening");
       while (true)
       {
           Socket S = sSocket.accept();
           System.out.println ("Connected");
           Thread Client  =  new Thread(new MultiThreadServer(S));      
           Client.start();
               }
    }    
    
}