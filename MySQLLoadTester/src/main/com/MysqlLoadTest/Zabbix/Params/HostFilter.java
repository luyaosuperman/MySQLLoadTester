package com.MysqlLoadTest.Zabbix.Params;

import java.util.ArrayList;

public class HostFilter {
	
	
	public class Filter{
		public ArrayList<String> ip = new ArrayList<String>();

		public ArrayList<String> getIp() {
			return ip;
		}

		public void setHost(ArrayList<String> ip) {
			this.ip = ip;
		}
	}
	
	private String output;
	public Filter filter;
	public String getOutput() {
		return output;
	}
	public void setOutput(String output) {
		this.output = output;
	}
	public Filter getFilter() {
		return filter;
	}
	public void setFilter(Filter filter) {
		this.filter = filter;
	}
	
	public HostFilter(){
		this.filter = new Filter();
	}
	
}
