package com.hykj.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hykj.R;
import com.hykj.activity.usermanagement.MsgCenterActivity;
import com.hykj.entity.PushDataEntity;
import com.hykj.view.MyItemLayout;

/**
 * @author 作者 : zhaoyu
 * @version 创建时间：2016年1月19日 下午3:02:36 类说明
 */
public class MsgCenterAdapter extends BaseAdapter {
	private List<PushDataEntity> entity;
	private Context context;
	private MsgCenterActivity activity;
	public MsgCenterAdapter(List<PushDataEntity> entity, Context context,MsgCenterActivity activity) {
		super();
		this.entity = entity;
		this.context = context;
		this.activity=activity;
	}

	@Override
	public int getCount() {
		if (entity != null) {
			return entity.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	MyItemLayout finalContentView;

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder vh = null;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.item_msgcenter, null);
			vh = new ViewHolder();
			vh.tv_time = (TextView) convertView
					.findViewById(R.id.item_msg_time);
			vh.tv_point = (TextView) convertView
					.findViewById(R.id.item_msg_point);
			vh.tv_content = (TextView) convertView
					.findViewById(R.id.item_msg_content);
			vh.tv_del = (TextView) convertView
					.findViewById(R.id.item_msg_tv_del);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		finalContentView = (MyItemLayout) convertView;
		finalContentView.smoothCloseMenu();
		PushDataEntity data = entity.get(position);
		vh.tv_time.setText(data.getTime());
		vh.tv_content.setText(data.getTitle());
		if(data.getPoint()==0){
			vh.tv_point.setBackgroundResource(R.color.point);
		}else{
			vh.tv_point.setBackgroundResource(data.getPoint());
		}

		vh.tv_del.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				entity.remove(position);
				notifyDataSetChanged();
				activity.updataCount();
				
			}
		});
		return convertView;
	}

	public static class ViewHolder {
		public TextView tv_time, tv_point, tv_content, tv_del;
	

	}
}
