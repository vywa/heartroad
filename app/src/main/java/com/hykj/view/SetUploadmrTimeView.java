package com.hykj.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.hykj.R;
import com.hykj.activity.messure.DietConditionActivity;
import com.hykj.activity.messure.UploadMedicalRecordActivity;
import com.hykj.entity.MyDate;
import com.hykj.view.spinnerwheel.AbstractWheel;
import com.hykj.view.spinnerwheel.OnWheelChangedListener;
import com.hykj.view.spinnerwheel.WheelHorizontalView;
import com.hykj.view.spinnerwheel.WheelVerticalView;
import com.hykj.view.spinnerwheel.adapters.ArrayWheelAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by zhaoyu on 16-5-26.
 */
public class SetUploadmrTimeView extends Dialog {

    Context context;
    UploadMedicalRecordActivity activity;
    private WheelHorizontalView whv_day;
    private TextView tv_month;
    private TextView tv_between;
    private WheelVerticalView mWv_hour, mWv_min;
    ArrayList<MyDate> myDates = new ArrayList<MyDate>();
    String[] days = new String[100];
    int month;
    int day;
    int year;

    private String[] hours, minutes;

    public SetUploadmrTimeView(Context context, UploadMedicalRecordActivity activity) {
        super(context);
        this.context = context;
        this.activity = activity;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.dialog_uploadmr);


        initViews();
        initDatas();
        registAndSettings();

        this.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                activity.mTv_time.setText(tv_month.getText().toString()
                        + myDates.get(whv_day.getCurrentItem())
                        .getDay() + "日" + "  "
                        + mWv_hour.getCurrentItem() + ":" + mWv_min.getCurrentItem());
            }
        });
    }


    private void registAndSettings() {


        ArrayWheelAdapter<String> dayAdapter = new ArrayWheelAdapter<String>(
                context, days);
        dayAdapter.setItemResource(R.layout.whell_bsday);
        dayAdapter.setItemTextResource(R.id.text);
        whv_day.setViewAdapter(dayAdapter);
        whv_day.setCurrentItem(days.length - 1);

        whv_day.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(AbstractWheel wheel, int oldValue,
                                  int newValue) {
                MyDate myDate = myDates.get(newValue);
                String between = myDate.getBetween();
                if ("0".equals(between)) {
                    tv_between.setText("今天");
                } else {
                    tv_between.setText(between + "天前");
                }
                String month = myDate.getMonth();
                String year = myDate.getYear();
                tv_month.setText(year + "年" + month + "月");
            }
        });
    }

    private void initDatas() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        for (int i = 99; i >= 0; i--) {
            calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.add(Calendar.DAY_OF_YEAR, -i);

            MyDate myDate = new MyDate();
            myDate.setBetween(i + "");
            myDate.setDay(calendar.get(Calendar.DAY_OF_MONTH) + "");
            myDate.setMonth(calendar.get(Calendar.MONTH) + 1 + "");
            myDate.setYear(calendar.get(Calendar.YEAR) + "");
            myDate.setWeek(calendar.get(Calendar.DAY_OF_WEEK) + "");
            myDate.setTime(calendar.getTimeInMillis());

            myDates.add(myDate);
        }
        for (int i = 0; i < myDates.size(); i++) {
            StringBuilder builder = new StringBuilder(myDates.get(i).getDay());
            builder.append("\n");
            switch (Integer.parseInt(myDates.get(i).getWeek())) {
                case 1:
                    builder.append("日");
                    break;
                case 2:
                    builder.append("一");
                    break;
                case 3:
                    builder.append("二");
                    break;
                case 4:
                    builder.append("三");
                    break;
                case 5:
                    builder.append("四");
                    break;
                case 6:
                    builder.append("五");
                    break;
                case 7:
                    builder.append("六");
                    break;
            }
            days[i] = builder.toString();
        }

        hours = new String[24];
        for (int i = 0; i < 24; i++) {
            hours[i] = i + "";
        }
        mWv_hour.setVisibleItems(2);
        ArrayWheelAdapter<String> hour_adapter = new ArrayWheelAdapter<String>(
                context, hours);
        hour_adapter.setItemResource(R.layout.choose_diettime_wheel);
        hour_adapter.setItemTextResource(R.id.text_diettime);
        mWv_hour.setViewAdapter(hour_adapter);
        mWv_hour.setCurrentItem(8);

        minutes = new String[60];
        for (int i = 0; i < 60; i++) {
            if (i < 10) {
                minutes[i] = "0" + i;
            } else {
                minutes[i] = i + "";
            }
        }
        mWv_min.setVisibleItems(2);
        ArrayWheelAdapter<String> min_adapter = new ArrayWheelAdapter<String>(
                context, minutes);
        min_adapter.setItemResource(R.layout.choose_diettime_wheel);
        min_adapter.setItemTextResource(R.id.text_diettime);
        mWv_min.setViewAdapter(min_adapter);
        mWv_min.setCurrentItem(10);
        MyDate myDate = myDates.get(myDates.size() - 1);
        String month = myDate.getMonth();
        String year = myDate.getYear();
        tv_month.setText(year + "年" + month + "月");
    }

    private void initViews() {
        mWv_hour = (WheelVerticalView) findViewById(R.id.dialog_uploadmr_wvv_hour);
        mWv_min = (WheelVerticalView) findViewById(R.id.dialog_uploadmr_wvv_min);
        whv_day = (WheelHorizontalView) findViewById(R.id.dialog_uploadmr_wv_day);
        tv_month = (TextView) findViewById(R.id.dialog_uploadmr_tv_month);
        tv_between = (TextView) findViewById(R.id.dialog_uploadmr_tv_between);

    }


}
