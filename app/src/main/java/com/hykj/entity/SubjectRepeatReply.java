package com.hykj.entity;

public class SubjectRepeatReply {
	private String repeat;
	private String repeatTo;
	private String repeatContent;
	public String getRepeat() {
		return repeat;
	}
	public void setRepeat(String repeat) {
		this.repeat = repeat;
	}
	public String getRepeatTo() {
		return repeatTo;
	}
	public void setRepeatTo(String repeatTo) {
		this.repeatTo = repeatTo;
	}
	public String getRepeatContent() {
		return repeatContent;
	}
	public void setRepeatContent(String repeatContent) {
		this.repeatContent = repeatContent;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((repeat == null) ? 0 : repeat.hashCode());
		result = prime * result + ((repeatContent == null) ? 0 : repeatContent.hashCode());
		result = prime * result + ((repeatTo == null) ? 0 : repeatTo.hashCode());
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
		SubjectRepeatReply other = (SubjectRepeatReply) obj;
		if (repeat == null) {
			if (other.repeat != null)
				return false;
		} else if (!repeat.equals(other.repeat))
			return false;
		if (repeatContent == null) {
			if (other.repeatContent != null)
				return false;
		} else if (!repeatContent.equals(other.repeatContent))
			return false;
		if (repeatTo == null) {
			if (other.repeatTo != null)
				return false;
		} else if (!repeatTo.equals(other.repeatTo))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "SubjectRepeatReply [repeat=" + repeat + ", repeatTo=" + repeatTo + ", repeatContent=" + repeatContent + "]";
	}
	public SubjectRepeatReply(String repeat, String repeatTo, String repeatContent) {
		super();
		this.repeat = repeat;
		this.repeatTo = repeatTo;
		this.repeatContent = repeatContent;
	}
	public SubjectRepeatReply() {
	}
	
}
