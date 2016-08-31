package com.MysqlLoadTest.Interfaces;

import java.util.List;
import java.util.Map;

import javax.persistence.Entity;

@Entity
public interface TestConfig {
	
	public Map<String,Object> getConfigItems();
	
	public void setConfigItems(Map<String,Object> configItems);
	
	public boolean isConfigSet();
	
	public int getTestIdentifier();
	

}
