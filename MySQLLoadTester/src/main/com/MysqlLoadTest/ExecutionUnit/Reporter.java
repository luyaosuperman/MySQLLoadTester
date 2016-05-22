package com.MysqlLoadTest.ExecutionUnit;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.MysqlLoadTest.Utilities.ConnectionManager;
import com.MysqlLoadTest.Utilities.TestInfo;
import com.mysql.jdbc.Statement;

public class Reporter extends Thread{
	
	private static Logger log = LogManager.getLogger(Reporter.class); 
	private boolean stop = false;
	private long previousReportTimeNS = 0;
	private long reportIntervalNS = 1000000000;//nanoseconds
	
	private long totalExecution = 0;
	//private long intervalExecution = 0;
	private long previousTotalExecution = 0;
	
	//private ObjectInputStream inputPipe;
	private ConcurrentLinkedDeque<RunnerMessage> queue;
	
	private Connection connect;
	private TestInfo testInfo;
	
	private long referenceNanoSecond ;
	
	private void preRunSummary(){
		//Generate the log into database, about configured test information
		
		try {
			PreparedStatement preparedStatement = 
					this.connect.prepareStatement(
					"insert into testInfo "
					+ "(timestamp,testType,threads,runCount) values (now(),?,?,?)",
					Statement.RETURN_GENERATED_KEYS);
			
			//preparedStatement.setTimestamp(1, new java.sql.Timestamp(System.currentTimeMillis()));
			preparedStatement.setInt(1, testInfo.getTestType());
			preparedStatement.setInt(2, testInfo.getTotalThreads());
			preparedStatement.setInt(3, testInfo.getRunCount());
			
			preparedStatement.executeUpdate();
			
			ResultSet rs = preparedStatement.getGeneratedKeys();
			if (rs.next()) {
				this.testInfo.setTestId(rs.getInt(1));
			}
			else{
				assert false;
			}
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void postRunSummary(){
		//Generate the log into database, about the summary of test after the test finished
		
	}
	
	public Reporter(TestInfo testInfo){
		this.testInfo = testInfo;
		this.referenceNanoSecond = System.nanoTime();
		
		connect = ConnectionManager.getConnection("testReport");
		
		this.preRunSummary();
		
	}
	
	public void stopReporter(){
		this.stop = true;
		this.postRunSummary();
	}
	
	public void pause(int ms){
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void summaryReport(){
		//This is to give a detailed summary about TPS info
		
		long currentTime = System.nanoTime() - this.referenceNanoSecond;
		long currentExecutionCount = this.totalExecution;
		
		//log.info("this.referenceNanoSecond "  + this.referenceNanoSecond);
		//log.info("this.previousReportTimeNS " + this.previousReportTimeNS);
		//log.info("currentTime " + currentTime);
		
		if ( this.previousReportTimeNS != 0 && currentTime - this.previousReportTimeNS >= this.reportIntervalNS){
			
			double progress = 1.0*this.totalExecution/(this.testInfo.getRunCount()*this.testInfo.getTotalThreads());
			
			log.debug("currentExecutionCount " + currentExecutionCount + "this.previousTotalExecution" +this.previousTotalExecution
					+ " currentTime " +currentTime  + " this.previousReportTimeNS " + this.previousReportTimeNS);
			log.info("Total " +(currentExecutionCount-this.previousTotalExecution)+ " inserts for past " +(currentTime - this.previousReportTimeNS) / 1000000.0+ "ms " 
					+ "progress: " + progress*100 + "% " 
					+ "elapse time: " + currentTime/1000000000 + "seconds");//, estimate "+(currentTime/1000000000/progress -currentTime/1000000000) +" seconds to go.");
			try {
				PreparedStatement preparedStatement = 
						this.connect.prepareStatement( "insert into testRuntimeInfo "
								+ "(systemNanoTime,testId,totalExecutionCount) values "
								+ "(?,?,?)");
				preparedStatement.setLong(1, currentTime);
				preparedStatement.setInt(2, this.testInfo.getTestId());
				preparedStatement.setLong(3,currentExecutionCount);
				
				preparedStatement.execute();
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
			this.previousReportTimeNS = currentTime;
			this.previousTotalExecution = currentExecutionCount;
		}	

	}
	
	public void run(){
		//this.inputPipe = PipeManager.getInputPipe();
		this.queue = PipeManager.getQueue();
		
		
		while (!stop){
			if (!this.queue.isEmpty()){
				RunnerMessage runnerMessage=this.queue.poll();
				this.totalExecution += runnerMessage.intervalInsertCount;
				log.debug(runnerMessage.toString());
				if (this.previousReportTimeNS == 0){ this.previousReportTimeNS = System.nanoTime() - this.referenceNanoSecond;}
			}
			else{
				this.pause(10);
			}

			this.summaryReport();
			
		}
	}
}
