/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package simple;

import java.lang.reflect.Method;
import simple.Journal.Node;
import static simple.lib.Date.now;
import sun.reflect.Reflection;


public class Journal {
    java.util.logging.Logger logger;
    
    private final String name;
    
    public Journal() {
        this(Reflection.getCallerClass());
    }

    public Journal(Class clazz) {
        this(clazz.getName());
    }
    
    public Journal(String name) {
        this.name = name;
    }
    
    public class Record {
        
    }
    
    public static enum Level {
        
    }
    
    /**     
     * Nodes are monitoring tools
     * 
     * NODE_NAME                VIE    EX   ERR   CALLS
     * NewsLeecher()            10:52   0    0      1
     *  |- leech()              00:53   0    0      1
     *      |- open()           00:03   0    0      1
     *      |- auth()           00:05   0    0      1
     *      |- have()           00:28   0    0      1
     *      |- close()          00:01   0    0      1
     *      |- leech()          00:03   0    0      1
     *  |- save()               1ms     0    0      156/s
     *      |- save()           1ms     128  3      156/s
     *  |- Reader()             1ms     0    0      50
     *      |- open()           1ms     0    0      156/s
     *      |- auth()           1ms     0    0      156/s
     *      |- next()           1ms     0    0      156/s
     *      |- save()           1ms     0    0      156/s
     *  |- close()              00:03   0    0      1
     */
    public class Node {        
        private final long debut = now();
        
        private final String name;
        
        private int calls = 1;

        private Node(String name) {
            this.name = name;
        }
        
    }
    
    public class Log {

        public void log(Exception ex) {
            
        }
        
        public void log(Error err) {
            
        }
        
    }
}
