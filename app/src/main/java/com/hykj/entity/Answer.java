package com.hykj.entity;
/**
 * @author 作者 : zhaoyu
 * @version 创建时间：2016年3月10日 下午3:45:54
 * 类说明：答案
 */
public class Answer {
	private String answerId;//答案id
	private String answer_content;//答案主体
	private int ans_state;//答案是否被解答
	/**
	 * @return the answerId
	 */
	public String getAnswerId() {
		return answerId;
	}
	/**
	 * @param answerId the answerId to set
	 */
	public void setAnswerId(String answerId) {
		this.answerId = answerId;
	}
	/**
	 * @return the answer_content
	 */
	public String getAnswer_content() {
		return answer_content;
	}
	/**
	 * @param answer_content the answer_content to set
	 */
	public void setAnswer_content(String answer_content) {
		this.answer_content = answer_content;
	}
	/**
	 * @return the ans_state
	 */
	public int getAns_state() {
		return ans_state;
	}
	/**
	 * @param ans_state the ans_state to set
	 */
	public void setAns_state(int ans_state) {
		this.ans_state = ans_state;
	}
	
}
