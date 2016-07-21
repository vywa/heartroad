package com.hykj.entity;
/**
 * @author 作者 : zhaoyu
 * @version 创建时间：2016年4月8日 下午8:05:05
 * 类说明
 */
public class TargetBloodSugar {
	String afterBreakfast;
	String afterLunch;
	String afterSupper;
	String beforeBreakfast;
	String beforeLunch;
	String beforeSleep;
	String beforeSupper;
	String random;
	String zero;
	/**
	 * @return the afterBreakfast
	 */
	public String getAfterBreakfast() {
		return afterBreakfast;
	}
	/**
	 * @param afterBreakfast the afterBreakfast to set
	 */
	public void setAfterBreakfast(String afterBreakfast) {
		this.afterBreakfast = afterBreakfast;
	}
	/**
	 * @return the afterLunch
	 */
	public String getAfterLunch() {
		return afterLunch;
	}
	/**
	 * @param afterLunch the afterLunch to set
	 */
	public void setAfterLunch(String afterLunch) {
		this.afterLunch = afterLunch;
	}
	/**
	 * @return the afterSupper
	 */
	public String getAfterSupper() {
		return afterSupper;
	}
	/**
	 * @param afterSupper the afterSupper to set
	 */
	public void setAfterSupper(String afterSupper) {
		this.afterSupper = afterSupper;
	}
	/**
	 * @return the beforeBreakfast
	 */
	public String getBeforeBreakfast() {
		return beforeBreakfast;
	}
	/**
	 * @param beforeBreakfast the beforeBreakfast to set
	 */
	public void setBeforeBreakfast(String beforeBreakfast) {
		this.beforeBreakfast = beforeBreakfast;
	}
	/**
	 * @return the beforeLunch
	 */
	public String getBeforeLunch() {
		return beforeLunch;
	}
	/**
	 * @param beforeLunch the beforeLunch to set
	 */
	public void setBeforeLunch(String beforeLunch) {
		this.beforeLunch = beforeLunch;
	}
	/**
	 * @return the beforeSleep
	 */
	public String getBeforeSleep() {
		return beforeSleep;
	}
	/**
	 * @param beforeSleep the beforeSleep to set
	 */
	public void setBeforeSleep(String beforeSleep) {
		this.beforeSleep = beforeSleep;
	}
	/**
	 * @return the beforeSupper
	 */
	public String getBeforeSupper() {
		return beforeSupper;
	}
	/**
	 * @param beforeSupper the beforeSupper to set
	 */
	public void setBeforeSupper(String beforeSupper) {
		this.beforeSupper = beforeSupper;
	}
	/**
	 * @return the random
	 */
	public String getRandom() {
		return random;
	}
	/**
	 * @param random the random to set
	 */
	public void setRandom(String random) {
		this.random = random;
	}
	/**
	 * @return the zero
	 */
	public String getZero() {
		return zero;
	}
	/**
	 * @param zero the zero to set
	 */
	public void setZero(String zero) {
		this.zero = zero;
	}
	/**
	 * @param afterBreakfast
	 * @param afterLunch
	 * @param afterSupper
	 * @param beforeBreakfast
	 * @param beforeLunch
	 * @param beforeSleep
	 * @param beforeSupper
	 * @param random
	 * @param zero
	 */
	public TargetBloodSugar(String afterBreakfast, String afterLunch,
			String afterSupper, String beforeBreakfast, String beforeLunch,
			String beforeSleep, String beforeSupper, String random, String zero) {
		super();
		this.afterBreakfast = afterBreakfast;
		this.afterLunch = afterLunch;
		this.afterSupper = afterSupper;
		this.beforeBreakfast = beforeBreakfast;
		this.beforeLunch = beforeLunch;
		this.beforeSleep = beforeSleep;
		this.beforeSupper = beforeSupper;
		this.random = random;
		this.zero = zero;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TargetBloodSugar [afterBreakfast=" + afterBreakfast
				+ ", afterLunch=" + afterLunch + ", afterSupper=" + afterSupper
				+ ", beforeBreakfast=" + beforeBreakfast + ", beforeLunch="
				+ beforeLunch + ", beforeSleep=" + beforeSleep
				+ ", beforeSupper=" + beforeSupper + ", random=" + random
				+ ", zero=" + zero + "]";
	}
	
}
