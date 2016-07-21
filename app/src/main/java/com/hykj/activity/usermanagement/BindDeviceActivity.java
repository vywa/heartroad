package com.hykj.activity.usermanagement;

import android.app.Activity;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hykj.App;
import com.hykj.Constant;
import com.hykj.R;
import com.hykj.utils.MyLog;
import com.hykj.utils.MyProgress;
import com.hykj.utils.MyToast;
import com.hykj.utils.NetUtils;
import com.wukoon.api.Sniffer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BindDeviceActivity extends Activity implements View.OnClickListener {

	private String sn;
	private Button bt1;
	private Button bt2;
	StringBuilder url;
	String description;
	private Button bt_unbind;
	private Button bt_connect;
	private EditText et_pwd;
	private Sniffer sniffer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bind_device);

//		Bundle bundle = getIntent().getBundleExtra("bundle");

		String result = getIntent().getStringExtra("result");

		Log.wtf("",result);

		MyToast.show(result);

		if (!result.contains("SN")){
			return;
		}

		result = result.replace("[","{");
		result = result.replace("]","}");

		String s = result.substring(result.indexOf("{"), result.indexOf("}") + 1);
		try {
			JSONObject jsonObject = new JSONObject(s);

			sn = jsonObject.getString("SN");

		} catch (JSONException e) {
			e.printStackTrace();
		}


		bt1 = (Button) findViewById(R.id.bt_binddev_1);
		bt1.setOnClickListener(this);

		bt2 = (Button) findViewById(R.id.bt_binddev_2);
		bt2.setOnClickListener(this);

		bt_unbind = (Button) findViewById(R.id.bt_unbind);
		bt_unbind.setOnClickListener(this);

		et_pwd = (EditText) findViewById(R.id.et_wifipwd);

		bt_connect = (Button) findViewById(R.id.bt_connect);
		bt_connect.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		MyProgress.show(BindDeviceActivity.this);
		switch (view.getId()) {
			case R.id.bt_binddev_1:
				bindDevice(1);
				break;
			case R.id.bt_binddev_2:
				bindDevice(2);
				break;
			case R.id.bt_unbind:
				unbundDevice();
				break;
			case R.id.bt_connect:
				connectWIFI();
				break;
		}
	}

	private void connectWIFI() {
		String pwd = et_pwd.getText().toString().trim();
		String ssid = null;

		if (NetUtils.isWifi(App.getContext())){
			ssid = getConnectWifiSsid();
		}else{
			MyToast.show("手机wifi没有链接");
			return;
		}

		sniffer = Sniffer.getInstance();

		sniffer.setListener(new Sniffer.EventListener() {
			@Override
			public void onDeviceOnline(String s) {
				MyToast.show("绑定成功");
				MyProgress.dismiss();
			}
		});

		if (ssid.startsWith("\"")&&ssid.endsWith("\"")) {
			ssid = ssid.replace("\"","");
			ssid = ssid.replace("\"","");
		}

		MyProgress.show(BindDeviceActivity.this);
		sniffer.startSniffer(ssid,pwd,App.getContext());

		MyLog.wtf(ssid,pwd);
	}

	private String getConnectWifiSsid(){
		WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		return wifiInfo.getSSID();
	}

	private void unbundDevice() {
		url = new StringBuilder(App.BASE + Constant.DEVICE_UNBIND);
		url.append("tocken=" + App.TOKEN);
		MyLog.wtf("解除绑定设备的URL", url.toString());
		App.getRequestQueue().add(
				new StringRequest(Request.Method.GET, url.toString(),
						new Response.Listener<String>() {
							@Override
							public void onResponse(String res) {
								MyLog.wtf("解除绑定设备的返回结果", res);
								try {
									JSONObject json = new JSONObject(res);
									int responseCode = json.getInt("code");
									Message message = Message.obtain();
									if (responseCode != 206) {
										description = json.getString("message");
										handler.sendEmptyMessage(Constant.GET_DATA_SERVER_ERROR);
										return;
									}
									message.what = Constant.GET_DATA_SUCCESS;
									handler.sendMessage(message);
								} catch (JSONException e) {
									e.printStackTrace();
									handler.sendEmptyMessage(Constant.GET_DATA_ANALYZE_ERROR);
								}
							}
						}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						handler.sendEmptyMessage(Constant.GET_DATA_NETWORK_ERROR);
					}
				}
				));
	}


	private void bindDevice(int i) {
		url = new StringBuilder(App.BASE + Constant.DEVICE_BIND);
		url.append("tocken=" + App.TOKEN);
		url.append("&data=");
		JSONObject json = new JSONObject();
		try {
			json.put("serial", sn);
			json.put("user", i);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		url.append(json.toString());
		MyLog.wtf("绑定设备的URL", url.toString());
		App.getRequestQueue().add(
				new StringRequest(Request.Method.GET, url.toString(),
						new Response.Listener<String>() {
							@Override
							public void onResponse(String res) {
								MyLog.wtf("绑定设备的返回结果", res);
								try {
									JSONObject json = new JSONObject(res);
									int responseCode = json.getInt("code");
									Message message = Message.obtain();
									if (responseCode != 206) {
										description = json.getString("message");
										handler.sendEmptyMessage(Constant.GET_DATA_SERVER_ERROR);
										return;
									}
									message.what = Constant.GET_DATA_SUCCESS;
									handler.sendMessage(message);
								} catch (JSONException e) {
									e.printStackTrace();
									handler.sendEmptyMessage(Constant.GET_DATA_ANALYZE_ERROR);
								}
							}
						}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						handler.sendEmptyMessage(Constant.GET_DATA_NETWORK_ERROR);
					}
				}
				));
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			MyProgress.dismiss();
			switch (msg.what) {
				case Constant.GET_DATA_SUCCESS:
					MyToast.show("操作成功");
					finish();
					break;
				case Constant.GET_DATA_ANALYZE_ERROR:
					MyToast.show("解析数据失败");
					finish();
					break;
				case Constant.GET_DATA_NETWORK_ERROR:
					MyToast.show("解析数据失败");
					break;
				case Constant.GET_DATA_NULL:
					MyToast.show("获取列表为空");
					finish();
					break;
				case Constant.GET_DATA_SERVER_ERROR:
					MyToast.show(description);
					finish();
					break;
				case 0:
					MyToast.show("绑定失败");
					sniffer.stopSniffer();
					break;
			}
		}
	};


}























