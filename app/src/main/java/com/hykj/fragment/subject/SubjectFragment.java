package com.hykj.fragment.subject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hykj.App;
import com.hykj.Constant;
import com.hykj.R;
import com.hykj.activity.messure.AddInfoActivity;
import com.hykj.activity.subject.NewSubjectActivity;
import com.hykj.activity.subject.SubjectDetailActivity;
import com.hykj.adapter.SubjectAdapter;
import com.hykj.db.DataBaseHelper;
import com.hykj.entity.Subject;
import com.hykj.utils.DensityUtils;
import com.hykj.utils.MyToast;
import com.hykj.utils.ScreenUtils;

@SuppressLint("NewApi")
public class SubjectFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, OnClickListener {

	// 下拉刷新控件 但只支持下拉
	private SwipeRefreshLayout mSwipeRefreshWidget;
	// 支持上拉加载更多
	private ListView lv;
	private SubjectAdapter listViewAdapter;
	private View viewRoot;

	// 最后一个可见的条目 用于加载更多
	int lastVisibleItem;

	private ImageView iv_publish;
	private RelativeLayout rl_title;
	private TextView tv_all;
	private TextView tv_best;
	private TextView tv_my;
	private View titleView;
	private RelativeLayout.LayoutParams params;

	// 数据存放的集合
	public static LinkedList<Subject> subjects = new LinkedList<Subject>();
	private LinkedList<Subject> allSubjects = new LinkedList<Subject>();
	private LinkedList<Subject> mySubjects = new LinkedList<Subject>();
	private LinkedList<Subject> bestSubjects = new LinkedList<Subject>();
	private LinkedList<Subject> tempSubjects = new LinkedList<Subject>();

	int subjectType = 0;
	int subjectId = Integer.MAX_VALUE;
	int freshenType = 1;

	StringBuilder url = null;

	int responseCode = -1;
	String description = null;

	// 是否正在加载中
	private boolean isLoading = true;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case -1:
				mSwipeRefreshWidget.setProgressViewOffset(false, 0, DensityUtils.dp2px(App.getContext(), 24));
				break;
			case 0:
				mSwipeRefreshWidget.setRefreshing(false);
				isLoading = false;
				handler.sendEmptyMessageDelayed(-1, 500);
				break;
			case Constant.GET_DATA_SUCCESS:
				if (subjectType == 0) {
					subjects.clear();
					if (freshenType == 1) {
						allSubjects.addAll(tempSubjects);
					} else {
						allSubjects.addAll(0, tempSubjects);
					}
					for (Subject s : allSubjects) {
						if (!subjects.contains(s)) {
							subjects.add(s);
						}
					}
				} else if (subjectType == 1) {
					subjects.clear();
					if (freshenType == 1) {
						bestSubjects.addAll(tempSubjects);
//						allSubjects.addAll(tempSubjects);
					} else {
						bestSubjects.addAll(0, tempSubjects);
//						allSubjects.addAll(0, tempSubjects);
					}
					for (Subject s : bestSubjects) {
						if (!subjects.contains(s)) {
							subjects.add(s);
						}
					}
				} else if (subjectType == 2) {
					subjects.clear();
					if (freshenType == 1) {
						mySubjects.addAll(tempSubjects);
//						allSubjects.addAll(tempSubjects);
					} else {
						mySubjects.addAll(0, tempSubjects);
//						allSubjects.addAll(0, tempSubjects);
					}
					for (Subject s : mySubjects) {
						if (!subjects.contains(s)) {
							subjects.add(s);
						}
					}
				}
				listViewAdapter.notifyDataSetChanged();
				handler.sendEmptyMessage(0);
				enableTitle(true);

				tempSubjects.clear();
				break;
			case Constant.GET_DATA_ANALYZE_ERROR:
				MyToast.show("解析数据失败");
				handler.sendEmptyMessage(0);
				// readFromDB();
				enableTitle(true);
				break;
			case Constant.GET_DATA_NETWORK_ERROR:
				MyToast.show("网络错误");
				handler.sendEmptyMessage(0);
				// readFromDB();
				enableTitle(true);
				break;
			case Constant.GET_DATA_NULL:
				if (freshenType == 1) {
					MyToast.show("没有更多数据了");
				}
				handler.sendEmptyMessage(0);
				enableTitle(true);
				break;
			case Constant.GET_DATA_SERVER_ERROR:
				MyToast.show(description);
				handler.sendEmptyMessage(0);
				enableTitle(true);
				// readFromDB();
				break;
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (viewRoot == null) {
			viewRoot = inflater.inflate(R.layout.fragment_question, container, false);
			findView();
			registAndSetting();
		}

		initData();

		return viewRoot;
	}


	public static void setSubjectCollection(int subjectId,boolean col){
		for (Subject s:subjects) {
			if (s.getSubjectId() == subjectId){
				s.setCollection(col);
			}
		}
	}

	public static void addSubjectLiked(int subjectId){
		for (Subject s:subjects) {
			if (s.getSubjectId() == subjectId){
				s.setLiked(true);
				s.setLikeCount(s.getLikeCount()+1);
			}
		}
	}

	private void readFromDB() {
		if (allSubjects.size() == 0) {
			DataBaseHelper helper = new DataBaseHelper(App.getContext());
			SQLiteDatabase database = helper.getWritableDatabase();
			Cursor cursor = database.rawQuery("select * from subjects where userId=?", new String[] { App.USERID + "" });
			while (cursor.moveToNext()) {
				Subject subject = new Subject();
				subject.setSubjectId(cursor.getInt(0));
				subject.setTitle(cursor.getString(1));
				subject.setContent(cursor.getString(2));
				subject.setLocInfo(cursor.getString(3));
				subject.setVideoUrl(cursor.getString(6));
				subject.setSoundUrl(cursor.getString(7));
				subject.setFileUrl(cursor.getString(8));
				String s = cursor.getString(9);
				String[] split = s.split("~!@#$%^&*()_+");
				for (String string : split) {
					subject.addImgUrls(string);
				}
				subject.setAuthor(cursor.getString(10));
				subject.setReplyNum(cursor.getInt(11));
				subject.setLiked(Boolean.parseBoolean(cursor.getString(12)));
				subject.setLikeCount(cursor.getInt(13));
				subject.setPublishTime(cursor.getString(14));
				subject.setSubjectType(cursor.getInt(15));
				subject.setAuthorPhotoImgUrl(cursor.getString(16));
				subject.setCollection(Boolean.parseBoolean(cursor.getString(17)));
				allSubjects.add(subject);
			}
			database.close();
			helper.close();

			subjectType = 0;
			subjects.clear();
			subjects.addAll(allSubjects);
			listViewAdapter.notifyDataSetChanged();
		}
	}

	protected void freshenDB() {
		new Thread() {
			public void run() {
				DataBaseHelper helper = new DataBaseHelper(App.getContext());
				SQLiteDatabase database = helper.getWritableDatabase();
				database.delete("subjects", "userId=?", new String[] { App.USERID + "" });

				try {
					database.beginTransaction();
					for (int i = 0; i < tempSubjects.size(); i++) {
						Subject subject = tempSubjects.get(i);
						ContentValues values = new ContentValues();
						values.put("subjectId", subject.getSubjectId());
						values.put("title", subject.getTitle());
						values.put("content", subject.getContent());
						values.put("locInfo", subject.getLocInfo());
						values.put("videoUrl", subject.getVideoUrl());
						values.put("soundUrl", subject.getSoundUrl());
						values.put("fileUrl", subject.getFileUrl());
						ArrayList<String> imgUrls = subject.getImgUrls();
						String s = "";
						for (int j = 0; j < imgUrls.size(); j++) {
							if (j == imgUrls.size() - 1) {
								s += imgUrls.get(j);
							} else {
								s += imgUrls.get(j) + "~!@#$%^&*()_+";
							}
						}
						values.put("imgUrls", s);
						values.put("author", subject.getAuthor());
						values.put("replyNum", subject.getReplyNum());
						values.put("isLiked", subject.isLiked());
						values.put("likeCount", subject.getLikeCount());
						values.put("publishTime", subject.getPublishTime());
						values.put("subjectType", subject.getSubjectType());
						values.put("authorPhotoImgUrl", subject.getAuthorPhotoImgUrl());
						values.put("isCollection", subject.isCollection());
						values.put("userId", App.USERID);
						database.insert("subjects", null, values);
					}
					database.setTransactionSuccessful();
				} finally {
					database.endTransaction();
				}
				database.close();
				helper.close();
			}
		}.start();
	}

	private void registAndSetting() {

		mSwipeRefreshWidget.setOnRefreshListener(this);
		mSwipeRefreshWidget.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
		mSwipeRefreshWidget.setFadingEdgeLength(1000);
		mSwipeRefreshWidget.setLayoutMode(SwipeRefreshLayout.OVER_SCROLL_IF_CONTENT_SCROLLS);
		mSwipeRefreshWidget.setProgressViewOffset(false, 0, DensityUtils.dp2px(App.getContext(), 24));
		mSwipeRefreshWidget.setRefreshing(true);
		isLoading = true;

		// Listview 单击事件
		lv.setOnItemClickListener(new SubjectListItemClickListener());
		listViewAdapter = new SubjectAdapter(subjects);
		lv.setAdapter(listViewAdapter);
		// 监听RecyclerView的状态
		lv.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && lastVisibleItem == subjects.size() && !isLoading) {

					// int measuredHeight = lv.getHeight();
					mSwipeRefreshWidget.setProgressViewOffset(false, 0, DensityUtils.dp2px(App.getContext(), 350));
					// 此处在现实项目中，请换成网络请求数据代码，sendRequest .....
					mSwipeRefreshWidget.setRefreshing(true);
					isLoading = true;
					loadMoreData();
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				lastVisibleItem = firstVisibleItem + visibleItemCount;
			}
		});
	}

	class SubjectListItemClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Intent intent = new Intent(SubjectFragment.this.getActivity(), SubjectDetailActivity.class);
			intent.putExtra("subject", subjects.get(position));
			startActivityForResult(intent, 0);
		}
	}

	// 加载更多
	protected void loadMoreData() {
		url = new StringBuilder(App.BASE + Constant.GET_SUBJECTS_LIST);
		url.append("tocken=" + App.TOKEN);
		url.append("&data=");
		JSONObject json = new JSONObject();
		try {
			if (subjectType == 0) {
				if (subjects.size() == 0) {
					subjectId = Integer.MAX_VALUE;
				} else {
					subjectId = subjects.get(subjects.size() - 1).getSubjectId();
				}
			} else if (subjectType == 1) {
				url = new StringBuilder(App.BASE + Constant.GET_SUBJECTS_BESTLIST);
				url.append("tocken=" + App.TOKEN);
				url.append("&data=");
				if (bestSubjects.size() == 0) {
					subjectId = Integer.MAX_VALUE;
				} else {
					subjectId = bestSubjects.get(subjects.size() - 1).getSubjectId();
				}
			} else if (subjectType == 2) {
				url = new StringBuilder(App.BASE + Constant.GET_SUBJECTS_SELFLIST);
				url.append("tocken=" + App.TOKEN);
				url.append("&data=");
				if (mySubjects.size() == 0) {
					subjectId = Integer.MAX_VALUE;
				} else {
					subjectId = mySubjects.get(subjects.size() - 1).getSubjectId();
				}
			}
			json.put("subjectId", subjectId);
			json.put("subjectType", subjectType);
			freshenType = 1;
			json.put("freshenType", freshenType);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		url.append(json.toString());
		App.getRequestQueue().add(getreqData());
		enableTitle(false);
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (hidden) {
			int childCount = rl_title.getChildCount();
			while (childCount >= 4) {
				rl_title.removeViewAt(childCount - 1);
				childCount = rl_title.getChildCount();
			}
			iv_publish.setImageResource(R.drawable.add);
			iv_publish.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(App.getContext(), AddInfoActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
				}
			});
		} else {
			iv_publish.setImageResource(R.drawable.selector_title_publish);
			iv_publish.setOnClickListener(this);
			if (rl_title.getChildCount() <= 3) {
				rl_title.addView(titleView, params);
			}
		}
	}

	@Override
	public void onRefresh() {
		if (isLoading) {
			return;
		}
		freshenType = -1;

		isLoading = true;
		url = new StringBuilder(App.BASE + Constant.GET_SUBJECTS_LIST);
		url.append("tocken=" + App.TOKEN);
		url.append("&data=");
		JSONObject json = new JSONObject();
		try {
			if (subjectType == 0) {
				if (allSubjects.size() == 0) {
					subjectId = Integer.MIN_VALUE;
				} else {
					subjectId = allSubjects.get(0).getSubjectId();
				}
			} else if (subjectType == 1) {
				url = new StringBuilder(App.BASE + Constant.GET_SUBJECTS_BESTLIST);
				url.append("tocken=" + App.TOKEN);
				url.append("&data=");
				if (bestSubjects.size() == 0) {
					subjectId = Integer.MIN_VALUE;
				} else {
					subjectId = bestSubjects.get(0).getSubjectId();
				}
			} else if (subjectType == 2) {
				url = new StringBuilder(App.BASE + Constant.GET_SUBJECTS_SELFLIST);
				url.append("tocken=" + App.TOKEN);
				url.append("&data=");
				if (mySubjects.size() == 0) {
					subjectId = Integer.MIN_VALUE;
				} else {
					subjectId = mySubjects.get(0).getSubjectId();
				}
			}
			json.put("subjectId", subjectId);
			json.put("subjectType", subjectType);
			json.put("freshenType", freshenType);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		url.append(json.toString());
		App.getRequestQueue().add(getreqData());

		enableTitle(false);
	}

	private void enableTitle(boolean b){
			if (b) {
				tv_all.setOnClickListener(this);
				tv_best.setOnClickListener(this);
				tv_my.setOnClickListener(this);
			}else{
				tv_all.setOnClickListener(null);
				tv_best.setOnClickListener(null);
				tv_my.setOnClickListener(null);
			}
	}

	private void initData() {
		url = new StringBuilder(App.BASE + Constant.GET_SUBJECTS_LIST);
		url.append("tocken=" + App.TOKEN);
		url.append("&data=");
		JSONObject json = new JSONObject();
		try {
			json.put("subjectId", subjectId);
			json.put("subjectType", subjectType);
			json.put("freshenType", freshenType);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		url.append(json.toString());
		App.getRequestQueue().add(getreqData());
		enableTitle(false);
	}


	@SuppressLint("ResourceAsColor")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.main_imgv_add:
			Intent intent = new Intent(getActivity(), NewSubjectActivity.class);
			startActivity(intent);

			break;
		case R.id.tv_subject_all:
			if (isLoading) {
				break;
			}
			subjectType = 0;
			tv_all.setTextColor(Color.rgb(0, 0X9A, 0XA9));
			tv_best.setTextColor(Color.rgb(0XFF, 0XFF, 0XFF));
			tv_my.setTextColor(Color.rgb(0XFF, 0XFF, 0XFF));

			tv_all.setBackgroundResource(R.drawable.shape_subject_title_bg_left);
			tv_best.setBackgroundResource(R.color.bg_bule);
			tv_my.setBackgroundResource(R.drawable.shape_subject_title_bg_rightblue);

//			subjects.clear();
//			subjects.addAll(allSubjects);
//			listViewAdapter.notifyDataSetChanged();
			if (allSubjects.size() <= 10) {
				onRefresh();
			}else{
				handler.sendEmptyMessage(Constant.GET_DATA_SUCCESS);
			}
			break;

		case R.id.tv_subject_best:
			if (isLoading) {
				break;
			}
			subjectType = 1;
			tv_all.setTextColor(Color.rgb(0XFF, 0XFF, 0XFF));
			tv_best.setTextColor(Color.rgb(0, 0X9A, 0XA9));
			tv_my.setTextColor(Color.rgb(0XFF, 0XFF, 0XFF));

			tv_all.setBackgroundResource(R.drawable.shape_subject_title_bg_leftblue);
			tv_best.setBackgroundResource(android.R.color.white);
			tv_my.setBackgroundResource(R.drawable.shape_subject_title_bg_rightblue);

//			bestSubjects.clear();
//			for (Subject s : allSubjects) {
//				if (1 == s.getSubjectType()) {
//					bestSubjects.add(s);
//				}
//			}
//			subjects.clear();
//			subjects.addAll(bestSubjects);
//			listViewAdapter.notifyDataSetChanged();

			if (bestSubjects.size() <= 10) {
				onRefresh();
			}else{
				handler.sendEmptyMessage(Constant.GET_DATA_SUCCESS);
			}
			break;
		case R.id.tv_subject_my:
			if (isLoading) {
				break;
			}
			subjectType = 2;

			tv_all.setTextColor(Color.rgb(0XFF, 0XFF, 0XFF));
			tv_best.setTextColor(Color.rgb(0XFF, 0XFF, 0XFF));
			tv_my.setTextColor(Color.rgb(0, 0X9A, 0XA9));

			tv_all.setBackgroundResource(R.drawable.shape_subject_title_bg_leftblue);
			tv_best.setBackgroundResource(R.color.bg_bule);
			tv_my.setBackgroundResource(R.drawable.shape_subject_title_bg_right);

//			mySubjects.clear();
//			for (Subject s : allSubjects) {
//				if (2 == s.getSubjectType()) {
//					mySubjects.add(s);
//				}
//			}
//			subjects.clear();
//			subjects.addAll(mySubjects);
//			listViewAdapter.notifyDataSetChanged();

			if (mySubjects.size() <= 10) {
				onRefresh();
			}else{
				handler.sendEmptyMessage(Constant.GET_DATA_SUCCESS);
			}
			break;
		}
	}

	private void findView() {
		mSwipeRefreshWidget = (SwipeRefreshLayout) viewRoot.findViewById(R.id.swipe_refresh_widget);
		lv = (ListView) viewRoot.findViewById(R.id.lv_subjectlist);

		View decorView = getActivity().getWindow().getDecorView();

		iv_publish = (ImageView) decorView.findViewById(R.id.main_imgv_add);
		iv_publish.setImageResource(R.drawable.selector_title_publish);
		iv_publish.setOnClickListener(this);

		rl_title = (RelativeLayout) decorView.findViewById(R.id.main_title);
		titleView = View.inflate(App.getContext(), R.layout.item_subject_title, null);
		params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_IN_PARENT);
		rl_title.addView(titleView, params);

		tv_all = (TextView) titleView.findViewById(R.id.tv_subject_all);
		tv_all.setOnClickListener(this);
		tv_best = (TextView) titleView.findViewById(R.id.tv_subject_best);
		tv_best.setOnClickListener(this);
		tv_my = (TextView) titleView.findViewById(R.id.tv_subject_my);
		tv_my.setOnClickListener(this);

	}

	StringRequest getreqData() {
//		Log.wtf("帖子列表url", url.toString());
		return new StringRequest(Method.GET, url.toString(), new Listener<String>() {
			@Override
			public void onResponse(String res) {
//				Log.wtf("帖子列表返回结果", res);
				tempSubjects.clear();
				try {
					JSONObject response = new JSONObject(res);
					responseCode = response.optInt("code", 0);
					if (responseCode != 206) {
						handler.sendEmptyMessage(Constant.GET_DATA_SERVER_ERROR);
						description = response.getString("message");
						return;
					}
					JSONArray array = response.optJSONArray("subjectList");
					if (array == null) {
						handler.sendEmptyMessage(Constant.GET_DATA_NULL);
						return;
					}
					for (int i = 0; i < array.length(); i++) {
						JSONObject object = (JSONObject) array.get(i);
						Subject subject = new Subject();
						JSONArray jsonArray = object.optJSONArray("imgUrls");
						if (jsonArray != null) {
							for (int j = 0; j < jsonArray.length(); j++) {
								subject.addImgUrls(jsonArray.getString(j));
							}
						}
						subject.setAuthor(object.optString("author", ""));
						subject.setContent(object.optString("content", ""));
						subject.setTitle(object.optString("title", ""));
						subject.setReplyNum(object.optInt("replyNum", 0));
						subject.setSubjectId(object.optInt("subjectId", 0));
						subject.setLiked(object.optBoolean("liked", false));
						subject.setCollection(object.optBoolean("collection", false));
						subject.setLikeCount(object.optInt("likeCount", 0));
						subject.setPublishTime(object.optString("publishTime", ""));
						subject.setSubjectType(object.optInt("subjectType", 0));
						subject.setVideoUrl(object.optString("videoUrl", ""));
						subject.setFileUrl(object.optString("fileUrl", ""));
						subject.setLocInfo(object.optString("locInfo", ""));
						subject.setSoundUrl((object.optString("soundUrl", "")));
						subject.setAuthorPhotoImgUrl((object.optString("authorPhotoImgUrl", "")));
						subject.setDoctor(object.optString("authorId", "").startsWith("1"));
						subject.setViewCount(object.optInt("viewCount", 0));
						tempSubjects.add(subject);
					}
					handler.sendEmptyMessage(Constant.GET_DATA_SUCCESS);
				} catch (Exception e) {
					e.printStackTrace();
					handler.sendEmptyMessage(Constant.GET_DATA_ANALYZE_ERROR);
				}
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				handler.sendEmptyMessage(Constant.GET_DATA_NETWORK_ERROR);
			}
		});
	}

}
