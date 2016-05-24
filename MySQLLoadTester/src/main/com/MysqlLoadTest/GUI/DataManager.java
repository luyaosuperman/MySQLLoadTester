package com.MysqlLoadTest.GUI;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import com.MysqlLoadTest.Utilities.ConnectionManager;
import com.MysqlLoadTest.Utilities.TestInfo;



public class DataManager {
	
	public static int runTest(TestInfo testInfo){
		int testId = 0;
   	 	try {
			 testId = com.MysqlLoadTest.ExecutionUnit.Application.runTest(testInfo);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
   	 	return testId;
	}
	
	public static TestInfo getTestInfo(int testId)
	{
		Connection connect = ConnectionManager.getConnection("testreport");
		
		TestInfo testInfo=null;
     	PreparedStatement preparedStatement = null;
		ResultSet rs;
		try {
			preparedStatement = connect.prepareStatement("select "
					+ "id,timestamp,testType,threads,runCount,comment "
					+ "from testinfo where id = ?;");
			preparedStatement.setInt(1, testId);
			rs = preparedStatement.executeQuery();
			if (rs.next()){
				assert testId == rs.getInt("id");
				int testType = rs.getInt("testType");
				Date testDate = rs.getDate("timestamp");
				int threads = rs.getInt("threads");
				int runCount = rs.getInt("runCount");
				String comment = rs.getString("comment");
				testInfo = new TestInfo(testType,threads,runCount,comment);
				testInfo.setTestId(testId);
				testInfo.setTestDate(testDate);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assert testInfo != null;
		return testInfo; 
	}
	
	public static ArrayList<TestInfo> getAllTestInfo(){
		Connection connect = ConnectionManager.getConnection("testreport");
		ArrayList<TestInfo> testInfoList = new ArrayList<TestInfo>();
		
     	PreparedStatement preparedStatement = null;
		ResultSet rs;
		try {
			preparedStatement = connect.prepareStatement("select "
					+ "id "
					+ "from testinfo;");
			rs = preparedStatement.executeQuery();
			while (rs.next()){
				int testId = rs.getInt(1);
				testInfoList.add(getTestInfo(testId));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return testInfoList;
	}
	
	public static ArrayList<long[]> getTestData(int testId){
		Connection connect = ConnectionManager.getConnection("testreport");
		
		ArrayList<long[]> result = new ArrayList<long[]>();
		
		PreparedStatement preparedStatement = null;
		ResultSet rs;
		try {
			preparedStatement = connect.prepareStatement("select "+
							"a.systemNanoTime / 1000000, "+
							"a.totalExecutionCount, "+
							"a.totalExecutionCount - @lasttotalExecutionCount as intervalExecutionCount, "+
							"@lasttotalExecutionCount := a.totalExecutionCount "+
							"from testreport.testruntimeinfo a, "+
							"(select @lasttotalExecutionCount := 0) SQLVars "+
							"where testid = ?");
			preparedStatement.setInt(1, testId);
			
			rs = preparedStatement.executeQuery();
			while (rs.next()){
				long[] point ={rs.getLong(1), rs.getLong(3)};
				result.add(point);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	
	

}
