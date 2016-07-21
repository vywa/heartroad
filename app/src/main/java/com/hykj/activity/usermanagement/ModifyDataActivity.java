/**
 * 
 */
package com.hykj.activity.usermanagement;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smackx.packet.DiscoverInfo.Feature;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hykj.App;
import com.hykj.Constant;
import com.hykj.R;
import com.hykj.activity.BaseActivity;
import com.hykj.db.DataBaseHelper;
import com.hykj.utils.MyProgress;
import com.hykj.utils.MyToast;
import com.hykj.view.ChooseCityDialog;
import com.hykj.view.ChooseCityDialog.AcceptButtonClickListener;
import com.hykj.view.ChooseHeightView;
import com.hykj.view.ChooseWeightView;
import com.tencent.wxop.stat.h;

/**
 * @author zhaoyu
 * @version 1.0 创建时间：2015年12月10日 类说明：用户修改资料
 */
public class ModifyDataActivity extends BaseActivity {
	private ImageView mImgv_back;
	private Button mBtn_save, mBtn_cancel;
	public TextView mTv_name, mTv_sex, mTv_birth, mTv_height, mTv_weight, mTv_addr;
	private String oldName, oldAddr;

	String sex = App.SEX;
	String birth = App.BIRTHDAY;
	String height = App.HEIGHT;
	String weight = App.WEIGHT;
	String trueName = App.TRUENAME;
	String address = App.ADDRESS;
	
	@Override
	public void init() {
		App.addActivity(this);
		setContentView(R.layout.activity_modifydata);
		
		if (TextUtils.isEmpty(App.BIRTHDAY) || TextUtils.isEmpty(App.HEIGHT) || TextUtils.isEmpty(App.WEIGHT)|| TextUtils.isEmpty(App.TRUENAME)|| TextUtils.isEmpty(App.SEX) ) {
			MyToast.show("认真填写基本资料有助于医生对您病情的诊断");
		}
		
		mImgv_back = (ImageView) findViewById(R.id.modify_imgv_back);// 回退
		mBtn_save = (Button) findViewById(R.id.modify_btn_true);// 保存
		mBtn_cancel = (Button) findViewById(R.id.modify_btn_cancel);

		mTv_name = (TextView) findViewById(R.id.modify_tv_name);// 姓名
		mTv_sex = (TextView) findViewById(R.id.modify_tv_sex);// 性别
		mTv_birth = (TextView) findViewById(R.id.modify_tv_birth);// 生日
		mTv_height = (TextView) findViewById(R.id.modify_tv_height);// 身高
		mTv_weight = (TextView) findViewById(R.id.modify_tv_weight);// 体重
		mTv_addr = (TextView) findViewById(R.id.modify_tv_addr);// 地址

		mImgv_back.setOnClickListener(this);
		mBtn_save.setOnClickListener(this);
		mBtn_cancel.setOnClickListener(this);
		mTv_addr.setOnClickListener(this);

		mTv_name.setOnClickListener(this);
		mTv_sex.setOnClickListener(this);
		mTv_birth.setOnClickListener(this);
		mTv_height.setOnClickListener(this);
		mTv_weight.setOnClickListener(this);
		mTv_addr.setOnClickListener(this);
		// getDataFromDb();// 获取个人信息
		setInfo();
		heights = new ArrayList<String>();
		for (int i = 30; i <= 200; i++) {
			heights.add(i + "");
		}

	}

	private void setInfo() {
		if (TextUtils.isEmpty(trueName)) {
			mTv_name.setText("请输入姓名");
		} else {
			mTv_name.setText(trueName);
		}

		if (TextUtils.isEmpty(sex)) {
			mTv_sex.setText("请选择性别");
		} else {
			mTv_sex.setText(sex);
		}

		if (TextUtils.isEmpty(birth)) {
			mTv_birth.setText("请选择出生日期");
		} else {
			mTv_birth.setText(birth);
		}

		if (TextUtils.isEmpty(height)) {
			mTv_height.setText("请选择身高");
		} else {
			mTv_height.setText(height);
		}

		if (TextUtils.isEmpty(weight)) {
			mTv_weight.setText("请选择体重");
		} else {
			mTv_weight.setText(weight);
		}

		if (TextUtils.isEmpty(address)) {
			mTv_addr.setText("请选择居住地址");
		} else {
			mTv_addr.setText(address);
		}
	}

	/**
	 * 查询数据库获取数据
	 */

	public void getDataFromDb() {
		if (baseHelper == null) {
			baseHelper = new DataBaseHelper(this);
		}

		SQLiteDatabase db = baseHelper.getReadableDatabase();
		Cursor c = db.rawQuery("select * from patient where userId=?", new String[] { String.valueOf(App.USERID) });
		if (c.moveToNext()) {
			trueName = c.getString(1);
			sex = c.getString(2);
			birth = c.getString(4);
			height = c.getString(5);
			weight = c.getString(6);
			address = c.getString(7);

		}
		c.close();
		db.close();
		baseHelper.close();

		setInfo();// 设置信息
	}

	List<String> heights;

	@Override
	public void click(View v) {
		switch (v.getId()) {
		case R.id.modify_imgv_back:
			this.finish();
			break;
		case R.id.modify_btn_true:
			save2Server();

			break;
		case R.id.modify_btn_cancel:
			finish();
			break;
		case R.id.modify_tv_addr:
			startActivityForResult(new Intent(this, ModifyCityActivity.class), 1);
			break;
		case R.id.modify_tv_height:
			ChooseHeightView height = new ChooseHeightView(this, this);
			
			height.setConfirmHeightListener(new ChooseHeightView.ConfirmHeight() {
				@Override
				public void function(String height) {
					ModifyDataActivity.this.height = height;
					mTv_height.setText(height);
				}
			});
			
			height.show();

			break;
		case R.id.modify_tv_weight:
			ChooseWeightView weight = new ChooseWeightView(this, this);
			
			weight.setConfirmWeightListener(new ChooseWeightView.ConfirmWeight() {
				@Override
				public void function(String weight) {
					ModifyDataActivity.this.weight = weight;
					mTv_weight.setText(weight);
				}
			}); 
			
			weight.show();
			break;
		case R.id.modify_tv_birth:
			showDatePickerDialog();

			break;
		case R.id.modify_tv_name:

			startActivityForResult(new Intent(this, ModifyNameActivity.class), 0);
			break;
		case R.id.modify_tv_sex:
			final Dialog sexDialog = new Dialog(this);
			sexDialog.setCanceledOnTouchOutside(false);
			sexDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			sexDialog.setTitle("请选择性别");
			sexDialog.setContentView(R.layout.dialog_modifysex);
			Window dialogWindow = sexDialog.getWindow();
			WindowManager.LayoutParams lp = dialogWindow.getAttributes();
			dialogWindow.setGravity(Gravity.CENTER);
			WindowManager m = dialogWindow.getWindowManager();
			Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
			lp.height = (int) (d.getHeight() * 0.3); // 高度设置为屏幕的0.6
			lp.width = (int) (d.getWidth() * 0.8); // 宽度设置为屏幕的0.65
			dialogWindow.setAttributes(lp);
			sexDialog.show();
			final TextView mTv_man = (TextView) sexDialog.findViewById(R.id.modifysex_tv_man);
			final TextView mTv_woman = (TextView) sexDialog.findViewById(R.id.modifysex_tv_woman);
			mTv_man.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mTv_sex.setText("男");
					sex = "男";
					sexDialog.cancel();
				}
			});
			mTv_woman.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mTv_sex.setText("女");
					sex = "女";
					sexDialog.cancel();
				}
			});
			break;
		default:
			break;
		}
	}

	/*
	 * 设置修改后姓名
	 */

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		if (arg2 == null) {
			return;
		}
		switch (arg1) {
		case 0:
			trueName = arg2.getBundleExtra("name").getString("name");
			if (!TextUtils.isEmpty(trueName)) {
				mTv_name.setText(trueName);
			}
			break;
		case 1:
			address = arg2.getBundleExtra("address").getString("address");
			if (!TextUtils.isEmpty(address)) {
//				Log.wtf("adr", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa+++++++++");
				mTv_addr.setText(address);
			} 
			break;
		}

	}

	private void go2Db(String str) {
		if (baseHelper == null) {
			baseHelper = new DataBaseHelper(this);
		}
		SQLiteDatabase database = baseHelper.getReadableDatabase();
		Cursor c;
		if (str.equals("name")) {
			c = database.rawQuery("select fullname from patient where userId=?", new String[] { String.valueOf(App.USERID) });
			if (c.moveToNext()) {
				if (!TextUtils.isEmpty(c.getString(0))) {
					mTv_name.setText(c.getString(0));
				} else {
					mTv_name.setText("请输入姓名");
				}
			}
		} else {
			c = database.rawQuery("select address from patient where userId=?", new String[] { String.valueOf(App.USERID) });
			if (c.moveToNext()) {
				if (!TextUtils.isEmpty(c.getString(0))) {
					mTv_addr.setText(c.getString(0));
				} else {
					mTv_addr.setText("请选择居住地址");
				}

			}
		}
		c.close();
	}

	private void showDatePickerDialog() {
		// mCalendar=Calendar.getInstance();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View view = getLayoutInflater().inflate(R.layout.dialog_choosebirth, null);
		final DatePicker datePicker = (DatePicker) view.findViewById(R.id.dialog_dp_brith);
		/*
		 * mYear=mCalendar.get(Calendar.YEAR);
		 * mMonth=mCalendar.get(Calendar.MONDAY);
		 * mDay=mCalendar.get(Calendar.DAY_OF_MONTH);
		 */
		datePicker.init(1990, 0, 1, null);
		builder.setView(view);
		builder.setTitle("请选择出生日期");
		builder.setPositiveButton("选定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				mTv_birth.setText(datePicker.getYear() + "年" + (datePicker.getMonth() + 1) + "月" + datePicker.getDayOfMonth() + "日");
				birth = datePicker.getYear() + "年" + (datePicker.getMonth() + 1) + "月" + datePicker.getDayOfMonth() + "日";
				arg0.cancel();
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				arg0.cancel();
			}
		});
		builder.create().show();

	}

	/**
	 * 保存修改的资料到数据库
	 */
	DataBaseHelper baseHelper;

	private void save2Db(final Long time) {
		if (baseHelper == null) {
			baseHelper = new DataBaseHelper(this);
		}
		new Thread(new Runnable() {

			@Override
			public void run() {
				ContentValues values = new ContentValues();
				if (!TextUtils.isEmpty(trueName)) {
					values.put("fullname", trueName);
				}
				if (mTv_sex.getText().toString().trim().length() == 1) {
					values.put("sex", mTv_sex.getText().toString().trim().equals("男") ? "男" : "女");
				}

				if (mTv_birth.getText().toString().contains("年")) {
					values.put("birthday", mTv_birth.getText().toString().trim());
				}
				if (mTv_height.getText().toString().contains("cm")) {
					values.put("height", mTv_height.getText().toString().trim());
				}
				if (mTv_weight.getText().toString().contains("kg")) {
					values.put("weight", mTv_weight.getText().toString().trim());
				}
				if (mTv_addr.getText().toString().contains("市")) {
					values.put("address", mTv_addr.getText().toString().trim());
				}
				values.put("modtime", time);
				baseHelper.getWritableDatabase().update("patient", values, "userId=?", new String[] { String.valueOf(App.USERID) });

			}

		}).start();
		baseHelper.close();

		mHandler.sendEmptyMessage(-1);

	}

	String getEncoder(String s) {
		String string = null;
		try {
			string = URLEncoder.encode(s, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return string;
	}

	String getDecoder(String s) {
		String string = null;
		try {
			string = URLDecoder.decode(s, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return string;
	}

	/**
	 * 修改的数据保存服务器
	 */
	private void save2Server() {
		RequestQueue rq = App.getRequestQueue();
		StringBuilder url = new StringBuilder(App.BASE + Constant.UPDATA_INFO);
		url.append("data=");
		JSONObject json = new JSONObject();

		try {
			if (!TextUtils.isEmpty(trueName)) {
				json.put("trueName", getEncoder(trueName));
			}
			if (!TextUtils.isEmpty(sex)) {
				json.put("sex",  getEncoder(sex));
			}
			if (!TextUtils.isEmpty(birth)) {
				json.put("birthday", getEncoder(birth));
			}
			if (!TextUtils.isEmpty(height)) {
				json.put("height", height);
			}
			if (!TextUtils.isEmpty(weight)) {
				json.put("weight", weight);
			}
			if (!TextUtils.isEmpty(address)) {
				json.put("address", getEncoder(address));
			}

			url.append(json.toString());
			url.append("&tocken=" + App.TOKEN);
//			Log.wtf("xxxxxx", url.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		rq.add(new StringRequest(Request.Method.GET, url.toString(), new Listener<String>() {

			@Override
			public void onResponse(String response) {
				try {
					JSONObject json = new JSONObject(response);
					String code = json.getString("code");
					if ("206".equals(code)) {
//						long time = json.getLong("message");
//						Message msg = mHandler.obtainMessage(0);
//						msg.obj = time;
//						msg.sendToTarget();
						mHandler.sendEmptyMessage(0);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

			}
		}));
		rq.start();
		// 服务器保存成功后保存数据到本地数据库
	}

	public Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case -1:
				ModifyDataActivity.this.finish();
				break;
			case 0:
				MyToast.show("保存资料成功");
				App.SEX =  sex;
				App.BIRTHDAY =  birth;
				App.HEIGHT = height ;
				App.WEIGHT = weight;
				App.TRUENAME = trueName;
				App.ADDRESS = address;
				//				long time = (Long) msg.obj;
//				save2Db(time);
				ModifyDataActivity.this.finish();
				break;
			case 1:
				MyToast.show("保存资料失败");
				break;
			}
		}
	};
}
