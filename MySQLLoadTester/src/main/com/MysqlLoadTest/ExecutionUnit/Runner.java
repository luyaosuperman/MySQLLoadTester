package com.MysqlLoadTest.ExecutionUnit;

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

public class Runner extends Thread {
//public class Runner implements Runnable  {
	
	private static Logger log = LogManager.getLogger(Runner.class); 

	
	private Connection connect;
	private int threadID;
	
	private int runCount;
	private int runCountCurrent = 0;
	private int reportInterval = 100;
	private boolean finished = false;
	private long previousReportDateNS = 0;
	
	//private ObjectOutputStream outputPipe;
	private ConcurrentLinkedDeque<RunnerMessage> queue;
	
	private TestInfo testInfo;
	

	public Runner(TestInfo testInfo,int threadID){
		this.connect = ConnectionManager.getConnection();
		//this.outputPipe = PipeManager.getOutputPipe();
		this.queue=PipeManager.getQueue();
		this.threadID = threadID;
		this.testInfo = testInfo;
		this.runCount = this.testInfo.getRunCount();

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
	
	private void InsertData(){
		
		try {
			
			PreparedStatement preparedStatement = this.connect.prepareStatement("insert into tbl1 (b) values (?)");

			long startTime = System.nanoTime();
			while (this.runCount > this.runCountCurrent){
				for (int i=0; i< reportInterval; i++){
					preparedStatement.setInt(1, this.threadID);
					preparedStatement.execute();
					this.runCountCurrent++;
				}
				this.reportProgress();
			}
			
			long endTime = System.nanoTime();
			long duration_ns = (endTime - startTime); 
			
			log.info("Elapse time in milli second: " + duration_ns / 1000000);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.setFinished(true);
	}
	
	private boolean SelectData(){
		try {
			Statement statement = this.connect.createStatement();
			ResultSet resultSet;
			resultSet = statement.executeQuery("select a,b from tbl1");
			
			while(resultSet.next())
			{
				int a = resultSet.getInt("a");
				int b = resultSet.getInt("b");
				//System.out.println("Fetch a,b: " + a + " " + b);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
		
	}
	
	public void run(){
		log.info("Thread " + this.threadID + " started");
		this.InsertData();
		log.info("Thread " + this.threadID + " finished");
	}

	public boolean getFinished() {return finished;}

	public void setFinished(boolean finished) {this.finished = finished;}
	
}
