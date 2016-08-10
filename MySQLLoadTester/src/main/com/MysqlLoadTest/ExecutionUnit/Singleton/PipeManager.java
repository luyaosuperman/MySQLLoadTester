package com.MysqlLoadTest.ExecutionUnit.Singleton;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
public class PipeManager {
	
	private static Logger log = LogManager.getLogger(PipeManager.class); 

	/*private static PipedOutputStream outputPipe = new PipedOutputStream();
	private static PipedInputStream inputPipe = new PipedInputStream();	
	private static ObjectOutputStream objectOutputPipe = null;
	private static ObjectInputStream objectInputPipe = null;*/
	
	private static ConcurrentLinkedDeque<RunnerMessage> queue = new ConcurrentLinkedDeque<RunnerMessage>();

	public static ConcurrentLinkedDeque<RunnerMessage> getQueue() {
		return queue;
	}
	
	
	/*public static ObjectOutputStream getOutputPipe() { 
		try {
			if (objectOutputPipe == null){
				outputPipe.connect(inputPipe);
				objectOutputPipe = new ObjectOutputStream(outputPipe);
			}
			//ObjectOutputStream objectOutputPipe = new ObjectOutputStream(outputPipe);
			log.debug("Returning ObjectOutputStream as objectOutputPipe");
			return objectOutputPipe;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.debug("Returning ObjectOutputStream as null");
		return null;
		}
	public static ObjectInputStream  getInputPipe()  { 
		try {
			objectInputPipe = new ObjectInputStream(inputPipe);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.debug("Returning ObjectInputStream as client request");
		return objectInputPipe; 
		}*/
}
