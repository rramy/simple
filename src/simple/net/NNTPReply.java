/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package simple.net;

import simple.lib.Chaine;
import simple.lib.Tokens;

/**
 *
 */
public class NNTPReply { 
    private final int code;
    private Tokens reply;
    private Tokens lines;

    public NNTPReply(int code) {
        this.code = code;
    }
    
    public NNTPReply(String value) {
        Tokens tokens = Chaine.lines(value);
        
        reply   = Chaine.split(tokens.get(0), " ");
        lines   = tokens.delete(0);        
        code    = Chaine.toInt(reply.get(0));
    }

    public void append(String value) { 
        if (Chaine.isEmpty(value)) return;
        
        lines = Chaine.lines(lines.join("\n") + value);
    }

    public int getCode() {
        return code;
    }
    
    public String getText() {
        return reply.delete(0).join(" ");
    }
    
    public String getReply() {
        return reply.join(" ");
    }
    
    public String getArgument(int index) {
        return reply.get(index);
    }

    public String getContent() {
        if (lines == null) return "";
        
        return getLines().join("\n");
    }

    public Tokens getLines() {
        return lines.delete(-1);
    }
    
    public String toString() {
        return code + " " + getReply() + "\n" + getContent();
    }

    public boolean isComplete() {   
        switch (getCode()) {
            case 100: // HELP
            case 215: // LIST
            case 218: // XINDEX
            case 221: // HEAD
            case 222: // BODY
            case 224: // XOVER
            case 230: // NEWNEWS 
            case 282: // XGTITLE
                return lines.size() > 0 &&
                       lines.get(-1).equals(".");
            default :
                return lines.size() == 0 || 
                       lines.get(-1).equals(".");
        }
    }

}
