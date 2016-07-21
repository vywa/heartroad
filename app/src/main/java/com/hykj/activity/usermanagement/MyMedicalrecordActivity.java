package com.hykj.activity.usermanagement;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.hykj.adapter.MymedicalRecordAdapter;
import com.hykj.entity.MedicalRecord;
import com.hykj.utils.MyProgress;
import com.hykj.utils.MyToast;

/**
 * @author 作者 : zhaoyu
 * @version 创建时间：2016年4月15日 上午11:34:52 类说明：我的病历
 */
public class MyMedicalrecordActivity extends BaseActivity {
	private ImageView mImg_back;
	private TextView mTv_medical, mTv_form, mTv_report, mTv_other;
	private ListView mLv;
	private List<MedicalRecord> data = new ArrayList<MedicalRecord>();
	private List<MedicalRecord> data1 = new ArrayList<MedicalRecord>();
	private List<MedicalRecord> data2 = new ArrayList<MedicalRecord>();
	private List<MedicalRecord> data3 = new ArrayList<MedicalRecord>();
	private List<MedicalRecord> data4 = new ArrayList<MedicalRecord>();
	private MymedicalRecordAdapter adapter;

	int type = 1;

	@Override
	public void init() {
		setContentView(R.layout.activity_mymedicalrecord);
		initViews();
	}

	private void initViews() {
		mLv = (ListView) findViewById(R.id.mymedical_lv);
		mImg_back = (ImageView) findViewById(R.id.mymedical_imgv_back);
		mImg_back.setOnClickListener(this);
		mTv_medical = (TextView) findViewById(R.id.mymedical_tv_medicalrecord);
		mTv_form = (TextView) findViewById(R.id.mymedical_tv_form);
		mTv_report = (TextView) findViewById(R.id.mymedical_tv_report);
		mTv_other = (TextView) findViewById(R.id.mymedical_tv_other);
		mTv_medical.setSelected(true);
		mTv_medical.setOnClickListener(this);
		mTv_form.setOnClickListener(this);
		mTv_report.setOnClickListener(this);
		mTv_other.setOnClickListener(this);
		getDataFromServer();
		adapter = new MymedicalRecordAdapter(data, MyMedicalrecordActivity.this);
		mLv.setAdapter(adapter);
		mLv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				MedicalRecord record = data.get(position);
				Intent intent = new Intent(MyMedicalrecordActivity.this, MedicalRecordDetailActivity.class);
				intent.putExtra("record", record);
				MyMedicalrecordActivity.this.startActivity(intent);
			}
		});
	}

	private void getDataFromServer() {
		MyProgress.show(this,R.layout.progressdialog_getcode);

		RequestQueue rq = App.getRequestQueue();
		StringBuilder url = new StringBuilder(App.BASE
				+ Constant.MEDICALRECORDQUARY + "tocken=" + App.TOKEN);
//		Log.wtf("咨询请求url", url.toString());

		rq.add(new StringRequest(Request.Method.GET, url.toString(),
				new Listener<String>() {

					@Override
					public void onResponse(String response) {
						data1.clear();
						try {
							JSONObject o = new JSONObject(response);
							if ("206".equals(o.getString("code"))) {
								JSONArray array = o.optJSONArray("recordList");
								if (array != null) {
									for (int i = 0; i < array.length(); i++) {
										JSONObject o1 = array.optJSONObject(i);
										String date = o1
												.optString("recordTime");
										String describe = o1
												.optString("content");
										int id = o1.optInt("id");
										String type = o1.optString("type");
										MedicalRecord diet = new MedicalRecord();
										diet.setContent(describe);
										diet.setRecordTime(date);
										diet.setType(type);
										JSONArray arr = o1
												.optJSONArray("imageList");
										if (arr != null) {
											for (int j = 0; j < arr.length(); j++) {
												diet.setImg(arr.getString(j));
											}
										}
										data1.add(diet);

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
					MyProgress.dismiss();
					if (type == 1) {
						data.clear();
						data.addAll(data1);
					} else if (type == 2) {
						data.clear();
						data.addAll(data2);
					} else if (type == 3) {
						data.clear();
						data.addAll(data3);
					} else if (type == 4) {
						data.clear();
						data.addAll(data4);
					}
					adapter.notifyDataSetChanged();

					break;
				case 1:
					MyProgress.dismiss();
					MyToast.show("暂无饮食数据，添加后查看");
					break;
				case 2:
					MyProgress.dismiss();
					MyToast.show("网络连接错误");
				default:
					break;
			}
		}
	};

	@Override
	public void click(View v) {
		switch (v.getId()) {
			case R.id.mymedical_imgv_back:
				finish();

				break;
			case R.id.mymedical_tv_medicalrecord:
				mTv_medical.setBackgroundResource(R.drawable.uploadmr_bg);
				mTv_form.setBackgroundResource(R.drawable.shape_corner_rectangle);
				mTv_report
						.setBackgroundResource(R.drawable.shape_corner_rectangle);
				mTv_other
						.setBackgroundResource(R.drawable.shape_corner_rectangle1);

				type = 1;
				getDataFromServer();
				break;
			case R.id.mymedical_tv_form:
				mTv_medical
						.setBackgroundResource(R.drawable.shape_corner_rectangle);
				mTv_form.setBackgroundResource(R.drawable.uploadmr_bg);
				mTv_report
						.setBackgroundResource(R.drawable.shape_corner_rectangle);
				mTv_other
						.setBackgroundResource(R.drawable.shape_corner_rectangle1);
				type = 2;
				mHandler.sendEmptyMessage(0);
				break;
			case R.id.mymedical_tv_report:
				mTv_medical
						.setBackgroundResource(R.drawable.shape_corner_rectangle);
				mTv_form.setBackgroundResource(R.drawable.shape_corner_rectangle);
				mTv_report.setBackgroundResource(R.drawable.uploadmr_bg);
				mTv_other
						.setBackgroundResource(R.drawable.shape_corner_rectangle1);

				type = 3;
				mHandler.sendEmptyMessage(0);
				break;
			case R.id.mymedical_tv_other:
				mTv_medical
						.setBackgroundResource(R.drawable.shape_corner_rectangle);
				mTv_form.setBackgroundResource(R.drawable.shape_corner_rectangle);
				mTv_report
						.setBackgroundResource(R.drawable.shape_corner_rectangle);
				mTv_other
						.setBackgroundResource(R.drawable.uploadmr_bg);

				type = 4;
				mHandler.sendEmptyMessage(0);
				break;
		}
	}

}
