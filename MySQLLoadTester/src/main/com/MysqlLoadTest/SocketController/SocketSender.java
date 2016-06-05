package com.MysqlLoadTest.SocketController;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.MysqlLoadTest.Utilities.TestInfo;

public class SocketSender {
	
	private static Logger log = LogManager.getLogger(SocketSender.class); 
	
	public static final String IP_ADDR = "localhost";//服务器地址 
	public static final int PORT = 12345;//服务器端口号  
	
    public static void main(String[] args) {  
    	
		//int testType = 1;
		int totalThreads = 20;
		int runCount = 3000000;
		int rowCount = 30000;
		
		String tableName = "testLt";
		String createTableSql = "create table testLt (" +
								"id bigint unsigned auto_increment primary key, " +
								"runnerId int unsigned not null, " +
								"col1 int unsigned not null, " +
								"col2 bigint unsigned not null, " +
								"col3 char(255) not null, " +
								"col4 char(255) not null, " + 
								"col5 char(255) not null, " + 
								"col6 char(255) not null, " + 
								"col7 char(255) not null, " + 
								"col8 char(255) not null, " + 
								"col9 char(255) not null, " + 
								"col10 char(255) not null, " +
								"col11 char(255) not null, " + 
								"col12 char(255) not null, " + 
								"col13 char(255) not null, " + 
								"col14 char(255) not null, " + 
								"col99 char(255) not null)";
		/*int insertPct = 100;
		int selectPct = 0;
		int updatePct = 0;*/
		
		int insertPct = 10;
		int selectPct = 30;
		int updatePct = 60;
		
		
		int initDataAmount = 10000;
		
		
		TestInfo testInfo = new TestInfo(totalThreads,runCount,rowCount,"test",
				tableName,createTableSql,insertPct,selectPct,updatePct,initDataAmount);
		
    	
    	
    	
        log.info("Start client");  
        //System.out.println("当接收到服务器端字符为 \"OK\" 的时候, 客户端将终止\n"); 
        //while (true) {  
        	Socket socket = null;
        	try {
        		//创建一个流套接字并将其连接到指定主机上的指定端口号
	        	socket = new Socket(IP_ADDR, PORT);  
	            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
	            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
	            
	            log.info("sending TestInfo with table name: " + testInfo.getTableName());
	            out.writeObject(testInfo);
	            log.info("sent");
	            //out.writeUTF(str);  
	              
	            log.info("receiving");
	            int returnValue = (int) input.readObject();
	            log.info("Received test ID: " + returnValue);
	            
	            out.close();
	            input.close();
        	} catch (Exception e) {
        		log.fatal("Client exception:" + e.getMessage()); 
        	} finally {
        		if (socket != null) {
        			try {
						socket.close();
					} catch (IOException e) {
						socket = null; 
						log.fatal("Client exception in \"finally\" block:" + e.getMessage()); 
					}
        		}
        	}
        //}  
    }  
}  