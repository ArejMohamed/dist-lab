package ServerSide;

import java.net.*;
import java.io.*;

public class MultiThreadServer implements Runnable
{
    Socket cSocket;
    public MultiThreadServer (Socket cSocket)
    {
        this.cSocket = cSocket;        
    }   
       
    @Override
    public void run() {
        try {
            DataOutputStream out = new DataOutputStream(cSocket.getOutputStream());
            DataInputStream input = new DataInputStream(cSocket .getInputStream());
            int num1 = input.readInt();
            char operator = input.readChar();
            int num2 = input.readInt();
            int result;
            switch (operator)
            {
                case '+': result = num1+num2;
                          break;
                case '-': result = num1-num2;
                          break;
                case '*': result = num1*num2;
                          break;
                case '/': result = num1/num2;
                          break;
                default: result = -1;
            }
            out.writeInt(result);
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