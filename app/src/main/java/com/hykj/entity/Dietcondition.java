package com.hykj.entity;

import java.io.Serializable;

/**
 * @author 作者 : zhaoyu
 * @version 创建时间：2016年4月13日 上午10:20:20
 * 类说明：饮食情况
 */
public class Dietcondition implements Serializable {
	private int id;
	/**
	 * @param id
	 * @param type
	 * @param describe
	 * @param date
	 */
	public Dietcondition(int id, String type, String describe, String date) {
		super();
		this.id = id;
		this.type = type;
		this.describe = describe;
		this.date = date;
	}
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	private String type;
	private String describe;
	private String date;
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the describe
	 */
	public String getDescribe() {
		return describe;
	}
	/**
	 * @param describe the describe to set
	 */
	public void setDescribe(String describe) {
		this.describe = describe;
	}
	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}
	/**
	 * @param date the date to set
	 */
	public void setDate(String date) {
		this.date = date;
	}
	
	/**
	 * 
	 */
	public Dietcondition() {
		super();
	}

	
}
