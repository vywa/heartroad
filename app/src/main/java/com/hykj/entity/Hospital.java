package com.hykj.entity;

import java.io.Serializable;

public class Hospital implements Serializable{
	private String address;
	private String hospitalName;
	private String id;
	private double Lng;//经
	private double Lat;//维
	
	
	public Hospital(String address, String hospitalName, String id, double lng, double lat) {
		super();
		this.address = address;
		this.hospitalName = hospitalName;
		this.id = id;
		Lng = lng;
		Lat = lat;
	}
	public double getLng() {
		return Lng;
	}
	public void setLng(double lng) {
		Lng = lng;
	}
	public double getLat() {
		return Lat;
	}
	public void setLat(double lat) {
		Lat = lat;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getHospitalName() {
		return hospitalName;
	}
	public void setHospitalName(String hospitalName) {
		this.hospitalName = hospitalName;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	@Override
	public String toString() {
		return "Hospital [address=" + address + ", hospitalName=" + hospitalName + ", id=" + id + "]";
	}
	

}
