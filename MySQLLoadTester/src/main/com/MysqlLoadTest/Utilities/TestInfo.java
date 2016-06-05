package com.MysqlLoadTest.Utilities;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class TestInfo implements Serializable{

	//private int testType; //1: Insert
	private long runCount; //total run count, not per thread.
	private long rowCount;
	private int totalThreads;
	private String comment;
	
	private int testId = -1;
	private Date testDate;
	
	/////////////////////////////////////////
	//Extended Info
	//randomnized table names
	//Structure of Table(s) -> Create table statement?
	//create table code
	//select/insert/update percentage
	//init data amount -> How to achieve fast?
	
	private String tableName = null;
	private String createTableSQL = null;
	private int insertPct=0;
	private int selectPct=0;
	private int updatePct=0;
	
	private long initDataAmount = 0;
	
	///////////////
	//Test status
	public static final int PREPARING = 0;
	public static final int RUNNING = 1;
	
	public int testStatus = PREPARING;
	private double testProgress = 0;
	
	private int maxId = 0;
	
	
	//private HashMap tableColMap;
	public LinkedHashMap<String,Tuple<String,Integer>> tableColMap = new LinkedHashMap<String,Tuple<String,Integer>>();
	//name, class, length
	
	//need to record down three different charts.
	
	//TODO: move getTestInfo here

	
	public TestInfo(//int testType,
			int totalThreads, long runCount, long rowCount, String comment, 
			String tableName, String createTableSql, int insertPct, int selectPct, int updatePct, int initDataAmount   ){
		//this.setTestType(testType);
		this.setTotalThreads(totalThreads);
		this.setRunCount(runCount);
		this.setRowCount(rowCount);
		this.setComment(comment);
		
		this.setTableName(tableName);
		this.setCreateTableSQL(createTableSql);
		this.setInsertPct(insertPct);
		this.setSelectPct(selectPct);
		this.setUpdatePct(updatePct);
		this.setInitDataAmount(initDataAmount);
		
		assert insertPct + selectPct + updatePct == 100;
		
	}
	

	public static TestInfo getTestInfo(int testId)
	{
		Connection connect = ConnectionManager.getConnection("testreport");
		
		TestInfo testInfo=null;
	 	PreparedStatement preparedStatement = null;
		ResultSet rs;
		try {
			preparedStatement = connect.prepareStatement("select "
					+ "id,timestamp,threads,runCount,rowCount,comment, "
					+ "tableName,createTableSql,insertPct,selectPct,updatePct,initDataAmount "
					+ "from testinfo where id = ?;");
			preparedStatement.setInt(1, testId);
			rs = preparedStatement.executeQuery();
			if (rs.next()){
				assert testId == rs.getInt("id");
				//int testType = rs.getInt("testType");
				Date testDate = rs.getDate("timestamp");
				int threads = rs.getInt("threads");
				long runCount = rs.getLong("runCount");
				long rowCount = rs.getLong("rowCount");
				String comment = rs.getString("comment");
				
				String tableName = rs.getString("tableName");
				String createTableSql = rs.getString("createTableSql");
				int insertPct = rs.getInt("insertPct");
				int selectPct = rs.getInt("selectPct");
				int updatePct = rs.getInt("updatePct");
				int initDataAmount = rs.getInt("initDataAmount");
				testInfo = new TestInfo(threads,runCount,rowCount,comment,
						tableName,createTableSql,insertPct,selectPct,updatePct,initDataAmount);
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
	
    @Override public TestInfo clone() {
        try {
            final TestInfo result = (TestInfo) super.clone();
            // copy fields that need to be copied here!
            return result;
        } catch (final CloneNotSupportedException ex) {
            throw new AssertionError();
        }
    }



	public long getRunCount() {		return runCount;	}
	public void setRunCount(long runCount) {		this.runCount = runCount;	}
	public int getTotalThreads() {		return totalThreads;	}
	public void setTotalThreads(int totalThreads) {		this.totalThreads = totalThreads;	}
	//public int getTestType() {		return testType;	}
	//public void setTestType(int testType) {		this.testType = testType;	}
	public int getTestId() {		return testId;	}
	public void setTestId(int testId) {		this.testId = testId;	}
	public String getComment() {		return comment;	}
	public void setComment(String comment) {		this.comment = comment;	}
	public Date getTestDate() {		return testDate;	}
	public void setTestDate(Date testDate) {		this.testDate = testDate;	}
	public String getCreateTableSQL() {		return createTableSQL;	}
	public void setCreateTableSQL(String createTableSQL) {		this.createTableSQL = createTableSQL;	}
	public String getTableName() {		return tableName;	}
	public void setTableName(String tableName) {		this.tableName = tableName;	}
	public int getInsertPct() {		return insertPct;	}
	public void setInsertPct(int insertPct) {		this.insertPct = insertPct;	}
	public int getSelectPct() {		return selectPct;	}
	public void setSelectPct(int selectPct) {		this.selectPct = selectPct;	}
	public int getUpdatePct() {		return updatePct;	}
	public void setUpdatePct(int updatePct) {		this.updatePct = updatePct;	}
	public long getInitDataAmount() {		return initDataAmount;	}
	public void setInitDataAmount(long initDataAmount) {		this.initDataAmount = initDataAmount;	}
	public int getMaxId() {return maxId;}
	public void setMaxId(int maxId) {this.maxId = maxId;}
	public long getRowCount() {return rowCount;}
	public void setRowCount(long rowCount) {this.rowCount = rowCount;}


	public double getTestProgress() {
		return testProgress;
	}


	public void setTestProgress(double testProgress) {
		this.testProgress = testProgress;
	}
}
