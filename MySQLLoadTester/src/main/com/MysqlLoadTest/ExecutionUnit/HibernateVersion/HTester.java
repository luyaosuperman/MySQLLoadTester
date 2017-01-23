package com.MysqlLoadTest.ExecutionUnit.HibernateVersion;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.MysqlLoadTest.Interfaces.TestConfig;
import com.MysqlLoadTest.Interfaces.TestController;

public class HTester {
	
	private static Logger log = LogManager.getLogger(HTester.class); 
	
	

	public static void main(String[] args){
		
		System.setProperty("com.mchange.v2.log.MLog", "com.mchange.v2.log.FallbackMLog");

		
		TestController testController = new HTestController();
		TestConfig testConfig = testController.getConfigObject();
		Map<String,Object> configList = testConfig.getConfigItems();
		for(String key: configList.keySet()){
			//log.info("key: " + key);
			log.info("got "+ key + " "+configList.get(key));
		}
		
		testConfig.setConfigItems(configList);
		
		
		testController.startTest();
		
	}
	
}
