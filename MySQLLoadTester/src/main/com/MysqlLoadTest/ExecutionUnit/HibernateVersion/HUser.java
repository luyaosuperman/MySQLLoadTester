package com.MysqlLoadTest.ExecutionUnit.HibernateVersion;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.MysqlLoadTest.Utilities.GenerateData;
import com.MysqlLoadTest.Utilities.GeneratedData;

@Entity
public class HUser {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY) 
	@Column(name = "HUSER_ID")
	private long id;
	
	@GeneratedData(stringLength=100) private String username;
	@GeneratedData(stringLength=100) private String userdata1;
	@GeneratedData(stringLength=100) private String userdata2;
	@GeneratedData(stringLength=100) private String userdata3;
	@GeneratedData(stringLength=100) private String userdata4;
	@GeneratedData(stringLength=100) private String userdata5;
	@GeneratedData(stringLength=100) private String userdata6;
	@GeneratedData(stringLength=100) private String userdata7;
	@GeneratedData(stringLength=100) private String userdata8;
	@GeneratedData(stringLength=100) private String userdata9;
	@GeneratedData(stringLength=100) private String userdata10;

	HUser(){
		//System.out.println("HUser init");
		GenerateData.generateData(this);
	}
	
	
}
