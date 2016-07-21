package com.hykj.activity.messure;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hykj.App;
import com.hykj.R;
import com.hykj.activity.usermanagement.MedicationremindersActivity;
import com.hykj.adapter.ImageAdapter;
import com.hykj.db.DataBaseHelper;
import com.hykj.entity.Remind;
import com.hykj.utils.MyLog;

public class AddInfoActivity extends Activity implements OnClickListener {

	private ImageView iv_bpinput;
	private ImageView iv_bsinput;
	private ImageView iv_dietcondition;
	private ImageView iv_medicationreminders;
	private ImageView iv_prescriptionupload;
	private ImageView iv_back;
	private ImageView iv_medicalrecordupload;
	private ImageHandler imageHandler = new ImageHandler(new WeakReference<AddInfoActivity>(this));
	private ViewPager viewPager;
	private ArrayList<ImageView> imageLists = new ArrayList<ImageView>();
	private ViewGroup viewGroup;
	private ImageView[] imageDots;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		App.addActivity(this);
		setContentView(R.layout.activity_addinfo);

		findViews();
		registAndSettings();
	}

	private void registAndSettings() {
		iv_bpinput.setOnClickListener(this);
		iv_bsinput.setOnClickListener(this);
		iv_dietcondition.setOnClickListener(this);
		iv_medicationreminders.setOnClickListener(this);
		iv_prescriptionupload.setOnClickListener(this);
		iv_back.setOnClickListener(this);
		iv_medicalrecordupload.setOnClickListener(this);
		iv_bpinput.setOnTouchListener(new OnTouchListener() {

			@SuppressLint("NewApi")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					iv_bsinput
							.setBackgroundResource(R.color.addinfo_bg_selected);
					iv_bsinput.setImageAlpha(100);
					iv_dietcondition
							.setBackgroundResource(R.color.addinfo_bg_selected);
					iv_dietcondition.setImageAlpha(100);
					iv_medicationreminders
							.setBackgroundResource(R.color.addinfo_bg_selected);
					iv_medicationreminders.setImageAlpha(100);
					iv_prescriptionupload
							.setBackgroundResource(R.color.addinfo_bg_selected);
					iv_prescriptionupload.setImageAlpha(100);
					iv_medicalrecordupload
							.setBackgroundResource(R.color.addinfo_bg_selected);
					iv_medicalrecordupload.setImageAlpha(100);
					break;
				case MotionEvent.ACTION_UP:
					iv_bsinput
							.setBackgroundResource(R.drawable.selector_addinfo_bg);
					iv_bsinput.setImageAlpha(255);
					iv_dietcondition
							.setBackgroundResource(R.drawable.selector_addinfo_bg);
					iv_dietcondition.setImageAlpha(255);
					iv_medicationreminders
							.setBackgroundResource(R.drawable.selector_addinfo_bg);
					iv_medicationreminders.setImageAlpha(255);
					iv_prescriptionupload
							.setBackgroundResource(R.drawable.selector_addinfo_bg);
					iv_prescriptionupload.setImageAlpha(255);
					iv_medicalrecordupload
							.setBackgroundResource(R.drawable.selector_addinfo_bg);
					iv_medicalrecordupload.setImageAlpha(255);
				default:
					break;
				}
				return false;
			}
		});
		iv_bsinput.setOnTouchListener(new OnTouchListener() {

			@SuppressLint("NewApi")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					iv_bpinput
							.setBackgroundResource(R.color.addinfo_bg_selected);
					iv_bpinput.setImageAlpha(100);
					iv_dietcondition
							.setBackgroundResource(R.color.addinfo_bg_selected);
					iv_dietcondition.setImageAlpha(100);
					iv_medicationreminders
							.setBackgroundResource(R.color.addinfo_bg_selected);
					iv_medicationreminders.setImageAlpha(100);
					iv_prescriptionupload
							.setBackgroundResource(R.color.addinfo_bg_selected);
					iv_prescriptionupload.setImageAlpha(100);
					iv_medicalrecordupload
							.setBackgroundResource(R.color.addinfo_bg_selected);
					iv_medicalrecordupload.setImageAlpha(100);
					break;
				case MotionEvent.ACTION_UP:
					iv_bpinput
							.setBackgroundResource(R.drawable.selector_addinfo_bg);
					iv_bpinput.setImageAlpha(255);
					iv_dietcondition
							.setBackgroundResource(R.drawable.selector_addinfo_bg);
					iv_dietcondition.setImageAlpha(255);
					iv_medicationreminders
							.setBackgroundResource(R.drawable.selector_addinfo_bg);
					iv_medicationreminders.setImageAlpha(255);
					iv_prescriptionupload
							.setBackgroundResource(R.drawable.selector_addinfo_bg);
					iv_prescriptionupload.setImageAlpha(255);
					iv_medicalrecordupload
							.setBackgroundResource(R.drawable.selector_addinfo_bg);
					iv_medicalrecordupload.setImageAlpha(255);
				default:
					break;
				}
				return false;
			}
		});
		iv_dietcondition.setOnTouchListener(new OnTouchListener() {

			@SuppressLint("NewApi")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					iv_bsinput
							.setBackgroundResource(R.color.addinfo_bg_selected);
					iv_bsinput.setImageAlpha(100);
					iv_bpinput
							.setBackgroundResource(R.color.addinfo_bg_selected);
					iv_bpinput.setImageAlpha(100);
					iv_medicationreminders
							.setBackgroundResource(R.color.addinfo_bg_selected);
					iv_medicationreminders.setImageAlpha(100);
					iv_prescriptionupload
							.setBackgroundResource(R.color.addinfo_bg_selected);
					iv_prescriptionupload.setImageAlpha(100);
					iv_medicalrecordupload
							.setBackgroundResource(R.color.addinfo_bg_selected);
					iv_medicalrecordupload.setImageAlpha(100);
					break;
				case MotionEvent.ACTION_UP:
					iv_bsinput
							.setBackgroundResource(R.drawable.selector_addinfo_bg);
					iv_bsinput.setImageAlpha(255);
					iv_bpinput
							.setBackgroundResource(R.drawable.selector_addinfo_bg);
					iv_bpinput.setImageAlpha(255);
					iv_medicationreminders
							.setBackgroundResource(R.drawable.selector_addinfo_bg);
					iv_medicationreminders.setImageAlpha(255);
					iv_prescriptionupload
							.setBackgroundResource(R.drawable.selector_addinfo_bg);
					iv_prescriptionupload.setImageAlpha(255);
					iv_medicalrecordupload
							.setBackgroundResource(R.drawable.selector_addinfo_bg);
					iv_medicalrecordupload.setImageAlpha(255);
				default:
					break;
				}
				return false;
			}
		});
		iv_medicationreminders.setOnTouchListener(new OnTouchListener() {

			@SuppressLint("NewApi")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					iv_bsinput
							.setBackgroundResource(R.color.addinfo_bg_selected);
					iv_bsinput.setImageAlpha(100);
					iv_dietcondition
							.setBackgroundResource(R.color.addinfo_bg_selected);
					iv_dietcondition.setImageAlpha(100);
					iv_bpinput
							.setBackgroundResource(R.color.addinfo_bg_selected);
					iv_bpinput.setImageAlpha(100);
					iv_prescriptionupload
							.setBackgroundResource(R.color.addinfo_bg_selected);
					iv_prescriptionupload.setImageAlpha(100);
					iv_medicalrecordupload
							.setBackgroundResource(R.color.addinfo_bg_selected);
					iv_medicalrecordupload.setImageAlpha(100);
					break;
				case MotionEvent.ACTION_UP:
					iv_bsinput
							.setBackgroundResource(R.drawable.selector_addinfo_bg);
					iv_bsinput.setImageAlpha(255);
					iv_dietcondition
							.setBackgroundResource(R.drawable.selector_addinfo_bg);
					iv_dietcondition.setImageAlpha(255);
					iv_bpinput
							.setBackgroundResource(R.drawable.selector_addinfo_bg);
					iv_bpinput.setImageAlpha(255);
					iv_prescriptionupload
							.setBackgroundResource(R.drawable.selector_addinfo_bg);
					iv_prescriptionupload.setImageAlpha(255);
					iv_medicalrecordupload
							.setBackgroundResource(R.drawable.selector_addinfo_bg);
					iv_medicalrecordupload.setImageAlpha(255);
				default:
					break;
				}
				return false;
			}
		});
		iv_prescriptionupload.setOnTouchListener(new OnTouchListener() {

			@SuppressLint("NewApi")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					iv_bsinput
							.setBackgroundResource(R.color.addinfo_bg_selected);
					iv_bsinput.setImageAlpha(100);
					iv_dietcondition
							.setBackgroundResource(R.color.addinfo_bg_selected);
					iv_dietcondition.setImageAlpha(100);
					iv_medicationreminders
							.setBackgroundResource(R.color.addinfo_bg_selected);
					iv_medicationreminders.setImageAlpha(100);
					iv_bpinput
							.setBackgroundResource(R.color.addinfo_bg_selected);
					iv_bpinput.setImageAlpha(100);
					iv_medicalrecordupload
							.setBackgroundResource(R.color.addinfo_bg_selected);
					iv_medicalrecordupload.setImageAlpha(100);
					break;
				case MotionEvent.ACTION_UP:
					iv_bsinput
							.setBackgroundResource(R.drawable.selector_addinfo_bg);
					iv_bsinput.setImageAlpha(255);
					iv_dietcondition
							.setBackgroundResource(R.drawable.selector_addinfo_bg);
					iv_dietcondition.setImageAlpha(255);
					iv_medicationreminders
							.setBackgroundResource(R.drawable.selector_addinfo_bg);
					iv_medicationreminders.setImageAlpha(255);
					iv_bpinput
							.setBackgroundResource(R.drawable.selector_addinfo_bg);
					iv_bpinput.setImageAlpha(255);
					iv_medicalrecordupload
							.setBackgroundResource(R.drawable.selector_addinfo_bg);
					iv_medicalrecordupload.setImageAlpha(255);
				default:
					break;
				}
				return false;
			}
		});
		iv_medicalrecordupload.setOnTouchListener(new OnTouchListener() {

			@SuppressLint("NewApi")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
//					Log.wtf("aaa", "ACTION_DOWN");
					iv_bsinput
							.setBackgroundResource(R.color.addinfo_bg_selected);
					iv_bsinput.setImageAlpha(200);
					iv_dietcondition
							.setBackgroundResource(R.color.addinfo_bg_selected);
					iv_dietcondition.setImageAlpha(200);
					iv_medicationreminders
							.setBackgroundResource(R.color.addinfo_bg_selected);
					iv_medicationreminders.setImageAlpha(200);
					iv_prescriptionupload
							.setBackgroundResource(R.color.addinfo_bg_selected);
					iv_prescriptionupload.setImageAlpha(200);
					iv_bpinput
							.setBackgroundResource(R.color.addinfo_bg_selected);
					iv_bpinput.setImageAlpha(200);
					break;
				case MotionEvent.ACTION_UP:
//					Log.wtf("aaa", "ACTION_UP");
					iv_bsinput
							.setBackgroundResource(R.drawable.selector_addinfo_bg);
					iv_bsinput.setImageAlpha(255);
					iv_dietcondition
							.setBackgroundResource(R.drawable.selector_addinfo_bg);
					iv_dietcondition.setImageAlpha(255);
					iv_medicationreminders
							.setBackgroundResource(R.drawable.selector_addinfo_bg);
					iv_medicationreminders.setImageAlpha(255);
					iv_prescriptionupload
							.setBackgroundResource(R.drawable.selector_addinfo_bg);
					iv_prescriptionupload.setImageAlpha(255);
					iv_bpinput
							.setBackgroundResource(R.drawable.selector_addinfo_bg);
					iv_bpinput.setImageAlpha(255);
				default:
					break;
				}
				return false;
			}
		});
	}

	private void findViews() {
		iv_bpinput = (ImageView) findViewById(R.id.iv_addinfo_bpinput);
		iv_bsinput = (ImageView) findViewById(R.id.iv_addinfo_bsinput);
		iv_dietcondition = (ImageView) findViewById(R.id.iv_addinfo_dietcondition);
		iv_medicationreminders = (ImageView) findViewById(R.id.iv_addinfo_medicationreminders);
		iv_prescriptionupload = (ImageView) findViewById(R.id.iv_addinfo_prescriptionupload);
		iv_back = (ImageView) findViewById(R.id.iv_addinfo_back);
		iv_medicalrecordupload = (ImageView) findViewById(R.id.iv_addinfo_medicalrecordupload);

		viewPager = (ViewPager) findViewById(R.id.addinfo_viewpager);
		viewGroup = (ViewGroup) findViewById(R.id.addinfo_point_group);
		viewGroup.getBackground().setAlpha(200);
		ImageView image1 = new ImageView(this);
		image1.setScaleType(ImageView.ScaleType.FIT_XY);
		image1.setImageResource(R.drawable.iv_addinfo_banner1);
		ImageView image2 = new ImageView(this);
		image2.setScaleType(ImageView.ScaleType.FIT_XY);
		image2.setImageResource(R.drawable.iv_addinfo_banner2);
		ImageView image3 = new ImageView(this);
		image3.setScaleType(ImageView.ScaleType.FIT_XY);
		image3.setImageResource(R.drawable.iv_addinfo_banner3);

		imageLists.add(image1);
		imageLists.add(image2);
		imageLists.add(image3);
		viewGroup.removeAllViews();
		imageDots = new ImageView[imageLists.size()];
		for (int i = 0; i < imageDots.length; i++) {
			imageDots[i] = new ImageView(this);
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
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.iv_addinfo_bpinput:
			Intent bpinputIntent = new Intent(this,
					AddBloodPressureActivity.class);
			startActivity(bpinputIntent);
			break;
		case R.id.iv_addinfo_bsinput:
			Intent bsinputIntent = new Intent(this, AddBloodSugarActivity.class);
			startActivity(bsinputIntent);
			break;
		case R.id.iv_addinfo_back:
			AddInfoActivity.this.finish();
			break;
		case R.id.iv_addinfo_medicationreminders:
			getDataFromDb();
			break;
		case R.id.iv_addinfo_dietcondition:
			Intent dietIntent=new Intent(this, DietConditionActivity.class);
			startActivity(dietIntent);
			break;
		case R.id.iv_addinfo_medicalrecordupload:
			startActivity(new Intent(this, UploadMedicalRecordActivity.class));
			break;
		case R.id.iv_addinfo_prescriptionupload:
			startActivity(new Intent(this, SportConditionActivity.class));

			break;
		}
	}

	DataBaseHelper db;
	List<Remind> data;

	private void getDataFromDb() {
		if (db == null) {
			db = new DataBaseHelper(this);
		}
		data = new ArrayList<Remind>();
		SQLiteDatabase base = db.getReadableDatabase();
		Cursor c = base.rawQuery("select * from remind where userId=?",
				new String[] { String.valueOf(App.USERID) });
		while (c.moveToNext()) {
			String type = c.getString(2);
			if (type.equals("1")) {
				type = "用药提醒";
			}
			if (type.equals("2")) {
				type = "血压提醒";
			}
			if (type.equals("3")) {
				type = "血糖提醒";
			}
			if (type.equals("4")) {
				type = "运动提醒";
			}
			String contents = c.getString(3);
			String time = c.getString(4);
			String repeat = c.getString(5);
			String islocked = c.getString(6);
			data.add(new Remind(type, contents, time, repeat, islocked));
		}
		c.close();
		base.close();
		Intent medicationintent = new Intent(this,
				MedicationremindersActivity.class);
		if (data != null) {
			medicationintent.putExtra("data", (Serializable) data);
		}

		startActivity(medicationintent);
	}
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
		private WeakReference<AddInfoActivity> weakReference;
		private int currentItem = 0;

		protected ImageHandler(WeakReference<AddInfoActivity> weakReference) {
			this.weakReference = weakReference;
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			AddInfoActivity addInfoActivity = weakReference.get();
			if (addInfoActivity == null) {
				return;
			}
			//检查消息队列并移除未发送的消息，这主要是避免在复杂环境下消息出现重复等问题
			if (addInfoActivity.imageHandler.hasMessages(MSG_UPDATE_IMAGE)) {
				addInfoActivity.imageHandler.removeMessages(MSG_UPDATE_IMAGE);
			}
			switch (msg.what) {
				case MSG_UPDATE_IMAGE:
					currentItem++;
					addInfoActivity.viewPager.setCurrentItem(currentItem);
					//准备下次播放
					addInfoActivity.imageHandler.sendEmptyMessageDelayed(MSG_UPDATE_IMAGE, MSG_DELAY);
					break;
				case MSG_KEEP_SILENT:
					//只要不发送消息就暂停
					break;
				case MSG_BREAK_SILENT:
					addInfoActivity.imageHandler.sendEmptyMessageDelayed(MSG_UPDATE_IMAGE, MSG_DELAY);
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
