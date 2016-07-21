package com.hykj.activity.messure;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.gc.materialdesign.widgets.ProgressDialog;
import com.hykj.App;
import com.hykj.Constant;
import com.hykj.R;
import com.hykj.db.DataBaseHelper;
import com.hykj.entity.MyDate;
import com.hykj.fragment.usermanagement.MainconditionFragment;
import com.hykj.utils.MyProgress;
import com.hykj.utils.MyToast;
import com.hykj.utils.TimeUtil;
import com.hykj.view.spinnerwheel.AbstractWheel;
import com.hykj.view.spinnerwheel.OnWheelChangedListener;
import com.hykj.view.spinnerwheel.WheelHorizontalView;
import com.hykj.view.spinnerwheel.WheelVerticalView;
import com.hykj.view.spinnerwheel.adapters.ArrayWheelAdapter;

public class AddBloodPressureActivity extends Activity implements OnClickListener {

	private WheelHorizontalView whv_lowPressure;
	private WheelHorizontalView whv_highPressure;
	private WheelHorizontalView whv_heartRate;
	
	private WheelVerticalView wvv_time;
	private Button bt_confirm;
	int month ;
	int day;
	int year;
	ArrayList<MyDate> myDates = new ArrayList<MyDate>();
	String[] time = new String[24 * 60];
	String[] days = new String[100];
	
	private String[] hbpValues;
	private String[] hsValues;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		App.addActivity(this);
		setContentView(R.layout.activity_addbloodpressure);

		ImageView iv_back = (ImageView) findViewById(R.id.iv_title_back);
		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("输入血压");
		iv_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AddBloodPressureActivity.this.finish();
			}
		});
		
		initDate();
		
		findViews();
		
		registAndSettings();
	}

	private void registAndSettings() {
		
		ArrayWheelAdapter<String> pressureAdapter = new ArrayWheelAdapter<String>(this, hbpValues);
		pressureAdapter.setItemResource(R.layout.item_addbp_wheeltext);
		pressureAdapter.setItemTextResource(R.id.tv_itemaddbp);
		whv_lowPressure.setViewAdapter(pressureAdapter);
		whv_highPressure.setViewAdapter(pressureAdapter);
		whv_lowPressure.setCurrentItem(80);
		whv_highPressure.setCurrentItem(120);
		whv_lowPressure.setVisibleItems(3);
		whv_highPressure.setVisibleItems(3);

		ArrayWheelAdapter<String> heartAdapter = new ArrayWheelAdapter<String>(this, hsValues);
		heartAdapter.setItemResource(R.layout.item_addbp_wheeltext);
		heartAdapter.setItemTextResource(R.id.tv_itemaddbp);
		whv_heartRate.setViewAdapter(heartAdapter);
		whv_heartRate.setCurrentItem(75);
		whv_heartRate.setVisibleItems(3);

		ArrayWheelAdapter<String> hourAdapter = new ArrayWheelAdapter<String>(this, time);
		hourAdapter.setItemResource(R.layout.wheel_text_centered);
		hourAdapter.setItemTextResource(R.id.text);
		wvv_time.setViewAdapter(hourAdapter);
		
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
		long now = calendar.getTimeInMillis();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		long zero = calendar.getTimeInMillis();
		
		wvv_time.setCurrentItem((int)((now-zero)/1000/60));
		
		ArrayWheelAdapter<String> dayAdapter = new ArrayWheelAdapter<String>(this, days);
		dayAdapter.setItemResource(R.layout.whell_bsday);
		dayAdapter.setItemTextResource(R.id.text);
		whv_day.setViewAdapter(dayAdapter);
		whv_day.setCurrentItem(days.length-1);

		whv_day.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
				MyDate myDate = myDates.get(newValue);
				String between = myDate.getBetween();
				if ("0".equals(between)) {
					tv_between.setText("今天");
				}else{
					tv_between.setText(between+"天前");
				}
				String month = myDate.getMonth();
				String year = myDate.getYear();
				tv_month.setText(year+"年"+month+"月");
			}
		});
		
		bt_confirm.setOnClickListener(this);
	}

	private void initDate() {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
		for (int i = 99; i >= 0; i--) {
			calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			calendar.add(Calendar.DAY_OF_YEAR, -i);

			MyDate myDate = new MyDate();
			myDate.setBetween(i + "");
			myDate.setDay(calendar.get(Calendar.DAY_OF_MONTH) + "");
			myDate.setMonth(calendar.get(Calendar.MONTH) + 1 + "");
			myDate.setYear(calendar.get(Calendar.YEAR) + "");
			myDate.setWeek(calendar.get(Calendar.DAY_OF_WEEK) + "");
			myDate.setTime(calendar.getTimeInMillis());

			myDates.add(myDate);
			
		}
		
		for (int i = 0; i < myDates.size(); i++) {
			StringBuilder builder= new StringBuilder(myDates.get(i).getDay());
			builder.append("\n");
			switch (Integer.parseInt(myDates.get(i).getWeek())) {
			case 1:
				builder.append("日");
				break;
			case 2:
				builder.append("一");
				break;
			case 3:
				builder.append("二");
				break;
			case 4:
				builder.append("三");
				break;
			case 5:
				builder.append("四");
				break;
			case 6:
				builder.append("五");
				break;
			case 7:
				builder.append("六");
				break;
			}
			days[i] = builder.toString();
		}
 
		int minute = 0;
		int hour = 0;
		StringBuffer buffer;
		for (int i = 0; i < time.length; i++) {
			buffer = new StringBuffer();
			if (minute == 60) {
				hour++;
				minute = 0;
			}
			if (hour < 10) {
				buffer.append(0);
			}
			buffer.append(hour).append(":");
			if (minute < 10) {
				buffer.append(0);
			}
			buffer.append(minute);
			
			time[i] = buffer.toString();
			minute++;
		}
		
		hbpValues = new String[300];
		hsValues = new String[221];

		for (int i = 0; i < 300; i++) {
			hbpValues[i] = i + "";
			if (i<=220) {
				hsValues[i] = i + "";
			}
		}
		
	}
	
	private void findViews() {
		whv_lowPressure = (WheelHorizontalView) findViewById(R.id.whv_addbp_lbp);
		whv_highPressure = (WheelHorizontalView) findViewById(R.id.whv_addbp_hbp);
		whv_heartRate = (WheelHorizontalView) findViewById(R.id.whv_addbp_hr);
		
		wvv_time = (WheelVerticalView) findViewById(R.id.wv_addbp_time);
		whv_day = (WheelHorizontalView) findViewById(R.id.wv_addbp_day);
		
		bt_confirm = (Button) findViewById(R.id.bt_addbp_confirm);
		
		tv_month = (TextView) findViewById(R.id.tv_addbp_month);
		tv_between = (TextView) findViewById(R.id.tv_addbp_between);
		
		MyDate myDate = myDates.get(myDates.size()-1);
		String month = myDate.getMonth();
		String year = myDate.getYear();
		tv_month.setText(year+"年"+month+"月");
	}

	ProgressDialog dialog;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_addbp_confirm:
//			MaterialUtil.showProgressDialog(AddBloodPressureActivity.this);
			MyProgress.show(AddBloodPressureActivity.this,R.layout.progressdialog_getcode);
			
			final int lowBP = whv_lowPressure.getCurrentItem();
			final int highBP = whv_highPressure.getCurrentItem();
			final int heartRate = whv_heartRate.getCurrentItem();
			
			int currentDay = whv_day.getCurrentItem();
			int currentTime = wvv_time.getCurrentItem();
			
			long measureTime = myDates.get(currentDay).getTime()+currentTime*60*1000;
			
			/*
			 * new Thread() { public void run() { writeToBSDB(lowBP, highBP,
			 * heartRate, measureTime); synchronizationDB(); }; }.start();
			 */
			uploadData(measureTime,  highBP, lowBP, heartRate);
			break;

		}
	}


	protected void writeToBSDB(int lowBP, int highBP, int heartRate, long measureTime) {
		if (bpDatabaseHelper == null) {
			bpDatabaseHelper = new DataBaseHelper(App.getContext());
		}
		SQLiteDatabase bpDB = bpDatabaseHelper.getWritableDatabase();

		bpDB.beginTransaction();
		try {
			bpDB.execSQL("REPLACE INTO bpinfo VALUES(" + measureTime + "," + App.USERID + "," + highBP + "," + lowBP + "," + heartRate + ",0)");
			bpDB.setTransactionSuccessful();
		} finally {
			bpDB.endTransaction();
		}
		bpDB.close();
		bpDatabaseHelper.close();
	}

	int count = 0;

	protected void synchronizationDB() {
		if (bpDatabaseHelper == null) {
			bpDatabaseHelper = new DataBaseHelper(App.getContext());
		}
		SQLiteDatabase bsDB = bpDatabaseHelper.getWritableDatabase();
		Cursor c = bsDB.rawQuery("SELECT * FROM bpinfo where uploadSuccess=?", new String[] { "0" });
		while (c.moveToNext()) {
			count++;
			uploadData(c.getLong(0),  c.getInt(2), c.getInt(3), c.getInt(4));
		}
		c.close();
		bpDatabaseHelper.close();
	}

	private void uploadData(long measureTime,  int highPressure, int lowPressure, int heartRate) {
		url = new StringBuilder(App.BASE + Constant.ADD_BLOOD_PRESSURE);
		url.append("tocken="+App.TOKEN);
		url.append("&data=");
		JSONObject json = new JSONObject();
		try {
			json.put("measureTime", measureTime);
			json.put("highBP", highPressure);
			json.put("lowBP", lowPressure);
			json.put("heartRate", heartRate);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		url.append(json.toString());
		
//		Log.wtf("添加血压信息URL", url.toString());

		App.getRequestQueue().add(getreqFreshening(measureTime));
	}

	StringBuilder url = null;
	int responseCode = -1;
	String description = null;
	private DataBaseHelper bpDatabaseHelper;

	StringRequest getreqFreshening(final long measureTime) {
		return new StringRequest(Method.GET, url.toString(), new Listener<String>() {
			@Override
			public void onResponse(String res) {
				try {
//					Log.wtf("添加血压信息返回结果", res);
					JSONObject json = new JSONObject(res);
					responseCode = json.getInt("code");

					if (responseCode != 0) { //成功 0
						description = json.getString("message");
						handler.sendEmptyMessage(Constant.GET_DATA_SERVER_ERROR);
						return;
					}
					Message message = Message.obtain();
					message.obj = measureTime;
					message.what = Constant.GET_DATA_SUCCESS;
					handler.sendMessage(message);
					App.NEEDGET_BP = true;
				} catch (JSONException e) {
					e.printStackTrace();
					handler.sendEmptyMessage(Constant.GET_DATA_ANALYZE_ERROR);
				}
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				handler.sendEmptyMessage(Constant.GET_DATA_NETWORK_ERROR);
			}
		});
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			MyProgress.dismiss();
			switch (msg.what) {
			case Constant.GET_DATA_SUCCESS:
				/*
				 * long measureTime = (Long) msg.obj;
				 * flagewriteToDB(measureTime); if (--count == 0) {
				 * MaterialUtil.progressDialogDismiss();
				 * MaterialUtil.showSnackBar(AddBloodPressureActivity.this,
				 * "上传成功", true); }
				 */
//				MaterialUtil.progressDialogDismiss();
//				MaterialUtil.showSnackBar(AddBloodPressureActivity.this, "上传成功", true);
				MyToast.show("上传成功");
				finish();
				break;
			case Constant.GET_DATA_ANALYZE_ERROR:
				/*
				 * if (--count == 0) { MaterialUtil.progressDialogDismiss();
				 * MaterialUtil.showSnackBar(AddBloodPressureActivity.this,
				 * "解析数据失败", true); }
				 */
//				MaterialUtil.progressDialogDismiss();
//				MaterialUtil.showSnackBar(AddBloodPressureActivity.this, "解析数据失败", true);
				MyToast.show("解析数据失败");
				finish();
				break;
			case Constant.GET_DATA_NETWORK_ERROR:
				/*
				 * if (--count == 0) { //MaterialUtil.progressDialogDismiss();
				 * MaterialUtil.showSnackBar(AddBloodPressureActivity.this,
				 * "网络错误", false); }
				 */
//				MaterialUtil.progressDialogDismiss();
//				MaterialUtil.showSnackBar(AddBloodPressureActivity.this, "网络错误", false);
				MyToast.show("网络错误");
				break;
			case Constant.GET_DATA_NULL:
				/*
				 * if (--count == 0) { MaterialUtil.progressDialogDismiss();
				 * MaterialUtil.showSnackBar(AddBloodPressureActivity.this,
				 * "获取列表为空", true); }
				 */
//				MaterialUtil.progressDialogDismiss();
//				MaterialUtil.showSnackBar(AddBloodPressureActivity.this, "获取列表为空", true);
				MyToast.show("获取列表为空");
				finish();
				break;
			case Constant.GET_DATA_SERVER_ERROR:
				/*
				 * if (--count == 0) { MaterialUtil.progressDialogDismiss();
				 * MaterialUtil.showSnackBar(AddBloodPressureActivity.this,
				 * description, true); }
				 */
//				MaterialUtil.progressDialogDismiss();
//				MaterialUtil.showSnackBar(AddBloodPressureActivity.this, description, true);
				MyToast.show(description);
				finish();
				break;
			}
		}
	};
	private WheelHorizontalView whv_day;
	private TextView tv_month;
	private TextView tv_between;

	private void flagewriteToDB(long measureTime) {
		if (bpDatabaseHelper == null) {
			bpDatabaseHelper = new DataBaseHelper(App.getContext());
		}
		SQLiteDatabase bsDB = bpDatabaseHelper.getWritableDatabase();
		Cursor c = bsDB.rawQuery("SELECT * FROM bpinfo where measureTime=?", new String[] { measureTime + "" });
		if (c.moveToNext()) {
			ContentValues cv = new ContentValues();
			cv.put("uploadSuccess", 1);
			bsDB.update("bpinfo", cv, "measureTime = ?", new String[] { measureTime + "" });
			bsDB.close();
			return;
		}
		bsDB.close();
		bpDatabaseHelper.close();
	}

}
