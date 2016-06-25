package com.MysqlLoadTest.Web;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.MysqlLoadTest.Utilities.TestInfo;
import com.MysqlLoadTest.ZabbixIntegration.Zabbix3;

@RestController
public class ZabbixRESTController {
	
	private Zabbix3 zabbix3;
	
	private TestInfo testInfo;
	
	
	@Autowired
	public void setZabbix3(Zabbix3 zabbix3) {		this.zabbix3 = zabbix3;	}
	
	@Autowired
	public void setTestInfo(TestInfo testInfo) {this.testInfo = testInfo;}
	
	public ZabbixRESTController(){
		
	}
	
	private TestInfo getTestInfo(int testId){
		
		if (testId == this.testInfo.getTestId()){
			return this.testInfo;
		} else {
			return TestInfo.getTestInfo(testId);
		}
	}

	@RequestMapping(value="/get_zabbix_items",method=RequestMethod.GET)
	public ArrayList<LinkedHashMap<String,String>> getZabbixItems(@RequestParam(value="testId", required=true) int testId){
		//from testId:
		//(1) hostip
		//return: list of itemids
		TestInfo queryingTestInfo = this.getTestInfo(testId);
		return this.zabbix3.getItems(queryingTestInfo);
		
	}
	
	@RequestMapping(value="/get_zabbix_history",method=RequestMethod.GET)
	public LinkedHashMap<String,ArrayList<LinkedHashMap<String,String>>> 
	getZabbixHistory(@RequestParam(value="testId", required=true) int testId, 
					 @RequestParam(value="itemids", required=true) String[] itemids){
		//from testId:
		//(1) hostip
		//(2) start and stop timestamp 
		TestInfo queryingTestInfo = this.getTestInfo(testId);
		
		return this.zabbix3.getHistory(queryingTestInfo, itemids);
		
		
	}


	
}
