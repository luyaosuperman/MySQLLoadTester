package com.MysqlLoadTest.ZabbixIntegration;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.MysqlLoadTest.Utilities.ConfigLoader;
import com.MysqlLoadTest.Utilities.LoadFromConfig;
import com.MysqlLoadTest.Utilities.TestInfo;
import com.googlecode.jsonrpc4j.JsonRpcHttpClient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Zabbix3{

	private static Logger log = LogManager.getLogger(Zabbix3.class); 
	
	private TestInfo testInfo;
	
	@LoadFromConfig
	private String zabbixServerHostname;
	
	@LoadFromConfig
	private String username = "admin";
	
	@LoadFromConfig
	private String password = "zabbix";
	
	private String authToken=null;
	
	@LoadFromConfig
	private String url = "http://192.168.1.137/zabbix/api_jsonrpc.php";
	
	
	Map<String,Object> additionalJsonContent;
	
	
	private JsonRpcHttpClient client;
	
	Zabbix3(){
		//ConfigLoader.loadFromConfig(this);
		try {
			this.client = new JsonRpcHttpClient(new URL(this.url));
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void setTestInfo(TestInfo testInfo){
		this.testInfo = testInfo;
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
		log.info("Token returned: " + this.authToken);
		this.additionalJsonContent.replace("auth", this.authToken);
		this.client.setAdditionalJsonContent(this.additionalJsonContent);
	}
	
	private void getHost(String hostname){
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
				
				for(Entry<String,String> entry: hostId.entrySet()){
					String hostid = entry.getValue();
					log.info("hostid: "+ hostid);
				}
				
				log.info("hostIdList.toString()" + hostIdList.toString());
			}
			
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
	}
	
	private void getItems(){
		
		String method = "item.get";
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
		zabbix3.getHost("127.0.0.1");
		
		//zabbix3.testInfo = new TestInfo();
		//String hostname = zabbix3.testInfo.connectionInfo.hostname;
		
		
		
	}
}
