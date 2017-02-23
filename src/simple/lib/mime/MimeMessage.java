/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package simple.lib.mime;

import java.util.ArrayList;
import java.util.List;
import static simple.lib.Chaine.isEmpty;
import static simple.lib.Chaine.search;
import static simple.lib.Chaine.searchEmptyLine;
import static simple.lib.Chaine.sub;
import static simple.lib.Chaine.trim;
import simple.lib.Tokens;

/**
 *
 */
public class MimeMessage extends MimeHeaders {
    
    private final List<MimeMessage> parts = new ArrayList<MimeMessage>();
    
    private String body;

    public void parse(Tokens lines) {
        parse(lines.join("\n"));
    }
    
    public void parse(String value) {        
        int index = searchEmptyLine(value);
        if (index > 0) {
            setHead(sub(value, 0, index).trim());
            setBody(sub(value, index +1).trim());  
        } else if (value.indexOf(":") > 0)
            setHead(trim(value));
        else setBody(trim(value));
    }

    public void setHead(String head) {
        head = head.replaceAll(
            "\n[\\s]+", " ");        
        setHeaders(head.split("\n"));
    }

    public void setBody(String body) {
        // TODO : Charset decode
        
        this.body = body;
    }   
    
    public void addPart(String value) {        
        if (isEmpty(value)) return;
        
        MimeMessage part = new MimeMessage();
        
        part.parse(value);
        parts.add(part);
    }
    
    public void setParts(String body, String boundary) {
        String[] parts = body.split(boundary);
        
        for (String part : parts) addPart(part);
    }
    
    public void setHeaders(String[] lines) {        
        for (String line : lines) {
            int x = search(line, ":");
            
            String name = sub(line,0,x);
            String value = sub(line,x+1);
            
            setHeader(name, value.trim());
        }            
    }
    
    public String getHead() {
        StringBuffer sb = new StringBuffer();
        
        for (String name : names())
            sb.append(name)
              .append(": ")
              .append(getHeader(name))
              .append("\n");
        
        return sb.toString();
    }
    
    public String getBody() {
        return body;
    }

}
