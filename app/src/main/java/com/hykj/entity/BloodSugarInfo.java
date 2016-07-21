package com.hykj.entity;

import java.io.Serializable;

public class BloodSugarInfo implements Serializable{
	private double bsValue ;
	private long measureTime ;
	private int measureType;
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(bsValue);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + (int) (measureTime ^ (measureTime >>> 32));
		result = prime * result + measureType;
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BloodSugarInfo other = (BloodSugarInfo) obj;
		if (Double.doubleToLongBits(bsValue) != Double.doubleToLongBits(other.bsValue))
			return false;
		if (measureTime != other.measureTime)
			return false;
		return measureType == other.measureType;
	}
	@Override
	public String toString() {
		return "BloodSugarInfo [bsValue=" + bsValue + ", measureTime=" + measureTime + ", measureType=" + measureType + "]";
	}
	public int getMeasureType() {
		return measureType;
	}
	public void setMeasureType(int measureType) {
		this.measureType = measureType;
	}
	public double getBsValue() {
		return bsValue;
	}
	public void setBsValue(double bsValue) {
		this.bsValue = bsValue;
	}
	public long getMeasureTime() {
		return measureTime;
	}
	public void setMeasureTime(long measureTime) {
		this.measureTime = measureTime;
	}
	public BloodSugarInfo() {
		super();
	}

	public BloodSugarInfo(long measureTime,double bsValue , int measureType) {
		super();
		this.bsValue = bsValue;
		this.measureTime = measureTime;
		this.measureType = measureType;
	}
	
}
