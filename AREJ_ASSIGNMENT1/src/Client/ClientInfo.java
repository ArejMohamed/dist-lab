/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Client;

import java.io.Serializable;

/**
 *
 * @author mroja
 */
public class ClientInfo implements Serializable{
    String name;
    String IP;
    int port;

    public ClientInfo(String name, String IP, int port) {
        this.name = name;
        this.IP = IP;
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public String getIP() {
        return IP;
    }

    public int getPort() {
        return port;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public void setPort(int port) {
        this.port = port;
    }

    
}
