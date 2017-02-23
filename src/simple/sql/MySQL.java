/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package simple.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import static simple.sql.SimpleDatabase.Safe.load;

/**
 *
 */
public class MySQL {
   
    static {
        load("com.mysql.jdbc.Driver");
    }
    
    public static Connection getConnection(
        String hostname, 
        String username, 
        String password, 
        String database
    ) throws SQLException {
        return DriverManager.getConnection(
            "jdbc:mysql://" + hostname + "/" + database, username, password);
    }
    
    public static SimpleDatabase connect(
        String hostname, 
        String username, 
        String password, 
        String database
    ) throws SQLException {
        return new SimpleDatabase(getConnection(hostname, username, password, database));
    }

}
