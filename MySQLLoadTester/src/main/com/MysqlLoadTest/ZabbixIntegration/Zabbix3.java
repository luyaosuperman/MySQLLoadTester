package com.MysqlLoadTest.ZabbixIntegration;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.MysqlLoadTest.Utilities.ConfigLoader;
import com.MysqlLoadTest.Utilities.LoadFromConfig;
import com.MysqlLoadTest.Utilities.TestInfo;
import com.MysqlLoadTest.Zabbix.Params.HistoryFilter;
import com.MysqlLoadTest.Zabbix.Params.HostFilter;
import com.MysqlLoadTest.Zabbix.Params.ItemFilter;
import com.googlecode.jsonrpc4j.JsonRpcHttpClient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Zabbix3{

	private static Logger log = LogManager.getLogger(Zabbix3.class); 
	
	//private TestInfo testInfo;
	
	//@LoadFromConfig
	//private String zabbixServerHostname;
	
	@LoadFromConfig	private String username;// = "admin";
	@LoadFromConfig	private String password;// = "zabbix";
	@LoadFromConfig	private String url;// = "http://192.168.1.137/zabbix/api_jsonrpc.php";
	
	private String authToken=null;
	Map<String,Object> additionalJsonContent;
	private JsonRpcHttpClient client;
	
	Zabbix3(){
		ConfigLoader.loadFromConfig(this);
		try {
			this.client = new JsonRpcHttpClient(new URL(this.url));
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.Auth();
		
	}
	
	//public void setTestInfo(TestInfo testInfo){		this.testInfo = testInfo;	}
	
	public ArrayList<LinkedHashMap<String,String>> getItems (TestInfo testInfo){
		String hostid = this.getHostid(testInfo.zabbixHostIP);
		return this.getItems(hostid);
	}
	public LinkedHashMap<String,ArrayList<LinkedHashMap<String,String>>> getHistory (TestInfo testInfo, String[] itemids){
		Date startDate = testInfo.getTestDate();
		int time_from = (int) ( startDate.getTime() / 1000L);
		int duration = testInfo.getTestDuration();
		int time_till = time_from + duration;
		
		
		LinkedHashMap<String,ArrayList<LinkedHashMap<String,String>>> result = new LinkedHashMap<String,ArrayList<LinkedHashMap<String,String>>>();
		
		for (String itemid: itemids){
			log.info("retriving history for " + itemid + " from " + time_from + " till " + time_till);
			result.put(itemid, this.getHistory(itemid, String.valueOf(time_from), String.valueOf(time_till)));
		}
		
		return result;
	}
	
	private void Auth(){
		this.additionalJsonContent  = new HashMap<String,Object>();
		this.additionalJsonContent.put("auth", null);
		
		Map<String,Object> paramsMap = new HashMap<String,Object>();
		paramsMap.put("user", this.username);
		paramsMap.put("password", this.password);
		try {
			this.authToken = this.client.invoke("user.login", paramsMap, String.class);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//log.info("Token returned: " + this.authToken);
		this.additionalJsonContent.replace("auth", this.authToken);
		this.client.setAdditionalJsonContent(this.additionalJsonContent);
	}
	
	private String getHostid(String hostname){
		String method = "host.get";
		
		ArrayList<String> host = new ArrayList<String>();
		host.add(hostname);
		
		/*Map<String,Object> paramsMap = new HashMap<String,Object>();
		paramsMap.put("output", "extended");
		paramsMap.put("filter", (new HashMap<String,Object>()).put("host", host));*/
		
		HostFilter hostFilter=new HostFilter();
		
		//hostFilter.setOutput("extend");
		hostFilter.filter.ip.add(hostname);
		
		try {
			ArrayList<LinkedHashMap<String,String>> hostIdList =  this.client.invoke(method, hostFilter,new ArrayList<LinkedHashMap<String,String>>().getClass());
			//log.info("hostIdList.get(0).getClass(): " + hostIdList.get(0).getClass());
			
			for (LinkedHashMap<String,String> hostId: hostIdList){
				//log.info("hostId.getClass()" + hostId.getClass());
				String hostid = hostId.get("hostid");
				//log.info("hostid: "+ hostid);
				return hostid;
			}
			
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		return null;
		
	}
	
	private ArrayList<LinkedHashMap<String,String>>  getItems(String hostid){
		
		String method = "item.get";
		
		ItemFilter itemFilter = new ItemFilter();
		
		itemFilter.hostids = hostid;
		itemFilter.sortfield = "name";
		itemFilter.search.put("key_", "mysql");
		itemFilter.output.add("name");
		itemFilter.output.add("key_");
		
		try {
			ArrayList<LinkedHashMap<String,String>> itemListArray = this.client.invoke(method, itemFilter, new ArrayList<LinkedHashMap<String,String>>().getClass());
			return itemListArray;
			 
			
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
		
	}
	
	private ArrayList<LinkedHashMap<String,String>> getHistory(String itemids, String time_from, String time_till){
		String method = "history.get";
		
		HistoryFilter historyFilter = new HistoryFilter();
		historyFilter.itemids = itemids;
		historyFilter.time_from = time_from;
		historyFilter.time_till = time_till;
		try {
			ArrayList<LinkedHashMap<String,String>> historyListArray = this.client.invoke(method, historyFilter, new ArrayList<LinkedHashMap<String,String>>().getClass());
			for (LinkedHashMap<String,String> historyList: historyListArray){
				for (Entry<String,String> entry:  historyList.entrySet()){
					log.info(entry.getKey() + " " + entry.getValue());
				}
			}
			return historyListArray;
			 
			
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	private void testResponse(){
		try {
			
			this.additionalJsonContent  = new HashMap<String,Object>();
			this.additionalJsonContent.put("auth", null);
			
			this.client.setAdditionalJsonContent(this.additionalJsonContent);
			
			log.info("return value:" + this.client.invoke("apiinfo.version", new Object[]{"dummy","dummy"}, String.class));
			//log.info(zabbixInfo.toString());
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public static void main(String args[]){
		Zabbix3 zabbix3 = new Zabbix3();
		zabbix3.testResponse();
		zabbix3.Auth();
		ArrayList<LinkedHashMap<String,String>> historyListArray = zabbix3.getHistory("24089", "1466697600", "1466739546");
		for(LinkedHashMap<String,String>historyList : historyListArray){
			String clock = historyList.get("clock");
			String value = historyList.get("value");
			log.info("found item: " + "24089" + " clock: " + clock + " value: " + value);
		}
		/*
		String hostid = zabbix3.getHostid("127.0.0.1");
		ArrayList<LinkedHashMap<String,String>> itemListArray =  zabbix3.getItems(hostid);
		for(LinkedHashMap<String,String>itemList : itemListArray){
			String itemid = itemList.get("itemid");
			String name = itemList.get("name");
			String key_ = itemList.get("key_");
			log.info("found itemid: " + itemid + " name: " + name + " key_: " + key_);
			String time_from = Long.toString((System.currentTimeMillis()/1000L)-6000 );
			String time_till = Long.toString((System.currentTimeMillis()/1000L));
			ArrayList<LinkedHashMap<String,String>> historyListArray = zabbix3.getHistory(itemid,  time_from, time_till );
			for(LinkedHashMap<String,String>historyList : historyListArray){
				String clock = historyList.get("clock");
				String value = historyList.get("value");
				log.info("found item: " + name + " clock: " + clock + " value: " + value);
			}
			//log.info("Length of historyListArray: " + historyListArray.size());
			//log.info("System.currentTimeMillis()/1000L: " + time_from);
			System.exit(0);
		}*/
		
		//zabbix3.testInfo = new TestInfo();
		//String hostname = zabbix3.testInfo.connectionInfo.hostname;
		
		
		
	}
}
