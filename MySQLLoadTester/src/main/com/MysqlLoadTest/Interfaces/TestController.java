package com.MysqlLoadTest.Interfaces;

public interface TestController {

	public TestConfig getConfigObject();
	
	public void startTest();
	
	public void cancelTest();
	
	public Teststatus getTestStatus();
	
	public int getTestIdentity();
	
	
}
