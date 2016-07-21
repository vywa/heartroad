package com.hykj.utils;

import com.lidroid.xutils.exception.HttpException;

public interface OnUploadStateListener {
	void onUploadSuccess(String result);
	void onFailure(HttpException error, String msg);
}
