package ServerSide;

import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class University implements Runnable
{
    Socket cSocket;
    static ArrayList<Student> students = new ArrayList<Student>();
        
    public University (Socket cSocket)
    {
        this.cSocket = cSocket; 
        students.add(new Student("Ahmed",1234,3.5));
        students.add(new Student("Ali",1569,2.5));
        students.add(new Student("Nada",1699,2.0));
    }   
       
    @Override
    public void run() {
        try {
            ObjectOutputStream out = new ObjectOutputStream(cSocket.getOutputStream());
            DataInputStream input = new DataInputStream(cSocket.getInputStream());
            int id = input.readInt();
            Student s1 = null;
            for (Student s:students)
            {
                if (s.getId()==id)
                {
                     s1 = s;
                     break;
                }
            }
            out.writeObject(s1);
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