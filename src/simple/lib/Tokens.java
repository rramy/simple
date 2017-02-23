/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package simple.lib;

import java.util.List;

/**
 *
 */
public class Tokens extends Listes<String> {

    public Tokens() {
        super();
    }
    
    public Tokens(Tokens tokens) {
        addAll(tokens);
    }
    
    public Tokens(String[] array) {
        addAll(array);
    }
    
    public Tokens(Tokens ... all) {
        for (Tokens t : all) addAll(t);
    }

    public String join(String sep) {
        return join(this, sep);
    }
    
    public Tokens sub(int fr, int to) {
        return (Tokens) super.sub(fr, to);
    }

    public Tokens delete(int index) {
        Tokens t = new Tokens(this);
        t.remove(index);
        return t;
    }
    
    /**
     * Static
     */
    public static String join(List values, Object sep) {
        if (values.size() == 0) return "";
        
        StringBuffer sb = new StringBuffer();
        String _sep = sep.toString();
        
        for (Object value : values) {
            sb.append(value);
            sb.append(_sep);
        }
        
        int l = sb.length()
              - _sep.length();
        
        return sb.substring(0, l);
    }
    
}
