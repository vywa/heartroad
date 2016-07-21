package com.hykj.activity.usermanagement;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.hykj.App;
import com.hykj.R;
import com.hykj.activity.BaseActivity;
import com.hykj.db.DataBaseHelper;
import com.hykj.utils.MyToast;
import com.hykj.view.spinnerwheel.WheelVerticalView;
import com.hykj.view.spinnerwheel.adapters.ArrayWheelAdapter;

/**
 * @author 作者 : zhaoyu
 * @version 创建时间：2016年3月28日 下午4:31:15 类说明：提醒设置
 */
public class MedicationsetActivity extends BaseActivity {

	private ImageView mImg_back;
	private Button mBtn_takemedicine, mBtn_bloodpressure, mBtn_bloodsugar,
			mBtn_sports;
	private Button mBtn_mon, mBtn_tues, mBtn_wed, mBtn_thur, mBtn_fri,
			mBtn_sat, mBtn_sun;
	private EditText mEdt_contents;
	private WheelVerticalView mWv_hour, mWv_min;
	private Button mBtn_true, mBtn_cancel;
	private String[] hours, minutes;

	@Override
	public void init() {
		setContentView(R.layout.activity_medicationset);
		initViews();
		initDatas();
	}

	private void initDatas() {
		hours = new String[24];
		for (int i = 0; i < 24; i++) {
			hours[i] = i + "";
		}
		mWv_hour.setVisibleItems(2);
		ArrayWheelAdapter<String> hour_adapter = new ArrayWheelAdapter<String>(
				this, hours);
		hour_adapter.setItemResource(R.layout.choose_alarm_wheel);
		hour_adapter.setItemTextResource(R.id.text_alarmwheel);
		mWv_hour.setViewAdapter(hour_adapter);
		mWv_hour.setCurrentItem(8);

		minutes = new String[60];
		for (int i = 0; i < 60; i++) {
			if (i < 10) {
				minutes[i] = "0" + i;
			} else {
				minutes[i] = i + "";
			}
		}
		mWv_hour.setVisibleItems(2);
		ArrayWheelAdapter<String> min_adapter = new ArrayWheelAdapter<String>(
				this, minutes);
		min_adapter.setItemResource(R.layout.choose_alarm_wheel);
		min_adapter.setItemTextResource(R.id.text_alarmwheel);
		mWv_min.setViewAdapter(min_adapter);
		mWv_min.setCurrentItem(10);
	}

	private void initViews() {
		mImg_back = (ImageView) findViewById(R.id.medicationset_imgv_back);
		mImg_back.setOnClickListener(this);
		mBtn_takemedicine = (Button) findViewById(R.id.medicationset_btn_takemedicine);
		mBtn_takemedicine.setOnClickListener(this);
		mBtn_bloodpressure = (Button) findViewById(R.id.medicationset_btn_bloodpressure);
		mBtn_bloodpressure.setOnClickListener(this);
		mBtn_bloodsugar = (Button) findViewById(R.id.medicationset_btn_bloodsugar);
		mBtn_bloodsugar.setOnClickListener(this);
		mBtn_sports = (Button) findViewById(R.id.medicationset_btn_sports);
		mBtn_sports.setOnClickListener(this);
		mBtn_mon = (Button) findViewById(R.id.medicationset_btn_mon);
		mBtn_mon.setOnClickListener(this);
		mBtn_tues = (Button) findViewById(R.id.medicationset_btn_tues);
		mBtn_tues.setOnClickListener(this);
		mBtn_wed = (Button) findViewById(R.id.medicationset_btn_wed);
		mBtn_wed.setOnClickListener(this);
		mBtn_thur = (Button) findViewById(R.id.medicationset_btn_thur);
		mBtn_thur.setOnClickListener(this);
		mBtn_fri = (Button) findViewById(R.id.medicationset_btn_fri);
		mBtn_fri.setOnClickListener(this);
		mBtn_sat = (Button) findViewById(R.id.medicationset_btn_sat);
		mBtn_sat.setOnClickListener(this);
		mBtn_sun = (Button) findViewById(R.id.medicationset_btn_sun);
		mBtn_sun.setOnClickListener(this);
		mEdt_contents = (EditText) findViewById(R.id.medicationset_edt_contents);
		mBtn_true = (Button) findViewById(R.id.medicationset_btn_true);
		mBtn_true.setOnClickListener(this);
		mBtn_cancel = (Button) findViewById(R.id.medicationset_btn_cancel);
		mBtn_cancel.setOnClickListener(this);
		mWv_hour = (WheelVerticalView) findViewById(R.id.medicationset_wv_hour);
		mWv_min = (WheelVerticalView) findViewById(R.id.medicationset_wv_minute);

		btns = new Button[7];
		btns[0] = mBtn_mon;
		btns[1] = mBtn_tues;
		btns[2] = mBtn_wed;
		btns[3] = mBtn_thur;
		btns[4] = mBtn_fri;
		btns[5] = mBtn_sat;
		btns[6] = mBtn_sun;
	}

	private int type = 1;// 判断提醒类型
	private Button[] btns;

	@Override
	public void click(View v) {
		switch (v.getId()) {
		case R.id.medicationset_imgv_back:
			mHandler.sendEmptyMessage(0);
			break;
		case R.id.medicationset_btn_takemedicine:
			type = 1;
			mBtn_takemedicine
					.setBackgroundResource(R.drawable.medicationset_btn_bg_select);
			mBtn_takemedicine.setTextColor(Color.parseColor("#ffffff"));
			mBtn_bloodpressure
					.setBackgroundResource(R.drawable.medication_btn_bg1);
			mBtn_bloodpressure.setTextColor(Color.parseColor("#616060"));
			mBtn_bloodsugar.setBackgroundResource(R.drawable.medication_btn_bg1);
			mBtn_bloodsugar.setTextColor(Color.parseColor("#616060"));
			mBtn_sports.setBackgroundResource(R.drawable.medication_btn_bg2);
			mBtn_sports.setTextColor(Color.parseColor("#616060"));
			mEdt_contents.setHint("例：降压药物，降糖药物等");
			break;
		case R.id.medicationset_btn_bloodpressure:
			type = 2;
			mBtn_takemedicine
					.setBackgroundResource(R.drawable.medication_btn_bg1);
			mBtn_takemedicine.setTextColor(Color.parseColor("#616060"));
			mBtn_bloodpressure
					.setBackgroundResource(R.drawable.medicationset_btn_bg_select);
			mBtn_bloodpressure.setTextColor(Color.parseColor("#ffffff"));
			mBtn_bloodsugar.setBackgroundResource(R.drawable.medication_btn_bg1);
			mBtn_bloodsugar.setTextColor(Color.parseColor("#616060"));
			mBtn_sports.setBackgroundResource(R.drawable.medication_btn_bg2);
			mBtn_sports.setTextColor(Color.parseColor("#616060"));
			mEdt_contents.setHint("例：早餐前测量血压");
			break;
		case R.id.medicationset_btn_bloodsugar:
			type = 3;
			mBtn_takemedicine
					.setBackgroundResource(R.drawable.medication_btn_bg1);
			mBtn_takemedicine.setTextColor(Color.parseColor("#616060"));
			mBtn_bloodpressure
					.setBackgroundResource(R.drawable.medication_btn_bg1);
			mBtn_bloodpressure.setTextColor(Color.parseColor("#616060"));
			mBtn_bloodsugar
					.setBackgroundResource(R.drawable.medicationset_btn_bg_select);
			mBtn_bloodsugar.setTextColor(Color.parseColor("#ffffff"));
			mBtn_sports.setBackgroundResource(R.drawable.medication_btn_bg2);
			mBtn_sports.setTextColor(Color.parseColor("#616060"));
			mEdt_contents.setHint("例：早餐前测量血糖");
			break;
		case R.id.medicationset_btn_sports:
			type = 4;
			mBtn_takemedicine
					.setBackgroundResource(R.drawable.medication_btn_bg1);
			mBtn_takemedicine.setTextColor(Color.parseColor("#616060"));
			mBtn_bloodpressure
					.setBackgroundResource(R.drawable.medication_btn_bg1);
			mBtn_bloodpressure.setTextColor(Color.parseColor("#616060"));
			mBtn_bloodsugar.setBackgroundResource(R.drawable.medication_btn_bg1);
			mBtn_bloodsugar.setTextColor(Color.parseColor("#616060"));
			mBtn_sports
					.setBackgroundResource(R.drawable.medicationset_btn_bg_select);
			mBtn_sports.setTextColor(Color.parseColor("#ffffff"));
			mEdt_contents.setHint("例：游泳，打羽毛球等");
			break;
		case R.id.medicationset_btn_mon:
			if (mBtn_mon.getCurrentTextColor() == Color.parseColor("#616060")) {
				mBtn_mon.setBackgroundResource(R.drawable.medicationset_btn_bg_select);
				mBtn_mon.setTextColor(Color.parseColor("#ffffff"));
			} else {
				mBtn_mon.setBackgroundResource(R.drawable.medication_btn_bg_week);
				mBtn_mon.setTextColor(Color.parseColor("#616060"));
			}
			break;
		case R.id.medicationset_btn_tues:
			if (mBtn_tues.getCurrentTextColor() == Color.parseColor("#616060")) {
				mBtn_tues
						.setBackgroundResource(R.drawable.medicationset_btn_bg_select);
				mBtn_tues.setTextColor(Color.parseColor("#ffffff"));

			} else {
				mBtn_tues
						.setBackgroundResource(R.drawable.medication_btn_bg_week);
				mBtn_tues.setTextColor(Color.parseColor("#616060"));

			}
			break;
		case R.id.medicationset_btn_wed:
			if (mBtn_wed.getCurrentTextColor() == Color.parseColor("#616060")) {
				mBtn_wed.setBackgroundResource(R.drawable.medicationset_btn_bg_select);
				mBtn_wed.setTextColor(Color.parseColor("#ffffff"));

			} else {
				mBtn_wed.setBackgroundResource(R.drawable.medication_btn_bg_week);
				mBtn_wed.setTextColor(Color.parseColor("#616060"));

			}
			break;
		case R.id.medicationset_btn_thur:
			if (mBtn_thur.getCurrentTextColor() == Color.parseColor("#616060")) {
				mBtn_thur
						.setBackgroundResource(R.drawable.medicationset_btn_bg_select);
				mBtn_thur.setTextColor(Color.parseColor("#ffffff"));

			} else {
				mBtn_thur
						.setBackgroundResource(R.drawable.medication_btn_bg_week);
				mBtn_thur.setTextColor(Color.parseColor("#616060"));

			}
			break;
		case R.id.medicationset_btn_fri:
			if (mBtn_fri.getCurrentTextColor() == Color.parseColor("#616060")) {
				mBtn_fri.setBackgroundResource(R.drawable.medicationset_btn_bg_select);
				mBtn_fri.setTextColor(Color.parseColor("#ffffff"));

			} else {
				mBtn_fri.setBackgroundResource(R.drawable.medication_btn_bg_week);
				mBtn_fri.setTextColor(Color.parseColor("#616060"));

			}
			break;
		case R.id.medicationset_btn_sat:
			if (mBtn_sat.getCurrentTextColor() == Color.parseColor("#616060")) {
				mBtn_sat.setBackgroundResource(R.drawable.medicationset_btn_bg_select);
				mBtn_sat.setTextColor(Color.parseColor("#ffffff"));

			} else {
				mBtn_sat.setBackgroundResource(R.drawable.medication_btn_bg_week);
				mBtn_sat.setTextColor(Color.parseColor("#616060"));

			}
			break;
		case R.id.medicationset_btn_sun:
			if (mBtn_sun.getCurrentTextColor() == Color.parseColor("#616060")) {
				mBtn_sun.setBackgroundResource(R.drawable.medicationset_btn_bg_select);
				mBtn_sun.setTextColor(Color.parseColor("#ffffff"));

			} else {
				mBtn_sun.setBackgroundResource(R.drawable.medication_btn_bg_week);
				mBtn_sun.setTextColor(Color.parseColor("#616060"));

			}
			break;
		case R.id.medicationset_btn_true:
			if (TextUtils.isEmpty(mEdt_contents.getText().toString())) {
				MyToast.show("请填写提醒内容");
			} else {
//				Log.wtf("aaa", "shengui");
				mHandler.sendEmptyMessageDelayed(1, 1);
			}
			break;
		case R.id.medicationset_btn_cancel:
			mHandler.sendEmptyMessage(0);
			break;

		default:
			break;
		}
	}

	public Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				Intent nullIntent = new Intent();
				Bundle b = new Bundle();
				nullIntent.putExtra("remind", b);
				setResult(0, nullIntent);
				finish();
				break;
			case 1:
				setData();
				break;
			default:
				break;
			}
		}
	};
	StringBuilder repeat;

	protected void setData() {
		Intent intent = new Intent();
		Bundle b = new Bundle();
		repeat = new StringBuilder("每周");
		for (int i = 0; i < btns.length; i++) {
			if (btns[i].getCurrentTextColor() != Color.parseColor("#616060")) {
				repeat.append(btns[i].getText().toString());
			}
		}
//		Log.wtf("aaa", "sfsdfsdfsdfsdf");
		if (type == 1) {
			b.putString("type", "用药提醒");
		}
		if (type == 2) {
			b.putString("type", "血压提醒");
		}
		if (type == 3) {
			b.putString("type", "血糖提醒");
		}
		if (type == 4) {
			b.putString("type", "运动提醒");
		}
		b.putString("contents", mEdt_contents.getText().toString().trim());
		b.putString(
				"time",
				mWv_hour.getCurrentItem()
						+ ":"
						+ (mWv_min.getCurrentItem() < 10 ? "0"
								+ mWv_min.getCurrentItem() : mWv_min
								.getCurrentItem()));
		b.putString("repeat", repeat.toString());
		b.putString("islocked", "false");
//		Log.wtf("aaa", b.toString());
		intent.putExtra("remind", b);
		setResult(0, intent);

		saveToDb();

	}

	DataBaseHelper db;

	private void saveToDb() {
		if (db == null) {
			db = new DataBaseHelper(this);
		}
		ContentValues value = new ContentValues();
		value.put("userId", App.USERID);
		value.put("type", type);
		value.put("contents", mEdt_contents.getText().toString().trim());
		value.put(
				"time",
				mWv_hour.getCurrentItem()
						+ ":"
						+ (mWv_min.getCurrentItem() < 10 ? "0"
								+ mWv_min.getCurrentItem() : mWv_min
								.getCurrentItem()));
		value.put("repeat", repeat.toString());
		value.put("islocked", "false");
		db.getWritableDatabase().insert("remind", null, value);
		db.close();
		finish();
	}
}
