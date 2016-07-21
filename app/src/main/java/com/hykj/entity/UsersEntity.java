package com.hykj.entity;

import java.io.Serializable;

/**
 * @author 作者：赵宇
 * @version 1.0 创建时间：2015年11月2日 下午6:39:55 类说明
 */
public class UsersEntity implements Serializable {
	private String id;
	private String username;
	private String password;
	private String phoneNumber;
	private String email;
	private String QQ;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getQQ() {
		return QQ;
	}
	public void setQQ(String qQ) {
		QQ = qQ;
	}
	
}
