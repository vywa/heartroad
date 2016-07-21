package com.hykj.fragment.usermanagement;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.drawee.view.SimpleDraweeView;
import com.gc.materialdesign.widgets.Dialog;
import com.hykj.App;
import com.hykj.Constant;
import com.hykj.R;
import com.hykj.activity.chat.ChatActivity;
import com.hykj.activity.usermanagement.BindUserActivity;
import com.hykj.activity.usermanagement.HospitalList;
import com.hykj.activity.usermanagement.MainActivity;
import com.hykj.db.ChatProvider;
import com.hykj.db.ChatProvider.ChatConstants;
import com.hykj.db.DataBaseHelper;
import com.hykj.db.RosterProvider;
import com.hykj.db.RosterProvider.RosterConstants;
import com.hykj.entity.Doctor;
import com.hykj.utils.StatusMode;

import org.json.JSONException;
import org.json.JSONObject;

public class MainChatFragment extends Fragment implements OnClickListener {

	private String alias;
	private String userJid;
	private String subscribe;

	private View v;
	private ImageView iv_search;
	private Button mBtn_mydoctor;
	private ContentResolver resolver;
	private SimpleDraweeView iv_docIcon;

	private ContentObserver mChatObserver = new ChatObserver();
	private ContentObserver mRosterObserver = new RosterObserver();

	private static final String OFFLINE_EXCLUSION = RosterConstants.STATUS_MODE + " != " + StatusMode.offline.ordinal();

	private static final String COUNT_MEMBERS = "SELECT COUNT() FROM " + RosterProvider.TABLE_ROSTER + " inner_query" + " WHERE inner_query." + RosterConstants.GROUP + " = "
			+ RosterProvider.QUERY_ALIAS + "." + RosterConstants.GROUP;

	private static final String COUNT_AVAILABLE_MEMBERS = "SELECT COUNT() FROM " + RosterProvider.TABLE_ROSTER + " inner_query" + " WHERE inner_query." + RosterConstants.GROUP + " = "
			+ RosterProvider.QUERY_ALIAS + "." + RosterConstants.GROUP + " AND inner_query." + OFFLINE_EXCLUSION;

	private static final String[] GROUPS_QUERY_COUNTED = new String[]{RosterConstants._ID, RosterConstants.GROUP,
			"(" + COUNT_AVAILABLE_MEMBERS + ") || '/' || (" + COUNT_MEMBERS + ") AS members"};

	private static final String[] ROSTER_QUERY = new String[]{RosterConstants._ID, RosterConstants.JID, RosterConstants.ALIAS, RosterConstants.STATUS_MODE, RosterConstants.STATUS_MESSAGE,
			RosterConstants.SUBSCRIBE, RosterConstants.USERID};

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case 1:
					updateRoster();
					break;
				case 2:
					updateMessage();
					break;
				case -1:
					tv_find.setText(doctor.getName());
					iv_docIcon.setVisibility(View.VISIBLE);
					iv_search.setVisibility(View.GONE);
//					Picasso.with(App.getContext()).load(doctor.getIconUrl()).into(iv_docIcon);
					iv_docIcon.setImageURI(Uri.parse(doctor.getIconUrl()));
					break;
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		v = inflater.inflate(R.layout.fragment_main_chat, null);
		init();
		return v;
	}

	private void init() {

		mBtn_mydoctor = (Button) v.findViewById(R.id.main_btn_mydoctor);
		tv_status = (TextView) v.findViewById(R.id.main_tv_chat);
		tv_find = (TextView) v.findViewById(R.id.main_tv_find);
		mBtn_mydoctor.setOnClickListener(this);

		iv_docIcon = (SimpleDraweeView) v.findViewById(R.id.iv_docicon);
		iv_search = (ImageView) v.findViewById(R.id.iv_search);

		v.findViewById(R.id.iv_docicon);

		resolver = getActivity().getContentResolver();
		resolver.registerContentObserver(RosterProvider.CONTENT_URI, true, mRosterObserver);
		updateRoster();

		resolver.registerContentObserver(ChatProvider.CONTENT_URI, true, mChatObserver);
		updateMessage();

		mBtn_mydoctor.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View view) {
				if (!TextUtils.isEmpty(userJid)) {
					final Dialog dialog = new com.gc.materialdesign.widgets.Dialog(MainChatFragment.this.getActivity(),
							"信息", "确定要与医生解除关系？");
					dialog.setOnAcceptButtonClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							((MainActivity) getActivity()).delFriend(userJid);
						}
					});
					dialog.addCancelButton("取消", new OnClickListener() {
						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});
					dialog.show();

					return true;
				} else {
					return false;
				}
			}
		});
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
			case R.id.main_btn_mydoctor:
				if (!TextUtils.isEmpty(userJid)) {
					if (!"none".equals(subscribe)) {
						Intent chatIntent = new Intent(App.getContext(), ChatActivity.class);
						Uri userNameUri = Uri.parse(userJid);
						chatIntent.setData(userNameUri);
						chatIntent.putExtra(ChatActivity.INTENT_EXTRA_USERNAME, alias);
//                        chatIntent.putExtra("doctor", doctor);
						App.doctor = doctor;
						getActivity().startActivity(chatIntent);
					}
				} else {
					// TODO
					if (!TextUtils.isEmpty(App.PHONE)) {
						Intent intent = new Intent(getActivity(), HospitalList.class);
						getActivity().startActivity(intent);
					} else {
						final Dialog dialog = new com.gc.materialdesign.widgets.Dialog(MainChatFragment.this.getActivity(), null, "先绑定手机号才能选择医生");
						dialog.setOnAcceptButtonClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								Intent intent = new Intent(getActivity(), BindUserActivity.class);
								getActivity().startActivity(intent);
								dialog.dismiss();
							}
						});
						dialog.addCancelButton("取消", new OnClickListener() {
							@Override
							public void onClick(View v) {
								dialog.dismiss();
							}
						});
						dialog.show();
					}
				}
				break;
		}
	}

	DataBaseHelper helper;

	private boolean bindPhone() {
		if (helper == null) {
			helper = new DataBaseHelper(App.getContext());
		}
		SQLiteDatabase database = helper.getWritableDatabase();
		Cursor cursor = database.query("patient", new String[]{"phonenumber"}, "userId = " + App.USERID, null, null, null, null);
		boolean flag = cursor.moveToNext();
		if (flag) {
			String phone = cursor.getString(0);
			flag = !TextUtils.isEmpty(phone);
		}
		cursor.close();
		database.close();
		helper.close();

		return flag;
	}

	private class RosterObserver extends ContentObserver {
		public RosterObserver() {
			super(handler);
		}

		public void onChange(boolean selfChange) {
			// handler.sendEmptyMessageDelayed(1, 1000);
			handler.sendEmptyMessage(1);
		}
	}

	private class ChatObserver extends ContentObserver {
		public ChatObserver() {
			super(handler);
		}

		public void onChange(boolean selfChange) {
			// handler.sendEmptyMessageDelayed(2, 1000);
			handler.sendEmptyMessage(2);
		}
	}

	public void updateRoster() {
		Cursor cursor = null;
		try {
			cursor = resolver.query(RosterProvider.GROUPS_URI, GROUPS_QUERY_COUNTED, null, null, RosterConstants.GROUP);
			userJid = null;
			subscribe = null;
			if (cursor.moveToNext()) {

				String groupName = cursor.getString(cursor.getColumnIndex(RosterConstants.GROUP));

				String selectWhere = RosterConstants.GROUP + " = ? AND " + RosterConstants.USERID + " = ?";
				Cursor childCursor = null;
				try {
					childCursor = resolver.query(RosterProvider.CONTENT_URI, ROSTER_QUERY, selectWhere, new String[]{groupName, App.USERID + ""}, null);
					if (childCursor.moveToNext()) {
						userJid = childCursor.getString(childCursor.getColumnIndexOrThrow(RosterConstants.JID));
						alias = childCursor.getString(childCursor.getColumnIndexOrThrow(RosterConstants.ALIAS));
						subscribe = childCursor.getString(childCursor.getColumnIndexOrThrow(RosterConstants.SUBSCRIBE));

						int statusMode = Integer.parseInt(childCursor.getString(childCursor.getColumnIndexOrThrow(RosterConstants.STATUS_MODE)));

						String string = mBtn_mydoctor.getText().toString();
						int start = string.indexOf("(");
						int end = string.indexOf(")");

						if (statusMode == 0) {
							mBtn_mydoctor.setBackgroundResource(R.drawable.bg_black_button);
							if (start != -1 && end != -1) {
								mBtn_mydoctor.setText(string.substring(0, start) + "(离线)");
							} else {
								mBtn_mydoctor.setText(string + "(离线)");
							}
						} else {
							mBtn_mydoctor.setBackgroundResource(R.drawable.selector_bg_redbutton);
							if (start != -1 && end != -1) {
								mBtn_mydoctor.setText(string.substring(0, start) + "(在线)");
							} else {
								mBtn_mydoctor.setText(string + "(在线)");
							}
						}
						if (TextUtils.isEmpty(doctor.getName())) {
							getDocInfo(alias);
						}
						tv_status.setText("向天衡的医生提问");
					} else {
						tv_status.setText("您还没有向天衡的医生提问过");
						tv_find.setText("去寻找您的专属医生");
						mBtn_mydoctor.setBackgroundResource(R.drawable.selector_bg_redbutton);
						iv_docIcon.setVisibility(View.GONE);
						iv_search.setVisibility(View.VISIBLE);
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (childCursor != null) {
						childCursor.close();
					}
				}
			} else {
				tv_status.setText("您还没有向天衡的医生提问过");
				tv_find.setText("去寻找您的专属医生");
				mBtn_mydoctor.setBackgroundResource(R.drawable.selector_bg_redbutton);
				iv_docIcon.setVisibility(View.GONE);
				iv_search.setVisibility(View.VISIBLE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	StringBuilder url;

	private void getDocInfo(String s) {

		url = new StringBuilder(App.BASE + Constant.GET_PERSON_INFO);
		url.append("tocken=" + App.TOKEN);
		url.append("&data=");
		JSONObject object = new JSONObject();
		try {
			object.put("userId", s);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		url.append(object.toString());
//		Log.wtf("用户列表请求url", url.toString());
		App.getRequestQueue().add(getreqFreshening());
	}


	private Doctor doctor = new Doctor();

	StringRequest getreqFreshening() {
		return new StringRequest(Method.GET, url.toString(), new Listener<String>() {

			@Override
			public void onResponse(String res) {
//				Log.wtf("用户列表返回结果", res);
				try {
					JSONObject jsonObject = new JSONObject(res);
					String code = jsonObject.optString("code", "");
					if ("206".equals(code)) {
						JSONObject object = jsonObject.optJSONObject("doctorInfo");
						doctor = new Doctor();
						doctor.setUserId(object.optInt("userId"));
						doctor.setSex(object.optString("sex"));
						doctor.setPrifile(object.optString("resume"));
						doctor.setName(object.optString("trueName"));
						doctor.setIconUrl(object.optString("iconUrl", App.DEFULT_PHOTO));
						doctor.setHospitalName(object.optString("shortName"));
						doctor.setAge(object.optString("age"));
						doctor.setFriend(true);
						handler.sendEmptyMessage(-1);
					} else {
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
			}
		});
	}

	private static final String[] FROM = new String[]{ChatProvider.ChatConstants._ID, ChatProvider.ChatConstants.DATE, ChatProvider.ChatConstants.DIRECTION,
			ChatProvider.ChatConstants.JID, ChatProvider.ChatConstants.MESSAGE, ChatProvider.ChatConstants.DELIVERY_STATUS};// 查询字段
	private static final String SELECT = ChatConstants.DATE + " in (select max(" + ChatConstants.DATE + ") from " + ChatProvider.TABLE_NAME + " group by " + ChatConstants.JID
			+ " having count(*)>0)";// 查询合并重复jid字段的所有聊天对象
	private static final String SORT_ORDER = ChatConstants.DATE + " DESC";
	private TextView tv_status;
	private TextView tv_find;

	public void updateMessage() {
		Cursor cursor = null;
		String string = mBtn_mydoctor.getText().toString();
		int start = string.indexOf("(");
		int end = string.indexOf(")");

		try {
			cursor = resolver.query(ChatProvider.CONTENT_URI, FROM, SELECT, null, SORT_ORDER);
			if (cursor.moveToNext()) {
				String jid = cursor.getString(cursor.getColumnIndex(ChatProvider.ChatConstants.JID));
				String selection = ChatConstants.JID + " = '" + jid + "' AND " + ChatConstants.DIRECTION + " = " + ChatConstants.INCOMING + " AND " + ChatConstants.DELIVERY_STATUS + " = "
						+ ChatConstants.DS_NEW;// 新消息数量字段
				Cursor msgcursor = null;
				try {
					msgcursor = resolver.query(ChatProvider.CONTENT_URI, new String[]{"count(" + ChatConstants.PACKET_ID + ")", ChatConstants.DATE, ChatConstants.MESSAGE}, selection,
							null, SORT_ORDER);

					if (msgcursor.moveToNext()) {
						if (msgcursor.getInt(0) > 0) {
							mBtn_mydoctor.setText("我的医生 \t 未读" + msgcursor.getString(0) + "条\n\r" + (start == -1 && end == -1 ? "" : string.substring(start, string.length())));
						} else {
							mBtn_mydoctor.setText("我的医生" + (start == -1 && end == -1 ? "" : string.substring(start, string.length())));
						}
					} else {
						mBtn_mydoctor.setText("我的医生" + (start == -1 && end == -1 ? "" : string.substring(start, string.length())));
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (msgcursor != null) {
						msgcursor.close();
					}
				}
			} else {
				mBtn_mydoctor.setText("我的医生" + (start == -1 && end == -1 ? "" : string.substring(start, string.length())));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

}
