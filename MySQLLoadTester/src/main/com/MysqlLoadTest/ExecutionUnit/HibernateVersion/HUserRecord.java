package com.MysqlLoadTest.ExecutionUnit.HibernateVersion;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.MysqlLoadTest.Utilities.GenerateData;
import com.MysqlLoadTest.Utilities.GeneratedData;

@Entity
public class HUserRecord {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY) 
	@Column(name = "HUSER_RECORD")
	private int id;
	private HUser hUser;
	@GeneratedData(stringLength=20) private String userData;
	
	HUserRecord(){ GenerateData.generateData(this);	}
	
	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE},targetEntity = HUser.class, fetch=FetchType.EAGER)
	@JoinColumn(name = "USER_ID")
	//@Column(name="USER_ID")
	@Access(AccessType.PROPERTY)
	public HUser gethUser() {return hUser;	}
	public void sethUser(HUser hUser) {this.hUser = hUser;}
	
}
