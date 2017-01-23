package com.MysqlLoadTest.ExecutionUnit.HibernateVersion;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Persistence;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.MysqlLoadTest.Interfaces.StatusItem;
import com.MysqlLoadTest.Interfaces.TestConfig;
import com.MysqlLoadTest.Interfaces.TestStatusManager;



public class HTestStatusManager extends Thread implements TestStatusManager {
	
	private static Logger log = LogManager.getLogger(TestStatusManager.class); 
	

	
	private int status = TestStatusManager.PENDING;
	private TestConfig testConfig;

	private int flushIntervalMillis = 1000;
	
	private EntityManager em = null;
	private EntityTransaction ex = null;
	
	private HTestStatus hTestStatus = new HTestStatus();
	
	public HTestStatusManager(TestConfig testConfig){
		this.em = HRunner.emf.createEntityManager();
		this.testConfig = testConfig;
		this.start();
	}
	
	
	@Override
	public float getProgress() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getStatus() {
		return this.status;
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

	@Override
	public void updateProgress(String item, Object value) {
		// TODO Auto-generated method stub
		if (HTestStatus.statusItemList.contains(item)){
				try {
					Field field = HTestStatus.class.getDeclaredField(item);
					field.set(this.hTestStatus, (long) field.get(this.hTestStatus) + (long) value);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		} else {
			log.error("non-exist status item " + item);
		}
	}
	
	public void flushProgress(){
		this.hTestStatus.currentTime = System.currentTimeMillis();
		this.hTestStatus.testId = this.testConfig.getTestIdentifier();
		
		this.ex = this.em.getTransaction();
		this.ex.begin();
		this.em.persist(this.hTestStatus);
		this.ex.commit();
		
		log.info("Interval insert: " + this.hTestStatus.intervalInsertCount + " update : " + this.hTestStatus.intervalUpdateCount + " select : " + this.hTestStatus.intervalSelectCount);
		
		this.hTestStatus = new HTestStatus();
		//log.info("flushProgress finished");
	}
	
	public void run(){
		while (true){
			//log.info("this.status " + this.status);
			try {
				Thread.sleep(this.flushIntervalMillis);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(this.status == HTestStatusManager.RUNNING){
				//log.info("try to flushProgress");
				this.flushProgress();
				
			}
		}
	}
	
	

}
