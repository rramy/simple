package simple.net.news;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import static simple.lib.Date.now;
import simple.net.NNTPClient;
import simple.net.NNTPClient.Article;
import simple.net.NNTPClient.Group;
import simple.lib.Safe;
import simple.net.NNTPException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 */
public class NewsLeecher {
    private static final int MAX_EMPTY_ARTICLES = 3;   
    
    private final ThreadPoolExecutor workers = new ThreadPoolExecutor(
        0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
    
    private final NewsRepository repository;
    
    private long total, added = 1;
    
    public NewsLeecher() throws Exception {
        repository = new SQLRepository();
    }
    
    public void leech(String host) {
        leech(host, null, null);
    }
    
    public void leech(String host, int count) {
        leech(host, null, null, count);
    }

    public void leech(String host, String user, String pass) {
        leech(host, user, pass, 1);
    }

    /**
     * Teste la connection au serveur et prepare les processus de recuperation (@see #Reader)
     * 
     * @param host Nom ou adresse de l'hote
     * @param user Nom de l'utilisateur (identifiant)
     * @param pass Mot de passe du compte utilisateur
     * @param conn Nombre de connections simultanées autorisées par le serveur
     */
    public void leech(
            final String host, 
            final String user, 
            final String pass, 
            final int conn) {
        workers.execute(new Runnable() {
            private NNTPClient client;
            private List<String> groups;
            
            public void run() {
                client = new NNTPClient();
            try {    
                client.open(host);
            if (user != null)
                client.auth(user, pass);
            
                groups = have(client.list());
                
                client.close();
            
                leech(host, user, pass, conn, groups);
            } catch (Throwable cause) {
                cause.printStackTrace();
            }
            }
        });
    }    
    
    private void leech(
            final String host, 
            final String user, 
            final String pass, 
            final int conn,
            final List<String> groups) {
        for (int i=0;i<conn;i++)
            workers.execute(new Reader(
                host, user, pass, groups)); 
    }
    
    private List<String> have(List<Group> groups) throws Exception { 
        List<String> list = new ArrayList<String>();
        
        for (Group group : groups) {             
            list.add(group.getName());
            
            total += group.getCount();
        }
        
        Collections.shuffle(list);
        
        return list;
    }
    
    public String pick(List<String> groups) throws Exception {    
        
        return repository.pick(groups);
    }

    private final long debut = now();
    
    private void save(Article article) {
        try {
            long x = now();
            
            repository.save(article); added++;    

            long y = now();

            long moy = (y-debut)/added;

            System.out.println(
                total-added + "x" + moy + "ms (" + (y-x) + ")" +
            // $host/$group:$id/$count             
                " - "  + article.getClient().getRemoteHost().getHost() +
                ":"    + article.getClient().getLocalHost().getPort() +
                "/"    + article.getGroup() + 
                ":"    + article.getID() + 
                "/"    + article.getGroup().getHigh());
        } catch (Throwable cause) {
            cause.printStackTrace();
        } 
    }
    
    /**
     * Processus de lecture des groupes
     */
    public class Reader implements Runnable {
        
        private final String host, user, pass;

        private final List<String> groups;
        
        private NNTPClient client;
        
        private Group group;
        
        private Article article; 

        private int empty_articles = 0;       

        private Reader(String host, String user, String pass, List<String> groups) {
            this.host   = host;
            this.user   = user;
            this.pass   = pass;
            this.groups = groups;
        }

        public void run() {
            client = new NNTPClient();
        while (client != null) try {        
            client.open(host);
        if (user != null)
            client.auth(user, pass);
        
            while (next()) save(article);            
        } catch (Exception cause) {
            cause.printStackTrace();
        } catch (OutOfMemoryError err) {
            err.printStackTrace();
            return;
        } finally {
            Safe.close(client);
            Safe.sleep(3000);
            
            article = null;
            group = null;
        }}
        
        private boolean next() {     
            if (group == null) nextGroup();
            else article = nextArticle();            
            if (article == null) nextGroup();
        try {    
            client.article(article);
        } catch (Exception ex) {
            ex.printStackTrace();
            
            if (empty_articles++ >
                    MAX_EMPTY_ARTICLES) {
                empty_articles = 0;
                group = null;
            }
            
            return next();
        }
            return article != null;
        }

        private void nextGroup() {
            for (article = null;
                article == null;) try {
                    String pick = pick(groups);

                    if (pick == null) return;

                    group = client.group(pick);

                    if (group.isEmpty()) continue;

                    article = client.next();                    
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
        }

        private Article nextArticle() {
            try {
                return client.next();
            } catch (Throwable ex) {ex.printStackTrace();
                return null;
            }
        }
        
    }

}
