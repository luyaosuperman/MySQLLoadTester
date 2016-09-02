package com.MysqlLoadTest.ExecutionUnit.HibernateVersion;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.MysqlLoadTest.Interfaces.TestConfig;
import com.MysqlLoadTest.Interfaces.TestController;

public class HTester {
	
	private static Logger log = LogManager.getLogger(HTester.class); 
	
	

	public static void main(String[] args){
		
		//TestController testController = new HTestController();
		//TestController testController = new HTestController();
		TestController testController = null;
		try {
			testController = (TestController) Class.forName("com.MysqlLoadTest.ExecutionUnit.HibernateVersion.HTestController").newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
