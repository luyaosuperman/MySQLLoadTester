package com.MysqlLoadTest.Interfaces;

import java.util.List;
import java.util.Map;

public interface TestConfig {
	
	public Map<String,Object> getConfigItems();
	
	public void setConfigItems(Map<String,Object> configItems);
	
	public boolean isConfigSet();
	

}
