package com.MysqlLoadTest.ExecutionUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.MysqlLoadTest.Utilities.TestInfo;

public class Application {

	private static Logger log = LogManager.getLogger(Application.class); 
	

	
	public static void main(String[] args) {
		//int testType = 1;
		int totalThreads = 50;
		int runCount = 30000;
		int rowCount = 30000;
		
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
		
		
		int initDataAmount = 10000;
		
		
		TestInfo testInfo = new TestInfo(totalThreads,runCount,rowCount,"test",
				tableName,createTableSql,insertPct,selectPct,updatePct,initDataAmount);
		
		TestController controller = new TestController();
		controller.start();
		
		controller.startTest(testInfo);
		while (controller.testStatus() != TestController.NOTRUNNING){
			log.info("Waiting: " +
					"testId: " + testInfo.getTestId() +
					" status: " + testInfo.testStatus +
					" progress: " + testInfo.getTestProgress()
					);
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		log.info("test seems to be finished");
		
		log.info("Do it again");
		
		controller.startTest(testInfo);
		while (controller.testStatus() != TestController.NOTRUNNING){
			log.info("Waiting: " +
					"testId: " + testInfo.getTestId() +
					" status: " + testInfo.testStatus +
					" progress: " + testInfo.getTestProgress()
					);
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		log.info("test seems to be finished");

	}
}
