package com.hykj.service;

public interface IConnectionStatusCallback {
	void connectionStatusChanged(int connectedState, String reason);
}
