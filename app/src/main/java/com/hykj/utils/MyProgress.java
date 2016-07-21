package com.hykj.utils;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

public class MyProgress {

	private static Dialog progressDialog;

	public static void show(Context context, String title, String content) {
		
		progressDialog = ProgressDialog.show(context, title, content, true, false);
		progressDialog.setCancelable(false);
		progressDialog.setCanceledOnTouchOutside(false);
	}
	public static void show(Context context,int resid){
		progressDialog=new Dialog(context);
		progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		progressDialog.setContentView(resid);
		progressDialog.setCancelable(false);
		progressDialog.setCanceledOnTouchOutside(false);
		Window dialogWindow = progressDialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.CENTER);
		WindowManager m = dialogWindow.getWindowManager();
		Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
		lp.height = (int) (d.getHeight() * 0.2); // 高度设置为屏幕的0.6
		lp.width = (int) (d.getWidth() * 0.8); // 宽度设置为屏幕的0.65
		dialogWindow.setAttributes(lp);
		progressDialog.show();
	}
	
	public static void show(Context context) {
		progressDialog = ProgressDialog.show(context, "提醒", "请稍候......", true, false);
		if (progressDialog.isShowing()) {
			return;
		}
		progressDialog.setCancelable(false);
		progressDialog.setCanceledOnTouchOutside(false);
	}
	

	public static void dismiss() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}
}
