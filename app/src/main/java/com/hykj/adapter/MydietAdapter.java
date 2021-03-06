package com.hykj.adapter;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hykj.R;
import com.hykj.entity.Dietcondition;

/**
 * @author 作者 : zhaoyu
 * @version 创建时间：2016年4月13日 上午11:35:46 类说明：我的饮食
 */
public class MydietAdapter extends BaseAdapter {
	private Context context;
	private List<Dietcondition> data;
	private LayoutInflater inflater;

	public MydietAdapter(Context context, List<Dietcondition> data) {
		super();
		this.context = context;
		this.data = data;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
//		Log.wtf("bbb", data.size() + "");
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_lv_mydiet, null);
			vh = new ViewHolder();
			vh.tv_type = (TextView) convertView
					.findViewById(R.id.mydiet_tv_type);
			vh.tv_describe = (TextView) convertView
					.findViewById(R.id.mydiet_tv_describe);
			vh.tv_time = (TextView) convertView
					.findViewById(R.id.mydiet_tv_time);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		vh.tv_type.setText(data.get(position).getType());
//		Log.wtf("bbb", data.get(position).getType());
		vh.tv_describe.setText(data.get(position).getDescribe());
		vh.tv_time.setText(data.get(position).getDate());
		return convertView;
	}

	public class ViewHolder {
		private TextView tv_type, tv_describe, tv_time;

	}
}
