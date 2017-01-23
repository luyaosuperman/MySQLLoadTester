package com.MysqlLoadTest.ExecutionUnit.HibernateVersion;

import com.MysqlLoadTest.Interfaces.TestController;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.MysqlLoadTest.Interfaces.TestConfig;
import com.MysqlLoadTest.Interfaces.TestStatusManager;

public class HTestController extends Thread implements TestController {
	
	private static Logger log = LogManager.getLogger(HTestController.class); 

	
	private HRunner[] hRunnerArray = null;
	private HTestConfig hTestConfig = null;
	private HTestStatusManager hTestStatus = null;
	
	private EntityManager em = null;
	private EntityTransaction ex = null;
	
	HTestController(){
		this.start();
		this.hTestConfig = new HTestConfig();
		this.hTestStatus = new HTestStatusManager(this.hTestConfig);
		
		this.em = HRunner.emf.createEntityManager();
		
		this.ex = this.em.getTransaction();
		this.ex.begin();
		this.em.persist(this.hTestConfig);
		this.ex.commit();
		
	}
	
	@Override
	public TestConfig getConfigObject() {
		return this.hTestConfig;
	}
	
	@Override
	public TestStatusManager getTestStatus() {
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
			this.hRunnerArray[i]=new HRunner(this.hTestConfig, this.hTestStatus);
		}
		
		this.hTestStatus.setStatus(TestStatusManager.PREPARING);
		this.prepareTest();
		this.hTestStatus.setStatus(TestStatusManager.RUNNING);
		this.runTest();
		this.hTestStatus.setStatus(TestStatusManager.FINISHED);
		
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
				if (hRunner.isPrepared()){
					preparedCount ++;
				}
				if (preparedCount == hRunnerArray.length){
					log.info("all runners prepared");
					return;
				}
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	private void runTest(){
		for (HRunner hRunner: hRunnerArray){
			hRunner.runTest();
		}
		
		while(true){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			int finishedCount = 0;
			/*long insertedUserCountThisInterval=0;
			long updatedUserCountThisInterval=0;
			long selectedUserCountThisInterval=0;*/
			
			for (HRunner hRunner: hRunnerArray){
				if (hRunner.isFinished()){
					finishedCount ++;
				}
				
				/*HRunner.Stastics stastics = hRunner.getStastics();
				insertedUserCountThisInterval += stastics.insertedUserCountThisThread;
				updatedUserCountThisInterval  += stastics.updatedUserCountThisThread;
				selectedUserCountThisInterval += stastics.selectedUserCountThisThread;*/
				
			}
			
			
			/*this.ex = this.em.getTransaction();
			this.ex.begin();
			this.em.persist(this.hTestStatus);
			this.ex.commit();*/
			
			//log.info("Interval insert: " + insertedUserCountThisInterval + " update : " + updatedUserCountThisInterval + " select : " + selectedUserCountThisInterval);
			
			if (finishedCount == hRunnerArray.length){
				log.info("all runners finished");
				return;
			}
			

		}
	}

	@Override
	public int getTestIdentity() {
		// TODO Auto-generated method stub
		return 0;
	}

}
