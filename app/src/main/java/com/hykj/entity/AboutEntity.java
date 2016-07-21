package com.hykj.entity;
/**
 * @author 作者 : zhaoyu
 * @version 创建时间：2016年1月22日 下午3:41:46
 * 类说明
 */
public class AboutEntity {
	private String title;
	private String contents;
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
	 * @param title
	 * @param contents
	 */
	public AboutEntity(String title, String contents) {
		super();
		this.title = title;
		this.contents = contents;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "QuestionEntity [title=" + title + ", contents=" + contents
				+ "]";
	}
	/**
	 * 
	 */
	public AboutEntity() {
		super();
	}
}
