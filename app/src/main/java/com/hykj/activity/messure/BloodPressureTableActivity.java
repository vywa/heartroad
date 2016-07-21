package com.hykj.activity.messure;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hykj.App;
import com.hykj.R;
import com.hykj.entity.BloodPressureInfo;
import com.hykj.utils.TimeUtil;

public class BloodPressureTableActivity extends Activity {

	private ListView lv;
	ArrayList<BloodPressureInfo> pressureInfos = new ArrayList<BloodPressureInfo>();
	long startTime;
	long endTime;
	private TextView tv_start;
	private TextView tv_end;
	private ImageView iv_back;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		App.addActivity(this);
		setContentView(R.layout.activity_bloodpreesurehistory);
		
		lv = (ListView) findViewById(R.id.lv_bsh);
		tv_start = (TextView) findViewById(R.id.tv_start);
		tv_end = (TextView) findViewById(R.id.tv_end);
		iv_back = (ImageView) findViewById(R.id.iv_title_back);
		
		iv_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				BloodPressureTableActivity.this.finish();
			}
		});
		
		display();
	}

	private void display() {
		Intent intent = getIntent();
		pressureInfos = (ArrayList<BloodPressureInfo>) intent.getSerializableExtra("pressureInfos");
		
		startTime = intent.getLongExtra("startTime", 0);
		tv_start.setText(TimeUtil.getStringTime(startTime).substring(0, 11));
		endTime = intent.getLongExtra("endTime", 0);
		tv_end.setText(TimeUtil.getStringTime(endTime).substring(0, 11));
		
		lv.setAdapter(new MyBPHistoryAdapter());
	}
	
	class MyBPHistoryAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			return pressureInfos.size();
		}
		@Override
		public Object getItem(int position) {
			return null;
		}
		@Override
		public long getItemId(int position) {
			return 0;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			MyBPHistoryAdapterHolder holder;
			if (convertView == null) {
				convertView = View.inflate(App.getContext(), R.layout.activity_bloodpreesurehistory_item, null);
				holder = new MyBPHistoryAdapterHolder(convertView);
				convertView.setTag(holder);
			}else{
				holder = (MyBPHistoryAdapterHolder) convertView.getTag();
			}
			BloodPressureInfo info = pressureInfos.get(position);
			String time = TimeUtil.getStringTime(info.getMeasureTime());
			time = time.substring(5, time.length());
			holder.time.setText(time);
			holder.highBP.setText(info.getHighBP()+"");
			holder.lowBP.setText(info.getLowBP()+"");
			holder.heartRate.setText(info.getHeartRate()+"");
			return convertView;
		}
	}
	
	class MyBPHistoryAdapterHolder{
		TextView time,highBP,lowBP,heartRate;
		
		public MyBPHistoryAdapterHolder(View view) {
			time = (TextView) view.findViewById(R.id.tv_bsh_time);
			highBP = (TextView) view.findViewById(R.id.tv_bsh_high);
			lowBP = (TextView) view.findViewById(R.id.tv_bsh_low);
			heartRate = (TextView) view.findViewById(R.id.tv_bsh_heart);
		}
	}
}














