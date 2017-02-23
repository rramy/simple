/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package simple.net;

import java.io.IOException;

/**
 *
 */
public class NNTPException extends IOException {
    private final NNTPReply reply;

    public NNTPException(NNTPReply reply) {
        super(reply.getReply());
        
        this.reply = reply;
    }
    
    public NNTPReply getReply() {
        return reply;
    }

}
