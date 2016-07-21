package com.hykj.view;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import android.app.Dialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.gc.materialdesign.views.ButtonFlat;
import com.hykj.R;
import com.hykj.entity.CityModel;
import com.hykj.entity.DistrictModel;
import com.hykj.entity.ProvinceModel;
import com.hykj.service.XmlParserHandler;
import com.hykj.view.spinnerwheel.AbstractWheel;
import com.hykj.view.spinnerwheel.OnWheelChangedListener;
import com.hykj.view.spinnerwheel.WheelVerticalView;
import com.hykj.view.spinnerwheel.adapters.ArrayWheelAdapter;

public class ChooseCityDialog extends Dialog implements OnWheelChangedListener,android.view.View.OnClickListener{

	Context context;
	View view;
	View backView;

	public ChooseCityDialog(Context context) {
		super(context, android.R.style.Theme_Translucent);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_choosecity);

		findViews();
		initProvinceDatas();
		registAndSettings();
		updateCities();
		updateAreas();
	}

	private void registAndSettings() {
		wvv_province.setPadding(0, 0, 0, 0);
		wvv_city.setPadding(0, 0, 0, 0);
		wvv_district.setPadding(0, 0, 0, 0);

		wvv_province.addChangingListener(this);
		wvv_city.addChangingListener(this);
		wvv_district.addChangingListener(this);

		wvv_province.setVisibleItems(7);
		wvv_city.setVisibleItems(7);
		wvv_district.setVisibleItems(7);

		ArrayWheelAdapter<String> province_adapter = new ArrayWheelAdapter<String>(context, mProvinceDatas);
		province_adapter.setItemResource(R.layout.choose_city_wheel_text);
		province_adapter.setItemTextResource(R.id.text);
		wvv_province.setViewAdapter(province_adapter);
		wvv_province.setCurrentItem(0);
	}

	private void findViews() {
		wvv_province = (WheelVerticalView) findViewById(R.id.wv_chcity_province);
		wvv_city = (WheelVerticalView) findViewById(R.id.wv_chcity_city);
		wvv_district = (WheelVerticalView) findViewById(R.id.wv_chcity_district);
		
		view = findViewById(R.id.contentDialog);
		backView = findViewById(R.id.dialog_rootView);
		
		Button cancel = (Button) findViewById(R.id.button_cancel);
		cancel.setOnClickListener(this);
		Button accept = (Button) findViewById(R.id.button_accept);
		accept.setOnClickListener(this);
	}

	@Override
	public void show() {
		
		// TODO 自动生成的方法存根
		super.show();
		// set dialog enter animations
		view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.dialog_main_show_amination));
		backView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.dialog_root_show_amin));
	}
	@Override
	public void dismiss() {
		Animation anim = AnimationUtils.loadAnimation(context, R.anim.dialog_main_hide_amination);
		anim.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				view.post(new Runnable() {
					@Override
					public void run() {
			        	ChooseCityDialog.super.dismiss();
			        }
			    });
			}
		});
		//Animation backAnim = AnimationUtils.loadAnimation(context, R.anim.dialog_root_hide_amin);
		
		view.startAnimation(anim);
		//backView.startAnimation(backAnim);
	}
	
	private AcceptButtonClickListener onAcceptButtonClickListener;
	
	public interface AcceptButtonClickListener{
		void onClick(String province,String city,String district);
	}
	public void addAcceptButton( AcceptButtonClickListener onAcceptButtonClickListener){
		this.onAcceptButtonClickListener = onAcceptButtonClickListener;
	}
	/**
	 * 所有省
	 */
	protected String[] mProvinceDatas;
	/**
	 * key - 省 value - 市
	 */
	protected Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();
	/**
	 * key - 市 values - 区
	 */
	protected Map<String, String[]> mDistrictDatasMap = new HashMap<String, String[]>();

	/**
	 * key - 区 values - 邮编
	 */
	protected Map<String, String> mZipcodeDatasMap = new HashMap<String, String>();

	/**
	 * 当前省的名称
	 */
	protected String mCurrentProviceName;
	/**
	 * 当前市的名称
	 */
	protected String mCurrentCityName;
	/**
	 * 当前区的名称
	 */
	protected String mCurrentDistrictName = "";

	/**
	 * 当前区的邮政编码
	 */
	protected String mCurrentZipCode = "";
	private WheelVerticalView wvv_province;
	private WheelVerticalView wvv_district;
	private WheelVerticalView wvv_city;

	/**
	 * 解析省市区的XML数据
	 */

	protected void initProvinceDatas() {
		List<ProvinceModel> provinceList = null;
		AssetManager asset = context.getAssets();
		try {
			InputStream input = asset.open("province_data.xml");
			// 创建一个解析xml的工厂对象
			SAXParserFactory spf = SAXParserFactory.newInstance();
			// 解析xml
			SAXParser parser = spf.newSAXParser();
			XmlParserHandler handler = new XmlParserHandler();
			parser.parse(input, handler);
			input.close();
			// 获取解析出来的数据
			provinceList = handler.getDataList();
			// */ 初始化默认选中的省、市、区
			if (provinceList != null && !provinceList.isEmpty()) {
				mCurrentProviceName = provinceList.get(0).getName();
				List<CityModel> cityList = provinceList.get(0).getCityList();
				if (cityList != null && !cityList.isEmpty()) {
					mCurrentCityName = cityList.get(0).getName();
					List<DistrictModel> districtList = cityList.get(0).getDistrictList();
					mCurrentDistrictName = districtList.get(0).getName();
					mCurrentZipCode = districtList.get(0).getZipcode();
				}
			}
			// */
			mProvinceDatas = new String[provinceList.size()];
			for (int i = 0; i < provinceList.size(); i++) {
				// 遍历所有省的数据
				mProvinceDatas[i] = provinceList.get(i).getName();
				List<CityModel> cityList = provinceList.get(i).getCityList();
				String[] cityNames = new String[cityList.size()];
				for (int j = 0; j < cityList.size(); j++) {
					// 遍历省下面的所有市的数据
					cityNames[j] = cityList.get(j).getName();
					List<DistrictModel> districtList = cityList.get(j).getDistrictList();
					String[] distrinctNameArray = new String[districtList.size()];
					DistrictModel[] distrinctArray = new DistrictModel[districtList.size()];
					for (int k = 0; k < districtList.size(); k++) {
						// 遍历市下面所有区/县的数据
						DistrictModel districtModel = new DistrictModel(districtList.get(k).getName(), districtList.get(k).getZipcode());
						// 区/县对于的邮编，保存到mZipcodeDatasMap
						mZipcodeDatasMap.put(districtList.get(k).getName(), districtList.get(k).getZipcode());
						distrinctArray[k] = districtModel;
						distrinctNameArray[k] = districtModel.getName();
					}
					// 市-区/县的数据，保存到mDistrictDatasMap
					mDistrictDatasMap.put(cityNames[j], distrinctNameArray);
				}
				// 省-市的数据，保存到mCitisDatasMap
				mCitisDatasMap.put(provinceList.get(i).getName(), cityNames);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {

		}
	}

	/**
	 * 根据当前的市，更新区WheelView的信息
	 */
	private void updateAreas() {
		int pCurrent = wvv_city.getCurrentItem();
		mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
		String[] areas = mDistrictDatasMap.get(mCurrentCityName);

		if (areas == null) {
			areas = new String[] { "" };
		}
		ArrayWheelAdapter<String> district_adapter = new ArrayWheelAdapter<String>(context, areas);
		district_adapter.setItemResource(R.layout.choose_city_wheel_text);
		district_adapter.setItemTextResource(R.id.text);
		wvv_district.setViewAdapter(district_adapter);
		wvv_district.setCurrentItem(0);
		mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[wvv_district.getCurrentItem()];
	}

	/**
	 * 根据当前的省，更新市WheelView的信息
	 */
	private void updateCities() {
		int pCurrent = wvv_province.getCurrentItem();
		mCurrentProviceName = mProvinceDatas[pCurrent];
		String[] cities = mCitisDatasMap.get(mCurrentProviceName);
		if (cities == null) {
			cities = new String[] { "" };
		}
		ArrayWheelAdapter<String> city_adapter = new ArrayWheelAdapter<String>(context, cities);
		city_adapter.setItemResource(R.layout.choose_city_wheel_text);
		city_adapter.setItemTextResource(R.id.text);
		wvv_city.setViewAdapter(city_adapter);
		wvv_city.setCurrentItem(0);
		updateAreas();
	}

	@Override
	public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
		if (wheel == wvv_province) {
			updateCities();
		} else if (wheel == wvv_city) {
			updateAreas();
		} else if (wheel == wvv_district) {
			mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[newValue];
			mCurrentZipCode = mZipcodeDatasMap.get(mCurrentDistrictName);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_cancel:
			dismiss();
			break;
		case R.id.button_accept:
			if (onAcceptButtonClickListener != null) {
//				Log.wtf("aaaaaa", mCurrentProviceName+mCurrentCityName+mCurrentDistrictName);
				onAcceptButtonClickListener.onClick(mCurrentProviceName,mCurrentCityName,mCurrentDistrictName);
			}
			dismiss();
			break;
		}
	}
}























