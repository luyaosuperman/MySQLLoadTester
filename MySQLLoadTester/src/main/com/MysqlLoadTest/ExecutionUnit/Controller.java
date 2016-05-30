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
import com.MysqlLoadTest.Utilities.Tuple;

public class Controller {

	private static Logger log = LogManager.getLogger(Controller.class); 

	
	private Connection connect;
	//private int threadID;
	
	private TestInfo testInfo;
	//private TestInfo testInfoPrepare;
	
	public Controller(TestInfo testInfo){
		this.testInfo = testInfo;
		this.connect = ConnectionManager.getConnection();
		//this.threadID = threadID;
		this.DropCreateTable();
		this.parseTestTable();
		
	}
	
	
	private void DropCreateTable(){
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
	}
	
	private void parseTestTable(){
		try {
			PreparedStatement preparedStatement = this.connect.prepareStatement("select " 
+"ordinal_position,column_name,data_type,character_maximum_length "
+"from information_schema.`COLUMNS` col "
+"where col.TABLE_SCHEMA = 'test' " 
+"and col.TABLE_NAME = ? "
+"and col.ORDINAL_POSITION > 2; ");
			preparedStatement.setString(1, this.testInfo.getTableName());
			
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()){
				String column_name = rs.getString("column_name");
				String data_type = rs.getString("data_type");
				int character_maximum_length = rs.getInt("character_maximum_length");
				
				this.testInfo.tableColMap.put(column_name, 
						new Tuple<String,Integer>(data_type,character_maximum_length));
			}
			
			log.info("table parsed");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
			//return false;
		}
		
	}
	
	public void prepareData(){
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
				Thread.sleep(5000);
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
