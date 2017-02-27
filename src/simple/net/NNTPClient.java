/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package simple.net;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import static simple.lib.Chaine.isEmpty;

/**
 *
 */
public class NNTPClient extends TCPClient implements NNTP {
    private Article article;

    private Group group;

    public void auth(String user, String pass) throws IOException {
        execute("AUTHINFO USER " + user);
        execute("AUTHINFO PASS " + pass);
    }

    public void open(String host) throws IOException {
        open(host, DEFAULT_PORT);
    }

    public void open(String host, int port) throws IOException {
        super.open(host, port); getReply();
    }

    public NNTPReply execute(String command) throws IOException {
        // System.out.println(this + " > " + command);

        write(command); return getReply();
    }

    public NNTPReply essayer(String ... commands) throws IOException {
        IOException cause = null;

        for (String command : commands) try {
            return execute(command);
        } catch (IOException ex) {
            cause = ex;
        }

        throw cause;
    }

    public NNTPReply getReply() throws IOException {
        NNTPReply reply = new NNTPReply(read());

        for (int i=0;!reply.isComplete();i++) {
            String value = read();

            if (isEmpty(value)) {
                if (i > 3) throw new NNTPError("Truncate data.", reply);
            } else reply.append(value);
        }

        // System.out.println(this + " < " + reply.getReply());

        if (reply.getCode() >= 400)
            throw new NNTPException(reply);
        return reply;
    }

    public void close() throws IOException {
        try {
            execute("quit");
        } finally {
            super.close();
        }
    }

    public void article(Article article) throws IOException {
        NNTPReply reply = essayer(
            "article",
            "article " + article.getID(),
            "article " + article.getMID());

        article.parse(reply.getContent());
    }

    public void head(Article article) throws IOException {
        NNTPReply reply = essayer(
            "head",
            "head " + article.getID(),
            "head " + article.getMID());

        article.setHead(reply.getContent());
    }

    public void body(Article article) throws IOException {
        NNTPReply reply = essayer(
            "body",                
            "body " + article.getID(),
            "body " + article.getMID());

        article.setHead(reply.getContent());
    }

    public Group group(String name) throws IOException {
        NNTPReply reply = execute("group " + name);

        Group group = new Group();
        group.setCount(reply.getArgument(1));
        group.setLow(reply.getArgument(2));
        group.setHigh(reply.getArgument(3));
        group.setName(reply.getArgument(4));

        return this.group = group;
    }

    public List<Group> list() throws IOException {
        int timeout = setSocketTimeout(5000);

        NNTPReply reply = execute("list");

        List<Group> groups = new ArrayList<Group>();

        for (String line : reply.getLines()) try {
            StringTokenizer st = new StringTokenizer(line);

            Group group = new Group();
            group.setName(st.nextToken());
            group.setHigh(st.nextToken());
            group.setLow(st.nextToken());
            group.setFlag(st.nextToken());
            groups.add(group);
        } catch (Throwable cause) {
            cause.printStackTrace();
        }

        setSocketTimeout(timeout); // reset timeout

        return groups;
    }

    public Article stat(long id) throws IOException {
        NNTPReply reply = execute("stat " + id);

        Article article = new Article();
        article.setID(reply.getArgument(1));
        article.setMID(reply.getArgument(2));

        return article;
    }

    public Article next() throws IOException {
        NNTPReply reply = execute("next");

        Article article = new Article();
        article.setID(reply.getArgument(1));
        article.setMID(reply.getArgument(2));

        return this.article = article;
    }

    public class Group extends NNTPGroup {

        public NNTPClient getClient() {
            return NNTPClient.this;
        }

    }

    public class Article extends NNTPArticle {

        public Article() {
            super(group);
        }

        public String getServer() {
            return getRemoteHost().getHost();
        }

        public NNTPClient getClient() {
            return NNTPClient.this;
        }

        public boolean isLast() {
            return getID() == getGroup().getHigh();
        }

    }

}
