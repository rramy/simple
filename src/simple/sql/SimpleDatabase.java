/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package simple.sql;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import simple.lib.Date;

/**
 *
 */
public class SimpleDatabase {
    
    private Connection connexion;
    
    public SimpleDatabase(Connection connexion) {
        this.connexion = connexion;
    }

    public SimpleDatabase(
            String host, 
            String user, 
            String pass) throws SQLException {
        connexion = DriverManager.getConnection(host, user, pass);
    }
    
    public CallableStatement call(String query) throws SQLException {
        return connexion.prepareCall(query); 
    }
    
    public Statement prepare(String query) throws SQLException {
        return new Statement(query);
    }

    public Statement.Result execute(String query) throws SQLException {
        return prepare(query).query();
    }
    
    @Deprecated
    public Insert insert(String table) {
        return new Insert(table);
    }

    public void close() throws SQLException {
        connexion.close();
    }
    
    public class Statement {
        private final PreparedStatement statement;
        
        private final String query;

        private Statement(String query) throws SQLException {
            statement = connexion.prepareStatement(
                this.query = query, java.sql.Statement.RETURN_GENERATED_KEYS);
        }

        public void clear() throws SQLException {
            statement.clearParameters();
        }

        public void set(int index, int value) throws SQLException {
            if (value > 0)
                statement.setInt(index, value);
            else statement.setNull(index, Types.INTEGER);
        }
        
        public void set(int index, Date date) throws SQLException {
            if (date == null)
                statement.setTimestamp(index, new Timestamp(date.getTime()));
            else statement.setNull(index, Types.TIMESTAMP);            
        }

        public void set(int index, String value) throws SQLException {
            if (value != null)
                statement.setString(index, value);
            else statement.setNull(index, Types.VARCHAR);
        }

        public void set(int index, Timestamp ts) throws SQLException {
            statement.setTimestamp(index, ts);
        }

        public Update update() throws SQLException {
            return new Update();
        }

        public Result query() throws SQLException {
            return new Result();
        }
        
        public class Result {
            private ResultSet rs;
            
            public Result() throws SQLException {
                rs = statement.executeQuery();
            if (rs == null && !rs.next())
                throw new SQLException("Empty ResultSet");
            }

            public int getInt(int index) throws SQLException {
                return rs.getInt(index);
            }

            public long getLong(int index) throws SQLException {
                return rs.getLong(index);
            }

            public String getString(int index) throws SQLException {
                return rs.getString(index);
            }
            
            public String getString(String label) throws SQLException {
                return rs.getString(label);
            }

            public Date getDate(int index) throws SQLException {
                return new Date(rs.getTimestamp(index).getTime());
            }
            
            public Date getDate(String label) throws SQLException {
                return new Date(rs.getTimestamp(label).getTime());
            }

            public boolean next() throws SQLException {
                return rs.next();
            }

            public void close() throws SQLException {
                if (rs != null) try {
                    rs.close();
                } finally {
                    rs = null;
                }
            }
            
            public int count() {
                int count = 0;
            try {
                rs.last();
                count = rs.getRow();
                rs.first();
            } catch(Exception ex) {
                return -1;
            }
                return count;
            }
            
        }
        
        public class Update {
            private final ResultSet keys;
            private final int rows;
            
            public Update() throws SQLException {                
                rows = statement.executeUpdate();

                if (rows == 0) 
                    throw new SQLException("Update failed, no rows affected.");        

                keys = statement.getGeneratedKeys();
            }

            public int getInsertID() throws SQLException {
                if (!keys.next()) 
                    throw new SQLException("Insert failed, no generated key obtained.");

                return keys.getInt(1);
            }
            
        }
        
    }
    
    public class Insert {
        private final List<Field> fields = new ArrayList<Field>();
        
        private final String table;
        
        public Insert(String table) {
            this.table = table;
        }
        
        public void set(int type, String name, Object value) {
            fields.add(new Field(type, name, value));
        }

        public void setText(String name, String value) {
            set(Types.VARCHAR, name, value);
        }

        public void setInteger(String name, int value) {
            set(Types.INTEGER, name, value);
        }

        public void setVarchar(String name, String value) {
            set(Types.VARCHAR, name, value);
        }

        public void setTimestamp(String name, long time) {
            Timestamp value = new Timestamp(time);
            
            set(Types.TIMESTAMP, name, value);
        }

        public void setTimestamp(String name, Date date) {
            setTimestamp(name, date.getTime());
        }

        public void setNull(String name) {
            setNull(name);
        }

        public int execute() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public int index() {
            return fields.size();
        }
        
    }
    
    public class Field {
        private final int type;
        private final String name;
        private final Object value;

        private Field(int type, String name, Object value) {
            this.type   = type;
            this.name   = name;
            this.value  = value;
        }
        
    }
    
    public static int count(ResultSet rs) {
        int count = 0;
    try {
        rs.last();
        count = rs.getRow();
        rs.beforeFirst();
    } catch(Exception ex) {
        return 0;
    }
        return count;
    }
    
    public static int insert_id(PreparedStatement statement) throws SQLException {
        int rows = statement.executeUpdate();
        
        if (rows == 0) 
            throw new SQLException("Insert failed, no rows affected.");        
        
        ResultSet keys = statement.getGeneratedKeys();
        
        if (!keys.next()) 
            throw new SQLException("Insert failed, no generated key obtained.");
        
        return keys.getInt(1);
    }
    
    public static class Safe {
    
        public static void load(String path) {        
            try {
                Class.forName(path);
            } catch (Throwable cause) {
                throw new RuntimeException(cause);
            }
        }
        
        public static PreparedStatement prepare(Connection connexion, String statement) {
            try {
                return connexion.prepareStatement(statement);
            } catch (Throwable cause) {
                throw new RuntimeException(cause);
            }
        }
    
    }
}
