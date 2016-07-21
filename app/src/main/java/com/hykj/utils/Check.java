package com.hykj.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 作者：赵宇
 * @version 1.0 创建时间：2015年10月16日 下午4:12:03 类说明：验证用户名和密码是否符合要求
 */
public class Check {
	/*
	 * 验证手机号是否符合要求
	 */
	public static boolean isMobile(String mobiles) {
		Pattern p = Pattern
				.compile("^(13[0-9]|15[012356789]|17[0678]|18[0-9]|14[57])[0-9]{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

	/*
	 * 验证邮箱是否符合要求
	 */
	public static boolean isMail(String email) {
		String emailPattern = "[a-zA-Z0-9][a-zA-Z0-9._-]{2,16}[a-zA-Z0-9]@[a-zA-Z0-9]+.[a-zA-Z0-9]+";
		boolean result = Pattern.matches(emailPattern, email);
		return result;
	}

	/*
	 * 验证用户名是否符合要求（3-15任意字符）
	 */
	public static boolean CheckUsername(String username) {
		if ("".equals(username) && null == username) {
			return false;
		}

		Pattern mPattern = Pattern.compile("[a-zA-Z]\\w{2,15}");
		Matcher m = mPattern.matcher(username);
		return m.matches();
	}
	/*
	 * 验证密码是否符合要求（6-15任意字符）
	 */
	public static boolean CheckPassword(String password) {
		if ("".equals(password) && null == password) {
			return false;
		}

		Pattern mPattern = Pattern.compile("^[0-9_a-zA-Z]{6,20}$");
		Matcher m = mPattern.matcher(password);
		return m.matches();
	}
	/*
	 * 验证工号是否符合要求
	 */
	public static boolean isWorkNo(String no) {
		Pattern p = Pattern
				.compile("\\d{3,6}");
		Matcher m = p.matcher(no);
		return m.matches();
	}

}
