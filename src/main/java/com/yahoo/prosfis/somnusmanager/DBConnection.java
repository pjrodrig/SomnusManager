package com.yahoo.prosfis.somnusmanager;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
	
	Connection connection;
	
	public DBConnection (String host, String port, String sID, String user, String password) throws Exception {
		String url = "jdbc:mysql://" + host + ":" + port + "/" + sID;
		connection = DriverManager.getConnection(url, user, password);
	} 
	
	public Connection getConnection(){
		return connection;
	}
}
