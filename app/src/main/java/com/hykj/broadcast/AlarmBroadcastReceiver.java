package com.hykj.broadcast;

import java.io.IOException;
import java.util.Calendar;
import java.util.TimeZone;

import com.hykj.R;
import com.hykj.activity.usermanagement.AlarmRemindActivity;
import com.hykj.activity.usermanagement.MainActivity;
import com.hykj.utils.TimeUtil;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * @author 作者 : zhaoyu
 * @version 创建时间：2016年4月1日 下午4:27:05 类说明
 */
public class AlarmBroadcastReceiver extends BroadcastReceiver {
    private NotificationManager manager;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.wtf("aaa", TimeUtil.getTime(System.currentTimeMillis()));
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        //calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        String week = String.valueOf(calendar.get(Calendar.DAY_OF_WEEK));
        if ("1".equals(week)) {
            week = "天";
        } else if ("2".equals(week)) {
            week = "一";
        } else if ("3".equals(week)) {
            week = "二";
        } else if ("4".equals(week)) {
            week = "三";
        } else if ("5".equals(week)) {
            week = "四";
        } else if ("6".equals(week)) {
            week = "五";
        } else if ("7".equals(week)) {
            week = "六";
        }
        String[] types = intent.getType().split(":");
        String type = types[0];
        String contents = intent.getAction();
        if (types[1].contains(week)) {
            final MediaPlayer mp = MediaPlayer.create(context, RingtoneManager
                    .getActualDefaultRingtoneUri(context,
                            RingtoneManager.TYPE_RINGTONE));
            mp.setLooping(false);

            try {
                mp.prepare();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mp.start();


            manager = (NotificationManager) context
                    .getSystemService(android.content.Context.NOTIFICATION_SERVICE);
            Intent intent1 = new Intent(context, AlarmRemindActivity.class);
            intent1.setType(type);
            intent1.setAction(contents);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                    intent1, PendingIntent.FLAG_CANCEL_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(
                    context);
            builder.setContentTitle(type)
                    .setContentText(contents)
                    .setSmallIcon(R.drawable.icon_account)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setSound(
                            RingtoneManager.getActualDefaultRingtoneUri(context,
                                    RingtoneManager.TYPE_RINGTONE));
            manager.notify(1, builder.build());
            new Thread(new Runnable() {

                @Override
                public void run() {
                    SystemClock.sleep(10000);
                    mp.stop();
                }
            }).start();
        }
    }


}