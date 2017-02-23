/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package simple.net;

import java.io.IOException;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SocketChannel;
import simple.io.SimpleSocket;
import simple.lib.Host;

/**
 *
 */
public class TCPClient extends SimpleSocket {
    private SocketChannel channel;

    public void open(String host, int port) throws IOException {
        open(new Host(host, port));
    }

    public void open(Host host) throws IOException {        
        channel = SocketChannel.open();
        channel.socket().setSoTimeout(3000);
        channel.connect(host.getSocketAddress());
    }
    
    // SocketChannel#read(ByteBuffer) timeout doesn't work otherwise
    public int read(ByteBuffer buffer) throws IOException {
        ReadableByteChannel reader = Channels.newChannel(
                channel.socket().getInputStream());
        
        return reader.read(buffer);
    }

    public int write(ByteBuffer buffer) throws IOException {
        
        return channel.write(buffer);
    }

    public void close() throws IOException {
        
        channel.close();
    }

    public Host getRemoteHost() {
        return channel != null && 
               channel.isOpen() ? 
                    new Host(channel.socket().getInetAddress(), 
                             channel.socket().getPort()):
                    null;
    }

    public Host getLocalHost() {        
        return channel != null && 
               channel.isOpen() ? 
                    new Host(channel.socket().getLocalAddress(), 
                             channel.socket().getLocalPort()):
                    null;
    }

    public int setSocketTimeout(int value) throws SocketException {
        int timeout = getSocketTimeout();
        
        channel.socket().setSoTimeout(value);
        
        return timeout;
    }

    private int getSocketTimeout() throws SocketException {
        return channel.socket().getSoTimeout();
    }

    public boolean isOpen() {
        return channel != null && channel.isOpen();
    }

}
