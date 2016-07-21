package com.hykj.view;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;
import com.hykj.App;
import com.hykj.R;
import com.hykj.activity.BaseActivity;
import com.hykj.utils.MyToast;
import com.hykj.utils.ScreenUtils;

/**
 * @author 作者 : zhaoyu
 * @version 创建时间：2016年1月21日 下午5:31:05 类说明
 */
public class SetSugarView extends BaseActivity {
	// doc-医生建议 tar-目标 b-before a-after bk-breakfast l-lunch d-dinner s-sleep
	// da-凌晨 r-random
	private TextView mTv_tarbbf, mTv_tarabf, mTv_tarbl, mTv_taral, mTv_tarbd,
			mTv_tarad, mTv_tarbs, mTv_tarda, mTv_tarr;
	private TextView mTv_sure, mTv_cancel;
	private ImageView mImg_back;

	@Override
	public void init() {
		setContentView(R.layout.activity_setsugar);
		initViews();
	}

	private void initViews() {
		mImg_back = (ImageView) findViewById(R.id.setsugar_img_back);
		mImg_back.setOnClickListener(this);
		mTv_sure = (TextView) findViewById(R.id.sugar_tv_sure);
		mTv_cancel = (TextView) findViewById(R.id.sugar_tv_cancel);
		mTv_tarbbf = (TextView) findViewById(R.id.sugar_tv_tbbf);
		mTv_tarabf = (TextView) findViewById(R.id.sugar_tv_tabf);
		mTv_tarbl = (TextView) findViewById(R.id.sugar_tv_tbl);
		mTv_taral = (TextView) findViewById(R.id.sugar_tv_tal);
		mTv_tarbd = (TextView) findViewById(R.id.sugar_tv_tbd);
		mTv_tarad = (TextView) findViewById(R.id.sugar_tv_tad);
		mTv_tarbs = (TextView) findViewById(R.id.sugar_tv_tbs);
		mTv_tarda = (TextView) findViewById(R.id.sugar_tv_td);
		mTv_tarr = (TextView) findViewById(R.id.sugar_tv_tr);
		mTv_sure.setOnClickListener(this);
		mTv_cancel.setOnClickListener(this);
		mTv_tarbbf.setOnClickListener(this);
		mTv_tarabf.setOnClickListener(this);
		mTv_tarbl.setOnClickListener(this);
		mTv_taral.setOnClickListener(this);
		mTv_tarbd.setOnClickListener(this);
		mTv_tarad.setOnClickListener(this);
		mTv_tarbs.setOnClickListener(this);
		mTv_tarda.setOnClickListener(this);
		mTv_tarr.setOnClickListener(this);

	}

	@Override
	public void click(View v) {
		switch (v.getId()) {
		case R.id.setsugar_img_back:
			finish();
			break;
		case R.id.sugar_tv_sure:
			sendToServer();

			break;
		case R.id.sugar_tv_cancel:
			finish();
			break;
		case R.id.sugar_tv_tbbf:

			selectSugar(mTv_tarbbf);
			break;
		case R.id.sugar_tv_tabf:
			selectSugar(mTv_tarabf);
			break;
		case R.id.sugar_tv_tbl:
			selectSugar(mTv_tarbl);
			break;
		case R.id.sugar_tv_tal:
			selectSugar(mTv_taral);
			break;
		case R.id.sugar_tv_tbd:
			selectSugar(mTv_tarbd);
			break;
		case R.id.sugar_tv_tad:
			selectSugar(mTv_tarad);
			break;
		case R.id.sugar_tv_tbs:
			selectSugar(mTv_tarbs);
			break;
		case R.id.sugar_tv_td:
			selectSugar(mTv_tarda);
			break;
		case R.id.sugar_tv_tr:
			selectSugar(mTv_tarr);
			break;
		default:
			break;
		}
	}

	private void sendToServer() {
		RequestQueue rq = App.getRequestQueue();
		StringBuilder url = new StringBuilder(App.BASE
				+ "healthTarget/setBloodSugger?");
		JSONObject json = new JSONObject();
		url.append("data=");
		try {
			json.put("beforeBreakfast",
					mTv_tarbbf.getText().toString().equals("选择目标") ? "0.0"
							: mTv_tarbbf.getText().toString());
			json.put("afterBreakfast",
					mTv_tarabf.getText().toString().equals("选择目标") ? "0.0"
							: mTv_tarabf.getText().toString());
			json.put("beforeLunch",
					mTv_tarbl.getText().toString().equals("选择目标") ? "0.0"
							: mTv_tarbl.getText().toString());
			json.put("afterLunch", mTv_taral.getText().toString()
					.equals("选择目标") ? "0.0" : mTv_taral.getText().toString());
			json.put("beforeSupper",
					mTv_tarbd.getText().toString().equals("选择目标") ? "0.0"
							: mTv_tarbd.getText().toString());
			json.put("afterSupper",
					mTv_tarad.getText().toString().equals("选择目标") ? "0.0"
							: mTv_tarad.getText().toString());
			json.put("beforeSleep",
					mTv_tarbs.getText().toString().equals("选择目标") ? "0.0"
							: mTv_tarbs.getText().toString());
			json.put("zero",
					mTv_tarda.getText().toString().equals("选择目标") ? "0.0"
							: mTv_tarda.getText().toString());
			json.put("random",
					mTv_tarr.getText().toString().equals("选择目标") ? "0.0"
							: mTv_tarr.getText().toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		url.append(json.toString());
		url.append("&tocken=" + App.TOKEN);
//		Log.wtf("pressrue", url.toString());
		rq.add(new StringRequest(Request.Method.GET, url.toString(),
				new Listener<String>() {

					@Override
					public void onResponse(String response) {
						try {
							JSONObject json = new JSONObject(response);
							String code = json.getString("code");
							if ("206".equals(code)) {
								mHandler.sendEmptyMessage(0);
							}
							if ("110".equals(code)) {
								mHandler.sendEmptyMessage(1);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						mHandler.sendEmptyMessage(-1);
					}
				}));
		rq.start();
	}

	@SuppressLint("HandlerLeak")
	public Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case -1:
				MyToast.show("网络连接失败！");
				break;
			case 0:
				MyToast.show("数据上传成功！");

				SetSugarView.this.finish();
				break;
			case 1:
				MyToast.show("数据上传失败！");
				break;

			default:
				break;
			}
		}
	};

	/*
	 * 选择范围值
	 */
	private void selectSugar(TextView tv) {
		SetSugarDialog dialog = new SetSugarDialog(this, tv);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setCancelable(false);
		dialog.show();
		Window dialogWindow = dialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.BOTTOM);
		WindowManager m = dialogWindow.getWindowManager();
		Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
		lp.height = (int) (d.getHeight() * 0.5); // 高度设置为屏幕的0.6
		lp.width = ScreenUtils.getScreenWidth(App.getContext()); // 宽度设置为屏幕的0.65
		dialogWindow.setAttributes(lp);
	}

}
