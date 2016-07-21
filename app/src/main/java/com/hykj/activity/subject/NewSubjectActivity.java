package com.hykj.activity.subject;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hykj.App;
import com.hykj.Constant;
import com.hykj.R;
import com.hykj.db.DataBaseHelper;
import com.hykj.manager.GetPhotoManager;
import com.hykj.manager.MyLoactionManager;
import com.hykj.manager.MySoundManager;
import com.hykj.manager.MySoundManager.SoundLevelChangeListener;
import com.hykj.manager.RecordVideoManager;
import com.hykj.utils.MyProgress;
import com.hykj.utils.MyToast;
import com.hykj.utils.OnUploadStateListener;
import com.hykj.utils.XHttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.tb.emoji.Emoji;
import com.tb.emoji.EmojiUtil;
import com.tb.emoji.FaceFragment;
import com.tb.emoji.FaceFragment.OnEmojiClickListener;

public class NewSubjectActivity extends Activity implements OnClickListener,OnEmojiClickListener{
	
	private Button bt_publish;
	private EditText et_content;
	private EditText et_title;

	private String videoPath = null;
	private String videoUrl = null;
	private String filePath = null;
	private String fileUrl = null;
	private String soundPath = null;
	private String soundUrl = null;
	private String locInfo = null;
	private double latitude = 0;
	private double longitude = 0;
	
	private ArrayList<String> imagePath = new ArrayList<String>();
	private ArrayList<String> imgUrls = new ArrayList<String>();

	private StringBuilder url = null;
	private int responseCode = -1;
	private String description = null;

	private int uploadCount = 0;
	private int uploadSuccessCount = 0;

	private WindowManager.LayoutParams mWindowNanagerParams;
	private InputMethodManager mInputMethodManager;
	
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				uploadSuccessCount++;
				if (uploadSuccessCount == uploadCount) {
//					updataToDB();
					sendData();
				}
				break;
			case 1:
				MyProgress.show(NewSubjectActivity.this);
				uploadFile();
				break;
			case Constant.GET_DATA_SUCCESS:
				MyProgress.dismiss();
				MyToast.show(description);
				NewSubjectActivity.this.finish();
//				MaterialUtil.progressDialogDismiss();
//				delToDB();
				break;
			case Constant.GET_DATA_ANALYZE_ERROR:
				MyProgress.dismiss();
				MyToast.show("解析数据失败");
//				MaterialUtil.progressDialogDismiss();
				break;
			case Constant.GET_DATA_NETWORK_ERROR:
				MyProgress.dismiss();
				MyToast.show("网络错误");
//				MaterialUtil.progressDialogDismiss();
				break;
			case Constant.GET_DATA_SERVER_ERROR:
				MyProgress.dismiss();
				MyToast.show(description);
//				MaterialUtil.progressDialogDismiss();
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		App.addActivity(this);
		
		mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		mWindowNanagerParams = getWindow().getAttributes();
		
//		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		setContentView(R.layout.activity_subject_new);
		findView();
		registAndSetting();

//		readFromDB();
		
	}

	private void readFromDB() {
		DataBaseHelper dataBaseHelper = new DataBaseHelper(App.getContext());
		SQLiteDatabase database = dataBaseHelper.getWritableDatabase();

		Cursor cursor = database.rawQuery("SELECT * FROM subjectinfo WHERE userId=?", new String[] { App.USERID + "" });
		if (cursor.moveToNext()) {
			String title = cursor.getString(1);
			String content = cursor.getString(2);
			try {
				EmojiUtil.handlerEmojiText(et_title, title, App.getContext());
				EmojiUtil.handlerEmojiText(et_content, content, App.getContext());
			} catch (IOException e) {
				e.printStackTrace();
			}
			et_title.setSelection(et_title.length());
			et_content.setSelection(et_content.length());
			locInfo = cursor.getString(3);
			latitude = cursor.getDouble(4);
			longitude = cursor.getDouble(5);
			videoUrl = cursor.getString(6);
			videoPath = cursor.getString(7);
			soundUrl = cursor.getString(8);
			soundPath = cursor.getString(9);
			fileUrl = cursor.getString(10);
			filePath = cursor.getString(11);
			String ip = cursor.getString(12);
			if (!TextUtils.isEmpty(ip)) {
				String[] ips = ip.split("~!@#$%^&*()_+");
				for (int i = 0; i < ips.length; i++) {
					imagePath.add(ips[i]);
				}
			}
			String is = cursor.getString(13);
			if (!TextUtils.isEmpty(is)) {
				String[] iss = is.split("~!@#$%^&*()_+");
				for (int i = 0; i < iss.length; i++) {
					imgUrls.add(iss[i]);
				}
			}
		}
		dataBaseHelper.close();
		database.close();
	}

	private void registAndSetting() {
		
		et_title.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					if (mWindowNanagerParams.softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE || fl_facechoose.getVisibility() == View.VISIBLE) {
						fl_facechoose.setVisibility(View.GONE);
						return true;
					}
				}
				return false;
			}
		});
		
		et_content.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					if (mWindowNanagerParams.softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE || fl_facechoose.getVisibility() == View.VISIBLE) {
						fl_facechoose.setVisibility(View.GONE);
						return true;
					}
				}
				return false;
			}
		});
		bt_publish.setOnClickListener(this);

		FaceFragment faceFragment = FaceFragment.Instance();
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.add(R.id.fl_facechoose, faceFragment);
		transaction.commit();
		
		et_content.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mInputMethodManager.showSoftInput(et_content, 0);
				fl_facechoose.setVisibility(View.GONE);
				return false;
			}
		});
		
		et_title.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mInputMethodManager.showSoftInput(et_title, 0);
				fl_facechoose.setVisibility(View.GONE);
				return false;
			}
		});
		
	}

	private void findView() {
		
		ImageView iv_back = (ImageView) findViewById(R.id.iv_title_back);
		iv_back.setOnClickListener(this);
		ImageView iv_pic = (ImageView) findViewById(R.id.publish_pic);
		iv_pic.setOnClickListener(this);
		ImageView iv_loc = (ImageView) findViewById(R.id.publish_loc);
		iv_loc.setOnClickListener(this);
		ImageView iv_sound = (ImageView) findViewById(R.id.publish_sound);
		iv_sound.setOnClickListener(this);
		ImageView iv_file = (ImageView) findViewById(R.id.publish_file);
		iv_file.setOnClickListener(this);
		ImageView iv_video = (ImageView) findViewById(R.id.publish_video);
		iv_video.setOnClickListener(this);
		ImageView iv_face = (ImageView) findViewById(R.id.publish_face);
		iv_face.setOnClickListener(this);
		bt_publish = (Button) findViewById(R.id.bt_subjectnew_publish);
		
		et_title = (EditText) findViewById(R.id.et_subjectnew_title);
		et_content = (EditText) findViewById(R.id.et_subjectnew_content);
		fl_facechoose = (FrameLayout) findViewById(R.id.fl_facechoose);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.publish_pic:
			Intent intentPic = new Intent(NewSubjectActivity.this,NewSubjectPicActivity.class);
			intentPic.putExtra("imagePath", imagePath);
			startActivityForResult(intentPic, 0);
			break;
		case R.id.publish_loc:
			Intent intent = new Intent(this, MyLoactionManager.class);
			intent.putExtra("locInfo", locInfo);
			startActivityForResult(intent, 0);
			break;
		case R.id.publish_sound:
			Intent intentSound = new Intent(this, NewSubjectSoundActivity.class);
			intentSound.putExtra("soundPath", soundPath);
			startActivityForResult(intentSound, 0);
			break;
		case R.id.publish_file:
			Intent intentFile = new Intent(this, NewSubjectFileActivity.class);
			intentFile.putExtra("filePath", filePath);
			startActivityForResult(intentFile, 0);
			break;
		case R.id.publish_video:
			Intent videoIntent = new Intent(this, RecordVideoManager.class);
			videoIntent.putExtra("videoPath", videoPath);
			startActivityForResult(videoIntent, 0);
			break;
		case R.id.publish_face:
			if (fl_facechoose.getVisibility() == View.GONE) {
				fl_facechoose.setVisibility(View.VISIBLE);
			}else{
				fl_facechoose.setVisibility(View.GONE);
			}
			break;
		case R.id.iv_title_back:
			NewSubjectActivity.this.finish();
			break;
		case R.id.bt_subjectnew_publish:
				publishSubject();
			break;
		}
	}


	private FrameLayout fl_facechoose;

	private void publishSubject() {
		if (checkText(et_title)) {
			MyToast.show("标题不能为空或者不能含有特殊字符");
			return;
		}
		if (checkText(et_content)) {
			MyToast.show("内容不能为空或者不能含有特殊字符");
			return;
		}
//		MaterialUtil.showProgressDialog(NewSubjectActivity.this);
		handler.sendEmptyMessage(1);
//		writeToDB();
//		checkAccess();
	}

	private boolean checkText(EditText et) {
		String s = et.getText().toString();
		if (TextUtils.isEmpty(s)) {
			return true;
		}
		/*for (int i = 0; i < s.length(); i++) {
			String special = "~!@#$%^&*()_+=-,.<>'?/;:\\\"|[]{}0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ ";
			String charat = s.charAt(i) + "";
			Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
			Matcher m = p.matcher(charat);
			if (!m.find() && !special.contains(charat)) {
				return true;
			}
		}*/
		return false;
	}

	protected void updataToDB() {
		DataBaseHelper baseHelper = new DataBaseHelper(App.getContext());
		SQLiteDatabase database = baseHelper.getWritableDatabase();
		String sql = "";
		if (!TextUtils.isEmpty(soundUrl)) {
			sql = "UPDATE subjectinfo SET soundUrl =" + soundUrl + "WHERE  userId=" + App.USERID + ";";
			database.execSQL(sql);
		}
		if (!TextUtils.isEmpty(videoUrl)) {
			sql = "UPDATE subjectinfo SET videoUrl =" + videoUrl + "WHERE  userId=" + App.USERID + ";";
			database.execSQL(sql);
		}
		if (!TextUtils.isEmpty(fileUrl)) {
			sql = "UPDATE subjectinfo SET fileUrl =" + fileUrl + "WHERE  userId=" + App.USERID + ";";
			database.execSQL(sql);
		}
		if (imgUrls.size() > 0) {
			String s = "";
			for (int i = 0; i < imgUrls.size(); i++) {
				if (i == imgUrls.size() - 1) {
					s += imgUrls.get(i);
				} else {
					s += imgUrls.get(i) + "~!@#$%^&*()_+";
				}
			}
			sql = "UPDATE subjectinfo SET imgUrls =" + s + "WHERE  userId=" + App.USERID + ";";
			database.execSQL(sql);
		}
		database.close();
		baseHelper.close();
	}

	/*private void writeToDB() {
		DataBaseHelper baseHelper = new DataBaseHelper(App.getContext());
		SQLiteDatabase database = baseHelper.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("userId", App.USERID);
		values.put("title", et_title.getText().toString());
		values.put("content", et_content.getText().toString());
		database.replace("subjectinfo", null, values);

		if (!TextUtils.isEmpty(locInfo)) {
			values = new ContentValues();
			values.put("locInfo", locInfo);
			values.put("latitude", latitude);
			values.put("longitude", longitude);
			database.update("subjectinfo", values, "userId=?", new String[] { App.USERID + "" });
		}
		if (!TextUtils.isEmpty(videoPath)) {
			values = new ContentValues();
			values.put("videoPath", videoPath);
			database.update("subjectinfo", values, "userId=?", new String[] { App.USERID + "" });
		}
		if (!TextUtils.isEmpty(filePath)) {
			values = new ContentValues();
			values.put("filePath", filePath);
			database.update("subjectinfo", values, "userId=?", new String[] { App.USERID + "" });
		}
		if (!TextUtils.isEmpty(soundPath)) {
			values = new ContentValues();
			values.put("soundPath", soundPath);
			database.update("subjectinfo", values, "userId=?", new String[] { App.USERID + "" });
		}
		if (imagePath.size() > 0) {
			String s = "";
			for (int i = 0; i < imagePath.size(); i++) {
				if (i == imagePath.size() - 1) {
					s += imagePath.get(i);
				} else {
					s += imagePath.get(i) + "~!@#$%^&*()_+";
				}
			}
			values = new ContentValues();
			values.put("imagePath", s);
			database.update("subjectinfo", values, "userId=?", new String[] { App.USERID + "" });
		}
		database.close();
		baseHelper.close();
	}*/

	
	String getEncode(String s){
		String result =null;
		
		try {
			result = URLEncoder.encode(s, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return result;
	}

	private void sendData() {
		url = new StringBuilder(App.BASE + Constant.PUBLISH_SUBJECT);
		url.append("tocken="+App.TOKEN);
		url.append("&data=");
		JSONObject json = new JSONObject();
		try {
			json.put("content", getEncode(et_content.getText().toString()));
			json.put("title", getEncode(et_title.getText().toString()));
			if (!TextUtils.isEmpty(locInfo)) {
				json.put("locInfo", getEncode(locInfo));
				json.put("lat", latitude);
				json.put("lng", longitude);
			}
			if (!TextUtils.isEmpty(videoUrl)) {
				json.put("videoUrl", videoUrl);
			}
			if (!TextUtils.isEmpty(fileUrl)) {
				json.put("fileUrl", fileUrl);
			}
			if (!TextUtils.isEmpty(soundUrl)) {
				json.put("soundUrl", soundUrl);
			}
			if (imgUrls.size() > 0) {
				JSONArray array = new JSONArray();
				for (int i = 0; i < imgUrls.size(); i++) {
					array.put(i, imgUrls.get(i));
				}
				json.put("imgUrls", array);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		url.append(json.toString());
//		Log.wtf("发帖的url", url.toString());
		App.getRequestQueue().add(getreqData());
	}

	private Request getreqData() {
		return new StringRequest(Method.GET, url.toString(), new Listener<String>() {
			@Override
			public void onResponse(String res) {
				try {
//					Log.wtf("发帖的返回结果", res);
					/*byte[] bytes = res.toString().getBytes("ISO-8859-1");
					JSONObject response = new JSONObject(new String(bytes, "UTF-8"));*/
					JSONObject response = new JSONObject(res);

					responseCode = response.getInt("code");
					description = response.getString("message");
					if (responseCode != 206) {
						handler.sendEmptyMessage(Constant.GET_DATA_SERVER_ERROR);
						return;
					}
					handler.sendEmptyMessage(Constant.GET_DATA_SUCCESS);
				} catch (Exception e) {
					e.printStackTrace();
					handler.sendEmptyMessage(Constant.GET_DATA_ANALYZE_ERROR);
				}
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				handler.sendEmptyMessage(Constant.GET_DATA_NETWORK_ERROR);
			}
		});
	}

	protected void delToDB() {
		DataBaseHelper baseHelper = new DataBaseHelper(App.getContext());
		SQLiteDatabase database = baseHelper.getWritableDatabase();

		String sql = "DELETE FROM subjectinfo WHERE userId = " + App.USERID + ";";
		database.execSQL(sql);

		baseHelper.close();
		database.close();
	}


	private void uploadFile() {
		if (!TextUtils.isEmpty(videoPath)) {
			upload(videoPath, "video");
			uploadCount++;
		}
		if (!TextUtils.isEmpty(filePath)) {
			upload(filePath, "file");
			uploadCount++;
		}
		if (!TextUtils.isEmpty(soundPath)) {
			upload(soundPath, "sound");
			uploadCount++;
		}
		if (imagePath.size() != 0) {
			for (int i = 0; i < imagePath.size(); i++) {
				upload(imagePath.get(i), "img");
				uploadCount++;
			}
		}
		if (uploadCount == 0) {
			sendData();
		}
	}

	private void upload(String uploadPath, final String t) {
		com.lidroid.xutils.http.RequestParams requestParams = new com.lidroid.xutils.http.RequestParams();
		requestParams.addBodyParameter("media", new File(uploadPath));
		requestParams.addBodyParameter("tocken", App.TOKEN);
//		Log.wtf("xxxxxxxxxxx", App.BASE+Constant.UPLOAD_FILE);
		XHttpUtils.getInstance().upload(App.BASE+Constant.UPLOAD_FILE, requestParams, new OnUploadStateListener() {
			@Override
			public void onUploadSuccess(String result) {
				try {
//					Log.wtf("上传文件返回结果", result);
					JSONObject json = new JSONObject(result);
					int responseCode = json.getInt("code");
					if (responseCode != 206) {
						if ("sound".equals(t)) {
							soundUrl = json.getString("fileUrl");
						} else if ("video".equals(t)) {
							videoUrl = json.getString("fileUrl");
						} else if ("file".equals(t)) {
							fileUrl = json.getString("fileUrl");
						} else if ("img".equals(t)) {
							imgUrls.add(json.getString("fileUrl"));
						}
						handler.sendEmptyMessage(0);
					} else {
						description = json.getString("message");
						handler.sendEmptyMessage(Constant.GET_DATA_SERVER_ERROR);
					}
				} catch (JSONException e) {
					e.printStackTrace();
					handler.sendEmptyMessage(Constant.GET_DATA_ANALYZE_ERROR);
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				
//				Log.wtf("xxxxxxxxxxx", "onFailure");
				
				handler.sendEmptyMessage(Constant.GET_DATA_NETWORK_ERROR);
			}
		});
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case -20:
			locInfo = data.getStringExtra("locInfo");
			latitude = data.getDoubleExtra("latitude", 0);
			longitude = data.getDoubleExtra("longitude", 0);
			break;
		case -800:
			imagePath = (ArrayList<String>) data.getSerializableExtra("imagePath");
			break;
		case -900:
			soundPath = data.getStringExtra("soundPath");
			break;
		case -700:
			filePath = data.getStringExtra("filePath");
			break;
		case -22:
			videoPath = data.getStringExtra("videoPath");
			break;
		}
	}

	@Override
	public void onEmojiDelete() {
		EditText editText = null;
		if (et_title.hasFocus()) {
			editText = et_title;
		}else{
			editText = et_content;
		}
		 String text = editText.getText().toString();
	        if (text.isEmpty()) {
	            return;
	        }
	        if ("]".equals(text.substring(text.length() - 1, text.length()))) {
	            int index = text.lastIndexOf("[");
	            if (index == -1) {
	                int action = KeyEvent.ACTION_DOWN;
	                int code = KeyEvent.KEYCODE_DEL;
	                KeyEvent event = new KeyEvent(action, code);
	                editText.onKeyDown(KeyEvent.KEYCODE_DEL, event);
	                displayTextView();
	                return;
	            }
	            editText.getText().delete(index, text.length());
	            displayTextView();
	            return;
	        }
	        int action = KeyEvent.ACTION_DOWN;
	        int code = KeyEvent.KEYCODE_DEL;
	        KeyEvent event = new KeyEvent(action, code);
	        editText.onKeyDown(KeyEvent.KEYCODE_DEL, event);
	        displayTextView();
	}
    private void displayTextView() {
    	EditText editText = null;
		if (et_title.hasFocus()) {
			editText = et_title;
		}else{
			editText = et_content;
		}
        try {
            EmojiUtil.handlerEmojiText(editText, editText.getText().toString(), App.getContext());
        } catch (IOException e) {
            e.printStackTrace();
        }
        editText.setSelection(editText.length());
    }
	@Override
	public void onEmojiClick(Emoji emoji) {
		EditText editText = null;
		if (et_title.hasFocus()) {
			editText = et_title;
		}else{
			editText = et_content;
		}
		if (emoji != null) {
            int index = editText.getSelectionStart();
            Editable editable = editText.getEditableText();
            if (index < 0) {
                editable.append(emoji.getContent());
            } else {
                editable.insert(index, emoji.getContent());
            }
        }
        displayTextView();
	}

}
