package com.MysqlLoadTest.Utilities;

import java.util.Date;

public class TestInfo {

	private int testType; //1: Insert
	private int runCount;
	private int totalThreads;
	private String comment;
	
	private int testId = -1;
	private Date testDate;

	
	public TestInfo(Object... arguments ){
		this.setTestType((int) arguments[0]);
		this.setTotalThreads((int) arguments[1]);
		this.setRunCount((int) arguments[2]);
		this.setComment((String) arguments[3]);
		
		
	}


	public int getRunCount() {
		return runCount;
	}


	public void setRunCount(int runCount) {
		this.runCount = runCount;
	}


	public int getTotalThreads() {
		return totalThreads;
	}


	public void setTotalThreads(int totalThreads) {
		this.totalThreads = totalThreads;
	}


	public int getTestType() {
		return testType;
	}


	public void setTestType(int testType) {
		this.testType = testType;
	}


	public int getTestId() {
		return testId;
	}


	public void setTestId(int testId) {
		this.testId = testId;
	}


	public String getComment() {
		return comment;
	}


	public void setComment(String comment) {
		this.comment = comment;
	}


	public Date getTestDate() {
		return testDate;
	}


	public void setTestDate(Date testDate) {
		this.testDate = testDate;
	}
}
