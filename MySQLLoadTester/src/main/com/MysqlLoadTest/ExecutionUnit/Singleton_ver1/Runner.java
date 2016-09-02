package com.MysqlLoadTest.ExecutionUnit.Singleton_ver1;

import java.util.Random;
import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.io.*;
import java.lang.Thread;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.MysqlLoadTest.Utilities.ConfigLoader;
import com.MysqlLoadTest.Utilities.ConnectionInfo;
import com.MysqlLoadTest.Utilities.ConnectionManager;
import com.MysqlLoadTest.Utilities.LoadFromConfig;
import com.MysqlLoadTest.Utilities.TestInfo;
import com.MysqlLoadTest.Utilities.Tuple;
import com.MysqlLoadTest.Utilities.XORShiftRandom;

public class Runner extends Thread {
//public class Runner implements Runnable  {
	
	private static Logger log = LogManager.getLogger(Runner.class); 
	
	private Connection connect;
	private final int threadID;
	private final long runCount;
	private final long rowCount;
	private final int insertPct;
	private final int updatePct;
	private final int selectPct;
	
	private int totalInsertCount;
	private int totalUpdateCount;
	private int totalSelectCount;
	private int totalRunCount;
	
	private int intervalInsertCount;
	private int intervalUpdateCount;
	private int intervalSelectCount;
	private int intervalRunCount;
	
	private  String sqlInsertTemplate;
	private  String sqlupdateTemplate;
	private  String sqlselectTemplate;
	
	private  PreparedStatement insertStatement;
	private  PreparedStatement updateStatement;
	private  PreparedStatement selectStatement;
	
	private final int arraySize;
	
	
	@LoadFromConfig
	private int reportInterval;// = 100;
	private boolean finished = false;
	private long previousReportDateNS = 0;
	
	//private Random rand = new Random();
	private XORShiftRandom rand = new XORShiftRandom();
	
	//private ObjectOutputStream outputPipe;
	private ConcurrentLinkedDeque<RunnerMessage> queue;
	
	private final TestInfo testInfo;
	
	private String[] dataSet;
	
	//@LoadFromConfig
	//private ConnectionInfo connectionInfo;
	
	private String removeTrailing(String c){
		return c.substring(0, c.length()-1);
	}

	public Runner(TestInfo testInfo,int threadID){
		
		ConfigLoader.loadFromConfig(this);
		this.testInfo = testInfo;
		
		this.connect = ConnectionManager.getConnection(this.testInfo.connectionInfo);
		//this.outputPipe = PipeManager.getOutputPipe();
		this.queue=PipeManager.getQueue();
		this.threadID = threadID;
		if(this.testInfo.testStatus == TestInfo.PREPARING){
			this.rowCount = this.testInfo.getInitDataAmount();
			this.insertPct = 100;
			this.updatePct = 0;
			this.selectPct = 0;
		}
		else{
			
			this.rowCount = this.testInfo.getRowCount();
			this.insertPct = this.testInfo.getInsertPct();
			this.updatePct = this.testInfo.getUpdatePct();
			this.selectPct = this.testInfo.getSelectPct();
		}
		this.runCount = this.testInfo.getRunCount()/this.testInfo.getTotalThreads();
		
		
		//prepare statement template
		
		String parameterTemplate = "";
		String ValueTemplate = "";
		for (String colName : this.testInfo.tableColMap.keySet()){
			parameterTemplate += colName+",";
			ValueTemplate += "?,"; 
		}
		parameterTemplate = this.removeTrailing(parameterTemplate);
		ValueTemplate = this.removeTrailing(ValueTemplate);
		
		this.sqlInsertTemplate = String.format("insert into %s (runnerId,%s) values (?,%s)",
										this.testInfo.getTableName(),parameterTemplate,ValueTemplate);
		//this.sqlInsertTemplate = String.format("insert into %s (runnderid,%s) values (?)",
		//		this.testInfo.getTableName(),parameterTemplate);
		
		
		this.sqlupdateTemplate = String.format("update %s set runnerid = ?, ", this.testInfo.getTableName());
		
		for (String colName : this.testInfo.tableColMap.keySet()){
			this.sqlupdateTemplate += String.format(" %s = ?,",colName);
		}
		this.sqlupdateTemplate = this.removeTrailing(sqlupdateTemplate);
		this.sqlupdateTemplate += " where id = ?;";
		
		this.sqlselectTemplate = String.format("select * from %s where id = ?; ",this.testInfo.getTableName());

		this.arraySize = this.testInfo.tableColMap.size();
		
		try {
			this.insertStatement = this.connect.prepareStatement(this.sqlInsertTemplate);
			this.updateStatement = this.connect.prepareStatement(this.sqlupdateTemplate);
			this.selectStatement = this.connect.prepareStatement(this.sqlselectTemplate);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.dataGenerator();
		
	}
	
	private void reportProgress(){
		
		RunnerMessage runnerMessage = new RunnerMessage();
		if (this.previousReportDateNS != 0){
			runnerMessage.date = new Date();
			runnerMessage.threadID = this.threadID;
			runnerMessage.intervalRunCount = this.intervalRunCount;
			runnerMessage.intervalInsertCount = this.intervalInsertCount;
			runnerMessage.intervalUpdateCount = this.intervalUpdateCount;
			runnerMessage.intervalSelectCount = this.intervalSelectCount;

			runnerMessage.reportInterval = System.nanoTime() - this.previousReportDateNS;
			
			this.queue.push(runnerMessage);
			log.debug("Progress reported from thread: " + this.threadID);
		}
		
		this.intervalRunCount=0;
		this.intervalInsertCount=0;
		this.intervalUpdateCount=0;
		this.intervalSelectCount=0;
		
		this.previousReportDateNS = System.nanoTime();
	}
	
	
	private void dataGenerator(){
		this.dataSet = new String[this.arraySize+1];
		this.dataSet[0] = Integer.toString(this.threadID);
		int colIndent = 1;
		int value = this.rand.nextInt(10000)+1;
		for (int i=0;i<this.arraySize; i++){
			//Tuple<String, Integer> colInfo = this.testInfo.tableColMap.get(colName);
			this.dataSet[colIndent] = Integer.toString(value);			
			colIndent +=1;
		}
		
	}
	
	private void insertData(){
		
		try {
			
			this.insertStatement.clearParameters();
			
			int indent = 1;
			for (String item: this.dataSet){
				//log.info("indent: " + indent + " item: " + item);
				this.insertStatement.setString(indent, item);
				indent += 1;
			}
			this.insertStatement.execute();
			
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.totalInsertCount++;
		this.intervalInsertCount++;
	}
	
	private int getRandomId(){
		return this.rand.nextInt(this.testInfo.getMaxId())+1;
		
	}
	
	private void selectData(){
		
		try {
			this.selectStatement.clearParameters();
			this.selectStatement.setInt(1, this.getRandomId());
			//this.selectStatement.executeQuery();
			this.selectStatement.execute();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		this.totalSelectCount++;
		this.intervalSelectCount++;
	}
	
	private void updateData(){
		
		this.dataGenerator();
		try {
			this.updateStatement.clearParameters();
			
			int indent = 1;
			for(String item : this.dataSet){
				this.updateStatement.setString(indent, item);
				indent ++;
			}
			this.updateStatement.setInt(indent,getRandomId());
			//this.updateStatement.executeUpdate();
			this.updateStatement.execute();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.totalUpdateCount++;
		this.intervalUpdateCount++;
	}
	
	public boolean continueToRun(){
		if (this.insertPct > 0){//with insert
			return this.testInfo.getMaxId() < this.rowCount;
		}else{//with no insert
			return this.totalRunCount < this.runCount;
		}
	}
	
	public void run(){
		/*todo:
		Implement select,update,insert
		Implement random dispatch*/
		//log.info("Thread " + this.threadID + " started");

		long startTime = System.nanoTime();
		while (true){
			for (int i=0; i< reportInterval; i++){
				
				int randomNum = this.rand.nextInt(100)+1;//1~100
				//insert:0-insert
				if (randomNum <=this.insertPct){
					//insert
					this.insertData();
				}else if (randomNum <=this.insertPct + this.updatePct){
					//update
					this.updateData();
				}else{
					//select
					this.selectData();
				}
				this.totalRunCount++;
				this.intervalRunCount++;
			}
			this.reportProgress();
			if (!this.continueToRun()){break;}
		}
		long endTime = System.nanoTime();
		long duration_ns = (endTime - startTime); 
		
		//log.info("Thread " + this.threadID + " finished");
		//log.info("Elapse time in milli second: " + duration_ns / 1000000);
		this.setFinished(true);
	}

	public boolean getFinished() {return finished;}

	public void setFinished(boolean finished) {this.finished = finished;}
	
}
