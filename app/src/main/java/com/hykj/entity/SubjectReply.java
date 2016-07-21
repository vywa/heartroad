package com.hykj.entity;

import java.util.ArrayList;

public class SubjectReply {
	private String replyContent;
	private String replyAuthor;
	private String replyAuthorPhotoUrl;
	private String replyTime;
	private String replySoundUrl;
	private String replyLocInfo;
	private String replyLongitude;
	private String replyLatitude;
	private int replyId;
	private boolean isDoctor;
	private ArrayList<String> replyImg = new ArrayList<String>();
	private ArrayList<SubjectRepeatReply> repeatReply = new ArrayList<SubjectRepeatReply>();
	
	
	public boolean isDoctor() {
		return isDoctor;
	}
	public void setDoctor(boolean isDoctor) {
		this.isDoctor = isDoctor;
	}
	public String getReplyContent() {
		return replyContent;
	}
	public void setReplyContent(String replyContent) {
		this.replyContent = replyContent;
	}
	public String getReplyAuthor() {
		return replyAuthor;
	}
	public void setReplyAuthor(String replyAuthor) {
		this.replyAuthor = replyAuthor;
	}
	public String getReplyAuthorPhotoUrl() {
		return replyAuthorPhotoUrl;
	}
	public void setReplyAuthorPhotoUrl(String replyAuthorPhotoUrl) {
		this.replyAuthorPhotoUrl = replyAuthorPhotoUrl;
	}
	public String getReplyTime() {
		return replyTime;
	}
	public void setReplyTime(String replyTime) {
		this.replyTime = replyTime;
	}
	public String getReplySoundUrl() {
		return replySoundUrl;
	}
	public void setReplySoundUrl(String replySoundUrl) {
		this.replySoundUrl = replySoundUrl;
	}
	public String getReplyLocInfo() {
		return replyLocInfo;
	}
	public void setReplyLocInfo(String replyLocInfo) {
		this.replyLocInfo = replyLocInfo;
	}
	public String getReplyLongitude() {
		return replyLongitude;
	}
	public void setReplyLongitude(String replyLongitude) {
		this.replyLongitude = replyLongitude;
	}
	public String getReplyLatitude() {
		return replyLatitude;
	}
	public void setReplyLatitude(String replyLatitude) {
		this.replyLatitude = replyLatitude;
	}
	public int getReplyId() {
		return replyId;
	}
	public void setReplyId(int replyId) {
		this.replyId = replyId;
	}
	public void addReplyImg(String url){
		replyImg.add(url);
	}
	public ArrayList<String> getReplyImg(){
		return replyImg;
	}
	public void addRepeatReply(SubjectRepeatReply repeatReply){
		this.repeatReply.add(repeatReply);
	}
	public ArrayList<SubjectRepeatReply> getRepeatReply(){
		return repeatReply;
	}
}
