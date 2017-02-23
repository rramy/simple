/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package simple.lib;

import java.lang.reflect.InvocationTargetException;

/**
 *
 */
public class Safe {

    public static Object newInstance(String name) {
        try {
            return Class.forName(name).newInstance();
        } catch (Throwable cause) {
            throw new RuntimeException(cause);
        }
    }

    public static Throwable sleep(long timeout) {
        if (timeout > 0) try {
            Thread.sleep(timeout);
        } catch (Throwable cause) {
            return cause;
        }
            return null;
    }

    public static Throwable close(Object o) {
        if (o != null) try {
            o.getClass().getMethod("close").invoke(o);
        } catch (InvocationTargetException cause) {
            return cause.getTargetException();
        } catch (Throwable cause) {
        }
            return null;
    }

    public static boolean equals(Object ... values) {
        for (int x=0;x<values.length-1;x++)
        for (int y=x+1;y<values.length;y++)
            if((values[x] == null 
             && values[y] != null)
             || values[x] != null 
             &&!values[x].equals(values[y]))
                return false;
        
        return true;                        
    }

}
