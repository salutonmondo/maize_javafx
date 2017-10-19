/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wf;

import java.sql.Connection;
import java.sql.DriverManager;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.hsqldb.persist.HsqlProperties;
import wf.database.DesUtils;

/**
 *
 * @author wadeshu
 */
public class Test {

    public static void main(String[] args) {
//         DBManager dr = new DBManager();
//         dr.startDBServer();
//         dr.getDBConn();
        try{
            DesUtils des = new DesUtils("hbue");
            System.out.println(des.encrypt("1"));
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}

class DBManager {

    final String dbLocation = "/Users/wadeshu/Documents/database/wf;"; // change it to your db location
    org.hsqldb.server.Server sonicServer;
    Connection dbConn = null;
    
    public void startDBServer() {
        HsqlProperties props = new HsqlProperties();
        props.setProperty("server.database.0", "file:" + dbLocation);
        props.setProperty("server.dbname.0", "xdb");
        sonicServer = new org.hsqldb.Server();
        try {
            sonicServer.setProperties(props);
        } catch (Exception e) {
            return;
        }
        sonicServer.start();
    }

    public void stopDBServer() {
        sonicServer.shutdown();
    }

    public Connection getDBConn() {
        try {
            Class.forName("org.hsqldb.jdbcDriver");
            dbConn = DriverManager.getConnection(
                    "jdbc:hsqldb:hsql://localhost/xdb", "SA", "hbue");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dbConn;
    }
}
