package com.hykj.activity.subject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.drawee.view.SimpleDraweeView;
import com.gc.materialdesign.widgets.Dialog;
import com.hykj.App;
import com.hykj.Constant;
import com.hykj.R;
import com.hykj.adapter.SubjectReplyAdapter;
import com.hykj.entity.Subject;
import com.hykj.entity.SubjectRepeatReply;
import com.hykj.entity.SubjectReply;
import com.hykj.fragment.subject.SubjectFragment;
import com.hykj.fragment.subject.SubjectReplyAddFragment;
import com.hykj.manager.SubjectPhotoView;
import com.hykj.service.DownloadService;
import com.hykj.utils.DensityUtils;
import com.hykj.utils.MyLog;
import com.hykj.utils.MyToast;
import com.hykj.utils.OnUploadStateListener;
import com.hykj.utils.ScreenUtils;
import com.hykj.utils.TimeUtil;
import com.hykj.utils.XHttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.tb.emoji.Emoji;
import com.tb.emoji.EmojiUtil;
import com.tb.emoji.FaceFragment;
import com.tb.emoji.FaceFragment.OnEmojiClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class SubjectDetailActivity extends Activity implements OnClickListener, Serializable, OnEmojiClickListener {

	private ListView lv_reply;
	private TextView likeCount;
	private TextView tv_loc;

	private ImageButton bt_add;
	private EditText et_reply;
	private Button bt_publish;
	private ImageButton bt_emjor;
	private FrameLayout fl_face;

	private boolean isLoading = false;

	StringBuilder url = null;

	private View headerView;
	private SwipeRefreshLayout srl_reply;
	private SubjectReplyAdapter replyAdapter;
	private Subject subject;

	private ArrayList<SubjectReply> subjectReplys = new ArrayList<SubjectReply>();
	private ArrayList<SubjectReply> tempSubjectReplys = new ArrayList<SubjectReply>();

	private final int LIKE = 2;
	private final int COLLECT = 3;

	public static String locInfo = "";
	double latitude;
	double longitude;
	public static ArrayList<String> imagePath = new ArrayList<String>();

	int responseCode = -1;
	String description = null;

	MediaPlayer videoMediaPlayer = null;

	MediaPlayer soundMediaPlayer = null;
	private SurfaceView surfaceView;
	private SurfaceHolder surfaceHolder;

	private ArrayList<String> imgUrls = new ArrayList<String>();
	private String soundUrl = null;
	private String soundPath = null;

	private WindowManager.LayoutParams mWindowNanagerParams;
	private InputMethodManager mInputMethodManager;

	PopupWindow popupWindow;
	boolean isPopupWindowShow;
	LinearLayout popupWindowview;

	public static String[][] MIME_MAPTABLE = { { ".wps", "application/msword" }, { ".rtf", "application/msword" }, { ".doc", "application/msword" }, { ".docx", "application/msword" },
			{ ".xls", "application/vnd.ms-excel" }, { ".txt", "text/plain" }, { ".ppt", "application/vnd.ms-powerpoint" }, { ".pdf", "application/pdf" }, { ".bmp", "application/x-bmp" },
			{ ".gif", "image/gif" }, { ".png", "image/png" }, { ".jpg", "image/jpeg" }, { ".jpeg", "image/jpeg" }, { ".rmvb", "application/vnd.rn-realmedia-vbr" }, { ".avi", "video/avi" },
			{ ".rm", "application/vnd.rn-realmedia" }, { ".swf", "application/x-shockwave-flash" }, { ".wav", "audio/wav" }, { ".mpg", "video/mpg" }, { ".mp3", "audio/mp3" },
			{ ".mpeg", "video/mpg" }, { ".wmv", "video/x-ms-wmv" }, { ".mp4", "video/mpeg4" }, { ".apk", "application/vnd.android.package-archive" } };

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case Constant.GET_DATA_SUCCESS:
					subjectReplys.addAll(tempSubjectReplys);
					replyAdapter.notifyDataSetChanged();
					srl_reply.setRefreshing(false);
					srl_reply.setEnabled(false);
					isLoading = false;
					break;
				case Constant.GET_DATA_ANALYZE_ERROR:
					MyToast.show("解析数据失败");
					srl_reply.setRefreshing(false);
					srl_reply.setEnabled(false);
					isLoading = false;
					break;
				case Constant.GET_DATA_NETWORK_ERROR:
					MyToast.show("网络错误");
					srl_reply.setRefreshing(false);
					srl_reply.setEnabled(false);
					isLoading = false;
					break;
				case Constant.GET_DATA_NULL:
					MyToast.show("没有更多数据了");
					srl_reply.setRefreshing(false);
					srl_reply.setEnabled(false);
					isLoading = false;
					break;
				case Constant.GET_DATA_SERVER_ERROR:
					MyToast.show(description);
					srl_reply.setRefreshing(false);
					srl_reply.setEnabled(false);
					isLoading = false;
					break;
				case LIKE:
				case COLLECT:
					MyToast.show(description);
					break;
				case -101:
					uploadSuccessCount++;
					if (uploadSuccessCount == uploadCount) {
						sendData();
					}
					break;
				case -102:
					MyToast.show("回复成功");
					et_reply.setText("");
					soundPath = "";
					soundUrl = "";
					imgUrls.clear();
					imagePath.clear();
					uploadSuccessCount = 0;
					uploadCount = 0;
					loadData();
					break;
			}
		};
	};
	private Drawable drawable;
	private ImageView popu;
	private ImageView iv_start;

	private void findView() {

		getWindow().getDecorView().setLayerType(View.LAYER_TYPE_SOFTWARE, null);

		ImageView iv_back = (ImageView) findViewById(R.id.iv_title_back);
		iv_back.setOnClickListener(this);
		TextView tv_name = (TextView) findViewById(R.id.tv_title);
		tv_name.setText(subject.getTitle());

		mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		mWindowNanagerParams = getWindow().getAttributes();

		lv_reply = (ListView) findViewById(R.id.lv_subjectdetail_reply);
		srl_reply = (SwipeRefreshLayout) findViewById(R.id.srl_subjectdetail_reply);
		srl_reply.setEnabled(false);

		et_reply = (EditText) findViewById(R.id.et_subjectdetail_reply);
		bt_add = (ImageButton) findViewById(R.id.bt_subjectdetail_add);
		bt_publish = (Button) findViewById(R.id.bt_subjectdetail_publish);
		bt_emjor = (ImageButton) findViewById(R.id.bt_subjectdetail_emjor);
		fl_face = (FrameLayout) findViewById(R.id.fl_facechoose);

		FaceFragment faceFragment = FaceFragment.Instance();
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.replace(R.id.fl_facechoose, faceFragment,"face");
		transaction.commit();

		bt_emjor.setOnClickListener(this);
		bt_add.setOnClickListener(this);
		bt_publish.setOnClickListener(this);

		et_reply.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					if (mWindowNanagerParams.softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE || fl_face.getVisibility() == View.VISIBLE) {
						fl_face.setVisibility(View.GONE);
						return true;
					}
				}
				return false;
			}
		});

		et_reply.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mInputMethodManager.showSoftInput(et_reply, 0);
				fl_face.setVisibility(View.GONE);
				return false;
			}
		});

		popupWindowview = (LinearLayout) View.inflate(this, R.layout.activity_subjectdetail_popu, null);

		coll = (ImageView) popupWindowview.findViewById(R.id.iv_sd_coll);
		if(subject.isCollection()){
			coll.setImageResource(R.drawable.subject_collect_fill);
		}else{
			coll.setImageResource(R.drawable.subject_coll);
		}
		coll.setOnClickListener(this);

		ImageView share = (ImageView) popupWindowview.findViewById(R.id.iv_sd_share);
		share.setOnClickListener(this);
	}

	@SuppressLint("NewApi")
	private void registAndSetting() {
		popupWindow = new PopupWindow(popupWindowview, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		drawable = SubjectDetailActivity.this.getResources().getDrawable(R.drawable.bg_subjectliked);
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());

		srl_reply.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
		srl_reply.setFadingEdgeLength(1000);
		srl_reply.setLayoutMode(SwipeRefreshLayout.OVER_SCROLL_IF_CONTENT_SCROLLS);
		srl_reply.setProgressViewOffset(false, 0,DensityUtils.dp2px(App.getContext(), 400));
		isLoading = true;

		headerView = View.inflate(this, R.layout.activity_subjectdetail_header, null);

		TextView title = (TextView) headerView.findViewById(R.id.tv_sd_title);
		TextView content = (TextView) headerView.findViewById(R.id.tv_sd_content);

		title.setText(subject.getTitle());
		content.setText(subject.getContent());

		EmojiUtil.displayTextView(title);
		EmojiUtil.displayTextView(content);

		popu = (ImageView) headerView.findViewById(R.id.iv_sd_popu);
		popu.setOnClickListener(this);

		TextView author = (TextView) headerView.findViewById(R.id.tv_sd_authorname);
		author.setText(subject.getAuthor());

		if (subject.isDoctor()) {
			author.setTextColor(Color.RED);
		}else{
			author.setTextColor(Color.BLACK);
		}

		TextView publishtime = (TextView) headerView.findViewById(R.id.tv_sd_publishtime);
		publishtime.setText(TimeUtil.getStringTime(Long.parseLong(subject.getPublishTime())));

		SimpleDraweeView authorPhoto = (SimpleDraweeView) headerView.findViewById(R.id.niv_subjecthead_photo);
		String photoImgUrl = subject.getAuthorPhotoImgUrl();
//		Picasso.with(App.getContext()).load(TextUtils.isEmpty(photoImgUrl)?App.DEFULT_PHOTO:photoImgUrl).into(authorPhoto);
		authorPhoto.setImageURI(Uri.parse(TextUtils.isEmpty(photoImgUrl)?App.DEFULT_PHOTO:photoImgUrl));

		TextView tv_viewNum = (TextView) headerView.findViewById(R.id.tv_sd_viewnum);
		tv_viewNum.setText(subject.getViewCount()+"");

		TextView tv_replyNum = (TextView) headerView.findViewById(R.id.tv_sd_replynum);
		tv_replyNum.setText(subject.getReplyNum()+"");

		LinearLayout ll_container = (LinearLayout) headerView.findViewById(R.id.ll_sd_imagecontainer);

		for (int i = 0; i < subject.getImgUrls().size(); i++) {
			SimpleDraweeView imageView = new SimpleDraweeView(App.getContext());
			imageView.setScaleType(ScaleType.FIT_CENTER);
			int screenWidth = ScreenUtils.getScreenWidth(App.getContext());
			int ivSize = screenWidth - DensityUtils.dp2px(App.getContext(), 40);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ivSize, ivSize);
			params.setMargins(0,DensityUtils.dp2px(App.getContext(),5),0,0);
			imageView.setLayoutParams(params);
			String imgUrl = subject.getImgUrls().get(i);
//			Picasso.with(App.getContext()).load(TextUtils.isEmpty(imgUrl) ? App.DEFULT_PHOTO:imgUrl).into(imageView);
			imageView.setImageURI(Uri.parse(TextUtils.isEmpty(imgUrl) ? App.DEFULT_PHOTO:imgUrl));
			ll_container.addView(imageView);
			final int y = i;
			imageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(SubjectDetailActivity.this, SubjectPhotoView.class);
					intent.putExtra("imageUrls", subject.getImgUrls());
					intent.putExtra("currentItem", y);
					startActivity(intent);
				}
			});
		}

		tv_loc = (TextView) headerView.findViewById(R.id.tv_sd_loc);
		if (!TextUtils.isEmpty(subject.getLocInfo())) {
			tv_loc.setText(subject.getLocInfo());
		} else {
			tv_loc.setVisibility(View.GONE);
		}

		// TODO VIDEO
		if (!TextUtils.isEmpty(subject.getVideoUrl())) {
			RelativeLayout relativeLayout = new RelativeLayout(App.getContext());
			surfaceView = new SurfaceView(App.getContext());
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, DensityUtils.dp2px(App.getContext(),450));
			surfaceView.setLayoutParams(params);
			relativeLayout.addView(surfaceView);
			surfaceView.setOnClickListener(new VideoPlayListener());
			surfaceHolder = surfaceView.getHolder();
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
			iv_start = new ImageView(App.getContext());
			iv_start.setImageResource(R.drawable.video_play);
			params = new RelativeLayout.LayoutParams(DensityUtils.dp2px(App.getContext(),35), DensityUtils.dp2px(App.getContext(),35));
			params.addRule(RelativeLayout.CENTER_IN_PARENT);
			iv_start.setLayoutParams(params);
			relativeLayout.addView(iv_start);
			ll_container.addView(relativeLayout);
		}
		// TODO VIDEO DONE

		RelativeLayout relativeLayout = new RelativeLayout(App.getContext());

		RelativeLayout.LayoutParams params;
		if (!TextUtils.isEmpty(subject.getSoundUrl())) {
			params = new RelativeLayout.LayoutParams(DensityUtils.dp2px(App.getContext(), 75), DensityUtils.dp2px(App.getContext(), 35));
			ImageView imageView = new ImageView(SubjectDetailActivity.this);
			imageView.setImageResource(R.drawable.mysound);
			params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			imageView.setLayoutParams(params);
			imageView.setOnClickListener(new SoundPlayListener());
			relativeLayout.addView(imageView);
		}
		if (!TextUtils.isEmpty(subject.getFileUrl())) {
			params = new RelativeLayout.LayoutParams(DensityUtils.dp2px(App.getContext(), 35), DensityUtils.dp2px(App.getContext(), 35));
			ImageView imageView = new ImageView(SubjectDetailActivity.this);
			imageView.setImageResource(R.drawable.file);
			params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			imageView.setLayoutParams(params);
			imageView.setOnClickListener(new DownloadFileListener());
			relativeLayout.addView(imageView);
		}
		LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		p.setMargins(0,DensityUtils.dp2px(App.getContext(),10),0,0);
		relativeLayout.setLayoutParams(p);
		ll_container.addView(relativeLayout);

		likeCount = (TextView) headerView.findViewById(R.id.tv_sd_like);
		likeCount.setText(subject.getLikeCount() + "");
		if (!subject.isLiked()) {
			likeCount.setOnClickListener(this);
		}else{
			likeCount.setCompoundDrawables(drawable,null,null,null);
		}

		lv_reply.addHeaderView(headerView);
		replyAdapter = new SubjectReplyAdapter(subjectReplys, subject.getSubjectId(), SubjectDetailActivity.this);
		lv_reply.setAdapter(replyAdapter);
		replyAdapter.setOnRepeatListener(new MyRepeatListener());

		lv_reply.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE &&view.getLastVisiblePosition() == subjectReplys.size() && !isLoading) {// && subjectReplys.size() != 0
					srl_reply.setEnabled(true);
					isLoading = true;
					srl_reply.setRefreshing(true);
					loadData();
				}
			}
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			}
		});

	}

	private int uploadSuccessCount = 0;
	private int uploadCount = 0;


	private void upload(String uploadPath, final String t) {
		com.lidroid.xutils.http.RequestParams requestParams = new com.lidroid.xutils.http.RequestParams();
		requestParams.addBodyParameter("media", new File(uploadPath));
		requestParams.addBodyParameter("tocken", App.TOKEN);
		XHttpUtils.getInstance().upload(App.BASE + Constant.UPLOAD_FILE, requestParams, new OnUploadStateListener() {
			@Override
			public void onUploadSuccess(String result) {
				try {
					MyLog.wtf("上传文件返回结果", result);
					JSONObject json = new JSONObject(result);
					int responseCode = json.getInt("code");
					if (responseCode == 206) {
						if ("sound".equals(t)) {
							soundUrl = json.getString("fileUrl");
						} else if ("img".equals(t)) {
							imgUrls.add(json.getString("fileUrl"));
						}
						handler.sendEmptyMessage(-101);
					} else {
						description = json.getString("message");
						handler.sendEmptyMessage(Constant.GET_DATA_SERVER_ERROR);
					}
				} catch (JSONException e) {
					e.printStackTrace();
					handler.sendEmptyMessage(Constant.GET_DATA_ANALYZE_ERROR);
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {

				handler.sendEmptyMessage(Constant.GET_DATA_NETWORK_ERROR);
			}
		});
	}

	String getEncode(String s) {
		String result = null;

		try {
			result = URLEncoder.encode(s, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return result;
	}

	private void sendData() {
		url = new StringBuilder(App.BASE + Constant.SUBJECT_REPLY);
		url.append("tocken=" + App.TOKEN);
		url.append("&data=");
		JSONObject json = new JSONObject();
		try {
			json.put("subjectId", subject.getSubjectId());
			json.put("replyContent", getEncode(et_reply.getText().toString()));
			if (!TextUtils.isEmpty(locInfo)) {
				json.put("replyLocInfo", getEncode(locInfo));
			}
			if (!TextUtils.isEmpty(soundUrl)) {
				json.put("replySoundUrl", soundUrl);
			}
			if (imgUrls.size() > 0) {
				JSONArray array = new JSONArray();
				for (int i = 0; i < imgUrls.size(); i++) {
					array.put(i, imgUrls.get(i));
				}
				json.put("replyImgUrls", array);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		url.append(json.toString());
		MyLog.wtf("回复的url", url.toString());
		App.getRequestQueue().add(getreqData());
	}

	private Request getreqData() {
		return new StringRequest(Method.GET, url.toString(), new Listener<String>() {
			@Override
			public void onResponse(String res) {
				try {
					MyLog.wtf("回复的返回结果", res);
					JSONObject response = new JSONObject(res);

					responseCode = response.getInt("code");
					description = response.getString("message");
					if (responseCode != 206) {
						handler.sendEmptyMessage(Constant.GET_DATA_SERVER_ERROR);
						return;
					}
					handler.sendEmptyMessage(-102);
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		App.addActivity(this);
		setContentView(R.layout.activity_subjectdetail);
		subject = (Subject) getIntent().getSerializableExtra("subject");

		findView();
		registAndSetting();

		loadData();
	}

	private void loadData() {
		url = new StringBuilder(App.BASE + Constant.SUBJECT_LIST_REPLY);
		url.append("tocken="+App.TOKEN);
		url.append("&data=");
		JSONObject json = new JSONObject();
		try {
			if (subjectReplys.size() == 0) {
				json.put("replyId", 1);
			} else {
				json.put("replyId", subjectReplys.get(subjectReplys.size() - 1).getReplyId());
			}
			json.put("subjectId", subject.getSubjectId());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		url.append(json.toString());

		MyLog.wtf("获取回复列表", url.toString());
		App.getRequestQueue().add(getreqInit());
	}


	class MyRepeatListener implements SubjectReplyAdapter.RepeatListener {
		@Override
		public void setRepeatNickName(String name) {
			MyToast.show(name);
		}
	}

	class DownloadFileListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			File file = new File(Environment.getExternalStorageDirectory() + "/Thealth/download/" + subject.getFileUrl().substring(subject.getFileUrl().lastIndexOf("/")));
			if (file.exists()) {
				startActivity(getOpenFileIntent(file));
				return;
			}
			final Dialog dialog = new com.gc.materialdesign.widgets.Dialog(SubjectDetailActivity.this, null, "是否下载附件");
			dialog.setOnAcceptButtonClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent downloadIntent = new Intent(SubjectDetailActivity.this, DownloadService.class);
					downloadIntent.putExtra("fileUrl", subject.getFileUrl());
					startService(downloadIntent);
					dialog.dismiss();
				}
			});
			dialog.addCancelButton("取消", new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			dialog.show();
		}
	}

	class SoundPlayListener implements OnClickListener {
		@Override
		public void onClick(final View v) {
			if (soundMediaPlayer != null && soundMediaPlayer.isPlaying()) {
				return;
			}
			if (soundMediaPlayer == null) {
				soundMediaPlayer = MediaPlayer.create(App.getContext(), Uri.parse(subject.getSoundUrl()));
			} else {
				soundMediaPlayer.start();
			}
			soundMediaPlayer.setOnPreparedListener(new OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer mp) {
					soundMediaPlayer.start();
				}
			});
			soundMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					/*
					 * soundMediaPlayer.release(); soundMediaPlayer = null;
					 */
				}
			});
		}
	}

	class VideoPlayListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			if (videoMediaPlayer != null && videoMediaPlayer.isPlaying()) {
				return;
			}

			if (videoMediaPlayer == null) {
//				videoMediaPlayer = MediaPlayer.create(App.getContext(), Uri.parse("http://192.168.31.111:8080/download.mp4"), surfaceHolder);
				videoMediaPlayer = MediaPlayer.create(App.getContext(), Uri.parse(subject.getVideoUrl()), surfaceHolder);
			} else {
				videoMediaPlayer.start();
			}

			videoMediaPlayer.setOnPreparedListener(new OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer mp) {
					iv_start.setVisibility(View.GONE);
					videoMediaPlayer.start();
				}
			});
			videoMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
				}
			});
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
			case -20:
				locInfo = data.getStringExtra("locInfo");
				latitude = data.getDoubleExtra("latitude", 0);
				longitude = data.getDoubleExtra("longitude", 0);
				break;
			case -800:
				imagePath = (ArrayList<String>) data.getSerializableExtra("imagePath");
				break;
		}
	}

	Fragment fragment;
	private ImageView coll;
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
			case R.id.iv_title_back:
				SubjectDetailActivity.this.finish();
				break;
			case R.id.tv_sd_like:
				likeCount.setText(Integer.parseInt(likeCount.getText().toString()) + 1 + "");
				subject.setLiked(true);
//			likeCount.setBackgroundResource(R.drawable.bg_subjectliked);
				likeCount.setCompoundDrawables(drawable,null,null,null);
				likeCount.setClickable(false);

				try {
					SubjectFragment.addSubjectLiked(subject.getSubjectId());
				} catch (Exception e1) {
					e1.printStackTrace();
				}

				url = new StringBuilder(App.BASE + Constant.SUBJECT_LIKE);
				url.append("tocken="+App.TOKEN);
				url.append("&data=");
				JSONObject json = new JSONObject();
				try {
					json.put("subjectId", subject.getSubjectId());
				} catch (JSONException e) {
					e.printStackTrace();
				}
				url.append(json.toString());
				App.getRequestQueue().add(getOperatorReq(LIKE));

				break;
			case R.id.iv_sd_share:
				showShare();
				break;
			case R.id.iv_sd_coll:

				if(subject.isCollection()){
					coll.setImageResource(R.drawable.subject_coll);
					url = new StringBuilder(App.BASE + Constant.SUBJECT_DELCOLL + "tocken=" + App.TOKEN);
					url.append("&data=");
					JSONObject object = new JSONObject();
					try {
						object.put("id", subject.getSubjectId());
					} catch (JSONException e) {
						e.printStackTrace();
					}
					url.append(object.toString());
					MyLog.wtf("删除收藏帖子的url", url.toString());
					App.getRequestQueue().add(getOperatorReq(COLLECT));
				}else{
					coll.setImageResource(R.drawable.subject_collect_fill);
					url = new StringBuilder(App.BASE + Constant.SUBJECT_COLL);
					url.append("tocken=").append(App.TOKEN);
					url.append("&data=");
					JSONObject json2 = new JSONObject();
					try {
						json2.put("id", subject.getSubjectId());
					} catch (JSONException e) {
						e.printStackTrace();
					}
					url.append(json2.toString());
					MyLog.wtf("收藏的url", url.toString());
					App.getRequestQueue().add(getOperatorReq(COLLECT));
				}

				subject.setCollection(!subject.isCollection());

				try {
					SubjectFragment.setSubjectCollection(subject.getSubjectId(),subject.isCollection());
				} catch (Exception e1) {
					e1.printStackTrace();
				}

				break;
			case R.id.bt_subjectdetail_emjor:
				if (fl_face.getVisibility() == View.GONE) {
					mInputMethodManager.hideSoftInputFromWindow(et_reply.getWindowToken(), 0);
					fragment = new FaceFragment();
					FragmentTransaction transaction =getFragmentManager().beginTransaction();
					transaction.replace(R.id.fl_facechoose,fragment,"face");
					//提交修改
					transaction.commit();
					try {
						Thread.sleep(80);// 解决此时会黑一下屏幕的问题
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					fl_face.setVisibility(View.VISIBLE);
				} else {
					if ("face".equals((String)fragment.getTag())) {
						fl_face.setVisibility(View.GONE);
						mInputMethodManager.showSoftInput(et_reply, 0);
					}else{
						fragment = new FaceFragment();
						FragmentTransaction transaction =getFragmentManager().beginTransaction();
						transaction.replace(R.id.fl_facechoose,fragment,"face");
						//提交修改
						transaction.commit();
					}
				}
				break;
			case 1:

				break;
			case R.id.iv_sd_popu:
				if (isPopupWindowShow) {
					popupWindowHiden();
				} else {
					popupWindowShow(headerView);
				}
				break;
			case R.id.bt_subjectdetail_add:
				if (fl_face.getVisibility() == View.GONE) {
					mInputMethodManager.hideSoftInputFromWindow(et_reply.getWindowToken(), 0);
					fragment = new SubjectReplyAddFragment();
					FragmentTransaction picTransaction =getFragmentManager().beginTransaction();
					picTransaction.replace(R.id.fl_facechoose,fragment,"pic");
					//提交修改
					picTransaction.commit();
					try {
						Thread.sleep(80);// 解决此时会黑一下屏幕的问题
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					fl_face.setVisibility(View.VISIBLE);
				} else {
					if ("pic".equals((String)fragment.getTag())) {
						fl_face.setVisibility(View.GONE);
						mInputMethodManager.showSoftInput(et_reply, 0);
					}else{
						fragment = new SubjectReplyAddFragment();
						FragmentTransaction picTransaction =getFragmentManager().beginTransaction();
						picTransaction.replace(R.id.fl_facechoose,fragment,"pic");
						//提交修改
						picTransaction.commit();
					}
				}
				break;
			case R.id.bt_subjectdetail_publish:
					publishSubject();
				break;
		}
	}

	private void publishSubject() {
		if (!TextUtils.isEmpty(soundPath)) {
			upload(soundPath, "sound");
			uploadCount++;
		}
		if (imagePath.size() != 0) {
			for (int i = 0; i < imagePath.size(); i++) {
				upload(imagePath.get(i), "img");
				uploadCount++;
			}
		}
		if (uploadCount == 0) {
			sendData();
		}
	}

	private void showShare() {
		ShareSDK.initSDK(this);
		OnekeyShare oks = new OnekeyShare();
		// 关闭sso授权
		oks.disableSSOWhenAuthorize();

		// 分享时Notification的图标和文字 2.5.9以后的版本不调用此方法
		// oks.setNotification(R.drawable.ic_launcher,
		// getString(R.string.app_name));
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
//		oks.setTitle(subject.getTitle());
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		// oks.setTitleUrl("http://sharesdk.cn");
		// text是分享文本，所有平台都需要这个字段
		oks.setText(subject.getContent());
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		// oks.setImagePath("/sdcard/test.jpg");// 确保SDcard下面存在此张图片
		// url仅在微信（包括好友和朋友圈）中使用
		// oks.setUrl("http://sharesdk.cn");
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		// oks.setComment("我是测试评论文本");
		// site是分享此内容的网站名称，仅在QQ空间使用
		/*oks.setSite(getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		if (subject.getImgUrls().size() > 0) {
			oks.setImageUrl(subject.getImgUrls().get(0));
		}
		if (TextUtils.isEmpty(subject.getSoundUrl())) {
			oks.setMusicUrl(subject.getSoundUrl());
		}*/
		oks.setSiteUrl("http://www.tianhengyl.com/");
		// 启动分享GUI
		oks.show(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		popupWindowHiden();
		if (videoMediaPlayer != null) {
			videoMediaPlayer.release();
		}
		if (soundMediaPlayer != null) {
			soundMediaPlayer.release();
		}
	}

	private Request getOperatorReq(final int i) {
		return new StringRequest(Method.GET, url.toString(), new Listener<String>() {
			@Override
			public void onResponse(String res) {
				MyLog.wtf("操作的返回结果", res);
				try {
					JSONObject response = new JSONObject(res);
					responseCode = response.optInt("code");
					description = response.optString("message");
					if (responseCode != 206) {
						handler.sendEmptyMessage(Constant.GET_DATA_SERVER_ERROR);
						return;
					}

					handler.sendEmptyMessage(i);
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

	private Request getreqInit() {
		return new StringRequest(Method.GET, url.toString(), new Listener<String>() {
			@Override
			public void onResponse(String res) {
				try {
					MyLog.wtf("回复列表的返回结果", res);
					JSONObject response = new JSONObject(res);

					tempSubjectReplys.clear();
					responseCode = response.getInt("code");
					description = response.getString("message");

					if (responseCode != 206) {
						handler.sendEmptyMessage(Constant.GET_DATA_SERVER_ERROR);
						return;
					}
					JSONArray array = response.optJSONArray("replyList");
					if (array == null) {
						handler.sendEmptyMessage(Constant.GET_DATA_NULL);
						return;
					}
					for (int i = 0; i < array.length(); i++) {
						JSONObject object = (JSONObject) array.get(i);
						SubjectReply subject = new SubjectReply();
						JSONArray jsonArray = object.optJSONArray("replyImgUrls");
						if (jsonArray != null) {
							for (int j = 0; j < jsonArray.length(); j++) {
								subject.addReplyImg(jsonArray.getString(j));
							}
						}
						JSONArray jsonArray1 = object.optJSONArray("repeatReply");
						if (jsonArray1 != null) {
							for (int j = 0; j < jsonArray1.length(); j++) {
								JSONObject obj = jsonArray1.getJSONObject(j);
								subject.addRepeatReply(new SubjectRepeatReply(obj.getString("repeat"), obj.getString("repeatTo"), obj.getString("repeatContent")));
							}
						}
						subject.setReplyAuthor(object.optString("replyAuthor",""));
						subject.setReplyAuthorPhotoUrl(object.optString("replyAuthorPhotoUrl",""));
						subject.setReplyContent(object.optString("replyContent",""));
						subject.setReplyId(object.optInt("replyId",0));
						subject.setReplyLocInfo(object.optString("replyLocInfo",""));
						subject.setReplySoundUrl(object.optString("replySoundUrl",""));
						subject.setReplyTime(object.optString("replyTime",""));
						subject.setDoctor(object.optString("userId","").startsWith("1"));
						tempSubjectReplys.add(subject);
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

	private Intent getOpenFileIntent(File file) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// 设置intent的Action属性
		intent.setAction(Intent.ACTION_VIEW);
		// 设置intent的data和Type属性。
		String type = getMIMEType(file);
		intent.setDataAndType(Uri.fromFile(file), type);
		return intent;
	}

	private String getMIMEType(File file) {
		String type = "*/*";
		String fName = file.getName();
		// 获取后缀名前的分隔符"."在fName中的位置。
		int dotIndex = fName.lastIndexOf(".");
		if (dotIndex < 0) {
			return type;
		}
		/* 获取文件的后缀名 */
		String end = fName.substring(dotIndex, fName.length()).toLowerCase();
		if (end == "")
			return type;
		// 在MIME和文件类型的匹配表中找到对应的MIME类型。
		for (int i = 0; i < MIME_MAPTABLE.length; i++) {
			if (end.equals(MIME_MAPTABLE[i][0]))
				type = MIME_MAPTABLE[i][1];
		}
		return type;
	}

	@Override
	public void onEmojiDelete() {
		String text = et_reply.getText().toString();
		if (text.isEmpty()) {
			return;
		}
		if ("]".equals(text.substring(text.length() - 1, text.length()))) {
			int index = text.lastIndexOf("[");
			if (index == -1) {
				int action = KeyEvent.ACTION_DOWN;
				int code = KeyEvent.KEYCODE_DEL;
				KeyEvent event = new KeyEvent(action, code);
				et_reply.onKeyDown(KeyEvent.KEYCODE_DEL, event);
				displayTextView();
				return;
			}
			et_reply.getText().delete(index, text.length());
			displayTextView();
			return;
		}
		int action = KeyEvent.ACTION_DOWN;
		int code = KeyEvent.KEYCODE_DEL;
		KeyEvent event = new KeyEvent(action, code);
		et_reply.onKeyDown(KeyEvent.KEYCODE_DEL, event);
		displayTextView();
	}

	@Override
	public void onEmojiClick(Emoji emoji) {
		if (emoji != null) {
			int index = et_reply.getSelectionStart();
			Editable editable = et_reply.getEditableText();
			if (index < 0) {
				editable.append(emoji.getContent());
			} else {
				editable.insert(index, emoji.getContent());
			}
		}
		displayTextView();
	}

	private void displayTextView() {
		try {
			EmojiUtil.handlerEmojiText(et_reply, et_reply.getText().toString(), App.getContext());
		} catch (IOException e) {
			e.printStackTrace();
		}
		et_reply.setSelection(et_reply.length());
	}

	private void popupWindowShow(View view) {

		int[] location = new int[2];
		popu.getLocationOnScreen(location);

		popupWindow.showAtLocation(view, Gravity.LEFT | Gravity.TOP, location[0]-DensityUtils.dp2px(App.getContext(),25), location[1]-DensityUtils.dp2px(App.getContext(),35));

		isPopupWindowShow = true;

		ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.9f, Animation.RELATIVE_TO_SELF, 0.9f);
		scaleAnimation.setDuration(400);// 动画的持续时间
		// 渐变动画
		// 从透明到不透明的效果，或者从不透明到透明
		AlphaAnimation alphaAnimation = new AlphaAnimation(0.4f, 1);
		alphaAnimation.setDuration(400);// 动画的持续时间

		// 组合动画
		// shareInterpolator ： 是否使用相同的动画插入器，true：使用相同的
		// false:使用每个动画各自
		AnimationSet animationSet = new AnimationSet(true);
		animationSet.addAnimation(scaleAnimation);// 添加动画
		animationSet.addAnimation(alphaAnimation);
		// 执行动画，注意，如果不给popupwindow设置背景，动画是不能执行的
		popupWindowview.startAnimation(animationSet);
	}

	private void popupWindowHiden() {
		if (isPopupWindowShow && popupWindow != null) {
			popupWindow.dismiss();// 隐藏popupwindow
			// popupWindow = null;// 置为空，为下次显示popupwindow做准备
			isPopupWindowShow = false;
		}
	}
}