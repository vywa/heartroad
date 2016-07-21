package com.hykj.fragment.usermanagement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.hykj.App;
import com.hykj.R;
import com.hykj.activity.messure.AddBloodPressureActivity;
import com.hykj.activity.messure.BloodPressureLineActivity;
import com.hykj.activity.messure.BloodSugarLineActivity;
import com.hykj.activity.usermanagement.HealthRecordActivity;
import com.hykj.activity.usermanagement.MyDietActivity;
import com.hykj.activity.usermanagement.MyMedicalrecordActivity;
import com.hykj.activity.usermanagement.MyPrescriptionActivity;
import com.hykj.activity.usermanagement.MySportActivity;
import com.hykj.activity.usermanagement.QuestionnaireActivity;
import com.hykj.adapter.ImageAdapter;
import com.hykj.utils.MyLog;
import com.hykj.utils.MyProgress;
import com.hykj.utils.MyToast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 作者：赵宇
 * @version 1.0 创建时间：2015年10月19日 下午3:58:32 类说明：健康档案
 */
public class RecordFragment extends Fragment implements OnClickListener {
    private ImageButton mBtn_wdxy, mBtn_wdxt, mBtn_jqzb, mBtn_tjbg, mBtn_wdbl, mBtn_wdcf, mBtn_wdyd, mBtn_wdys, mBtn_wdsm;
    private ImageHandler imageHandler = new ImageHandler(new WeakReference<RecordFragment>(this));
    private ViewPager viewPager;
    private ArrayList<ImageView> imageLists = new ArrayList<ImageView>();
    private ViewGroup viewGroup;
    private ImageView[] imageDots;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_healthrecord, container, false);
        mBtn_wdxy = (ImageButton) v.findViewById(R.id.health_btn_wdxy);// 我的血压
        mBtn_wdxt = (ImageButton) v.findViewById(R.id.health_btn_wdxt);// 我的血糖
        mBtn_wdys = (ImageButton) v.findViewById(R.id.health_btn_wdys);// 我的饮食
        mBtn_wdyd = (ImageButton) v.findViewById(R.id.health_btn_wdyd);// 我的运动
        mBtn_jqzb = (ImageButton) v.findViewById(R.id.health_btn_jqzb);// 近期指标
        mBtn_wdbl = (ImageButton) v.findViewById(R.id.health_btn_wdbl);// 我的病历
        mBtn_wdcf = (ImageButton) v.findViewById(R.id.health_btn_wdcf);// 我的处方
        mBtn_wdxy.setOnClickListener(this);
        mBtn_wdxt.setOnClickListener(this);
        mBtn_wdys.setOnClickListener(this);
        mBtn_wdyd.setOnClickListener(this);
        mBtn_jqzb.setOnClickListener(this);
        mBtn_wdbl.setOnClickListener(this);
        mBtn_wdcf.setOnClickListener(this);

        viewPager = (ViewPager) v.findViewById(R.id.health_viewpager);
        viewGroup = (ViewGroup) v.findViewById(R.id.health_point_group);
        viewGroup.getBackground().setAlpha(200);
        ImageView image1 = new ImageView(getActivity());
        image1.setScaleType(ImageView.ScaleType.FIT_XY);
        image1.setImageResource(R.drawable.iv_hr_banner1);
        ImageView image2 = new ImageView(getActivity());
        image2.setScaleType(ImageView.ScaleType.FIT_XY);
        image2.setImageResource(R.drawable.iv_hr_banner2);
        ImageView image3 = new ImageView(getActivity());
        image3.setScaleType(ImageView.ScaleType.FIT_XY);
        image3.setImageResource(R.drawable.iv_hr_banner3);

        imageLists.add(image1);
        imageLists.add(image2);
        imageLists.add(image3);
        MyLog.wtf("imagelists", imageLists.size() + imageLists.toString());
        viewGroup.removeAllViews();
        imageDots = new ImageView[imageLists.size()];
        for (int i = 0; i < imageDots.length; i++) {
            imageDots[i] = new ImageView(getActivity());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(20, 20);
            params.gravity = Gravity.CENTER;
            params.setMargins(35, 0, 35, 0);

            imageDots[i].setLayoutParams(params);
            if (i == 0) {
                imageDots[i].setImageResource(R.drawable.banner_point1);
            } else {
                imageDots[i].setImageResource(R.drawable.banner_point2);
            }
            viewGroup.addView(imageDots[i]);
        }
        MyLog.wtf("test", imageDots.length + "===" + imageDots[1]);
        ImageAdapter imageAdapter = new ImageAdapter(imageLists);
        viewPager.setAdapter(imageAdapter);
        imageAdapter.notifyDataSetChanged();
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                imageHandler.sendMessage(Message.obtain(imageHandler, ImageHandler.MSG_PAGE_CHANGED, position, 0));
                int currentPosition = position % imageLists.size();
                for (int i = 0; i < imageLists.size(); i++) {
                    imageDots[currentPosition].setImageResource(R.drawable.banner_point1);

                    if (currentPosition != i) {
                        imageDots[i].setImageResource(R.drawable.banner_point2);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state) {
                    case ViewPager.SCROLL_STATE_DRAGGING:
                        imageHandler.sendEmptyMessage(ImageHandler.MSG_KEEP_SILENT);
                        break;
                    case ViewPager.SCROLL_STATE_IDLE:
                        imageHandler.sendEmptyMessageDelayed(ImageHandler.MSG_UPDATE_IMAGE, ImageHandler.MSG_DELAY);
                        break;
                    default:
                        break;
                }
            }
        });
        viewPager.setCurrentItem(Integer.MAX_VALUE / 2);//默认在中间，是用户看不到边界
        //开始轮播效果
        imageHandler.sendEmptyMessageDelayed(ImageHandler.MSG_BREAK_SILENT, ImageHandler.MSG_DELAY);
        return v;
    }

    private void getResult() {
        RequestQueue rq = App.getRequestQueue();
        rq.add(new StringRequest(Request.Method.GET, App.BASE + "diagnosis/result?tocken=" + App.TOKEN,

                new Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.wtf("bbbbb", App.BASE + "diagnosis/result?tocken=" + App.TOKEN);
                            JSONObject json = new JSONObject(response);
                            String code = json.optString("code");
//					Log.wtf("aaa", code);
                            if ("206".equals(code)) {// 成功
                                JSONObject all = json.optJSONObject("caseHistory");
//						Log.wtf("aaa", all.toString());
                                JSONObject o = all.optJSONObject("affiliatedClinicalDisease");// 伴临床疾患
                                Bundle b1 = new Bundle();
//						Log.wtf("aaa", o.toString());
                                if (o.getBoolean("cardiovascularDisease")) {
                                    b1.putString("cardiovascularDisease", "心血管病");
                                }
                                if (o.getBoolean("cerebralVascularDisease")) {
                                    b1.putString("cerebralVascularDisease", "脑血管病");
                                }
                                if (o.getBoolean("kidneyDisease")) {
                                    b1.putString("kidneyDisease", "肾脏疾病");
                                }
                                if (o.getBoolean("peripheralVascularDisease")) {
                                    b1.putString("peripheralVascularDisease", "外周血管病");
                                }
                                if (o.getBoolean("retinopathy")) {
                                    b1.putString("retinopathy", "视网膜病变");
                                }
                                if (o.getBoolean("diabetesMelliitus")) {
                                    b1.putString("diabetesMelliitus", "糖尿病");
                                }

                                JSONObject o1 = all.optJSONObject("targetOrganDamage");// 靶器官损伤
//						Log.wtf("aaa", o1.toString());
                                Bundle b2 = new Bundle();
                                if (o1.getBoolean("leftVentricularHypertrophy")) {
                                    b2.putString("leftVentricularHypertrophy", "左心室肥厚");
                                }
                                if (o1.getBoolean("neckArteries")) {
                                    b2.putString("neckArteries", "颈动脉内膜厚");
                                }
                                if (o1.getBoolean("ankleArteries")) {
                                    b2.putString("ankleArteries", "踝动脉脉搏波速度");
                                }
                                if (o1.getBoolean("limbArteries")) {
                                    b2.putString("limbArteries", "臂动脉血压指数");
                                }
                                if (o1.getBoolean("kidneyBall")) {
                                    b2.putString("kidneyBall", "肾小球滤过率降低");
                                }
                                if (o1.getBoolean("urineProtein")) {
                                    b2.putString("urineProtein", "微量蛋白尿");
                                }

                                JSONObject o2 = all.optJSONObject("riskFactor");// 危险因素
//						Log.wtf("aaa", o2.toString());
                                Bundle b3 = new Bundle();
                                if (o2.getBoolean("cigerate")) {
                                    b3.putString("cigerate", "吸烟");
                                }
                                if (o2.getBoolean("suggerEndure")) {
                                    b3.putString("suggerEndure", "糖耐量受损");
                                }
                                if (o2.getBoolean("bloodFatException")) {
                                    b3.putString("bloodFatException", "血脂异常");
                                }
                                if (o2.getBoolean("vesselherit")) {
                                    b3.putString("vesselherit", "早发心血管病家族史");
                                }
                                if (o2.getBoolean("fat")) {
                                    b3.putString("fat", "肥胖或腹型肥胖");
                                }
                                if (o2.getBoolean("hcy")) {
                                    b3.putString("hcy", "血同型半胱氨酸升高");
                                }
                                if (o2.getBoolean("hsCRP")) {
                                    b3.putString("hsCRP", "高敏c反应蛋白");
                                }
                                if (o2.getBoolean("physicalActivity")) {
                                    b3.putString("physicalActivity", "缺乏体力活动");
                                }
                                int hightBloodPressure = o2.getInt("hightBloodPressure");// 血压等级
                                int age = o2.getInt("age");// 年龄
                        /*
						 * if(hightBloodPressure==-1){
						 * mHandler.sendEmptyMessage(7); return; }
						 */

                                JSONObject o3 = all.optJSONObject("diagnosis");
                                JSONArray array = o3.optJSONArray("cotent");
                                String result = array.getString(0);// 结果

                                Message msg = mHandler.obtainMessage(5);
                                Bundle b = new Bundle();
                                b.putString("result", result);
                                b.putInt("hightBloodPressure", hightBloodPressure);
                                b.putBundle("affiliatedClinicalDisease", b1);
                                b.putBundle("targetOrganDamage", b2);
                                b.putBundle("riskFactor", b3);
                                msg.obj = b;
                                msg.sendToTarget();

                            } else if ("110".equals(code)) {// 错误
                                mHandler.sendEmptyMessage(6);
                            } else if ("111".equals(code)) {// 未测血压
                                mHandler.sendEmptyMessage(7);
                            } else if ("112".equals(code)) {// 未评估
                                mHandler.sendEmptyMessage(8);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.health_btn_wdxy:
                Intent bp_intent = new Intent(getActivity(), BloodPressureLineActivity.class);
                startActivity(bp_intent);
                break;
            case R.id.health_btn_wdxt:
                Intent bs_intent = new Intent(getActivity(), BloodSugarLineActivity.class);
                startActivity(bs_intent);
                break;
            case R.id.health_btn_wdys:
                Intent wdys_intent = new Intent(getActivity(), MyDietActivity.class);
                startActivity(wdys_intent);
                break;
            case R.id.health_btn_wdyd:
                Intent wdyd_intent = new Intent(getActivity(), MySportActivity.class);
                startActivity(wdyd_intent);
                break;
            case R.id.health_btn_jqzb:
                getResult();
                break;
            case R.id.health_btn_wdbl:
                Intent wdbl_intent = new Intent(getActivity(), MyMedicalrecordActivity.class);
                startActivity(wdbl_intent);
                break;
            case R.id.health_btn_wdcf:
                Intent wdcf_intent = new Intent(getActivity(), MyPrescriptionActivity.class);
                startActivity(wdcf_intent);
                break;
            default:
                break;
        }
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {

                case 5:
                    Bundle allResult = (Bundle) msg.obj;
                    MyProgress.dismiss();
                    Intent health_intent = new Intent(getActivity(), HealthRecordActivity.class);
                    health_intent.putExtra("result", allResult);
                    getActivity().startActivity(health_intent);
                    break;
                case 6:
                    MyProgress.dismiss();
                    MyToast.show("网络访问错误");
                    break;
                case 7:
                    MyProgress.dismiss();
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                    builder1.setTitle("您尚未录入血压").setMessage("请录入血压").setPositiveButton("确定", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getActivity(), AddBloodPressureActivity.class);
                            getActivity().startActivity(intent);
                        }
                    }).create().show();
                    break;
                case 8:// 未评估健康因素
                    MyProgress.dismiss();
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("您尚未评估健康因素").setMessage("请评估健康因素").setPositiveButton("确定", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getActivity(), QuestionnaireActivity.class);
                            getActivity().startActivity(intent);
                        }
                    }).create().show();
                    break;
                default:
                    break;
            }
        }
    };



    private static class ImageHandler extends Handler {
        /*
         * 请求更新显示的view
         */
        protected static final int MSG_UPDATE_IMAGE = 1;
        /*
         * 请求暂停轮播
         */
        protected static final int MSG_KEEP_SILENT = 2;
        /*
         * 请求回复轮播
         */
        protected static final int MSG_BREAK_SILENT = 3;
        /*
         * 记录最新的页号，当用户手动滑动时需要记录新页号，否则会使轮播的页面出错。
         * 例如当前如果在第一页，本来准备播放的是第二页，而这时候用户滑动到了末页，
         * 则应该播放的是第一页，如果继续按照原来的第二页播放，则逻辑上有问题。
         */
        protected static final int MSG_PAGE_CHANGED = 4;

        //轮播间隔时间
        protected static final long MSG_DELAY = 2000;
        //使用弱引用避免Handler泄露
        private WeakReference<RecordFragment> weakReference;
        private int currentItem = 0;

        protected ImageHandler(WeakReference<RecordFragment> weakReference) {
            this.weakReference = weakReference;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            RecordFragment fragment = weakReference.get();
            if (fragment == null) {
                return;
            }
            //检查消息队列并移除未发送的消息，这主要是避免在复杂环境下消息出现重复等问题
            if (fragment.imageHandler.hasMessages(MSG_UPDATE_IMAGE)) {
                fragment.imageHandler.removeMessages(MSG_UPDATE_IMAGE);
            }
            switch (msg.what) {
                case MSG_UPDATE_IMAGE:
                    currentItem++;
                    fragment.viewPager.setCurrentItem(currentItem);
                    //准备下次播放
                    fragment.imageHandler.sendEmptyMessageDelayed(MSG_UPDATE_IMAGE, MSG_DELAY);
                    break;
                case MSG_KEEP_SILENT:
                    //只要不发送消息就暂停
                    break;
                case MSG_BREAK_SILENT:
                    fragment.imageHandler.sendEmptyMessageDelayed(MSG_UPDATE_IMAGE, MSG_DELAY);
                    break;
                case MSG_PAGE_CHANGED:
                    //记录当前的页号，避免播放的时候页面显示不正确
                    currentItem = msg.arg1;
                    break;
                default:
                    break;
            }
        }
    }
}
