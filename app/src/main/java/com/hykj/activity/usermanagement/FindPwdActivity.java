package com.hykj.activity.usermanagement;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.IBinder;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

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
import com.hykj.service.RegisterCodeTimerService;
import com.hykj.utils.Check;
import com.hykj.utils.FindView;
import com.hykj.utils.MyLog;
import com.hykj.utils.MyProgress;
import com.hykj.utils.MyToast;
import com.hykj.utils.MyDialog;
import com.hykj.view.RegisterCodeTimer;
import com.hykj.view.TimeButton;

/**
 * @author 作者：赵宇
 * @version 1.0 创建时间：2015年10月19日 上午9:47:53 类说明：忘记密码
 */
public class FindPwdActivity extends BaseActivity{
	private EditText mEdt_phonenms, mEdt_code, mEdt_pwd1, mEdt_pwd2;// 手机号，验证码，新密码，确认密码输入框
	private Button mBtn_submit;// 提交按钮
	private Button mBtn_code;// 获取验证码
	private FindView mFindView;// 初始化组件工具
	private Check mCheck;// 检查格式
	private ImageView mImg_back;// 返回键
	private Intent mIntent;

	private String desc = "";

	@Override
	public void init() {
		App.addActivity(this);
		setContentView(R.layout.activity_forgot_pwd);
		mFindView = new FindView(this);
		mEdt_phonenms = mFindView.findActivityView(R.id.forgot_edt_phonenbs);
		mEdt_phonenms.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
									  int arg3) {
				if (TextUtils.isEmpty(arg0.toString())) {
					mBtn_code.setEnabled(false);
				} else {
					mBtn_code.setEnabled(true);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
										  int arg2, int arg3) {
				if (TextUtils.isEmpty(arg0.toString())) {
					mBtn_code.setEnabled(false);
				} else {
					mBtn_code.setEnabled(true);
				}
			}

			@Override
			public void afterTextChanged(Editable arg0) {
				if (TextUtils.isEmpty(arg0.toString())) {
					mBtn_code.setEnabled(false);
				} else {
					mBtn_code.setEnabled(true);
				}
			}
		});
		mEdt_code = mFindView.findActivityView(R.id.forgot_edt_code);
		mEdt_pwd1 = mFindView.findActivityView(R.id.forgot_edt_pwd1);
		mEdt_pwd2 = mFindView.findActivityView(R.id.forgot_edt_pwd2);
		mBtn_code = mFindView.findActivityView(R.id.forgot_btn_code);
		RegisterCodeTimerService.setHandler(mHandler);
		mIntent = new Intent(this, RegisterCodeTimerService.class);

		mBtn_submit = mFindView.findActivityView(R.id.forgot_btn_submit);
		mBtn_code.setOnClickListener(this);
		mBtn_submit.setOnClickListener(this);
		mCheck = new Check();
		mImg_back = mFindView.findActivityView(R.id.change_img_back);
		mImg_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FindPwdActivity.this.finish();
			}
		});
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case -3:
					MyProgress.dismiss();
					MyToast.show("验证码错误");
					break;
				case -2:
					MyProgress.dismiss();
					MyToast.show("用户不存在");
					break;
				case -1:
					MyProgress.dismiss();
					MyToast.show("密码修改失败");
					break;
				case 0:
					MyDialog.cancelDialog();
					FindPwdActivity.this.finish();
					break;
				case 3:
					MyToast.show("验证码发送成功");
					break;
				case 4:
					mBtn_code.setText("获取验证码");
					mBtn_code.setEnabled(true);
					MyToast.show("用户尚未注册");
					break;
				case 5:
					mBtn_code.setText("获取验证码");
					mBtn_code.setEnabled(true);
					MyToast.show("验证码发送次数过多");
					break;
				case 108:
					MyProgress.dismiss();
					MyToast.show("网络连接错误");
					break;
				case RegisterCodeTimer.IN_RUNNING:
					mBtn_code.setEnabled(false);
					mBtn_code.setText(msg.obj.toString());
					break;
				case RegisterCodeTimer.END_RUNNING:
					mBtn_code.setEnabled(true);
					mBtn_code.setText(msg.obj.toString());
					break;
				case 100:
					MyProgress.dismiss();
					MyToast.show(desc);
					break;
			}
		}
	};

	/**
	 * 修改的密码保存到数据库
	 */
	/*
	 * protected void saveToDb() { if(baseHelper==null){ baseHelper=new
	 * DataBaseHelper(this); } SQLiteDatabase
	 * dataBase=baseHelper.getWritableDatabase(); ContentValues values=new
	 * ContentValues(); values.put("password", mEdt_pwd1.getText().toString());
	 * dataBase.update("userinfo", values, "phonenumber=? or email=?", new
	 * String
	 * []{mEdt_phonenms.getText().toString(),mEdt_phonenms.getText().toString
	 * ()}); dataBase.execSQL(
	 * "update patient set password=? where phonenumber=? or email=?", new
	 * String
	 * []{mEdt_pwd1.getText().toString().trim(),mEdt_phonenms.getText().toString
	 * ().trim(),mEdt_phonenms.getText().toString().trim()});
	 * 
	 * dataBase.close(); baseHelper.close(); FindPwdActivity.this.finish(); }
	 */
	String newPWD = "";

	@Override
	public void click(View v) {
		switch (v.getId()) {
			case R.id.forgot_btn_submit: // 点击进行提交
				if (checkUserAndPwd()) {
					newPWD = mEdt_pwd1.getText().toString();
					MyProgress.show(this, R.layout.progressdialog_getcode);
					changePwd();
				}
				break;

			default:// 获取验证码
				String nums = mEdt_phonenms.getText().toString();
				if (Check.isMobile(nums)) {
					MyProgress.show(this, R.layout.progressdialog_getcode);
					getCode();
				} else {
					mEdt_phonenms.setError(Html
							.fromHtml("<font color=#ff0000>电话号格式错误</font>"));
				}
				break;
		}
	}
	/*
	 * 验证手机号/邮箱，密码是否符合要求
	 */

	private boolean checkUserAndPwd() {
		String username = mEdt_phonenms.getText().toString();
		String pwd1 = mEdt_pwd1.getText().toString();
		String pwd2 = mEdt_pwd2.getText().toString();
		Drawable d = getResources().getDrawable(R.drawable.right);
		d.setBounds(0, 0, 40, 40);
		if (!Check.isMobile(username)) {
			mEdt_phonenms.setError(Html
					.fromHtml("<font color=#ff0000>电话号格式错误</font>"));
			return false;
		} else {
			mEdt_phonenms.setCompoundDrawables(null, null, d, null);
		}
		if (!Check.CheckPassword(pwd1)) {
			mEdt_pwd1.setError(Html
					.fromHtml("<font color=#ff0000>密码格式错误</font>"));
			return false;
		} else {
			mEdt_pwd1.setCompoundDrawables(null, null, d, null);
		}
		if (!Check.CheckPassword(pwd2)) {
			mEdt_pwd2.setError(Html
					.fromHtml("<font color=#ff0000>密码格式错误</font>"));
			return false;
		} else {
			mEdt_pwd2.setCompoundDrawables(null, null, d, null);
		}
		if (!pwd1.equals(pwd2)) {
			mEdt_pwd2.setError(Html
					.fromHtml("<font color=#ff0000>密码不一致</font>"));
			return false;
		}
		return true;
	}

	String userId;

	/**
	 * 修改密码
	 */
	private void changePwd() {
		RequestQueue rq = App.getRequestQueue();
		StringBuilder url = new StringBuilder(App.BASE
				+ Constant.CHANGE_PASSWORD);
		url.append("data=");
		JSONObject json = new JSONObject();
		try {
			json.put("mobilephone", mEdt_phonenms.getText());
			json.put("code", mEdt_code.getText());
			json.put("password", mEdt_pwd1.getText());
			url.append(json.toString());
		} catch (Exception e) {

		}
		MyLog.wtf("修改密码的url", url.toString());
		rq.add(new StringRequest(Request.Method.GET, url.toString(),
				new Listener<String>() {

					@Override
					public void onResponse(String response) {
						try {
							MyLog.wtf("修改密码的响应", response);
							JSONObject json = new JSONObject(response);
							String code = json.getString("code");
							if ("206".equals(code)) { // 密码修改成功
								userId = json.optString("message");
								MyProgress.dismiss();
								MyDialog.showDialog(FindPwdActivity.this,
										R.layout.dialog_modifypwd_success);
								mHandler.sendEmptyMessageDelayed(0, 1000);
							} else if ("107".equals(code)) { // 验证码错误
								mHandler.sendEmptyMessage(-3);
							} else if ("103".equals(code)) {// 用户尚未注册
								mHandler.sendEmptyMessage(-2);
							} else {
								desc = json.optString("message");
								mHandler.sendEmptyMessage(100);
							}

						} catch (Exception e) {
							e.printStackTrace();
							mHandler.sendEmptyMessage(108);
						}

					}
				}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				mHandler.sendEmptyMessage(108);

			}
		}));
		rq.start();
	}

	/**
	 * 访问网络获取验证码
	 */
	private void getCode() {
		RequestQueue rq = App.getRequestQueue();
		StringBuilder url = new StringBuilder(App.BASE
				+ Constant.GETCODE_PASSWORD);
		url.append("data=");
		JSONObject o = new JSONObject();
		Log.wtf("aaa", url.toString());
		try {
			if (mEdt_phonenms.getText().toString().contains("@")) {
				o.put("email", mEdt_phonenms.getText().toString());
			} else {
				o.put("mobilephone", mEdt_phonenms.getText().toString());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		url.append(o.toString());
		MyLog.wtf("aaa", url.toString());
		rq.add(new StringRequest(Request.Method.GET, url.toString(),
				new Listener<String>() {

					@Override
					public void onResponse(String response) {
						try {
							JSONObject json = new JSONObject(response);
							String code = json.getString("code");
							Log.wtf("code", code);
							if ("205".equals(code)) {
								MyProgress.dismiss();
								startService(mIntent);
								mHandler.sendEmptyMessage(3);
							} else if ("103".equals(code)) {// 用户未注册
								MyProgress.dismiss();
								mHandler.sendEmptyMessage(4);
							} else if ("106".equals(code)) {// 验证码发送次数过多
								MyProgress.dismiss();
								mHandler.sendEmptyMessage(5);
							} else {
								MyProgress.dismiss();
							}
						} catch (JSONException e) {
							e.printStackTrace();
							MyProgress.dismiss();
							mHandler.sendEmptyMessage(108);
						}
					}
				}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				MyProgress.dismiss();
				mHandler.sendEmptyMessage(108);
			}
		}));
		rq.start();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		stopService(mIntent);
	}
}
