package com.hykj.activity.usermanagement;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.gc.materialdesign.widgets.Dialog;
import com.hykj.App;
import com.hykj.Constant;
import com.hykj.R;
import com.hykj.service.DownloadService;
import com.hykj.utils.Coder;
import com.hykj.utils.MyLog;

public class StartActivity extends Activity {

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case GET_DATA_SUCCESS:

                    checkVersion();

                    break;
                case GET_DATA_ANALYZE_ERROR:
                case GET_DATA_ERROR:
                    mHandler.sendEmptyMessageDelayed(-1, 2000);
                    break;
                case -1:
                /*boolean flag = false;

				SharedPreferences setting = getSharedPreferences("First", 0);
				String str = setting.getString("key", "");
				if (str.equals("")) {

					Intent intent = new Intent(StartActivity.this,
							WelcomeActivity.class);
					startActivity(intent);
					finish();
				} else {
					flag = true;
				}
				if (flag) {
					Intent mIntent = new Intent(StartActivity.this,
							LoginActivity.class);
					startActivity(mIntent);
					finish();
				}*/
                    SharedPreferences welcome=getSharedPreferences("welcome",Activity.MODE_PRIVATE);
                    String isWelcome=welcome.getString("welcome","");
                    if (TextUtils.isEmpty(isWelcome)) {
                        Intent mIntent = new Intent(StartActivity.this, WelcomeActivity.class);
                        startActivity(mIntent);
                        finish();
                    }else {
                        SharedPreferences share = getSharedPreferences("autologin", Activity.MODE_PRIVATE);

                        String token = share.getString("token", "");
                        String userid = share.getString("userid", "");
                        String password = share.getString("password", "");

                        if (TextUtils.isEmpty(token)) {
                            Intent mIntent = new Intent(StartActivity.this, LoginActivity.class);
                            startActivity(mIntent);
                            finish();
                        } else {
                            try {
                                token = new String(Coder.ees3DecodeECB(Base64.decode(token.getBytes("UTF-8"), Base64.DEFAULT)), "UTF-8");
                                userid = new String(Coder.ees3DecodeECB(Base64.decode((userid + "").getBytes("UTF-8"), Base64.DEFAULT)), "UTF-8");
                                password = new String(Coder.ees3DecodeECB(Base64.decode(password.getBytes("UTF-8"), Base64.DEFAULT)), "UTF-8");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            MyLog.wtf("pwd", password);
                            MyLog.wtf("userid", "" + userid);
                            MyLog.wtf("TOKEN", token);

                            App.TOKEN = token;
                            App.USERID = Integer.parseInt(userid);
                            App.tempPwd = password;

                            go2Server(token);
                        }
                    }
                    break;
                case 0:
                    Intent intent = new Intent(StartActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case 4:
                    Intent mIntent = new Intent(StartActivity.this, LoginActivity.class);
                    startActivity(mIntent);
                    finish();
                    break;
            }
        }

        ;
    };

    private void go2Server(final String token) {
        RequestQueue q = App.getRequestQueue();
        final String url = App.BASE + Constant.AUTOLOGIN;
        q.add(new StringRequest(Request.Method.GET, url + "tocken=" + token,
                new Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.wtf("aaa", url + "tocken=" + token);
                            JSONObject json = new JSONObject(response);
                            String code = json.getString("code");
                            if ("206".equals(code)) {
                                mHandler.sendEmptyMessage(0);
                            }
                            if ("106".equals(code)) {
                                mHandler.sendEmptyMessage(4);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            mHandler.sendEmptyMessage(4);
                        }
                    }
                }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mHandler.sendEmptyMessage(4);
            }
        }));
    }

    String localCode;
    String serverCode;
    String apkUrl;
    String description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        getLocalVersion();
        getServerVersion();

    }

    protected void checkVersion() {
        if (localCode.equals(serverCode)) {
            mHandler.sendEmptyMessageDelayed(-1, 2000);
        } else {
            showUpdataDialog();
        }
    }

    protected void showUpdataDialog() {
        final Dialog dialog = new com.gc.materialdesign.widgets.Dialog(this,
                "版本升级", description);
        dialog.setOnAcceptButtonClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                downLoadApk();
            }
        });
        dialog.addCancelButton("取消", new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                mHandler.sendEmptyMessageDelayed(-1, 1000);
            }
        });
        dialog.show();
    }

    private void downLoadApk() {

        Intent updateIntent = new Intent(this, DownloadService.class);
        updateIntent.putExtra("fileUrl", apkUrl);
        startService(updateIntent);
        mHandler.sendEmptyMessage(-1);
    }

    private void getServerVersion() {
        StringRequest request = getreqData();
        App.getRequestQueue().add(request);
    }

    /**
     * 获取版本本地应用的版本信息
     */
    private void getLocalVersion() {
        PackageManager manager = getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
            localCode = info.versionName;// 获取到当前应用的版本号
        } catch (NameNotFoundException e) {
        }
    }

    private StringRequest getreqData() {
        return new StringRequest(Method.GET, App.BASE + Constant.CHECKVERSION,
                new Listener<String>() {
                    @Override
                    public void onResponse(String res) {
//						Log.wtf("版本更新的响应", res);
                        try {
                            JSONObject response = new JSONObject(res);
                            if ("206".equals(response.optString("code"))) {
                                JSONObject jsonObject = response.getJSONObject("version");
                                serverCode = jsonObject.getString("serverCode");
                                apkUrl = jsonObject.getString("apkUrl");
                                description = jsonObject.getString("description");
                                mHandler.sendEmptyMessage(GET_DATA_SUCCESS);
                            } else {
                                mHandler.sendEmptyMessage(GET_DATA_ERROR);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            mHandler.sendEmptyMessage(GET_DATA_ANALYZE_ERROR);
                        }
                    }
                }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                mHandler.sendEmptyMessage(GET_DATA_ERROR);
            }
        });
    }

    public static final int GET_DATA_SUCCESS = 1;
    public static final int GET_DATA_ANALYZE_ERROR = 2;
    public static final int GET_DATA_ERROR = 3;

}
