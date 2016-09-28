package com.journaldev.spring.model;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

//@JsonIgnoreProperties({ "permanent", "role" })
//ignore any "extra" properties from JSON (ones for which there is no counterpart in POJO)
@JsonIgnoreProperties(ignoreUnknown=true)
public class EmployeeCopy implements Serializable{

	private static final long serialVersionUID = -7788619177798333712L;
	
	@JsonProperty("id") 
	private int identifier;
	@JsonProperty("name") 
	private String firstName;
	private Date createdDate;
	
	public int getIdentifier() {
		return identifier;
	}
	public void setIdentifier(int identifier) {
		this.identifier = identifier;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	//@JsonSerialize(using=DateSerializer.class)
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	
	
}
