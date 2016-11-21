package com.MysqlLoadTest.ExecutionUnit.HibernateVersion;

import com.MysqlLoadTest.Interfaces.TestController;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.MysqlLoadTest.Interfaces.TestConfig;
import com.MysqlLoadTest.Interfaces.TestStatus;

public class HTestController extends Thread implements TestController {
	
	private static Logger log = LogManager.getLogger(HTestController.class); 

	
	private HRunner[] hRunnerArray = null;
	private HTestConfig hTestConfig = null;
	private HTestStatus hTestStatus = null;
	
	HTestController(){
		this.start();
		this.hTestConfig = new HTestConfig();
		this.hTestStatus = new HTestStatus();
		
	}
	
	@Override
	public TestConfig getConfigObject() {
		return this.hTestConfig;
	}
	
	@Override
	public TestStatus getTestStatus() {
		return this.hTestStatus;
	}


	@Override
	public void startTest() {
		// TODO Auto-generated method stub
		if (!this.hTestConfig.isConfigSet() ){
			log.fatal("test started before config set");
			System.exit(1);
		}
		
		this.hRunnerArray = new HRunner[this.hTestConfig.threadsCount];
		for (int i=0;i<this.hTestConfig.threadsCount;i++){
			this.hRunnerArray[i]=new HRunner(this.hTestConfig);
		}
		
		this.prepareTest();
		this.runTest();
		
	}

	@Override
	public void cancelTest() {
		// TODO Auto-generated method stub
		for (HRunner hRunner: hRunnerArray){
			log.fatal("Cancel test not implemented yet");
			System.exit(1);
		}
	}


	private void prepareTest() {
		for (HRunner hRunner: hRunnerArray){
			hRunner.prepare();
		}
		
		while(true){
			int preparedCount = 0;
			for (HRunner hRunner: hRunnerArray){
				if (hRunner.prepared()){
					preparedCount ++;
				}
				if (preparedCount == hRunnerArray.length){
					log.info("all runners prepared");
					return;
				}
			}
		}
		
	}
	private void runTest(){
		for (HRunner hRunner: hRunnerArray){
			hRunner.runTest();
		}
	}

	@Override
	public int getTestIdentity() {
		// TODO Auto-generated method stub
		return 0;
	}

}
