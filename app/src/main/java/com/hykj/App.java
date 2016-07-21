package com.hykj;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Stack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.hykj.entity.Doctor;
import com.hykj.utils.FileManager;

public class App extends Application {// implements UncaughtExceptionHandler

	private static Context mContext;
	public static RequestQueue mQueue;

	// 1,管理员-------2,医生-------3,病人-------4,游客

	public static final int PATIENT_DOMAIN = 3;
	public static final int GUEST_DOMAIN = 4;

	public static String TOKEN = "";

	public static boolean NEEDGET_BS = true;
	public static boolean NEEDGET_BP = true;

	public static int USERID = 0; // 用户唯一ID

	public static String tempUserName = "";
	public static String tempPwd = "123456";
	public static String DEFAULT_PWD = "123456";

	public static String AGE = "";
	public static String BIRTHDAY = "";
	public static String SEX = "";
	public static String USERNAME = "";
	public static String TRUENAME = "";
	public static String EMAIL = "";
	public static String PHONE = "";
	public static String HEIGHT = "";
	public static String WEIGHT = "";
	public static String ADDRESS = "";

	public static String QQ = "";
	public static String WEIXIN = "";
	public static String WEIBO = "";

	public static String BASE = "http://192.168.1.201/healthcloudserver/";
	public static String OPENFIREURL = "192.168.1.201";
	public static String OPENFIRE_SERVICE_NAME = "@bob-optiplex-3020";
	public static String DEFULT_PHOTO = BASE + "info/download?iconUrl=200000010temp.jpg";
	public static String HEAD_IMAGE_URL = BASE + "info/download?iconUrl=200000010temp.jpg";

//	public static String BASE = "http://123.56.200.202/healthcloudserver/";
//	public static String OPENFIREURL = "123.56.200.202";
//	public static String OPENFIRE_SERVICE_NAME = "@iz25ibbhvflz";
//	public static String DEFULT_PHOTO = "http://123.56.200.202/download/index.jpg";
//	public static String HEAD_IMAGE_URL = "http://123.56.200.202/download/index.jpg";

	public static Doctor doctor;

	// 用于存放倒计时时间
	public static Map<String, Long> map;

	private static Stack<Activity> activityStack = new Stack<Activity>();

	public static boolean ISDEBUG = true;

	@Override
	public void onCreate() {
		super.onCreate();

		mContext = getApplicationContext();

		new Thread() {
			public void run() {
				createDir();
			}
		}.start();

		// registerUncatchedException();
		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(mContext);

		Fresco.initialize(mContext);
		// getAssetsConstant();
		appendHeader();
	}

	public static String appendHeader() {
		StringBuilder builder = new StringBuilder();
		builder.append("appName=");
		builder.append("patient");
		builder.append("&imei=");
		TelephonyManager tm = (TelephonyManager) mContext
				.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = tm.getDeviceId();
		String imsi = tm.getSubscriberId();

		builder.append(imei);

		builder.append("&imsi=");
		if (TextUtils.isEmpty(imsi)) {
			builder.append("None");
		} else {
			builder.append(imsi);
		}
		builder.append("&os=");

		builder.append("Android" + android.os.Build.VERSION.RELEASE);// 获取到当前应用的版本号

		builder.append("&appversion=");
		builder.append("1.0.0");

		builder.append("&data=");

		return builder.toString();
	}

	private void getAssetsConstant() {
		try {
			InputStream is = getAssets().open("CONSTANT");
			int size = is.available();
			// Read the entire asset into a local byte buffer.
			byte[] buffer = new byte[size];
			is.read(buffer);
			is.close();
			// Convert the buffer into a string.
			String json = new String(buffer, "UTF-8");
			JSONObject jsonObject = new JSONObject(json);
			String baseUrl = jsonObject.getString("BaseUrl");
			BASE = baseUrl;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void createDir() {

		File file = new File(Constant.ROOF);
		if (!file.exists()) {
			file.mkdirs();
		}
		file = new File(Constant.TEMP_FILEPATH);
		if (!file.exists()) {
			file.mkdirs();
		}
		file = new File(Constant.DOWNLOAD_FILEPATH);
		if (!file.exists()) {
			file.mkdirs();
		}
		file = new File(Constant.SOUND_FILEPATH);
		if (!file.exists()) {
			file.mkdirs();
		}
		file = new File(Constant.VIDEO_FILEPATH);
		if (!file.exists()) {
			file.mkdirs();
		}
		file = new File(Constant.IMAGE_FILEPATH);
		if (!file.exists()) {
			file.mkdirs();
		}
		file = new File(Constant.LOG_FILEPATH);
		if (!file.exists()) {
			file.mkdirs();
		}

		if (FileManager.getDirSize(new File(Constant.ROOF)) > 50) {
			FileManager.deleteDirectory(Constant.LOG_FILEPATH);
			FileManager.deleteDirectory(Constant.SOUND_FILEPATH);
			FileManager.deleteDirectory(Constant.VIDEO_FILEPATH);
			if (FileManager.getDirSize(new File(Constant.ROOF)) > 50) {
				FileManager.deleteDirectory(Constant.IMAGE_FILEPATH);
				FileManager.deleteDirectory(Constant.DOWNLOAD_FILEPATH);
				FileManager.deleteDirectory(Constant.TEMP_FILEPATH);
			}
		}
	}

	public static Context getContext() {
		return mContext;
	}

	public static RequestQueue getRequestQueue() {
		// lazy initialize the request queue, the queue instance will be
		// created when it is accessed for the first time
		if (mQueue == null) {
			mQueue = Volley.newRequestQueue(mContext);
		}
		return mQueue;
	}

	public static void addActivity(Activity activity) {
		activityStack.add(activity);
	}

	public static void finishAll() {
		int size = activityStack.size();
		for (int i = size - 1; i >= 0; i--) {
			finishActivity(activityStack.get(i));
			// Log.wtf("xxxxxxxxxxx", "finishAll");
		}
		activityStack.clear();
	}

	public static void finishActivity(Activity activity) {
		// Log.wtf("xxxxxxxxxxx", "finishActivity");
		if (activity != null) {
			activityStack.remove(activity);
			if (!activity.isFinishing()) {
				activity.finish();
				activity = null;
			}
		}
	}

	/*
	 * private void registerUncatchedException() {
	 * Thread.setDefaultUncaughtExceptionHandler(this); }
	 * 
	 * @Override public void uncaughtException(Thread thread, Throwable ex) {
	 * ex.printStackTrace();
	 * android.os.Process.killProcess(android.os.Process.myPid()); }
	 */

	/*
	 * ActivityManager manager = (ActivityManager)
	 * getSystemService(ACTIVITY_SERVICE);
	 * manager.killBackgroundProcesses("com.hykj");
	 * 
	 * Intent intent = new Intent(getApplicationContext(), StartActivity.class);
	 * PendingIntent restartIntent =
	 * PendingIntent.getActivity(getApplicationContext(), 0, intent,
	 * Intent.FLAG_ACTIVITY_NEW_TASK); // 退出程序 AlarmManager mgr = (AlarmManager)
	 * getApplicationContext().getSystemService(Context.ALARM_SERVICE);
	 * mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000,
	 * restartIntent); // 1秒钟后重启应用
	 */
}
