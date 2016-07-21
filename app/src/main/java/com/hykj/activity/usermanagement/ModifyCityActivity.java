package com.hykj.activity.usermanagement;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hykj.R;
import com.hykj.activity.BaseActivity;
import com.hykj.utils.MyToast;
import com.hykj.utils.MaterialUtil;
import com.hykj.view.ChooseCityDialog;
import com.hykj.view.ChooseCityDialog.AcceptButtonClickListener;

/**
 * @author 作者 : zhaoyu
 * @version 创建时间：2016年3月23日 上午11:48:43 类说明：选择地址
 */
public class ModifyCityActivity extends BaseActivity {
	private ImageView mImg_back;
	private Button mBtn_save, mBtn_cancel;
	private TextView mTv_addr;
	private EditText mEdt_addr;

	@Override
	public void init() {
		setContentView(R.layout.activity_modifycity);
		mImg_back = (ImageView) findViewById(R.id.modifycity_imgv_back);
		mBtn_cancel = (Button) findViewById(R.id.modifycity_btn_cancel);
		mBtn_save = (Button) findViewById(R.id.modifycity_btn_save);
		mTv_addr = (TextView) findViewById(R.id.modifycity_tv_addr);
		mEdt_addr = (EditText) findViewById(R.id.modifycity_edt_addr);

		mImg_back.setOnClickListener(this);
		mBtn_cancel.setOnClickListener(this);
		mBtn_save.setOnClickListener(this);
		mTv_addr.setOnClickListener(this);
	}

	@Override
	public void click(View v) {
		switch (v.getId()) {
		case R.id.modifycity_imgv_back:
			mHandler.sendEmptyMessage(-1);
			break;
		case R.id.modifycity_btn_cancel:
			mHandler.sendEmptyMessage(-1);
			break;
		case R.id.modifycity_btn_save:
			if ("点击选择地区".equals(mTv_addr.getText().toString())) {
				mHandler.sendEmptyMessage(1);
			} else if (TextUtils.isEmpty(mEdt_addr.getText().toString().trim())) {
				mHandler.sendEmptyMessage(2);
			} else {
				mHandler.sendEmptyMessage(0);
			}
			break;
		case R.id.modifycity_tv_addr:
			ChooseCityDialog dialog = new ChooseCityDialog(this);
			dialog.addAcceptButton(new AcceptButtonClickListener() {

				@Override
				public void onClick(String province, String city, String district) {
					mTv_addr.setText(province + city + district);
				}
			});
			dialog.show();
			break;

		default:
			break;
		}
	}

	public Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case -1:

				finish();
				break;
			case 0:
				Intent intent = new Intent();
				Bundle b = new Bundle();
				
				b.putString("address", mTv_addr.getText().toString().trim() + mEdt_addr.getText().toString().trim());
				intent.putExtra("address", b);
				setResult(1, intent);
				finish();
				break;
			case 1:
				MyToast.show("请选择省市县级地区");
				break;
			case 2:
				MyToast.show("请输入详细地址");
				break;

			default:
				break;
			}
		}
	};
}
