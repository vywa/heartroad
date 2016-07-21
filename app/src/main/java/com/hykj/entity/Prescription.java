package com.hykj.entity;

import java.io.Serializable;

/**
 * @author 作者 : zhaoyu
 * @version 创建时间：2016年4月23日 下午2:26:20
 * 类说明：处方
 */
public class Prescription implements Serializable {
	private String medicalname;//药品名称
	private String frequency;//服用频率
	private String method;//服用方式
	private int meal;//每顿用量
	private int all;//总量
	/**
	 * @return the medicalname
	 */
	public String getMedicalname() {
		return medicalname;
	}
	/**
	 * @param medicalname the medicalname to set
	 */
	public void setMedicalname(String medicalname) {
		this.medicalname = medicalname;
	}
	/**
	 * @return the frequency
	 */
	public String getFrequency() {
		return frequency;
	}
	/**
	 * @param frequency the frequency to set
	 */
	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}
	/**
	 * @return the method
	 */
	public String getMethod() {
		return method;
	}
	/**
	 * @param method the method to set
	 */
	public void setMethod(String method) {
		this.method = method;
	}
	/**
	 * @return the meal
	 */
	public int getMeal() {
		return meal;
	}
	/**
	 * @param meal the meal to set
	 */
	public void setMeal(int meal) {
		this.meal = meal;
	}
	/**
	 * @return the all
	 */
	public int getAll() {
		return all;
	}
	/**
	 * @param all the all to set
	 */
	public void setAll(int all) {
		this.all = all;
	}
	/**
	 * @param medicalname
	 * @param frequency
	 * @param method
	 * @param meal
	 * @param all
	 */
	public Prescription(String medicalname, String frequency, String method,
			int meal, int all) {
		super();
		this.medicalname = medicalname;
		this.frequency = frequency;
		this.method = method;
		this.meal = meal;
		this.all = all;
	}
	/**
	 * 
	 */
	public Prescription() {
		super();
	}
	
}
