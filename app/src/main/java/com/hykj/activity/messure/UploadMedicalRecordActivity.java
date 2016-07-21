package com.hykj.activity.messure;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
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
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hykj.App;
import com.hykj.Constant;
import com.hykj.R;
import com.hykj.activity.BaseActivity;
import com.hykj.activity.subject.NewSubjectPicActivity;
import com.hykj.entity.MyDate;
import com.hykj.utils.MyToast;
import com.hykj.utils.OnUploadStateListener;
import com.hykj.utils.ScreenUtils;
import com.hykj.utils.XHttpUtils;
import com.hykj.view.SetDietTimeView;
import com.hykj.view.SetUploadmrTimeView;
import com.hykj.view.spinnerwheel.AbstractWheel;
import com.hykj.view.spinnerwheel.OnWheelChangedListener;
import com.hykj.view.spinnerwheel.WheelHorizontalView;
import com.hykj.view.spinnerwheel.WheelVerticalView;
import com.hykj.view.spinnerwheel.adapters.ArrayWheelAdapter;
import com.lidroid.xutils.exception.HttpException;

/**
 * @author 作者 : zhaoyu
 * @version 创建时间：2016年4月15日 上午11:14:33 类说明
 */
public class UploadMedicalRecordActivity extends BaseActivity {
    private ImageView mImg_back, mImg_takephone;
    private TextView mTv_medical, mTv_form, mTv_report, mTv_other;
    private Button mBtn_save;
    public TextView mTv_time;

    private EditText mEdt_describe;

    private int uploadCount = 0;// 上传的数量
    private int uploadSuccessCount = 0;// 上传成功的数量
    private ArrayList<String> imagePath = new ArrayList<String>();

    @Override
    public void init() {
        setContentView(R.layout.activity_uploadmedicalrecord);

        initViews();

    }


    private void initViews() {
        mImg_back = (ImageView) findViewById(R.id.uploadmedical_imgv_back);
        mImg_takephone = (ImageView) findViewById(R.id.uploadmedical_img_takephone);
        mImg_back.setOnClickListener(this);
        mImg_takephone.setOnClickListener(this);
        mTv_medical = (TextView) findViewById(R.id.uploadmedical_tv_medicalrecord);
        mTv_form = (TextView) findViewById(R.id.uploadmedical_tv_form);
        mTv_report = (TextView) findViewById(R.id.uploadmedical_tv_report);
        mTv_other = (TextView) findViewById(R.id.uploadmedical_tv_other);
        mTv_medical.setOnClickListener(this);
        mTv_form.setOnClickListener(this);
        mTv_report.setOnClickListener(this);
        mTv_other.setOnClickListener(this);
        mBtn_save = (Button) findViewById(R.id.uploadmedical_btn_save);
        mBtn_save.setOnClickListener(this);
        mTv_medical.setSelected(true);
        mEdt_describe = (EditText) findViewById(R.id.uploadmedical_edit_about);
        mTv_time = (TextView) findViewById(R.id.uploadmedical_tv_time);
        mTv_time.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        imagePath = (ArrayList<String>) data.getSerializableExtra("imagePath");

    }

    @Override
    public void click(View v) {
        switch (v.getId()) {
            case R.id.uploadmedical_imgv_back:
                finish();
                break;
            case R.id.uploadmedical_img_takephone:
                Intent intentPic = new Intent(this, NewSubjectPicActivity.class);
                intentPic.putExtra("imagePath", imagePath);
                startActivityForResult(intentPic, 0);
                break;
            case R.id.uploadmedical_tv_medicalrecord:
                mTv_medical.setBackgroundResource(R.drawable.uploadmr_bg);
                mTv_form.setBackgroundResource(R.drawable.shape_corner_rectangle);
                mTv_report
                        .setBackgroundResource(R.drawable.shape_corner_rectangle);
                mTv_other
                        .setBackgroundResource(R.drawable.shape_corner_rectangle1);

                mTv_medical.setSelected(true);
                mTv_form.setSelected(false);
                mTv_report.setSelected(false);
                mTv_other.setSelected(false);
                break;
            case R.id.uploadmedical_tv_form:
                mTv_medical
                        .setBackgroundResource(R.drawable.shape_corner_rectangle);
                mTv_form.setBackgroundResource(R.drawable.uploadmr_bg);
                mTv_report
                        .setBackgroundResource(R.drawable.shape_corner_rectangle);
                mTv_other
                        .setBackgroundResource(R.drawable.shape_corner_rectangle1);

                mTv_medical.setSelected(false);
                mTv_form.setSelected(true);
                mTv_report.setSelected(false);
                mTv_other.setSelected(false);
                break;
            case R.id.uploadmedical_tv_report:
                mTv_medical
                        .setBackgroundResource(R.drawable.shape_corner_rectangle);
                mTv_form.setBackgroundResource(R.drawable.shape_corner_rectangle);
                mTv_report.setBackgroundResource(R.drawable.uploadmr_bg);
                mTv_other
                        .setBackgroundResource(R.drawable.shape_corner_rectangle1);

                mTv_medical.setSelected(false);
                mTv_form.setSelected(false);
                mTv_report.setSelected(true);
                mTv_other.setSelected(false);
                break;
            case R.id.uploadmedical_tv_other:
                mTv_medical
                        .setBackgroundResource(R.drawable.shape_corner_rectangle);
                mTv_form.setBackgroundResource(R.drawable.shape_corner_rectangle);
                mTv_report
                        .setBackgroundResource(R.drawable.shape_corner_rectangle);
                mTv_other.setBackgroundResource(R.drawable.uploadmr_bg);

                mTv_medical.setSelected(false);
                mTv_form.setSelected(false);
                mTv_report.setSelected(false);
                mTv_other.setSelected(true);
                break;
            case R.id.uploadmedical_btn_save:
                uploadImage();
                break;
            case R.id.uploadmedical_tv_time:
                SetUploadmrTimeView view = new SetUploadmrTimeView(this, this);
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

    private void uploadImage() {
        if (imagePath.size() != 0) {
            for (int i = 0; i < imagePath.size(); i++) {
                upload(imagePath.get(i));
                uploadCount++;
            }
        } else {
            MyToast.show("请上传病历/处方单/检验报告");
        }
    }

    private List<String> imgUrls = new ArrayList<String>();// 存放图片url

    private void upload(String uploadPath) {
        com.lidroid.xutils.http.RequestParams requestParams = new com.lidroid.xutils.http.RequestParams();
        requestParams.addBodyParameter("media", new File(uploadPath));
        requestParams.addBodyParameter("tocken", App.TOKEN);
//		Log.wtf("xxxxxxxxxxx", App.BASE + Constant.UPLOAD_FILE);
        XHttpUtils.getInstance().upload(App.BASE + Constant.UPLOAD_FILE,
                requestParams, new OnUploadStateListener() {
                    @Override
                    public void onUploadSuccess(String result) {
                        try {
                            Log.wtf("上传文件返回结果", result);
                            JSONObject json = new JSONObject(result);
                            int responseCode = json.getInt("code");
                            if (responseCode == 206) {
                                imgUrls.add(json.getString("fileUrl"));
                                mHandler.sendEmptyMessage(3);
                            } else {
                                mHandler.sendEmptyMessage(4);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            mHandler.sendEmptyMessage(4);
                        }
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {

                        mHandler.sendEmptyMessage(4);
                    }
                });
    }

    private void save2Server() {
        RequestQueue rq = App.getRequestQueue();
        StringBuilder url = new StringBuilder(App.BASE + Constant.MEDICALRECORD
                + "data=");
        JSONObject json = new JSONObject();
        try {
            if (mTv_medical.isSelected()) {
                json.put("type", getEncoder("病历"));
            }
            if (mTv_form.isSelected()) {
                json.put("type", getEncoder("处方单"));
            }
            if (mTv_report.isSelected()) {
                json.put("type", getEncoder("检验报告"));
            }
            if (mTv_other.isSelected()) {
                json.put("type", getEncoder("其他"));
            }
            if (TextUtils.isEmpty(mEdt_describe.getText())) {
                mHandler.sendEmptyMessage(1);
                return;
            } else {
                json.put("content", getEncoder(mEdt_describe.getText()
                        .toString().trim()));
            }
            if (TextUtils.isEmpty(mTv_time.getText())) {
                mHandler.sendEmptyMessage(5);
                return;
            } else {
                json.put(
                        "recordTime", getEncoder(mTv_time.getText().toString()));
            }

            if (imgUrls.size() > 0) {
                JSONArray array = new JSONArray();
                for (int i = 0; i < imgUrls.size(); i++) {
                    array.put(i, imgUrls.get(i));
                }
                json.put("imageList", array);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        url.append(json.toString());
        url.append("&tocken=" + App.TOKEN);
        Log.wtf("病历url", url.toString());
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
                    MyToast.show("请填写病历说明");
                    break;
                case 2:
                    MyToast.show("保存失败");
                    break;
                case 3:
                    uploadSuccessCount++;
                    if (uploadSuccessCount == uploadCount) {
                        save2Server();
                    }
                    break;
                case 4:
                    MyToast.show("上传失败");
                    break;
                case 5:
                    MyToast.show("请设置时间");
                    break;
                default:
                    break;
            }
        }
    };


}
