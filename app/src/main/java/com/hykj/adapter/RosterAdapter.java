package com.hykj.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.hykj.App;
import com.hykj.activity.chat.ChatActivity;
import com.hykj.db.RosterProvider;
import com.hykj.db.RosterProvider.RosterConstants;
import com.hykj.utils.StatusMode;

public class RosterAdapter extends BaseAdapter {

	// 不在线状态
	private static final String OFFLINE_EXCLUSION = RosterConstants.STATUS_MODE + " != " + StatusMode.offline.ordinal();
	// 在线人数
	private static final String COUNT_AVAILABLE_MEMBERS = "SELECT COUNT() FROM " + RosterProvider.TABLE_ROSTER + " inner_query" + " WHERE inner_query." + RosterConstants.GROUP + " = " + RosterProvider.QUERY_ALIAS + "." + RosterConstants.GROUP + " AND inner_query." + OFFLINE_EXCLUSION;
	// 总人数
	private static final String COUNT_MEMBERS = "SELECT COUNT() FROM " + RosterProvider.TABLE_ROSTER + " inner_query" + " WHERE inner_query." + RosterConstants.GROUP + " = " + RosterProvider.QUERY_ALIAS + "." + RosterConstants.GROUP;
	private static final String[] GROUPS_QUERY_COUNTED = new String[] { RosterConstants._ID, RosterConstants.GROUP, "(" + COUNT_AVAILABLE_MEMBERS + ") || '/' || (" + COUNT_MEMBERS + ") AS members" };
	// 联系人查询序列
	private static final String[] ROSTER_QUERY = new String[] { RosterConstants._ID, RosterConstants.JID, RosterConstants.ALIAS, RosterConstants.STATUS_MODE, RosterConstants.STATUS_MESSAGE, };

	private ContentResolver mContentResolver;
	private List<Group> mGroupList = new ArrayList<Group>();
	private List<Roster> rosters = new ArrayList<Roster>();

	ListView lv;
	Context context;

	public RosterAdapter(ListView lv, Context context) {
		this.lv = lv;
		this.context = context;
		mContentResolver = context.getContentResolver();
	}

	public void requery() {
		if (mGroupList != null && mGroupList.size() > 0)
			mGroupList.clear();
		// 是否显示在线人数
		String selectWhere = null;
		Cursor groupCursor = mContentResolver.query(RosterProvider.GROUPS_URI, GROUPS_QUERY_COUNTED, selectWhere, null, RosterConstants.GROUP);
		groupCursor.moveToFirst();
		while (!groupCursor.isAfterLast()) {
			Group group = new Group();
			group.setGroupName(groupCursor.getString(groupCursor.getColumnIndex(RosterConstants.GROUP)));
			group.setMembers(groupCursor.getString(groupCursor.getColumnIndex("members")));
			mGroupList.add(group);
			groupCursor.moveToNext();
		}

		groupCursor.close();
		notifyDataSetChanged();

		rosters.clear();
		for (Group g : mGroupList) {
			for (Roster r : getChildrenRosters(g.getGroupName())) {
				rosters.add(r);
			}
		}
	}

	protected List<Roster> getChildrenRosters(String groupname) {
		List<Roster> childList = new ArrayList<Roster>();

		String selectWhere = RosterConstants.GROUP + " = ?";

		Cursor childCursor = mContentResolver.query(RosterProvider.CONTENT_URI, ROSTER_QUERY, selectWhere, new String[] { groupname }, null);

		childCursor.moveToFirst();
		while (!childCursor.isAfterLast()) {
			Roster roster = new Roster();
			roster.setJid(childCursor.getString(childCursor.getColumnIndexOrThrow(RosterConstants.JID)));
			roster.setAlias(childCursor.getString(childCursor.getColumnIndexOrThrow(RosterConstants.ALIAS)));
			roster.setStatus_message(childCursor.getString(childCursor.getColumnIndexOrThrow(RosterConstants.STATUS_MESSAGE)));
			roster.setStatusMode(childCursor.getString(childCursor.getColumnIndexOrThrow(RosterConstants.STATUS_MODE)));
			childList.add(roster);
			childCursor.moveToNext();
		}
		childCursor.close();
		return childList;
	}

	private void startChatActivity(String userJid, String userName) {
		Intent chatIntent = new Intent(context, ChatActivity.class);
		Uri userNameUri = Uri.parse(userJid);
		chatIntent.setData(userNameUri);
		chatIntent.putExtra(ChatActivity.INTENT_EXTRA_USERNAME, userName);
		context.startActivity(chatIntent);
	}

	@Override
	public int getCount() {
		return rosters.size();
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
	public View getView(final int position, View convertView, ViewGroup parent) {

		Button button = new Button(context);
		Roster roster = rosters.get(position);
//		Log.wtf("xxxxxxxxxxxxx", ""+roster.getStatusMessage()+"");
//		Log.wtf("yyyyyyyyyy", ""+roster.getStatusMode()+"");
		int statusId = StatusMode.values()[Integer.parseInt(roster.getStatusMode())].getDrawableId();
//		String s = TextUtils.isEmpty(roster.getStatusMessage()) ? "离线" : roster.getStatusMessage();
		String s = (statusId != -1)?"离线":"";
		button.setText(roster.getAlias()+"\t"+s);

		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startChatActivity(rosters.get(position).getJid(), rosters.get(position).alias);
			}
		});

		return button;
	}

	public class Group {
		private String groupName;
		private String members;

		public String getGroupName() {
			return groupName;
		}

		public void setGroupName(String groupName) {
			this.groupName = groupName;
		}

		public String getMembers() {
			return members;
		}

		public void setMembers(String members) {
			this.members = members;
		}
	}

	public class Roster {
		private String jid;
		private String alias;
		private String statusMode;
		private String statusMessage;

		public String getJid() {
			return jid;
		}

		public void setJid(String jid) {
			this.jid = jid;
		}

		public String getAlias() {
			return alias;
		}

		public void setAlias(String alias) {
			this.alias = alias;
		}

		public String getStatusMode() {
			return statusMode;
		}

		public void setStatusMode(String statusMode) {
			this.statusMode = statusMode;
		}

		public String getStatusMessage() {
			return statusMessage;
		}

		public void setStatus_message(String statusMessage) {
			this.statusMessage = statusMessage;
		}
	}
}
