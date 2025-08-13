package com.trainning.migrate;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.neo4j.driver.*;


@NoArgsConstructor
@Data
public class ConnectionHelper {
    private String connectionUrl = "";
    private String connectionType = "";
    private String username = "";
    private String password = "";

    // extend the code to other drivers later on
    public static Connection createSQLConnection(String url, String username, String password) {
        Connection conn = null;

        try{
            Class.forName("oracle.jdbc.OracleDriver");
            conn = DriverManager.getConnection(url,username,password);
            System.out.println("Connected to DB Successfully");
        }catch (ClassNotFoundException e){
            System.out.println("Unable to connect to the database\n Driver not found.");
            e.printStackTrace();
            System.exit(-1);
        } catch (Exception e) {
            try {
                if(conn!=null&&!conn.isClosed()) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
                System.exit(-2);
            }
            e.printStackTrace();
            System.exit(-2);
        }
        return conn;
    }

    public static Driver createNEO4jConnection() {
        String uri = "jdbc:neo4j:bolt://localhost:7687";
        String password = "password";
        String user = "neo4j";
        return GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }


}
