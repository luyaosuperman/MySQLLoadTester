package com.MysqlLoadTest.ExecutionUnit.HibernateVersion;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.MysqlLoadTest.ExecutionUnit.Singleton.PipeManager;
import com.MysqlLoadTest.Interfaces.ConfigurableItem;
import com.MysqlLoadTest.Interfaces.TestConfig;
import com.MysqlLoadTest.Utilities.ConfigLoader;
import com.MysqlLoadTest.Utilities.LoadFromConfig;

public class HTestConfig implements TestConfig {
	
	private static Logger log = LogManager.getLogger(HTestConfig.class); 
	
	private int id;

	@ConfigurableItem @LoadFromConfig protected long userCountStart;
	@ConfigurableItem @LoadFromConfig protected long userCountStop;
	@ConfigurableItem @LoadFromConfig protected long userRecordPerUser;
	
	@ConfigurableItem @LoadFromConfig protected int  threadsCount;
	
	@ConfigurableItem @LoadFromConfig protected int  insertPct;
	@ConfigurableItem @LoadFromConfig protected int  selectPct;
	@ConfigurableItem @LoadFromConfig protected int  deletePct;
	@ConfigurableItem @LoadFromConfig protected int  updatePct;
	
	@ConfigurableItem @LoadFromConfig protected int  usernameLength;
	@ConfigurableItem @LoadFromConfig protected int  userDataLength;
	
	@ConfigurableItem @LoadFromConfig String testComment;
	
	private boolean configSet = false;
	
	public HTestConfig(){
		ConfigLoader.loadFromConfig(this);
	}

	@Override
	public Map<String,Object> getConfigItems() {
		// TODO Auto-generated method stub
		//log.info("entering public Map<String,Object> getConfigItems()");
		Map<String,Object> result = new LinkedHashMap<String,Object>();
		Field[] fields = this.getClass().getDeclaredFields();
		//log.info(this.getClass().getName());
		for (Field field:fields){
			//log.info("field.getName(): " + field.getName());
			try {
				if(field.getAnnotation(ConfigurableItem.class)!=null){
					//log.info(field.getName() + " " +field.getAnnotation(ConfigurableItem.class));
					field.setAccessible(true);
					result.put(field.getName(), field.get(this));
					//log.info("field.getName(): " + field.getName() + " field.get(this): " + field.get(this));
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
				Field field = this.getClass().getDeclaredField(key);
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

	@Override
	public int getTestIdentifier() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getId() {		return id;	}

}
