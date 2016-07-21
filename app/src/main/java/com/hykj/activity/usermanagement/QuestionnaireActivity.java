package com.hykj.activity.usermanagement;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hykj.App;
import com.hykj.R;
import com.hykj.activity.BaseActivity;
import com.hykj.utils.MyToast;

/**
 * @author 作者 : zhaoyu
 * @version 创建时间：2016年3月17日 上午9:53:31 类说明
 */
public class QuestionnaireActivity extends BaseActivity implements
		OnCheckedChangeListener {
	private Button mBtn_ignore, mBtn_next;
	private CheckBox one_cb1, one_cb2, one_cb3, one_cb4, one_cb5, one_cb6,
			one_cb7, one_cb8;
	private CheckBox two_cb1, two_cb2, two_cb3, two_cb4, two_cb5, two_cb6;
	private CheckBox three_cb1, three_cb2, three_cb3, three_cb4, three_cb5,
			three_cb6;

	@Override
	public void init() {
		setContentView(R.layout.activity_questionnaire);
		mBtn_ignore = (Button) findViewById(R.id.naire_btn_ignore);
		mBtn_next = (Button) findViewById(R.id.naire_btn_next);
		mBtn_ignore.setOnClickListener(this);
		mBtn_next.setOnClickListener(this);
		one_cb1 = (CheckBox) findViewById(R.id.naire_one_cb1);
		one_cb1.setOnCheckedChangeListener(this);
		one_cb2 = (CheckBox) findViewById(R.id.naire_one_cb2);
		one_cb2.setOnCheckedChangeListener(this);
		one_cb3 = (CheckBox) findViewById(R.id.naire_one_cb3);
		one_cb3.setOnCheckedChangeListener(this);
		one_cb4 = (CheckBox) findViewById(R.id.naire_one_cb4);
		one_cb4.setOnCheckedChangeListener(this);
		one_cb5 = (CheckBox) findViewById(R.id.naire_one_cb5);
		one_cb5.setOnCheckedChangeListener(this);
		one_cb6 = (CheckBox) findViewById(R.id.naire_one_cb6);
		one_cb6.setOnCheckedChangeListener(this);
		one_cb7 = (CheckBox) findViewById(R.id.naire_one_cb7);
		one_cb7.setOnCheckedChangeListener(this);
		one_cb8 = (CheckBox) findViewById(R.id.naire_one_cb8);
		one_cb8.setOnCheckedChangeListener(this);

		two_cb1 = (CheckBox) findViewById(R.id.naire_two_cb1);
		two_cb1.setOnCheckedChangeListener(this);
		two_cb2 = (CheckBox) findViewById(R.id.naire_two_cb2);
		two_cb2.setOnCheckedChangeListener(this);
		two_cb3 = (CheckBox) findViewById(R.id.naire_two_cb3);
		two_cb3.setOnCheckedChangeListener(this);
		two_cb4 = (CheckBox) findViewById(R.id.naire_two_cb4);
		two_cb4.setOnCheckedChangeListener(this);
		two_cb5 = (CheckBox) findViewById(R.id.naire_two_cb5);
		two_cb5.setOnCheckedChangeListener(this);
		two_cb6 = (CheckBox) findViewById(R.id.naire_two_cb6);
		two_cb6.setOnCheckedChangeListener(this);

		three_cb1 = (CheckBox) findViewById(R.id.naire_three_cb1);
		three_cb1.setOnCheckedChangeListener(this);
		three_cb2 = (CheckBox) findViewById(R.id.naire_three_cb2);
		three_cb2.setOnCheckedChangeListener(this);
		three_cb3 = (CheckBox) findViewById(R.id.naire_three_cb3);
		three_cb3.setOnCheckedChangeListener(this);
		three_cb4 = (CheckBox) findViewById(R.id.naire_three_cb4);
		three_cb4.setOnCheckedChangeListener(this);
		three_cb5 = (CheckBox) findViewById(R.id.naire_three_cb5);
		three_cb5.setOnCheckedChangeListener(this);
		three_cb6 = (CheckBox) findViewById(R.id.naire_three_cb6);
		three_cb6.setOnCheckedChangeListener(this);
	}

	@Override
	public void click(View v) {
		switch (v.getId()) {
		case R.id.naire_btn_ignore:
			this.finish();
			break;
		case R.id.naire_btn_next:
			CheckSelected();
			break;
		default:
			break;
		}
	}

	private void CheckSelected() {
		StringBuilder dangerstring = new StringBuilder("0,");// 危险因素
		if (one_cb1.isChecked()) {
			dangerstring.append("1,");
		}
		if (one_cb2.isChecked()) {
			dangerstring.append("2,");
		}
		if (one_cb3.isChecked()) {
			dangerstring.append("3,");
		}
		if (one_cb4.isChecked()) {
			dangerstring.append("4,");
		}
		if (one_cb5.isChecked()) {
			dangerstring.append("5,");
		}
		if (one_cb6.isChecked()) {
			dangerstring.append("6,");
		}
		if (one_cb7.isChecked()) {
			dangerstring.append("7,");
		}
		if (one_cb8.isChecked()) {
			dangerstring.append("8,");
		}
		dangerstring.append("0");
		StringBuilder damagestring = new StringBuilder("0,");// 靶器官损害
		if (two_cb1.isChecked()) {
			damagestring.append("1,");
		}
		if (two_cb2.isChecked()) {
			damagestring.append("2,");
		}
		if (two_cb3.isChecked()) {
			damagestring.append("3,");
		}
		if (two_cb4.isChecked()) {
			damagestring.append("4,");
		}
		if (two_cb5.isChecked()) {
			damagestring.append("5,");
		}
		if (two_cb6.isChecked()) {
			damagestring.append("6,");
		}
		damagestring.append("0");
		StringBuilder diseasestring = new StringBuilder("0,");// 伴临床疾患
		
		if (three_cb1.isChecked()) {
			diseasestring.append("1,");
			
		}
		if (three_cb2.isChecked()) {
			diseasestring.append("2,");
		}
		if (three_cb3.isChecked()) {
			diseasestring.append("3,");
		}
		if (three_cb4.isChecked()) {
			diseasestring.append("4,");
		}
		if (three_cb5.isChecked()) {
			diseasestring.append("5,");
		}
		if (three_cb6.isChecked()) {
			diseasestring.append("6,");
		}
		diseasestring.append("0");
		go2Server(dangerstring.toString(), damagestring.toString(),
				diseasestring.toString());
	}

	private void go2Server(String str1, String str2, String str3) {
	//	diagnosis/upload?data={"danger":[1,2]",damage":[2,3]"":""}
		RequestQueue rq = App.getRequestQueue();
		StringBuilder url = new StringBuilder(App.BASE+"diagnosis/upload?data=");
		JSONObject json = new JSONObject();
		try {
		
			
			json.put("danger","["+str1+"]" );
			json.put("damage", "["+str2+"]");
			json.put("disease", "["+str3+"]");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		url.append(json.toString());
		url.append("&tocken="+App.TOKEN);
//		Log.wtf("sdfsfsd", url.toString());
		rq.add(new StringRequest(Request.Method.GET, url.toString(), new Listener<String>() {

			@Override
			public void onResponse(String response) {
					try {
						JSONObject o=new JSONObject(response);
						String code=o.getString("code");
						if("206".equals(code)){
//							Log.wtf("sdfsfs", "上传成功");
							mHandler.sendEmptyMessage(0);
						}else{
							mHandler.sendEmptyMessage(1);
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				
			}
		}));
		rq.start();
	}
	public Handler mHandler =new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				MyToast.show("数据上传成功");
				//startActivity(new Intent(QuestionnaireActivity.this, MainActivity.class));
				QuestionnaireActivity.this.finish();
				break;
			case 1:
				MyToast.show("数据上传失败");
				break;
			default:
				break;
			}
		}
	};
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (buttonView.isChecked()) {
			buttonView.setTextColor(Color.parseColor("#de4559"));
		} else {
			buttonView.setTextColor(Color.parseColor("#616060"));
		}
	}

}
