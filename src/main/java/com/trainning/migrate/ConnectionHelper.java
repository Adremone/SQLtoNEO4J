package com.trainning.migrate;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@NoArgsConstructor
@Data
public class ConnectionHelper {
    private String connectionUrl = "";
    private String connectionType = "";
    private String username = "";
    private String password = "";

    // extend the code to other drivers later on
    public static Connection createConnection(String url, String username, String password) {
        String[] params= url.split(":");
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

}
