package com.hykj.activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

/**
 * @author  作者：赵宇   	
 * @version 1.0
   创建时间：2015年10月19日 下午3:09:04
 * 类说明：所有Activity的基类
 */
public abstract class BaseActivity extends FragmentActivity implements OnClickListener {
		public abstract void init();//界面初始化
		public abstract void click(View v);//点击事件
		
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		 setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏锁定
		 requestWindowFeature(Window.FEATURE_NO_TITLE);//无标题
		 init();
	}
		

	@Override
	public void onClick(View arg0) {
		click(arg0);
	}

}
