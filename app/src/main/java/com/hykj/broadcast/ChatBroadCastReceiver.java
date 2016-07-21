package com.hykj.broadcast;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.text.TextUtils;
import android.util.Log;

public class ChatBroadCastReceiver extends BroadcastReceiver {

	public static ArrayList<EventHandler> mListeners = new ArrayList<EventHandler>();
	@Override
	public void onReceive(Context context, Intent intent) {
		
		if (TextUtils.equals( intent.getAction(), ConnectivityManager.CONNECTIVITY_ACTION)) {
			if (mListeners.size() > 0)// 通知接口完成加载
				for (EventHandler handler : mListeners) {
					handler.onNetChange();
				}
		}
	}
	
	public interface EventHandler {
		void onNetChange();
	}
}
