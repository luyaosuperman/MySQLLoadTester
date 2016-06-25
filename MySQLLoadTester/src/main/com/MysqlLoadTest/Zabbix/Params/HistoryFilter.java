package com.MysqlLoadTest.Zabbix.Params;


/*{
    "jsonrpc": "2.0",
    "method": "history.get",
    "params": {
        "output":"extend",
        "history": 0,
        "itemids": "23695",
        "sortfield": "clock",
        "sortorder": "ASC",
        "time_from": "1466695523"

    },
    "auth": "a91c4a976705d7a8398444ce5d819a66",
    "id": 1
} */
 
public class HistoryFilter {

    public String output = "extend";
    public String history = "0";
    public String itemids; 
    public String sortfield = "clock";
    public String sortorder = "ASC";
    public String time_from;
    public String time_till;
}
