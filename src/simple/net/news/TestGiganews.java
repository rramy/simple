/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package simple.net.news;

/**
 *
 */
public class TestGiganews {
    
    public static void main(String[] argv) throws Exception {
        new NewsLeecher().leech("news.giganews.com", "rramy", "*********", 50);
    }

}
