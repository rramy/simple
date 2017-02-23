/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package simple.net;

import java.io.IOError;

/**
 *
 */
public class NNTPError extends IOError {
    private final NNTPReply reply;

    NNTPError(String message, NNTPReply reply) {
        super(new Exception(message));
        
        this.reply = reply;
    }

}
