package com.MysqlLoadTest.ExecutionUnit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {
	private static String username = "root";
	private static String password = "Chaemohz1quiegh";
	
	public static Connection getConnection(){
		String connString = String.format("jdbc:mysql://localhost/test?user=%s&password=%s",username,password);
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
