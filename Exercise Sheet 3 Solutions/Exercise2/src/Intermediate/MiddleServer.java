package Intermediate;

import java.net.*;
import java.io.*;

public class MiddleServer implements Runnable
{
    Socket cSocket;
    public MiddleServer (Socket cSocket)
    {
        this.cSocket = cSocket;        
    }   
       
    @Override
    public void run() {
        try {
            DataOutputStream out = new DataOutputStream(cSocket.getOutputStream());
            DataInputStream in = new DataInputStream(cSocket .getInputStream());
            int num1 = in.readInt();
            char operator = in.readChar();
            int num2 = in.readInt();
             
             //opening a client socket with the main Server 
             Socket s1 = new Socket("localhost",3000);
             DataOutputStream output = new DataOutputStream(s1.getOutputStream());
             DataInputStream input = new DataInputStream(s1.getInputStream());
             output.writeInt(num1);
             output.writeChar(operator);
             output.writeInt(num2);
             int result = input.readInt();
                
             //writing from midddle server to client
             out.writeInt(result);
             System.out.println("The result is:"+result);
    
    input.close();
    in.close();
    output.close();
    out.close();
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
           Thread Client  =  new Thread(new MiddleServer(S));      
           Client.start();
               }
    }    
    
}