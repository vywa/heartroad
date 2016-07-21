package com.hykj.activity.messure;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.FrameLayout;
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
import com.hykj.entity.BloodPressureInfo;
import com.hykj.manager.BloodPressureLineUtils;
import com.hykj.utils.MyToast;
import com.hykj.utils.MaterialUtil;
import com.hykj.utils.TimeUtil;

public class BloodPressureLineActivity extends Activity implements OnClickListener {
	private FrameLayout mFrameLayoutCubicLine;
	private BloodPressureLineUtils mCubicLineUtils;

	private ImageView iv_month;
	private ImageView iv_week;
	private ImageView iv_day;
	private TextView tv_month;
	private TextView tv_week;
	private TextView tv_day;
	private TextView tv_table;

	private long startTime;
	private long endTime;
	private Calendar cal;
	
	private int lowBPValue = 90;
	private int highBPValue = 140;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		App.addActivity(this);
		setContentView(R.layout.activity_bpinfo_line);

		findViews();

		reqData();

		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			getWindow().getDecorView().setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
	}

	private void findViews() {
		iv_month = (ImageView) findViewById(R.id.iv_bpinfo_month);
		iv_week = (ImageView) findViewById(R.id.iv_bpinfo_week);
		iv_day = (ImageView) findViewById(R.id.iv_bpinfo_day);

		tv_month = (TextView) findViewById(R.id.tv_bpinfo_month);
		tv_week = (TextView) findViewById(R.id.tv_bpinfo_week);
		tv_day = (TextView) findViewById(R.id.tv_bpinfo_day);
		tv_table = (TextView) findViewById(R.id.tv_bpinfo_table);

		mFrameLayoutCubicLine = (FrameLayout) findViewById(R.id.fl_cubic_line);

		tv_start = (TextView) findViewById(R.id.tv_bpinfo_start);
		tv_end = (TextView) findViewById(R.id.tv_bpinfo_end);

		iv_month.setOnClickListener(this);
		iv_week.setOnClickListener(this);
		iv_day.setOnClickListener(this);

		tv_start.setOnClickListener(this);
		tv_end.setOnClickListener(this);
		tv_table.setOnClickListener(this);
		
		cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.MILLISECOND, 0);
		endTime = cal.getTimeInMillis() + 24L * 3600 * 1000;
		startTime = endTime - 7L * 24 * 3600 * 1000;
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
//			MaterialUtil.progressDialogDismiss();
			switch (msg.what) {
			case Constant.GET_DATA_SUCCESS:
				addCubicLineView(serverInfos);
				break;
			case Constant.GET_DATA_ANALYZE_ERROR:
				// getDBData();
				MyToast.show("解析数据失败");
				break;
			case Constant.GET_DATA_NETWORK_ERROR:
				// getDBData();
				MyToast.show("网络错误");
				break;
			case Constant.GET_DATA_NULL:
				// getDBData();
				MyToast.show("获取列表为空");
				break;
			case Constant.GET_DATA_SERVER_ERROR:
				// getDBData();
				MyToast.show("服务器返回数据有误");
				break;
			/*
			 * case -1: addCubicLineView(locInfos); break; case 0: getDBData();
			 * break;
			 */
			}
		}
	};
	StringBuilder url;

	private void reqData() {
		tv_start.setText(TimeUtil.getStringTime(startTime));
		tv_end.setText(TimeUtil.getStringTime(endTime));

//		MaterialUtil.showProgressDialog(BloodPressureLineActivity.this);
		url = new StringBuilder(App.BASE + Constant.BLOODPRESSURE_HISTORY);
		url.append("tocken=" + App.TOKEN);
		url.append("&data=");
		JSONObject json = new JSONObject();
		try {
			json.put("startTime", startTime);
			json.put("endTime", endTime);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		url.append(json.toString());

//		Log.wtf("获取血压信息URL", url.toString());
//		Log.wtf("startTime", TimeUtil.getStringTime(startTime));
//		Log.wtf("endTime", TimeUtil.getStringTime(endTime));

		App.getRequestQueue().add(getreqData());
	}

	protected void getDBData() {
		new Thread() {
			public void run() {
				DataBaseHelper helper = new DataBaseHelper(App.getContext());
				SQLiteDatabase db = helper.getWritableDatabase();
				Cursor cursor = db.query("bpinfo", null, "measureTime>? AND measureTime<?", new String[] { 0 + "", 10000000L + "" }, null, null, "measureTime asc");
				while (cursor.moveToNext()) {
					locInfos.add(new BloodPressureInfo(cursor.getLong(0), cursor.getInt(2), cursor.getInt(3), cursor.getInt(4)));
				}
				handler.sendEmptyMessage(-1);
			}
		}.start();
	}

	ArrayList<BloodPressureInfo> serverInfos = new ArrayList<BloodPressureInfo>();
	ArrayList<BloodPressureInfo> locInfos = new ArrayList<BloodPressureInfo>();
	int responseCode = -1;
	private TextView tv_start;
	private TextView tv_end;

	StringRequest getreqData() {
		return new StringRequest(Method.GET, url.toString(), new Listener<String>() {
			@Override
			public void onResponse(String res) {
//				Log.wtf("获取血压信息结果", res);
				
				try {
					JSONObject response = new JSONObject(res);
					responseCode = response.getInt("code"); // 211 112

					if (responseCode != 211) {
						handler.sendEmptyMessage(Constant.GET_DATA_SERVER_ERROR);
						return;
					}
					JSONArray array = response.optJSONArray("bloodPressureInfo");
					if (array == null) {
						handler.sendEmptyMessage(Constant.GET_DATA_NULL);
						return;
					}
					serverInfos.clear();
					for (int i = 0; i < array.length(); i++) {
						JSONObject object = (JSONObject) array.get(i);
						BloodPressureInfo pressureInfo = new BloodPressureInfo(object.getLong("measureTime"), object.getInt("highBP"), object.getInt("lowBP"), object.getInt("heartRate"));
						serverInfos.add(pressureInfo);
					}
					
					JSONObject targetJson = response.optJSONObject("healthLine");
					lowBPValue = targetJson.optInt("lowBPValue");
					highBPValue = targetJson.optInt("highBPValue");
					
					handler.sendEmptyMessage(Constant.GET_DATA_SUCCESS);
				} catch (Exception e) {
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

	private void writeToDB() {
		new Thread() {
			public void run() {
				DataBaseHelper helper = new DataBaseHelper(App.getContext());
				SQLiteDatabase db = helper.getWritableDatabase();

				try {
					db.beginTransaction();
					for (BloodPressureInfo info : serverInfos) {
						String sql = "REPLACE INTO bpinfo VALUES(" + info.getMeasureTime() + "," + App.USERID + "," + info.getHighBP() + "," + info.getLowBP() + "," + info.getHeartRate()
								+ ",1);";
						db.execSQL(sql);
					}
					db.setTransactionSuccessful();
				} finally {
					db.endTransaction();
				}
				db.close();
				helper.close();
				handler.sendEmptyMessage(0);
			}
		}.start();
	}

	private void addCubicLineView(ArrayList<BloodPressureInfo> pressureInfos) {
		mFrameLayoutCubicLine.removeAllViews();
		mCubicLineUtils = new BloodPressureLineUtils(BloodPressureLineActivity.this, pressureInfos,lowBPValue,highBPValue);
		mFrameLayoutCubicLine.addView(mCubicLineUtils.initCubicLineGraphView());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_bpinfo_month:
			iv_month.setBackgroundResource(R.drawable.bsinfo_dot);
			tv_month.setTextColor(Color.WHITE);

			iv_week.setBackgroundResource(R.drawable.transparent);
			tv_week.setTextColor(Color.BLACK);
			iv_day.setBackgroundResource(R.drawable.transparent);
			tv_day.setTextColor(Color.BLACK);

			cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.MILLISECOND, 0);
			cal.add(Calendar.MONTH, -1);
			startTime = cal.getTimeInMillis();

			cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.MILLISECOND, 0);
			endTime = cal.getTimeInMillis() + 24L * 3600 * 1000;

			reqData();
			break;
		case R.id.iv_bpinfo_week:
			iv_week.setBackgroundResource(R.drawable.bsinfo_dot);
			tv_week.setTextColor(Color.WHITE);

			iv_month.setBackgroundResource(R.drawable.transparent);
			tv_month.setTextColor(Color.BLACK);
			iv_day.setBackgroundResource(R.drawable.transparent);
			tv_day.setTextColor(Color.BLACK);

			cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.MILLISECOND, 0);
			startTime = cal.getTimeInMillis() - 6L * 24 * 3600 * 1000;

			endTime = cal.getTimeInMillis() + 24L * 3600 * 1000;

			reqData();
			break;
		case R.id.iv_bpinfo_day:
			iv_day.setBackgroundResource(R.drawable.bsinfo_dot);
			tv_day.setTextColor(Color.WHITE);

			iv_week.setBackgroundResource(R.drawable.transparent);
			tv_week.setTextColor(Color.BLACK);
			iv_month.setBackgroundResource(R.drawable.transparent);
			tv_month.setTextColor(Color.BLACK);

			cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.MILLISECOND, 0);
			startTime = cal.getTimeInMillis();
			endTime = cal.getTimeInMillis() + 24L * 3600 * 1000;

			reqData();
			break;
		case R.id.tv_bpinfo_start:
			showDatePickerDialog("请选择开始日期", tv_start);
			break;
		case R.id.tv_bpinfo_end:
			showDatePickerDialog("请选择截至日期", tv_end);
			break;
		case R.id.tv_bpinfo_table:
			Intent intent = new Intent(this,BloodPressureTableActivity.class);
			intent.putExtra("pressureInfos", serverInfos);
			intent.putExtra("startTime", startTime);
			intent.putExtra("endTime", endTime);
			startActivity(intent);
			break;
		}
	}

	private void showDatePickerDialog(final String title, final TextView tv) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View view = getLayoutInflater().inflate(R.layout.dialog_choosebirth, null);
		final DatePicker datePicker = (DatePicker) view.findViewById(R.id.dialog_dp_brith);
		String string = tv.getText().toString();
		int year = Integer.parseInt(string.substring(0, string.indexOf("年")));
		int month = Integer.parseInt(string.substring(string.indexOf("年") + 1, string.indexOf("月"))) - 1;
		int day = Integer.parseInt(string.substring(string.indexOf("月") + 1, string.indexOf("日")));
		datePicker.init(year, month, day, null);
		builder.setView(view);
		builder.setTitle(title);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				String time = datePicker.getYear() + "年" + (datePicker.getMonth() + 1) + "月" + datePicker.getDayOfMonth() + "日 00:00";
				if (!"请选择开始日期".equals(title) && TimeUtil.getLongTime(time) < startTime) {
					MyToast.show("选择结束日期不能小于开始日期");
					return;
				}
				tv.setText(time);
				if ("请选择开始日期".equals(title)) {
					startTime = TimeUtil.getLongTime(tv.getText().toString());
				} else {
					endTime = TimeUtil.getLongTime(tv.getText().toString());
				}
				reqData();
				arg0.cancel();
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				arg0.cancel();
			}
		});
		builder.create().show();
	}

}
