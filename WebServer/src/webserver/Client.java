/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webserver;

/**
 *
 * @author arman
 */
class Client {
    public String ip;
    public int port;

    public Client(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    @Override
    public boolean equals(Object obj) {
        Client c=(Client) obj;
        return (ip.equals(c.ip) && port==c.port);
    }

    @Override
    public String toString() {
        return ("client(ip:"+ip+",port:"+port+")");
    }
    
    
    
    
    
}
