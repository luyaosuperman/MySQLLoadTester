package com.MysqlLoadTest.ExecutionUnit.HibernateVersion;

import com.MysqlLoadTest.Interfaces.TestController;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.MysqlLoadTest.Interfaces.TestConfig;
import com.MysqlLoadTest.Interfaces.Teststatus;

public class HTestController extends Thread implements TestController {
	
	private static Logger log = LogManager.getLogger(HTestController.class); 

	
	private HRunner[] hRunnerArray = null;
	private int testIdentifier;
	private TestConfig hTestConfig = new HTestConfig();
	
	HTestController(){
		this.start();
	}
	
	@Override
	public TestConfig getConfigObject() {
		// TODO Auto-generated method stub
		return hTestConfig;
	}

	@Override
	public void startTest() {
		// TODO Auto-generated method stub
		if (!this.hTestConfig.isConfigSet() ){
			log.fatal("test started before config set");
			System.exit(1);
		}
		this.prepareTest();
		
	}

	@Override
	public void cancelTest() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Teststatus getTestStatus() {
		// TODO Auto-generated method stub
		return null;
	}

	private void prepareTest() {
		
	}
	private void runTest(){
		
	}

	@Override
	public int getTestIdentity() {
		// TODO Auto-generated method stub
		return 0;
	}

}
