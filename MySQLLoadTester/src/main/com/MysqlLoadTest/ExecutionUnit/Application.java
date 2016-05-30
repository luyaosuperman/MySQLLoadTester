package com.MysqlLoadTest.ExecutionUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.MysqlLoadTest.Utilities.TestInfo;

public class Application {

	private static Logger log = LogManager.getLogger(Application.class); 
	
	private static void runnerCall(){
		
	}
	
	public static int runTest(TestInfo testInfo) throws InterruptedException{
		//return testId
		log.debug("log4j.configurationFile: " + System.getProperty("log4j.configurationFile"));
		log.debug("java.class.path: " + System.getProperty("java.class.path"));
		log.info("Start");
		
		
		Controller controller = new Controller(testInfo);
		
		log.info("prepareData()");
		controller.prepareData();
		log.info("runTest()");
		return controller.runTest();

	}
	
	public static void main(String[] args) throws InterruptedException {
		//int testType = 1;
		int totalThreads = 10;
		int runCount = 3000;
		
		String tableName = "testLt";
		String createTableSql = "create table testLt (" +
								"id bigint unsigned auto_increment primary key, " +
								"runnerId int unsigned not null, " +
								"col1 int unsigned not null, " +
								"col2 bigint unsigned not null, " +
								"col3 varchar(255) not null, " + 
								"col4 varchar(255) not null)";
		int insertPct = 50;
		int selectPct = 30;
		int updatePct = 20;
		int initDataAmount = 100000;
		
		
		TestInfo testInfo = new TestInfo(totalThreads,runCount,"test",
				tableName,createTableSql,insertPct,selectPct,updatePct,initDataAmount);
		
		runTest(testInfo);

	}
}
