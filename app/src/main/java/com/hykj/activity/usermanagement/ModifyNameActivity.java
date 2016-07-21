package com.hykj.activity.usermanagement;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hykj.App;
import com.hykj.R;
import com.hykj.activity.BaseActivity;
import com.hykj.utils.MyToast;

/**
 * @author 作者 : zhaoyu
 * @version 创建时间：2016年3月21日 下午2:11:57 类说明：修改姓名
 */
public class ModifyNameActivity extends BaseActivity {
	private ImageView mImg_back;
	private EditText mEdt_name;
	private Button mBtn_true, mBtn_cancel;
	private TextView mTv_del;

	@Override
	public void init() {
		setContentView(R.layout.activity_modifyname);
		initView();
	}

	private void initView() {
		mImg_back = (ImageView) findViewById(R.id.modifyname_imgv_back);
		mEdt_name = (EditText) findViewById(R.id.modifyname_edt_name);
		mBtn_true = (Button) findViewById(R.id.modifyname_btn_save);
		mBtn_cancel = (Button) findViewById(R.id.modifyname_btn_cancel);
		mTv_del = (TextView) findViewById(R.id.modifyname_tv_del);
		mImg_back.setOnClickListener(this);
		mBtn_true.setOnClickListener(this);
		mBtn_cancel.setOnClickListener(this);
		mTv_del.setOnClickListener(this);
		mEdt_name.addTextChangedListener(new TextWatcher() {

			@SuppressLint("NewApi")
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (count == 0) {
					mTv_del.setBackground(null);
				} else {
					mTv_del.setBackgroundResource(R.drawable.delall);
				}
			}

			@SuppressLint("NewApi")
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				if (count == 0) {
					mTv_del.setBackground(null);
				} else {
					mTv_del.setBackgroundResource(R.drawable.delall);
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
		mEdt_name.setText(App.TRUENAME);
	}

	@Override
	public void click(View v) {
		switch (v.getId()) {
		case R.id.modifyname_imgv_back:
			mHandler.sendEmptyMessage(1);
			break;
		case R.id.modifyname_btn_save:
			mHandler.sendEmptyMessage(0);
			break;
		case R.id.modifyname_btn_cancel:
			mHandler.sendEmptyMessage(1);
			break;
		case R.id.modifyname_tv_del:

			mEdt_name.setText("");
			break;

		default:
			break;
		}
	}

	public Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				if (TextUtils.isEmpty(mEdt_name.getText().toString())) {
					MyToast.show("请输入姓名");
				} else {
					Intent intent = new Intent();
					Bundle b = new Bundle();
					b.putString("name", mEdt_name.getText().toString());
					intent.putExtra("name", b);
					setResult(0, intent);
					finish();
				}
				break;
			case 1:

				finish();

				break;

			default:
				break;
			}
		}
	};
}
