package com.hykj.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;

import com.hykj.App;
import com.hykj.Constant;
import com.hykj.R;
import com.hykj.activity.subject.SubjectDetailActivity;


public class DownloadService extends Service {

	// 标题
	private String downloadFileUrl;
	private final static int DOWNLOAD_COMPLETE = 0;
	private final static int DOWNLOAD_FAIL = 1;
	// 文件存储
	private File downloadDir = null;
	private File downloadFile = null;

	// 通知栏
	private NotificationManager notificationManager = null;
	private Notification notification = null;
	// 通知栏跳转Intent
	private Intent updateIntent = null;
	private PendingIntent updatePendingIntent = null;

	private String notifyTitle;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// 获取传值
		downloadFileUrl = intent.getStringExtra("fileUrl");
		updateIntent = intent;
		notifyTitle = downloadFileUrl.substring(downloadFileUrl.lastIndexOf("/") + 1);
		// 创建文件
		if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment.getExternalStorageState())) {
			downloadDir = new File(Environment.getExternalStorageDirectory(), "Thealth/download/");
			downloadFile = new File(downloadDir, notifyTitle);
		}

		this.notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		this.notification = new Notification();

		// 设置通知栏显示内容
		notification.icon = R.drawable.ic_launcher;
		notification.tickerText = "开始下载";
		notification.setLatestEventInfo(this, notifyTitle, "0%", null);
		// 发出通知
		notificationManager.notify(0, notification);

		// 开启一个新的线程下载，如果使用Service同步下载，会导致ANR问题，Service本身也会阻塞
		new Thread(new downloadRunnable()).start();// 这个是下载的重点，是下载的过程

		return super.onStartCommand(intent, flags, startId);
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DOWNLOAD_COMPLETE:
				notification.flags |= Notification.FLAG_AUTO_CANCEL;
				if (notifyTitle.endsWith(".apk")) {
					// 跳转
					startActivity(getOpenFileIntent());
				} else {
					final com.gc.materialdesign.widgets.Dialog dialog = new com.gc.materialdesign.widgets.Dialog(App.getContext(), "下载成功", "是否打开");
					dialog.setOnAcceptButtonClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							// 跳转
							startActivity(getOpenFileIntent());
							
							dialog.dismiss();
						}
					});
					dialog.addCancelButton("取消", new OnClickListener() {
						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});
					dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
			        dialog.show();
				}
				clickNotifyEven();
				// 停止服务
				stopService(updateIntent);
				break;
			case DOWNLOAD_FAIL:
				// 下载失败
				notification.setLatestEventInfo(DownloadService.this, notifyTitle, "下载失败。", null);
				notificationManager.notify(0, notification);
				// 停止服务
				stopService(updateIntent);
				break;
			default:
				stopService(updateIntent);
			}
		}
	};
	
	private void clickNotifyEven() {
		updatePendingIntent = PendingIntent.getActivity(DownloadService.this, 0, getOpenFileIntent(), 0);
		notification.defaults = Notification.DEFAULT_SOUND;// 铃声提醒
		notification.setLatestEventInfo(DownloadService.this, notifyTitle, "下载完成,点击打开。", updatePendingIntent);
		notificationManager.notify(0, notification);
	}
	
	private Intent getOpenFileIntent(){
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// 设置intent的Action属性
		intent.setAction(Intent.ACTION_VIEW);
		// 设置intent的data和Type属性。
		String type = getMIMEType(downloadFile);
		intent.setDataAndType(Uri.fromFile(downloadFile), type);
		return intent;
	}

	private String getMIMEType(File file) {
		String type = "*/*";
		String fName = file.getName();
		// 获取后缀名前的分隔符"."在fName中的位置。
		int dotIndex = fName.lastIndexOf(".");
		if (dotIndex < 0) {
			return type;
		}
		/* 获取文件的后缀名 */
		String end = fName.substring(dotIndex, fName.length()).toLowerCase();
		if (end == "")
			return type;
		// 在MIME和文件类型的匹配表中找到对应的MIME类型。
		for (int i = 0; i < SubjectDetailActivity.MIME_MAPTABLE.length; i++) {
			if (end.equals(SubjectDetailActivity.MIME_MAPTABLE[i][0]))
				type = SubjectDetailActivity.MIME_MAPTABLE[i][1];
		}
		return type;
	}

	class downloadRunnable implements Runnable {
		Message message = handler.obtainMessage();

		public void run() {
			message.what = DOWNLOAD_COMPLETE;
			try {
				// 增加权限<uses-permission
				// android:name="android.permission.WRITE_EXTERNAL_STORAGE">;
				if (!downloadDir.exists()) {
					downloadDir.mkdirs();
				}
				if (!downloadFile.exists()) {
					downloadFile.createNewFile();
				}
				long downloadSize = downloadUpdateFile(downloadFileUrl, downloadFile);
				if (downloadSize > 0) {
					// 下载成功
					handler.sendMessage(message);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				message.what = DOWNLOAD_FAIL;
				// 下载失败
				handler.sendMessage(message);
			}
		}
	}

	public long downloadUpdateFile(String downloadUrl, File saveFile) throws Exception {
		// 这样的下载代码很多，我就不做过多的说明
		long downloadCount = 0;
		long currentSize = 0;
		long totalSize = 0;
		long updateTotalSize = 0;
		HttpURLConnection httpConnection = null;
		InputStream is = null;
		FileOutputStream fos = null;

		try {
			URL url = new URL(downloadUrl);
			httpConnection = (HttpURLConnection) url.openConnection();
			httpConnection.setRequestProperty("User-Agent", "PacificHttpClient");
			/*if (currentSize > 0) {
				httpConnection.setRequestProperty("RANGE", "bytes=" + currentSize + "-");
			}*/
			httpConnection.setConnectTimeout(10000);
			httpConnection.setReadTimeout(20000);
			updateTotalSize = httpConnection.getContentLength();
			if (httpConnection.getResponseCode() == 404) {
				throw new Exception("fail!");
			}
			is = httpConnection.getInputStream();
			fos = new FileOutputStream(saveFile, false);
			byte buffer[] = new byte[4096];
			int readsize = 0;
			while ((readsize = is.read(buffer)) > 0) {
				fos.write(buffer, 0, readsize);
				totalSize += readsize;
				// 为了防止频繁的通知导致应用吃紧，百分比增加3才通知一次
				if ((downloadCount == 0) || (int) (totalSize * 100 / updateTotalSize) - 3 > downloadCount) {
					downloadCount += 3;
					notification.setLatestEventInfo(DownloadService.this, "正在下载", (int) (totalSize * 100 / updateTotalSize) + "%", null);
					notificationManager.notify(0, notification);
				}
			}
		} finally {
			if (httpConnection != null) {
				httpConnection.disconnect();
			}
			if (is != null) {
				is.close();
			}
			if (fos != null) {
				fos.close();
			}
		}
		return totalSize;
	}
}
