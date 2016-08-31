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
	@GeneratedData(stringLength=10) private String username;

	HUser(){
		System.out.println("HUser init");
		GenerateData.generateData(this);
	}
	
	
}
