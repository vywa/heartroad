package com.hykj.activity.usermanagement;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.Service;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;

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
import com.hykj.service.RegisterCodeTimerService;
import com.hykj.utils.Check;
import com.hykj.utils.Decoder;
import com.hykj.utils.MyProgress;
import com.hykj.utils.MyToast;
import com.hykj.utils.MyDialog;
import com.hykj.view.RegisterCodeTimer;

/**
 * @author 作者 : zhaoyu
 * @version 创建时间：2016年1月14日 下午3:58:11 类说明：绑定用户信息
 */
public class BindUserActivity extends BaseActivity {

    private TextView mTv_phoneno, mTv_email, mTv_qq, mTv_wx, mTv_wb;
    private ImageView mTv_back;
    private TextView mTv_username;
    // private EditText mEdt_username;
    private Button mBtn_done;
    private Button btn_bind_code;
    private Intent mIntent;
    DataBaseHelper db;

    String username = App.USERNAME;
    String phoneno = App.PHONE;
    String email = App.EMAIL;
    String qq = App.QQ;
    String weibo = App.WEIBO;
    String weixin = App.WEIXIN;

    EditText edt_unbind_email;
    EditText edt_unbind_email_code;
    EditText edt_unbind_no;
    EditText edt_unbind_code;
    EditText edt_email;
    EditText edt_email_code;
    EditText edt_phone_no;
    EditText edt_phone_code;
    Dialog bindphone;

    Dialog unBindemail;
    Dialog unBindphone;
    Dialog bindemail;

    @Override
    public void init() {
        setContentView(R.layout.activity_binduser);
        ShareSDK.initSDK(this);
        mTv_back = (ImageView) findViewById(R.id.bind_tv_back);
        mTv_username = (TextView) findViewById(R.id.bind_tv_username);
        mTv_phoneno = (TextView) findViewById(R.id.bind_tv_phone);
        mTv_email = (TextView) findViewById(R.id.bind_tv_email);
        mTv_qq = (TextView) findViewById(R.id.bind_tv_qq);
        mTv_wx = (TextView) findViewById(R.id.bind_tv_wx);
        mTv_wb = (TextView) findViewById(R.id.bind_tv_wb);
        mBtn_done = (Button) findViewById(R.id.bind_btn_bind);
        // goToDb();

        mTv_username.setOnClickListener(this);
        mTv_email.setOnClickListener(this);
        mTv_qq.setOnClickListener(this);
        mTv_phoneno.setOnClickListener(this);
        mTv_wx.setOnClickListener(this);
        mTv_wb.setOnClickListener(this);
        mTv_back.setOnClickListener(this);
        mBtn_done.setOnClickListener(this);

        if (TextUtils.isEmpty(App.USERNAME)) {
            mTv_username.setText("请输入用户名");
        } else {
            mTv_username.setText(username);
        }

        mTv_phoneno.setText(App.PHONE);
        mTv_email.setText(App.EMAIL);
        mTv_qq.setText(App.QQ);
        mTv_wx.setText(App.WEIXIN);
        mTv_wb.setText(App.WEIBO);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (TextUtils.isEmpty(App.USERNAME)) {
            mTv_username.setText("请输入用户名");
        } else {
            mTv_username.setText(App.USERNAME);
        }
    }

    private void goToDb() {
        if (db == null) {
            db = new DataBaseHelper(this);
        }
        SQLiteDatabase base = db.getReadableDatabase();
        Cursor c = base.rawQuery("select * from patient where userId=?",
                new String[]{String.valueOf(App.USERID)});
        if (c.moveToNext()) {

            phoneno = c.getString(8);
            email = c.getString(9);
            String qq = c.getString(12);
            String wx = c.getString(13);
            String wb = c.getString(14);

        }
        c.close();
        base.close();
        db.close();
    }

    @Override
    public void click(View v) {
        switch (v.getId()) {

            case R.id.bind_tv_back:
                finish();
                break;
            case R.id.bind_tv_username:
                startActivity(
                        new Intent(this, BindUsernameActivity.class));
                break;
            case R.id.bind_tv_phone:
                if (!TextUtils.isEmpty(phoneno)) {
                    unBindphone();
                } else {
                    bindPhone();
                }
                break;
            case R.id.bind_tv_email:
                if (!TextUtils.isEmpty(email)) {
                    unBindemail();
                } else {
                    bindEmail();
                }
                break;
            case R.id.bind_tv_qq:
                bindQQ();
                break;
            case R.id.bind_tv_wx:
                bindWeChat();
                break;
            case R.id.bind_tv_wb:
                bindWeibo();
                break;
            case R.id.bind_btn_bind:
                bind(App.USERNAME);
                break;
        }
    }

    private void unBindemail() {
        unBindemail = new Dialog(this);
        unBindemail.setCanceledOnTouchOutside(false);
        unBindemail.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View v = View.inflate(this, R.layout.dialog_unbind_email, null);
        unBindemail.setContentView(v);
        Window dialogWindow = unBindemail.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        WindowManager m = dialogWindow.getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        lp.height = (int) (d.getHeight() * 0.3); // 高度设置为屏幕的0.6
        lp.width = (int) (d.getWidth() * 0.95); // 宽度设置为屏幕的0.65
        dialogWindow.setAttributes(lp);
        unBindemail.show();
        edt_unbind_email = (EditText) v
                .findViewById(R.id.dialog_edt_unbind_email);
        edt_unbind_email_code = (EditText) v
                .findViewById(R.id.dialog_edt_unbind_ecode);
        btn_bind_code = (Button) v.findViewById(R.id.dialog_btn_bind_code);
        RegisterCodeTimerService.setHandler(mHandler);
        mIntent = new Intent(this, RegisterCodeTimerService.class);
        edt_unbind_email.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                if (TextUtils.isEmpty(arg0.toString())) {
                    btn_bind_code.setEnabled(false);
                } else {
                    btn_bind_code.setEnabled(true);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                if (TextUtils.isEmpty(arg0.toString())) {
                    btn_bind_code.setEnabled(false);
                } else {
                    btn_bind_code.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                if (TextUtils.isEmpty(arg0.toString())) {
                    btn_bind_code.setEnabled(false);
                } else {
                    btn_bind_code.setEnabled(true);
                }
            }
        });
        btn_bind_code.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(edt_unbind_email.getText().toString()
                        .trim())) {
                    mHandler.sendEmptyMessage(4);
                    return;
                }
                if (Check.isMail(edt_unbind_email.getText().toString())) {
                    MyProgress.show(BindUserActivity.this, R.layout.progressdialog_getcode);
                    unBindvalidate(edt_unbind_email.getText().toString().trim());
                }

            }
        });
        v.findViewById(R.id.dialog_btn_unbind_ebind).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (TextUtils.isEmpty(edt_unbind_email.getText()
                                .toString().trim())) {
                            mHandler.sendEmptyMessage(4);
                            return;
                        }
                        if (TextUtils.isEmpty(edt_unbind_email_code.getText()
                                .toString().trim())) {
                            mHandler.sendEmptyMessage(5);
                            return;
                        }
                        unbind(edt_unbind_email.getText().toString().trim());
                    }
                });
        v.findViewById(R.id.dialog_btn_unbind_ecancel).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        unBindemail.cancel();
                    }
                });

    }

    private void unBindphone() {
        unBindphone = new Dialog(this);
        unBindphone.setCanceledOnTouchOutside(false);
        unBindphone.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View v = View.inflate(this, R.layout.dialog_unbind_phoneno, null);
        unBindphone.setContentView(v);
        Window dialogWindow = unBindphone.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        WindowManager m = dialogWindow.getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        lp.height = (int) (d.getHeight() * 0.3); // 高度设置为屏幕的0.6
        lp.width = (int) (d.getWidth() * 0.95); // 宽度设置为屏幕的0.65
        dialogWindow.setAttributes(lp);
        unBindphone.show();

        edt_unbind_no = (EditText) v.findViewById(R.id.dialog_edt_unbind_phone);
        edt_unbind_code = (EditText) v
                .findViewById(R.id.dialog_edt_unbind_code);
        btn_bind_code = (Button) v.findViewById(R.id.dialog_btn_bind_code);
        RegisterCodeTimerService.setHandler(mHandler);
        mIntent = new Intent(this, RegisterCodeTimerService.class);
        edt_unbind_no.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                if (TextUtils.isEmpty(arg0.toString())) {
                    btn_bind_code.setEnabled(false);
                } else {
                    btn_bind_code.setEnabled(true);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                if (TextUtils.isEmpty(arg0.toString())) {
                    btn_bind_code.setEnabled(false);
                } else {
                    btn_bind_code.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                if (TextUtils.isEmpty(arg0.toString())) {
                    btn_bind_code.setEnabled(false);
                } else {
                    btn_bind_code.setEnabled(true);
                }
            }
        });
        btn_bind_code.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils
                        .isEmpty(edt_unbind_no.getText().toString().trim())) {
                    mHandler.sendEmptyMessage(4);
                    return;
                }

                if (Check.isMobile(edt_unbind_no.getText().toString())) {
                    MyProgress.show(BindUserActivity.this, R.layout.progressdialog_getcode);
                    unBindvalidate(edt_unbind_no.getText().toString().trim());
                }

            }
        });
        v.findViewById(R.id.dialog_btn_unbind_bind).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (TextUtils.isEmpty(edt_unbind_no.getText()
                                .toString().trim())) {
                            mHandler.sendEmptyMessage(4);
                            return;
                        }
                        if (TextUtils.isEmpty(edt_unbind_code.getText()
                                .toString().trim())) {
                            mHandler.sendEmptyMessage(5);
                            return;
                        }
                        unbind(edt_unbind_no.getText().toString().trim());
                    }
                });
        v.findViewById(R.id.dialog_btn_unbind_cancel).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        unBindphone.cancel();
                    }
                });
    }

    protected void unbind(final String str) {
        RequestQueue rq = App.getRequestQueue();
        StringBuilder url = null;
        JSONObject json = null;
        try {
            url = new StringBuilder(App.BASE + Constant.UNBIND);
            url.append("data=");
            json = new JSONObject();
            if (Check.isMobile(str)) {
                json.put("username", str);
                json.put("code", edt_unbind_code.getText().toString().trim());
            } else if (Check.isMail(str)) {
                json.put("username", str);
                json.put("code", edt_unbind_email_code.getText().toString()
                        .trim());
            } else {
                MyToast.show("输入有误");
            }
            url.append(json.toString());
            url.append("&tocken=" + App.TOKEN);
            // Log.wtf("aaaaaaa", url.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        rq.add(new StringRequest(Request.Method.GET, url.toString(),
                new Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject o = new JSONObject(response);// 整个JSONOject
                            String type = o.getString("code");
                            // Log.wtf("code", type);
                            if ("205".equals(type)) {// 解绑成功

                                if (Check.isMail(str)) {
                                    mHandler.sendEmptyMessage(7);
                                }
                                if (Check.isMobile(str)) {
                                    mHandler.sendEmptyMessage(6);
                                }
                            }
                            if ("107".equals(type)) {// 验证码错误
                                mHandler.sendEmptyMessage(-3);
                            }
                            if ("106".equals(type)) {// 验证码发送次数过多
                                mHandler.sendEmptyMessage(106);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            mHandler.sendEmptyMessage(108);
                        }
                    }
                }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mHandler.sendEmptyMessage(108);
            }
        }));
        rq.start();
    }

    protected void unBindvalidate(String str) {
        RequestQueue rq = App.getRequestQueue();
        StringBuilder code = null;

        code = new StringBuilder(App.BASE + Constant.UNBINDVALIDATE);
        code.append("data=");
        JSONObject jsonObject = new JSONObject();
        try {
            if (Check.isMobile(str)) {
                jsonObject.put("username", edt_unbind_no.getText());
                jsonObject.put("type", "mobilephone");

            } else {
                jsonObject.put("username", edt_unbind_email.getText());
                jsonObject.put("type", "email");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        code.append(jsonObject.toString());
        code.append("&tocken=" + App.TOKEN);
        // Log.wtf("change", code.toString());
        rq.add(new StringRequest(Request.Method.GET, code.toString(),
                new Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            String type = json.getString("code");

                            if ("205".equals(type)) {// 验证码发送成功
                                MyProgress.dismiss();
                                startService(mIntent);
                                mHandler.sendEmptyMessage(3);
                            }
                            if ("103".equals(type)) {// 帐号不存在
                                MyProgress.dismiss();
                                mHandler.sendEmptyMessage(103);
                            }
                            if ("106".equals(type)) {// 验证码发送次数过多
                                MyProgress.dismiss();
                                mHandler.sendEmptyMessage(106);
                            }

                        } catch (JSONException e) {
                            MyProgress.dismiss();
                            mHandler.sendEmptyMessage(108);
                            e.printStackTrace();
                        }
                    }
                }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // Log.wtf("reglog", error.toString());
                MyProgress.dismiss();
                mHandler.sendEmptyMessage(108);
            }
        }));
        rq.start();
    }



    private void bindWeibo() {
        final Platform weibo = ShareSDK.getPlatform(this, SinaWeibo.NAME);
        weibo.SSOSetting(false);
        weibo.setPlatformActionListener(new PlatformActionListener() {

            @Override
            public void onError(Platform arg0, int arg1, Throwable arg2) {

            }

            @Override
            public void onComplete(Platform arg0, int arg1,
                                   HashMap<String, Object> arg2) {
                RequestQueue rq = App.getRequestQueue();
                StringBuilder url = new StringBuilder(App.BASE
                        + Constant.CHANGE_THIRD);
                url.append("data=");
                JSONObject json = new JSONObject();

                final String openId = weibo.getDb().getUserId();
                final String nickname = weibo.getDb().get("nickname");
                try {
                    json.put("username", openId);
                    json.put("type", "weiBo");
                    json.put("nickName", Decoder.getEncoder(nickname));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                url.append(json.toString());
                url.append("&tocken=" + App.TOKEN);
                // Log.wtf("aaaaaa", url.toString());
                rq.add(new StringRequest(Request.Method.GET, url.toString(),
                        new Listener<String>() {

                            @Override
                            public void onResponse(String response) {

                                try {
                                    JSONObject json = new JSONObject(response);
                                    String code = json.getString("code");
                                    // Log.wtf("aaaaaa", response);
                                    if ("206".equals(code)) {// 绑定成功
                                        mHandler.sendEmptyMessage(-1);
                                        mTv_wb.setText(nickname);
                                        // saveWb2Db(nickname);
                                        App.WEIBO = nickname;
                                        mTv_wb.setText(nickname);
                                    }
                                    if ("103".equals(code)) {// 帐号已被绑定
                                        mHandler.sendEmptyMessage(-2);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    mHandler.sendEmptyMessage(108);
                                }
                            }
                        }, new ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mHandler.sendEmptyMessage(108);
                    }
                }));
                rq.start();
            }

            @Override
            public void onCancel(Platform arg0, int arg1) {

            }
        });
        weibo.authorize();
    }

    private void bindWeChat() {
        final Platform wechat = ShareSDK.getPlatform(this, Wechat.NAME);
        wechat.SSOSetting(false);
        wechat.setPlatformActionListener(new PlatformActionListener() {

            @Override
            public void onError(Platform arg0, int arg1, Throwable arg2) {

            }

            @Override
            public void onComplete(Platform arg0, int arg1,
                                   HashMap<String, Object> arg2) {
                RequestQueue rq = App.getRequestQueue();
                StringBuilder url = new StringBuilder(App.BASE
                        + Constant.CHANGE_THIRD);
                url.append("data=");
                JSONObject json = new JSONObject();

                final String openId = wechat.getDb().getUserId();
                final String nickname = wechat.getDb().get("nickname");
                try {
                    json.put("username", openId);
                    json.put("type", "weiChat");
                    json.put("nickName",Decoder.getEncoder(nickname));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                url.append(json.toString());
                url.append("&tocken=" + App.TOKEN);
                // Log.wtf("aaaaaa", url.toString());
                rq.add(new StringRequest(Request.Method.GET, url.toString(),
                        new Listener<String>() {

                            @Override
                            public void onResponse(String response) {

                                try {
                                    JSONObject json = new JSONObject(response);
                                    String code = json.getString("code");
                                    // Log.wtf("aaaaaa", response);
                                    if ("206".equals(code)) {// 绑定成功
                                        mHandler.sendEmptyMessage(-1);
                                        mTv_wx.setText(nickname);
                                        // saveWx2Db(nickname);
                                        App.WEIXIN = nickname;
                                        mTv_wx.setText(nickname);
                                    }
                                    if ("103".equals(code)) {// 帐号已被绑定
                                        mHandler.sendEmptyMessage(-2);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    mHandler.sendEmptyMessage(108);
                                }
                            }
                        }, new ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mHandler.sendEmptyMessage(108);
                    }
                }));
                rq.start();
            }

            @Override
            public void onCancel(Platform arg0, int arg1) {

            }
        });
        wechat.authorize();
    }

    private void bindQQ() {
        final Platform qq = ShareSDK.getPlatform(this, QZone.NAME);
        qq.SSOSetting(false);
        qq.setPlatformActionListener(new PlatformActionListener() {

            @Override
            public void onError(Platform arg0, int arg1, Throwable arg2) {

            }

            @Override
            public void onComplete(Platform arg0, int arg1,
                                   HashMap<String, Object> res) {

                RequestQueue rq = App.getRequestQueue();
                StringBuilder url = new StringBuilder(App.BASE
                        + Constant.CHANGE_THIRD);
                url.append("data=");
                JSONObject json = new JSONObject();

                final String openId = qq.getDb().getUserId();
                final String nickname = qq.getDb().get("nickname");
                try {
                    json.put("username", openId);
                    json.put("type", "QQ");
                    json.put("nickName", Decoder.getEncoder(nickname));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                url.append(json.toString());
                url.append("&tocken=" + App.TOKEN);
                // Log.wtf("aaaaaa", url.toString());
                rq.add(new StringRequest(Request.Method.GET, url.toString(),
                        new Listener<String>() {

                            @Override
                            public void onResponse(String response) {

                                try {
                                    JSONObject json = new JSONObject(response);
                                    String code = json.getString("code");
                                    // Log.wtf("aaaaaa", response);
                                    if ("206".equals(code)) {// 绑定成功
                                        mHandler.sendEmptyMessage(-1);
                                        mTv_qq.setText(nickname);
                                        // saveQQ2Db(nickname);
                                        App.QQ = nickname;
                                        mTv_qq.setText(nickname);
                                    }

                                    if ("103".equals(code)) {// 帐号已被绑定
                                        mHandler.sendEmptyMessage(-2);
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    mHandler.sendEmptyMessage(108);
                                }
                            }
                        }, new ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mHandler.sendEmptyMessage(108);
                    }
                }));
                rq.start();

            }

            @Override
            public void onCancel(Platform arg0, int arg1) {
            }
        });
        qq.authorize();
    }

    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case -3:
                    MyToast.show("验证码错误！");
                    break;
                case -2:
                    MyToast.show("帐号已被绑定！");

                    break;
                case -1:
                    MyToast.show("三方帐号绑定成功！");
                    break;
                case 0:
                    MyToast.show("绑定成功！");
                    if (Check.isMobile((String) msg.obj)) {
                        mTv_phoneno.setText((String) msg.obj);
                        bindphone.cancel();
                        btn_bind_code.setText("获取验证码");
                        btn_bind_code.setEnabled(true);
                        App.PHONE = (String) msg.obj;
                    } else if (Check.isMail((String) msg.obj)) {
                        mTv_email.setText((String) msg.obj);
                        bindemail.cancel();
                        btn_bind_code.setText("获取验证码");
                        btn_bind_code.setEnabled(true);
                        App.EMAIL = (String) msg.obj;
                    } else if (Check.CheckUsername((String) msg.obj)) {
                        mTv_username.setText((String) msg.obj);
                        App.USERNAME = (String) msg.obj;
                        BindUserActivity.this.finish();
                    }
                    // saveToDb((String) msg.obj);
                    break;
                case 3:
                    MyToast.show("验证码发送成功！");

                    break;
                case 4:
                    MyToast.show("请输入手机号/邮箱");
                    break;
                case 5:
                    MyToast.show("请输入验证码");
                    break;
                case 6:
                    MyToast.show("解绑成功");
                    unBindphone.cancel();
                    btn_bind_code.setText("获取验证码");
                    btn_bind_code.setEnabled(true);
                    bindPhone();
                    break;
                case 7:
                    MyToast.show("解绑成功");
                    unBindemail.cancel();
                    btn_bind_code.setText("获取验证码");
                    btn_bind_code.setEnabled(true);
                    bindEmail();
                    break;
                case 106:
                    MyToast.show("验证码发送次数过多");
                    btn_bind_code.setText("获取验证码");
                    btn_bind_code.setEnabled(true);
                    break;
                case 103:
                    MyToast.show("帐号不存在");
                    btn_bind_code.setText("获取验证码");
                    btn_bind_code.setEnabled(true);
                    break;
                case 108:
                    MyToast.show("网络连接错误");
                    break;
                case RegisterCodeTimer.IN_RUNNING:
                    btn_bind_code.setEnabled(false);
                    btn_bind_code.setText(msg.obj.toString());
                    break;
                case RegisterCodeTimer.END_RUNNING:
                    btn_bind_code.setEnabled(true);
                    btn_bind_code.setText(msg.obj.toString());
                    break;
                default:
                    break;
            }
        }

        ;
    };

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//
//        stopService(mIntent);
//    }

    private void bindEmail() {
        bindemail = new Dialog(this);
        bindemail.setCanceledOnTouchOutside(false);
        bindemail.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View v = View.inflate(this, R.layout.dialog_bind_email, null);
        bindemail.setContentView(v);
        Window dialogWindow = bindemail.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        WindowManager m = dialogWindow.getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        lp.height = (int) (d.getHeight() * 0.3); // 高度设置为屏幕的0.6
        lp.width = (int) (d.getWidth() * 0.95); // 宽度设置为屏幕的0.65
        dialogWindow.setAttributes(lp);

        bindemail.show();
        btn_bind_code = (Button) v.findViewById(R.id.dialog_btn_bind_code);
        RegisterCodeTimerService.setHandler(mHandler);
        mIntent = new Intent(this, RegisterCodeTimerService.class);

        edt_email = (EditText) v.findViewById(R.id.dialog_edt_bind_email);
        edt_email.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                if (TextUtils.isEmpty(arg0.toString())) {
                    btn_bind_code.setEnabled(false);
                } else {
                    btn_bind_code.setEnabled(true);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                if (TextUtils.isEmpty(arg0.toString())) {
                    btn_bind_code.setEnabled(false);
                } else {
                    btn_bind_code.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                if (TextUtils.isEmpty(arg0.toString())) {
                    btn_bind_code.setEnabled(false);
                } else {
                    btn_bind_code.setEnabled(true);
                }
            }
        });
        edt_email_code = (EditText) v.findViewById(R.id.dialog_edt_bind_ecode);

        btn_bind_code.setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (TextUtils.isEmpty(edt_email.getText().toString()
                                .trim())) {
                            mHandler.sendEmptyMessage(4);
                            return;
                        }
                        if (Check.isMail(edt_email.getText().toString())) {
                            MyProgress.show(BindUserActivity.this, R.layout.progressdialog_getcode);
                            validate(edt_email.getText().toString().trim());
                        }

                    }
                });
        v.findViewById(R.id.dialog_btn_bind_ebind).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (TextUtils.isEmpty(edt_email.getText().toString()
                                .trim())) {
                            mHandler.sendEmptyMessage(4);
                            return;
                        }
                        if (TextUtils.isEmpty(edt_email_code.getText()
                                .toString().trim())) {
                            mHandler.sendEmptyMessage(5);
                            return;
                        }
                        bind(edt_email.getText().toString().trim());
                    }
                });
        v.findViewById(R.id.dialog_btn_bind_ecancel).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        bindemail.cancel();
                    }
                });
    }

    protected void saveQQ2Db(String str) {
        if (db == null) {
            db = new DataBaseHelper(this);
        }
        SQLiteDatabase base = db.getWritableDatabase();
        ContentValues value = new ContentValues();
        // value.put("userId", App.USERID);
        value.put("qq", str);
        base.update("patient", value, "userId=?",
                new String[]{String.valueOf(App.USERID)});
        base.close();
        db.close();
    }

    protected void saveWx2Db(String str) {
        if (db == null) {
            db = new DataBaseHelper(this);
        }
        SQLiteDatabase base = db.getWritableDatabase();
        ContentValues value = new ContentValues();
        // value.put("userId", App.USERID);
        value.put("weixin", str);
        base.update("patient", value, "userId=?",
                new String[]{String.valueOf(App.USERID)});
        base.close();
        db.close();
    }

    protected void saveWb2Db(String str) {
        if (db == null) {
            db = new DataBaseHelper(this);
        }
        SQLiteDatabase base = db.getWritableDatabase();
        ContentValues value = new ContentValues();
        // value.put("userId", App.USERID);
        value.put("weibo", str);
        base.update("patient", value, "userId=?",
                new String[]{String.valueOf(App.USERID)});
        base.close();
        db.close();
    }

    private void bindPhone() {

        bindphone = new Dialog(this);
        bindphone.setCanceledOnTouchOutside(false);
        bindphone.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View v = View.inflate(this, R.layout.dialog_bind_phoneno, null);
        bindphone.setContentView(v);
        Window dialogWindow = bindphone.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        WindowManager m = dialogWindow.getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        lp.height = (int) (d.getHeight() * 0.3); // 高度设置为屏幕的0.6
        lp.width = (int) (d.getWidth() * 0.95); // 宽度设置为屏幕的0.65
        dialogWindow.setAttributes(lp);

        bindphone.show();
        edt_phone_no = (EditText) v.findViewById(R.id.dialog_edt_bind_phone);
        edt_phone_code = (EditText) v.findViewById(R.id.dialog_edt_bind_code);
        btn_bind_code = (Button) v.findViewById(R.id.dialog_btn_bind_code);
        RegisterCodeTimerService.setHandler(mHandler);
        mIntent = new Intent(this, RegisterCodeTimerService.class);

        edt_phone_no.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                if (TextUtils.isEmpty(arg0.toString())) {
                    btn_bind_code.setEnabled(false);
                } else {
                    btn_bind_code.setEnabled(true);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                if (TextUtils.isEmpty(arg0.toString())) {
                    btn_bind_code.setEnabled(false);
                } else {
                    btn_bind_code.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                if (TextUtils.isEmpty(arg0.toString())) {
                    btn_bind_code.setEnabled(false);
                } else {
                    btn_bind_code.setEnabled(true);
                }
            }
        });
        btn_bind_code.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(edt_phone_no.getText().toString().trim())) {
                    mHandler.sendEmptyMessage(4);
                    return;
                }
                if (Check.isMobile(edt_phone_no.getText().toString())) {
                    MyProgress.show(BindUserActivity.this, R.layout.progressdialog_getcode);
                    validate(edt_phone_no.getText().toString().trim());
                }

            }
        });
        v.findViewById(R.id.dialog_btn_bind_bind).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (TextUtils.isEmpty(edt_phone_no.getText().toString()
                                .trim())) {
                            mHandler.sendEmptyMessage(4);
                            return;
                        }
                        if (TextUtils.isEmpty(edt_phone_code.getText()
                                .toString().trim())) {
                            mHandler.sendEmptyMessage(5);
                            return;
                        }
                        bind(edt_phone_no.getText().toString().trim());
                    }
                });
        v.findViewById(R.id.dialog_btn_bind_cancel).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        bindphone.cancel();
                    }
                });

    }

    protected void saveToDb(final String str) {

        if (db == null) {
            db = new DataBaseHelper(this);
        }
        new Thread(new Runnable() {

            @Override
            public void run() {
                ContentValues value = new ContentValues();
                // value.put("userId", App.USERID);
                if (Check.isMobile(str)) {
                    value.put("phonenumber", str);
                } else if (Check.isMail(str)) {
                    value.put("email", str);
                } else {

                    value.put("username", str);
                    // Log.wtf("str", str);
                }
                db.getWritableDatabase().update("patient", value, "userId=?",
                        new String[]{String.valueOf(App.USERID)});

                db.close();
                if (bindphone != null) {
                    bindphone.cancel();
                }
                if (bindemail != null) {
                    bindemail.cancel();
                }
            }
        }).start();
    }

    protected void bind(final String str) {
        RequestQueue rq = App.getRequestQueue();
        StringBuilder url = null;
        JSONObject json = null;
        try {
            url = new StringBuilder(App.BASE + Constant.CHANGE);
            url.append("data=");
            json = new JSONObject();

            if (Check.isMail(str)) {
                json.put("username", str);
                json.put("code", edt_email_code.getText().toString().trim());
            } else if (Check.isMobile(str)) {
                json.put("username", str);
                json.put("code", edt_phone_code.getText().toString().trim());
            } else if (Check.CheckUsername(str)) {
                json.put("username", str);
                json.put("code", "none");
            } else {
                MyToast.show("输入有误");
                return ;
            }

            url.append(json.toString());
            url.append("&tocken=" + App.TOKEN);
            Log.wtf("aaaaaaa", url.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        rq.add(new StringRequest(Request.Method.GET, url.toString(),
                new Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject o = new JSONObject(response);// 整个JSONOject
                            String type = o.getString("code");
                            Log.wtf("code", type);
                            if ("206".equals(type)) {
                                Message msg = mHandler.obtainMessage(0);
                                msg.obj = str;
                                msg.sendToTarget();
                            }
                            if ("107".equals(type)) {// 验证码错误
                                mHandler.sendEmptyMessage(-3);
                            }
                            if ("103".equals(type)) {// 帐号已被绑定
                                mHandler.sendEmptyMessage(-2);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            mHandler.sendEmptyMessage(108);
                        }
                    }
                }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                mHandler.sendEmptyMessage(108);
            }
        }));
        rq.start();
    }

    /**
     * 验证
     */

    private void validate(String str) {
        RequestQueue rq = App.getRequestQueue();
        StringBuilder code = null;

        code = new StringBuilder(App.BASE + Constant.VALIDATE);
        code.append("data=");
        JSONObject jsonObject = new JSONObject();
        try {

            if (Check.isMobile(str)) {
                jsonObject.put("username", edt_phone_no.getText());
            }
            if (Check.isMail(str)) {
                jsonObject.put("username", edt_email.getText());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        code.append(jsonObject.toString());
        code.append("&tocken=" + App.TOKEN);
        Log.wtf("change", code.toString());
        rq.add(new StringRequest(Request.Method.GET, code.toString(),
                new Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject json = new JSONObject(response);
                            String type = json.getString("code");
                            Log.wtf("change", type.toString());
                            if ("205".equals(type)) {// 验证码发送成功
                                MyProgress.dismiss();
                                startService(mIntent);
                                mHandler.sendEmptyMessage(3);
                            }
                            if ("103".equals(type)) {// 帐号已被绑定
                                MyProgress.dismiss();
                                mHandler.sendEmptyMessage(-2);
                            }
                            if ("106".equals(type)) {// 验证码发送次数过多
                                MyProgress.dismiss();
                                mHandler.sendEmptyMessage(106);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            MyProgress.dismiss();
                            mHandler.sendEmptyMessage(108);
                        }
                    }
                }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // Log.wtf("reglog", error.toString());
                MyProgress.dismiss();
                mHandler.sendEmptyMessage(108);
            }
        }));
        rq.start();
    }

}
