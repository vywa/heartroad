package com.hykj.entity;

import java.io.Serializable;



/**
 * @author 作者 : zhaoyu
 * @version 创建时间：2016年1月12日 上午11:59:51
 * 类说明：推送消息实体类
 */
public class PushDataEntity implements Serializable {
	private String url;
	private String contents;
	private String title;
	private String time;
	private int point;
	private int count;
	private String content_url;
	private boolean collected;//是否被收藏
	
	/**
	 * @return the collected
	 */
	public boolean isCollected() {
		return collected;
	}


	/**
	 * @param collected the collected to set
	 */
	public void setCollected(boolean collected) {
		this.collected = collected;
	}


	/**
	 * @param url
	 * @param contents
	 * @param title
	 * @param time
	 * @param point
	 * @param content_url
	 * @param iscollected
	 */
	public PushDataEntity(String url, String contents, String title,
			String time, int point, String content_url, boolean collected) {
		super();
		this.url = url;
		this.contents = contents;
		this.title = title;
		this.time = time;
		this.point = point;
		this.content_url = content_url;
		this.collected = collected;
	}

	
	/**
	 * @return the content_url
	 */
	public String getContent_url() {
		return content_url;
	}

	/**
	 * @param content_url the content_url to set
	 */
	public void setContent_url(String content_url) {
		this.content_url = content_url;
	}

	/**
	 * @param url
	 * @param contents
	 * @param title
	 * @param time
	 * @param point
	 * @param count
	 * @param content_url
	 */
	
	public PushDataEntity(String url, String contents, String title,
			String time, int point, int count, String content_url) {
		super();
		this.url = url;
		this.contents = contents;
		this.title = title;
		this.time = time;
		this.point = point;
		this.count = count;
		this.content_url = content_url;
	}

	/**
	 * @param url
	 * @param contents
	 * @param title
	 * @param time
	 * @param content_url
	 */
	public PushDataEntity(String url, String contents, String title,
			String time, String content_url) {
		super();
		this.url = url;
		this.contents = contents;
		this.title = title;
		this.time = time;
		this.content_url = content_url;
	}

	/**
	 * @param url
	 * @param contents
	 * @param title
	 * @param time
	 * @param point
	 * @param content_url
	 */
	public PushDataEntity(String url, String contents, String title,
			String time, int point, String content_url) {
		super();
		this.url = url;
		this.contents = contents;
		this.title = title;
		this.time = time;
		this.point = point;
		this.content_url = content_url;
	}

	/**
	 * @param url
	 * @param contents
	 * @param title
	 * @param time
	 * @param point
	 * @param count
	 */
	public PushDataEntity(String url, String contents, String title,
			String time, int point, int count) {
		super();
		this.url = url;
		this.contents = contents;
		this.title = title;
		this.time = time;
		this.point = point;
		this.count = count;
	}

	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}

	/**
	 * @param count the count to set
	 */
	public void setCount(int count) {
		this.count = count;
	}

	/**
	 * @param url
	 * @param contents
	 * @param title
	 * @param time
	 * @param point
	 */
	public PushDataEntity(String url, String contents, String title,
			String time, int point) {
		super();
		this.url = url;
		this.contents = contents;
		this.title = title;
		this.time = time;
		this.point = point;
	}
	
	/**
	 * @return the point
	 */
	public int getPoint() {
		return point;
	}

	/**
	 * @param point the point to set
	 */
	public void setPoint(int point) {
		this.point = point;
	}

	/**
	 * @param url
	 * @param contents
	 * @param title
	 * @param time
	 */
	public PushDataEntity(String url, String contents, String title, String time) {
		super();
		this.url = url;
		this.contents = contents;
		this.title = title;
		this.time = time;
	}
	
	/**
	 * @return the time
	 */
	public String getTime() {
		return time;
	}
	/**
	 * @param time the time to set
	 */
	public void setTime(String time) {
		this.time = time;
	}
	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	/**
	 * @return the contents
	 */
	public String getContents() {
		return contents;
	}
	/**
	 * @param contents the contents to set
	 */
	public void setContents(String contents) {
		this.contents = contents;
	}
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @param url
	 * @param contents
	 * @param title
	 */
	public PushDataEntity(String url, String contents, String title) {
		super();
		this.url = url;
		this.contents = contents;
		this.title = title;
	}
	/**
	 * 
	 */
	public PushDataEntity() {
		super();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PushDataEntity [url=" + url + ", contents=" + contents
				+ ", title=" + title + ", time=" + time + ", point=" + point
				+ "]";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((contents == null) ? 0 : contents.hashCode());
		result = prime * result + count;
		result = prime * result + point;
		result = prime * result + ((time == null) ? 0 : time.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PushDataEntity other = (PushDataEntity) obj;
		if (contents == null) {
			if (other.contents != null)
				return false;
		} else if (!contents.equals(other.contents))
			return false;
		if (count != other.count)
			return false;
		if (point != other.point)
			return false;
		if (time == null) {
			if (other.time != null)
				return false;
		} else if (!time.equals(other.time))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}
	
	
}
