package com.hykj.utils;

import android.os.Environment;

/**
 * @author 作者：赵宇
 * @version 1.0 创建时间：2015年10月28日 下午2:53:03 类说明：检查设备是否有SDCard
 */
public class HasSDCard {
	public static boolean hasSDCard() {
		String state = Environment.getExternalStorageState();
		return state.equals(Environment.MEDIA_MOUNTED);
	}
}