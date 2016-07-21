package com.hykj.activity.messure;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;
import com.hykj.App;
import com.hykj.Constant;
import com.hykj.R;
import com.hykj.activity.BaseActivity;
import com.hykj.utils.MyToast;
import com.hykj.utils.ScreenUtils;
import com.hykj.view.SetDietTimeView;

/**
 * @author 作者 : zhaoyu
 * @version 创建时间：2016年4月12日 下午4:39:08 类说明：饮食情况
 */
public class DietConditionActivity extends BaseActivity {
    private Button mBtn_breakfast, mBtn_lunch, mBtn_dinner;
    private ImageView mImg_back;
    private Button mBtn_save;
    private EditText mEdt_describe;
    public TextView mTv_time;


    @Override
    public void init() {
        setContentView(R.layout.activity_dietcondition);
        initViews();
    }


    private void initViews() {
        mImg_back = (ImageView) findViewById(R.id.dietconditon_imgv_back);
        mBtn_breakfast = (Button) findViewById(R.id.dietcondition_btn_breakfast);
        mBtn_lunch = (Button) findViewById(R.id.dietcondition_btn_lunch);
        mBtn_dinner = (Button) findViewById(R.id.dietcondition_btn_dinner);
        mBtn_save = (Button) findViewById(R.id.dietcondition_btn_save);
        mEdt_describe = (EditText) findViewById(R.id.dietcondition_edit_about);
        mTv_time = (TextView) findViewById(R.id.dietcondition_tv_time);
        mImg_back.setOnClickListener(this);
        mBtn_breakfast.setOnClickListener(this);
        mBtn_lunch.setOnClickListener(this);
        mBtn_dinner.setOnClickListener(this);
        mBtn_save.setOnClickListener(this);
        mBtn_breakfast.setSelected(true);
        mTv_time.setOnClickListener(this);


    }

    @Override
    public void click(View v) {
        switch (v.getId()) {
            case R.id.dietconditon_imgv_back:
                finish();
                break;
            case R.id.dietcondition_btn_save:

                save2Server();
                break;
            case R.id.dietcondition_btn_breakfast:
                mBtn_breakfast.setBackgroundResource(R.drawable.round_selecte);
                mBtn_lunch.setBackgroundResource(R.drawable.round_normal);
                mBtn_dinner.setBackgroundResource(R.drawable.round_normal);
                mBtn_breakfast.setTextColor(0xffffffff);
                mBtn_lunch.setTextColor(0xffcccccc);
                mBtn_dinner.setTextColor(0xffcccccc);
                mBtn_breakfast.setSelected(true);
                mBtn_lunch.setSelected(false);
                mBtn_dinner.setSelected(false);
                mEdt_describe.setText("");
                break;
            case R.id.dietcondition_btn_lunch:
                mBtn_breakfast.setBackgroundResource(R.drawable.round_normal);
                mBtn_lunch.setBackgroundResource(R.drawable.round_selecte);
                mBtn_dinner.setBackgroundResource(R.drawable.round_normal);
                mBtn_breakfast.setTextColor(0xffcccccc);
                mBtn_lunch.setTextColor(0xffffffff);
                mBtn_dinner.setTextColor(0xffcccccc);
                mBtn_breakfast.setSelected(false);
                mBtn_lunch.setSelected(true);
                mBtn_dinner.setSelected(false);
                mEdt_describe.setText("");
                break;
            case R.id.dietcondition_btn_dinner:
                mBtn_breakfast.setBackgroundResource(R.drawable.round_normal);
                mBtn_lunch.setBackgroundResource(R.drawable.round_normal);
                mBtn_dinner.setBackgroundResource(R.drawable.round_selecte);
                mBtn_breakfast.setTextColor(0xffcccccc);
                mBtn_lunch.setTextColor(0xffcccccc);
                mBtn_dinner.setTextColor(0xffffffff);
                mBtn_breakfast.setSelected(false);
                mBtn_lunch.setSelected(false);
                mBtn_dinner.setSelected(true);
                mEdt_describe.setText("");
                break;
            case R.id.dietcondition_tv_time:
                SetDietTimeView view = new SetDietTimeView(this,this);
                view.setCanceledOnTouchOutside(true);
                view.setCancelable(true);
                view.show();
                Window dialogWindow = view.getWindow();
                WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                dialogWindow.setGravity(Gravity.CENTER);
                WindowManager m = dialogWindow.getWindowManager();
                Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
                lp.height = (int) (d.getHeight() * 0.4); // 高度设置为屏幕的0.6
                lp.width = ScreenUtils.getScreenWidth(App.getContext()); // 宽度设置为屏幕的0.65
                dialogWindow.setAttributes(lp);
                break;
            default:
                break;
        }
    }



    private void save2Server() {
        RequestQueue rq = App.getRequestQueue();
        StringBuilder url = new StringBuilder(App.BASE + Constant.DIETADD
                + "data=");
        JSONObject json = new JSONObject();
        try {
            if (mBtn_breakfast.isSelected()) {
                json.put("type", getEncoder("早餐"));
            }
            if (mBtn_lunch.isSelected()) {
                json.put("type", getEncoder("午餐"));
            }
            if (mBtn_dinner.isSelected()) {
                json.put("type", getEncoder("晚餐"));
            }
            if (TextUtils.isEmpty(mEdt_describe.getText())) {
                mHandler.sendEmptyMessage(1);
                return;
            } else {
                json.put("description", getEncoder(mEdt_describe.getText().toString()
                        .trim()));
            }
            if (TextUtils.isEmpty(mTv_time.getText())) {
                mHandler.sendEmptyMessage(3);
                return;
            } else {
                json.put(
                        "date", getEncoder(mTv_time.getText().toString()));
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        url.append(json.toString());
        url.append("&tocken=" + App.TOKEN);
//		Log.wtf("咨询请求url", url.toString());
        rq.add(new StringRequest(Request.Method.GET, url.toString(),
                new Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        JSONObject o;
                        try {
                            o = new JSONObject(response);
                            if ("206".equals(o.getString("code"))) {
                                mHandler.sendEmptyMessage(0);
                            } else {
                                mHandler.sendEmptyMessage(2);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mHandler.sendEmptyMessage(2);
            }
        }));
        rq.start();

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

    public Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    MyToast.show("保存成功");
                    finish();
                    break;
                case 1:
                    MyToast.show("请填写饮食说明");
                    break;
                case 2:
                    MyToast.show("保存失败");
                    break;
                case 3:
                    MyToast.show("请设置时间");
                default:
                    break;
            }
        }
    };

}
