package com.hykj.utils;


import org.json.JSONObject;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

/**
 * @author  作者：赵宇   	
 * @version 1.0
   创建时间：2015年10月27日 下午2:01:59
 * 类说明：网络访问工具
 */
public class HttpUtil {
	/*
	 * 通过httpClient访问网络，访问方式为get，返回请求的结果
	 */
	/*public static String getStringFromService(String url){
		try {
			HttpClient mClient=new DefaultHttpClient();
			HttpGet mGet=new HttpGet(url);
			HttpResponse mResponse=mClient.execute(mGet);
			if(mResponse.getStatusLine().getStatusCode()==200){
				String res=EntityUtils.toString(mResponse.getEntity());
				return res;
			}
		} catch (Exception e) {
		}
		return null;
	}*/
	/*
	 * 通过volley访问网络，访问方式为get，返回请求结果;
	 */
	
	public static JSONObject getJsonFromService(String url,final Context context){
		
		RequestQueue queue=Volley.newRequestQueue(context);
		queue.add(new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				MyToast.show("网络访问成功");
				//ParseUtil.parse(response);
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				MyToast.show("网络访问失败");
			}
		}));
		queue.start();
		
		return null;
		
	}

	
}
