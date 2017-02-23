/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package simple.lib.mime;

import simple.lib.From;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import static simple.lib.Chaine.unquote;

/**
 *
 */
public class MimeHeaders {

    public final Map<String, String> headers = new HashMap<String, String>();
    
    public void setHeader(String name, String value) {
        if (name.isEmpty())
            throw new NullPointerException();
     
        headers.put(name, value);
    }
    
    public String getHeader(String name) {        
        return headers.get(name);
    }
    
    public String getHeader(String name, String key) {     
        String value = getHeader(name);
        if (value == null) return null;
        
        int l = key.length()+1;
        int x = value.indexOf(key + "=");
        if (x == -1) return null;
        int y = value.indexOf(";", x+l);
        if (y == -1) y = value.length();
        
        value = value.substring(x+l,y);
        
        return unquote(value);
    }
    
    public Set<String> names() {
        return headers.keySet();
    }
    
    public boolean isSet(String name) {
        return headers.containsKey(name);
    }
    
    /** Utils */
    
    public String getCharset() {
        return getHeader("Content-Type", "charset");
    }
    
    public String getBoundary() {
        return getHeader("Content-Type", "boundary");
    }
    
    public String getUserAgent() {
        return getHeader("User-Agent");
    }
    
    public From getFrom() {
        String value = getHeader("From");
        
        return value != null ? new From(value) : null;
    }
    
    public boolean isMultipart() {
        return getBoundary() != null;
    }

}
