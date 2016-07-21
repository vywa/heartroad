package com.hykj.activity.usermanagement;

import android.view.View;
import android.widget.ImageView;

import com.hykj.R;
import com.hykj.activity.BaseActivity;

/**
 * @author 作者 : zhaoyu
 * @version 创建时间：2016年1月8日 下午4:27:03 类说明：商城
 */
public class StoreActivity extends BaseActivity {
	private ImageView mImg_back;

	@Override
	public void init() {
		setContentView(R.layout.activity_store);
		initViews();

	}

	private void initViews() {
		mImg_back = (ImageView) findViewById(R.id.store_img_back);
		mImg_back.setOnClickListener(this);
	}

	@Override
	public void click(View v) {
		switch (v.getId()) {
		case R.id.store_img_back:
			finish();
			break;

		default:
			break;
		}
	}

}
