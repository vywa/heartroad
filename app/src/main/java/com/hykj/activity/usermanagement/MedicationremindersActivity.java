package com.hykj.activity.usermanagement;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.hykj.R;
import com.hykj.activity.BaseActivity;
import com.hykj.adapter.RemindAdapter;
import com.hykj.broadcast.AlarmBroadcastReceiver;
import com.hykj.db.DataBaseHelper;
import com.hykj.entity.Remind;
import com.hykj.utils.TimeUtil;

/**
 * @author 作者 : zhaoyu
 * @version 创建时间：2016年3月28日 上午11:12:13 类说明：用药提醒
 */
public class MedicationremindersActivity extends BaseActivity {
	private ImageView mImg_back, mImg_set;
	private List<Remind> data;
	private ListView mLv;
	private RemindAdapter adapter;

	@Override
	public void init() {
		setContentView(R.layout.activity_medicationreminders);
		initViews();
	}

	private void initViews() {
		mImg_back = (ImageView) findViewById(R.id.medicationreminders_imgv_back);
		mImg_set = (ImageView) findViewById(R.id.medicationreminders_imgv_set);
		mImg_back.setOnClickListener(this);
		mImg_set.setOnClickListener(this);
		mLv = (ListView) findViewById(R.id.medication_lv);
		data = (List<Remind>) getIntent().getSerializableExtra("data");
		if (data != null) {
			adapter = new RemindAdapter(this, data);
			mLv.setAdapter(adapter);
			
		}
		mLv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {
				final Dialog dialog = new Dialog(
						MedicationremindersActivity.this);
				dialog.setCancelable(false);
				dialog.setCanceledOnTouchOutside(false);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setContentView(R.layout.dialog_del_remind);
				Window dialogWindow = dialog.getWindow();
				WindowManager.LayoutParams lp = dialogWindow.getAttributes();
				dialogWindow.setGravity(Gravity.CENTER);
				WindowManager m = dialogWindow.getWindowManager();
				Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
				lp.height = (int) (d.getHeight() * 0.3); // 高度设置为屏幕的0.6
				lp.width = (int) (d.getWidth() * 0.8); // 宽度设置为屏幕的0.65
				dialogWindow.setAttributes(lp);
				Button btn_true = (Button) dialog
						.findViewById(R.id.dialog_btn_remind_true);
				Button btn_cancel = (Button) dialog
						.findViewById(R.id.dialog_btn_remind_cancel);
				dialog.show();
				btn_cancel.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.cancel();
					}
				});
				btn_true.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						delDataFromDb(data.get(position).getContents());
						data.remove(position);
						adapter.notifyDataSetChanged();
						dialog.cancel();
						Intent intent = new Intent(MedicationremindersActivity.this,
								AlarmBroadcastReceiver.class);
						PendingIntent sender = PendingIntent.getBroadcast(MedicationremindersActivity.this, 0,
								intent, 0);
						AlarmManager am = (AlarmManager) MedicationremindersActivity.this
								.getSystemService(Context.ALARM_SERVICE);
						am.cancel(sender);
					}
				});
			}
		});
	}

	DataBaseHelper db;

	public void delDataFromDb(final String str) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (db == null) {
					db = new DataBaseHelper(MedicationremindersActivity.this);
				}
				db.getWritableDatabase().delete("remind", "contents=?",
						new String[] { str });
				db.close();
			}
		}).start();
	}

	String time;

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		Bundle b = arg2.getBundleExtra("remind");
		if (!b.isEmpty()) {
//			Log.wtf("aaa", b.toString() + "-------b");
			String type = b.getString("type");
			String contents = b.getString("contents");
			time = b.getString("time");
			String repeat = b.getString("repeat");
			String islocked = b.getString("islocked");
			data.add( new Remind(type, contents, time, repeat, islocked));
//			Log.wtf("aaa", data.toString() + "------data");
			
		}
		adapter.notifyDataSetChanged();
	}

	@Override
	public void click(View v) {
		switch (v.getId()) {
		case R.id.medicationreminders_imgv_back:
			finish();
			break;
		case R.id.medicationreminders_imgv_set:
			Intent intent = new Intent(this, MedicationsetActivity.class);
			startActivityForResult(intent, 0);
			break;
		}
	}

}
