package com.hykj.fragment.usermanagement;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hykj.App;
import com.hykj.Constant;
import com.hykj.R;
import com.hykj.adapter.InfoPushAdapter;
import com.hykj.entity.PushDataEntity;
import com.hykj.utils.MyToast;

public class MainInfoFragment extends Fragment implements OnClickListener {
	private ListView mLv;
	private List<PushDataEntity> push_datas = new ArrayList<PushDataEntity>();
	private BaseAdapter infoPushAdapter;
	private View v;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		v = inflater.inflate(R.layout.activity_pushinfo, null);
		initView();
		return v;
	}

	private void initView() {
		mLv = (ListView) v.findViewById(R.id.pushinfo_lv);
		infoPushAdapter = new InfoPushAdapter(getActivity(), push_datas);
		mLv.setAdapter(infoPushAdapter);



	}

	@Override
	public void onResume() {
		super.onResume();
		initData();
	}

	@TargetApi(19)
	private void setTranslucentStatus(boolean on) {
		Window win = getActivity().getWindow();
		WindowManager.LayoutParams winParams = win.getAttributes();
		final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
		if (on) {
			winParams.flags |= bits;
		} else {
			winParams.flags &= ~bits;
		}
		win.setAttributes(winParams);
	}

	PushDataEntity entity;

	private void initData() {
		RequestQueue rq = App.getRequestQueue();
		StringBuilder url = new StringBuilder(App.BASE + Constant.PUSHINFO
				+ "tocken=" + App.TOKEN);
		Log.wtf("咨询请求url", url.toString());

		rq.add(new StringRequest(Request.Method.GET, url.toString(),
				new Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.wtf("咨询返回信息", response);
						JSONObject json;
						try {
							json = new JSONObject(response);
							if ("206".equals(json.getString("code"))) {
								JSONArray array = json.getJSONArray("daily");
								if (array != null) {

									for (int i = 0; i < array.length(); i++) {
										JSONObject o = array.optJSONObject(i);
										String content = o.getString("content");
										String imageUrl = o
												.getString("imageUrl");
										SimpleDateFormat sdf = new SimpleDateFormat(
												"yyyy-MM-dd");
										String time = sdf.format(new Date(o
												.getLong("publishTime")));
										String title = o.getString("title");
										String content_url=o.getString("url");
										boolean collected=o.getBoolean("collected");
										int id=o.getInt("id");
										entity = new PushDataEntity(imageUrl,
												content, title, time,id,content_url,collected);
										if (push_datas.contains(entity)) {
											push_datas.remove(entity);

										}
										push_datas.add(entity);
									}
									mHandler.sendEmptyMessage(2);

								}else{
									
									mHandler.sendEmptyMessage(1);
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
						mHandler.sendEmptyMessage(3);

					}
				}));
		rq.start();

	}

	public Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				MyToast.show("暂无新资讯");
				break;
			case 2:
				infoPushAdapter.notifyDataSetChanged();
			break;
			case 3:
				MyToast.show("网络错误");
				break;

			default:
				break;
			}
		}
	};

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {

	}
}
