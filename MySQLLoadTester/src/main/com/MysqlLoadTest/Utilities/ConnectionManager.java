package com.MysqlLoadTest.Utilities; 

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {
	private static String username = "root";
	private static String password = "Chaemohz1quiegh";
	
	public static Connection getConnection(){
		return getConnection("test");
	}
	
	public static Connection getConnection(String databaseName){
		String connString = String.format("jdbc:mysql://localhost/%s?user=%s&password=%s",databaseName,username,password);
		Connection connect;
		try {
			connect = DriverManager.getConnection(connString);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return connect;
	}
}
