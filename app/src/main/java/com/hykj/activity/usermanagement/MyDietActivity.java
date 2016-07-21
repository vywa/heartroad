package com.hykj.activity.usermanagement;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

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
import com.hykj.adapter.MydietAdapter;
import com.hykj.broadcast.AlarmBroadcastReceiver;
import com.hykj.entity.Dietcondition;
import com.hykj.utils.MyToast;

/**
 * @author 作者 : zhaoyu
 * @version 创建时间：2016年4月13日 上午11:28:33 类说明：我的饮食
 */
public class MyDietActivity extends BaseActivity {
    private ListView mLv;
    private ImageView mImg_back;
    private List<Dietcondition> data;
    private MydietAdapter adapter;

    @Override
    public void init() {
        setContentView(R.layout.activity_mydiet);
        mLv = (ListView) findViewById(R.id.mydiet_lv);
        mImg_back = (ImageView) findViewById(R.id.mydiet_imgv_back);
        mImg_back.setOnClickListener(this);
        getDataFromServer();
        mLv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {
                final Dialog dialog = new Dialog(
                        MyDietActivity.this);
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_del_diet);
                Window dialogWindow = dialog.getWindow();
                WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                dialogWindow.setGravity(Gravity.CENTER);
                WindowManager m = dialogWindow.getWindowManager();
                Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
                lp.height = (int) (d.getHeight() * 0.3); // 高度设置为屏幕的0.6
                lp.width = (int) (d.getWidth() * 0.8); // 宽度设置为屏幕的0.65
                dialogWindow.setAttributes(lp);
                Button btn_true = (Button) dialog
                        .findViewById(R.id.dialog_btn_diet_true);
                Button btn_cancel = (Button) dialog
                        .findViewById(R.id.dialog_btn_diet_cancel);
                dialog.show();
                btn_cancel.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
                btn_true.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                        delData(position);
                        if (delSuccess) {
                            data.remove(position);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });

            }
        });

    }

    private boolean delSuccess = true;

    protected void delData(int position) {
        RequestQueue rq = App.getRequestQueue();
        StringBuilder url = new StringBuilder(App.BASE + Constant.DIETDELETE);
        JSONObject json = new JSONObject();
        url.append("data=");
        try {
            json.put("id", data.get(position).getId());
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        url.append(json.toString());
        url.append("&tocken=" + App.TOKEN);
        Log.wtf("删除饮食", url.toString());

        rq.add(new StringRequest(Request.Method.GET, url.toString(),
                new Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject o = new JSONObject(response);
                            if ("206".equals(o.getString("code"))) {
                                mHandler.sendEmptyMessage(2);
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


    private void getDataFromServer() {

        data = new ArrayList<Dietcondition>();
        RequestQueue rq = App.getRequestQueue();
        StringBuilder url = new StringBuilder(App.BASE + Constant.DIETQUERY
                + "tocken=" + App.TOKEN);
        Log.wtf("咨询请求url", url.toString());

        rq.add(new StringRequest(Request.Method.GET, url.toString(),
                new Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject o = new JSONObject(response);
                            if ("206".equals(o.getString("code"))) {
                                JSONArray array = o
                                        .optJSONArray("dietInfoList");

                                if (array != null) {
                                    for (int i = 0; i < array.length(); i++) {
                                        JSONObject o1 = array.optJSONObject(i);
                                        String date = o1.optString("date");
                                        String describe = o1
                                                .optString("description");
                                        int id = o1.optInt("id");
                                        String type = o1.optString("type");
                                        Message msg = mHandler.obtainMessage(0);
                                        Bundle b = new Bundle();
                                        b.putString("date", date);
                                        b.putString("describe", describe);
                                        b.putInt("id", id);
                                        b.putString("type", type);
                                        msg.obj = b;
                                        msg.sendToTarget();
                                    }

                                }
                            } else {
                                mHandler.sendEmptyMessage(1);
                            }
                        } catch (JSONException e) {
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

    public Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    Bundle b = (Bundle) msg.obj;
                    Dietcondition diet = new Dietcondition(b.getInt("id"), b.getString("type"),
                            b.getString("describe"), b.getString("date"));
                    data.add(diet);
                    adapter = new MydietAdapter(MyDietActivity.this, data);
                    mLv.setAdapter(adapter);
                    break;
                case 1:
                    MyToast.show("暂无饮食数据，添加后查看");
                    break;
                case 2:
                    delSuccess = true;
                    MyToast.show("删除成功");
                    break;
                case 3:
                    delSuccess = false;
                    MyToast.show("网络失败");
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void click(View v) {
        if (v.getId() == R.id.mydiet_imgv_back) {
            finish();
        }
    }

}