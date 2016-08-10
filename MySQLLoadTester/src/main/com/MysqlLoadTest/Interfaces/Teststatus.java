package com.MysqlLoadTest.Interfaces;

import java.util.Map;

public interface Teststatus {

	public float getProgress();
	
	public int getStatus();
	
	public int getTestIdentifier();
	
	public static final int PENDING = 0;
	public static final int PREPARING = 1;
	public static final int RUNNING = 2;
	public static final int FINISHED = 3;
	
	public Map getReport();
	
}
