package com.MysqlLoadTest.Web;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.MysqlLoadTest.ExecutionUnit.Singleton.TestController;
import com.MysqlLoadTest.ObjectLibrary.TestList;
import com.MysqlLoadTest.ObjectLibrary.TestProgress;
import com.MysqlLoadTest.ObjectLibrary.TestResult;
import com.MysqlLoadTest.Utilities.ConfigLoader;
import com.MysqlLoadTest.Utilities.ConnectionInfo;
import com.MysqlLoadTest.Utilities.ConnectionManager;
import com.MysqlLoadTest.Utilities.LoadFromConfig;
import com.MysqlLoadTest.Utilities.TestInfo;

@RestController
public class TestRESTController {
	
	@LoadFromConfig
	private ConnectionInfo connectionInfo;
	
	
	private TestController testController;
	//private TestInfo testInfo;
	
	@Autowired
    public void setTestController(TestController testController) {
        this.testController = testController;
        //this.testController.start();
    }
	
	/*@Autowired
    public void setTestInfo(TestInfo testInfo) {
        this.testController.testInfo = testInfo;
    }*/
	
	public TestRESTController(){
		ConfigLoader.loadFromConfig(this);
	}

    @RequestMapping(value="/get_progress",method=RequestMethod.GET)
    public TestInfo get_progress(){
    	//return WebBridge.testInfo;
    	return this.testController.testInfo;
    }
    
    @RequestMapping(value="/get_testList",method=RequestMethod.GET)
    public TestList getTestList(){
    	Connection connect = ConnectionManager.getConnection(this.connectionInfo);
    	TestList testList = new TestList();
    	
		PreparedStatement preparedStatement = null;
		ResultSet rs;
		
		try {
			preparedStatement = connect.prepareStatement("select "+
														"info.id, "+
														"info.timestamp, "+
														"info.threads, "+
														"info.runCount, "+
														"info.rowCount, "+
														"info.`comment`, "+
														"info.tableName, "+
														"info.createTableSql, "+
														"info.insertPct, "+
														"info.selectPct, "+
														"info.updatePct, "+
														"info.initDataAmount "+
														"from testInfo info; ");
			String[] properties = {
					"id",
					"timestamp",
					"threads",
					"runCount",
					"rowCount",
					"comment",
					"tableName",
					//"createTableSql",
					"insertPct",
					"selectPct",
					"updatePct",
					"initDataAmount"};
			testList.properties = properties;  	
			
			rs = preparedStatement.executeQuery();
			while (rs.next()){
				
				HashMap<String,String> testDetailList = new HashMap<String,String>();
				for(String property: properties){
					String value = rs.getString(property);
					testDetailList.put(property, value);
				}

				testList.testList.add(testDetailList);
			}	
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	
		return testList;
    	
    }
    
    @RequestMapping(value="/get_data",method=RequestMethod.GET)
    public TestResult getData(@RequestParam(value="testId", required=true) int[] testIdArray){
    	
    	//System.out.println("/get_data invoked, with testId: " + testId);
    	/*for (int testId: testIdArray){
    		System.out.println("testId: " + testId);
    	}*/
    	int testId = testIdArray[0];
    	TestResult testResult = new TestResult();

    	Connection connect = ConnectionManager.getConnection(this.connectionInfo);
    	
		PreparedStatement preparedStatement = null;
		ResultSet rs;
		try {
			preparedStatement = connect.prepareStatement("select "+ 
					"a.systemNanoTime, "+
					"@rowcount + a.insertCount as rowCount, "+
					"a.runCount - @lasttotalrunCount as intervalrunCount,"+
					"a.insertCount - @lasttotalinsertCount as intervalinsertCount, "+
					"a.updateCount - @lasttotalupdateCount as intervalupdateCount, "+
					"a.selectCount - @lasttotalselectCount as intervalselectCount, "+
					"@lasttotalrunCount := a.runCount, "+
					"@lasttotalinsertCount := a.insertCount, "+
					"@lasttotalupdateCount := a.updateCount, "+
					"@lasttotalselectCount := a.selectCount "+
					"from testreport.testRuntimeInfo a, "+
					"(select "+
					"@lasttotalrunCount := 0, "+
					"@lasttotalinsertCount := 0, "+
					"@lasttotalupdateCount := 0, "+
					"@lasttotalselectCount := 0, "+
					"@rowcount := initdataamount from testInfo where id = ? "+
					") SQLVars "+
					"where testid = ? ");
			preparedStatement.setInt(1, testId);
			preparedStatement.setInt(2, testId);
			
			//String[] columns = {"systemNanoTime","rowCount","intervalrunCount","intervalinsertCount","intervalupdateCount","intervalselectCount"};
			String[] columns = {"rowCount","intervalrunCount","intervalinsertCount","intervalupdateCount","intervalselectCount"};
	    	testResult.columns = columns;  	
			
			rs = preparedStatement.executeQuery();
			while (rs.next()){
				
				long[] pointInfo = new long[columns.length];
				
				for (int i=0;i<columns.length;i++){
					String column = columns[i];
					pointInfo[i]=rs.getLong(column);
				}
				testResult.dataPoint.add(pointInfo);		
			}	
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	return testResult;
    }
    
    @RequestMapping(value="/get_zabbix",method=RequestMethod.GET)
    public Object getZabbix(@RequestParam(value="testId", required=true) int[] testIdArray){
    	int testId = testIdArray[0];
    	
    	return null;
    	
    }

}