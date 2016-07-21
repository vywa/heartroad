package com.hykj.utils;

import android.text.TextUtils;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class XHttpUtils {
private HttpUtils httpUtils;
	
	/**
	 * 上传文件
	 * @param url
	 * @param requestParams
	 * @param onUploadSuccessListener：上传完成监听接口
	 * @return
	 */

	public HttpHandler<String> upload(String url,RequestParams requestParams,final OnUploadStateListener onUploadStateListener){
		HttpHandler<String> httpHandler=getHttpUtils().send(HttpMethod.POST, url , requestParams, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				if(!TextUtils.isEmpty(responseInfo.result)){ //自行判断结果是否为null或空串
					onUploadStateListener.onUploadSuccess(responseInfo.result);
				}
			}
			
			@Override
			public void onLoading(long total, long current, boolean isUploading) {
				super.onLoading(total, current, isUploading);
			}
			
			@Override
			public void onStart() {
				super.onStart();
			}
			
			@Override
			public void onFailure(HttpException error, String msg) {
				onUploadStateListener.onFailure( error,  msg);
			}
			
		});
		return httpHandler;
	}
	
	// 私有化构造函数，防止创建对象
	private XHttpUtils() {
	}
	
	private static class XHttpUtilsHolder {
		public static final XHttpUtils INSTANCE = new XHttpUtils();
	}
	
	public static XHttpUtils getInstance(){
		return XHttpUtilsHolder.INSTANCE;
	}
	
	public HttpUtils getHttpUtils(){
		if(null!=httpUtils){
			return httpUtils;
		}else{
			httpUtils=new HttpUtils();
			httpUtils.configCurrentHttpCacheExpiry(3000);
			httpUtils.configTimeout(10*1000);
			return httpUtils;
		}
	}
}
