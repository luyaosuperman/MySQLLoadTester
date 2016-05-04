package com.MysqlLoadTest.ExecutionUnit;

import java.io.IOException;
import java.io.ObjectInputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Reporter extends Thread{
	
	private static Logger log = LogManager.getLogger(Reporter.class); 
	private boolean stop = false;
	private long previousReportTimeNS = 0;
	private long reportIntervalNS = 1000000000;//nanoseconds
	
	private long totalExecution = 0;
	//private long intervalExecution = 0;
	private long previousTotalExecution = 0;
	
	private ObjectInputStream inputPipe;
	public void stopReporter(){
		this.stop = true;
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
		long currentTime = System.nanoTime();
		long currentExecutionCount = this.totalExecution;
		
		if ( this.previousReportTimeNS != 0 && currentTime - this.previousReportTimeNS >= this.reportIntervalNS){
			log.debug("currentExecutionCount " + currentExecutionCount + "this.previousTotalExecution" +this.previousTotalExecution
					+ " currentTime " +currentTime  + " this.previousReportTimeNS " + this.previousReportTimeNS);
			log.info("Total " +(currentExecutionCount-this.previousTotalExecution)+ " inserts for past " +(currentTime - this.previousReportTimeNS) / 1000000.0+ "ms" );
			
			this.previousReportTimeNS = currentTime;
			this.previousTotalExecution = currentExecutionCount;
		}	

	}
	
	public void run(){
		this.inputPipe = PipeManager.getInputPipe();
		
		while (!stop){
			try {
				Object o;
				//System.out.println("this.inputPipe: " + this.inputPipe);
				o =  this.inputPipe.readObject();
				if (o!= null){
					RunnerMessage runnerMessage = (RunnerMessage) o;
					this.totalExecution += runnerMessage.intervalInsertCount;
					log.debug(runnerMessage.toString());
					if (this.previousReportTimeNS == 0){ this.previousReportTimeNS = System.nanoTime();}
					//this.pause(1);
				}
			} catch (IOException | ClassNotFoundException | NullPointerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				this.pause(1000);
			}
			this.summaryReport();
			
			
			/*
			try {
				inputAvailable = inputPipe.available();
				log.debug("inputPipe.available returned: " + inputAvailable);
				if (inputAvailable >= RunnerMessage.structureSize) {
					//log.debug("inputPipe.available returned: " + inputAvailable);
					this.inputPipe.read(b, 0, RunnerMessage.structureSize);
					RunnerMessage runnerMessage = (RunnerMessage) RunnerMessage.deserialize(b);
					log.debug(runnerMessage.toString());
				}
				
			} catch (ClassNotFoundException |IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/

		}
	}
}
