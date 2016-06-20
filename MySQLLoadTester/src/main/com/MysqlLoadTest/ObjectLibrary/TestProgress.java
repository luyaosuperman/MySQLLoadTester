package com.MysqlLoadTest.ObjectLibrary;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName(value = "TestProgress")  
public class TestProgress {

	@JsonProperty
	private final int testId;
	@JsonProperty
	private final float testProgress;
	
	public TestProgress(int testId, float testProgress){
		this.testId = testId;
		this.testProgress = testProgress;
	}
	
}
