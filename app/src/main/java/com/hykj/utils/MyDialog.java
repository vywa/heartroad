package com.hykj.utils;

import com.hykj.R;

import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

/**
 * @author 作者 : zhaoyu
 * @version 创建时间：2016年4月9日 下午1:50:43 类说明
 */
public class MyDialog {
	private static Dialog dialog;

	public static void showDialog(Context context, int desid) {
		dialog = new Dialog(context);
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(desid);
		Window dialogWindow = dialog.getWindow();
		dialogWindow.setBackgroundDrawableResource(R.drawable.transparent);
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.CENTER);
		WindowManager m = dialogWindow.getWindowManager();
		Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
		lp.height = (int) (d.getHeight() * 0.2); // 高度设置为屏幕的0.6
		lp.width = (int) (d.getWidth() * 0.5); // 宽度设置为屏幕的0.65
		dialogWindow.setAttributes(lp);
		dialog.show();
	}
	
	public static void cancelDialog(){
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
	}
}
