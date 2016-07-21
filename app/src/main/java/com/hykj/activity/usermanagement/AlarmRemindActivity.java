package com.hykj.activity.usermanagement;

import android.app.Dialog;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.hykj.R;
import com.hykj.activity.BaseActivity;

/**
 * @author 作者 : zhaoyu
 * @version 创建时间：2016年4月12日 下午3:38:41 类说明
 */
public class AlarmRemindActivity extends BaseActivity {
	Dialog dialog;

	@Override
	public void init() {

		setContentView(R.layout.activity_alarmremind);
		popDialog();
	}

	private void popDialog() {
		String type = getIntent().getType();
		String contents = getIntent().getAction();

		dialog = new Dialog(this);
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_alarm_remind);
		Window dialogWindow = dialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.CENTER);
		WindowManager m = dialogWindow.getWindowManager();
		Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
		lp.height = (int) (d.getHeight() * 0.4); // 高度设置为屏幕的0.6
		lp.width = (int) (d.getWidth() * 0.8); // 宽度设置为屏幕的0.65
		dialogWindow.setAttributes(lp);
		Button btn_true = (Button) dialog
				.findViewById(R.id.dialog_alarm_remind_true);
		btn_true.setOnClickListener(this);
		TextView tv_title = (TextView) dialog
				.findViewById(R.id.dialog_alarm_remind_type);
		tv_title.setText(type);
		TextView tv_contents = (TextView) dialog
				.findViewById(R.id.dialog_alarm_remind_contents);
		tv_contents.setText(contents);
		dialog.show();

	}

	@Override
	public void click(View v) {
		switch (v.getId()) {
		case R.id.dialog_alarm_remind_true:
			dialog.cancel();
			finish();
			break;

		default:
			break;
		}
	}

}
