/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package simple.io;

import simple.lib.Host;

/**
 *
 */
public interface SimpleOptions {
        
    public Host getRemoteHost();
    
    public Host getLocalHost();
    
    public boolean isOpen();

}
