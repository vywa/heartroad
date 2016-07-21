package com.hykj.view;

/**
 * @author 作者 : zhaoyu
 * @version 创建时间：2016年1月6日 下午3:25:45
 * 类说明：身高选择器
 */
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.hykj.R;
import com.hykj.activity.usermanagement.ModifyDataActivity;
import com.hykj.view.spinnerwheel.WheelVerticalView;
import com.hykj.view.spinnerwheel.adapters.ArrayWheelAdapter;

public class ChooseHeightView extends Dialog implements android.view.View.OnClickListener {
	Context context;
	View view;
	View backView;
	private WheelVerticalView mWvv;
	private String[] heights;
	private ModifyDataActivity activity;

	public ChooseHeightView(Context context, ModifyDataActivity activity) {
		super(context, android.R.style.Theme_Translucent);
		this.context = context;
		this.activity = activity;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_chooseheight);
		findViews();
		setting();
	}

	private void setting() {
		// TODO Auto-generated method stub
		heights = new String[220 - 120 + 1];
		for (int i = 120; i <= 220; i++) {
			heights[i - 120] = i + "";
		}
		mWvv.setVisibleItems(2);

		ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<String>(context, heights);

		adapter.setItemResource(R.layout.choose_city_wheel_text);
		adapter.setItemTextResource(R.id.text);

		mWvv.setViewAdapter(adapter);

		mWvv.setCurrentItem(40);

	}

	private void findViews() {
		mWvv = (WheelVerticalView) findViewById(R.id.dialog_height_wv);
		Button cancel = (Button) findViewById(R.id.dialog_height_btn_cancel);
		cancel.setOnClickListener(this);
		Button accept = (Button) findViewById(R.id.dialog_height_btn_save);
		accept.setOnClickListener(this);

	}
	private ConfirmHeight confirmHeight;
	
	public interface ConfirmHeight{
		void function(String height);
	}
	
	public void setConfirmHeightListener(ConfirmHeight confirmHeight){
		this.confirmHeight = confirmHeight;
	}
	
	
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.dialog_height_btn_cancel:
			dismiss();
			break;
		case R.id.dialog_height_btn_save:
			confirmHeight.function(mWvv.getCurrentItem() + 120 + "cm");
			dismiss();
			break;
		default:
			break;
		}
	}

}
