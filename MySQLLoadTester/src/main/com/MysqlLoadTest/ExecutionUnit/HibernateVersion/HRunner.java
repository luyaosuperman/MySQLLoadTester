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
	
	final static EntityManagerFactory emf; 
	EntityManager em = null;
	EntityTransaction ex = null;
	
	static{
		emf = Persistence.createEntityManagerFactory("HelloWorldPU");
		EntityManager em = emf.createEntityManager();
		EntityTransaction ex = em.getTransaction();
		ex.begin();
		String[] stringQuerys = {"DELETE FROM HUserRecord","DELETE FROM HUser"};
		for (String stringQuery: stringQuerys){
			Query query = em.createQuery(stringQuery);
			query.executeUpdate();
		}
		ex.commit();
		
	}
	
	HRunner(HTestConfig hTestConfig){
		this.threadId = threadIdSeed++;
		log.info(this.threadId + " Init HRunner");
		this.hTestConfig=hTestConfig;
		//this.emf =Persistence.createEntityManagerFactory("HelloWorldPU");
		this.em = emf.createEntityManager();

	}
	
	protected void prepare(){}
	
	protected void runTest(){
		this.start();

	}
	
	public void run(){
		log.info(this.threadId + " runTest");
		while (true){
			this.insert();
		}
	}
	
	private void insert(){
		
		log.info(this.threadId + " Insert");
		
		this.ex = this.em.getTransaction();
		this.ex.begin();
		HUser hUser = new HUser();
		HUserRecord hUserRecord = new HUserRecord(); 
		hUserRecord.sethUser(hUser);
		this.em.persist(hUser);
		this.em.persist(hUserRecord);
		this.ex.commit();
		
	}
	
	
}
