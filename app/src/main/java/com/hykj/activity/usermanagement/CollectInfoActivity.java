package com.hykj.activity.usermanagement;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hykj.App;
import com.hykj.Constant;
import com.hykj.R;
import com.hykj.adapter.InfoCollectAdapter;
import com.hykj.entity.PushDataEntity;
import com.hykj.entity.Subject;
import com.hykj.utils.MyToast;

/**
 * @author 作者 : zhaoyu
 * @version 创建时间：2016年1月13日 下午4:02:03 类说明：我的收藏
 */
public class CollectInfoActivity extends com.hykj.activity.BaseActivity {
	private ListView mLv;
	private List<PushDataEntity> collect_data = new ArrayList<PushDataEntity>();
	private ImageView mTv_back;
	private LinkedList<Subject> tempSubjects = new LinkedList<Subject>();
	private BaseAdapter adapter = new InfoCollectAdapter(
			CollectInfoActivity.this, collect_data, tempSubjects);

	@Override
	public void init() {
		setContentView(R.layout.activity_collectinfo);
		mLv = (ListView) findViewById(R.id.collectinfo_lv);
		mLv.setAdapter(adapter);
		mTv_back = (ImageView) findViewById(R.id.collectinfo_tv_back);
		mTv_back.setOnClickListener(this);
		getDataFromServer();
		getSubjectData();
	}

	// 从服务器获取收藏信息
	private void getDataFromServer() {
		RequestQueue rq = App.getRequestQueue();
		StringBuilder url = new StringBuilder(App.BASE + Constant.GETCOLLECTION
				+ "tocken=" + App.TOKEN);
		// Log.wtf("咨询请求url", url.toString());
		rq.add(new StringRequest(Request.Method.GET, url.toString(),
				new Listener<String>() {
					@Override
					public void onResponse(String response) {
						JSONObject json;
						try {
							json = new JSONObject(response);
							if ("206".equals(json.getString("code"))) {
								JSONArray array = json.optJSONArray("daily");
								if (array != null && array.length() != 0) {

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
										String content_url = o.getString("url");
										int id = o.getInt("id");
										PushDataEntity entity = new PushDataEntity(
												imageUrl, content, title, time,
												id, content_url);
										if (!collect_data.contains(entity)) {
											collect_data.add(entity);
										}
										Message msg = mHandler.obtainMessage(0,
												collect_data);
										msg.sendToTarget();
									}

								} else {
									mHandler.sendEmptyMessage(1);
								}

							} else {
								mHandler.sendEmptyMessage(2);
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

	private StringBuilder url;

	private void getSubjectData() {
		url = new StringBuilder(App.BASE + Constant.SUBJECT_COLLIST);
		url.append("tocken=" + App.TOKEN);
		App.getRequestQueue().add(getreqFreshening());
		// Log.wtf("收藏帖子列表url", url.toString());
	}

	StringRequest getreqFreshening() {
		return new StringRequest(Method.GET, url.toString(),
				new Listener<String>() {
					@Override
					public void onResponse(String res) {
						// Log.wtf("收藏帖子列表的返回结果", res);
						try {
							JSONObject response = new JSONObject(res);
							int responseCode = response.optInt("code",
									0);
							if (responseCode != 206) {
								mHandler.sendEmptyMessage(Constant.GET_DATA_SERVER_ERROR);
								return;
							}
							JSONArray array = response
									.optJSONArray("subjectList");
							if (array == null) {
								mHandler.sendEmptyMessage(Constant.GET_DATA_NULL);
								return;
							}
							for (int i = 0; i < array.length(); i++) {
								JSONObject object = (JSONObject) array.get(i);
								Subject subject = new Subject();
								JSONArray jsonArray = object
										.optJSONArray("imgUrls");
								if (jsonArray != null) {
									for (int j = 0; j < jsonArray.length(); j++) {
										subject.addImgUrls(jsonArray
												.getString(j));
									}
								}
								subject.setAuthor(object
										.optString("author", ""));
								subject.setContent(object.optString("content",
										""));
								subject.setTitle(object.optString("title", ""));
								subject.setReplyNum(object
										.optInt("replyNum", 0));
								subject.setSubjectId(object.optInt("subjectId",
										0));
								subject.setLiked(object.optBoolean("liked",
										false));
								subject.setCollection(object.optBoolean(
										"collection", false));
								subject.setLikeCount(object.optInt("likeCount",
										0));
								subject.setPublishTime(object.optString(
										"publishTime", ""));
								subject.setSubjectType(object.optInt(
										"subjectType", 0));
								subject.setVideoUrl(object.optString(
										"videoUrl", ""));
								subject.setFileUrl(object.optString("fileUrl",
										""));
								subject.setLocInfo(object.optString("locInfo",
										""));
								subject.setSoundUrl((object.optString(
										"soundUrl", "")));
								subject.setAuthorPhotoImgUrl((object.optString(
										"authorPhotoImgUrl", "")));
								subject.setDoctor(object.optString("authorId",
										"").startsWith("1"));
								subject.setViewCount(object.optInt("viewCount",
										0));
								tempSubjects.add(subject);
							}
							mHandler.sendEmptyMessage(Constant.GET_DATA_SUCCESS);
						} catch (JSONException e) {
							e.printStackTrace();
							mHandler.sendEmptyMessage(Constant.GET_DATA_ANALYZE_ERROR);
						}
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						mHandler.sendEmptyMessage(Constant.GET_DATA_NETWORK_ERROR);
					}
				});
	}

	public Handler mHandler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				collect_data = (List<PushDataEntity>) msg.obj;
				adapter.notifyDataSetChanged();
				/*
				 * if (collect_data.size() > 0) { adapter = new
				 * InfoCollectAdapter(CollectInfoActivity.this, collect_data);
				 * mLv.setAdapter(adapter); Log.wtf("aaa", "you"); } else {
				 * LYLToast.show("您暂无收藏"); Log.wtf("aaa", "wu"); }
				 */
				break;
			case 2:
				MyToast.show("网络连接错误");
				break;
			case Constant.GET_DATA_ANALYZE_ERROR:
				MyToast.show("解析数据失败");
				break;
			case Constant.GET_DATA_NETWORK_ERROR:
				MyToast.show("网络错误");
				break;
			case Constant.GET_DATA_SERVER_ERROR:
				MyToast.show("获取信息失败");
				break;
			case Constant.GET_DATA_SUCCESS:
				adapter.notifyDataSetChanged();
				break;
			}
		}
	};

	@Override
	public void click(View v) {
		switch (v.getId()) {
		case R.id.collectinfo_tv_back:
			finish();
			break;

		default:
			break;
		}
	}

}
