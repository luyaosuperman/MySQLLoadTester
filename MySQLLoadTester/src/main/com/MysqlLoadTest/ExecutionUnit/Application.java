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
	
	public static void main(String[] args) {
		//int testType = 1;
		int totalThreads = 20;
		int runCount = 3000000;
		int rowCount = 300000;
		
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
		
		
		int initDataAmount = 100000;
		
		
		TestInfo testInfo = new TestInfo(totalThreads,runCount,rowCount,"test",
				tableName,createTableSql,insertPct,selectPct,updatePct,initDataAmount);
		
		try {
			runTest(testInfo);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
