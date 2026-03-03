package ServerSide;

import java.net.*;
import java.io.*;

public class Main implements Runnable
{
    Socket cSocket;
        
    public Main (Socket cSocket)
    {
        this.cSocket = cSocket;         
    }   
       
    @Override
    public void run() {
        try {
            DataOutputStream out = new DataOutputStream(cSocket.getOutputStream());
            DataInputStream input = new DataInputStream(cSocket.getInputStream());
            String msg = input.readUTF();
            System.out.println("Hi " + msg);
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
       ServerSocket sSocket = new ServerSocket (5000);
       System.out.println("Listening");
       while (true)
       {
           Socket S = sSocket.accept();
           System.out.println ("Connected");
           Thread Client  =  new Thread(new Main(S));      
           Client.start();
               }
    }    
    
}