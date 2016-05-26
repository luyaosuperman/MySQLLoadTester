package com.MysqlLoadTest.ExecutionUnit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.MysqlLoadTest.Utilities.ConnectionManager;
import com.MysqlLoadTest.Utilities.TestInfo;

public class Controller {

	private static Logger log = LogManager.getLogger(Controller.class); 

	
	private Connection connect;
	//private int threadID;
	
	private TestInfo testInfo;
	
	public Controller(TestInfo testInfo){
		this.testInfo = testInfo;
		this.connect = ConnectionManager.getConnection();
		//this.threadID = threadID;
		this.DropCreateTable();
		this.parseTestTable();
		
	}
	
	
	boolean DropCreateTable(){
		try {
			Statement statement = this.connect.createStatement();
			statement.execute("drop table if exists " + this.testInfo.getTableName());
			statement.execute(this.testInfo.getCreateTableSQL());
			//statement.execute("create table if not exists tbl1(a int auto_increment primary key, b int)");
			log.info("table recreated");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
			//return false;
		}
		return true;
	}
	
	private void parseTestTable(){
		mark todo
		
	}
	
	private void prepareData(){
		assert this.testInfo.testStatus == TestInfo.PREPARING;
		this.runTest();
		this.testInfo.testStatus = TestInfo.RUNNING;
	}
	
	public int runTest(){
		Runner[] instanceArray = new Runner[testInfo.getTotalThreads()];
		int finishedThreads = 0;
		
		
		for (int i =0; i<testInfo.getTotalThreads(); i++){
			instanceArray[i] = new Runner(testInfo,i);
			instanceArray[i].start();
		}
		
		Reporter reporter = new Reporter(testInfo);
		reporter.start();

		
		while (finishedThreads < testInfo.getTotalThreads())
		{
			finishedThreads = 0;
			for (int i = 0; i<testInfo.getTotalThreads(); i++){
				if (instanceArray[i].getFinished() == true){finishedThreads ++;}
			}
			log.debug("thread finished count/total: " + finishedThreads + "/" + testInfo.getTotalThreads());
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		log.info("Finish");
		reporter.stopReporter();
		
		return testInfo.getTestId();
	}
	
	
}
