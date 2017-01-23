package com.MysqlLoadTest.ExecutionUnit.HibernateVersion;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.MysqlLoadTest.Interfaces.StatusItem;
import com.MysqlLoadTest.Interfaces.TestStatusManager;

@Entity
@Table(name="HTestStatus")
public class HTestStatus {
	
	private static Logger log = LogManager.getLogger(TestStatusManager.class); 
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY) 
	@Column(name = "Record_ID")
	private int id;
	
	protected long testId;
	
	@StatusItem protected long  intervalInsertCount;
	@StatusItem protected long  intervalUpdateCount;
	@StatusItem protected long  intervalSelectCount;
	
	@Transient
	final public static List<String> statusItemList = new ArrayList<String>();
	
	protected long currentTime;
	
	static{
		for (Field field: HTestStatus.class.getDeclaredFields()){
			if (field.getAnnotation(StatusItem.class) != null){
				statusItemList.add(field.getName());
				//log.info("statusItem added: " + field.getName());
			}
		}
	}
	
	public HTestStatus()
	{

	}
}
