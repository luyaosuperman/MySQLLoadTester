package com.MysqlLoadTest.ExecutionUnit.HibernateVersion;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HRunner extends Thread{

	private static Logger log = LogManager.getLogger(HRunner.class); 
	
	static int threadIdSeed=0;
	int threadId = -1;
	
	HTestConfig hTestConfig = null;
	
	final static int INITED = 1;
	final static int PREPARING = 2;
	final static int PREPARED = 3;
	final static int RUNNING = 4;
	final static int FINISHED = 5;
	
	private int runnerStatus = -1;
	private long preparedUserCountThisThread=0;
	private long createdUserCountThisThread=0;
	
	final static EntityManagerFactory emf; 
	EntityManager em = null;
	EntityTransaction ex = null;
	
	static{
		emf = Persistence.createEntityManagerFactory("HelloWorldPU");
		/*EntityManager em = emf.createEntityManager();
		EntityTransaction ex = em.getTransaction();
		ex.begin();
		String[] stringQuerys = {"DELETE FROM HUserRecord","DELETE FROM HUser"};
		for (String stringQuery: stringQuerys){
			Query query = em.createQuery(stringQuery);
			query.executeUpdate();
		}
		ex.commit();*/
		
	}
	
	HRunner(HTestConfig hTestConfig){
		this.threadId = threadIdSeed++;
		log.info(this.threadId + " Init HRunner");
		this.hTestConfig=hTestConfig;
		//this.emf =Persistence.createEntityManagerFactory("HelloWorldPU");
		this.em = emf.createEntityManager();
		
		runnerStatus = INITED;
		
		this.start();

	}
	
	protected void prepare(){
		
		if (this.runnerStatus == INITED){
			this.runnerStatus = PREPARING;
			log.info("runner " + this.threadId + " PREPARING");
		} else {
			log.fatal("runner status error. prepare() called while runner not in INITED status");
		}
		
	}
	
	protected boolean prepared(){
		return this.runnerStatus == PREPARED;
	}
	
	protected void runTest(){
		
		if (this.runnerStatus == PREPARED){
			this.runnerStatus = RUNNING;
			log.info("runner " + this.threadId + " RUNNING");
		} else {
			log.fatal("runner status error. runTest() called while runner not in PREPARED status");
		}

	}
	
	public void run(){
		log.info(this.threadId + " run()");
		
		while (true){
			switch (this.runnerStatus){
				case PREPARING:
					this.insert();
					this.preparedUserCountThisThread ++;
					if ( this.preparedUserCountThisThread > this.hTestConfig.userCountStart / this.hTestConfig.threadsCount){
						this.runnerStatus = PREPARED;
						this.createdUserCountThisThread=this.preparedUserCountThisThread;
						log.info("runner " + this.threadId + " prepared");
					}
					break;
				case RUNNING:
					//log.info("runner " + this.threadId + " case RUNNING:");
					this.insert();
					this.createdUserCountThisThread ++;
					if ( this.createdUserCountThisThread > this.hTestConfig.userCountStop / this.hTestConfig.threadsCount){
						this.runnerStatus = FINISHED;
						log.info("runner " + this.threadId + " finished");
					}
					break;
				case FINISHED:
					return;
				default:
					
			}
		}
		
	}
	
	private void insert(){
		
		//log.info(this.threadId + " Insert");
		
		this.ex = this.em.getTransaction();
		this.ex.begin();
		HUser hUser = new HUser();
		this.em.persist(hUser);
		for (int i=0;i< this.hTestConfig.userRecordPerUser;i++){
			HUserRecord hUserRecord = new HUserRecord(); 
			hUserRecord.sethUser(hUser);
			this.em.persist(hUserRecord);
		}
		this.ex.commit();
		
	}
	
	
}
