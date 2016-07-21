package com.hykj.entity;

import java.io.Serializable;

/**
 * @author 作者 : zhaoyu
 * @version 创建时间：2016年4月13日 下午4:13:29 类说明：我的运动
 */
public class MySport implements Serializable {
	private int id;
	/**
	 * @param id
	 * @param type
	 * @param describe
	 * @param date
	 * @param time
	 */
	public MySport(int id, String type, String describe, String date,
			String time) {
		super();
		this.id = id;
		this.type = type;
		this.describe = describe;
		this.date = date;
		this.time = time;
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
	private String time;

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
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
	 * @param describe
	 *            the describe to set
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
	 * @param date
	 *            the date to set
	 */
	public void setDate(String date) {
		this.date = date;
	}

	/**
	 * @return the time
	 */
	public String getTime() {
		return time;
	}

	/**
	 * @param time
	 *            the time to set
	 */
	public void setTime(String time) {
		this.time = time;
	}


	/**
	 * 
	 */
	public MySport() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MySport [type=" + type + ", describe=" + describe + ", date="
				+ date + ", time=" + time + "]";
	}

}
