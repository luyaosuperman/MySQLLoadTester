package com.MysqlLoadTest.demo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class RunnerMessage implements java.io.Serializable{

	public Date date;
	public int threadID;
	public int totalInsertCount;
	public long reportInterval;
	public int intervalInsertCount;
	
	/*public static int structureSize = 0;
	
	public static byte[] serialize(Object obj) throws IOException {
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    ObjectOutputStream os = new ObjectOutputStream(out);
	    os.writeObject(obj);
	    structureSize = out.toByteArray().length;
	    System.out.println("serized size: " + out.toByteArray().length);
	    return out.toByteArray();
	}
	public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
	    ByteArrayInputStream in = new ByteArrayInputStream(data);
	    ObjectInputStream is = new ObjectInputStream(in);
	    
	    return is.readObject();
	}*/
	
	public String toString(){
		
		return  "record date: " + this.date + 
				" ThreadID: " + this.threadID + 
				" totalInsertCount: " + this.totalInsertCount +
				" reportInterval: " + this.reportInterval + 
				" intervalInsertCount: " + this.intervalInsertCount;
	}
	
}
