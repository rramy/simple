/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package simple.net;

import simple.lib.Chaine;
import static simple.lib.Chaine.split;
import static simple.lib.Chaine.toInt;
import simple.lib.Date;
import simple.lib.Tokens;
import simple.lib.mime.MimeMessage;

/**
 *
 */
public class NNTPArticle extends MimeMessage {    
    private NNTPGroup group;
    
    private int article_id;
    
    public NNTPArticle(
           NNTPGroup group) {
        this.group = group;
    }

    public void setID(String value) {
        article_id = Chaine.toInt(value);
    }

    public void setMID(String value) {
        setHeader("Message-ID", value);
    }

    public void setDate(String value) {
        setHeader("Date", value);
    }

    public void setTitle(String value) {
        setHeader("Subject", value);
    }
    
    // overwrite MailHeaders#setHeaders
    public void setHeader(String name, String value) {
             if (Chaine.isEmpty(name)) return;
        else if (name.startsWith("X-")) 
            setHeader(name.substring(2), value);
        else super.setHeader(name, value);
    }
    
    public int getID() {
        return article_id > 0 ? article_id :
              (article_id = getXrefId());
    }
    
    public int getXrefId() {
        try {
            return toInt(getXref().get(1).split(":")[1]);
        } catch (Throwable cause) {
            return -1;
        }
    }
    
    public String getXrefHost() {
        return getXref().get(1).split(":")[0];
    }
    
    public String getMID() {
        return getHeader("Message-ID");
    }
    
    public Tokens getXref() {
        return split(getHeader("Xref"), " ");
    }

    public String getTitle() {
        return getHeader("Subject");
    }

    public Tokens getNewsgroups() {
        return split(getHeader("Newsgroups"), " ");
    }
    
    public Date getDate() {
        for (String name : names())
            if (name.indexOf("Date") >= 0) try {
                return Date.parse(getHeader(name));
            } catch (Throwable cause) {
                System.err.println(cause);
            }
        
        return null;
    }
    
    public String getParent() { 
        Tokens parents = getParents();
        
        return parents.size() > 0 ?
               parents.get(-1) : null;
    }
    
    public Tokens getParents() { 
        Tokens parents = new Tokens();
        
        if (isSet("References"))
            parents.addAll(Chaine.split(getHeader("References"), " "));
        if (isSet("In-Reply-To"))
            parents.add(getHeader("In-Reply-To"));
        
        return parents;
    }

    public NNTPGroup getGroup() {
        return group;
    }

    public String getServer() {
        return getXrefHost();
    }
    
    public String toString() {
        return getHeader("Xref");
    }

}
