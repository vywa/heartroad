package com.hykj.activity.messure;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hykj.App;
import com.hykj.Constant;
import com.hykj.R;
import com.hykj.db.DataBaseHelper;
import com.hykj.entity.MyDate;
import com.hykj.fragment.usermanagement.MainconditionFragment;
import com.hykj.utils.MyProgress;
import com.hykj.utils.MyToast;
import com.hykj.view.spinnerwheel.AbstractWheel;
import com.hykj.view.spinnerwheel.OnWheelChangedListener;
import com.hykj.view.spinnerwheel.WheelHorizontalView;
import com.hykj.view.spinnerwheel.WheelVerticalView;
import com.hykj.view.spinnerwheel.adapters.ArrayWheelAdapter;

public class AddBloodSugarActivity extends Activity implements OnClickListener {

	private WheelVerticalView wv_value;
	private WheelVerticalView wv_measureType;
	private WheelVerticalView wv_time;
	private WheelHorizontalView whv_day;
	private Button bt_confirm;
	private TextView tv_between;
	private TextView tv_yearmonth;

	private DataBaseHelper bsdbHelper;

	private String[] bSvalues;
	ArrayList<MyDate> myDates = new ArrayList<MyDate>();
	String[] time = new String[24 * 60];
	String[] days = new String[100];

	public static String[] measureType = { "凌晨测量", "早餐前测量", "早餐后测量", "午餐前测量",
			"午餐后测量", "晚餐前测量", "晚餐后测量", "睡前测量", "随机测量" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		App.addActivity(this);
		setContentView(R.layout.activity_addbloodsugar);

		initDate();

		findViews();

		registAndSettings();
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
			StringBuilder builder = new StringBuilder(myDates.get(i).getDay());
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

		bSvalues = new String[334];

		for (int i = 0; i < 334; i++) {
			bSvalues[i] = i / 10.0f + "";
		}

	}

	private void registAndSettings() {

		ArrayWheelAdapter<String> wvValueAdapter = new ArrayWheelAdapter<String>(
				this, bSvalues);
		wvValueAdapter.setItemResource(R.layout.wheel_text_centered);
		wvValueAdapter.setItemTextResource(R.id.text);
		wv_value.setViewAdapter(wvValueAdapter);
		wv_value.setVisibleItems(5);
		wv_value.setCurrentItem(51);

		ArrayWheelAdapter<String> hourAdapter = new ArrayWheelAdapter<String>(
				this, time);
		hourAdapter.setItemResource(R.layout.wheel_text_centered);
		hourAdapter.setItemTextResource(R.id.text);
		wv_time.setViewAdapter(hourAdapter);

		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
		long now = calendar.getTimeInMillis();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		long zero = calendar.getTimeInMillis();

		wv_time.setCurrentItem((int) ((now - zero) / 1000 / 60));

		ArrayWheelAdapter<String> measureTypeAdapter = new ArrayWheelAdapter<String>(
				this, measureType);
		measureTypeAdapter.setItemResource(R.layout.whell_bstype);
		measureTypeAdapter.setItemTextResource(R.id.text);
		wv_measureType.setViewAdapter(measureTypeAdapter);
		wv_measureType.setCurrentItem(3);

		ArrayWheelAdapter<String> dayAdapter = new ArrayWheelAdapter<String>(
				this, days);
		dayAdapter.setItemResource(R.layout.whell_bsday);
		dayAdapter.setItemTextResource(R.id.text);
		whv_day.setViewAdapter(dayAdapter);
		whv_day.setCurrentItem(days.length - 1);

		whv_day.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(AbstractWheel wheel, int oldValue,
					int newValue) {
				MyDate myDate = myDates.get(newValue);
				String between = myDate.getBetween();
				if ("0".equals(between)) {
					tv_between.setText("今天");
				} else {
					tv_between.setText(between + "天前");
				}
				String month = myDate.getMonth();
				String year = myDate.getYear();
				tv_yearmonth.setText(year + "年" + month + "月");
			}
		});
		bt_confirm.setOnClickListener(this);
	}

	private void findViews() {

		ImageView iv_back = (ImageView) findViewById(R.id.iv_title_back);
		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("输入血糖");
		iv_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AddBloodSugarActivity.this.finish();
			}
		});

		wv_value = (WheelVerticalView) findViewById(R.id.wv_addbs_1);
		wv_value.setPadding(50, 0, 50, 0);

		wv_measureType = (WheelVerticalView) findViewById(R.id.wv_addbs_type);
		wv_time = (WheelVerticalView) findViewById(R.id.wv_addbs_time);

		whv_day = (WheelHorizontalView) findViewById(R.id.wv_addbs_day);

		tv_between = (TextView) findViewById(R.id.tv_addbs_between);
		tv_yearmonth = (TextView) findViewById(R.id.tv_addbs_month);

		MyDate myDate = myDates.get(myDates.size() - 1);
		String month = myDate.getMonth();
		String year = myDate.getYear();
		tv_yearmonth.setText(year + "年" + month + "月");

		bt_confirm = (Button) findViewById(R.id.bt_addbs_confirm);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_addbs_confirm:
			// MaterialUtil.showProgressDialog(AddBloodSugarActivity.this);
			MyProgress.show(AddBloodSugarActivity.this,R.layout.progressdialog_getcode);
			int no_1 = wv_value.getCurrentItem();
			final double bsValue = no_1 / 10.0;
			final int measureType = wv_measureType.getCurrentItem();

			int currentDay = whv_day.getCurrentItem();
			int currentTime = wv_time.getCurrentItem();

			long measureTime = myDates.get(currentDay).getTime() + currentTime* 60 * 1000;
			
			/*
			 * new Thread(){ public void run() {
			 * writeToBSDB(measureTime,bsValue,measureType);
			 * 
			 * synchronizationDB(); }; }.start();
			 */
			
			uploadData(measureTime, bsValue, measureType);
			break;
		}
	}

	private void synchronizationDB() {
		if (bsdbHelper == null) {
			bsdbHelper = new DataBaseHelper(App.getContext());
		}
		SQLiteDatabase bsDB = bsdbHelper.getWritableDatabase();
		Cursor c = bsDB.rawQuery("SELECT * FROM bsinfo WHERE uploadSuccess=?",
				new String[] { "0" });
		while (c.moveToNext()) {
			count++;
			uploadData(c.getLong(0), c.getDouble(2), c.getInt(3));
		}
		c.close();
		bsdbHelper.close();
	}

	private void uploadData(long measureTime, double bsValue, int measureType) {
		url = new StringBuilder(App.BASE + Constant.ADD_BLOOD_SUGAR);
		url.append("tocken=" + App.TOKEN);
		url.append("&data=");
		JSONObject json = new JSONObject();
		try {
			json.put("measureTime", measureTime);
			json.put("bsValue", bsValue);
			json.put("measureType", measureType);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		url.append(json.toString());
//		Log.wtf("添加血糖信息URL", url.toString());
		App.getRequestQueue().add(getreqFreshening(measureTime));
	}

	int count = 0;

	private void writeToBSDB(long measureTime, double bsValue, int measureType2) {
		if (bsdbHelper == null) {
			bsdbHelper = new DataBaseHelper(App.getContext());
		}
		SQLiteDatabase bsDB = bsdbHelper.getWritableDatabase();

		bsDB.beginTransaction();
		try {
			bsDB.execSQL("REPLACE INTO bsinfo VALUES(" + measureTime + ","
					+ App.USERID + "," + bsValue + "," + measureType2 + ",0)"); // measureTime,App.USERID,bsValue,measureType,0
			bsDB.setTransactionSuccessful();
		} finally {
			bsDB.endTransaction();
		}
		bsDB.close();
		bsdbHelper.close();
	}

	StringBuilder url = null;
	int responseCode = -1;
	String description = null;

	StringRequest getreqFreshening(final long measureTime) {
		return new StringRequest(Method.GET, url.toString(),
				new Listener<String>() {
					@Override
					public void onResponse(String res) {
//						Log.wtf("添加血糖信息返回结果", res);
						try {
							JSONObject json = new JSONObject(res);
							responseCode = json.getInt("code");

							if (responseCode != 0) { // 成功 0
								description = json.getString("message");
								handler.sendEmptyMessage(Constant.GET_DATA_SERVER_ERROR);
								return;
							}
							Message message = Message.obtain();
							message.obj = measureTime;
							message.what = Constant.GET_DATA_SUCCESS;
							handler.sendMessage(message);
							App.NEEDGET_BS = true;
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

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			MyProgress.dismiss();
			switch (msg.what) {
			case Constant.GET_DATA_SUCCESS:
				// long measureTime = (Long) msg.obj;
				// flagewriteToDB(measureTime);
				/*
				 * if (--count == 0) { MaterialUtil.progressDialogDismiss();
				 * MaterialUtil.showSnackBar(AddBloodSugarActivity.this, "上传成功",
				 * true); }
				 */
				// MaterialUtil.progressDialogDismiss();
				// MaterialUtil.showSnackBar(AddBloodSugarActivity.this, "上传成功",
				// true);
				MyToast.show("上传成功");
				finish();
				break;
			case Constant.GET_DATA_ANALYZE_ERROR:
				/*
				 * if (--count == 0) { MaterialUtil.progressDialogDismiss();
				 * MaterialUtil.showSnackBar(AddBloodSugarActivity.this,
				 * "解析数据失败", true); }
				 */
				// MaterialUtil.progressDialogDismiss();
				// MaterialUtil.showSnackBar(AddBloodSugarActivity.this,
				// "解析数据失败", true);
				MyToast.show("解析数据失败");
				finish();
				break;
			case Constant.GET_DATA_NETWORK_ERROR:
				/*
				 * if (--count == 0) { MaterialUtil.progressDialogDismiss();
				 * MaterialUtil.showSnackBar(AddBloodSugarActivity.this, "网络错误",
				 * false); }
				 */
				// MaterialUtil.progressDialogDismiss();
				// MaterialUtil.showSnackBar(AddBloodSugarActivity.this, "网络错误",
				// false);
				MyToast.show("解析数据失败");
				break;
			case Constant.GET_DATA_NULL:
				/*
				 * if (--count == 0) { MaterialUtil.progressDialogDismiss();
				 * MaterialUtil.showSnackBar(AddBloodSugarActivity.this,
				 * "获取列表为空", true); }
				 */
				// MaterialUtil.progressDialogDismiss();
				// MaterialUtil.showSnackBar(AddBloodSugarActivity.this,
				// "获取列表为空", true);
				MyToast.show("获取列表为空");
				finish();
				break;
			case Constant.GET_DATA_SERVER_ERROR:
				/*
				 * if (--count == 0) { MaterialUtil.progressDialogDismiss();
				 * MaterialUtil.showSnackBar(AddBloodSugarActivity.this,
				 * description, true); }
				 */
				// MaterialUtil.progressDialogDismiss();
				// MaterialUtil.showSnackBar(AddBloodSugarActivity.this,
				// description, true);
				MyToast.show(description);
				finish();
				break;
			}
		}
	};

	private void flagewriteToDB(long measureTime) {
		if (bsdbHelper == null) {
			bsdbHelper = new DataBaseHelper(App.getContext());
		}
		SQLiteDatabase bsDB = bsdbHelper.getWritableDatabase();
		Cursor c = bsDB.rawQuery("SELECT * FROM bsinfo WHERE measureTime=?",
				new String[] { measureTime + "" });
		if (c.moveToNext()) {
			ContentValues cv = new ContentValues();
			cv.put("uploadSuccess", 1 + "");
			bsDB.update("bsinfo", cv, "measureTime = ?",
					new String[] { measureTime + "" });
			bsDB.close();
			return;
		}
		bsDB.close();
		bsdbHelper.close();
	}
}
