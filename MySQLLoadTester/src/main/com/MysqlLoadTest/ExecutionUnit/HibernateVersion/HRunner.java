package com.MysqlLoadTest.ExecutionUnit.HibernateVersion;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

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
	HTestStatusManager hTestStatus = null;
	
	final static int INITED = 1;
	final static int PREPARING = 2;
	final static int PREPARED = 3;
	final static int RUNNING = 4;
	final static int FINISHED = 5;
	
	private int runnerStatus = -1;
	
	private long preparedUserCountThisThread=0;
	private long actionCountThisThread = 0;
	
	long reportMillisInterval = 100;
	long lastReportMillis = System.currentTimeMillis();
	
	Stastics statics = new Stastics();
	
	private long minId = -1,maxId = -1;
	

	final static EntityManagerFactory emf = Persistence.createEntityManagerFactory("HelloWorldPU");; 
	EntityManager em = null;
	EntityTransaction ex = null;
	
	/*static{
		emf = Persistence.createEntityManagerFactory("HelloWorldPU");	
	}*/
	
	HRunner(HTestConfig hTestConfig, HTestStatusManager hTestStatus){
		this.threadId = threadIdSeed++;
		log.info(this.threadId + " Init HRunner");
		this.hTestConfig=hTestConfig;
		this.hTestStatus = hTestStatus;
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
	
	protected boolean isPrepared(){
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
	
	protected boolean isFinished(){
		return this.runnerStatus == FINISHED;
	}
	
	private void action(){
		int randomNum = ThreadLocalRandom.current().nextInt(100)+1;//1~100
		//insert:0-insert
		if (randomNum <=this.hTestConfig.insertPct){
			//insert
			this.insert();
		}else if (randomNum <=this.hTestConfig.insertPct + this.hTestConfig.updatePct){
			//update
			this.update();
		}else{
			//select
			this.select();
		}
		
		if (System.currentTimeMillis() - this.lastReportMillis > this.reportMillisInterval){
			this.reportStatics();
			this.lastReportMillis = System.currentTimeMillis();
		}
	}
	
	private boolean ifStop(){
		return this.actionCountThisThread > this.hTestConfig.userCountStop / this.hTestConfig.threadsCount;
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
						this.statics.intervalInsertCount=this.preparedUserCountThisThread;
						log.info("runner " + this.threadId + " prepared");
					}
					break;
				case RUNNING:
					
					this.action();
					this.actionCountThisThread ++;
					if ( this.ifStop() ){
						this.runnerStatus = FINISHED;
						log.info("runner " + this.threadId + " finished");
					}
					//log.info("runner " + this.threadId + " this.createdUserCountThisThread: " + this.createdUserCountThisThread);
					//log.info("runner " + this.threadId + " case RUNNING:");
					break;
				case FINISHED:
					return;
				default:
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
		}
		
	}
	
	private void setMinMaxId(long id){
		if (this.minId < 0) {this.minId = id;}
		
		this.maxId = id;
	}
	
	private long getRandomId(){
		return ThreadLocalRandom.current().nextLong(this.minId, this.maxId); // omit +1
		
	}
	
	private void insert(){
		
		//log.info("Thread " + this.threadId + " Insert");
		this.statics.intervalInsertCount ++;
		
		this.ex = this.em.getTransaction();
		this.ex.begin();
		HUser hUser = new HUser();
		this.em.persist(hUser);
		this.ex.commit();
		
		this.setMinMaxId(hUser.getId());
		for (int i=0;i< this.hTestConfig.userRecordPerUser;i++){
			this.ex = this.em.getTransaction();
			this.ex.begin();
			HUserRecord hUserRecord = new HUserRecord(); 
			hUserRecord.sethUser(hUser);
			this.em.persist(hUserRecord);
			this.ex.commit();
		}
		//this.ex.commit();
		
		
	}
	
	private void update(){
		
		//log.info("Thread " + this.threadId + " Update");
		this.statics.intervalUpdateCount ++ ;
		
		HUser hUser = this.selectHUser();
		if (hUser == null) {return;}
		Set<HUserRecord> hUserRecordSet = selectHUserRecord(hUser);
		
		this.ex = this.em.getTransaction();
		this.ex.begin();
			hUser.updateValue();
			em.persist(hUser);
			for(HUserRecord hUserRecord: hUserRecordSet){
				hUserRecord.updateValue();
				em.persist(hUserRecord);
			}
		
		this.ex.commit();
		
	}
	
	
	private void select(){
		
		//log.info("Thread " + this.threadId + " Select");
		this.statics.intervalSelectCount ++;
		
		this.selectHUserRecord(this.selectHUser());
	}
	
	private HUser selectHUser(){
		long id = this.getRandomId();
		HUser hUser = null;
		if (id >= 0) {
			hUser = this.em.find(HUser.class, id);
		}
		return hUser;
	}
	
	private Set<HUserRecord> selectHUserRecord( HUser hUser ){
		if (hUser == null) { return null; }
		else { return hUser.getHUserRecordSet(); }
	}
	
	
	/*public Stastics getStastics(){
		try{
			this.suspend();
			return this.statics;
		}
		finally{
			this.statics = new Stastics();
			this.resume();
		}
	}*/
	
	private void reportStatics(){
		//log.info("update statics for thread " + this.threadId);
		for(Field field: Stastics.class.getDeclaredFields()){
			try {
				this.hTestStatus.updateProgress(field.getName(), field.get(statics));
				//log.info("updated " + field.getName() + " with value " + field.get(statics));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		statics = new Stastics();
	}
	
}
