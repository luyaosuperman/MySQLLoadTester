package com.MysqlLoadTest.Web;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName(value = "userinfo")  
public class UserInfo {
	
	@JsonProperty
	private final String username;
	
	@JsonProperty
	private final String password;
	
	public UserInfo(String username, String password){
		this.username = username;
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}
	
}
