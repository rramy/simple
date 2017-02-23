/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simple.net.news;

import java.util.List;
import simple.net.NNTPClient.Article;

/**
 *
 * @author ramy
 */
public interface NewsRepository {

    public void save(Article article) throws Exception;

    public String pick(List<String> groups);
    
}
