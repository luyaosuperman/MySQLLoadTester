package com.MysqlLoadTest.demo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PipedInputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Reporter extends Thread{
	
	private static Logger log = LogManager.getLogger(Reporter.class); 
	private boolean stop = false;
	
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
	
	public void run(){
		this.inputPipe = PipeManager.getInputPipe();
		
		while (!stop){
			try {
				Object o;
				//System.out.println("this.inputPipe: " + this.inputPipe);
				o =  this.inputPipe.readObject();
				if (o!= null){
					RunnerMessage runnerMessage = (RunnerMessage) o;
					log.debug(runnerMessage.toString());
					this.pause(10);
				}
			} catch (IOException | ClassNotFoundException | NullPointerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				this.pause(1000);
			}
			
			
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
