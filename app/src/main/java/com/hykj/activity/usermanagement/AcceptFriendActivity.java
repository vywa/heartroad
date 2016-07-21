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
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.hykj.App;
import com.hykj.R;
import com.hykj.service.ChatService;
import com.hykj.utils.MyToast;

public class AcceptFriendActivity extends Activity {

	private ChatService chartService;
	private TextView tv_name;
	private TextView tv_sex;
	private TextView tv_age;
	private TextView tv_hospital;
	private TextView tv_profile;
	private SimpleDraweeView civ;
	private ImageView iv_back;
	
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
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				MyToast.show("已添加");
				finish();
				break;
			case 2:
				MyToast.show("已拒绝");
				finish();
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_acceptfriend);
		
		Intent mServiceIntent = new Intent(this, ChatService.class);
		bindService(mServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
		
		iv_back = (ImageView) findViewById(R.id.iv_title_back);
		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("医生简介");
		iv_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AcceptFriendActivity.this.finish();
			}
		});
		
		if (App.doctor != null) {
			tv_name = (TextView) findViewById(R.id.tv_doctordetail_name);
			tv_name.setText(App.doctor.getName());
			tv_sex = (TextView) findViewById(R.id.tv_doctordetail_sex);
			tv_sex.setText(App.doctor.getSex());
			tv_age = (TextView) findViewById(R.id.tv_doctordetail_age);
			if ("-1".equals(App.doctor.getAge()) || TextUtils.isEmpty(App.doctor.getAge())) {
				tv_age.setText("");
			}else{
				tv_age.setText(App.doctor.getAge()+"岁");
			}
			tv_hospital = (TextView) findViewById(R.id.tv_doctordetail_hospital);
			tv_hospital.setText(App.doctor.getHospitalName());
			tv_profile = (TextView) findViewById(R.id.tv_doctordetail_profile);
			tv_profile.setText("简介：\t"+App.doctor.getPrifile());
			civ = (SimpleDraweeView) findViewById(R.id.niv_docdetail_photo);
			String iconUrl = App.doctor.getIconUrl();
			if(TextUtils.isEmpty(iconUrl)){
				iconUrl = App.DEFULT_PHOTO;
			}
//			Picasso.with(App.getContext()).load(iconUrl).into(civ);
			civ.setImageURI(Uri.parse(iconUrl));
		}

		findViewById(R.id.bt_acceptfri_acc).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new Thread(){
					public void run() {
						chartService.acceptFriend(App.doctor.getUserId()+App.OPENFIRE_SERVICE_NAME);
						handler.sendEmptyMessage(1);
					}
				}.start();
			}
		});
		findViewById(R.id.bt_acceptfri_dis).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new Thread(){
					public void run() {
						chartService.refuseFriend(App.doctor.getUserId()+App.OPENFIRE_SERVICE_NAME);
						handler.sendEmptyMessage(2);
					}
				}.start();
			}
		});
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(mServiceConnection);
	}
}






















