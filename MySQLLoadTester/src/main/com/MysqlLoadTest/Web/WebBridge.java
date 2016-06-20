package com.MysqlLoadTest.Web;

import com.MysqlLoadTest.ExecutionUnit.TestController;
import com.MysqlLoadTest.Utilities.TestInfo;

public class WebBridge {

	public static TestController controller = new TestController();
	public static TestInfo testInfo = null;
	
	static{
		controller.start();
	}
}
