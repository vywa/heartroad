package com.hykj.adapter;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.SystemClock;
import android.util.Log;
import android.util.TimeUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hykj.App;
import com.hykj.R;
import com.hykj.broadcast.AlarmBroadcastReceiver;
import com.hykj.db.DataBaseHelper;
import com.hykj.entity.Remind;
import com.hykj.utils.TimeUtil;

/**
 * @author 作者 : zhaoyu
 * @version 创建时间：2016年3月30日 下午7:35:25 类说明
 */
public class RemindAdapter extends BaseAdapter {
    private Context mContext;
    private List<Remind> datas;
    private LayoutInflater mInflater;

    public RemindAdapter(Context mContext, List<Remind> datas) {
        super();
        this.mContext = mContext;
        this.datas = datas;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    ViewHolder vh = null;

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_lv_medicineremind,
                    null);
            vh = new ViewHolder();
            vh.iv_clock = (ImageView) convertView
                    .findViewById(R.id.remind_img_clock);
            vh.iv_title = (ImageView) convertView
                    .findViewById(R.id.remind_img_title);
            vh.tv_contents = (TextView) convertView
                    .findViewById(R.id.remind_tv_contents);
            vh.tv_time = (TextView) convertView
                    .findViewById(R.id.remind_tv_time);
            vh.tv_title = (TextView) convertView
                    .findViewById(R.id.remind_tv_title);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        if (datas.get(position).getIsLocked().equals("true")) {
            vh.iv_clock.setImageResource(R.drawable.switch_left);
        } else {
            vh.iv_clock.setImageResource(R.drawable.switch_right);
        }

        vh.iv_clock.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (datas.get(position).getIsLocked().equals("true")) {
                    vh.iv_clock.setImageResource(R.drawable.switch_right);
                    datas.get(position).setIsLocked("false");
                    setLocked("false", position);
                    // 关闹钟
                    closeClock();
                } else {
                    vh.iv_clock.setImageResource(R.drawable.switch_left);
                    datas.get(position).setIsLocked("true");
                    setLocked("true", position);
                    // 开闹钟
                    openClock();
                }
                notifyDataSetChanged();
            }

            int interval = 24 * 60 * 60 * 1000;

            private void openClock() {
                Intent intent = new Intent(mContext,
                        AlarmBroadcastReceiver.class);
                intent.setType(datas.get(position).getType() + ":" + datas.get(position).getRepeat());
                intent.setAction(datas.get(position).getContents());


                PendingIntent sender = PendingIntent.getBroadcast(mContext, position,
                        intent, 0);
                AlarmManager am = (AlarmManager) mContext
                        .getSystemService(Context.ALARM_SERVICE);

                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
                String[] times = datas.get(position).getTime().split(":");
                //calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
                calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(times[0]));
                calendar.set(Calendar.MINUTE, Integer.parseInt(times[1]));
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);


                if (System.currentTimeMillis() > calendar.getTimeInMillis()) {
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                }

                am.setRepeating(AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(), interval, sender);
                Log.wtf("aaa",
                        TimeUtil.getTime(calendar.getTimeInMillis())
                                + "----tian ");

                Log.wtf("aaa", TimeUtil.getTime(System.currentTimeMillis()));


            }

            private void closeClock() {
                Intent intent = new Intent(mContext,
                        AlarmBroadcastReceiver.class);

                PendingIntent sender = PendingIntent.getBroadcast(mContext, position,
                        intent, 0);
                AlarmManager am = (AlarmManager) mContext
                        .getSystemService(Context.ALARM_SERVICE);
                am.cancel(sender);
            }
        });
        if (datas.get(position).getType().equals("用药提醒")) {
            vh.iv_title.setImageResource(R.drawable.takemedicine);
            vh.tv_title.setTextColor(Color.parseColor("#77dbf8"));
        }
        if (datas.get(position).getType().equals("血压提醒")) {
            vh.iv_title.setImageResource(R.drawable.xueya);
            vh.tv_title.setTextColor(Color.parseColor("#fd91c8"));
        }
        if (datas.get(position).getType().equals("血糖提醒")) {
            vh.iv_title.setImageResource(R.drawable.xuetang);
            vh.tv_title.setTextColor(Color.parseColor("#fdbe58"));
        }
        if (datas.get(position).getType().equals("运动提醒")) {
            vh.iv_title.setImageResource(R.drawable.sport);
            vh.tv_title.setTextColor(Color.parseColor("#dbc0fe"));
        }
        vh.tv_title.setText(datas.get(position).getType());
        vh.tv_time.setText(datas.get(position).getRepeat()
                + datas.get(position).getTime());
        vh.tv_contents.setText(datas.get(position).getContents());
        return convertView;

    }

    DataBaseHelper db;

    protected void setLocked(String str, int position) {
        if (db == null) {
            db = new DataBaseHelper(mContext);
        }
        ContentValues values = new ContentValues();
        values.put("islocked", str);
        db.getWritableDatabase().update("remind", values, "contents=?",
                new String[]{datas.get(position).getContents()});
        db.close();
    }

    public static class ViewHolder {
        public ImageView iv_title, iv_clock;
        public TextView tv_title, tv_contents, tv_time;
    }
}
