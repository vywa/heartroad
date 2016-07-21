package com.hykj.activity.usermanagement;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.gc.materialdesign.widgets.Dialog;
import com.hykj.App;
import com.hykj.R;
import com.hykj.entity.Doctor;
import com.hykj.service.ChatService;
import com.hykj.utils.MyToast;

public class DoctorDetailActivity extends Activity {
	private Doctor doctor;
	private ImageView iv_back;
	private ChatService chartService;

	ServiceConnection mServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			chartService = ((ChatService.ChatBinder) service).getService();
			// 开始连接xmpp服务器
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			chartService = null;
		}
	};

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 2:
				MyToast.show("已发送添加好友请求");
				finish();
				break;
			case 3:
				MyToast.show("添加失败");
				break;
			}
		}
	};

	private TextView tv_name;
	private TextView tv_sex;
	private TextView tv_age;
	private TextView tv_hospital;
	private TextView tv_profile;
	private SimpleDraweeView civ;
	private Button add;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_doctorllist);

		bindXMPPService();

		doctor = (Doctor) getIntent().getSerializableExtra("doctor");

		iv_back = (ImageView) findViewById(R.id.iv_title_back);
		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("医生简介");
		iv_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DoctorDetailActivity.this.finish();
			}
		});

		if (doctor != null) {
			tv_name = (TextView) findViewById(R.id.tv_doctordetail_name);
			tv_name.setText(doctor.getName());
			tv_sex = (TextView) findViewById(R.id.tv_doctordetail_sex);
			tv_sex.setText(doctor.getSex());
			tv_age = (TextView) findViewById(R.id.tv_doctordetail_age);
			if ("-1".equals(doctor.getAge()) || TextUtils.isEmpty(doctor.getAge())) {
				tv_age.setText("");
			} else {
				tv_age.setText(doctor.getAge() + "岁");
			}
			tv_hospital = (TextView) findViewById(R.id.tv_doctordetail_hospital);
			tv_hospital.setText(doctor.getHospitalName());
			tv_profile = (TextView) findViewById(R.id.tv_doctordetail_profile);
			tv_profile.setText("简介：\t" + doctor.getPrifile());
			civ = (SimpleDraweeView) findViewById(R.id.niv_docdetail_photo);
			String iconUrl = doctor.getIconUrl();
			if(TextUtils.isEmpty(iconUrl)){
				iconUrl = App.DEFULT_PHOTO;
			}
//			Picasso.with(App.getContext()).load(iconUrl).into(civ);
			civ.setImageURI(Uri.parse(iconUrl));
			add = (Button) findViewById(R.id.bt_doctordetail_add);
			if (doctor.isFriend()) {
				add.setVisibility(View.GONE);
			} else {
				add.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						final Dialog dialog = new com.gc.materialdesign.widgets.Dialog(DoctorDetailActivity.this, null, "确认添加医生？");
						dialog.setOnAcceptButtonClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								new Thread() {
									public void run() {
										boolean b = chartService.addUser(doctor.getUserId() + "");
										if (b) {
											handler.sendEmptyMessage(2);
										} else {
											handler.sendEmptyMessage(3);
										}
									}
								}.start();
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
				});
			}
		}
	}

	private void bindXMPPService() {
		Intent mServiceIntent = new Intent(this, ChatService.class);
		bindService(mServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(mServiceConnection);
	}
}
