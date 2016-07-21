package com.hykj.utils;

public interface HTTPDataReceiver {

	void onDataReceived(Object data);
	
	void onDataReceiveException(Object data);
	
}
