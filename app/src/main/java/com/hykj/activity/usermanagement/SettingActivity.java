package com.hykj.activity.usermanagement;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import com.hykj.activity.chat.ChatActivity;
import com.hykj.entity.Doctor;
import com.hykj.utils.Check;
import com.hykj.utils.DensityUtils;
import com.hykj.utils.MyLog;
import com.hykj.utils.MyToast;
import com.hykj.utils.ScreenUtils;
import com.hykj.view.SetPressureView;
import com.hykj.view.SetSugarView;

/**
 * @author 作者 : zhaoyu
 * @version 创建时间：2016年1月21日 上午10:12:08 类说明：个人设置
 */
public class SettingActivity extends BaseActivity {
	private ImageView mImage_back;
	private ImageView mImg_relatives, mImg_pressure, mImg_sugar, mImg_question,
			mImg_contact, mImg_about, mImg_call;
	public TextView mTv_pressure, mTv_sugar;

	@Override
	public void init() {
		setContentView(R.layout.activity_setting);
		initViews();
	}

	private void initViews() {
		mImage_back = (ImageView) findViewById(R.id.setting_tv_back);
		mImage_back.setOnClickListener(this);
		mImg_relatives = (ImageView) findViewById(R.id.setting_imgv_relatives);
		mImg_pressure = (ImageView) findViewById(R.id.setting_imgv_pressure);
		mImg_sugar = (ImageView) findViewById(R.id.setting_imgv_sugar);
		mImg_question = (ImageView) findViewById(R.id.setting_imgv_question);
		mImg_contact = (ImageView) findViewById(R.id.setting_imgv_contact);
		mImg_about = (ImageView) findViewById(R.id.setting_imgv_about);
		mImg_call = (ImageView) findViewById(R.id.setting_imgv_call);
		mTv_pressure = (TextView) findViewById(R.id.setting_tv_pressure);
		mTv_sugar = (TextView) findViewById(R.id.setting_tv_sugar);
		mImg_relatives.setOnClickListener(this);
		mImg_pressure.setOnClickListener(this);
		mImg_sugar.setOnClickListener(this);
		mImg_question.setOnClickListener(this);
		mImg_contact.setOnClickListener(this);
		mImg_about.setOnClickListener(this);
		mImg_call.setOnClickListener(this);
	}

	@Override
	public void click(View v) {
		switch (v.getId()) {
		case R.id.setting_tv_back://返回
			finish();
			break;
		case R.id.setting_imgv_relatives://关联亲人
			showRelativesDialog();
			break;
		case R.id.setting_imgv_pressure://目标血压
			
			showPressureDialog();
			break;
		case R.id.setting_imgv_sugar://目标血糖
			Intent intent = new Intent(this, SetSugarView.class);
			startActivity(intent);
			break;
		case R.id.setting_imgv_question://帮助
			Intent qusetion_intent = new Intent(this, QuestionActivity.class);
			startActivity(qusetion_intent);
			break;
		case R.id.setting_imgv_contact://联系客服
			Intent chat_intent = new Intent(this, ChatActivity.class);
			Uri userNameUri = Uri.parse("admin"+App.OPENFIRE_SERVICE_NAME);
			chat_intent.setData(userNameUri);
			Doctor doctor = new Doctor();
			doctor.setIconUrl(App.DEFULT_PHOTO);
			doctor.setAge("22");
			doctor.setFriend(true);
			doctor.setHospitalName("三高随便问");
			doctor.setName("天衡小秘书");
			doctor.setPrifile("天衡医疗");
			doctor.setSex("女");
			doctor.setUserId(54);
			doctor.setWorkNum("18");
//			chat_intent.putExtra("doctor", doctor);
			App.doctor = doctor;
			this.startActivity(chat_intent);

			break;
		case R.id.setting_imgv_about://关于天衡
			Intent about_intent = new Intent(this, AboutActivity.class);
			startActivity(about_intent);
			break;
		case R.id.setting_imgv_call://拨打电话
			showContactDialog();
			break;
		default:
			break;
		}
	}

	/*
	 * 设置血压目标
	 */
	private void showPressureDialog() {
		SetPressureView view = new SetPressureView(this,this);
		view.setCanceledOnTouchOutside(false);
		view.setCancelable(false);
		view.show();
		
		Window dialogWindow = view.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.BOTTOM);
		WindowManager m = dialogWindow.getWindowManager();
		Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
		lp.height = (int) (d.getHeight() * 0.5); // 高度设置为屏幕的0.6
		lp.width = ScreenUtils.getScreenWidth(App.getContext()); // 宽度设置为屏幕的0.65
		dialogWindow.setAttributes(lp);

	}

	/*
	 * 绑定亲人
	 */
	 Dialog dialog;
	private void showRelativesDialog() {
		 dialog = new Dialog(this);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setCancelable(false);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_relatives);
		Window dialogWindow = dialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.CENTER);
		WindowManager m = dialogWindow.getWindowManager();
		Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
		lp.height = (int) (d.getHeight() * 0.4); // 高度设置为屏幕的0.6
		lp.width = (int) (d.getWidth() * 0.95); // 宽度设置为屏幕的0.65
		dialogWindow.setAttributes(lp);
		dialogWindow.setAttributes(lp);
		dialog.show();
		Button btn_next = (Button) dialog
				.findViewById(R.id.dialog_btn_relatives_next);
		Button btn_cancel = (Button) dialog
				.findViewById(R.id.dialog_btn_relatives_cancel);
		final EditText edt_phone = (EditText) dialog
				.findViewById(R.id.dialog_edt_relatives_phone);
		btn_next.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (Check.isMobile(edt_phone.getText().toString())) {
					sendToTarget(edt_phone.getText().toString());
				} else {
					edt_phone.setError("电话号码有误！");
				}

			}
		});
		btn_cancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				dialog.cancel();
			}
		});
	}

	/*
	 * 请求服务器发送亲人电话号码
	 */
	protected void sendToTarget(String string) {
		RequestQueue rq = App.getRequestQueue();
		StringBuilder url = new StringBuilder(App.BASE
				+ Constant.BIND_RELATIVES);
		url.append("data=");
		JSONObject json = new JSONObject();
		try {
			json.put("mobilephone", string);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		url.append(json.toString());
		url.append("&tocken="+App.TOKEN);
		MyLog.wtf("urlrelative", url.toString());
		rq.add(new StringRequest(Request.Method.GET, url.toString(),
				new Listener<String>() {

					@Override
					public void onResponse(String response) {
						try {
							JSONObject json = new JSONObject(response);
							String code = json.getString("code");
							if ("206".equals(code)) {
								mHanlder.sendEmptyMessage(1);
							} else {
								mHanlder.sendEmptyMessage(2);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						mHanlder.sendEmptyMessage(-1);
					}
				}));

	}

	public Handler mHanlder = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				MyToast.show("请求发送成功！");
				dialog.cancel();
				break;
			case 2:
				MyToast.show("请求发送失败！");
				break;
			case -1:
				MyToast.show("请求发送失败！");
				break;
			default:
				break;
			}
		}
	};

	/*
	 * 联系客服对话框
	 */
	private void showContactDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("联系客服").setMessage("拨打客服电话：400-0603-999  ？")
				.setPositiveButton("取消", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				}).setNegativeButton("拨打", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent();
						intent.setAction(Intent.ACTION_CALL);
						intent.setData(Uri.parse("tel:" + "400-0603-999"));
						startActivity(intent);
						dialog.cancel();
					}
				}).create().show();
	}

}
