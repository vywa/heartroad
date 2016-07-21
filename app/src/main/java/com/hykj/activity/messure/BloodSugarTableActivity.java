package com.hykj.activity.messure;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hykj.App;
import com.hykj.R;
import com.hykj.entity.BloodSugarInfo;
import com.hykj.utils.TimeUtil;

public class BloodSugarTableActivity extends Activity {

	private ListView lv;
	ArrayList<BloodSugarInfo> sugarInfos = new ArrayList<BloodSugarInfo>();
	long startTime;
	long endTime;
	private TextView tv_start;
	private TextView tv_end;
	private ImageView iv_back;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		App.addActivity(this);
		setContentView(R.layout.activity_bloodsugarhistory);
		
		lv = (ListView) findViewById(R.id.lv_bsh);
		
		tv_start = (TextView) findViewById(R.id.tv_start);
		tv_end = (TextView) findViewById(R.id.tv_end);
		iv_back = (ImageView) findViewById(R.id.iv_title_back);
		
		iv_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				BloodSugarTableActivity.this.finish();
			}
		});
		
		display();
	}

	private void display() {
		Intent intent = getIntent();
		sugarInfos = (ArrayList<BloodSugarInfo>) intent.getSerializableExtra("sugarInfos");
		
		startTime = intent.getLongExtra("startTime", 0);
		tv_start.setText(TimeUtil.getStringTime(startTime).substring(0, 11));
		endTime = intent.getLongExtra("endTime", 0);
		tv_end.setText(TimeUtil.getStringTime(endTime).substring(0, 11));
		
		lv.setAdapter(new MyBPHistoryAdapter());
	}
	
	class MyBPHistoryAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return sugarInfos.size();
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
				convertView = View.inflate(App.getContext(), R.layout.activity_bloodsugarhis_item, null);
				holder = new MyBPHistoryAdapterHolder(convertView);
				convertView.setTag(holder);
			}else{
				holder = (MyBPHistoryAdapterHolder) convertView.getTag();
			}
			BloodSugarInfo info = sugarInfos.get(position);
			
			String time = TimeUtil.getStringTime(info.getMeasureTime());
			time = time.substring(5, time.length());
			holder.time.setText(time);
			
			switch (info.getMeasureType()) {
			case 0:
				holder.value0.setText(info.getBsValue()+"");
				holder.value1.setText("");
				holder.value2.setText("");
				holder.value3.setText("");
				holder.value4.setText("");
				holder.value5.setText("");
				holder.value6.setText("");
				holder.value7.setText("");
				holder.value8.setText("");
				break;
			case 1:
				holder.value1.setText(info.getBsValue()+"");
				holder.value0.setText("");
				holder.value2.setText("");
				holder.value3.setText("");
				holder.value4.setText("");
				holder.value5.setText("");
				holder.value6.setText("");
				holder.value7.setText("");
				holder.value8.setText("");
				break;
			case 2:
				holder.value2.setText(info.getBsValue()+"");
				holder.value1.setText("");
				holder.value0.setText("");
				holder.value3.setText("");
				holder.value4.setText("");
				holder.value5.setText("");
				holder.value6.setText("");
				holder.value7.setText("");
				holder.value8.setText("");
				break;
			case 3:
				holder.value3.setText(info.getBsValue()+"");
				holder.value1.setText("");
				holder.value2.setText("");
				holder.value0.setText("");
				holder.value4.setText("");
				holder.value5.setText("");
				holder.value6.setText("");
				holder.value7.setText("");
				holder.value8.setText("");
				break;
			case 4:
				holder.value4.setText(info.getBsValue()+"");
				holder.value1.setText("");
				holder.value2.setText("");
				holder.value3.setText("");
				holder.value0.setText("");
				holder.value5.setText("");
				holder.value6.setText("");
				holder.value7.setText("");
				holder.value8.setText("");
				break;
			case 5:
				holder.value5.setText(info.getBsValue()+"");
				holder.value1.setText("");
				holder.value2.setText("");
				holder.value3.setText("");
				holder.value4.setText("");
				holder.value0.setText("");
				holder.value6.setText("");
				holder.value7.setText("");
				holder.value8.setText("");
				break;
			case 6:
				holder.value6.setText(info.getBsValue()+"");
				holder.value1.setText("");
				holder.value2.setText("");
				holder.value3.setText("");
				holder.value4.setText("");
				holder.value5.setText("");
				holder.value0.setText("");
				holder.value7.setText("");
				holder.value8.setText("");
				break;
			case 7:
				holder.value7.setText(info.getBsValue()+"");
				holder.value1.setText("");
				holder.value2.setText("");
				holder.value3.setText("");
				holder.value4.setText("");
				holder.value5.setText("");
				holder.value6.setText("");
				holder.value0.setText("");
				holder.value8.setText("");
				break;
			case 8:
				holder.value8.setText(info.getBsValue()+"");
				holder.value1.setText("");
				holder.value2.setText("");
				holder.value3.setText("");
				holder.value4.setText("");
				holder.value5.setText("");
				holder.value6.setText("");
				holder.value7.setText("");
				holder.value0.setText("");
				break;
			}
			return convertView;
		}
	}
	
	class MyBPHistoryAdapterHolder{
		TextView time,value1,value2,value3,value4,value5,value6,value7,value8,value0;
		
		public MyBPHistoryAdapterHolder(View view) {
			time = (TextView) view.findViewById(R.id.tv_bsh_time);
			value0 = (TextView) view.findViewById(R.id.tv_bsh_1);
			value1 = (TextView) view.findViewById(R.id.tv_bsh_2);
			value2 = (TextView) view.findViewById(R.id.tv_bsh_3);
			value3 = (TextView) view.findViewById(R.id.tv_bsh_4);
			value4 = (TextView) view.findViewById(R.id.tv_bsh_5);
			value5 = (TextView) view.findViewById(R.id.tv_bsh_6);
			value6 = (TextView) view.findViewById(R.id.tv_bsh_7);
			value7 = (TextView) view.findViewById(R.id.tv_bsh_8);
			value8 = (TextView) view.findViewById(R.id.tv_bsh_9);
		}
	}
}














