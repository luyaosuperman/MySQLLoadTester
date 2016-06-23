package com.MysqlLoadTest.ZabbixIntegration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.MysqlLoadTest.Utilities.ConfigLoader;
import com.MysqlLoadTest.Utilities.LoadFromConfig;
import com.MysqlLoadTest.Utilities.TestInfo;

@SpringBootApplication
public class Zabbix3 implements CommandLineRunner {

	private static Logger log = LogManager.getLogger(Zabbix3.class); 
	
	private TestInfo testInfo;
	
	@LoadFromConfig
	private String zabbixServerHostname;
	
	@LoadFromConfig
	private String username;
	
	@LoadFromConfig
	private String password;
	
	private String uri = "http://192.168.1.137/zabbix/api_jsonrpc.php";
	
	
	Zabbix3(){
		//ConfigLoader.loadFromConfig(this);
	}
	
	public void setTestInfo(TestInfo testInfo){
		this.testInfo = testInfo;
	}
	
	private void Auth(){
		
	}
	
	private void sendRequest(){
		
	}
	
    @Override
    public void run(String... args) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
    	
    	MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
    	//map.add("companyId", "companyId);
    	//map.add("password", password);  
    	
    	HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.valueOf("json-rpc"));   
    	
    	HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
    	
    	List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
    	messageConverters.add(new MappingJackson2HttpMessageConverter());    
    	messageConverters.add(new FormHttpMessageConverter());
    	restTemplate.setMessageConverters(messageConverters);

    	

        ZabbixInfo zabbixInfo = (ZabbixInfo) restTemplate.postForObject(this.uri,request , ZabbixInfo.class);
        log.info(zabbixInfo.toString());
    }
	
	public static void main(String args[]){
		SpringApplication.run(Zabbix3.class);
	}
}
