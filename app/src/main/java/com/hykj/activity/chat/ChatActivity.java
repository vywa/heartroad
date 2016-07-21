package com.hykj.activity.chat;

import java.io.File;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.AsyncQueryHandler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.hykj.App;
import com.hykj.Constant;
import com.hykj.R;
import com.hykj.activity.usermanagement.DoctorDetailActivity;
import com.hykj.adapter.ChatAdapter;
import com.hykj.db.ChatProvider;
import com.hykj.db.ChatProvider.ChatConstants;
import com.hykj.db.RosterProvider;
import com.hykj.entity.Doctor;
import com.hykj.fragment.usermanagement.ChatPicFragment;
import com.hykj.manager.MySoundManager;
import com.hykj.manager.MySoundManager.SoundLevelChangeListener;
import com.hykj.service.ChatService;
import com.hykj.utils.BitmapUtil;
import com.hykj.utils.MyToast;
import com.tb.emoji.Emoji;
import com.tb.emoji.EmojiUtil;
import com.tb.emoji.FaceFragment;
import com.tb.emoji.FaceFragment.OnEmojiClickListener;

public class ChatActivity extends Activity implements OnEmojiClickListener, OnClickListener, OnTouchListener,SwipeRefreshLayout.OnRefreshListener{

	private EditText et_chat;
	private ListView lv_chat;

	private String mWithJabberID = null;// 当前聊天用户的ID

	private WindowManager.LayoutParams mWindowNanagerParams;
	private InputMethodManager mInputMethodManager;

	private ChatService chatService;

	private MySoundManager mySoundManager;
	private SwipeRefreshLayout mSwipeRefreshWidget;
	public static Doctor doctor = new Doctor();
	private Button bt_send;
	private Fragment fragment;
	
	public static final String INTENT_EXTRA_USERNAME = ChatActivity.class.getName() + ".username";// 昵称对应的key

	private static final String[] PROJECTION_FROM = new String[] { ChatProvider.ChatConstants._ID, ChatProvider.ChatConstants.DATE, ChatProvider.ChatConstants.DIRECTION,
			ChatProvider.ChatConstants.JID, ChatProvider.ChatConstants.MESSAGE, ChatProvider.ChatConstants.DELIVERY_STATUS, ChatProvider.ChatConstants.PACKET_ID };// 查询字段

	private ContentObserver mContactObserver = new ContactObserver();// 联系人数据监听，主要是监听对方在线状态

	// 查询联系人数据库字段
	private static final String[] STATUS_QUERY = new String[] { RosterProvider.RosterConstants.STATUS_MODE, RosterProvider.RosterConstants.STATUS_MESSAGE, };

	ServiceConnection mServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			chatService = ((ChatService.ChatBinder) service).getService();
			// 开始连接xmpp服务器
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			chatService = null;
		}
	};
	private FrameLayout fl_face;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		App.addActivity(this);
		setContentView(R.layout.activit_chat);
		initData();
		
		findView();
		
		setChatWindowAdapter(false);

		getContentResolver().registerContentObserver(RosterProvider.CONTENT_URI, true, mContactObserver);// 开始监听联系人数据库

		FaceFragment faceFragment = FaceFragment.Instance();
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.replace(R.id.fl_lylchat_facechoose, faceFragment,"face");
		transaction.commit();
	}

	private void sendMessageIfNotNull() {

		if (et_chat.getText().length() == 0) {
			return;
		}

		if (et_chat.getText().length() > 150) {
			MyToast.show("发送内容过长 请分段发送");
			return;
		}

		if (et_chat.getText().length() >= 1) {
			if (chatService != null) {
				chatService.sendMessage(mWithJabberID, et_chat.getText().toString());
			}
			et_chat.setText(null);
		}
		
		bt_send.setVisibility(View.GONE);
		bt_add.setVisibility(View.VISIBLE);
	}

	/**
	 * 联系人数据库变化监听
	 * 
	 */
	private class ContactObserver extends ContentObserver {
		public ContactObserver() {
			super(new Handler());
		}

		public void onChange(boolean selfChange) {
			updateContactStatus();// 联系人状态变化时，刷新界面
		}
	}

	private void bindXMPPService() {
		Intent mServiceIntent = new Intent(this, ChatService.class);
		bindService(mServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		// 窗口获取到焦点时绑定服务，失去焦点将解绑
		if (hasFocus)
			bindXMPPService();
		else {
			try {
				unbindService(mServiceConnection);
			} catch (IllegalArgumentException e) {
			}
		}
	}

	ListAdapter adapter;
	private Button bt_speak;
	private ImageButton bt_add;
	private ImageButton bt_change;
	private ImageButton bt_smile;

	int count = 10;
	/**
	 * 设置聊天的Adapter
	 */
	private void setChatWindowAdapter(final boolean isRefresh) {
		String selection = ChatConstants.JID + "='" + mWithJabberID + "' AND " + ChatConstants.SENDER + "='" + App.USERID + "'";
		
		// 异步查询数据库
		new AsyncQueryHandler(getContentResolver()) {
			@Override
			protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
				adapter = new ChatAdapter(ChatActivity.this, cursor, PROJECTION_FROM);
				lv_chat.setAdapter(adapter);
				lv_chat.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
				/*if (isRefresh) {
					lv_chat.setSelection(adapter.getCount() - 1);
					lv_chat.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
				}else{
					lv_chat.setSelection(0);
					lv_chat.setTranscriptMode(ListView.TRANSCRIPT_MODE_DISABLED);
					stackFromBottom
				}*/
				lv_chat.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
				if (isRefresh) {
					lv_chat.setStackFromBottom(false);
//					lv_chat.setTranscriptMode(ListView.TRANSCRIPT_MODE_DISABLED);
				}else{
					lv_chat.setStackFromBottom(true);
				}
				mSwipeRefreshWidget.setRefreshing(false);
				
			}
		}.startQuery(0, null, ChatProvider.CONTENT_URI, PROJECTION_FROM, selection, null, ChatConstants.DATE+" DESC limit "+(count += 5));
		
	}

	@Override
	public void onRefresh() {
		
		setChatWindowAdapter(true);
		
	}
	
	private void updateContactStatus() {
//		Cursor cursor = getContentResolver().query(RosterProvider.CONTENT_URI, STATUS_QUERY, RosterProvider.RosterConstants.JID + " = ?", new String[] { mWithJabberID }, null);
//		int MODE_IDX = cursor.getColumnIndex(RosterProvider.RosterConstants.STATUS_MODE);
//		int MSG_IDX = cursor.getColumnIndex(RosterProvider.RosterConstants.STATUS_MESSAGE);
//
//		if (cursor.getCount() == 1) {
//			cursor.moveToFirst();
//			int status_mode = cursor.getInt(MODE_IDX);
//			String status_message = cursor.getString(MSG_IDX);
//			/**
//			 * mTitleNameView.setText(XMPPHelper.splitJidAndServer(getIntent().
//			 * getStringExtra(INTENT_EXTRA_USERNAME))); int statusId =
//			 * StatusMode.values()[status_mode].getDrawableId(); if (statusId !=
//			 * -1) {// 如果对应离线状态 // Drawable icon =
//			 * getResources().getDrawable(statusId); //
//			 * mTitleNameView.setCompoundDrawablesWithIntrinsicBounds(icon, //
//			 * null, // null, null);
//			 * mTitleStatusView.setImageResource(statusId);
//			 * mTitleStatusView.setVisibility(View.VISIBLE); } else {
//			 * mTitleStatusView.setVisibility(View.GONE); }
//			 */
//		}
//		cursor.close();
	}

	private void initData() {
		doctor = App.doctor;
//		doctor = (Doctor) getIntent().getSerializableExtra("doctor");
		mWithJabberID = doctor.getUserId()+App.OPENFIRE_SERVICE_NAME;// 获取聊天对象的id
	}

	@SuppressLint("NewApi") 
	private void findView() {
		ImageView iv_back = (ImageView) findViewById(R.id.iv_title_back);
		iv_back.setOnClickListener(this);
		ImageView iv_info = (ImageView) findViewById(R.id.iv_title_info);
		iv_info.setOnClickListener(this);
		TextView tv_name = (TextView) findViewById(R.id.tv_title);
		tv_name.setText(doctor.getName());
		
		mSwipeRefreshWidget = (SwipeRefreshLayout) findViewById(R.id.srl_lylchat);
		mSwipeRefreshWidget.setOnRefreshListener(this);
		mSwipeRefreshWidget.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
		mSwipeRefreshWidget.setFadingEdgeLength(1000);
		mSwipeRefreshWidget.setLayoutMode(SwipeRefreshLayout.OVER_SCROLL_IF_CONTENT_SCROLLS);
		
		et_chat = (EditText) findViewById(R.id.et_lylchart);
		et_chat.setOnTouchListener(this);

		lv_chat = (ListView) findViewById(R.id.lv_lylchart);
		lv_chat.setOnTouchListener(this);
		lv_chat.setDivider(null);

		bt_smile = (ImageButton) findViewById(R.id.bt_lylchat_smile);
		bt_smile.setOnClickListener(this);
		
		bt_change = (ImageButton) findViewById(R.id.ib_lylchat_change);
		bt_change.setOnClickListener(this);
		
		bt_speak = (Button) findViewById(R.id.bt_lylchat_speak);
		bt_speak.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					bt_speak.setText("...............................");
					if (mySoundManager == null) {
						mySoundManager = new MySoundManager("lyl.arm");
						mySoundManager.setOnSoundLevelChangeListener(levelChangeListener);
					}
					mySoundManager.start();
					startTime = System.currentTimeMillis();
					// soundPath = Constant.SOUND_FILEPATH + "/lyl.arm";
				} else if (event.getAction() == MotionEvent.ACTION_UP && startTime != 0) {// 松开手势时执行录制完成
					bt_speak.setText("按住说话");
					if (mySoundManager != null) {
						mySoundManager.stop();
						mySoundManager = null;
					}
					if (System.currentTimeMillis() - startTime < 1000) {
						MyToast.show("录音时间太短");
					} else {
						sendSound();
					}
					startTime = 0L;
				} else if (event.getAction() == MotionEvent.ACTION_CANCEL && startTime != 0) {
					bt_speak.setText("按住说话");
					if (mySoundManager != null) {
						mySoundManager.stop();
						// mySoundManager.cancel();
						mySoundManager = null;
					}
					if (System.currentTimeMillis() - startTime < 1000) {
						MyToast.show("录音时间太短");
					} else {
						sendSound();
					}
					startTime = 0L;
				}
				return true;
			}
		});
		
		bt_send = (Button) findViewById(R.id.bt_lylchat_send);
		bt_send.setOnClickListener(this);
		
		bt_add = (ImageButton) findViewById(R.id.bt_lyladd);
		bt_add.setOnClickListener(this);
		
		fl_face = (FrameLayout) findViewById(R.id.fl_lylchat_facechoose);
		
		mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		mWindowNanagerParams = getWindow().getAttributes();

		et_chat.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (TextUtils.isEmpty(s)) {
					bt_add.setVisibility(View.VISIBLE);
					bt_send.setVisibility(View.GONE);
				}else{
					bt_add.setVisibility(View.GONE);
					bt_send.setVisibility(View.VISIBLE);
				}
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			@Override
			public void afterTextChanged(Editable s) {}
		});
		
		et_chat.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					if (mWindowNanagerParams.softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE || fl_face.getVisibility() == View.VISIBLE) {
						fl_face.setVisibility(View.GONE);
						// imm.showSoftInput(msgEt, 0);
						return true;
					}
				}
				return false;
			}
		});
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (v.getId()) {
		case R.id.lv_lylchart:
			mInputMethodManager.hideSoftInputFromWindow(et_chat.getWindowToken(), 0);
			fl_face.setVisibility(View.GONE);
			break;
		case R.id.et_lylchart:
			mInputMethodManager.showSoftInput(et_chat, 0);
			fl_face.setVisibility(View.GONE);
			break;

		}
		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_lylchat_smile:
			
			if (fl_face.getVisibility() == View.GONE) {
				mInputMethodManager.hideSoftInputFromWindow(et_chat.getWindowToken(), 0);
				fragment = new FaceFragment();
				FragmentTransaction transaction =getFragmentManager().beginTransaction();
				transaction.replace(R.id.fl_lylchat_facechoose,fragment,"face");
				//提交修改
				transaction.commit();
				try {
					Thread.sleep(80);// 解决此时会黑一下屏幕的问题
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				fl_face.setVisibility(View.VISIBLE);
			} else {
				if ("face".equals(fragment.getTag())) {
					fl_face.setVisibility(View.GONE);
					mInputMethodManager.showSoftInput(et_chat, 0);
				}else{
					fragment = new FaceFragment();
					FragmentTransaction transaction =getFragmentManager().beginTransaction();
					transaction.replace(R.id.fl_lylchat_facechoose,fragment,"face");
					//提交修改
					transaction.commit();
				}
			}
			break;
		case R.id.bt_lyladd:
			/*Intent intent = new Intent(this, GetPhotoManager.class);
			startActivityForResult(intent, 0);*/
			//创建修改实例
			
			if (fl_face.getVisibility() == View.GONE) {
				mInputMethodManager.hideSoftInputFromWindow(et_chat.getWindowToken(), 0);
				fragment = new ChatPicFragment();
				FragmentTransaction picTransaction =getFragmentManager().beginTransaction();
				picTransaction.replace(R.id.fl_lylchat_facechoose,fragment,"pic");
				//提交修改
				picTransaction.commit();
				try {
					Thread.sleep(80);// 解决此时会黑一下屏幕的问题
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				fl_face.setVisibility(View.VISIBLE);
			} else {
				if ("pic".equals(fragment.getTag())) {
					fl_face.setVisibility(View.GONE);
					mInputMethodManager.showSoftInput(et_chat, 0);
				}else{
					fragment = new ChatPicFragment();
					FragmentTransaction picTransaction =getFragmentManager().beginTransaction();
					picTransaction.replace(R.id.fl_lylchat_facechoose,fragment,"pic");
					//提交修改
					picTransaction.commit();
				}
			}
			break;
		case R.id.bt_lylchat_send:
			sendMessageIfNotNull();
			break;
		case R.id.iv_title_back:
			ChatActivity.this.finish();
			break;
		case R.id.iv_title_info:
			Intent infoIntent = new Intent(this,DoctorDetailActivity.class);
			infoIntent.putExtra("doctor", doctor);
			startActivity(infoIntent);
			break;
		case R.id.ib_lylchat_change:
			if (et_chat.getVisibility() == View.VISIBLE) {
				et_chat.setVisibility(View.GONE);
				bt_speak.setVisibility(View.VISIBLE);
				bt_smile.setVisibility(View.GONE);
				bt_send.setVisibility(View.GONE);
				bt_add.setVisibility(View.VISIBLE);
				mInputMethodManager.hideSoftInputFromWindow(et_chat.getWindowToken(), 0);
				fl_face.setVisibility(View.GONE);
				bt_change.setImageResource(R.drawable.chatkeybord);
			}else{
				et_chat.setVisibility(View.VISIBLE);
				bt_smile.setVisibility(View.VISIBLE);
				bt_speak.setVisibility(View.GONE);
				if (TextUtils.isEmpty(et_chat.getText().toString().trim())) {
					bt_send.setVisibility(View.GONE);
					bt_add.setVisibility(View.VISIBLE);
				}else{
					bt_send.setVisibility(View.VISIBLE);
					bt_add.setVisibility(View.GONE);
				}
				mInputMethodManager.showSoftInput(et_chat, 0);
				fl_face.setVisibility(View.GONE);
				bt_change.setImageResource(R.drawable.chatsound);
			}
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == ChatPicFragment.REQUEST_CODE_CAMERA) {
				BitmapUtil.compressImage(Constant.TEMP_FILEPATH + "/" + ChatPicFragment.FILE_NAME, Constant.TEMP_FILEPATH + "/temp.jpg");
				done();
			} else if (requestCode == ChatPicFragment.REQUEST_CODE_PICKPHOTO) {
				Uri selectedImage = data.getData();
				String[] filePathColumns = { MediaStore.Images.Media.DATA };
				Cursor c = this.getContentResolver().query(selectedImage, filePathColumns, null, null, null);
				c.moveToFirst();
				int columnIndex = c.getColumnIndex(filePathColumns[0]);
				String picturePath = c.getString(columnIndex);
				c.close();
				BitmapUtil.compressImage(picturePath, Constant.TEMP_FILEPATH + "/temp.jpg");
				done();
			}
		}
	}

	void done(){
		File file = new File(Constant.TEMP_FILEPATH , ChatPicFragment.FILE_NAME);
		if (file.exists()) {
			file.delete();
		}
		File f = new File(Constant.TEMP_FILEPATH + "/temp.jpg");
		if (chatService != null) {
			String decodeBitmap = BitmapUtil.bytesToHex(BitmapUtil.image2byte(f.getAbsolutePath()));
			chatService.sendMessage(mWithJabberID, decodeBitmap);
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		getContentResolver().unregisterContentObserver(mContactObserver);
		((ChatAdapter) adapter).stopPlay();
	}

	@Override
	public void onEmojiDelete() {
		String text = et_chat.getText().toString();
		if (text.isEmpty()) {
			return;
		}
		if ("]".equals(text.substring(text.length() - 1, text.length()))) {
			int index = text.lastIndexOf("[");
			if (index == -1) {
				int action = KeyEvent.ACTION_DOWN;
				int code = KeyEvent.KEYCODE_DEL;
				KeyEvent event = new KeyEvent(action, code);
				et_chat.onKeyDown(KeyEvent.KEYCODE_DEL, event);
				displayTextView();
				return;
			}
			et_chat.getText().delete(index, text.length());
			displayTextView();
			return;
		}
		int action = KeyEvent.ACTION_DOWN;
		int code = KeyEvent.KEYCODE_DEL;
		KeyEvent event = new KeyEvent(action, code);
		et_chat.onKeyDown(KeyEvent.KEYCODE_DEL, event);
		displayTextView();
	}

	@Override
	public void onEmojiClick(Emoji emoji) {
		if (emoji != null) {
			int index = et_chat.getSelectionStart();
			Editable editable = et_chat.getEditableText();
			if (index < 0) {
				editable.append(emoji.getContent());
			} else {
				editable.insert(index, emoji.getContent());
			}
		}
		displayTextView();
	}

	private void displayTextView() {
		try {
			EmojiUtil.handlerEmojiText(et_chat, et_chat.getText().toString(), App.getContext());
		} catch (IOException e) {
			e.printStackTrace();
		}
		et_chat.setSelection(et_chat.length());
	}

	SoundLevelChangeListener levelChangeListener = new MySoundManager.SoundLevelChangeListener() {
		@Override
		public void levelChange(double signalEMA) {
			switch ((int) signalEMA) {
			case 0:
			case 1:
//				Log.wtf("XXXXXXXXXXXXX", "111111111");
				break;
			case 2:
			case 3:
//				Log.wtf("XXXXXXXXXXXXX", "222222222222");
				break;
			case 4:
			case 5:
//				Log.wtf("XXXXXXXXXXXXX", "33333333333");
				break;
			case 6:
			case 7:
//				Log.wtf("XXXXXXXXXXXXX", "444444444444");
				break;
			case 8:
			case 9:
//				Log.wtf("XXXXXXXXXXXXX", "5555555555555");
				break;
			case 10:
			case 11:
//				Log.wtf("XXXXXXXXXXXXX", "666666666666");
				break;
			default:
				break;
			}
		}
	};

	long startTime = 0L;


	private void sendSound() {
		if (chatService != null) {
			String decodeBitmap = BitmapUtil.bytesToHex(BitmapUtil.image2byte(Constant.SOUND_FILEPATH + "/lyl.arm"));
			chatService.sendMessage(mWithJabberID, decodeBitmap);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		bt_speak.setText("按住说话");
		if (mySoundManager != null) {
			mySoundManager.stop();
			// mySoundManager.cancel();
			mySoundManager = null;
		}
	}
	
}
