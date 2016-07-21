package com.hykj.activity.chat;

import android.app.Activity;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ListView;

import com.hykj.App;
import com.hykj.R;
import com.hykj.adapter.RosterAdapter;
import com.hykj.db.RosterProvider;

public class LylListActivity extends Activity {
	private ListView lv;

	RosterAdapter mRosterAdapter;
	
	private ContentObserver mRosterObserver = new RosterObserver();
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				updateRoster();
				break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		App.addActivity(this);
		setContentView(R.layout.activity);
		
		initView();
		
		getContentResolver().registerContentObserver(
				RosterProvider.CONTENT_URI, true, mRosterObserver);
	}

	private void initView() {
		lv = (ListView) findViewById(R.id.lv_chart_list);
		
		mRosterAdapter = new RosterAdapter(lv,LylListActivity.this);
		lv.setAdapter(mRosterAdapter);
		mRosterAdapter.requery();
	}
	
	private class RosterObserver extends ContentObserver {
		public RosterObserver() {
			super(handler);
		}
		public void onChange(boolean selfChange) {
			handler.sendEmptyMessageDelayed(1, 100);
		}
	}
	
	public void updateRoster() {
		mRosterAdapter.requery();
	}
	
}















