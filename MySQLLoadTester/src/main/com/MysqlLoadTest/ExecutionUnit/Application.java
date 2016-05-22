package com.MysqlLoadTest.ExecutionUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.MysqlLoadTest.Utilities.TestInfo;

public class Application {

	private static Logger log = LogManager.getLogger(Application.class); 
	
	public static int runTest(TestInfo testInfo) throws InterruptedException{
		//return testId
		log.debug("log4j.configurationFile: " + System.getProperty("log4j.configurationFile"));
		log.debug("java.class.path: " + System.getProperty("java.class.path"));
		log.info("Start");
		
		
		Prepare prepare = new Prepare();
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
			Thread.sleep(1000);
		}
		
		//for (int i =0; i<10; i++){instanceArray[i].join();}
		
		log.info("Finish");
		reporter.stopReporter();
		
		return testInfo.getTestId();
	}
	
	public static void main(String[] args) throws InterruptedException {
		int testType = 1;
		int totalThreads = 10;
		int runCount = 30000;
		
		TestInfo testInfo = new TestInfo(testType,totalThreads,runCount);
		
		runTest(testInfo);

	}
}
