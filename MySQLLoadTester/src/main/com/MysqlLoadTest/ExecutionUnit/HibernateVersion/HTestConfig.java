package com.MysqlLoadTest.ExecutionUnit.HibernateVersion;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.MysqlLoadTest.ExecutionUnit.Singleton.PipeManager;
import com.MysqlLoadTest.Interfaces.ConfigurableItem;
import com.MysqlLoadTest.Interfaces.TestConfig;
import com.MysqlLoadTest.Utilities.LoadFromConfig;

public class HTestConfig implements TestConfig {
	
	private static Logger log = LogManager.getLogger(HTestConfig.class); 

	@ConfigurableItem @LoadFromConfig long userCountStart;
	@ConfigurableItem @LoadFromConfig long userCountStop;
	@ConfigurableItem @LoadFromConfig long userRecordPerUser;
	
	@ConfigurableItem @LoadFromConfig int  threadsCount;
	
	@ConfigurableItem @LoadFromConfig int  insertPct;
	@ConfigurableItem @LoadFromConfig int  selectPct;
	@ConfigurableItem @LoadFromConfig int  deletePct;
	@ConfigurableItem @LoadFromConfig int  updatePct;
	
	@ConfigurableItem @LoadFromConfig int  usernameLength;
	@ConfigurableItem @LoadFromConfig int  userDataLength;
	
	@ConfigurableItem @LoadFromConfig String testComment;
	
	private boolean configSet = false;

	@Override
	public Map<String,Object> getConfigItems() {
		// TODO Auto-generated method stub
		Map<String,Object> result = new LinkedHashMap<String,Object>();
		Field[] fields = this.getClass().getFields();
		for (Field field:fields){
			try {
				if(field.getAnnotationsByType(ConfigurableItem.class)!=null){
					field.setAccessible(true);
					result.put(field.getName(), field.get(this));
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
		return result;
	}

	@Override
	public void setConfigItems(Map<String,Object> configItems) {
		// TODO Auto-generated method stub
		for(String key: configItems.keySet()){
			
			try {
				Object value = configItems.get(key);
				Field field = this.getClass().getField(key);
				field.setAccessible(true);
				field.set(this, value);
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.configSet = true;
		
	}

	public boolean isConfigSet() {		return configSet;	}
	
}
