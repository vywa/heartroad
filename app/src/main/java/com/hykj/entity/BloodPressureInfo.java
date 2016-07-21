package com.hykj.entity;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

public class BloodPressureInfo implements Serializable{
	private int  highBP;
	private int  lowBP;
	private long measureTime;
	private int heartRate;
	
	
	public int getHeartRate() {
		return heartRate;
	}
	public void setHeartRate(int heartRate) {
		this.heartRate = heartRate;
	}
	public int getHighBP() {
		return highBP;
	}
	public void setHighBP(int highBP) {
		this.highBP = highBP;
	}
	public int getLowBP() {
		return lowBP;
	}
	public void setLowBP(int lowBP) {
		this.lowBP = lowBP;
	}
	public long getMeasureTime() {
		return measureTime;
	}
	public void setMeasureTime(long measureTime) {
		this.measureTime = measureTime;
	}
	public BloodPressureInfo() {
		super();
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + heartRate;
		result = prime * result + highBP;
		result = prime * result + lowBP;
		result = prime * result + (int) (measureTime ^ (measureTime >>> 32));
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
		BloodPressureInfo other = (BloodPressureInfo) obj;
		if (heartRate != other.heartRate)
			return false;
		if (highBP != other.highBP)
			return false;
		if (lowBP != other.lowBP)
			return false;
		return measureTime == other.measureTime;
	}
	public BloodPressureInfo(long measureTime, int highBP, int lowBP, int heartRate) {
		super();
		this.highBP = highBP;
		this.lowBP = lowBP;
		this.measureTime = measureTime;
		this.heartRate = heartRate;
	}
	
}
