package com.hykj.activity.usermanagement;

/**
 * @author 作者：赵宇
 * @version 1.0
 * 创建时间：2015年10月15日 下午3:30:20
 * 类说明：登录界面
 */

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;

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
import com.hykj.db.DataBaseHelper;
import com.hykj.service.ChatService;
import com.hykj.service.IConnectionStatusCallback;
import com.hykj.utils.Check;
import com.hykj.utils.Coder;
import com.hykj.utils.Decoder;
import com.hykj.utils.FindView;
import com.hykj.utils.MyLog;
import com.hykj.utils.MyProgress;
import com.hykj.utils.MyToast;

public class LoginActivity extends BaseActivity {

	private EditText mEdt_username, mEdt_password;// 用户名和密码输入框
	private Button mBtn_login;// 登录
	private ImageButton  mBtn_qq, mBtn_wx, mBtn_wb;//qq，微信，微博按钮
	private TextView mTv_forget, mTv_reg;// 忘记密码,注册新用户;
	private FindView mFindView;// 初始化组件工具
	private Check mCheck;// 检查格式
	private DataBaseHelper helper;

	private static final int LOGIN_FAILED = -2;
	private static final int LOGIN_SUCCESS = -1;
	private static final int WRONG_PWD = 1;
	private static final int NETWORK_ERROR = 2;
	private static final int WRONG_USER_DOMAIN = 4;
	private static final int LOGIN_CONFLICT = 6;

	String description = "";

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case LOGIN_FAILED:
					MyProgress.dismiss();
					MyToast.show(""+description);
					break;
				case LOGIN_SUCCESS:
					MyProgress.dismiss();
					MyToast.show("登录成功");
					SharedPreferences share = getSharedPreferences("autologin", Activity.MODE_PRIVATE);
					SharedPreferences.Editor editor = share.edit();

					String token = App.TOKEN;
					String userid = App.USERID + "";
					String password = App.tempPwd;

					try {
						token = new String(Base64.encode(Coder.des3EncodeECB(token.getBytes("UTF-8")), Base64.DEFAULT), "UTF-8");
						password = new String(Base64.encode(Coder.des3EncodeECB(password.getBytes("UTF-8")), Base64.DEFAULT), "UTF-8");
						userid = new String(Base64.encode(Coder.des3EncodeECB((userid + "").getBytes("UTF-8")), Base64.DEFAULT), "UTF-8");
					} catch (Exception e) {
						e.printStackTrace();
					}

					editor.putString("token", token);
					editor.putString("userid", userid);
					editor.putString("password", password);
					editor.commit();
					Intent intent = new Intent(LoginActivity.this,
							MainActivity.class);
					LoginActivity.this.startActivity(intent);
					if (!questionnaire) {
						startActivity(new Intent(LoginActivity.this,
								QuestionnaireActivity.class));
					}
					LoginActivity.this.finish();
					break;

				case WRONG_PWD:
					mEdt_username.setError(Html.fromHtml("<font color=#ff0000>用户名或密码错误</font>"));
					break;
				case NETWORK_ERROR:
					MyProgress.dismiss();
					MyToast.show("网络访问失败");
					break;
				case WRONG_USER_DOMAIN:
					MyProgress.dismiss();
					MyToast.show("用户类型错误");
					break;
				case LOGIN_CONFLICT:
					MyProgress.dismiss();
					MyToast.show("账户在其他设备上登录");
					break;
				case Constant.GET_DATA_ANALYZE_ERROR:
					MyProgress.dismiss();
					MyToast.show("解析数据失败");
					break;
			}
		}
	};

	/*
	 * 点击进行登录
	 */
	public void login(View v) {// TODO
		if (checkUserAndPwd()) {
			MyProgress.show(this, R.layout.progressdialog_login);
			savePWD();

			App.tempUserName = username;
			App.tempPwd = password;

			checkUser();
		}
	}

	private void savePWD() {
		SharedPreferences mySharedPreferences = getSharedPreferences("pwd",
				Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = mySharedPreferences.edit();
		editor.putString("username", username);
		editor.commit();
	}

	private void loadPWD() {
		SharedPreferences sharedPreferences = getSharedPreferences("pwd",
				Activity.MODE_PRIVATE);
		mEdt_username.setText(sharedPreferences.getString("username", ""));
	}

	/**
	 * 将用户改变的数据保存到数据库
	 */
	protected void saveInfoToDB(final Bundle b) {
//		Log.wtf("saveToDB", b.toString());

		if (helper == null) {
			helper = new DataBaseHelper(App.getContext());
		}
		SQLiteDatabase database = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		// values.put("userId", App.USERID);
		values.put("fullname", b.getString("name"));
//		Log.wtf("aaaaaa", b.getString("name"));
		values.put("sex", b.getString("sex"));
		values.put("birthday", b.getString("birth"));
		values.put("height", b.getString("height"));
		values.put("weight", b.getString("weight"));
		values.put("address", b.getString("address"));
		values.put("modtime", b.getString("recordtime"));
		values.put("username", b.getString("username"));
		values.put("icon", b.getString("iconUrl"));
		if (!TextUtils.isEmpty(b.getString("type"))) {
			if ("qq".equals(b.getString("type"))) {
				values.put("qq", b.getString("qq"));
			}
			if ("weixin".equals(b.getString("type"))) {
				values.put("weixin", b.getString("weixin"));
			}
			if ("weibo".equals(b.getString("type"))) {
				values.put("weibo", b.getString("weibo"));
			}
		}
		if ("email".equals(b.get("type"))) {

			values.put("email", b.getString("email"));

		} else {
			values.put("phonenumber", b.getString("mobilephone"));
		}
		values.put("userId", b.getInt("userId"));
		database.replace("patient", null, values);
		database.close();
		helper.close();

	}

	/*
	 * 检查用户名和密码是否正确：false格式错误，true格式正确
	 */
	String username = null;
	String password = null;

	private boolean checkUserAndPwd() {

		username = mEdt_username.getText().toString();
		password = mEdt_password.getText().toString();
		Drawable d = getResources().getDrawable(R.drawable.right);

		d.setBounds(0, 0, 40, 40);// 设置图片尺寸
		if (!mCheck.CheckUsername(username) && !mCheck.isMobile(username)
				&& !mCheck.isMail(username) && !mCheck.isWorkNo(username)) {
			mEdt_username.setError(Html
					.fromHtml("<font color=#ff0000>用户名格式错误</font>"));// 提示错误信息

			return false;
		} else {
			mEdt_username.setCompoundDrawables(null, null, d, null);
			// 设置EditText右边背景
		}
		if (!mCheck.CheckPassword(password)) {
			mEdt_password.setError(Html
					.fromHtml("<font color=#ff0000>密码格式错误</font>"));
			return false;
		} else {

			mEdt_password.setCompoundDrawables(null, null, d, null);
		}
		return true;
	}

	boolean questionnaire;// 是否评估健康因素

	public void checkUser() {
		StringBuilder url = new StringBuilder(App.BASE + Constant.LOGIN);
		JSONObject json = new JSONObject();
		try {
			json.put("password", mEdt_password.getText());
			json.put("username", mEdt_username.getText().toString());
			url.append(json.toString());
			Log.wtf("登录的url", url.toString());
		} catch (Exception e) {
			handler.sendEmptyMessage(Constant.GET_DATA_ANALYZE_ERROR);
		}
		RequestQueue q = App.getRequestQueue();
		q.add(new StringRequest(Request.Method.GET, url.toString(),
				new Listener<String>() {
					@Override
					public void onResponse(String response) {
						try {
							Log.wtf("登录的响应", response);
							JSONObject json = new JSONObject(response);
							String type = json.getString("code");

							if ("206".equals(type)) {//登录成功
								questionnaire = json.getBoolean("questionaire");
								App.USERID = json.getInt("userId");
								App.TOKEN = json.getString("tocken");
								handler.sendEmptyMessage(LOGIN_SUCCESS);
							} else{
								description = json.optString("message");
								handler.sendEmptyMessage(LOGIN_FAILED);
							}
						} catch (JSONException e){
							e.printStackTrace();
							handler.sendEmptyMessage(Constant.GET_DATA_ANALYZE_ERROR);
						}
					}
				}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				handler.sendEmptyMessage(NETWORK_ERROR);
			}
		}));
	}

	/*
	 * 点击进行注册
	 */
	public void register(View v) {

		Intent reg_intent = new Intent(this, RegActivity.class);
		startActivity(reg_intent);

	}

	/*
	 * 忘记密码
	 */
	public void forgotPwd(View v) {
		Intent find_intent = new Intent(this, FindPwdActivity.class);
		startActivity(find_intent);
	}


	@Override
	public void init() {
		setContentView(R.layout.activity_login);
		App.addActivity(this);
		mFindView = new FindView(this);
		mEdt_username = mFindView.findActivityView(R.id.login_edt_username);
		mEdt_password = mFindView.findActivityView(R.id.login_edt_password);
		mBtn_login = mFindView.findActivityView(R.id.login_btn_log);
		mBtn_qq = mFindView.findActivityView(R.id.login_btn_qq);
		mBtn_wx = mFindView.findActivityView(R.id.login_btn_wx);
		mBtn_wb = mFindView.findActivityView(R.id.login_btn_wb);
		mTv_forget = mFindView.findActivityView(R.id.login_tv_forget);
		mTv_reg = mFindView.findActivityView(R.id.login_tv_reg);

		loadPWD();

		mCheck = new Check();
		mBtn_wb.setOnClickListener(this);
		mBtn_qq.setOnClickListener(this);
		mBtn_wx.setOnClickListener(this);
		ShareSDK.initSDK(this);
	}

	/**
	 * 获取数据库中用户所有资料
	 */
	int userid;

	private void getUserData() {
		String log_time;
		if (helper == null) {
			helper = new DataBaseHelper(this);
		}
		SQLiteDatabase dataBase = helper.getReadableDatabase();
		Cursor c = dataBase
				.rawQuery(
						"select * from patient where username=? or phonenumber=? or email=?",
						new String[]{mEdt_username.getText().toString(),
								mEdt_username.getText().toString(),
								mEdt_username.getText().toString()});
		if (c.moveToNext()) {
			if (!TextUtils.isEmpty(c.getString(11))) {
				App.HEAD_IMAGE_URL = c.getString(11);
			}
			if (!TextUtils.isEmpty(c.getString(22))) {
				log_time = c.getString(22);

				checkUser();
			} else {
				checkUser();
			}
		} else {
			checkUser();
		}
		c.close();
		dataBase.close();
		helper.close();
	}

	@Override
	public void click(View v) {
		switch (v.getId()) {
			case R.id.login_btn_wb:
				thirdPartLogin(WEIBO);
				break;
			case R.id.login_btn_qq:
				thirdPartLogin(QQ);
				break;
			case R.id.login_btn_wx:
				thirdPartLogin(WECHAT);
		}
	}

	private static final int WEIBO = 1;
	private static final int QQ = 2;
	private static final int WECHAT = 3;

	Platform platform;

	private void thirdPartLogin(final int kind) {
		MyProgress.show(this, R.layout.progressdialog_login);
		switch (kind) {
			case WEIBO:
				platform = ShareSDK.getPlatform(this, SinaWeibo.NAME);
				break;
			case QQ:
				platform = ShareSDK.getPlatform(this, QZone.NAME);
				break;
			case WECHAT:
				platform = ShareSDK.getPlatform(this, Wechat.NAME);
				break;
		}
		platform.setPlatformActionListener(new PlatformActionListener() {
			@Override
			public void onError(Platform arg0, int arg1, Throwable arg2) {
				description = "登录失败";
				handler.sendEmptyMessage(LOGIN_FAILED);
			}

			@Override
			public void onComplete(Platform arg0, int arg1,
								   HashMap<String, Object> arg2) {
				String openId = platform.getDb().getUserId();
				String nickname = platform.getDb().get("nickname")
						.replace(" ", "hykjnickname");
//				Log.wtf("aaa", nickname+"shi kong de ma ");
				// saveDbLogin(kind, openId); LOGINCACHE
				if (TextUtils.isEmpty(openId)) {
					description = "获取openId失败";
					handler.sendEmptyMessage(LOGIN_FAILED);
				} else {
					goToServer(kind, openId, nickname);
				}
			}

			@Override
			public void onCancel(Platform arg0, int arg1) {
				description = "已取消";
				handler.sendEmptyMessage(LOGIN_FAILED);
			}
		});

		platform.authorize();
	}

	private void saveDbLogin(int kind, String openId) {
		if (helper == null) {
			helper = new DataBaseHelper(App.getContext());
		}
		SQLiteDatabase base = helper.getReadableDatabase();
		Cursor c = null;

		switch (kind) {
			case WEIBO:
				c = base.rawQuery("select * from patient where weibo=?",
						new String[]{openId});
				break;
			case QQ:
				c = base.rawQuery("select * from patient where weixin=?",
						new String[]{openId});
				break;
			case WECHAT:
				c = base.rawQuery("select * from patient where qq=?",
						new String[]{openId});
				break;
		}

		if (c.moveToNext()) {
			String recordTime = c.getString(22);
			if (!TextUtils.isEmpty(recordTime)) {
				goToServer(kind, openId, "");
			} else {
				goToServer(kind, openId, "");
			}
		} else {
			goToServer(kind, openId, "");
		}
	}

	private void goToServer(final int kind,
							final String openId, String nickname) {

		RequestQueue rq = App.getRequestQueue();
		StringBuilder url = new StringBuilder(App.BASE
				+ Constant.THIRDPART_LOGIN);
		JSONObject json = new JSONObject();
		try {
			json.put("recordTime", "none");
			json.put("openId", openId);
			switch (kind) {
				case WEIBO:
					json.put("type", "weiBo");
					App.WEIBO = nickname.replace("hykjnickname", " ");
					break;
				case QQ:
					json.put("type", "QQ");
					App.QQ = nickname.replace("hykjnickname", " ");
					break;
				case WECHAT:
					json.put("type", "weiChat");
					App.WEIXIN = nickname.replace("hykjnickname", " ");
					break;
			}

			json.put("nickName", Decoder.getEncoder(nickname));

		} catch (JSONException e) {
			e.printStackTrace();
		}
		url.append(json.toString());
		MyLog.wtf("第三方登录的请求", url.toString());
		rq.add(new StringRequest(Request.Method.GET, url.toString(),
				new Listener<String>() {
					@Override
					public void onResponse(String response) {
						try {
							MyLog.wtf("第三方登录的响应", response);

							JSONObject json = new JSONObject(response);
							String code = json.getString("code");
							questionnaire = json.getBoolean("questionaire");

							if ("203".equals(code)) {

								App.TOKEN =  json.getString("tocken");
								App.USERID = json.getInt("userId");

								String pwd = json.optString("password");
								if (TextUtils.isEmpty(pwd)) {
									App.tempPwd = App.DEFAULT_PWD;
								} else {
									App.tempPwd = pwd;
								}

								handler.sendEmptyMessage(LOGIN_SUCCESS);
							} else {
								description = json.optString("message");
								handler.sendEmptyMessage(LOGIN_FAILED);
							}
						} catch (JSONException e) {
							handler.sendEmptyMessage(Constant.GET_DATA_ANALYZE_ERROR);
							e.printStackTrace();
						}
					}
				}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				handler.sendEmptyMessage(NETWORK_ERROR);
			}
		}));
	}
}
