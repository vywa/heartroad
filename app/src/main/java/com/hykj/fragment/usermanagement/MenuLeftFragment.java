/**
 *
 */
package com.hykj.fragment.usermanagement;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hykj.App;
import com.hykj.Constant;
import com.hykj.R;
import com.hykj.activity.usermanagement.BindUserActivity;
import com.hykj.activity.usermanagement.CollectInfoActivity;
import com.hykj.activity.usermanagement.ModifyDataActivity;
import com.hykj.activity.usermanagement.SettingActivity;
import com.hykj.db.DataBaseHelper;
import com.hykj.manager.GetPhotoManager;
import com.hykj.utils.MyToast;
import com.hykj.utils.OnUploadStateListener;
import com.hykj.utils.XHttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;

/**
 * @author zhaoyu
 * @version 1.0 创建时间：2015年12月21日 类说明：左侧滑栏fragment
 */
public class MenuLeftFragment extends Fragment implements OnClickListener, Serializable {
	private View v;
	private TextView mTv_bind, mTv_info, mTv_health, mTv_quit, mTv_msg, mTv_integral, mTv_setting, mTv_order;
	private SimpleDraweeView mImg_head;
	public TextView mTv_name;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		v = inflater.inflate(R.layout.fragment_menuleft, null);
		initNormal();
		return v;
	}

	@SuppressLint("NewApi")
	private void initNormal() {
		mTv_info = (TextView) v.findViewById(R.id.menu_tv_info);
		mTv_health = (TextView) v.findViewById(R.id.menu_tv_healtch);
		mTv_health.setOnClickListener(this);
		mTv_info.setOnClickListener(this);
		mTv_order = (TextView) v.findViewById(R.id.menu_tv_order);
		mTv_order.setOnClickListener(this);
		mImg_head = (SimpleDraweeView) v.findViewById(R.id.menu_imgv_headicon);
		mTv_name = (TextView) v.findViewById(R.id.menu_tv_name);
		mTv_bind = (TextView) v.findViewById(R.id.menu_tv_bind);
		mTv_quit = (TextView) v.findViewById(R.id.menu_tv_quit);
		mTv_msg = (TextView) v.findViewById(R.id.menu_tv_message);
		mTv_integral = (TextView) v.findViewById(R.id.menu_tv_integral);
		mTv_setting = (TextView) v.findViewById(R.id.menu_tv_setting);
		mTv_quit.setOnClickListener(this);
		mImg_head.setOnClickListener(this);
		mTv_bind.setOnClickListener(this);
		mTv_msg.setOnClickListener(this);
		mTv_integral.setOnClickListener(this);
		mTv_setting.setOnClickListener(this);
		mImg_head.setOnClickListener(this);
		getDataFromServer();
	}

	private void getDataFromServer() {
		RequestQueue q = App.getRequestQueue();
		final String url = App.BASE + Constant.GETSELFINFO;
		q.add(new StringRequest(Request.Method.GET, url + "tocken=" + App.TOKEN,
				new Listener<String>() {
					@Override
					public void onResponse(String response) {
						try {
							JSONObject json = new JSONObject(response);

							Log.wtf("aaa", url + "tocken=" + App.TOKEN);
							JSONObject account = json.optJSONObject("account");

							String username = account.optString("username");
							App.USERNAME = username;
							String phone = account.optString("mobilephone");
							App.PHONE = phone;
							Log.wtf("aaa",App.PHONE);
							String email = account.optString("email");
							App.EMAIL = email;
							Log.wtf("aaa",App.USERNAME+"===============================");

							if (TextUtils.isEmpty(App.USERNAME)) {
								Log.wtf("aaa", "null" + App.USERNAME + "--------------------------------");
								mTv_name.setText("请完善用户名");
							} else {
								Log.wtf("aaa", "has" + App.USERNAME);
								mTv_name.setText(App.USERNAME);
							}

							JSONObject info=json.optJSONObject("patientInfo");

							String name = info.optString("trueName");
							App.TRUENAME = name;
							String sex = info.optString("sex");
							App.SEX = sex;
							String birth = info.optString("birthday");
							App.BIRTHDAY = birth;
							String age = info.optString("age");
							App.AGE = age;
							String height = info.optString("height");
							App.HEIGHT = height;
							String weight = info.optString("weight");
							App.WEIGHT = weight;
							String address = info.optString("address");
							App.ADDRESS = address;
							String icon = info.optString("iconUrl");
							App.HEAD_IMAGE_URL = icon;

							String qq = info.optString("qqName").replace("hykjnickname", " ");
							App.QQ = qq;
							String weixin = info.optString("weiChatName").replace("hykjnickname", " ");
							App.WEIXIN = weixin;
							String weibo = info.optString("weiBoName").replace("hykjnickname", " ");
							App.WEIBO = weibo;
							mHandler.sendEmptyMessage(100);
						} catch (Exception e) {
							e.printStackTrace();
							mHandler.sendEmptyMessage(5);
						}
					}
				}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				mHandler.sendEmptyMessage(5);
			}
		}));
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
			case -21:
				setHeadImage();
				break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/*
	 * 提取保存裁剪之后的图片，设置头像
	 */
	Bitmap photo;

	@SuppressLint("NewApi")
	private void setHeadImage() {

		upLoadImage2Server();
	}

	/**
	 * 根据userid查询数据库
	 */

	String head_image_url;
	DataBaseHelper baseHelper;

	private void goToDb() {
		if (baseHelper == null) {
			baseHelper = new DataBaseHelper(getActivity());
		}
		SQLiteDatabase dataBase = baseHelper.getWritableDatabase();
		Cursor c = dataBase.rawQuery("select * from patient where userId=?", new String[]{String.valueOf(App.USERID)});

		if (c.moveToNext()) {

			String username = c.getString(10);
			String icon = c.getString(11);
			App.HEAD_IMAGE_URL = icon;
			App.USERNAME = username;
			if (TextUtils.isEmpty(icon)) {
//				mImg_head.setDefaultImageResId(R.drawable.start);
			} else {
//				mImg_head.setImageUrl(icon, new ImageLoader(App.getRequestQueue(), BitmapLruCache.instance()));
			}
		}
		c.close();
		dataBase.close();
		baseHelper.close();
	}

	private void go2Server() {
		RequestQueue rq = App.getRequestQueue();

		rq.add(new StringRequest(Request.Method.GET, App.BASE + "reglog/logout?tocken=" + App.TOKEN, new Listener<String>() {

			@Override
			public void onResponse(String response) {
				try {
					JSONObject json = new JSONObject(response);
					String code = json.optString("code");
					if ("206".equals(code)) {
						mHandler.sendEmptyMessage(-2);
					} else {
						mHandler.sendEmptyMessage(-3);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				error.printStackTrace();
				mHandler.sendEmptyMessage(-3);
			}
		}));
		rq.start();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (TextUtils.isEmpty(App.USERNAME)) {
			mTv_name.setText("请完善用户名");
		} else {
			mTv_name.setText(App.USERNAME);
		}
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
			case R.id.menu_tv_info:// 基本资料
				Intent intent = new Intent(getActivity(), ModifyDataActivity.class);
				getActivity().startActivity(intent);

				break;
			case R.id.menu_tv_healtch:// 我的收藏
			case 4:// 获取收藏信息成功
				Intent collect_intent = new Intent(getActivity(), CollectInfoActivity.class);
				startActivity(collect_intent);

				break;
			case R.id.menu_imgv_headicon:// 头像
				Intent intent1 = new Intent(getActivity(), GetPhotoManager.class);
				intent1.putExtra("isCrop", true);
				startActivityForResult(intent1, 1);
				break;
			case R.id.menu_tv_bind:// 绑定资料
				Intent bind_intent = new Intent(getActivity(), BindUserActivity.class);
				getActivity().startActivity(bind_intent);
				break;
			case R.id.menu_tv_quit:// 退出
				final Dialog dialog = new Dialog(getActivity());
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setContentView(R.layout.dialog_logout);
				dialog.setCancelable(false);
				dialog.setCanceledOnTouchOutside(false);
				Window dialogWindow = dialog.getWindow();
				WindowManager.LayoutParams lp = dialogWindow.getAttributes();
				dialogWindow.setGravity(Gravity.CENTER);
				WindowManager m = dialogWindow.getWindowManager();
				Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
				lp.height = (int) (d.getHeight() * 0.25); // 高度设置为屏幕的0.6
				lp.width = (int) (d.getWidth() * 0.8); // 宽度设置为屏幕的0.65
				dialogWindow.setAttributes(lp);
				dialog.show();
				TextView btn_true = (TextView) dialog.findViewById(R.id.dialog_logout_btn_true);
				TextView btn_cancel = (TextView) dialog.findViewById(R.id.dialog_logout_btn_cancel);
				btn_true.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
						go2Server();

					}
				});
				btn_cancel.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();

					}
				});

				break;
			case R.id.menu_tv_message:// 消息中心
			/*
			 * Intent msg_intent = new Intent(getActivity(),
			 * MsgCenterActivity.class);
			 * getActivity().startActivity(msg_intent);
			 */
				break;
			case R.id.menu_tv_integral:// 积分中心
			/*
			 * Intent integration_intent = new Intent(getActivity(),
			 * IntegrationActivity.class);
			 * getActivity().startActivity(integration_intent);
			 */
				break;
			case R.id.menu_tv_setting:// 个人设置
				Intent setting_intent = new Intent(getActivity(), SettingActivity.class);
				getActivity().startActivity(setting_intent);
				break;
			case R.id.menu_tv_order:// 订单记录

				break;
			default:
				break;
		}

	}

	/**
	 * 上传头像到服务器
	 */
	private void upLoadImage2Server() {
		RequestParams params = new RequestParams();
		params.addBodyParameter("image", new File(Constant.TEMP_FILEPATH + "/temp.jpg"));
		params.addBodyParameter("tocken", App.TOKEN);
		XHttpUtils.getInstance().upload(App.BASE + "info/upload?", params, new OnUploadStateListener() {

			@Override
			public void onUploadSuccess(String result) {
				try {
					JSONObject json = new JSONObject(result);
					String code = json.getString("code");
					if ("207".equals(code)) {
						String iconurl = json.getString("iconUrl");
//						Log.wtf("sfsdfsdfsdf", iconurl);
						String recordTime = json.getString("recordTime");
						Bundle b = new Bundle();
						b.putString("icon", iconurl);
						b.putString("recordTime", recordTime);
						Message msg = mHandler.obtainMessage(0);
						msg.obj = b;
						msg.sendToTarget();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

			@Override
			public void onFailure(HttpException error, String msg) {
				mHandler.sendEmptyMessage(1);

			}
		});

	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case -3:
					android.os.Process.killProcess(android.os.Process.myPid());
					break;
				case -2:
					android.os.Process.killProcess(android.os.Process.myPid());
					break;

				case 0:
					Bundle b = (Bundle) msg.obj;
					String icon = b.getString("icon");
					String recordTime = b.getString("recordTime");
//					mImg_head.setImageUrl(icon, new ImageLoader(App.getRequestQueue(), BitmapLruCache.instance()));
					mImg_head.setImageURI(Uri.parse(icon));
					saveToDb(icon, recordTime);

					break;
				case 1:
					MyToast.show("头像上传失败");
					break;
				case 2:

					break;
				case 3:
					MyToast.show("头像设置失败");
					break;

				case 100:
					if (TextUtils.isEmpty(App.USERNAME)) {
						mTv_name.setText("请完善用户名");
					} else {
						mTv_name.setText(App.USERNAME);
					}
//					Picasso.with(App.getContext()).load(App.HEAD_IMAGE_URL).into(mImg_head);
					mImg_head.setImageURI(Uri.parse(App.HEAD_IMAGE_URL));
					break;
			}
		}
	};

	/*
	 * 头像url和修改时间保存本地
	 */
	protected void saveToDb(String icon, String recordTime) {

		if (baseHelper == null) {
			baseHelper = new DataBaseHelper(App.getContext());
		}
		SQLiteDatabase base = baseHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("userId", App.USERID);
		values.put("icon", icon);
		values.put("modtime", recordTime);
		base.replace("patient", null, values);
	}


}
