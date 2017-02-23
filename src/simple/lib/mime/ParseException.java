/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package simple.lib.mime;

/**
 *
 */
public class ParseException extends RuntimeException {
    
    public ParseException(String value) {
        this("Can't parse '" + value + "'", null);
    }

    public ParseException(String value, Throwable cause) {
        super("Can't parse '" + value + "'", cause);
    }

}
