package com.hykj.view;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hykj.App;
import com.hykj.R;
import com.hykj.activity.usermanagement.SettingActivity;
import com.hykj.utils.MyToast;
import com.hykj.view.spinnerwheel.WheelVerticalView;
import com.hykj.view.spinnerwheel.adapters.ArrayWheelAdapter;

/**
 * @author 作者 : zhaoyu
 * @version 创建时间：2016年1月21日 下午2:57:11 类说明
 */
public class SetPressureView extends Dialog implements
		android.view.View.OnClickListener {
	Context context;
	SettingActivity activity;

	private WheelVerticalView mWvv_high;
	private WheelVerticalView mWvv_low;
	private Button mBtn_sure, mBtn_cancel;
	private String[] datas;

	public SetPressureView(Context context, SettingActivity activity) {
		super(context);
		this.context = context;
		this.activity = activity;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.dialog_setpressure);
		initViews();
		setData();
	}

	/*
	 * 设置高低压值
	 */
	private void setData() {
		datas = new String[301];
		for (int i = 0; i <= 300; i++) {
			datas[i] = i + "";
		}
		mWvv_high.setVisibleItems(2);
		mWvv_low.setVisibleItems(2);
		ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<String>(
				context, datas);
		ArrayWheelAdapter<String> adapter1 = new ArrayWheelAdapter<String>(
				context, datas);
		adapter.setItemResource(R.layout.choose_city_wheel_text);
		adapter.setItemTextResource(R.id.text);
		adapter1.setItemResource(R.layout.choose_city_wheel_text);
		adapter1.setItemTextResource(R.id.text);
		mWvv_high.setViewAdapter(adapter);
		mWvv_low.setViewAdapter(adapter1);
		mWvv_high.setCurrentItem(120);
		mWvv_low.setCurrentItem(80);
	}

	/*
	 * 初始化控件
	 */
	private void initViews() {
		mWvv_high = (WheelVerticalView) findViewById(R.id.dialog_wvv_high);
		mWvv_low = (WheelVerticalView) findViewById(R.id.dialog_wvv_low);
		mBtn_sure = (Button) findViewById(R.id.dialog_pressure_sure);
		mBtn_cancel = (Button) findViewById(R.id.dialog_pressure_cancel);
		mBtn_sure.setOnClickListener(this);
		mBtn_cancel.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dialog_pressure_sure:
			sendToServer();

			break;
		case R.id.dialog_pressure_cancel:
			dismiss();
			break;
		default:
			break;
		}
	}

	private void sendToServer() {
		RequestQueue rq = App.getRequestQueue();
		StringBuilder url = new StringBuilder(App.BASE
				+ "healthTarget/setBloodPressure?");
		JSONObject json = new JSONObject();
		url.append("data=");
		try {
			json.put("targetHighBloodPressure", mWvv_high.getCurrentItem());
			json.put("targetLowBloodPressure", mWvv_low.getCurrentItem());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		url.append(json.toString());
		url.append("&tocken=" + App.TOKEN);
//		Log.wtf("pressrue", url.toString());
		rq.add(new StringRequest(Request.Method.GET, url.toString(),
				new Listener<String>() {

					@Override
					public void onResponse(String response) {
						try {
							JSONObject json = new JSONObject(response);
							String code = json.getString("code");
							if ("206".equals(code)) {
								mHandler.sendEmptyMessage(0);
							}
							if ("110".equals(code)) {
								mHandler.sendEmptyMessage(1);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						mHandler.sendEmptyMessage(-1);
					}
				}));
		rq.start();
	}

	public Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case -1:
				MyToast.show("网络连接失败！");
				break;
			case 0:
				MyToast.show("数据上传成功！");
				activity.mTv_pressure.setText("降压目标："
						+ "     "
						+ Html.fromHtml("<font color=#000000>"
								+ mWvv_high.getCurrentItem() + "~"
								+ mWvv_low.getCurrentItem() + "</font>"));
				SetPressureView.this.dismiss();
				break;
			case 1:
				MyToast.show("数据上传失败！");
				break;

			default:
				break;
			}
		}
	};
}
