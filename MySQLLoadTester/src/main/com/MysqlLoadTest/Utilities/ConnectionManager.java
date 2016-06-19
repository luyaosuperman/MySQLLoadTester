package com.MysqlLoadTest.Utilities; 

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {
	private static String username = "root";
	private static String password = "Chaemohz1quiegh";
	


	
	public static Connection getConnection(ConnectionInfo connectionInfo){
		String connString = String.format("jdbc:mysql://%s:%s/%s?user=%s&password=%s"
				+ "&autoReconnect=true&useSSL=false",
				connectionInfo.hostname,connectionInfo.port,connectionInfo.databaseName,connectionInfo.username,connectionInfo.password);
		Connection connect;
		try {
			connect = DriverManager.getConnection(connString);
			return connect;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}
} 
 