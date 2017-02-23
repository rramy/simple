/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package simple.net;

/**
 *
 */
public class NNTPGroup {    
    private String name;
    private long low,high;
    private String flags;
    private long count;

    public void setName(String name) {
        this.name = name;
    }

    public void setCount(String value) {
        count = Long.parseLong(value);
    }

    public void setHigh(String value) {
        high = Long.parseLong(value);
    }

    public void setLow(String value) {
        low = Long.parseLong(value);
    }

    public void setFlag(String value) {
        flags = value;
    }
    
    public long getLow() {
        return low;
    }
    
    public long getHigh() {
        return high;
    }
    
    public long getCount() {
        if (count > 0) 
            return count;
        if (high > low) 
            return high-low;
        return 0;
    }
    
    public String getName() {
        return name;
    }    
    
    public String getInfos() {
        return name + " " + high + " " + low + " " + flags;
    }
    
    public String toString() {
        return name;
    }

    public boolean isEmpty() {
        return getCount() <= 0;
    }

}
