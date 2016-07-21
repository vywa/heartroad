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
import com.hykj.entity.BloodSugarInfo;
import com.hykj.manager.BloodSugarLineUtils;
import com.hykj.utils.MyToast;
import com.hykj.utils.TimeUtil;

public class BloodSugarLineActivity extends Activity implements OnClickListener {
	private FrameLayout mFrameLayoutCubicLine;
	private BloodSugarLineUtils mCubicLineUtils;

	ArrayList<BloodSugarInfo> sugarServerInfos = new ArrayList<BloodSugarInfo>();
	ArrayList<BloodSugarInfo> sugarLocInfos = new ArrayList<BloodSugarInfo>();
	private ImageView iv_month;
	private ImageView iv_week;
	private ImageView iv_day;

	private long startTime;
	private long endTime;
	private Calendar cal;
	
	private double minValue = 3.9;
	private double maxBeforeMealValue = 7;
	private double maxAfterMealValue = 11.1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		App.addActivity(this);
		setContentView(R.layout.activity_bsinfo_line);

		findViews();

		reqData();

		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			getWindow().getDecorView().setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}

	}

	private void findViews() {
		iv_month = (ImageView) findViewById(R.id.iv_bsinfo_month);
		iv_week = (ImageView) findViewById(R.id.iv_bsinfo_week);
		iv_day = (ImageView) findViewById(R.id.iv_bsinfo_day);

		tv_month = (TextView) findViewById(R.id.tv_bsinfo_month);
		tv_week = (TextView) findViewById(R.id.tv_bsinfo_week);
		tv_day = (TextView) findViewById(R.id.tv_bsinfo_day);

		tv_start = (TextView) findViewById(R.id.tv_bsinfo_start);
		tv_end = (TextView) findViewById(R.id.tv_bsinfo_end);
		tv_table = (TextView) findViewById(R.id.tv_bsinfo_table);

		mFrameLayoutCubicLine = (FrameLayout) findViewById(R.id.fl_cubic_line);

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
				addCubicLineView(sugarServerInfos);
				break;
			case Constant.GET_DATA_ANALYZE_ERROR:
				MyToast.show("解析数据失败");
				break;
			case Constant.GET_DATA_NETWORK_ERROR:
				MyToast.show("网络错误");
				break;
			case Constant.GET_DATA_NULL:
				MyToast.show("获取列表为空");
				break;
			case Constant.GET_DATA_SERVER_ERROR:
				MyToast.show("服务器返回数据有误");
				break;
			case -1:
				addCubicLineView(sugarServerInfos);
				break;
			}
		}
	};

	private void addCubicLineView(ArrayList<BloodSugarInfo> sugarInfos) {
		mFrameLayoutCubicLine.removeAllViews();
		mCubicLineUtils = new BloodSugarLineUtils(BloodSugarLineActivity.this, sugarInfos,minValue,maxBeforeMealValue,maxAfterMealValue);
		
		mFrameLayoutCubicLine.addView(mCubicLineUtils.initCubicLineGraphView());
	}

	protected void writeToDB() {
		new Thread() {
			public void run() {
				DataBaseHelper helper = new DataBaseHelper(App.getContext());
				SQLiteDatabase db = helper.getWritableDatabase();
				try {
					db.beginTransaction();
					for (BloodSugarInfo info : sugarServerInfos) {
						String sql = "REPLACE INTO bsinfo VALUES(" + info.getMeasureTime() + "," + App.USERID + "," + info.getBsValue() + "," + info.getMeasureType() + ",1);";
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

	protected void getDBData() {
		new Thread() {
			public void run() {
				DataBaseHelper helper = new DataBaseHelper(App.getContext());
				SQLiteDatabase db = helper.getWritableDatabase();
				// TODO
				Cursor cursor = db.query("bsinfo", null, "measureTime>? AND measureTime<?", new String[] { 0 + "", 100000000L + "" }, null, null, "measureTime asc");
				while (cursor.moveToNext()) {
					sugarLocInfos.add(new BloodSugarInfo(cursor.getLong(0), cursor.getDouble(2), cursor.getInt(3)));
				}
				handler.sendEmptyMessage(-1);
			}
		}.start();
	}

	StringBuilder url;
	int responseCode = -1;
	private TextView tv_month;
	private TextView tv_week;
	private TextView tv_day;
	private TextView tv_start;
	private TextView tv_end;
	private TextView tv_table;

	private void reqData() {
		tv_start.setText(TimeUtil.getStringTime(startTime));
		tv_end.setText(TimeUtil.getStringTime(endTime));

//		MaterialUtil.showProgressDialog(BloodSugarLineActivity.this);
		
		url = new StringBuilder(App.BASE + Constant.BLOODSUGAR_HISTORY);
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

//		Log.wtf("获取血糖信息URL", url.toString());

		App.getRequestQueue().add(getreqData());
	}

	StringRequest getreqData() {
		return new StringRequest(Method.GET, url.toString(), new Listener<String>() {
			@Override
			public void onResponse(String res) {
//				Log.wtf("获取血糖信息返回结果", res);
				try {
					JSONObject response = new JSONObject(res);

					responseCode = response.getInt("code");
					if (responseCode != 211) {
						handler.sendEmptyMessage(Constant.GET_DATA_SERVER_ERROR);
						return;
					}
					JSONArray array = response.optJSONArray("bloodSuggerInfo");
					if (array == null) {
						handler.sendEmptyMessage(Constant.GET_DATA_NULL);
						return;
					}
					sugarServerInfos.clear();
					for (int i = 0; i < array.length(); i++) {
						JSONObject object = (JSONObject) array.get(i);
						BloodSugarInfo sugarInfo = new BloodSugarInfo(object.getLong("measureTime"), object.getDouble("bsValue"), object.getInt("measureType"));
						sugarServerInfos.add(sugarInfo);
					}
					
					JSONObject targetJson = response.optJSONObject("healthLine");
					maxAfterMealValue = targetJson.getDouble("maxAfterMealValue");
					maxBeforeMealValue = targetJson.getDouble("maxBeforeMealValue");
					minValue = targetJson.getDouble("minValue");
					
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_bsinfo_month:
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
		case R.id.iv_bsinfo_week:
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
		case R.id.iv_bsinfo_day:
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
		case R.id.tv_bsinfo_start:
			showDatePickerDialog("请选择开始日期", tv_start);
			break;
		case R.id.tv_bsinfo_end:
			showDatePickerDialog("请选择截至日期", tv_end);
			break;
		case R.id.tv_bsinfo_table:
			Intent intent = new Intent(this,BloodSugarTableActivity.class);
			intent.putExtra("sugarInfos", sugarServerInfos);
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
		int month = Integer.parseInt(string.substring(string.indexOf("年")+1, string.indexOf("月"))) - 1;
		int day = Integer.parseInt(string.substring(string.indexOf("月")+1, string.indexOf("日")));
		datePicker.init(year, month, day, null);
		builder.setView(view);
		builder.setTitle(title);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				String time = datePicker.getYear() + "年" + (datePicker.getMonth() + 1) + "月" + datePicker.getDayOfMonth() + "日 00:00";
				if (!"请选择开始日期".equals(title) && TimeUtil.getLongTime(time)<startTime) {
					MyToast.show("选择结束日期不能小于开始日期");
					return ;
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
