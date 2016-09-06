
package com.MysqlLoadTest.ExecutionUnit.Singleton;

import java.sql.Connection;

import com.MysqlLoadTest.Utilities.ConfigLoader;
import com.MysqlLoadTest.Utilities.ConnectionInfo;
import com.MysqlLoadTest.Utilities.ConnectionManager;
import com.MysqlLoadTest.Utilities.LoadFromConfig;
import com.MysqlLoadTest.Utilities.TestInfo;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MaxIdCatcher extends Thread{
	private static Logger log = LogManager.getLogger(MaxIdCatcher.class); 

	private Connection connect;
	private TestInfo testInfo;
	
	private PreparedStatement getMaxIdStatement;
	private boolean stop = false;
	
	//@LoadFromConfig
	//private ConnectionInfo connectionInfo;
	
	public MaxIdCatcher(TestInfo testInfo){
		ConfigLoader.loadFromConfig(this);
		this.testInfo = testInfo;
		this.connect = ConnectionManager.getConnection(this.testInfo.connectionInfo);
		
		try {
			this.getMaxIdStatement = this.connect.prepareStatement("select max(id) from "+this.testInfo.getTableName());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.catchMaxId();
		
	}
	
	private void catchMaxId() {
		try {
			ResultSet rs = this.getMaxIdStatement.executeQuery();
			if (rs.next()) {
				this.testInfo.setMaxId(rs.getInt(1));
				//log.info("current max(id): " + this.testInfo.getMaxId()); 
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void run(){
		while (this.stop != true){ 
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.catchMaxId();
		}
		
		
	}
	
	public void stopMaxIdCatcher(){
		this.stop = true;
	}
	
}
