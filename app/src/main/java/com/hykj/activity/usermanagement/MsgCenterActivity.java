package com.hykj.activity.usermanagement;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.hykj.R;
import com.hykj.activity.BaseActivity;
import com.hykj.adapter.MsgCenterAdapter;
import com.hykj.entity.PushDataEntity;
import com.hykj.view.DeleteListView;

/**
 * @author 作者 : zhaoyu
 * @version 创建时间：2016年1月19日 上午11:27:49 类说明：消息中心
 */
public class MsgCenterActivity extends BaseActivity {
	private TextView mTv_unread, mTv_read;
	private ImageView mTv_back;
	private DeleteListView mLv;
	private List<PushDataEntity> data;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private MsgCenterAdapter adapter;

	@Override
	public void init() {
		setContentView(R.layout.activity_msgcenter);

		initData();
		initViews();

	}

	private void initViews() {
		mTv_back = (ImageView) findViewById(R.id.msg_tv_back);
		mTv_unread = (TextView) findViewById(R.id.msg_tv_unread);
		mTv_read = (TextView) findViewById(R.id.msg_tv_read);

		mLv = (DeleteListView) findViewById(R.id.msg_lv);
		mTv_back.setOnClickListener(this);
		mTv_read.setOnClickListener(this);
		adapter = new MsgCenterAdapter(data, this, this);
		mLv.setAdapter(adapter);
		updataCount();

		mLv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				data.get(position).setPoint(0);
				data.get(position).setCount(0);
				adapter.notifyDataSetChanged();
				updataCount();
				Intent intent = new Intent(getApplicationContext(),
						PushInfoDetialActivity.class);
				intent.putExtra("title", data.get(position).getTitle());
				intent.putExtra("url", data.get(position).getUrl());
				intent.putExtra("contents", data.get(position).getContents());
				MsgCenterActivity.this.startActivity(intent);
			}
		});
		mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.msg_swiperflayout);
		mSwipeRefreshLayout.setProgressViewEndTarget(true, 120);
		mSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				data.add(new PushDataEntity(
						"http://img.my.csdn.net/uploads/201508/05/1438760758_3497.jpg",
						" Android中使用4个数字来表示颜色，分别是alpha、红(red)、绿(green)、蓝(blue)四个颜色值(ARGB)。每个数字取值0-255，因此一个颜色可以用一个整数来表示。为了运行效率，Android编码时用整数Color类实例来表示颜色",
						"颜色", "2015.1.20", R.drawable.point, 1));

				adapter.notifyDataSetChanged();
				mSwipeRefreshLayout.setRefreshing(false);

				updataCount();

			}
		});

	}

	/*
	 * 更新未读数据
	 */

	public void updataCount() {
		int count = 0;
		for (PushDataEntity one_data : data) {
			int one_count = one_data.getCount();
			if (one_count == 1) {
				count = one_count + count;
			}
		}

		if (count == 0) {
			mTv_unread.setText("无未读消息");
		} else {
			mTv_unread.setText("未读" + "(" + count + ")");
		}
	}

	private void initData() {
		data = new ArrayList<PushDataEntity>();
		data.add(new PushDataEntity(
				"http://img.my.csdn.net/uploads/201508/05/1438760758_3497.jpg",
				"高血压患者必读》内容简介：60个问答让高血压患者远离高血压的困扰；精确而详细地解答了高血压患者最应知道的问题； 通俗而生动的语言让您一看就懂； 实用且有效的方法让高血压患者能够有效管理自己的血压。天津医科大学总医院、三中心医院和塘沽区中医院有关专家在工作之余将自己多年来有关高血压防治的知识和经验汇集成册，奉献给高血压患者和热心读者。",
				"高血压必读", "2015.1.19", R.drawable.point, 1));
		data.add(new PushDataEntity(
				"http://img.my.csdn.net/uploads/201508/05/1438760758_3497.jpg",
				"内容包括糖尿病的概念和发病机理、糖尿病的分型与病因、糖尿病急性并发症、糖尿病慢性并发症、糖尿病的防治原则、糖尿病的饮食疗法、糖尿病的过动疗法、糖尿病的药物治疗、糖尿病的监测、糖尿病与日常生活、糖尿病的三级预防、糖尿病知识回答、糖尿病病例分析",
				"糖尿病必读", "2015.1.15", R.drawable.point, 1));
		data.add(new PushDataEntity(
				"http://img.my.csdn.net/uploads/201508/05/1438760758_3497.jpg",
				"天衡高血压糖尿病专科是专业治疗高血压和糖尿病的专科医院，为患者提供个性化的治疗方案和服务，是您的家庭医生。",
				"天衡高血压糖尿病专科", "2015.1.3", R.drawable.point, 1));
		data.add(new PushDataEntity(
				"http://img.my.csdn.net/uploads/201508/05/1438760758_3497.jpg",
				"衡云科技是一家新成立的从事移动医疗开发o2o科技公司。公司是由天衡医院管理有限公司控股的科技公司。天衡医院管理有限公司是一家全国连锁的高血压,糖尿病专业慢病管理集团,在北京建有高血压糖尿病研究中心,目前在全国开设分院26家,服务患者8万多人",
				"北京衡云科技有限公司", "2015.1.8", R.drawable.point, 1));

	}

	@Override
	public void click(View v) {
		switch (v.getId()) {
		case R.id.msg_tv_back:
			finish();
			break;
		case R.id.msg_tv_read:
			for (PushDataEntity one_data : data) {
				one_data.setPoint(0);
				one_data.setCount(0);
			}
			adapter.notifyDataSetChanged();
			updataCount();
			mTv_unread.setText("无未读消息");
			break;

		default:
			break;
		}
	}

}
