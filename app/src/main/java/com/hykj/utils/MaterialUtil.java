package com.hykj.utils;

import com.gc.materialdesign.widgets.ProgressDialog;
import com.gc.materialdesign.widgets.SnackBar;
import com.hykj.activity.messure.AddBloodSugarActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class MaterialUtil {
	private static SnackBar snackBar;
	private static boolean isShowing = false;
	/*public static void showSnackBar(final Activity activity, String description, final boolean hasFinish){
		
		if (snackBar != null && snackBar.getOwnerActivity() == activity) {
			
		}else{
			snackBar = new SnackBar(activity, description, "确定", new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (snackBar != null) {
						snackBar.dismiss();
					}
				}
			});
			snackBar.setDismissTimer(5000);
			snackBar.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
					isShowing = false;
					if (hasFinish) {
						activity.finish();
					}
				}
			});
		}
		
		if (!isShowing) {
			snackBar.show();
			isShowing = true;
		}
		
	}*/
/*	public static void showSnackBar(final Activity activity, String description, final boolean hasFinish){
		Toast.makeText(activity, description, 0).show();
		if (hasFinish) {
			activity.finish();
		}
	}*/
	private static ProgressDialog dialog = null;
	
	public static void showProgressDialog(Context context){
		dialog = new ProgressDialog(context, "  加载中......", 0x88ff8c00);
        dialog.setCancelable(true);
        dialog.show();
	}
	public static void showProgressDialog(Context context,String str){
		dialog = new ProgressDialog(context, str, 0x88ff8c00);
		dialog.setCancelable(false);
		dialog.show();
	}
	
	public static void progressDialogDismiss(){
		if (dialog == null) {
			return ;
		}else{
			dialog.dismiss();
			dialog = null;
		}
	}
}
