package com.MysqlLoadTest.ExecutionUnit.HibernateVersion;

import java.util.Map;

import com.MysqlLoadTest.Interfaces.StatusItem;
import com.MysqlLoadTest.Interfaces.TestConfig;
import com.MysqlLoadTest.Interfaces.TestStatus;

public class HTestStatus implements TestStatus{

	private int id;
	
	@StatusItem private long userCount;
	@StatusItem private long userRecordCount;
	
	
	
	@StatusItem private int  intervalInsertCount;
	@StatusItem private int  intervalUpdateCount;
	@StatusItem private int  intervalSelectCount;
	@StatusItem private int  intervalDeleteCount;
	
	private int status = TestStatus.PENDING;
	
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

	@Override
	public void setStatus(int status) {
		this.status = status;
	}

}
