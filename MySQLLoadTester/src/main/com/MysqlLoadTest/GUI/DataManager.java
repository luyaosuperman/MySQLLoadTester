package com.MysqlLoadTest.GUI;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.MysqlLoadTest.Utilities.ConnectionManager;
import com.MysqlLoadTest.Utilities.TestInfo;



public class DataManager {
	
	public static final int CHARTBYTIME = 0;
	public static final int CHARTBYROW = 1;
	
	public static int chartType = CHARTBYTIME;
	
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
				testInfoList.add(TestInfo.getTestInfo(testId));
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
				long [] point = new long[2];
				if      (chartType == CHARTBYTIME){point[0] = rs.getLong(1);}
				else if (chartType == CHARTBYROW) {point[0] = rs.getLong(2);}
				point[1] = rs.getLong(3);
				result.add(point);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	
	

}
