package com.MysqlLoadTest.demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Prepare {

	private static Logger log = LogManager.getLogger(Prepare.class); 

	
	private Connection connect;
	private int threadID;
	
	public Prepare(){
		this.connect = ConnectionManager.getConnection();
		this.threadID = threadID;
		this.DropCreateTable();
	}
	
	
	boolean DropCreateTable(){
		try {
			Statement statement = this.connect.createStatement();
			statement.execute("drop table if exists tbl1");
			statement.execute("create table if not exists tbl1(a int auto_increment primary key, b int)");
			log.info("table recreated");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	
}
