package com.hykj.entity;

import java.io.Serializable;

/**
 * @author 作者 : zhaoyu
 * @version 创建时间：2016年3月31日 上午11:49:49
 * 类说明：用药提醒
 */
public class Remind implements Serializable {
	
		private String type;
		private String contents;
		private String time;
		private String repeat;
		private String isLocked;
	
		/**
		 * @param type
		 * @param contents
		 * @param time
		 * @param repeat
		 * @param isLocked
		 */
		public Remind(String type, String contents, String time, String repeat,
				String isLocked) {
			super();
			this.type = type;
			this.contents = contents;
			this.time = time;
			this.repeat = repeat;
			this.isLocked = isLocked;
		}
		/**
		 * @return the isLocked
		 */
		public String getIsLocked() {
			return isLocked;
		}
		/**
		 * @param isLocked the isLocked to set
		 */
		public void setIsLocked(String isLocked) {
			this.isLocked = isLocked;
		}
		public String getType() {
			return type;
		}
		/**
		 * @param type the type to set
		 */
		public void setType(String type) {
			this.type = type;
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
		 * @return the repeat
		 */
		public String getRepeat() {
			return repeat;
		}
		/**
		 * @param repeat the repeat to set
		 */
		public void setRepeat(String repeat) {
			this.repeat = repeat;
		}
		/**
		 * @param type
		 * @param contents
		 * @param time
		 * @param repeat
		 */
		public Remind(String type, String contents, String time, String repeat) {
			super();
			this.type = type;
			this.contents = contents;
			this.time = time;
			this.repeat = repeat;
		}
		/**
		 * 
		 */
		public Remind() {
			super();
		}
		
}
