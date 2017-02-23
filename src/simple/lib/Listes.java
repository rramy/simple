/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package simple.lib;

import java.util.ArrayList;

/**
 *
 */
public class Listes<T> extends ArrayList<T> {
    
    public Listes() {
        super();
    }
    
    public Listes(T[] values) {
        addAll(values);
    }
    
    public void addAll(T[] values) {
        for (T value : values) add(value);
    }
    
    public T get(int index) {
        if (index < 0)
            index += size();
        
        return super.get(index);
    }
    
    public T getLast() {        
        return get(-1);
    }
    
    public T remove(int index) {
        if (index < 0)
            index += size();   
        if (index < 0) return null;
        
        return super.remove(index);
    }
    
    public Listes<T> sub(int fr, int to) {
        if (fr < 0) fr += size();
        if (to < 0) to += size();
        
        // ERROR : subList cannot be cast as Listes<T>
        Listes<T> sub = clone();
        
        sub.clear();
        
        for (int x=fr;x<=to;x++)
            sub.add(get(x));
        
        return sub;
    }
    
    public Listes<T> delete(int index) {
        Listes<T> clone = clone();
        clone.remove(index);
        return clone;
    }
    
    public Listes<T> clone() {
        return (Listes<T>) super.clone();
    }

}
