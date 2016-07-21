package com.hykj.activity.usermanagement;

import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.hykj.R;
import com.hykj.activity.BaseActivity;

/**
 * @author 作者 : zhaoyu
 * @version 创建时间：2016年1月13日 下午5:10:51 类说明
 */
public class PushInfoDetialActivity extends BaseActivity {
	private String url;
	private TextView mTv_title;
	private ImageView mTv_back;
	private WebView mWebview;

	@Override
	public void init() {
		setContentView(R.layout.activity_detial);
		mTv_back = (ImageView) findViewById(R.id.detial_tv_back);
		mTv_back.setOnClickListener(this);
		mTv_title=(TextView) findViewById(R.id.detial_tv_title);
		mTv_title.setText("天衡医疗");
		url = getIntent().getStringExtra("url");
		mWebview = (WebView) findViewById(R.id.detial_webview);
		// 设置WebView属性，能够执行Javascript脚本
		mWebview.getSettings().setJavaScriptEnabled(true);
		// 加载需要显示的网页
		mWebview.loadUrl(url);
		// 设置Web视图
		mWebview.setWebViewClient(new HelloWebViewClient());
	}

	@Override
	// 设置回退
	// 覆盖Activity类的onKeyDown(int keyCoder,KeyEvent event)方法
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebview.canGoBack()) {
			mWebview.goBack(); // goBack()表示返回WebView的上一页面
			return true;
		}
		return false;
	}

	// Web视图
	private class HelloWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}

	@Override
	public void click(View v) {
		switch (v.getId()) {
		case R.id.detial_tv_back:
			finish();
			break;

		default:
			break;
		}
	}

}
