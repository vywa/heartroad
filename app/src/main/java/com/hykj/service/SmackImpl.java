package com.hykj.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Mode;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.ReportedData;
import org.jivesoftware.smackx.ReportedData.Row;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.carbons.Carbon;
import org.jivesoftware.smackx.carbons.CarbonManager;
import org.jivesoftware.smackx.forward.Forwarded;
import org.jivesoftware.smackx.packet.DelayInfo;
import org.jivesoftware.smackx.packet.DelayInformation;
import org.jivesoftware.smackx.ping.PingManager;
import org.jivesoftware.smackx.ping.packet.Ping;
import org.jivesoftware.smackx.ping.provider.PingProvider;
import org.jivesoftware.smackx.provider.DataFormProvider;
import org.jivesoftware.smackx.provider.DelayInfoProvider;
import org.jivesoftware.smackx.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.receipts.DeliveryReceipt;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;
import org.jivesoftware.smackx.receipts.DeliveryReceiptRequest;
import org.jivesoftware.smackx.search.UserSearch;
import org.jivesoftware.smackx.search.UserSearchManager;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.SystemClock;
import android.util.Log;

import com.hykj.App;
import com.hykj.R;
import com.hykj.db.ChatProvider;
import com.hykj.db.ChatProvider.ChatConstants;
import com.hykj.db.RosterProvider;
import com.hykj.db.RosterProvider.RosterConstants;
import com.hykj.utils.MyLog;
import com.hykj.utils.PreferenceConstants;
import com.hykj.utils.PreferenceUtils;
import com.hykj.utils.StatusMode;

public class SmackImpl implements Smack {
	public static final String XMPP_IDENTITY_NAME = "Android_P";
	public static final String XMPP_IDENTITY_TYPE = "phone";
	private static final int PACKET_TIMEOUT = 45000;
	final static private String[] SEND_OFFLINE_PROJECTION =

	new String[] { ChatConstants._ID, ChatConstants.JID, ChatConstants.MESSAGE, ChatConstants.DATE, ChatConstants.PACKET_ID };

	public static boolean needRecon = true;

	// TODO
	final static private String SEND_OFFLINE_SELECTION =

	ChatConstants.DIRECTION + " = " + ChatConstants.OUTGOING + " AND " + ChatConstants.DELIVERY_STATUS + " < " + ChatConstants.DS_ACKED + " AND " + ChatConstants.SENDER + " = " + App.USERID;

	static {
		registerSmackProviders();
	}

	static void registerSmackProviders() {
		// SmackAndroid android = SmackAndroid.init(App.getContext());
		ProviderManager pm = ProviderManager.getInstance();
		// add IQ handling
		pm.addIQProvider("query", "http://jabber.org/protocol/disco#info", new DiscoverInfoProvider());
		// add delayed delivery notifications
		pm.addExtensionProvider("delay", "urn:xmpp:delay", new DelayInfoProvider());
		pm.addExtensionProvider("x", "jabber:x:delay", new DelayInfoProvider());
		// add carbons and forwarding
		pm.addExtensionProvider("forwarded", Forwarded.NAMESPACE, new Forwarded.Provider());
		pm.addExtensionProvider("sent", Carbon.NAMESPACE, new Carbon.Provider());
		pm.addExtensionProvider("received", Carbon.NAMESPACE, new Carbon.Provider());
		// add delivery receipts
		pm.addExtensionProvider(DeliveryReceipt.ELEMENT, DeliveryReceipt.NAMESPACE, new DeliveryReceipt.Provider());
		pm.addExtensionProvider(DeliveryReceiptRequest.ELEMENT, DeliveryReceipt.NAMESPACE, new DeliveryReceiptRequest.Provider());

		// TODO

		// User Search
		pm.addIQProvider("query", "jabber:iq:search", new UserSearch.Provider());
		// Data Forms
		pm.addExtensionProvider("x", "jabber:x:data", new DataFormProvider());
		// TODO

		// add XMPP Ping (XEP-0199)
		pm.addIQProvider("ping", "urn:xmpp:ping", new PingProvider());

		ServiceDiscoveryManager.setIdentityName(XMPP_IDENTITY_NAME);
		ServiceDiscoveryManager.setIdentityType(XMPP_IDENTITY_TYPE);
	}

	private ConnectionConfiguration mXMPPConfig;
	private XMPPConnection mXMPPConnection;
	private ChatService mService;
	private Roster mRoster;
	private final ContentResolver mContentResolver;
	private RosterListener mRosterListener;
	private PacketListener mPacketListener;
	private PacketListener mSendFailureListener;
	private PacketListener mPongListener;

	// ping-pong服务器
	private String mPingID;
	private PendingIntent mPingAlarmPendIntent;
	private PendingIntent mPongTimeoutAlarmPendIntent;
	private static final String PING_ALARM = "com.hykj.xx.PING_ALARM";
	private static final String PONG_TIMEOUT_ALARM = "com.hykj.xx.PONG_TIMEOUT_ALARM";
	private Intent mPingAlarmIntent = new Intent(PING_ALARM);
	private Intent mPongTimeoutAlarmIntent = new Intent(PONG_TIMEOUT_ALARM);
	private PongTimeoutAlarmReceiver mPongTimeoutAlarmReceiver = new PongTimeoutAlarmReceiver();
	private BroadcastReceiver mPingAlarmReceiver = new PingAlarmReceiver();

	// ping-pong服务器

	public SmackImpl(ChatService service) {
		// TODO
		String server = App.OPENFIREURL;

		this.mXMPPConfig = new ConnectionConfiguration(server); // use SRV

		this.mXMPPConfig.setReconnectionAllowed(false);
		this.mXMPPConfig.setSendPresence(false);
		this.mXMPPConfig.setCompressionEnabled(false); // disable for now
		this.mXMPPConfig.setDebuggerEnabled(false);
		// this.mXMPPConfig.setSecurityMode(ConnectionConfiguration.SecurityMode.required);
		this.mXMPPConnection = new XMPPConnection(mXMPPConfig);
		this.mService = service;
		mContentResolver = service.getContentResolver();
	}

	public boolean createAccount(String account, String password) {
		AccountManager amgr = this.mXMPPConnection.getAccountManager();
		try {
			if (!mXMPPConnection.isConnected()) {
				mXMPPConnection.connect();
			}
			amgr.createAccount(account, password);
			return true;
		} catch (XMPPException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean login(String account, String password) throws Exception {
		try {
			if (mXMPPConnection.isConnected()) {
				try {
					mXMPPConnection.disconnect();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			SmackConfiguration.setPacketReplyTimeout(PACKET_TIMEOUT);
			SmackConfiguration.setKeepAliveInterval(-1);
			SmackConfiguration.setDefaultPingInterval(0);
			registerRosterListener();// 监听联系人动态变化
			mXMPPConnection.connect();
			if (!mXMPPConnection.isConnected()) {
				throw new Exception("SMACK connect failed without exception!");
			}
			connectionListener = new ConnectionListener() {
				public void connectionClosedOnError(Exception e) {
					// TODO 多端登录异常
					if (e.getMessage().contains("(conflict)")) {// e.getMessage().equals("stream:error (conflict)");
						needRecon = false;
						mService.closeAll();
						mService.conflict();
					}else{
						mService.connectionFailed("");
					}
				}
				public void connectionClosed() {
				}
				public void reconnectingIn(int seconds) {
				}
				public void reconnectionFailed(Exception e) {
				}
				public void reconnectionSuccessful() {
				}
			};
			mXMPPConnection.addConnectionListener(connectionListener);
			needRecon = true;
			initServiceDiscovery();// 与服务器交互消息监听,发送消息需要回执，判断是否发送成功
			// SMACK auto-logins if we were authenticated before
			if (!mXMPPConnection.isAuthenticated()) {
				mXMPPConnection.login(account, password);
			}
			setStatusFromConfig();// 更新在线状态

		} catch (XMPPException e) {
			throw new Exception(e.getLocalizedMessage(), e.getWrappedThrowable());
		} catch (Exception e) {
			// actually we just care for IllegalState or NullPointer or XMPPEx.
			throw new Exception(e.getLocalizedMessage(), e.getCause());
		}

		registerAllListener();// 注册监听其他的事件，比如新消息

		return mXMPPConnection.isAuthenticated();
	}

	public ArrayList<String> searchUsers(String user) {
		ArrayList<String> users = new ArrayList<String>();
		UserSearchManager usm = new UserSearchManager(mXMPPConnection);
		Form searchForm = null;
		try {
			searchForm = usm.getSearchForm("search." + mXMPPConnection.getServiceName());
			Form answerForm = searchForm.createAnswerForm();
			answerForm.setAnswer("Username", true);
			answerForm.setAnswer("search", user);
			ReportedData data = usm.getSearchResults(answerForm, "search." + mXMPPConnection.getServiceName());
			// column:jid,Username,Name,Email
			Iterator<Row> it = data.getRows();
			Row row = null;
			while (it.hasNext()) {
				row = it.next();
				// Log.d("UserName",
				// row.getValues("Username").next().toString());
				// Log.d("Name", row.getValues("Name").next().toString());
				// Log.d("Email", row.getValues("Email").next().toString());
				// 若存在，则有返回,UserName一定非空，其他两个若是有设，一定非空
				users.add(row.getValues("Username").next().toString());
			}
		} catch (XMPPException e) {
			e.printStackTrace();
		}
		return users;
	}

	public boolean changePassword(String userName, final String pwd, final String newPWD) {
		try {
			if (mXMPPConnection.isConnected()) {
				try {
					mXMPPConnection.disconnect();
				} catch (Exception e) {
				}
			}
			mXMPPConnection.connect();
			if (!mXMPPConnection.isConnected()) {
				throw new Exception("SMACK connect failed without exception!");
			}
			mXMPPConnection.login(userName, pwd);
			SystemClock.sleep(2500);
			mXMPPConnection.getAccountManager().changePassword(newPWD);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	Timer offLineMessageTimer;

	private void registerAllListener() {
		// actually, authenticated must be true now, or an exception must have
		// been thrown.
		if (isAuthenticated()) {
			registerFriendListener();
			registerMessageListener();
			registerMessageSendFailureListener();
			registerPongListener();
			offLineMessageTimer = new Timer();
			offLineMessageTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					sendOfflineMessages();
				}
			}, 3000,  60 * 1000);

			if (mService == null) {
				mXMPPConnection.disconnect();
				return;
			}
			// we need to "ping" the service to let it know we are actually
			// connected, even when no roster entries will come in
			mService.rosterChanged();
		}
	}

	/************ start 新消息处理 ********************/
	private void registerMessageListener() {
		// do not register multiple packet listeners
		if (mPacketListener != null)
			mXMPPConnection.removePacketListener(mPacketListener);

		PacketTypeFilter filter = new PacketTypeFilter(Message.class);

		mPacketListener = new PacketListener() {
			public void processPacket(Packet packet) {
				try {
					// if (packet instanceof Message) {}

					Message msg = (Message) packet;
					String chatMessage = msg.getBody();

					MyLog.wtf("收到消息", msg.toXML());
					// try to extract a carbon
					Carbon cc = CarbonManager.getCarbon(msg);
					if (cc != null && cc.getDirection() == Carbon.Direction.received) {
						msg = (Message) cc.getForwarded().getForwardedPacket();
						chatMessage = msg.getBody();
						// fall through
					} else if (cc != null && cc.getDirection() == Carbon.Direction.sent) {
						msg = (Message) cc.getForwarded().getForwardedPacket();
						chatMessage = msg.getBody();
						if (chatMessage == null)
							return;
						String fromJID = getJabberID(msg.getTo());

						addChatMessageToDB(ChatConstants.OUTGOING, fromJID, chatMessage, ChatConstants.DS_SENT_OR_READ, System.currentTimeMillis(), msg.getPacketID());
						// always return after adding
						return;
					}

					if (chatMessage == null) {
						return;
					}

					if (msg.getType() == Message.Type.error) {
						chatMessage = "<Error> " + chatMessage;
					}

					long ts;
					DelayInfo timestamp = (DelayInfo) msg.getExtension("delay", "urn:xmpp:delay");
					if (timestamp == null)
						timestamp = (DelayInfo) msg.getExtension("x", "jabber:x:delay");
					if (timestamp != null)
						ts = timestamp.getStamp().getTime();
					else
						ts = System.currentTimeMillis();

					String fromJID = getJabberID(msg.getFrom());

					if (!isReceived(msg.getPacketID())) {
						addChatMessageToDB(ChatConstants.INCOMING, fromJID, chatMessage, ChatConstants.DS_NEW, ts, msg.getPacketID());
					} else {
						return;
					}
					mService.newMessage(fromJID, chatMessage);

				} catch (Exception e) {
					// SMACK silently discards exceptions dropped from
					// processPacket :(
					e.printStackTrace();
				}
			}
		};

		mXMPPConnection.addPacketListener(mPacketListener, filter);
	}

	// TODO

	private boolean isReceived(String packetID) {
		Cursor cursor = mContentResolver.query(ChatProvider.CONTENT_URI, new String[] { ChatConstants.PACKET_ID }, (ChatConstants.PACKET_ID + " = '" + packetID + "' AND "
				+ ChatConstants.SENDER + " = " + App.USERID), null, null);
		if (cursor.moveToNext()) {
			cursor.close();
			return true;
		} else {
			cursor.close();
			return false;
		}
	}

	// 发送添加好友请求
	public boolean askUser(String user) {
		Presence subscription = new Presence(Presence.Type.subscribe);
		if (user.contains("@")) {
			subscription.setTo(user);
		} else {
			subscription.setTo(user + "@" + mXMPPConnection.getServiceName());
		}
		MyLog.wtf("xxxxxxxxxxx" + user, "发送添加好友请求" + subscription.toXML());
		mXMPPConnection.sendPacket(subscription);

		return true;
	}

	// 收到添加好友请求后 选择接受
	public void acceptFriend(String user) {
		Presence subscription = new Presence(Presence.Type.subscribed);
		if (user.contains("@")) {
			subscription.setTo(user);
		} else {
			subscription.setTo(user + "@" + mXMPPConnection.getServiceName());
		}
		MyLog.wtf("xxxxxxxxxxx" + user, "选择接受" + subscription.toXML());
		mXMPPConnection.sendPacket(subscription);
	}

	// 收到添加好友请求后 选择拒绝
	public void refuseFriend(String user) {
		Presence subscription = new Presence(Presence.Type.unsubscribed);
		if (user.contains("@")) {
			subscription.setTo(user);
		} else {
			subscription.setTo(user + "@" + mXMPPConnection.getServiceName());
		}
		MyLog.wtf("xxxxxxxxxxx" + user, "选择拒绝" + subscription.toXML());
		mXMPPConnection.sendPacket(subscription);
	}

	public void delFriendEntry(String user) {
		MyLog.wtf("xxxxxxxxxxx" + user, "removeEntry");
		Roster roster = mXMPPConnection.getRoster();
		try {
			RosterEntry rosterEntry = roster.getEntry(user);
			if (rosterEntry != null) {
				roster.removeEntry(rosterEntry);
			}
		} catch (XMPPException e) {
			e.printStackTrace();
		}
		deleteRosterEntryFromDB(user);
	}

	// 发送发送删除好友请求
	public void askDelFriend(String user) {
		Presence subscription = new Presence(Presence.Type.unsubscribe);
		if (user.contains("@")) {
			subscription.setTo(user);
		} else {
			subscription.setTo(user + "@" + mXMPPConnection.getServiceName());
		}
		subscription.setTo(user);
		MyLog.wtf("xxxxxxxxxxx" + user, "发送发送删除好友请求" + subscription.toXML());
		mXMPPConnection.sendPacket(subscription);
	}

	public void registerFriendListener() {
		AndFilter presence_sub_filter = new AndFilter(new PacketTypeFilter(Presence.class));
		mXMPPConnection.addPacketListener(new PacketListener() {
			@Override
			public void processPacket(Packet packet) {
				Presence p = (Presence) packet; // cast packet to Presence
				String P_receiver = packet.getFrom();
				MyLog.wtf("xxxxxxxxxxx--packet", packet.toXML());
				/*
				 * subscribe -- 请求订阅别人，即请求加对方为好友
				 * subscribed--统一被别人订阅，也就是确认被对方加为好友 unsubscribe
				 * --他取消订阅别人，请求删除某好友 unsubscribed -- 拒绝被别人订阅，即拒绝对放的添加请求
				 */
				if (p.getType().equals(Presence.Type.subscribe)) { // 收到被加好友请求
					// 先删除 界面提醒
					if (!isFriend(P_receiver)) {// 如果 已经有好友就不再显示
						mService.getFriendInfo(P_receiver,null);
						MyLog.wtf("xxxxxxxxxxx--packet", "aaaaaaaaaaaaaaaaa");
					} else {
						acceptFriend(P_receiver);
						MyLog.wtf("xxxxxxxxxxx--packet", "bbbbbbbbbbbbbbbbbb");
					}
					MyLog.wtf("xxxxxxxxxxx" + P_receiver, "收到被加好友请求");
				} else if (p.getType().equals(Presence.Type.subscribed)) { // 收到确认添加好友消息
					// 添加好友逻辑
					MyLog.wtf("xxxxxxxxxxx" + P_receiver, "收到确认添加好友消息");
					acceptFriend(P_receiver);
				} else if (p.getType().equals(Presence.Type.unsubscribed)) { // 收到被拒绝好友请求
					// 先删除 界面提醒
					// refuseFriend(P_receiver);
					/*
					 * delFriend(P_receiver);
					 * deleteRosterEntryFromDB(P_receiver); delUser(P_receiver);
					 */
					MyLog.wtf("xxxxxxxxxxx" + P_receiver, "收到被拒绝好友请求");
					// refuseFriend(P_receiver);
//					delFriendEntry(P_receiver);
				} else if (p.getType().equals(Presence.Type.unsubscribe)) { // 收到被删除的请求//拒绝添加好友
																			// 和
																			// 删除好友
					MyLog.wtf("xxxxxxxxxxx" + P_receiver, "收到被删除的请求");
					// 删除好友逻辑
					refuseFriend(P_receiver);
					delFriendEntry(P_receiver);
				}
			}
		}, presence_sub_filter);
	}

	private void addChatMessageToDB(int direction, String JID, String message, int delivery_status, long ts, String packetID) {
		ContentValues values = new ContentValues();

		values.put(ChatConstants.DIRECTION, direction);
		values.put(ChatConstants.JID, JID);
		values.put(ChatConstants.MESSAGE, message);
		values.put(ChatConstants.DELIVERY_STATUS, delivery_status);
		values.put(ChatConstants.DATE, ts);
		values.put(ChatConstants.PACKET_ID, packetID);
		values.put(ChatConstants.SENDER, App.USERID);

		mContentResolver.insert(ChatProvider.CONTENT_URI, values);
	}

	/************ end 新消息处理 ********************/

	/***************** start 处理消息发送失败状态 ***********************/
	private void registerMessageSendFailureListener() {
		// do not register multiple packet listeners
		if (mSendFailureListener != null)
			mXMPPConnection.removePacketSendFailureListener(mSendFailureListener);

		PacketTypeFilter filter = new PacketTypeFilter(Message.class);

		mSendFailureListener = new PacketListener() {
			public void processPacket(Packet packet) {
				try {
					if (packet instanceof Message) {
						Message msg = (Message) packet;
						// String chatMessage = msg.getBody();

//						Log.wtf("发送消息失败", packet.toXML());
						// Log.d("SmackableImp", "message " + chatMessage +
						// " could not be sent (ID:" + (msg.getPacketID() ==
						// null ? "null" : msg.getPacketID()) + ")");

						changeMessageDeliveryStatus(msg.getPacketID(), ChatConstants.DS_NEW);
					}
				} catch (Exception e) {
					// SMACK silently discards exceptions dropped from
					// processPacket :(
					e.printStackTrace();
				}
			}
		};

		mXMPPConnection.addPacketSendFailureListener(mSendFailureListener, filter);
	}

	// TODO
	public void changeMessageDeliveryStatus(String packetID, int new_status) {
//		Log.wtf("changeMessageDeliveryStatus", packetID);
		ContentValues cv = new ContentValues();
		cv.put(ChatConstants.DELIVERY_STATUS, new_status);
		Uri rowuri = Uri.parse("content://" + ChatProvider.AUTHORITY + "/" + ChatProvider.TABLE_NAME);
		mContentResolver.update(rowuri, cv, ChatConstants.PACKET_ID + " = ? AND " + ChatConstants.DIRECTION + " = " + ChatConstants.OUTGOING + " AND " + ChatConstants.SENDER + " = "
				+ App.USERID, new String[] { packetID });

	}

	/***************** end 处理消息发送失败状态 ***********************/
	/***************** start 处理ping服务器消息 ***********************/
	// TODO
	private void registerPongListener() {
		// reset ping expectation on new connection
		mPingID = null;

		if (mPongListener != null)
			mXMPPConnection.removePacketListener(mPongListener);

		mPongListener = new PacketListener() {
			@Override
			public void processPacket(Packet packet) {
				if (packet == null)
					return;
				if (packet.getPacketID().equals(mPingID)) {
					mPingID = null;
					pongTimeOutTimer.cancel();
				} else {
//					Log.wtf("pong---iq", packet.toXML());
				}
			}
		};

		mXMPPConnection.addPacketListener(mPongListener, new PacketTypeFilter(IQ.class));

		try {
				mService.unregisterReceiver(mPingAlarmReceiver);
				mService.unregisterReceiver(mPongTimeoutAlarmReceiver);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		mPingAlarmPendIntent = PendingIntent.getBroadcast(mService.getApplicationContext(), 0, mPingAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mPongTimeoutAlarmPendIntent = PendingIntent.getBroadcast(mService.getApplicationContext(), 0, mPongTimeoutAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mService.registerReceiver(mPingAlarmReceiver, new IntentFilter(PING_ALARM));
		mService.registerReceiver(mPongTimeoutAlarmReceiver, new IntentFilter(PONG_TIMEOUT_ALARM));

		if (pingTimer != null) {
			pingTimer.cancel();
		}
		pingTimer = new Timer();
		pingTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					mPingAlarmPendIntent.send();
				} catch (CanceledException e) {
					e.printStackTrace();
				}
			}
		}, 0, 15000);
	}

	/**
	 * BroadcastReceiver to trigger reconnect on pong timeout.
	 */
	private class PongTimeoutAlarmReceiver extends BroadcastReceiver {
		public void onReceive(Context ctx, Intent i) {
//			Log.wtf("xxxxxxxxxxx", "pongtimeout");
			logout();// 超时就断开连接
			mService.connectionFailed("pong timeout");
		}
	}

	/**
	 * BroadcastReceiver to trigger sending pings to the server
	 */
	private class PingAlarmReceiver extends BroadcastReceiver {
		public void onReceive(Context ctx, Intent i) {
			if (mXMPPConnection.isAuthenticated()) {
				sendServerPing();
			}
		}
	}

	private String serviceName = null;

	private Timer pongTimeOutTimer;
	private Timer pingTimer;
	private ConnectionListener connectionListener;

	@Override
	public void sendServerPing() {
		if (mPingID != null) {
			return; // a ping is still on its way
		}
		// TODO
		pongTimeOutTimer = new Timer();

		pongTimeOutTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					mPongTimeoutAlarmPendIntent.send();
				} catch (CanceledException e) {
					e.printStackTrace();
				}
			}
		}, PACKET_TIMEOUT);

		if (!mXMPPConnection.isAuthenticated()) {
			if (mService != null) {
				mService.connectionFailed("");
			}
			return;
		}

		Ping ping = new Ping();
		ping.setType(Type.GET);
		if (serviceName == null) {
			serviceName = mXMPPConnection.getServiceName();
		}
		ping.setTo(serviceName);
		mPingID = ping.getPacketID();

//		Log.wtf("mPingID", ping.toXML());

		mXMPPConnection.sendPacket(ping);
	}

	/***************** end 处理ping服务器消息 ***********************/

	/******************************* start 联系人数据库事件处理 **********************************/
	private void registerRosterListener() {
		mRoster = mXMPPConnection.getRoster();
		mRoster.setSubscriptionMode(Roster.SubscriptionMode.manual);
		mRosterListener = new RosterListener() {
			private boolean isFristRoter;

			@Override
			public void presenceChanged(Presence presence) {
				String jabberID = getJabberID(presence.getFrom());
				RosterEntry rosterEntry = mRoster.getEntry(jabberID);
				updateRosterEntryInDB(rosterEntry);
				mService.rosterChanged();
			}

			@Override
			public void entriesUpdated(Collection<String> entries) {
				for (String entry : entries) {
					RosterEntry rosterEntry = mRoster.getEntry(entry);
					updateRosterEntryInDB(rosterEntry);
				}
				mService.rosterChanged();
			}

			@Override
			public void entriesDeleted(Collection<String> entries) {
				for (String entry : entries) {
					deleteRosterEntryFromDB(entry);
				}
				mService.rosterChanged();
			}

			@Override
			public void entriesAdded(Collection<String> entries) {
				ContentValues[] cvs = new ContentValues[entries.size()];
				int i = 0;
				for (String entry : entries) {
					RosterEntry rosterEntry = mRoster.getEntry(entry);
					cvs[i++] = getContentValuesForRosterEntry(rosterEntry);
				}
				mContentResolver.bulkInsert(RosterProvider.CONTENT_URI, cvs);
				if (isFristRoter) {
					isFristRoter = false;
					mService.rosterChanged();
				}
			}
		};
		mRoster.addRosterListener(mRosterListener);
	}

	private String getJabberID(String from) {
		String[] res = from.split("/");
		return res[0].toLowerCase();
	}

	private void updateRosterEntryInDB(final RosterEntry entry) {
		final ContentValues values = getContentValuesForRosterEntry(entry);

		if (mContentResolver.update(RosterProvider.CONTENT_URI, values, RosterConstants.JID + " = ? AND " + RosterConstants.USERID + " = ?",
				new String[] { entry.getUser(), App.USERID + "" }) == 0)
			addRosterEntryToDB(entry);
	}

	private void addRosterEntryToDB(final RosterEntry entry) {
		ContentValues values = getContentValuesForRosterEntry(entry);
		Uri uri = mContentResolver.insert(RosterProvider.CONTENT_URI, values);
	}

	private void deleteRosterEntryFromDB(final String jabberID) {
		int count = mContentResolver.delete(RosterProvider.CONTENT_URI, RosterConstants.JID + " = ? AND " + RosterConstants.USERID + " = ?", new String[] { jabberID, App.USERID + "" });
	}

	private boolean isFriend(final String jabberID) {
		boolean flag = false;
		Cursor cursor = null;
		try {
			cursor = mContentResolver.query(RosterProvider.CONTENT_URI, null, RosterConstants.JID + " = ? AND " + RosterConstants.USERID + " = ?", new String[] { jabberID,
					App.USERID + "" }, null);
			flag = cursor.moveToNext();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			cursor.close();
		}
		return flag;
	}

	private ContentValues getContentValuesForRosterEntry(final RosterEntry entry) {
		final ContentValues values = new ContentValues();

		values.put(RosterConstants.JID, entry.getUser());
		values.put(RosterConstants.ALIAS, getName(entry));
		values.put(RosterConstants.SUBSCRIBE, entry.getType().toString());

		Presence presence = mRoster.getPresence(entry.getUser());
		values.put(RosterConstants.STATUS_MODE, getStatusInt(presence));
		values.put(RosterConstants.STATUS_MESSAGE, presence.getStatus());
		values.put(RosterConstants.GROUP, getGroup(entry.getGroups()));
		values.put(RosterConstants.USERID, App.USERID);

		return values;
	}

	private String getGroup(Collection<RosterGroup> groups) {
		for (RosterGroup group : groups) {
			return group.getName();
		}
		return "";
	}

	private String getName(RosterEntry rosterEntry) {
		String name = rosterEntry.getName();
		if (name != null && name.length() > 0) {
			return name;
		}
		name = StringUtils.parseName(rosterEntry.getUser());
		if (name.length() > 0) {
			return name;
		}
		return rosterEntry.getUser();
	}

	private StatusMode getStatus(Presence presence) {
		if (presence.getType() == Presence.Type.available) {
			if (presence.getMode() != null) {
				return StatusMode.valueOf(presence.getMode().name());
			}
			return StatusMode.available;
		}
		return StatusMode.offline;
	}

	private int getStatusInt(final Presence presence) {
		return getStatus(presence).ordinal();
	}

	public void setStatusFromConfig() {
		boolean messageCarbons = PreferenceUtils.getPrefBoolean(mService, PreferenceConstants.MESSAGE_CARBONS, true);
		String statusMode = PreferenceUtils.getPrefString(mService, PreferenceConstants.STATUS_MODE, PreferenceConstants.AVAILABLE);
		String statusMessage = PreferenceUtils.getPrefString(mService, PreferenceConstants.STATUS_MESSAGE, mService.getString(R.string.status_online));
		int priority = PreferenceUtils.getPrefInt(mService, PreferenceConstants.PRIORITY, 0);
		if (messageCarbons)
			CarbonManager.getInstanceFor(mXMPPConnection).sendCarbonsEnabled(true);

		Presence presence = new Presence(Presence.Type.available);
		Mode mode = Mode.valueOf(statusMode);
		presence.setMode(mode);
		presence.setStatus(statusMessage);
		presence.setPriority(priority);
		mXMPPConnection.sendPacket(presence);
	}

	/******************************* end 联系人数据库事件处理 **********************************/

	/**
	 * 与服务器交互消息监听,发送消息需要回执，判断是否发送成功
	 */
	private void initServiceDiscovery() {
		// register connection features
		ServiceDiscoveryManager sdm = ServiceDiscoveryManager.getInstanceFor(mXMPPConnection);
		if (sdm == null)
			sdm = new ServiceDiscoveryManager(mXMPPConnection);

		sdm.addFeature("http://jabber.org/protocol/disco#info");

		// reference PingManager, set ping flood protection to 10s
		PingManager.getInstanceFor(mXMPPConnection).setPingMinimumInterval(10 * 1000);
		// reference DeliveryReceiptManager, add listener

		DeliveryReceiptManager dm = DeliveryReceiptManager.getInstanceFor(mXMPPConnection);
		dm.enableAutoReceipts();
		dm.registerReceiptReceivedListener(new DeliveryReceiptManager.ReceiptReceivedListener() {
			public void onReceiptReceived(String fromJid, String toJid, String receiptId) {
				changeMessageDeliveryStatus(receiptId, ChatConstants.DS_ACKED);
			}
		});
	}

	@Override
	public boolean isAuthenticated() {
		if (mXMPPConnection != null) {
			return (mXMPPConnection.isConnected() && mXMPPConnection.isAuthenticated());
		}
		return false;
	}

	@Override
	public void addRosterItem(String user, String alias, String group) throws Exception {
		addRosterEntry(user, alias, group);
	}

	private void addRosterEntry(String user, String alias, String group) throws Exception {
		mRoster = mXMPPConnection.getRoster();
		try {
			mRoster.createEntry(user, alias, new String[] { group });
		} catch (XMPPException e) {
			throw new Exception(e.getLocalizedMessage());
		}
	}

	@Override
	public void removeRosterItem(String user) throws Exception {

		removeRosterEntry(user);
		mService.rosterChanged();
	}

	private void removeRosterEntry(String user) throws Exception {
		mRoster = mXMPPConnection.getRoster();
		try {
			RosterEntry rosterEntry = mRoster.getEntry(user);

			if (rosterEntry != null) {
				mRoster.removeEntry(rosterEntry);
			}
		} catch (XMPPException e) {
			throw new Exception(e.getLocalizedMessage());
		}
	}

	@Override
	public void renameRosterItem(String user, String newName) throws Exception {
		mRoster = mXMPPConnection.getRoster();
		RosterEntry rosterEntry = mRoster.getEntry(user);

		if (!(newName.length() > 0) || (rosterEntry == null)) {
			throw new Exception("JabberID to rename is invalid!");
		}
		rosterEntry.setName(newName);
	}

	@Override
	public void moveRosterItemToGroup(String user, String group) throws Exception {
		tryToMoveRosterEntryToGroup(user, group);
	}

	private void tryToMoveRosterEntryToGroup(String userName, String groupName) throws Exception {

		mRoster = mXMPPConnection.getRoster();
		RosterGroup rosterGroup = getRosterGroup(groupName);
		RosterEntry rosterEntry = mRoster.getEntry(userName);

		removeRosterEntryFromGroups(rosterEntry);

		if (groupName.length() == 0)
			return;
		else {
			try {
				rosterGroup.addEntry(rosterEntry);
			} catch (XMPPException e) {
				throw new Exception(e.getLocalizedMessage());
			}
		}
	}

	private void removeRosterEntryFromGroups(RosterEntry rosterEntry) throws Exception {
		Collection<RosterGroup> oldGroups = rosterEntry.getGroups();

		for (RosterGroup group : oldGroups) {
			tryToRemoveUserFromGroup(group, rosterEntry);
		}
	}

	private void tryToRemoveUserFromGroup(RosterGroup group, RosterEntry rosterEntry) throws Exception {
		try {
			group.removeEntry(rosterEntry);
		} catch (XMPPException e) {
			throw new Exception(e.getLocalizedMessage());
		}
	}

	private RosterGroup getRosterGroup(String groupName) {
		RosterGroup rosterGroup = mRoster.getGroup(groupName);

		// create group if unknown
		if ((groupName.length() > 0) && rosterGroup == null) {
			rosterGroup = mRoster.createGroup(groupName);
		}
		return rosterGroup;

	}

	@Override
	public void renameRosterGroup(String group, String newGroup) {
		mRoster = mXMPPConnection.getRoster();
		RosterGroup groupToRename = mRoster.getGroup(group);
		if (groupToRename == null) {
			return;
		}
		groupToRename.setName(newGroup);
	}

	@Override
	public void requestAuthorizationForRosterItem(String user) {
		Presence response = new Presence(Presence.Type.subscribe);
		response.setTo(user);
		mXMPPConnection.sendPacket(response);
	}

	@Override
	public void addRosterGroup(String group) {
		mRoster = mXMPPConnection.getRoster();
		mRoster.createGroup(group);
	}

	@Override
	public void sendMessage(String toJID, String message) {
		final Message newMessage = new Message(toJID, Message.Type.chat);
		newMessage.setBody(message);
		newMessage.addExtension(new DeliveryReceiptRequest());
		if (isAuthenticated()) {
			addChatMessageToDB(ChatConstants.OUTGOING, toJID, message, ChatConstants.DS_SENT_OR_READ, System.currentTimeMillis(), newMessage.getPacketID());
			mXMPPConnection.sendPacket(newMessage);
//			Log.wtf("发送消息", newMessage.toXML());
		} else {
			addChatMessageToDB(ChatConstants.OUTGOING, toJID, message, ChatConstants.DS_NEW, System.currentTimeMillis(), newMessage.getPacketID());
		}
	}

	/**
	 * 测试逻辑
	 */
	public void sendCustomMessage() {
		final String s = "<message id=\"zG7oe-1159\" to=\"200000090@bob-optiplex-3020\" type=\"chat\"><body>哈哈2</body><request xmlns='urn:xmpp:receipts'/></message>";

		mXMPPConnection.sendPacket(new Packet() {
			@Override
			public String toXML() {
				return s;
			}
		});
	}

	private boolean isOnLine(String jid) {
		Cursor cursor = mContentResolver.query(RosterProvider.CONTENT_URI, new String[] { RosterConstants.STATUS_MODE }, RosterConstants.JID + " = '" + jid + "' AND "
				+ RosterConstants.USERID + " = '" + App.USERID + "'", null, null);
		int mode = -1;
		if (cursor.moveToNext()) {
			mode = cursor.getInt(0);
		}
		return 4 == mode;
	}

	/***************** start 发送离线消息 ***********************/
	public void sendOfflineMessages() {
		Cursor cursor = null;
		try {
			cursor = mContentResolver.query(ChatProvider.CONTENT_URI, SEND_OFFLINE_PROJECTION, SEND_OFFLINE_SELECTION, null, null);
			final int _ID_COL = cursor.getColumnIndexOrThrow(ChatConstants._ID);
			final int JID_COL = cursor.getColumnIndexOrThrow(ChatConstants.JID);
			final int MSG_COL = cursor.getColumnIndexOrThrow(ChatConstants.MESSAGE);
			final int TS_COL = cursor.getColumnIndexOrThrow(ChatConstants.DATE);
			final int PACKETID_COL = cursor.getColumnIndexOrThrow(ChatConstants.PACKET_ID);
			ContentValues mark_sent = new ContentValues();
			mark_sent.put(ChatConstants.DELIVERY_STATUS, ChatConstants.DS_SENT_OR_READ);
			while (cursor.moveToNext()) {
				int _id = cursor.getInt(_ID_COL);
				String toJID = cursor.getString(JID_COL);
//				Log.wtf("xxxxxxxxxxx", toJID);
				if (!isOnLine(toJID)) {
//					Log.wtf("xxxxxxxxxxx", "buzaixian");
					continue;
				} else {
//					Log.wtf("xxxxxxxxxxx", "zaixian");
				}

				String message = cursor.getString(MSG_COL);
				String packetID = cursor.getString(PACKETID_COL);
				long ts = cursor.getLong(TS_COL);
				final Message newMessage = new Message(toJID, Message.Type.chat);
				newMessage.setBody(message);
				DelayInformation delay = new DelayInformation(new Date(ts));
				newMessage.addExtension(delay);
				newMessage.addExtension(new DelayInfo(delay));
				newMessage.addExtension(new DeliveryReceiptRequest());
				if ((packetID != null) && (packetID.length() > 0)) {
					newMessage.setPacketID(packetID);
				} else {
					packetID = newMessage.getPacketID();
					mark_sent.put(ChatConstants.PACKET_ID, packetID);
				}
				Uri rowuri = Uri.parse("content://" + ChatProvider.AUTHORITY + "/" + ChatProvider.TABLE_NAME + "/" + _id);
				mContentResolver.update(rowuri, mark_sent, null, null);
//				Log.wtf("自动重发未收到回执的消息", newMessage.toXML());
				mXMPPConnection.sendPacket(newMessage); // must be after marking
														// delivered, otherwise it
														// may override the
														// SendFailListener
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}finally{
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public static void sendOfflineMessage(ContentResolver cr, String toJID, String message) {
		ContentValues values = new ContentValues();
		values.put(ChatConstants.DIRECTION, ChatConstants.OUTGOING);
		values.put(ChatConstants.JID, toJID);
		values.put(ChatConstants.MESSAGE, message);
		values.put(ChatConstants.DELIVERY_STATUS, ChatConstants.DS_NEW);
		values.put(ChatConstants.DATE, System.currentTimeMillis());

		cr.insert(ChatProvider.CONTENT_URI, values);
	}

	/***************** end 发送离线消息 ***********************/

	@Override
	public String getNameForJID(String jid) {
		if (null != this.mRoster.getEntry(jid) && null != this.mRoster.getEntry(jid).getName() && this.mRoster.getEntry(jid).getName().length() > 0) {
			return this.mRoster.getEntry(jid).getName();
		} else {
			return jid;
		}
	}

	@Override
	public boolean logout() {
		if (pingTimer != null) {
			pingTimer.cancel();
		}
		if (pongTimeOutTimer != null) {
			pongTimeOutTimer.cancel();
		}
		if (offLineMessageTimer != null) {
			offLineMessageTimer.cancel();
		}
		try {
			mService.unregisterReceiver(mPingAlarmReceiver);
			mService.unregisterReceiver(mPongTimeoutAlarmReceiver);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		try {
			mXMPPConnection.removeConnectionListener(connectionListener);
			mXMPPConnection.getRoster().removeRosterListener(mRosterListener);
			mXMPPConnection.removePacketListener(mPacketListener);
			mXMPPConnection.removePacketSendFailureListener(mSendFailureListener);
			mXMPPConnection.removePacketListener(mPongListener);
		} catch (Exception e) {
			return false;
		}
		if (mXMPPConnection.isConnected()) {
			mXMPPConnection.disconnect();
		}
		setStatusOffline();
		// this.mService = null;
		return true;
	}

	private void setStatusOffline() {
		ContentValues values = new ContentValues();
		values.put(RosterConstants.STATUS_MODE, StatusMode.offline.ordinal());
		mContentResolver.update(RosterProvider.CONTENT_URI, values, null, null);
	}
}
