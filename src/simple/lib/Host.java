/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package simple.lib;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;

/**
 *
 */
public class Host {

    private String user;

    private String pass;

    private String host;

    private int port;
    
    public Host() {
        __setHost();
    }

    public Host(int port) {
        __setHost();
        __setPort(port);
    }
    public Host(String host) {
        __setHost(host);
    }
    
    public Host(String host, int port) {
        __setHost(host);
        __setPort(port);
    }
    
    public Host(
            String host, 
            int port,
            String user, 
            String pass) {
        __setHost(host);
        __setPort(port);
        
        this.user = user;
        this.pass = pass;
    }
    
    public Host(
            InetAddress addr, int port) {
        __setHost(addr);
        __setPort(port);        
    }
    
    public Host(Host host) {
        this.user = host.user;
        this.pass = host.pass;
        this.host = host.host;
        this.port = host.port;
    }

    public String getHost() {
        return host;
    }

    public String getIP() throws UnknownHostException {
        return getAddress().getHostAddress();
    }
    
    public InetAddress getAddress() throws UnknownHostException {
        return InetAddress.getByName(host);
    }

    public SocketAddress getSocketAddress() throws UnknownHostException {
        return new InetSocketAddress(getHost(), port);
    }

    public String getUsername() {
        return user;
    }

    public String getPassword() {
        return pass;
    }
    
    public int getPort() {
        return port;
    }
    
    public Host setHostname(String value) {
        Host host = new Host(this);
        host.__setHost(value);

        return host;
    }

    public Host setUsername(String value) {
        Host host = new Host(this);
        host.user = value;

        return host;
    }

    public Host setPassword(String value) {
        Host host = new Host(this);
        host.pass = value;

        return host;
    }
    
    // $user:$pass@$host:$port
    public String toString() {
        StringBuffer sb = new StringBuffer();
        
        if (user != null) {
            sb.append(user);
        if (pass != null)
            sb.append(":")
              .append(pass);
            sb.append("@");
        }
        
        sb.append(getHost());
        
        if (port > 0)
            sb.append(":")
              .append(port);
        
        return sb.toString();
    }

    private void __setPort(int port) {
	if (port < 0 || port > 0xFFFF) 
	    throw new IllegalArgumentException("port out of range:" + port);
        this.port = port;
    }
    
    private void __setHost() {
        host = "localhost";
    }

    private void __setHost(String value) {
        host = value != null ? value : "localhost";
    }

    private void __setHost(InetAddress addr) {
        host = addr.getHostName();
    }

    public static boolean isAnonymous(String username) {
        return username != null && username.equalsIgnoreCase("anonymous");
    }

}
