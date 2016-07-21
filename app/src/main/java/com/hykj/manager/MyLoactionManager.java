package com.hykj.manager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.location.Address;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.hykj.R;
import com.hykj.utils.MyToast;

public class MyLoactionManager extends Activity {
	private MapView mapview;

	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();
	String locInfo = null;
	double latitude = 0;
	double longitude = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());

		setContentView(R.layout.activity_subjectnew_myloaction);
		
		init();

		location();
	}

	BaiduMap baiduMap;

	private void init() {
		Intent intent = getIntent();
		locInfo = intent.getStringExtra("locInfo");
		latitude = intent.getDoubleExtra("latitude", 0);
		longitude = intent.getDoubleExtra("longitude", 0);
		
		ImageView iv_back = (ImageView) findViewById(R.id.iv_title_back);
		iv_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MyLoactionManager.this.finish();
			}
		});
		
		Button bt_confirm = (Button) findViewById(R.id.bt_subnew_loc);
		bt_confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("locInfo", locInfo);
				intent.putExtra("latitude", latitude);
				intent.putExtra("longitude", longitude);
				setResult(-20, intent);

				MyLoactionManager.this.finish();
			}
		});
		
		tv_loc = (TextView) findViewById(R.id.tv_locinfo);
		tv_loc.setText(locInfo);
		
		mapview = (MapView) findViewById(R.id.mapview);
		baiduMap = mapview.getMap();// 通过Mapview获取BaiduMap对象

		// 缩放级别放到最大
		baiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(18));

		baiduMap.setOnMapStatusChangeListener(new OnMapStatusChangeListener() {

			@Override
			public void onMapStatusChangeStart(MapStatus arg0) {
			}

			@Override
			public void onMapStatusChangeFinish(MapStatus arg0) {
				LatLng ll = arg0.target;
				geoCoderLng(ll);
			}

			@Override
			public void onMapStatusChange(MapStatus arg0) {

			}
		});
	}

	PoiSearch poiSearch;

	private TextView tv_loc;

	void geoCoderLng(LatLng lng) {
		// 创建地理编码检索实例
		GeoCoder geoCoder = GeoCoder.newInstance();
		//
		OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {
			// 反地理编码查询结果回调函数
			@Override
			public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
				if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
					// 没有检测到结果
					MyToast.show("抱歉，未能找到结果");
					locInfo = "";
				}
				locInfo = result.getAddress();
				tv_loc.setText(locInfo);
			}

			// 地理编码查询结果回调函数
			@Override
			public void onGetGeoCodeResult(GeoCodeResult result) {
				if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
					// 没有检测到结果
				}
			}
		};
		// 设置地理编码检索监听者
		geoCoder.setOnGetGeoCodeResultListener(listener);
		//
		geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(lng));
		// 释放地理编码检索实例
		// geoCoder.destroy();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapview.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mapview.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mapview.onPause();
	}

	@Override
	protected void onStart() {
		super.onStart();
		mLocationClient.start();
	}

	@Override
	protected void onStop() {
		super.onStop();
		mLocationClient.stop();
	}

	private void location() {
		mLocationClient = new LocationClient(getApplicationContext()); // 声明LocationClient类
		mLocationClient.registerLocationListener(myListener); // 注册监听函数

		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// 设置定位模式
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
		option.setScanSpan(Integer.MAX_VALUE);// 设置发起定位请求的间隔时间为20000ms
		option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
		option.setNeedDeviceDirect(true);// 返回的定位结果包含手机机头的方向
		mLocationClient.setLocOption(option);
		/**
		 * BitmapDescriptor customMarker 用户自定义定位图标 boolean enableDirection
		 * 是否允许显示方向信息 MyLocationConfiguration.LocationMode locationMode 定位图层显示方式
		 */
		MyLocationConfiguration locationConfig = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.FOLLOWING, true, null);
		baiduMap.setMyLocationConfigeration(locationConfig);// 设置定位配置
		baiduMap.setMyLocationEnabled(true);// 打开定位图层-----不要忘记----定位属于另一个图层
	}

	class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation result) {
			MyLocationData locationData = new MyLocationData.Builder().latitude(result.getLatitude()).longitude(result.getLongitude()).build();
			baiduMap.setMyLocationData(locationData);
			Address address = result.getAddress();
			latitude = result.getLatitude();
			longitude = result.getLongitude();
			locInfo = address.address;
			tv_loc.setText(locInfo);
			// Toast.makeText(MyLoaction_NewSubActivity.this, address.address +
			// "", 0).show();
				// 1、实例化
				CircleOptions circleOptions = new CircleOptions();
				// 2、填充数据
				circleOptions.center(new LatLng(result.getLatitude(), result.getLongitude()));
				circleOptions.radius(200);
				circleOptions.fillColor(0x33000077);
				// 16进制：0x；透明度，红、绿、蓝：argb
				// circleOptions.stroke(new Stroke(5, 0x7700ff00));
				// 添加到地图上
				baiduMap.addOverlay(circleOptions);
		}
	}
}
