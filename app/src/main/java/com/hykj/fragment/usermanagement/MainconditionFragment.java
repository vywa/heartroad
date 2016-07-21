package com.hykj.fragment.usermanagement;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hykj.App;
import com.hykj.Constant;
import com.hykj.R;
import com.hykj.entity.BloodSugarInfo;
import com.hykj.entity.TargetBloodSugar;
import com.hykj.utils.TimeUtil;
import com.hykj.view.BSCircleView;
import com.hykj.view.HistogramView;

/**
 * @author 作者：赵宇
 * @version 1.0 创建时间：2015年11月2日 下午1:35:40 类说明：身体状况评估界面
 */
@SuppressLint("ValidFragment")
public class MainconditionFragment extends Fragment implements OnClickListener {
	private BSCircleView circleView;
	private Button mBtn_mesure;
	private ArrayList<BloodSugarInfo> arr = new ArrayList<BloodSugarInfo>();
	private View v;
	private StringBuilder url;
	private int responseCode = -1;
	private TargetBloodSugar target;

	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case Constant.GET_DATA_SUCCESS:
				registAndSetting();
				if (arr.size() == 0) {
					circleView.setTargetValue(new BloodSugarInfo(0, 0, -1),
							target);
				} else {
					circleView.setTargetValue(arr.get(0), target);
				}
				break;
			case 0:
//				Log.wtf("aaa", (int[]) msg.obj + "");
				int[] datas = (int[]) msg.obj;
				histogramView.setProgress(datas);
				histogramView.invalidate();
				break;
			case 3:
				circleView.invalidate();
				histogramView.invalidate();
				break;
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			getActivity().getWindow().getDecorView()
					.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
		v = inflater.inflate(R.layout.fragment_main_condition, null);
		// getDataFromServer();
		init();

		// if (!firstVisable) {
		// reqData();
		// }

		return v;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (App.NEEDGET_BP) {
			getDataFromServer();
		}

		if (App.NEEDGET_BS) {
			Log.wtf("aaa","getBs");
			reqData();
		}
	}

	private void getDataFromServer() {
		RequestQueue rq = App.getRequestQueue();
		StringBuilder url = new StringBuilder(App.BASE
				+ Constant.BLOODPRESSURE_HOMEPAGE + "tocken=" + App.TOKEN);
//		Log.wtf("aaa", url.toString());
		rq.add(new StringRequest(Request.Method.GET, url.toString(),
				new Listener<String>() {

					@Override
					public void onResponse(String response) {
						try {
//							Log.wtf("获取最近一次血压信息结果", response);
							JSONObject json = new JSONObject(response);

							if ("211".equals(json.getString("code"))) {
								JSONArray bloodPressureInfo = json
										.optJSONArray("bloodPressureInfo");
								if (bloodPressureInfo != null
										&& bloodPressureInfo.length() > 0) {
									JSONObject o = bloodPressureInfo
											.getJSONObject(0);
									int highBP = o.getInt("highBP");
									int lowBP = o.getInt("lowBP");
//									Log.wtf("aaa", highBP + "");
									Message msg = mHandler.obtainMessage(0);
									msg.obj = new int[] { highBP, lowBP };
									msg.sendToTarget();
									App.NEEDGET_BS = false;
								}

							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						mHandler.sendEmptyMessage(1);

					}
				}));
		rq.start();

	}

	private HistogramView histogramView;
	private RelativeLayout timeaxes;
	private LinearLayout ll_time, ll_container;

	private void init() {
		histogramView = (HistogramView) v.findViewById(R.id.histogram);
		circleView = (BSCircleView) v.findViewById(R.id.main_condition_canvas);
		mBtn_mesure = (Button) v.findViewById(R.id.main_condition_mesure);
		mBtn_mesure.setOnClickListener(this);
		timeaxes = (RelativeLayout) v
				.findViewById(R.id.main_condition_timeaxes);
		ll_time = (LinearLayout) v.findViewById(R.id.main_condition_time);
		ll_container = (LinearLayout) v
				.findViewById(R.id.main_condition_container);

	}

	// @Override
	// public void setUserVisibleHint(boolean isVisibleToUser) {
	// super.setUserVisibleHint(isVisibleToUser);
	// if (isVisibleToUser && firstVisable) {
	// reqData();
	// }
	// }

	@SuppressLint("NewApi")
	private void registAndSetting() {

		if (arr.size() == 0) {
			circleView.setTargetValue(null, target);
		} else {
			ImageView[] imgs = new ImageView[arr.size()];
			for (int i = 0; i < arr.size(); i++) {
				final int num;
				imgs[i] = new ImageView(getActivity());
				imgs[i].setImageResource(R.drawable.timepoint1);
				LayoutParams params = new LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
				@SuppressWarnings("deprecation")
				int width = getActivity().getWindowManager()
						.getDefaultDisplay().getWidth();// 屏幕宽度
				int timeWidth = width - 10 - (width * 22 / 645);// 时间轴宽度
				final long time = arr.get(i).getMeasureTime();
				String hour = TimeUtil.getHour(time);// 时间转换成小时
//				Log.wtf("aaa", hour);
				params.setMargins(Integer.parseInt(hour) * (timeWidth / 24), 0,
						0, 0);
				imgs[i].setLayoutParams(params);
				timeaxes.addView(imgs[i]);
				imgs[i].setClickable(true);
				num = i;
				imgs[i].setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Toast.makeText(getActivity(),
								"测量时间：" + TimeUtil.getTime(time), 0).show();
						circleView.setTargetValue(arr.get(num), target);
					}
				});
			}
		}
	}

	private void reqData() {
		url = new StringBuilder(App.BASE + Constant.BLOODSUGAR_HOMEPAGE);
		url.append("tocken=" + App.TOKEN);

		Log.wtf("获取最近一次血糖信息的URL", url.toString());
		App.getRequestQueue().add(getreqData());
	}

	StringRequest getreqData() {
		return new StringRequest(Method.GET, url.toString(),
				new Listener<String>() {
					@Override
					public void onResponse(String res) {
						Log.wtf("获取最近一次血糖信息结果", res);
						try {
							JSONObject response = new JSONObject(res);

							responseCode = response.optInt("code", 0);
							if (responseCode != 211) {
								return;
							}
							JSONArray array = response
									.optJSONArray("bloodSuggerInfo");
							if (array == null) {
								return;
							}
							arr.clear();
							for (int i = 0; i < array.length(); i++) {
								JSONObject object = (JSONObject) array.get(i);
								BloodSugarInfo sugarInfo = new BloodSugarInfo(
										object.getLong("measureTime"), object
												.getDouble("bsValue"), object
												.getInt("measureType"));
								arr.add(sugarInfo);
							}
//							Log.wtf("sugar", arr.size() + "");
							JSONObject json = response
									.optJSONObject("healthTarget");
							if (json != null) {
								String afterBreakfast = json
										.optString("afterBreakfast");
								String afterLunch = json
										.optString("afterLunch");
								String afterSupper = json
										.optString("afterSupper");
								String beforeBreakfast = json
										.optString("beforeBreakfast");
								String beforeLunch = json
										.optString("beforeLunch");
								String beforeSleep = json
										.optString("beforeSleep");
								String beforeSupper = json
										.optString("beforeSupper");
								String random = json.optString("random");
								String zero = json.optString("zero");
								target = new TargetBloodSugar(afterBreakfast,
										afterLunch, afterSupper,
										beforeBreakfast, beforeLunch,
										beforeSleep, beforeSupper, random, zero);
							} else {
								target = new TargetBloodSugar("0.0", "0.0",
										"0.0", "0.0", "0.0", "0.0", "0.0",
										"0.0", "0.0");
							}
							App.NEEDGET_BS = false;
							mHandler.sendEmptyMessage(Constant.GET_DATA_SUCCESS);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
					}
				});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.main_condition_mesure:
			if (histogramView.isShown()) {
				mBtn_mesure.setBackgroundResource(R.drawable.sugar_pressure);
				circleView.setVisibility(View.VISIBLE);
				histogramView.setVisibility(View.GONE);
				ll_container.setVisibility(View.VISIBLE);
				ll_time.setVisibility(View.VISIBLE);

			} else {
				mBtn_mesure.setBackgroundResource(R.drawable.pressure_sugar);
				circleView.setVisibility(View.GONE);
				histogramView.setVisibility(View.VISIBLE);
				ll_container.setVisibility(View.GONE);
				ll_time.setVisibility(View.GONE);

			}
			break;

		default:
			break;
		}
	}

}
