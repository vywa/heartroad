package com.hykj.activity.usermanagement;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hykj.App;
import com.hykj.Constant;
import com.hykj.R;
import com.hykj.activity.BaseActivity;
import com.hykj.entity.Prescription;

/**
 * @author 作者 : zhaoyu
 * @version 创建时间：2016年4月19日 下午5:32:33 类说明：我的处方
 */
public class MyPrescriptionActivity extends BaseActivity {
	private ImageView mImg_back;
	private TextView mTv_medical1, mTv_medical3;// 药品名称
	private TextView mTv_method1, mTv_method3;// 服用方式
	private TextView mTv_times1, mTv_times3;// 服用频率
	private TextView mTv_amount1, mTv_amount3;// 每顿用量
	private TextView mTv_total1, mTv_total3;// 总量
	private List<Prescription> datas;

	@Override
	public void init() {
		setContentView(R.layout.activity_myprescription);
		initViews();
		getDataFromServer();
	}

	/*
	 * 获取处方
	 */
	private void getDataFromServer() {
		RequestQueue rq = App.getRequestQueue();
		StringBuilder url = null;
		try {
			url = new StringBuilder(App.BASE + Constant.REVIEWPRESCRIPTION);
			url.append("&tocken=" + App.TOKEN);
//			Log.wtf("aaaaaaa", url.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		rq.add(new StringRequest(Request.Method.GET, url.toString(),
				new Listener<String>() {
					@Override
					public void onResponse(String response) {
						try {
							JSONObject o = new JSONObject(response);// 整个JSONOject
							String code = o.getString("code");
							if ("206".equals(code)) {
								JSONArray array = o.optJSONArray("recipeList");
								if (array.length() > 0) {
									for (int i = 0; i < array.length(); i++) {
										JSONObject o1 = array.optJSONObject(i);
										String medicalName = o1
												.optString("medicineName");
										String method = "口服";
										String frequence = o1
												.optString("frequence");
										int totalCount = o1
												.optInt("totalCount");
										int dosage = o1.optInt("dosage");
										Prescription data = new Prescription(
												medicalName, frequence, method,
												dosage, totalCount);
										if (!datas.contains(data)) {
											datas.add(data);
										}
									}
									mHandler.sendEmptyMessage(0);
								}

							} else {
								mHandler.sendEmptyMessage(1);
							}

						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						mHandler.sendEmptyMessage(2);
					}
				}));
		rq.start();
	}

	public Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				if (datas.size() >= 2) {
					mTv_medical1.setText(datas.get(1).getMedicalname());
					mTv_times1.setText(datas.get(1).getFrequency());
					mTv_method1.setText(datas.get(1).getMethod());
					mTv_amount1.setText(datas.get(1).getMeal() + "片");
					mTv_total1.setText(datas.get(1).getAll() + "片");
					mTv_medical3.setText(datas.get(0).getMedicalname());
					mTv_times3.setText(datas.get(0).getFrequency());
					mTv_method3.setText(datas.get(0).getMethod());
					mTv_amount3.setText(datas.get(0).getMeal() + "片");
					mTv_total3.setText(datas.get(0).getAll() + "片");
				} else {
					mTv_medical1.setText(datas.get(0).getMedicalname());
					mTv_times1.setText(datas.get(0).getFrequency());
					mTv_method1.setText(datas.get(0).getMethod());
					mTv_amount1.setText(datas.get(0).getMeal() + "片");
					mTv_total1.setText(datas.get(0).getAll() + "片");
				}
				break;

			default:
				break;
			}
		}
	};

	private void initViews() {
		mTv_medical1 = (TextView) findViewById(R.id.myprescription_tv_medical1);
		mTv_medical3 = (TextView) findViewById(R.id.myprescription_tv_medical3);
		mTv_method1 = (TextView) findViewById(R.id.myprescription_tv_method1);
		mTv_method3 = (TextView) findViewById(R.id.myprescription_tv_method3);
		mTv_times1 = (TextView) findViewById(R.id.myprescription_tv_times1);
		mTv_times3 = (TextView) findViewById(R.id.myprescription_tv_times3);
		mTv_amount1 = (TextView) findViewById(R.id.myprescription_tv_amount1);
		mTv_amount3 = (TextView) findViewById(R.id.myprescription_tv_amount3);
		mTv_total1 = (TextView) findViewById(R.id.myprescription_tv_sum1);
		mTv_total3 = (TextView) findViewById(R.id.myprescription_tv_sum3);
		mImg_back = (ImageView) findViewById(R.id.myprescription_imgv_back);
		mImg_back.setOnClickListener(this);
		datas = new ArrayList<Prescription>();
	}

	@Override
	public void click(View v) {
		switch (v.getId()) {
		case R.id.myprescription_imgv_back:
			finish();
			break;

		default:
			break;
		}
	}

}
