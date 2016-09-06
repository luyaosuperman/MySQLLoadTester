package com.MysqlLoadTest.ExecutionUnit.Singleton;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.MysqlLoadTest.Utilities.ConfigLoader;
import com.MysqlLoadTest.Utilities.ConnectionInfo;
import com.MysqlLoadTest.Utilities.ConnectionManager;
import com.MysqlLoadTest.Utilities.LoadFromConfig;
import com.MysqlLoadTest.Utilities.TestInfo;
import com.mysql.jdbc.Statement;

public class Reporter extends Thread{
	
	private static Logger log = LogManager.getLogger(Reporter.class); 
	private boolean stop = false;
	private long previousReportTimeNS = 0;
	
	@LoadFromConfig
	private long reportIntervalNS;// = ConfigLoader.config.getLong("Reporter.reportIntervalNS");//1000000000;//nanoseconds
	
	private long runCount = 0;
	private long insertCount;
	private long updateCount;
	private long selectCount;
	
	private long previousRunCount = 0;
	private long previousIntervalInsertCount;
	private long previousIntervalUpdateCount;
	private long previousIntervalSelectCount;
	
	//private ObjectInputStream inputPipe;
	private ConcurrentLinkedDeque<RunnerMessage> queue;
	
	private Connection connect;
	private TestInfo testInfo;
	
	private long referenceNanoSecond ;
	
	@LoadFromConfig
	private ConnectionInfo connectionInfo;
	
	//TODO
	//Prepare run distinguish: Done
	//insert,select,update report
	
	
	private void preRunSummary(){
		//Generate the log into database, about configured test information
		
		try {
			PreparedStatement preparedStatement = 
					this.connect.prepareStatement(
					"insert into testInfo "
					+ "(timestamp,threads,runCount,rowCount,comment,"
					+ "tableName, createTableSql,"
					+ "insertPct, selectPct, updatePct,"
					+ "initDataAmount) values "
					+ "(unix_timestamp(),?,?,?,?,?,?,?,?,?,?)",
					//TODO
					//marker
					Statement.RETURN_GENERATED_KEYS);
			
			//preparedStatement.setTimestamp(1, new java.sql.Timestamp(System.currentTimeMillis()));
			preparedStatement.setInt(1, testInfo.getTotalThreads());
			preparedStatement.setLong(2, testInfo.getRunCount());
			preparedStatement.setLong(3, testInfo.getRowCount());
			preparedStatement.setString(4, testInfo.getComment());
			preparedStatement.setString(5, testInfo.getTableName());
			preparedStatement.setString(6, testInfo.getCreateTableSQL());
			preparedStatement.setInt(7, testInfo.getInsertPct());
			preparedStatement.setInt(8, testInfo.getSelectPct());
			preparedStatement.setInt(9, testInfo.getUpdatePct());
			preparedStatement.setFloat(10, testInfo.getInitDataAmount());
			
			
			preparedStatement.executeUpdate();
			testInfo.setTestDate( (int) (System.currentTimeMillis() / 1000L));
			
			ResultSet rs = preparedStatement.getGeneratedKeys();
			if (rs.next()) {
				this.testInfo.setTestId(rs.getInt(1));
			}
			else{
				assert false;
			}
			
			
		} catch (SQLException e) {
			//TODO 
			//Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void postRunSummary(){
		//Generate the log into database, about the summary of test after the test finished
		
	}
	
	public Reporter(TestInfo testInfo){
		
		ConfigLoader.loadFromConfig(this);
		
		this.testInfo = testInfo;
		this.referenceNanoSecond = System.nanoTime();
		
		connect = ConnectionManager.getConnection(this.connectionInfo);
		if (this.testInfo.testStatus != TestInfo.PREPARING){
			this.preRunSummary();
			}
		
	}
	
	public void stopReporter(){
		this.stop = true;
		if (this.testInfo.testStatus != TestInfo.PREPARING){this.postRunSummary();}
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
		
		long currentTime = System.nanoTime() - this.referenceNanoSecond;
		long currentRunCount = this.runCount;
		long currentInsertCount = this.insertCount;
		long currentUpdateCount = this.updateCount;
		long currentSelectCount = this.selectCount;
		
		if ( this.previousReportTimeNS != 0 && currentTime - this.previousReportTimeNS >= this.reportIntervalNS){
			
			double progress;
			
			if (this.testInfo.testStatus != TestInfo.PREPARING){
				if (this.testInfo.getInsertPct()>0){
					progress = 1.0*(this.insertCount)/(this.testInfo.getRowCount() - this.testInfo.getInitDataAmount());
				}else{
					progress = 1.0*this.runCount/(this.testInfo.getRunCount());
				}
			}else{
				progress = 1.0*this.runCount/(this.testInfo.getInitDataAmount());
			}
			
			this.testInfo.setTestProgress(progress);
			
			String info = currentTime/1000000000 + "Sec "+
					"ROW:" + this.testInfo.getMaxId() + " " +
					"T:" + Long.toString(currentRunCount-this.previousRunCount) + '/' +
					"I:" + Long.toString(currentInsertCount-this.previousIntervalInsertCount) + " " +
					"U:" + Long.toString(currentUpdateCount-this.previousIntervalUpdateCount) + " " +
					"S:" + Long.toString(currentSelectCount-this.previousIntervalSelectCount) + " " +
					"Pct: " + progress*100 + "%";
			if (this.testInfo.testStatus == TestInfo.PREPARING){
				info = "*prepare " + info;
			}
			log.info(info);
			
			if (this.testInfo.testStatus != TestInfo.PREPARING){
				try {
					PreparedStatement preparedStatement = 
							this.connect.prepareStatement( "insert into testRuntimeInfo "
									+ "(systemNanoTime,testId,"
									+ "runCount,insertCount,updateCount,selectCount"
									+ ") values "
									+ "(?,?,"
									+ "?,?,?,?)");
					preparedStatement.setLong(1, currentTime);
					preparedStatement.setInt(2, this.testInfo.getTestId());
					preparedStatement.setLong(3,currentRunCount);
					preparedStatement.setLong(4,currentInsertCount);
					preparedStatement.setLong(5,currentUpdateCount);
					preparedStatement.setLong(6,currentSelectCount);
					
					preparedStatement.execute();
					
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			this.previousReportTimeNS = currentTime;
			this.previousRunCount = currentRunCount;
			this.previousIntervalInsertCount = currentInsertCount;
			this.previousIntervalUpdateCount = currentUpdateCount;
			this.previousIntervalSelectCount = currentSelectCount;
		}	

	}
	
	public void run(){
		this.queue = PipeManager.getQueue();
		
		
		while (!stop){
			if (!this.queue.isEmpty()){
				RunnerMessage runnerMessage=this.queue.poll();
				this.runCount += runnerMessage.intervalRunCount;
				this.insertCount += runnerMessage.intervalInsertCount;
				this.updateCount += runnerMessage.intervalUpdateCount;
				this.selectCount += runnerMessage.intervalSelectCount;
				log.debug(runnerMessage.toString());
				if (this.previousReportTimeNS == 0){ this.previousReportTimeNS = System.nanoTime() - this.referenceNanoSecond;}
			}
			else{
				//this.pause(100);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			this.summaryReport();
			
		}
	}
}
