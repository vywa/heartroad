package com.hykj.activity.usermanagement;

/**
 * @author  作者：赵宇   	
 * @version 1.0
 创建时间：2015年10月16日 上午10:20:35
 * 类说明：注册界面
 */

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
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
import com.hykj.db.DataBaseHelper;
import com.hykj.service.RegisterCodeTimerService;
import com.hykj.utils.Check;
import com.hykj.utils.FindView;
import com.hykj.utils.MyDialog;
import com.hykj.utils.MyProgress;
import com.hykj.utils.MyToast;
import com.hykj.view.RegisterCodeTimer;

import org.json.JSONException;
import org.json.JSONObject;

public class RegActivity extends BaseActivity{

	private EditText mEdt_num, mEdt_code, mEdt_pwd1, mEdt_pwd2;// 手机号，验证码，密码，重复密码输入框
	private TextView mTv_tel;
	private Button mBtn_reg;// 注册
	private Button mBtn_code;// 获取验证码按钮
	private FindView mFindView;// 初始化组件工具
	private Button mBtn_phone, mBtn_email;// 手机注册， 邮箱注册
	private Check mCheck;// 检查格式
	private int method = 1;// 方式1为手机注册，方式2为邮箱注册
	private SpannableString spanText;
	private ImageView mImg_back;// 返回键
	private Intent mIntent;// 发送验证码跳转服务

	@Override
	public void init() {
		App.addActivity(this);

		setContentView(R.layout.activity_register);
		mFindView = new FindView(this);
		mEdt_num = mFindView.findActivityView(R.id.reg_edt_phone);
		mEdt_num.addTextChangedListener(new TextWatcher() {
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
		mTv_tel = mFindView.findActivityView(R.id.reg_tv_text2);
		mEdt_code = mFindView.findActivityView(R.id.reg_edt_code);
		mEdt_pwd1 = mFindView.findActivityView(R.id.reg_edt_pwd1);
		mEdt_pwd2 = mFindView.findActivityView(R.id.reg_edt_pwd2);
		mBtn_code = (Button) findViewById(R.id.reg_btn_code);
		RegisterCodeTimerService.setHandler(mHandler);
		mIntent = new Intent(this, RegisterCodeTimerService.class);

		mBtn_reg = mFindView.findActivityView(R.id.reg_btn_next);
		mBtn_phone = mFindView.findActivityView(R.id.reg_btn_phone);
		mBtn_email = mFindView.findActivityView(R.id.reg_btn_email);
		mImg_back = mFindView.findActivityView(R.id.reg_img_back);
		mImg_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				RegActivity.this.finish();
			}
		});

		mBtn_phone.setSelected(true);

		mBtn_reg.setOnClickListener(this);
		mBtn_phone.setOnClickListener(this);
		mBtn_email.setOnClickListener(this);
		mBtn_code.setOnClickListener(this);

		mCheck = new Check();
		spanText = new SpannableString("注册遇到问题请拨打客服热线"
				+ Html.fromHtml("<h5>400-0603-999</h5>"));
		spanText.setSpan(new ClickableSpan() {
			@Override
			public void updateDrawState(TextPaint ds) {
				super.updateDrawState(ds);
				ds.setColor(Color.parseColor("#009aa9"));
				ds.setTextSize(30);
			}

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_CALL);
				intent.setData(Uri.parse("tel:" + "400-0603-999"));
				startActivity(intent);
			}
		}, 13, spanText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		mTv_tel.setHighlightColor(Color.TRANSPARENT);
		mTv_tel.append(spanText);
		mTv_tel.setMovementMethod(LinkMovementMethod.getInstance());

	}

	private boolean checkUserName() {
		String nums = mEdt_num.getText().toString();
		Drawable d = getResources().getDrawable(R.drawable.right);
		d.setBounds(0, 0, 40, 40);
		if (method == 1) {
			if (!Check.isMobile(nums)) {
				mEdt_num.setError(Html
						.fromHtml("<font color=#ff0000>电话号格式错误</font>"));
				return false;
			} else {
				mEdt_num.setCompoundDrawables(null, null, d, null);
			}
		}
		if (method == 2) {

			if (!Check.isMail(nums)) {
				mEdt_num.setError(Html
						.fromHtml("<font color=#ff0000>邮箱格式错误</font>"));
				return false;
			} else {
				mEdt_num.setCompoundDrawables(null, null, d, null);
			}
		}

		return true;

	}

	/*
	 * 检查用户名和密码是否符合要求
	 */

	private boolean checkUserAndPwd() {

		String nums = mEdt_num.getText().toString();
		String phone_pwd1 = mEdt_pwd1.getText().toString();
		String phone_pwd2 = mEdt_pwd2.getText().toString();
		Drawable d = getResources().getDrawable(R.drawable.right);
		d.setBounds(0, 0, 40, 40);
		if (method == 1) {
			if (!Check.isMobile(nums)) {

				mEdt_num.setError(Html
						.fromHtml("<font color=#ff0000>电话号格式错误</font>"));

				return false;
			} else {
				mEdt_num.setCompoundDrawables(null, null, d, null);
			}
		}
		if (method == 2) {

			if (!Check.isMail(nums)) {
				mEdt_num.setError(Html
						.fromHtml("<font color=#ff0000>邮箱格式错误</font>"));
				return false;
			} else {
				mEdt_num.setCompoundDrawables(null, null, d, null);
			}
		}

		if (!Check.CheckPassword(phone_pwd1)) {
			mEdt_pwd1.setError(Html
					.fromHtml("<font color=#ff0000>密码格式错误</font>"));
			return false;
		} else {
			mEdt_pwd1.setCompoundDrawables(null, null, d, null);
		}
		if (!Check.CheckPassword(phone_pwd2)) {
			mEdt_pwd2.setError(Html
					.fromHtml("<font color=#ff0000>密码格式错误</font>"));
			return false;
		} else {
			mEdt_pwd2.setCompoundDrawables(null, null, d, null);
		}
		if (!phone_pwd1.equals(phone_pwd2)) {
			mEdt_pwd2.setError(Html
					.fromHtml("<font color=#ff0000>密码不一致</font>"));
			return false;
		}
		return true;

	}

	@SuppressLint("NewApi")
	@Override
	public void click(View v) {
		switch (v.getId()) {
		case R.id.reg_btn_phone:// 手机注册
			mEdt_num.setError(null);
			mEdt_pwd1.setError(null);
			mEdt_pwd2.setError(null);
			mBtn_phone.setBackgroundResource(R.drawable.reg_border);
			mBtn_email.setBackground(null);
			mEdt_num.setText("");
			mEdt_code.setText("");
			mEdt_pwd1.setText("");
			mEdt_pwd2.setText("");
			method = 1;
			if (mBtn_email.isSelected()) {
				method = 2;
				mEdt_num.setHint("请输入邮箱");
				mEdt_num.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);// 设置输入类型为邮箱地址
				break;
			}
			if (mBtn_phone.isSelected()) {
				method = 1;
				mEdt_num.setHint("手机号（限中国大陆地区）");
				mEdt_num.setInputType(InputType.TYPE_CLASS_PHONE);// 设置输入类型为电话号
				break;
			}
			break;
		case R.id.reg_btn_email:// 邮箱注册
			mEdt_num.setError(null);
			mEdt_pwd1.setError(null);
			mEdt_pwd2.setError(null);
			mBtn_email.setBackgroundResource(R.drawable.reg_border);
			mBtn_phone.setBackground(null);
			mEdt_num.setText("");
			mEdt_code.setText("");
			mEdt_pwd1.setText("");
			mEdt_pwd2.setText("");
			if (mBtn_email.isSelected()) {
				method = 1;
				mEdt_num.setHint("手机号（限中国大陆地区）");
				mEdt_num.setInputType(InputType.TYPE_CLASS_PHONE);// 设置输入类型为电话号
				break;
			}
			if (mBtn_phone.isSelected()) {
				method = 2;
				mEdt_num.setHint("请输入邮箱");

				mEdt_num.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);// 设置输入类型为邮箱地址
				break;
			}
			break;

		case R.id.reg_btn_code:// 获取验证码

			if (checkUserName()) {
				MyProgress.show(this, R.layout.progressdialog_getcode);
				validate();
			}

			break;
		case R.id.reg_btn_next:// 点击进行注册
			if (checkUserAndPwd()) {
				MyProgress.show(this, R.layout.progressdialog_getcode);
				App.tempUserName = mEdt_num.getText().toString();
				App.tempPwd = mEdt_pwd1.getText().toString();
				RegisterUser();
			}
			break;
		}
	}

	/**
	 * 验证
	 */

	// 验证码reglog/smsSend

	private void validate() {
		RequestQueue rq = App.getRequestQueue();
		StringBuilder code = null;
		if (method == 1) {
			code = new StringBuilder(App.BASE + Constant.SMS_REGIST);
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put("mobilephone", mEdt_num.getText());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			code.append("data=");
			code.append(jsonObject.toString());
		}
		if (method == 2) {
			code = new StringBuilder(App.BASE + Constant.MAIL_REGIST);
			JSONObject jsonObject1 = new JSONObject();
			try {
				jsonObject1.put("email", mEdt_num.getText());
				// Log.wtf("aaaaaaaaaa", mEdt_num.getText().toString());
			} catch (Exception e) {
			}
			code.append("data=");
			code.append(jsonObject1.toString());
		}
		// Log.wtf("获取验证码的url", code.toString());
		rq.add(new StringRequest(Request.Method.GET, code.toString(),
				new Listener<String>() {
					@Override
					public void onResponse(String response) {
						// Log.wtf("获取验证码返回的信息", response);
						try {
							JSONObject json = new JSONObject(response);
							String type = json.getString("code");
							if ("102".equals(type)) {
								// 用户已存在
								MyProgress.dismiss();
								mHandler.sendEmptyMessage(102);
							}
							if ("106".equals(type)) {
								// 发送次数过多
								MyProgress.dismiss();
								mHandler.sendEmptyMessage(106);
							}
							if ("205".equals(type)) {// 验证码发送成功
								MyProgress.dismiss();
								startService(mIntent);
							}

						} catch (JSONException e) {
							e.printStackTrace();
							MyProgress.dismiss();
							mHandler.sendEmptyMessage(108);// 网络错误
						}
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						MyProgress.dismiss();
						mHandler.sendEmptyMessage(108);// 网络错误
					}
				}));
		rq.start();
	}

	/**
	 * 注册用户，发送注册请求，获取用户唯一标识userid
	 */
	private void RegisterUser() {
		// 注册 reglog/smsReceive
		RequestQueue rq = App.getRequestQueue();
		StringBuilder url = null;
		JSONObject json = null;
		try {
			if (mBtn_phone.isSelected())
				if (method == 1) {
					url = new StringBuilder(App.BASE + Constant.SMS_RECEIVE);
					url.append("data=");
					json = new JSONObject();
					json.put("mobilephone", mEdt_num.getText());
					json.put("code", mEdt_code.getText());
				}
			if (method == 2) {
				url = new StringBuilder(App.BASE + Constant.MAIL_RECEIVE);
				url.append("data=");
				json = new JSONObject();
				json.put("email", mEdt_num.getText());
				json.put("code", mEdt_code.getText());
			}

			json.put("password", mEdt_pwd1.getText());

			url.append(json.toString());
			// Log.wtf("aaaaaaa", url.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}

		rq.add(new StringRequest(Request.Method.GET, url.toString(),
				new Listener<String>() {
					@Override
					public void onResponse(String response) {
						// Log.wtf("sssssssss", response);
						try {
							JSONObject o = new JSONObject(response);// 整个JSONOject
							String result = o.getString("code");
							String userid = o.getString("message");
							if ("205".equals(result)) { // 成功
								Message msg = mHandler.obtainMessage(0);
								msg.obj = userid;
								msg.sendToTarget();
							} else if ("107".equals(result)) {
								mHandler.sendEmptyMessage(2);// 验证码错误
							} else if ("106".equals(result)) {
								mHandler.sendEmptyMessage(109);// 验证码输入次数过多，请稍后进行注册
							}

						} catch (JSONException e) {
							e.printStackTrace();
							mHandler.sendEmptyMessage(108);
						}
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// Log.wtf("error", error.networkResponse + "");
						mHandler.sendEmptyMessage(108);
					}
				}));
		rq.start();
	}

	int userid;
	private Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:// 网络访问成功
				userid = Integer.parseInt((String) msg.obj);
				// Log.wtf("aaaaa", userid + "");
//				createChatAccount(userid + "");
				MyProgress.dismiss();
				MyDialog.showDialog(RegActivity.this,
						R.layout.dialog_reg_success);
				mHandler.sendEmptyMessageDelayed(-2, 1000);
				break;
			case 2:// 验证码错误
				MyProgress.dismiss();
				MyToast.show("验证码错误");
				break;

			case -1:
				// LYLToast.show("注册成功！");
				MyProgress.dismiss();
				MyDialog.showDialog(RegActivity.this,
						R.layout.dialog_reg_success);
				mHandler.sendEmptyMessageDelayed(-2, 1000);
				break;
			case -2:
				MyDialog.cancelDialog();
				finish();
				break;
			case 102:
				MyProgress.dismiss();
				MyToast.show("用户已被注册");
				mBtn_code.setText("获取验证码");
				mBtn_code.setEnabled(true);
				break;
			case 106:
				MyToast.show("验证码发送次数过多");
				mBtn_code.setText("获取验证码");
				mBtn_code.setEnabled(true);
				break;
			case 108:
				MyToast.show("网络连接错误");
				break;
			case 109:
				MyToast.show("验证码错误次数过多，请稍后进行注册");
				break;
			case RegisterCodeTimer.IN_RUNNING:
				mBtn_code.setEnabled(false);
				mBtn_code.setText(msg.obj.toString());
				break;
			case RegisterCodeTimer.END_RUNNING:
				mBtn_code.setEnabled(true);
				mBtn_code.setText(msg.obj.toString());
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		stopService(mIntent);
	}

	/**
	 * 用户信息（用户名，密码，userid）保存到数据库
	 */
	protected void savedToDb() {
		new Thread() {

			@Override
			public void run() {

				DataBaseHelper baseHelper = new DataBaseHelper(App.getContext());
				SQLiteDatabase database = baseHelper.getWritableDatabase();
				ContentValues values = new ContentValues();

				values.put("userId", userid);

				if (mEdt_num.getText().toString().contains("@")) {
					values.put("email", mEdt_num.getText().toString());

				} else {
					values.put("phonenumber", mEdt_num.getText().toString());
				}

				database.insert("patient", null, values);

				database.close();
				baseHelper.close();

			}

		}.start();
		MyDialog.cancelDialog();
		this.finish();
	}
}
