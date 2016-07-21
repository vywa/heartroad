package com.hykj.utils;

import android.app.Activity;

import android.app.Dialog;
import android.view.View;

/**
 * @author 作者：赵宇
 * @version 1.0 创建时间：2015年10月15日 下午2:34:07 类说明 :实例化组件( 省强转)
 */
public class FindView {
	View mView;//通过View findView
	Dialog mDialog;//通过Dialog findView
	Activity mActivity;// 通过Activity findView

	public FindView(View mView) {
		super();
		this.mView = mView;
	}

	public FindView(Dialog mDialog) {
		super();
		this.mDialog = mDialog;
	}

	public FindView(Activity mActivity) {
		super();
		this.mActivity = mActivity;
	}

	public <T extends View> T findView(int resid) {
		return (T) mView.findViewById(resid);

	}
	public <T extends View> T findDigView(int resid){
		return (T) mDialog.findViewById(resid);
	}
	public <T extends View> T findActivityView(int resid){
		return (T) mActivity.findViewById(resid);
	}

}
