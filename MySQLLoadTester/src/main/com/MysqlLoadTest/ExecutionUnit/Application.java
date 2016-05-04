package com.MysqlLoadTest.ExecutionUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Application {

	private static Logger log = LogManager.getLogger(Application.class); 
	
	public static void main(String[] args) throws InterruptedException{
		
		log.debug("log4j.configurationFile: " + System.getProperty("log4j.configurationFile"));
		log.debug("java.class.path: " + System.getProperty("java.class.path"));
		log.info("Start");
		
		
		int totalThreads = 10;
		int runCount = 10000000;
		
		Prepare prepare = new Prepare();
		Runner[] instanceArray = new Runner[totalThreads];
		int finishedThreads = 0;
		
		
		for (int i =0; i<totalThreads; i++){
			instanceArray[i] = new Runner(i,runCount);
			instanceArray[i].start();
		}
		
		Reporter reporter = new Reporter();
		reporter.start();
		
		while (finishedThreads < totalThreads)
		{
			finishedThreads = 0;
			for (int i = 0; i<totalThreads; i++){
				if (instanceArray[i].getFinished() == true){finishedThreads ++;}
			}
			log.debug("thread finished count/total: " + finishedThreads + "/" + totalThreads);
			Thread.sleep(1000);
		}
		
		//for (int i =0; i<10; i++){instanceArray[i].join();}
		
		log.info("Finish");
		reporter.stopReporter();
	}
}
