package com.hykj.activity.usermanagement;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hykj.App;
import com.hykj.Constant;
import com.hykj.R;
import com.hykj.entity.Doctor;
import com.hykj.entity.Hospital;
import com.hykj.utils.MyLog;
import com.hykj.utils.MyProgress;
import com.hykj.view.spinnerwheel.AbstractWheel;
import com.hykj.view.spinnerwheel.OnWheelScrollListener;
import com.hykj.view.spinnerwheel.WheelVerticalView;
import com.hykj.view.spinnerwheel.adapters.ArrayWheelAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HospitalList extends Activity {

	private ListView lv;

	private ArrayList<Hospital> hosptialList = new ArrayList<Hospital>();
	private ArrayList<Doctor> docList = new ArrayList<Doctor>();
	private ArrayList<Doctor> tempdocList = new ArrayList<Doctor>();
	private String[] hospitalNames = {};

	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();
	
	private StringBuilder url;

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				wvAdapter = new ArrayWheelAdapter<String>(HospitalList.this, hospitalNames);
				wvAdapter.setItemResource(R.layout.wheel_text_hospital);
				wvAdapter.setItemTextResource(R.id.text);
				wv_hosptial.setViewAdapter(wvAdapter);
				if (latitude != 0 && longitude != 0) {
					changeCurrent();
				}
				break;
			case 3:
				docList.clear();
				docList.addAll(tempdocList);

				MyProgress.dismiss();
				adapter.notifyDataSetChanged();
				break;
			case LOCATIONED:
				if (hosptialList.size() > 0) {
					changeCurrent();
				}
				break;
			}
		}
	};
	
	int nearlyIndex = 0;
	double length = Double.MAX_VALUE;

	private void changeCurrent() {
		mLocationClient.stop();
		
		for (int i = 0; i < hosptialList.size(); i++) {
			Hospital hospital = hosptialList.get(i);
			if (Math.sqrt(Math.pow(hospital.getLat()-latitude, 2) + Math.pow(hospital.getLng() - longitude, 2)) < length) {
				nearlyIndex = i;
				length = Math.sqrt(Math.pow(hospital.getLat()-latitude, 2) + Math.pow(hospital.getLng() - longitude, 2));
			}
		}

		wv_hosptial.myScroll(nearlyIndex,1000);
	}

	private WheelVerticalView wv_hosptial;

	private ArrayWheelAdapter<String> wvAdapter;
	BaseAdapter adapter;

	private ImageView iv_back;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());
		
		setContentView(R.layout.activity_hospitallist);
		location();
		
		iv_back = (ImageView) findViewById(R.id.iv_title_back);
		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("选择医生");
		iv_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				HospitalList.this.finish();
			}
		});

		lv = (ListView) findViewById(R.id.lv_hl);

		adapter = new BaseAdapter() {
			@Override
			public View getView(final int position, View convertView, ViewGroup parent) {
				ViewHolder holder;
				if (convertView == null) {
					convertView = View.inflate(App.getContext(), R.layout.item_doclist, null);
					holder = new ViewHolder();
					holder.civ = (SimpleDraweeView) convertView.findViewById(R.id.niv_docList_photo);
					holder.tv_name = (TextView) convertView.findViewById(R.id.tv_docList_name);
					convertView.setTag(holder);
				} else {
					holder = (ViewHolder) convertView.getTag();
				}
				Doctor doctor = docList.get(position);

				holder.tv_name.setText(doctor.getName());

				String iconUrl = doctor.getIconUrl();
				if (TextUtils.isEmpty(iconUrl)){
					iconUrl = App.DEFULT_PHOTO;
				}
//				Picasso.with(App.getContext()).load(iconUrl).into(holder.civ);
				holder.civ.setImageURI(Uri.parse(iconUrl));
				return convertView;
			}

			@Override
			public long getItemId(int position) {
				return 0;
			}

			@Override
			public Object getItem(int position) {
				return null;
			}

			@Override
			public int getCount() {
				return docList.size();
			}
		};

		lv.setAdapter(adapter);

		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(HospitalList.this, DoctorDetailActivity.class);
				intent.putExtra("doctor", docList.get(position));
				startActivity(intent);
				HospitalList.this.finish();
			}
		});

		wv_hosptial = (WheelVerticalView) findViewById(R.id.wv_hosptial);

		wv_hosptial.addScrollingListener(new OnWheelScrollListener() {
			@Override
			public void onScrollingStarted(AbstractWheel wheel) {
			}

			@Override
			public void onScrollingFinished(AbstractWheel wheel) {
//				MyProgress.show(HospitalList.this);
				url = new StringBuilder(App.BASE + Constant.DOCTOR_LIST);
				url.append("data=");
				JSONObject jsonObject = new JSONObject();
				try {
					jsonObject.put("hospitalId", hosptialList.get(wv_hosptial.getCurrentItem()).getId());
				} catch (JSONException e) {
					e.printStackTrace();
				}
				url.append(jsonObject.toString());
				MyLog.wtf("获取医生列表请求url", url.toString());
				App.getRequestQueue().add(getDocList());
			}
		});

		MyProgress.show(HospitalList.this);
		App.getRequestQueue().add(getHospitalList());
	}

	private void location() {
		mLocationClient = new LocationClient(getApplicationContext()); // 声明LocationClient类
		mLocationClient.registerLocationListener(myListener); // 注册监听函数

		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// 设置定位模式
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
		option.setScanSpan(Integer.MAX_VALUE);// 设置发起定位请求的间隔时间为20000ms
		option.setIsNeedAddress(false);// 返回的定位结果包含地址信息
		option.setNeedDeviceDirect(false);// 返回的定位结果包含手机机头的方向
		mLocationClient.setLocOption(option);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		mLocationClient.start();
	}

	double latitude = 0;
	double longitude = 0;
	final int LOCATIONED = 4;
	
	class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation result) {
			latitude = result.getLatitude();
			longitude = result.getLongitude();
//			Log.wtf("xxxxxxxxxxx", latitude+"------------"+longitude);
			handler.sendEmptyMessage(LOCATIONED);
		}
	}
	
	StringRequest getDocList() {
		return new StringRequest(Method.GET, url.toString(), new Listener<String>() {
			@Override
			public void onResponse(String res) {
				MyLog.wtf("医生列表返回结果", res);
				tempdocList.clear();
				try {
					JSONArray array = new JSONArray(res);
					for (int i = 0; i < array.length(); i++) {
						JSONObject object = (JSONObject) array.get(i);

						Doctor doctor = new Doctor();
						doctor.setUserId(object.optInt("userId"));
						doctor.setSex(object.optString("sex"));
						doctor.setPrifile(object.optString("resume"));
						doctor.setName(object.optString("name"));
						doctor.setIconUrl(object.optString("image"));
						doctor.setHospitalName(object.optString("shortName"));
						doctor.setAge(object.optString("age"));
						doctor.setFriend(false);

						tempdocList.add(doctor);
					}
					handler.sendEmptyMessage(3);
				} catch (JSONException e) {
					e.printStackTrace();
					handler.sendEmptyMessage(3);
				}
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				handler.sendEmptyMessage(3);
			}
		});
	}




	StringRequest getHospitalList() {
		url = new StringBuilder(App.BASE + Constant.HOSPITAL_LIST);
		return new StringRequest(Method.GET, url.toString(), new Listener<String>() {
			@Override
			public void onResponse(String res) {
				MyLog.wtf("医院列表返回结果", res);
				try {
					JSONArray array = new JSONArray(res);
					for (int i = 0; i < array.length(); i++) {
						JSONObject object = (JSONObject) array.get(i);
						hosptialList.add(new Hospital(object.optString("address"), object.optString("shortName"), object.optString("id"), object.optDouble("longitude"), object.optDouble("latitude")));
					}
					hospitalNames = new String[hosptialList.size()];
					for (int i = 0; i < hosptialList.size(); i++) {
						hospitalNames[i] = hosptialList.get(i).getHospitalName();
					}
					handler.sendEmptyMessage(0);
				} catch (JSONException e) {
					e.printStackTrace();
					handler.sendEmptyMessage(0);
				}
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				handler.sendEmptyMessage(0);
			}
		});
	}

	class ViewHolder {
		SimpleDraweeView civ;
		TextView tv_name;
	}

}
