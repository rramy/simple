/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package simple.io;

/**
 *
 */
public abstract class SimpleSocket extends SimpleChannel implements SimpleOptions {
    
    public String toString() {
        return new StringBuffer()
                .append(getClass().getSimpleName())
                .append("[")
                .append(getLocalHost())
                .append("@")
                .append(getRemoteHost())
                .append("]")
                .toString();
    }

}
