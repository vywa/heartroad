package com.hykj.entity;

import java.io.Serializable;

public class Doctor implements Serializable{
	private String workNum;
	private String name;
	private String hospitalName;
	private int userId;
	private String iconUrl;
	private String sex;
	private String age;
	private String prifile;
	private boolean isFriend;
	
	public boolean isFriend() {
		return isFriend;
	}
	public void setFriend(boolean isFriend) {
		this.isFriend = isFriend;
	}
	public String getWorkNum() {
		return workNum;
	}
	public void setWorkNum(String workNum) {
		this.workNum = workNum;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getHospitalName() {
		return hospitalName;
	}
	public void setHospitalName(String hospitalName) {
		this.hospitalName = hospitalName;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getIconUrl() {
		return iconUrl;
	}
	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}
	public String getPrifile() {
		return prifile;
	}
	public void setPrifile(String prifile) {
		this.prifile = prifile;
	}
	
	public Doctor() {
		super();
	}
}
