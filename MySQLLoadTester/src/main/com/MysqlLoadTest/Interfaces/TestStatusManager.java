package com.MysqlLoadTest.Interfaces;

import java.util.Map;

import javax.persistence.Entity;

@Entity
public interface TestStatusManager {

	public float getProgress();
	
	public int getStatus();
	
	public void setStatus(int status);
	
	public static final int PENDING = 0;
	public static final int PREPARING = 1;
	public static final int RUNNING = 2;
	public static final int FINISHED = 3;
	
	public void setTestConfig(TestConfig testConfig);
	
	public Map<String,Object> getReport();
	
	public void updateProgress(String item, Object value);
	
}
