package com.MysqlLoadTest.ZabbixIntegration;

import java.lang.reflect.Field;

public class ZabbixInfo {

	private String jsonrpc;
	private String method;
	private int id;
	private String auth;
	private Object[] params;
	public String getJsonrpc() {
		return jsonrpc;
	}
	public void setJsonrpc(String jsonrpc) {
		this.jsonrpc = jsonrpc;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getAuth() {
		return auth;
	}
	public void setAuth(String auth) {
		this.auth = auth;
	}
	public Object[] getParams() {
		return params;
	}
	public void setParams(Object[] params) {
		this.params = params;
	}
	
	public String toString(){
		
		String result = "";
		for (Field field: ZabbixInfo.class.getDeclaredFields()){
			result += field.getName() + ": ";
			field.setAccessible(true);
			try {
				result += field.get(this) + " ";
			} catch (IllegalArgumentException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return result;
	}
}
