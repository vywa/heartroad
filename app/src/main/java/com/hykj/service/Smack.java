package com.hykj.service;

public interface Smack {
	boolean login(String account, String password) throws Exception;

	boolean logout();

	boolean isAuthenticated();

	void addRosterItem(String user, String alias, String group)
			throws Exception;

	void removeRosterItem(String user) throws Exception;

	void renameRosterItem(String user, String newName)
			throws Exception;

	void moveRosterItemToGroup(String user, String group)
			throws Exception;

	void renameRosterGroup(String group, String newGroup);

	void requestAuthorizationForRosterItem(String user);

	void addRosterGroup(String group);

	void setStatusFromConfig();

	void sendMessage(String user, String message);

	void sendServerPing();

	String getNameForJID(String jid);
}
