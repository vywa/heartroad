package com.hykj.entity;

import java.io.Serializable;
import java.util.ArrayList;

public class Subject implements Serializable {
	private String title;
	private String content;
	private String author;
	private int replyNum;
	private int subjectId;
	private boolean isLiked;
	private boolean isCollection;
	private int likeCount;
	private String publishTime;
	private int subjectType;
	private String locInfo;
	private double longitude;
	private double latitude;
	private String soundUrl;
	private String videoUrl;
	private String fileUrl;
	private String authorPhotoImgUrl;
	private boolean isDoctor;
	private int viewCount;
	private int authorUserId;
	private ArrayList<String> imgUrls = new ArrayList<String>();
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public int getReplyNum() {
		return replyNum;
	}
	public void setReplyNum(int replyNum) {
		this.replyNum = replyNum;
	}
	public int getSubjectId() {
		return subjectId;
	}
	public void setSubjectId(int subjectId) {
		this.subjectId = subjectId;
	}
	public boolean isLiked() {
		return isLiked;
	}
	public void setLiked(boolean isLiked) {
		this.isLiked = isLiked;
	}
	public boolean isCollection() {
		return isCollection;
	}
	public void setCollection(boolean isCollection) {
		this.isCollection = isCollection;
	}
	public int getLikeCount() {
		return likeCount;
	}
	public void setLikeCount(int likeCount) {
		this.likeCount = likeCount;
	}
	public String getPublishTime() {
		return publishTime;
	}
	public void setPublishTime(String publishTime) {
		this.publishTime = publishTime;
	}
	public int getSubjectType() {
		return subjectType;
	}
	public void setSubjectType(int subjectType) {
		this.subjectType = subjectType;
	}
	public String getLocInfo() {
		return locInfo;
	}
	public void setLocInfo(String locInfo) {
		this.locInfo = locInfo;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public String getSoundUrl() {
		return soundUrl;
	}
	public void setSoundUrl(String soundUrl) {
		this.soundUrl = soundUrl;
	}
	public String getVideoUrl() {
		return videoUrl;
	}
	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}
	public String getFileUrl() {
		return fileUrl;
	}
	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}
	public String getAuthorPhotoImgUrl() {
		return authorPhotoImgUrl;
	}
	public void setAuthorPhotoImgUrl(String authorPhotoImgUrl) {
		this.authorPhotoImgUrl = authorPhotoImgUrl;
	}
	public boolean isDoctor() {
		return isDoctor;
	}
	public void setDoctor(boolean isDoctor) {
		this.isDoctor = isDoctor;
	}
	public int getViewCount() {
		return viewCount;
	}
	public void setViewCount(int viewCount) {
		this.viewCount = viewCount;
	}
	public int getAuthorUserId() {
		return authorUserId;
	}
	public void setAuthorUserId(int authorUserId) {
		this.authorUserId = authorUserId;
	}
	public ArrayList<String> getImgUrls() {
		return imgUrls;
	}
	public void addImgUrls(String imgUrls) {
		this.imgUrls.add(imgUrls);
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((author == null) ? 0 : author.hashCode());
		result = prime * result + ((authorPhotoImgUrl == null) ? 0 : authorPhotoImgUrl.hashCode());
		result = prime * result + authorUserId;
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result + ((fileUrl == null) ? 0 : fileUrl.hashCode());
		result = prime * result + ((imgUrls == null) ? 0 : imgUrls.hashCode());
		result = prime * result + (isDoctor ? 1231 : 1237);
		long temp;
		temp = Double.doubleToLongBits(latitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((locInfo == null) ? 0 : locInfo.hashCode());
		temp = Double.doubleToLongBits(longitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((publishTime == null) ? 0 : publishTime.hashCode());
		result = prime * result + ((soundUrl == null) ? 0 : soundUrl.hashCode());
		result = prime * result + subjectId;
		result = prime * result + subjectType;
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((videoUrl == null) ? 0 : videoUrl.hashCode());
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
		Subject other = (Subject) obj;
		if (author == null) {
			if (other.author != null)
				return false;
		} else if (!author.equals(other.author))
			return false;
		if (authorPhotoImgUrl == null) {
			if (other.authorPhotoImgUrl != null)
				return false;
		} else if (!authorPhotoImgUrl.equals(other.authorPhotoImgUrl))
			return false;
		if (authorUserId != other.authorUserId)
			return false;
		if (content == null) {
			if (other.content != null)
				return false;
		} else if (!content.equals(other.content))
			return false;
		if (fileUrl == null) {
			if (other.fileUrl != null)
				return false;
		} else if (!fileUrl.equals(other.fileUrl))
			return false;
		if (imgUrls == null) {
			if (other.imgUrls != null)
				return false;
		} else if (!imgUrls.equals(other.imgUrls))
			return false;
		if (isDoctor != other.isDoctor)
			return false;
		if (Double.doubleToLongBits(latitude) != Double.doubleToLongBits(other.latitude))
			return false;
		if (locInfo == null) {
			if (other.locInfo != null)
				return false;
		} else if (!locInfo.equals(other.locInfo))
			return false;
		if (Double.doubleToLongBits(longitude) != Double.doubleToLongBits(other.longitude))
			return false;
		if (publishTime == null) {
			if (other.publishTime != null)
				return false;
		} else if (!publishTime.equals(other.publishTime))
			return false;
		if (soundUrl == null) {
			if (other.soundUrl != null)
				return false;
		} else if (!soundUrl.equals(other.soundUrl))
			return false;
		if (subjectId != other.subjectId)
			return false;
		if (subjectType != other.subjectType)
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (videoUrl == null) {
			if (other.videoUrl != null)
				return false;
		} else if (!videoUrl.equals(other.videoUrl))
			return false;
		return true;
	}

	public Subject() {
		super();
	}
}
