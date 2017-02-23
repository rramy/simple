/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package simple.net.news;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import simple.lib.Date;
import simple.lib.From;
import simple.net.NNTPClient;
import simple.net.NNTPClient.Article;
import simple.net.NNTPClient.Group;
import simple.sql.MySQL;
import simple.sql.SimpleDatabase;
import simple.sql.SimpleDatabase.Statement;
import simple.sql.SimpleDatabase.Statement.Result;

/**
 *
 */
public class SQLRepository implements NewsRepository {
    private final SimpleDatabase database;
    
    private final List<String> groups;    
    
    public SQLRepository() throws SQLException {
        database = MySQL.connect(
            "localhost", "ramy", "jfk2586", "simple");
        groups = getGroupsByPriority();
    }
    
    public List<String> getGroupsByPriority() throws SQLException {
        Statement statement = database.prepare(
            "select group_name from news_groups "
                    + "join news_references on news_references.group_id=news_groups.group_id "
                + "group by news_groups.group_id "
                + "order by max(news_references.post_id)");
        Result result = statement.query();
        
        List<String> groups = new ArrayList<String>();
        
        while (result.next())
            groups.add(result.getString(1));        
        result.close();
        
        return groups;
    }
    
    private CallableStatement add_article_stmt;
    
    private int add_article_count = 0;

    public void _save(Article article) throws SQLException {        
         if (add_article_stmt == null)
             add_article_stmt = database.call(
            "call addArticle(?,?,?,?,?,?,?,?,?,?,?)");            
        
        Timestamp article_date = _parse(article.getDate());        
    synchronized(add_article_stmt) { 
        add_article_stmt.clearParameters();
        add_article_stmt.setInt(1, article.getID());
        add_article_stmt.setTimestamp(2, article_date);
        add_article_stmt.setString(3, article.getTitle());
        add_article_stmt.setString(4, article.getMID());

        if (article.getParent() == null)
            add_article_stmt.setNull(5, Types.VARCHAR);
        else add_article_stmt.setString(5, article.getParent());

        add_article_stmt.setString(6, article.getServer());
        add_article_stmt.setString(7, article.getGroup().getName());

        From from = article.getFrom();
        
        if (from == null) return;
        
        add_article_stmt.setString(8, from.getName());
        add_article_stmt.setString(9, from.getMail());       
        
        add_article_stmt.setString(10, article.getHead());
        add_article_stmt.setString(11, article.getBody());
        add_article_stmt.addBatch();
        
        if (add_article_count++ > 10) {
            add_article_stmt.executeBatch();
            add_article_stmt.clearBatch();
            
            add_article_count = 0;
        }
    }
    }

    public void save(Article article) throws SQLException {
        SimpleDatabase.Statement statement = database.prepare(
            "insert into tmp_archives ("
                + "archive_ref,"
                + "archive_date,"
                + "archive_title,"
                + "archive_mid,"
                + "archive_pid,"
                + "archive_server,"
                + "archive_group,"
                + "archive_username,"
                + "archive_usermail,"
                + "archive_head,"
                + "archive_content"
            + ") values ("
            +   "?,?,?,?,?,?,?,?,?,?,?"
            + ")");
        
        Timestamp timestamp = _parse(article.getDate());  
        
        statement.clear();
        statement.set(1, article.getID());
        statement.set(2, timestamp);
        statement.set(3, article.getTitle());
        statement.set(4, article.getMID());        
        statement.set(5, article.getParent());
        statement.set(6, article.getServer());
        statement.set(7, article.getGroup().getName());
        
        From from = article.getFrom();
        if (from == null) return;
                
        statement.set(8, article.getFrom().getName());
        statement.set(9, article.getFrom().getMail());
        
        statement.set(10, article.getHead());
        statement.set(11, article.getBody());
        
        statement.update();
    }

    public String pick(List<String> availables) {
        synchronized(groups) {
            // Unknow first <> available on server / unknow on repository
            for (String available : availables)
                if (!groups.contains(available)) {
                    groups.add(available);
                    return available;
                }

            // Groups are sort by priority so first available on server
            for (String group : groups)
                if (availables.contains(group)) {
                    groups.remove(group);
                    groups.add(group);
                    return group;
                }

            return null;
        }
    }
    
    /**
     * Parser les dates tronquées
     */
    private Timestamp _parse(Date date) {
        
        return new Timestamp(date.getTime());
        
    }  

}
