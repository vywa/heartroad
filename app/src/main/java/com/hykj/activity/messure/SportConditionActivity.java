package com.hykj.activity.messure;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
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
import com.hykj.db.DataBaseHelper;
import com.hykj.entity.MyDate;
import com.hykj.utils.MyToast;
import com.hykj.utils.ScreenUtils;
import com.hykj.view.SetDietTimeView;
import com.hykj.view.SetSportTimeView;
import com.hykj.view.spinnerwheel.AbstractWheel;
import com.hykj.view.spinnerwheel.OnWheelChangedListener;
import com.hykj.view.spinnerwheel.WheelHorizontalView;
import com.hykj.view.spinnerwheel.WheelVerticalView;
import com.hykj.view.spinnerwheel.adapters.ArrayWheelAdapter;

/**
 * @author 作者 : zhaoyu
 * @version 创建时间：2016年4月13日 下午4:16:21 类说明：运动情况
 */
public class SportConditionActivity extends BaseActivity {
    private Button mBtn_slight, mBtn_medium, mBtn_severe;
    private ImageView mImg_back;
    private Button mBtn_save;

    private EditText mEdt_describe, mEdt_long;
    public TextView mTv_time;


    @Override
    public void init() {
        setContentView(R.layout.activity_sportcondition);
        initViews();
    }


    private void initViews() {
        mImg_back = (ImageView) findViewById(R.id.sportcondition_imgv_back);
        mBtn_slight = (Button) findViewById(R.id.sportcondition_btn_slight);
        mBtn_medium = (Button) findViewById(R.id.sportcondition_btn_medium);
        mBtn_severe = (Button) findViewById(R.id.sportcondition_btn_severe);
        mBtn_save = (Button) findViewById(R.id.sportcondition_btn_save);
        mEdt_long = (EditText) findViewById(R.id.sportcondition_edt_long);
        mImg_back.setOnClickListener(this);
        mBtn_slight.setOnClickListener(this);
        mBtn_medium.setOnClickListener(this);
        mBtn_severe.setOnClickListener(this);
        mBtn_save.setOnClickListener(this);
        mBtn_slight.setSelected(true);
        mEdt_describe = (EditText) findViewById(R.id.sportcondition_edit_about);
        mTv_time = (TextView) findViewById(R.id.sportcondition_tv_time);
        mTv_time.setOnClickListener(this);
    }


    @Override
    public void click(View v) {
        switch (v.getId()) {
            case R.id.sportcondition_imgv_back:
                finish();
                break;
            case R.id.sportcondition_btn_save:
                save2Db();
                break;
            case R.id.sportcondition_btn_slight:
                mBtn_slight.setBackgroundResource(R.drawable.round_selecte);
                mBtn_medium.setBackgroundResource(R.drawable.round_normal);
                mBtn_severe.setBackgroundResource(R.drawable.round_normal);
                mBtn_slight.setTextColor(0xffffffff);
                mBtn_medium.setTextColor(0xffcccccc);
                mBtn_severe.setTextColor(0xffcccccc);
                mBtn_slight.setSelected(true);
                mBtn_medium.setSelected(false);
                mBtn_severe.setSelected(false);
                mEdt_describe.setText("");
                break;
            case R.id.sportcondition_btn_medium:
                mBtn_slight.setBackgroundResource(R.drawable.round_normal);
                mBtn_medium.setBackgroundResource(R.drawable.round_selecte);
                mBtn_severe.setBackgroundResource(R.drawable.round_normal);
                mBtn_slight.setTextColor(0xffcccccc);
                mBtn_medium.setTextColor(0xffffffff);
                mBtn_severe.setTextColor(0xffcccccc);
                mBtn_slight.setSelected(false);
                mBtn_medium.setSelected(true);
                mBtn_severe.setSelected(false);
                mEdt_describe.setText("");
                break;
            case R.id.sportcondition_btn_severe:
                mBtn_slight.setBackgroundResource(R.drawable.round_normal);
                mBtn_medium.setBackgroundResource(R.drawable.round_normal);
                mBtn_severe.setBackgroundResource(R.drawable.round_selecte);
                mBtn_slight.setSelected(false);
                mBtn_medium.setSelected(false);
                mBtn_severe.setSelected(true);
                mBtn_slight.setTextColor(0xffcccccc);
                mBtn_medium.setTextColor(0xffcccccc);
                mBtn_severe.setTextColor(0xffffffff);
                mEdt_describe.setText("");
                break;
            case R.id.sportcondition_tv_time:
                SetSportTimeView view = new SetSportTimeView(this,this);
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

    private void save2Db() {
        RequestQueue rq = App.getRequestQueue();
        StringBuilder url = new StringBuilder(App.BASE + Constant.SPORTADD
                + "data=");
        JSONObject json = new JSONObject();
        try {
            if (mBtn_slight.isSelected()) {
                json.put("type", getEncoder("轻微"));
            }
            if (mBtn_medium.isSelected()) {
                json.put("type", getEncoder("中等"));
            }
            if (mBtn_severe.isSelected()) {
                json.put("type", getEncoder("剧烈"));
            }
            if (TextUtils.isEmpty(mEdt_describe.getText())) {
                mHandler.sendEmptyMessage(1);
                return;
            } else {
                json.put("description", getEncoder(mEdt_describe.getText()
                        .toString().trim()));
            }
            if (TextUtils.isEmpty(mEdt_long.getText())) {
                mHandler.sendEmptyMessage(2);
                return;
            } else {
                json.put("time", getEncoder(mEdt_long.getText().toString()
                        .trim()));
            }
            if (TextUtils.isEmpty(mTv_time.getText())) {
                mHandler.sendEmptyMessage(4);
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
        Log.wtf("咨询请求url", url.toString());
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
                                mHandler.sendEmptyMessage(3);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mHandler.sendEmptyMessage(3);
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
                    MyToast.show("请填写运动说明");
                    break;
                case 2:
                    MyToast.show("请填写运动时间");
                    break;
                case 3:
                    MyToast.show("保存失败");
                case 4:
                    MyToast.show("请设置运动时间");
                    break;
                default:
                    break;
            }
        }
    };
}
