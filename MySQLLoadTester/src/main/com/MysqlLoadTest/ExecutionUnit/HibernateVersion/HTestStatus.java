package com.MysqlLoadTest.ExecutionUnit.HibernateVersion;

import java.util.Map;

import com.MysqlLoadTest.Interfaces.TestConfig;
import com.MysqlLoadTest.Interfaces.TestStatus;

public class HTestStatus implements TestStatus{

	private int id;
	
	private long userCount;
	private long userRecordCount;
	private int  intervalInsertCount;
	private int  intervalUpdateCount;
	private int  intervalSelectCount;
	private int  intervalDeleteCount;
	
	private TestConfig testConfig;
	
	@Override
	public float getProgress() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getStatus() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Map<String,Object> getReport() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setTestConfig(TestConfig testConfig) {
		this.testConfig = testConfig;
		
	}

}
