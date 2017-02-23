/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package simple.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.charset.Charset;
import simple.lib.Chaine;

/**
 *
 */
public abstract class SimpleChannel implements ByteChannel {
    
    private ByteBuffer buffer = ByteBuffer.allocate(1024); // 1K - TODO : Stats
    
    protected Charset charset = Charset.defaultCharset();
    
    private void allocate() {  
        ByteBuffer nbuffer = ByteBuffer.allocate(buffer.capacity()*2);        
    synchronized (buffer) {    
         buffer.flip();
        nbuffer.put(buffer);
    }
         buffer = nbuffer;
    }
    
    public ByteBuffer recv() throws IOException {
        buffer.clear();
        
        while (true) {            
            int r = read(buffer);
            int t = buffer.position();
            int a = buffer.capacity() - t;
            
            if (a == 0) allocate(); else break;
        }
        
        buffer.flip();
        return buffer;
    }
    
    public String read() throws IOException {
        return read(getCharset());
    }
    
    public String read(Charset charset) throws IOException { 
        return read(recv(), charset);
    }
    
    public String read(ByteBuffer buffer, Charset charset) {        
        return Chaine.decode(buffer, charset);      
    }
    
    public int write(String message) throws IOException {        
        return write(message, getCharset());
    }
    
    public int write(String message, Charset charset) throws IOException {       
        if (!message.endsWith("\n")) message += "\n"; 
        
        return write(Chaine.encode(message, charset));
    }

    public abstract int read(ByteBuffer bb) throws IOException;

    public abstract int write(ByteBuffer bb) throws IOException;

    public abstract void close() throws IOException;

    public abstract boolean isOpen();
    
    public Charset getCharset() {
        return charset;
    }
    
    public void setCharset(String name) {
        charset = Charset.forName(name);
    }

}
