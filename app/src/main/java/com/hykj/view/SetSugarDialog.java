package com.hykj.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.hykj.R;
import com.hykj.view.spinnerwheel.WheelVerticalView;
import com.hykj.view.spinnerwheel.adapters.ArrayWheelAdapter;

/**
 * @author 作者 : zhaoyu
 * @version 创建时间：2016年1月22日 上午11:29:55 类说明
 */
public class SetSugarDialog extends Dialog implements
		android.view.View.OnClickListener {
	Context context;
	TextView tv;

	private WheelVerticalView mWvv_fsugar, mWvv_tsugar;
	private Button mBtn_sure, mBtn_cancel;
	private String[] nums;

	public SetSugarDialog(Context context, TextView tv) {
		super(context);
		this.context = context;
		this.tv = tv;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_setsugar);
		initViews();
		setNums();
	}

	private void setNums() {

		nums = new String[331];
		for (int i = 0; i <= 330; i++) {
			nums[i] = i / 10.0f + "";
		}
		mWvv_fsugar.setVisibleItems(2);

		mWvv_tsugar.setVisibleItems(2);

		ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<String>(
				context, nums);
		ArrayWheelAdapter<String> adapter1 = new ArrayWheelAdapter<String>(
				context, nums);
		adapter.setItemResource(R.layout.choose_city_wheel_text);
		adapter.setItemTextResource(R.id.text);
		adapter1.setItemResource(R.layout.choose_city_wheel_text);
		adapter1.setItemTextResource(R.id.text);
		mWvv_fsugar.setViewAdapter(adapter1);
		mWvv_tsugar.setViewAdapter(adapter1);
		mWvv_fsugar.setCurrentItem(40);
		mWvv_tsugar.setCurrentItem(70);
	}

	private void initViews() {
		mWvv_fsugar = (WheelVerticalView) findViewById(R.id.dialog_wvv_fsugar);
		mWvv_tsugar = (WheelVerticalView) findViewById(R.id.dialog_wvv_tsugar);
		mBtn_sure = (Button) findViewById(R.id.dialog_sugar_sure);
		mBtn_cancel = (Button) findViewById(R.id.dialog_sugar_cancel);
		mBtn_sure.setOnClickListener(this);
		mBtn_cancel.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dialog_sugar_sure:
			tv.setText(mWvv_fsugar.getCurrentItem() / 10.0f + "~"
					+ mWvv_tsugar.getCurrentItem() / 10.0f);
			cancel();
			break;
		case R.id.dialog_sugar_cancel:
			cancel();
			break;
		default:
			break;
		}
	}

}
