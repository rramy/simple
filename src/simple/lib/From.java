/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package simple.lib;

import simple.lib.mime.ParseException;

/**
 *
 */
public class From {
    private final String value;
    private String name, mail;

    public From(String value) {
        try {
            new Parser().parse(this.value = value);
        } catch (Throwable cause) {
            throw new ParseException(value, cause);
        }
    }

    public String getName() {
        return name;
    }

    public String getMail() {
        return mail;
    }

    public String getValue() {
        return value;
    }
    
    public String toString() {
        return mail == null ? name :
               name == null ? mail :
               mail  + " (" + name + ")";
               
    }
    
    public class Parser extends simple.lib.Parser {

        // Bodo G. Meier <bgmeier@kabelmail.de>
        // "Ralph A. Schmid, dk5ras" <ralph@schmid.xxx>
        // olaf@bigred.inka.de (Olaf Titz)
        private void parse(String value) {    
            int x1 = value.indexOf("\"");  
            int x2 = value.indexOf("\"", x1+1);
            
            int y1 = value.indexOf("<");  
            int y2 = value.indexOf(">", y1+1);
            
            int z1 = value.indexOf("(");  
            int z2 = value.indexOf(")", z1+1);
            
            if (x1 != -1) name = value.substring(x1+1, x2);
            if (y1 != -1) mail = value.substring(y1+1, y2);
            if (z1 != -1) name = value.substring(z1+1, z2);            
            
            if (name == null && mail == null) setOne();     // Assign mail if @ in value, name otherwise
       else if (mail == null && x1 >= 0) setMail(x1, x2);   // setMail with remaining
       else if (name == null && y1 >= 0) setName(y1, y2);   // setName with remaining
       else if (mail == null && z1 >= 0) setMail(z1, z2);   // setMail with remaining
            
            if (name != null) name = name.trim();
            if (mail != null) mail = mail.trim();
        }

        private void setOne() {
            if (value.indexOf("@") > 0) mail = value; else name = value;
        }

        private void setName(int x, int y) {
            name = x == 0 ?
                value.substring(y+1):
                value.substring(0,x);
        }

        private void setMail(int x, int y) {
            mail = x == 0 ? 
                value.substring(y+1):
                value.substring(0,x);
        }
        
    }

}
