package com.MysqlLoadTest.ExecutionUnit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.MysqlLoadTest.Utilities.ConfigLoader;
import com.MysqlLoadTest.Utilities.ConnectionInfo;
import com.MysqlLoadTest.Utilities.ConnectionManager;
import com.MysqlLoadTest.Utilities.LoadFromConfig;
import com.MysqlLoadTest.Utilities.TestInfo;
import com.MysqlLoadTest.Utilities.Tuple;

public class TestController extends Thread{

	private static Logger log = LogManager.getLogger(TestController.class); 

	
	private Connection connect;
	//private int threadID;
	
	private TestInfo testInfo;
	//private TestInfo testInfoPrepare;
	
	private int controllerStatus = this.NOTRUNNING;
	
	public static final int NOTRUNNING=0;
	public static final int RUNNING=1;
	
	//@LoadFromConfig
	//private ConnectionInfo connectionInfo;
	
	public TestController(){
		ConfigLoader.loadFromConfig(this);
		this.start();
	}
	
	public void startTest(TestInfo testInfo){
		//start test if none is running
		//otherwise throw an error
		if (this.controllerStatus!= TestController.NOTRUNNING){
			throw new IllegalStateException();
		}else{
			this.controllerStatus = TestController.RUNNING;
			this.testInfo = testInfo;
		}
	}
	
	public int testStatus(){
		return this.controllerStatus;
	}
	
	public void run(){
		
		while (true){
			
			if (this.controllerStatus == TestController.RUNNING){
				this.connect = ConnectionManager.getConnection(this.testInfo.connectionInfo);
				this.DropCreateTable();
				this.parseTestTable();
				
				log.info("prepareData()");
				this.prepareData();
				log.info("runTest()");
				this.runTest();
				this.testInfo.testStatus = TestInfo.FINISHED;
				
				this.controllerStatus = TestController.NOTRUNNING;
			}else{
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
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
		this.testInfo.testStatus = TestInfo.PREPARING;
		this.runTest();
		this.testInfo.testStatus = TestInfo.RUNNING;
		
	}
	
	private int runTest(){
		
		
		Runner[] instanceArray = new Runner[testInfo.getTotalThreads()];
		int finishedThreads = 0;
		
		for (int i =0; i<testInfo.getTotalThreads(); i++){
			instanceArray[i] = new Runner(testInfo,i);
			instanceArray[i].start();
		}
		
		MaxIdCatcher maxIdCatcher = new MaxIdCatcher(this.testInfo);
		maxIdCatcher.start();
		
		Reporter reporter = new Reporter(this.testInfo);
		reporter.start();

		while (finishedThreads < testInfo.getTotalThreads())
		{
			finishedThreads = 0;
			for (int i = 0; i<testInfo.getTotalThreads(); i++){
				if (instanceArray[i].getFinished() == true){finishedThreads ++;}
			}
			log.debug("thread finished count/total: " + finishedThreads + "/" + testInfo.getTotalThreads());
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		log.info("Finish");
		reporter.stopReporter();
		maxIdCatcher.stopMaxIdCatcher();;
		
		
		return testInfo.getTestId();
	}
	
	
}
