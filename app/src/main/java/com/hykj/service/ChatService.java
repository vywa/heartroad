package com.hykj.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;
import com.gc.materialdesign.widgets.Dialog;
import com.hykj.App;
import com.hykj.Constant;
import com.hykj.R;
import com.hykj.activity.chat.ChatActivity;
import com.hykj.activity.usermanagement.AcceptFriendActivity;
import com.hykj.activity.usermanagement.LoginActivity;
import com.hykj.broadcast.ChatBroadCastReceiver;
import com.hykj.broadcast.ChatBroadCastReceiver.EventHandler;
import com.hykj.entity.Doctor;
import com.hykj.utils.MyLog;
import com.hykj.utils.NetUtils;

public class ChatService extends Service implements EventHandler {

	public static final int CONNECTED = 0;
	public static final int DISCONNECTED = -1;
	public static final int CONNECTING = 1;
	public static final int TOLOGIN_PAGE = 2;
	public static final int CHANGE_SUCC = 3;
	public static final int CHANGE_FAILED = 4;
	public static final int REGISTSUCC = -2;
	public static final int REGISTFAILED = -3;
	public static final int CONFLICT = -4;

	private int mConnectedState = DISCONNECTED; // 是否已经连接

	private static final int RECONNECT_MAXIMUM = 10000;// 最大重连时间间隔

	private IBinder mBinder = new ChatBinder();

	private IConnectionStatusCallback mConnectionStatusCallback;
	private SmackImpl mSmackable;

	private ActivityManager mActivityManager;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case CONNECTED:
					mConnectedState = CONNECTED;
					if (mConnectionStatusCallback != null)
						mConnectionStatusCallback.connectionStatusChanged(mConnectedState, "");
					break;
				case DISCONNECTED:
					mConnectedState = DISCONNECTED;
					if (mConnectionStatusCallback != null) {
						mConnectionStatusCallback.connectionStatusChanged(mConnectedState, "");
					}

					break;
				case CONNECTING:
					mConnectedState = CONNECTING;
					if (mConnectionStatusCallback != null) {
						mConnectionStatusCallback.connectionStatusChanged(mConnectedState, "");
					}
					break;
				case REGISTSUCC:
					mConnectedState = REGISTSUCC;
					if (mConnectionStatusCallback != null) {
						mConnectionStatusCallback.connectionStatusChanged(mConnectedState, "");
					}
					break;
				case REGISTFAILED:
					mConnectedState = REGISTFAILED;
					if (mConnectionStatusCallback != null) {
						mConnectionStatusCallback.connectionStatusChanged(mConnectedState, "");
					}
					break;
				case TOLOGIN_PAGE:
					ChatService.this.logout();// 注销
					ChatService.this.stopSelf();// 停止服务
					App.finishAll();

					Intent intent = new Intent(App.getContext(), LoginActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
					break;
				case CHANGE_SUCC:
					mConnectedState = CHANGE_SUCC;
					if (mConnectionStatusCallback != null) {
						mConnectionStatusCallback.connectionStatusChanged(mConnectedState, "");
					}
					break;
				case CHANGE_FAILED:
					mConnectedState = CHANGE_FAILED;
					if (mConnectionStatusCallback != null) {
						mConnectionStatusCallback.connectionStatusChanged(mConnectedState, "");
					}
					break;
				case CONFLICT:
					mConnectedState = CONFLICT;
					if (mConnectionStatusCallback != null) {
						mConnectionStatusCallback.connectionStatusChanged(mConnectedState, "");
					}
					break;
				case 125:

					ChatService.this.notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
					ChatService.this.notification = new Notification();
					// 设置通知栏显示内容
					notification.icon = R.drawable.ic_launcher;
					notification.defaults = Notification.DEFAULT_SOUND;// 铃声提醒
					notification.flags |= Notification.FLAG_AUTO_CANCEL;

					Bundle bundle = (Bundle) msg.obj;
					App.doctor = (Doctor) bundle.getSerializable("doctor");
					String message = bundle.getString("s");

					if(TextUtils.isEmpty(message)){
						Intent addFriIntent = new Intent(ChatService.this, AcceptFriendActivity.class);
						addFriIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						notification.setLatestEventInfo(ChatService.this, App.doctor.getName(), "请求加为好友", PendingIntent.getActivity(ChatService.this, 0, addFriIntent, 0));
					}else{
						Intent chatIntent = new Intent(ChatService.this, ChatActivity.class);
						chatIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						notification.setLatestEventInfo(ChatService.this, App.doctor.getName(), message, PendingIntent.getActivity(ChatService.this, 0, chatIntent, 0));
					}

					// 发出通知
					notificationManager.notify(0, notification);

					break;
			}
		}
	};
	private Dialog dialog;

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	public class ChatBinder extends Binder {
		public ChatService getService() {
			return ChatService.this;
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		ChatBroadCastReceiver.mListeners.add(this);
		mActivityManager = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
	}

	public void changePassword(final String userName, final String pwd, final String newPWD) {
		if (!NetUtils.isConnected(App.getContext())) {
			handler.sendEmptyMessage(CHANGE_FAILED);
			return;
		}
		new Thread() {
			public void run() {
				if (mSmackable == null) {
					mSmackable = new SmackImpl(ChatService.this);
				}
				if (mSmackable.changePassword(userName, pwd, newPWD)) {
					handler.sendEmptyMessage(CHANGE_SUCC);
				} else {
					handler.sendEmptyMessage(CHANGE_FAILED);
				}
			}
		}.start();
	}

	/**
	 * 注册
	 *
	 * @param account
	 * @param password
	 * @return
	 */
	public void createAccount(final String account, final String password) {
		if (!NetUtils.isConnected(App.getContext())) {
			handler.sendEmptyMessage(REGISTFAILED);
			return;
		}
		new Thread() {
			public void run() {
				if (mSmackable == null) {
					mSmackable = new SmackImpl(ChatService.this);
				}
				if (mSmackable.createAccount(account, password)) {
					handler.sendEmptyMessage(REGISTSUCC);
				} else {
					handler.sendEmptyMessage(REGISTFAILED);
				}
			}
		}.start();
	}

	/**
	 * 登录
	 *
	 * @param account
	 * @param password
	 */
	public void Login(final String account, final String password) {
		if (!NetUtils.isConnected(App.getContext())) {
			handler.sendEmptyMessage(DISCONNECTED);
			return;
		}
		new Thread() {
			public void run() {
				try {
					handler.sendEmptyMessage(CONNECTING);
					mSmackable = new SmackImpl(ChatService.this);
					if (mSmackable.login(account, password)) {
						handler.sendEmptyMessage(CONNECTED);
					} else {
						handler.sendEmptyMessage(DISCONNECTED);
					}
				} catch (Exception e) {
					handler.sendEmptyMessage(DISCONNECTED);
					e.printStackTrace();
				}
			}
		}.start();
	}

	// 退出
	public boolean logout() {
		boolean isLogout = false;
		if (mSmackable != null) {
			isLogout = mSmackable.logout();
			mSmackable = null;
		}
		return isLogout;
	}

	/**
	 * 非UI线程连接失败反馈
	 *
	 * @param reason
	 */
	public void connectionFailed(final String reason) {// TODO
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				onNetChange();
			}
		}, 0, RECONNECT_MAXIMUM);

		Message message = Message.obtain();
		message.what = DISCONNECTED;
		message.obj = reason;
		handler.sendMessageDelayed(message, 1500);
	}

	public void conflict() {
		handler.sendEmptyMessage(CONFLICT);
	}

	// 是否连接上服务器
	public boolean isAuthenticated() {
		if (mSmackable != null) {
			return mSmackable.isAuthenticated();
		}
		return false;
	}

	// 发送消息
	public void sendMessage(final String user, final String message) {
		new Thread() {
			public void run() {
				if (mSmackable != null)
					mSmackable.sendMessage(user, message);
					// mSmackable.sendCustomMessage();
				else
					SmackImpl.sendOfflineMessage(getContentResolver(), user, message);
			}
		}.start();
	}

	// 联系人改变
	// TODO
	public void rosterChanged() {
		if (mSmackable == null)
			return;
		/*if (mSmackable != null && !mSmackable.isAuthenticated()) {
			connectionFailed("没有警告的断开连接");
		}*/
	}

	// 通知栏
	private NotificationManager notificationManager = null;
	private Notification notification = null;
	private Timer timer;

	// 收到加好友请求
	StringBuilder url;

	public void getFriendInfo(String from,String s) {

		if (from.contains("@")) {
			from = from.substring(0, from.indexOf("@"));
		}

		url = new StringBuilder(App.BASE + Constant.GET_PERSON_INFO);
		url.append("tocken=" + App.TOKEN);
		url.append("&data=");
		JSONObject object = new JSONObject();
		try {
			object.put("userId", from);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		url.append(object.toString());
		MyLog.wtf("用户列表请求url", url.toString());
		App.getRequestQueue().add(getreqFreshening(s));
	}

	StringRequest getreqFreshening(final String s) {
		return new StringRequest(Method.GET, url.toString(), new Listener<String>() {

			@Override
			public void onResponse(String res) {
				MyLog.wtf("用户列表返回结果", res);
				try {
					JSONObject jsonObject = new JSONObject(res);
					String code = jsonObject.optString("code", "");
					if ("206".equals(code)) {
						JSONObject object = jsonObject.optJSONObject("doctorInfo");
						Doctor doctor = new Doctor();
						doctor.setUserId(object.optInt("userId"));
						doctor.setSex(object.optString("sex"));
						doctor.setPrifile(object.optString("resume"));
						doctor.setName(object.optString("trueName"));
						doctor.setIconUrl(object.optString("iconUrl"));
						doctor.setHospitalName(object.optString("shortName"));
						doctor.setAge(object.optString("age"));
						doctor.setFriend(false);
						Message message = Message.obtain();
						message.what = 125;
						Bundle bundle = new Bundle();
						bundle.putSerializable("doctor",doctor);
						bundle.putString("s",s);
						message.obj = bundle;
						handler.sendMessage(message);
					} else {
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
			}
		});
	}


	public void delFriend(String id) {
		mSmackable.delFriendEntry(id);
	}

	// 接受加好友请求

	public void acceptFriend(String id) {
		mSmackable.acceptFriend(id);
		mSmackable.askUser(id);
	}

	// 拒绝加好友请求
	public void refuseFriend(final String id) {

		mSmackable.refuseFriend(id);
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				mSmackable.askDelFriend(id);
				mSmackable.delFriendEntry(id);
			}
		}, 1000);
	}

	public ArrayList<String> searchUsers(String user) {
		if (mSmackable == null)
			return null;
		if (mSmackable != null && !mSmackable.isAuthenticated()) {
			connectionFailed("");
			return null;
		}
		return mSmackable.searchUsers(user);
	}

	public boolean addUser(final String user) {

		if (mSmackable == null)
			return false;
		if (mSmackable != null && !mSmackable.isAuthenticated()) {
			connectionFailed("");
			return false;
		}

		return mSmackable.askUser(user);
	}

	// 收到新消息
	public void newMessage(final String from, final String message) {
		handler.post(new Runnable() {
			public void run() {
				// MediaPlayer.create(ChatService.this,R.raw.em_outgoing).start();
				if (!isAppOnForeground()) {
//					notifyClient(from, mSmackable.getNameForJID(from), message);
					getFriendInfo(from,message);
				}
			}
		});
	}

	public boolean isAppOnForeground() {
		List<RunningTaskInfo> taskInfos = mActivityManager.getRunningTasks(1);
		return taskInfos.size() > 0 && TextUtils.equals(getPackageName(), taskInfos.get(0).topActivity.getPackageName());
	}

	public void closeAll() {
		handler.sendEmptyMessage(TOLOGIN_PAGE);
	}

	public void registerConnectionStatusCallback(IConnectionStatusCallback cb) {
		mConnectionStatusCallback = cb;
	}

	public void unRegisterConnectionStatusCallback() {
		mConnectionStatusCallback = null;
	}

	@Override
	public void onNetChange() {
		if (isAuthenticated()) {
			// 如果已经连接上，直接返回
			return;
		}
		// TODO 重连
		if (NetUtils.isConnected(App.getContext()) && SmackImpl.needRecon) {
			if (timer != null) {
				timer.cancel();
			}
			Login(App.USERID + "", App.tempPwd);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		ChatBroadCastReceiver.mListeners.remove(this);
		logout();
	}
}
