package com.MysqlLoadTest.Web;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.MysqlLoadTest.ObjectLibrary.TestList;
import com.MysqlLoadTest.ObjectLibrary.TestProgress;
import com.MysqlLoadTest.ObjectLibrary.TestResult;
import com.MysqlLoadTest.Utilities.ConnectionManager;

@RestController
public class RESTController {

    /*private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/validate")
    public Greeting greeting(@RequestParam(value="name", defaultValue="Hello, World!") String name) {
        return new Greeting(counter.incrementAndGet(),
                            String.format(template, name));
    }*/
    
	@CrossOrigin(origins = "http://localhost:1841")
    @RequestMapping("/validate")
    public UserInfo greeting(@RequestParam(value="username", defaultValue="user1") String username,
    		@RequestParam(value="password", defaultValue="pass1") String password) {
    	
    	System.out.println("==== /validate ====");
        return new UserInfo(username,password);
        //http://localhost:8080/validate?username=luyao&password=123
    }

    @RequestMapping(value="/get_progress",method=RequestMethod.POST)
    public TestProgress get_progress(@RequestParam(value="runCount") int runCount,
    							   @RequestParam(value="totalThreads") int totalThreads
    		){
    	
    	System.out.println("runCount:" + runCount);
    	System.out.println("totalThreads:" + totalThreads);
    	
    	return new TestProgress(1,50);
    	
    }
    
    @RequestMapping(value="/get_testList",method=RequestMethod.GET)
    public TestList getTestList(){
    	Connection connect = ConnectionManager.getConnection("testReport");
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
														"from testinfo info; ");
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
    	for (int testId: testIdArray){
    		System.out.println("testId: " + testId);
    	}
    	int testId = testIdArray[0];
    	TestResult testResult = new TestResult();

    	Connection connect = ConnectionManager.getConnection("testReport");
    	
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
					"from testreport.testruntimeinfo a, "+
					"(select "+
					"@lasttotalrunCount := 0, "+
					"@lasttotalinsertCount := 0, "+
					"@lasttotalupdateCount := 0, "+
					"@lasttotalselectCount := 0, "+
					"@rowcount := initdataamount from testinfo where id = ? "+
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

}