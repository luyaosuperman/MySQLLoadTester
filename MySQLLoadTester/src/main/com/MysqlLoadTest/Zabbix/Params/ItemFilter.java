package com.MysqlLoadTest.Zabbix.Params;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
{
    "jsonrpc": "2.0",
    "method": "item.get",
    "params": {
        "output": ["name","key_"],
        "hostids": "10084",
        "sortfield": "name",
        "search": {
            "key_": "mysql"
        }
    },

    "auth": "9f456cab345922583f5c901248b4193d",
    "id": 1
} */
public class ItemFilter {
	public ArrayList<String> output = new ArrayList<String>();
	public String hostids;
	public String sortfield;
	public Map<String,String> search = new HashMap<String,String>();
}
