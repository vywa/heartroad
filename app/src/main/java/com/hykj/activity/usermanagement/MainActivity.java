package com.hykj.activity.usermanagement;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.hykj.activity.messure.AddInfoActivity;
import com.hykj.fragment.subject.SubjectFragment;
import com.hykj.fragment.usermanagement.MainFragment;
import com.hykj.fragment.usermanagement.RecordFragment;
import com.hykj.service.ChatService;
import com.hykj.service.IConnectionStatusCallback;
import com.hykj.utils.FindView;
import com.hykj.utils.MyLog;
import com.hykj.utils.MyToast;
import com.nineoldandroids.view.ViewHelper;

/**
 * @author 作者：赵宇
 * @version 1.0 创建时间：2015年10月19日 下午3:30:06 类说明：主页面
 */
public class MainActivity extends BaseActivity implements IConnectionStatusCallback {

	private ImageView mBtn_main, mBtn_record, mBtn_question, mBtn_store;// 对应每个界面的按钮
	private ImageView mIv_foot1, mIv_foot2, mIv_foot3, mIv_foot4;
	private FindView mFindView;// 初始化组件工具
	private Fragment mFm_main;// 主界面fragment
	private RecordFragment mFm_record;// 健康报告界面fragment
	private SubjectFragment mFm_question;// 社区界面fragment
	private DrawerLayout mDrawerLayout;
	private List<Fragment> mFragments;// 用来存放管理fragment的集合
	private ChatService chatService;

	ServiceConnection mServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			chatService = ((ChatService.ChatBinder) service).getService();
			chatService.registerConnectionStatusCallback(MainActivity.this);
			if (!chatService.isAuthenticated()) {
				MyLog.wtf(App.USERID + "", App.tempPwd);
				chatService.Login(App.USERID + "", App.tempPwd);
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			chatService.unRegisterConnectionStatusCallback();
			chatService = null;
		}
	};

	public void init() {
		App.addActivity(this);
		setContentView(R.layout.activity_main);

		Intent mServiceIntent = new Intent(this, ChatService.class);
		bindService(mServiceIntent, mServiceConnection,
				Context.BIND_AUTO_CREATE);

		mFindView = new FindView(this);

		mDrawerLayout = mFindView.findActivityView(R.id.drawer_layout);
		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED,
				Gravity.LEFT);

		initEvents();
		ImageButton iv_add = (ImageButton) findViewById(R.id.main_imgv_add);
		iv_add.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				add();
			}
		});

		mBtn_main = mFindView.findActivityView(R.id.main_btn_main);
		mBtn_record = mFindView.findActivityView(R.id.main_btn_records);
		mBtn_question = mFindView.findActivityView(R.id.main_btn_question);
		mBtn_store = mFindView.findActivityView(R.id.main_btn_store);
		mIv_foot1 = mFindView.findActivityView(R.id.iv_foot_1);
		mIv_foot2 = mFindView.findActivityView(R.id.iv_foot_2);
		mIv_foot3 = mFindView.findActivityView(R.id.iv_foot_3);
		mIv_foot4 = mFindView.findActivityView(R.id.iv_foot_4);
		mBtn_main.setOnClickListener(this);
		mBtn_record.setOnClickListener(this);
		mBtn_question.setOnClickListener(this);
		mBtn_store.setOnClickListener(this);

		// 把fragment放到集合统一管理
		mFragments = new ArrayList<Fragment>();
		mFm_main = new MainFragment(this);

		mFm_record = new RecordFragment();
		mFm_question = new SubjectFragment();

		mFragments.add(mFm_main);// 0---首页
		mFragments.add(mFm_record);// 1---健康档案
		mFragments.add(mFm_question);// 2----社区

		setDefaultFragment();

	}


	public void delFriend(final String id) {
		new Thread() {
			public void run() {
				chatService.delFriend(id);
			}
		}.start();
	}

	/**
	 *
	 */
	private void initEvents() {
		mDrawerLayout.setDrawerListener(new DrawerListener() {

			@Override
			public void onDrawerStateChanged(int arg0) {

			}

			@Override
			public void onDrawerSlide(View arg0, float arg1) {

				View mContent = mDrawerLayout.getChildAt(0);
				View mMenu = arg0;
				float scale = 1 - arg1;
				float leftScale = 1 - 0.3f * scale;
				float rightScale = 0.8f + scale * 0.2f;

				ViewHelper.setScaleX(mMenu, leftScale);
				ViewHelper.setScaleY(mMenu, leftScale);
				ViewHelper.setAlpha(mMenu, 0.6f + 0.4f * (1 - scale));
				ViewHelper.setTranslationX(mContent, mMenu.getMeasuredWidth()
						* (1 - scale));
				ViewHelper.setPivotX(mContent, 0);
				ViewHelper.setPivotY(mContent, mContent.getMeasuredHeight() / 2);
				mContent.invalidate();
				ViewHelper.setScaleX(mContent, rightScale);

			}

			@Override
			public void onDrawerOpened(View arg0) {

			}

			@Override
			public void onDrawerClosed(View arg0) {
				// mDrawerLayout.setDrawerLockMode(
				// DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.LEFT);//
				// 设置将只能程序控制弹出，否则可滑动弹出

			}
		});

	}

	public void openLeftMenu(View v) {

		mDrawerLayout.openDrawer(Gravity.LEFT);

		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED,
				Gravity.LEFT);
	}

	/*
	 * 设置默认的fragment（首页界面）
	 */
	private void setDefaultFragment() {

		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();

		ft.add(R.id.container, mFm_main, "mFm_main");

		ft.commitAllowingStateLoss();

	}

	/*
	 * 录入血糖血压
	 */
	public void add() {
		Intent intent = new Intent(this, AddInfoActivity.class);
		this.startActivity(intent);

	}

	// 设置当前页为首页
	int mCurrentPage = 0;

	/*
	 * 显示切换页，隐藏当前页
	 */
	public void changePage(int page) {
		Fragment fragment = mFragments.get(page);
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

		if (mCurrentPage != page) {
			// 隐藏mCurrentPage的页面
			Fragment fm = mFragments.get(mCurrentPage);
			ft.hide(fm);
			// 判断是否有添加，没有就加上，有就显示
			if (fragment.isAdded()) {
				// 调用显示代码
				ft.show(fragment);
			} else {
				// 调用添加代码
				ft.add(R.id.container, fragment, "fragment" + page);
				// ft.addToBackStack("tag");
			}
			mCurrentPage = page;
			ft.commit();
		}
	}

	@Override
	public void click(View v) {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		switch (v.getId()) {
			case R.id.main_btn_main:
				mBtn_main.setImageResource(R.drawable.main_selected);
				mBtn_record.setImageResource(R.drawable.record);
				mBtn_question.setImageResource(R.drawable.subject);
				mBtn_store.setImageResource(R.drawable.shop);
				mIv_foot1.setVisibility(View.VISIBLE);
				mIv_foot2.setVisibility(View.INVISIBLE);
				mIv_foot3.setVisibility(View.INVISIBLE);
				mIv_foot4.setVisibility(View.INVISIBLE);
				changePage(0);
				break;
			case R.id.main_btn_records:
				mBtn_main.setImageResource(R.drawable.main);
				mBtn_record.setImageResource(R.drawable.record_selected);
				mBtn_question.setImageResource(R.drawable.subject);
				mBtn_store.setImageResource(R.drawable.shop);
				mIv_foot1.setVisibility(View.INVISIBLE);
				mIv_foot2.setVisibility(View.VISIBLE);
				mIv_foot3.setVisibility(View.INVISIBLE);
				mIv_foot4.setVisibility(View.INVISIBLE);
				changePage(1);

				break;
			case R.id.main_btn_question:
				mBtn_main.setImageResource(R.drawable.main);
				mBtn_record.setImageResource(R.drawable.record);
				mBtn_question.setImageResource(R.drawable.subject_selected);
				mBtn_store.setImageResource(R.drawable.shop);
				mIv_foot1.setVisibility(View.INVISIBLE);
				mIv_foot2.setVisibility(View.INVISIBLE);
				mIv_foot3.setVisibility(View.VISIBLE);
				mIv_foot4.setVisibility(View.INVISIBLE);
				changePage(2);
				break;
			case R.id.main_btn_store:
				mBtn_main.setImageResource(R.drawable.main);
				mBtn_record.setImageResource(R.drawable.record);
				mBtn_question.setImageResource(R.drawable.subject);
				mBtn_store.setImageResource(R.drawable.shop_selected);
				mIv_foot1.setVisibility(View.INVISIBLE);
				mIv_foot2.setVisibility(View.INVISIBLE);
				mIv_foot3.setVisibility(View.INVISIBLE);
				mIv_foot4.setVisibility(View.VISIBLE);
				Intent store_intent = new Intent(this, ScanQRActivity.class);
				startActivity(store_intent);
				break;

		}
		ft.commit();
	}

	long[] times = new long[2];

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) { // 监控/拦截/屏蔽返回键
			long temp = times[1];
			times[0] = temp;
			times[1] = SystemClock.uptimeMillis();
			if ((times[1] - times[0]) < 1500) {
				mHandler.sendEmptyMessage(0);
			} else {
				MyToast.show("再次点击退出应用");
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}


	public static final int CONFLICT = 4;

	public Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 0:
					MainActivity.this.finish();
					break;
				case 1:
					MainActivity.this.finish();
					break;
				case 2:
					MainActivity.this.finish();
					break;
				case 4:
					MyToast.show("账户在其他设备上登录");
					break;
			}
		}

		;
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		chatService.logout();// 注销
		chatService.stopSelf();// 停止服务
		unbindService(mServiceConnection);
	}

	@Override
	public void connectionStatusChanged(int connectedState, String reason) {
		if (connectedState == ChatService.CONFLICT) {
			mHandler.sendEmptyMessage(CONFLICT);
		}
	}

}
