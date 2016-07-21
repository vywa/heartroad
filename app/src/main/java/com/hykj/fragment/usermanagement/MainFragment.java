package com.hykj.fragment.usermanagement;

import java.util.ArrayList;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hykj.App;
import com.hykj.R;
import com.hykj.activity.usermanagement.MainActivity;
import com.hykj.adapter.MyFragmentPagerAdapter;
import com.hykj.utils.MyToast;
import com.hykj.utils.MaterialUtil;
import com.hykj.utils.ScreenUtils;

/**
 * @author 作者：赵宇
 * @version 1.0 创建时间：2015年10月19日 下午3:49:32 类说明：主界面
 */
@SuppressLint("ValidFragment")
public class MainFragment extends Fragment {
	private ViewPager mPager;
	private ArrayList<Fragment> fragments;
	private ImageView ivBottomLine;// 滑动的线
	private int currIndex = 0;// 当前页编号
	private int bottomLineWidth;// 滑动线的宽度
	private int offset = 0;// 动画图片偏移量
	public final static int num = 3;// 页数
	private Fragment conditionFragment;
	private Fragment chatFragment;
	private Fragment infoFragment;

	private Resources resources;
	private MainActivity activity;
	private TextView mBtn_conditon, mBtn_ask, mBtn_info;// 身体状况评估，我要提问，医疗资讯
	private View v;

	public MainFragment(MainActivity activity) {

		super();
		this.activity = activity;
	}

	public MainFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		v = inflater.inflate(R.layout.fragment_main, container, false);
		resources = getResources();
		
		initWidth();
		initViewPager();
		init();
		return v;

	}

	int width;

	private void initWidth() {
		ivBottomLine = (ImageView) v.findViewById(R.id.main_imgv_line);
		width = ScreenUtils.getScreenWidth(App.getContext()); // 屏幕宽度（像素）
		ivBottomLine.setLayoutParams(new LinearLayout.LayoutParams(width / 3,
				5));
		bottomLineWidth = ivBottomLine.getLayoutParams().width;
		offset = (width / 3 - bottomLineWidth) / 2;
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		ivBottomLine.setImageMatrix(matrix);

	}

	private void initViewPager() {
		mPager = (ViewPager) v.findViewById(R.id.main_vPager);
		mPager.setOffscreenPageLimit(2);//设置最大缓存页数
		fragments = new ArrayList<Fragment>();
		conditionFragment = new MainconditionFragment();
		chatFragment = new MainChatFragment();
		infoFragment = new MainInfoFragment();
		fragments.add(conditionFragment);
		fragments.add(chatFragment);
		fragments.add(infoFragment);
		mPager.setAdapter(new MyFragmentPagerAdapter(getChildFragmentManager(),
				fragments));
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());
		mPager.setCurrentItem(0);
	}

	private void init() {

		mBtn_conditon = (TextView) v.findViewById(R.id.main_tv_condition);
		mBtn_ask = (TextView) v.findViewById(R.id.main_tv_ask);
		mBtn_info = (TextView) v.findViewById(R.id.main_tv_info);
		mBtn_conditon.setOnClickListener(new MyOnClickListener(0));
		mBtn_ask.setOnClickListener(new MyOnClickListener(1));
		mBtn_info.setOnClickListener(new MyOnClickListener(2));

	}

	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		public void onClick(View arg0) {
			mPager.setCurrentItem(index);

		}

	}

	class MyOnPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		int one = offset * 2 + bottomLineWidth;
		int two = one * 2;

		@Override
		public void onPageSelected(int arg0) {
			Animation animation = null;
			switch (arg0) {
			case 0:
				if (currIndex == 1) {
					animation = new TranslateAnimation(one, 0, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, 0, 0, 0);
				}
				break;
			case 1:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, one, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, one, 0, 0);
				}
				break;
			case 2:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, two, 0, 0);
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(one, two, 0, 0);
				}
				break;
			default:
				break;
			}
			currIndex = arg0;
			animation.setFillAfter(true);// True:图片停在动画结束位置
			animation.setDuration(100);
			ivBottomLine.startAnimation(animation);
		}
	}

	/**
	 * 访问服务器获取头像和用户名
	 */
	private void getIconAndNameFromServer() {
		RequestQueue rq = App.getRequestQueue();
		StringBuilder url = new StringBuilder(App.BASE);
		try {
			JSONObject json = new JSONObject();
			json.put("userId", App.USERID);
			json.put("icon", "head_image");
			url.append("data=" + json.toString());
		} catch (Exception e) {
		}
		rq.add(new StringRequest(Request.Method.GET, url.toString(),
				new Listener<String>() {

					@Override
					public void onResponse(String response) {
						App.HEAD_IMAGE_URL = response;
						mHandler.sendEmptyMessage(0);
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						mHandler.sendEmptyMessage(1);

					}
				}));
		rq.start();

	}

	public Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				MaterialUtil.showProgressDialog(activity, "正在拉取用户信息");
				activity.changePage(4);
				MaterialUtil.progressDialogDismiss();
				break;
			case 1:
				MyToast.show("拉取用户信息失败");
				break;
			default:
				break;
			}
		}
	};
}
