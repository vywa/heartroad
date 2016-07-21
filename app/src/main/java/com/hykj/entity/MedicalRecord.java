package com.hykj.entity;

import java.io.Serializable;
import java.util.ArrayList;

public class MedicalRecord implements Serializable {
	/**
	 * 
	 */
	public MedicalRecord() {
		super();
	}
	private String id;
	private String type;
	private String recordTime;
	private ArrayList<String> imgList = new ArrayList<String>();
	private String content;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getRecordTime() {
		return recordTime;
	}
	public void setRecordTime(String recordTime) {
		this.recordTime = recordTime;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public void setImg(String url){
		imgList.add(url);
	}
	public ArrayList<String> getImgList(){
		return imgList;
	}
	/**
	 * @param id
	 * @param type
	 * @param recordTime
	 * @param imgList
	 * @param content
	 */
	public MedicalRecord( String type, String recordTime,
			ArrayList<String> imgList, String content) {
		super();
		this.type = type;
		this.recordTime = recordTime;
		this.imgList = imgList;
		this.content = content;
	}
	
}
