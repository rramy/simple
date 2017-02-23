/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package simple.lib;

import java.util.ArrayList;
import java.util.List;
import static simple.lib.Date.now;
import static simple.lib.Tokens.join;

/**
 *
 */
public class Perfs {
    
    private final List<Long> sommes = new ArrayList<Long>();  
    
    private final List<Long> times = new ArrayList<Long>(); 
    
    private int cycles = -1;
    
    public void start() {     
        doSommes();
        
        times.clear();
        times.add(now());
    }

    public void mark() {
        if (times.size() == 0) start();
        else times.add(now());
    }
    
    // string: x+y+z=... ms
    public String moyennes() {        
        return toString(getSommes());
    }
    
    // string: x+y+z=... ms
    public String instant() {        
        return toString(getTimes());
    }  
    
    private void doSommes() {
        List<Integer> values = getTimes();
        
        for (int i=0;i<values.size();i++) 
            if (sommes.size() <= i)
                sommes.add(values.get(i)+0L);
            else sommes.set(i, 
                 sommes.get(i)+
                 values.get(i));
        
        cycles++;
    }
    
    public List<Integer> getSommes() {
        if (cycles == 0) return getTimes(); // 1er cycle
        
        List<Integer> values = new ArrayList<Integer>();
                
        for (int i=0;i<sommes.size();i++) 
            values.add((int) (sommes.get(i)/cycles));        
        
        return values;
    }
    
    public List<Integer> getTimes() {
        List<Integer> values = new ArrayList<Integer>();
        
        for (int i=0;i<times.size()-1;i++) 
            values.add((int) (times.get(i+1)
                            - times.get(i)));        
        
        return values;
    }

    public static String toString(List<Integer> times) {
        StringBuffer sb = new StringBuffer();
        
        sb.append(join(times, "+"));
        sb.append("=");
        sb.append(somme(times));
        sb.append("ms");        
        
        return sb.toString();
    }
    
    public static int somme(List<Integer> times) {
        int somme = 0;
        
        for (int t : times)
            somme += t;
        
        return somme;
    } 

    public static int duree(List<Long> times) {
        int last = times.size()-1;
        
        long debut = times.get(0);
        long fin = times.get(last);
        
        return (int) (fin - debut);
    }

}
