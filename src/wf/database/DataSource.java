/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wf.database;

import org.apache.commons.dbcp2.BasicDataSource;

/**
 *
 * @author wadeshu
 */
public class DataSource {

    public void getConnection() {
        BasicDataSource data = new BasicDataSource();
        data.setUsername("sa");
	data.setUrl("jdbc:hsqldb:file:/Users/wadeshu/Documents/web/ps/src/main/resources/database/wade");
        data.setUrl("jdbc:hsqldb:file:D:/cm/wade");
//	data.setUrl(env.getProperty("db.url1"));
        data.setPassword("hbue");
        data.setDriverClassName("org.hsqldb.jdbc.JDBCDriver");
    }
}
