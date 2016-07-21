package com.hykj.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.gc.materialdesign.views.ButtonFlat;
import com.hykj.R;
import com.hykj.activity.usermanagement.ModifyDataActivity;
import com.hykj.view.ChooseHeightView.ConfirmHeight;
import com.hykj.view.spinnerwheel.WheelVerticalView;
import com.hykj.view.spinnerwheel.adapters.ArrayWheelAdapter;

/**
 * @author 作者 : zhaoyu
 * @version 创建时间：2016年1月6日 下午7:03:30 类说明：体重选择器
 */
public class ChooseWeightView extends Dialog implements OnClickListener {
	Context context;
	View view;
	View backView;
	private WheelVerticalView mWvv;
	private String[] weights;
	private ModifyDataActivity activity;

	public ChooseWeightView(Context context, ModifyDataActivity activity) {
		super(context, android.R.style.Theme_Translucent);
		this.context = context;
		this.activity = activity;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_chooseweight);
		findViews();
		settings();
	}

	private void findViews() {
		mWvv = (WheelVerticalView) findViewById(R.id.dialog_weight_wv);
		Button cancel = (Button) findViewById(R.id.dialog_weight_btn_cancel);
		cancel.setOnClickListener(this);
		Button accept = (Button) findViewById(R.id.dialog_weight_btn_save);
		accept.setOnClickListener(this);
	}

	private void settings() {
		weights = new String[400 - 50 + 1];
		for (int i = 50; i <= 400; i++) {
			weights[i - 50] = i / 2.0f + "";
		}
		mWvv.setVisibleItems(2);
		ArrayWheelAdapter<String> adapter = new ArrayWheelAdapter<String>(context, weights);
		adapter.setItemResource(R.layout.choose_city_wheel_text);
		adapter.setItemTextResource(R.id.text);
		mWvv.setViewAdapter(adapter);
		mWvv.setCurrentItem(50);
	}
	
	private ConfirmWeight confirmWeight;
	
	public interface ConfirmWeight{
		void function(String weight);
	}
	
	public void setConfirmWeightListener(ConfirmWeight confirmWeight){
		this.confirmWeight= confirmWeight;
	}
	
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.dialog_weight_btn_cancel:
			dismiss();
			break;
		case R.id.dialog_weight_btn_save:
//			activity.mTv_weight.setText((mWvv.getCurrentItem() + 50) / 2.0f + "kg");
			confirmWeight.function((mWvv.getCurrentItem() + 50) / 2.0f + "kg");
			dismiss();
			break;
		default:
			break;
		}
	}

}
