/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package simple.lib;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

/**
 *
 */
public class Chaine {

    public static Tokens split(String value, String regexp) {
        return value != null 
             ? new Tokens(value.split(regexp))
             : new Tokens();
    }

    public static Tokens lines(String value) {
        value = value.replace("\r\n", "\n");
        
        return split(value, "\n");
    }

    public static String line(String value) {
        return sub(value, "\n");
    }
    
    public static String sub(String value, int x) {
        return value.substring(x);
    }

    public static String sub(String value, int x, int y) {
        return value.substring(x, y);
    }
    
    public static String sub(String value, String to) {
        int x = value.indexOf(to);
        
        return sub(value, x);
    }

    public static String sub(String value, String fr, String to) { 
        int l = fr.length();
        
        int x = search(value, fr);       
        if (x == -1) return null;
        int y = search(value, to, x+l);
        if (y == -1) return null;
        
        return sub(value, x, y);
    }

    public static String trim(String value) {
        return value != null ? value.trim() : "";
    }

    public static String decode(ByteBuffer buffer, Charset charset) {
        return decode(charset.decode(buffer));
    }

    public static String decode(CharBuffer buffer) {
        return buffer.toString();
    }

    public static ByteBuffer encode(String message, Charset charset) {
        return charset.encode(message);
    }

    public static boolean isEmpty(String value) {
        return value == null || trim(value).length() == 0;
    }
    
    public static int toInt(String value) {
        return Integer.parseInt(value);
    }
    
    public static int search(String value, String regexp) {
        return value != null ? value.indexOf(regexp) : 0;
    }
    
    public static int search(String value, String regexp, int index) {
        return value != null ? value.indexOf(regexp, index) : -1;
    }
    
    public static int searchEmptyLine(String value) {
        for (int x = 0;
                 x < value.length();) {
             int y = search(value, "\n", x+1);           
             
             if (y == -1) return -1;    
                  
             if (isEmpty(trim(sub(value,x,y)))) return x;
             
             x = y;
        }
        
        return -1;
    }
    
    public static String unquote(String value) {
        return trim(value); // TODO
    }
    
    public static String quote(String value) {
        return value; // TODO
    }

}
