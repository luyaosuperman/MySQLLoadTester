package com.MysqlLoadTest.ExecutionUnit;

import java.util.Random;

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

import com.MysqlLoadTest.Utilities.ConnectionManager;
import com.MysqlLoadTest.Utilities.TestInfo;
import com.MysqlLoadTest.Utilities.Tuple;

public class Runner extends Thread {
//public class Runner implements Runnable  {
	
	private static Logger log = LogManager.getLogger(Runner.class); 
	
	private Connection connect;
	private final int threadID;
	private final long runCount;
	private final int insertPct;
	private final int updatePct;
	private final int selectPct;
	
	private int insertCount;
	private int updateCount;
	private int selectCount;
	
	private String sqlInsertTemplate;
	private String sqlupdateTemplate;
	private String sqlselectTemplate;
	
	private PreparedStatement insertStatement;
	private PreparedStatement updateStatement;
	private PreparedStatement selectStatement;
	
	private final int arraySize;
	
	
	private int runCountCurrent = 0;
	private int reportInterval = 100;
	private boolean finished = false;
	private long previousReportDateNS = 0;
	
	private Random rand = new Random();
	
	//private ObjectOutputStream outputPipe;
	private ConcurrentLinkedDeque<RunnerMessage> queue;
	
	private TestInfo testInfo;
	
	private String removeTrailing(String c){
		return c.substring(0, c.length()-1);
	}

	public Runner(TestInfo testInfo,int threadID){
		this.connect = ConnectionManager.getConnection();
		//this.outputPipe = PipeManager.getOutputPipe();
		this.queue=PipeManager.getQueue();
		this.threadID = threadID;
		this.testInfo = testInfo;
		if(this.testInfo.testStatus == TestInfo.PREPARING){
			this.runCount = this.testInfo.getInitDataAmount()/this.testInfo.getTotalThreads();
			this.insertPct = 100;
			this.updatePct = 0;
			this.selectPct = 0;
		}
		else{
			this.runCount = this.testInfo.getRunCount()/this.testInfo.getTotalThreads();
			this.insertPct = this.testInfo.getInsertPct();
			this.updatePct = this.testInfo.getUpdatePct();
			this.selectPct = this.testInfo.getSelectPct();
		}
		
		//prepare statement template
		
		String parameterTemplate = "";
		String ValueTemplate = "";
		for (String colName : this.testInfo.tableColMap.keySet()){
			parameterTemplate += colName+",";
			ValueTemplate += "?,"; 
		}
		parameterTemplate = this.removeTrailing(parameterTemplate);
		ValueTemplate = this.removeTrailing(ValueTemplate);
		
		this.sqlInsertTemplate = String.format("insert into %s (runnderid,%s) values (?,%s)",
										this.testInfo.getTableName(),parameterTemplate,ValueTemplate);
		
		
		this.sqlupdateTemplate = "update %s set runnerid = ?, ";
		
		for (String colName : this.testInfo.tableColMap.keySet()){
			this.sqlupdateTemplate += String.format(" %s = ?,",colName);
		}
		this.sqlupdateTemplate = this.removeTrailing(sqlupdateTemplate);
		this.sqlupdateTemplate += String.format(" from %s where id = ?;",this.testInfo.getTableName());
		
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
		
	}
	
	private void reportProgress(){
		
		RunnerMessage runnerMessage = new RunnerMessage();
		if (this.previousReportDateNS != 0){
			runnerMessage.date = new Date();
			runnerMessage.threadID = this.threadID;
			runnerMessage.totalInsertCount = this.runCountCurrent;
			runnerMessage.intervalInsertCount = this.reportInterval;
			runnerMessage.reportInterval = System.nanoTime() - this.previousReportDateNS;
			
			this.queue.push(runnerMessage);
			log.debug("Progress reported from thread: " + this.threadID);
		}
		
		this.previousReportDateNS = System.nanoTime();
	}
	
	
	private Object[] dataGenerator(){
		Object[] result = new Object[this.arraySize];
		int  colIndent = 0;
		for (String colName: this.testInfo.tableColMap.keySet()){
			Tuple<String, Integer> colInfo = this.testInfo.tableColMap.get(colName);
			
			switch (colInfo.x){
			case "int":
				result[colIndent] = 1;
				break;
			case "bigint":
				result[colIndent] = 1;
				break;
			case "char":
				result[colIndent] = "abc";
				break;
			}
			
			
			
			colIndent +=1;
		}
		
		return result;
	}
	
	private void insertData(){
		
		try {
			
			this.insertStatement.clearParameters();
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.insertCount++;
	}
	
	private void selectData(){
		
		//how to get max(id) without lock?
		
		this.selectCount++;
	}
	
	private void updateData(){
		
		this.updateCount++;
	}
	
	public void run(){
		/*todo:
		Implement select,update,insert
		Implement random dispatch*/
		log.info("Thread " + this.threadID + " started");

		long startTime = System.nanoTime();
		while (this.runCount > this.runCountCurrent){
			for (int i=0; i< reportInterval; i++){
				
				int randomNum = this.rand.nextInt(100) +1;//1~100
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
			}
			this.reportProgress();
		}
		long endTime = System.nanoTime();
		long duration_ns = (endTime - startTime); 
		
		log.info("Thread " + this.threadID + " finished");
		log.info("Elapse time in milli second: " + duration_ns / 1000000);
		this.setFinished(true);
	}

	public boolean getFinished() {return finished;}

	public void setFinished(boolean finished) {this.finished = finished;}
	
}
