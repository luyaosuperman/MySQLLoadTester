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
	
	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE},targetEntity = HUser.class, fetch=FetchType.EAGER)
	@JoinColumn(name = "USER_ID")
	//@Column(name="USER_ID")
	@Access(AccessType.PROPERTY)
	private HUser hUser;
	
	@GeneratedData(stringLength=200) private String userData0;
	@GeneratedData(stringLength=200) private String userData1;
	@GeneratedData(stringLength=200) private String userData2;
	@GeneratedData(stringLength=200) private String userData3;
	@GeneratedData(stringLength=200) private String userData4;
	@GeneratedData(stringLength=200) private String userData5;
	@GeneratedData(stringLength=200) private String userData6;
	@GeneratedData(stringLength=200) private String userData7;
	@GeneratedData(stringLength=200) private String userData8;
	@GeneratedData(stringLength=200) private String userData9;
	
	
	HUserRecord(){ this.updateValue();	}
	
	public void updateValue(){
		GenerateData.generateData(this);
	}
	
	public HUser gethUser() {return hUser;	}
	public void sethUser(HUser hUser) {this.hUser = hUser;}
	
}
