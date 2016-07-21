package com.hykj.utils;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader;

public class BitmapLruCache  implements ImageLoader.ImageCache {//extends LruCache<String, Bitmap>
	public BitmapLruCache() {
//		super(maxSize);
		 int maxMemory = (int) Runtime.getRuntime().maxMemory();
		    int cacheSize = maxMemory / 8;
		    mMemoryCache = new LruCache<String, Bitmap>(cacheSize){
		      @Override
		      protected int sizeOf(String key, Bitmap bitmap){
		        return bitmap.getRowBytes() * bitmap.getHeight();
		      }
		    };	
	}
	  private static LruCache<String, Bitmap> mMemoryCache;
	  
	  private static BitmapLruCache lruImageCache;
	  
	  public static BitmapLruCache instance(){
		  
		    if(lruImageCache == null){
		      lruImageCache = new BitmapLruCache();
		    }
		    return lruImageCache;
		  }
		  
		  @Override
		  public Bitmap getBitmap(String arg0) {		
		    return mMemoryCache.get(arg0);	
		  }

		  @Override
		  public void putBitmap(String arg0, Bitmap arg1) {
		    if(getBitmap(arg0) == null){
		      mMemoryCache.put(arg0, arg1);
		    }		
		  }

	/*protected int sizeOf(String key, Bitmap bitmap) {
		return (bitmap.getRowBytes() * bitmap.getHeight());
	}

	public Bitmap getBitmap(String url) {
		return ((Bitmap) get(url));
	}

	public void putBitmap(String url, Bitmap bitmap) {
		put(url, bitmap);
	}*/
}// 创建ImageLoader中的参数cacheSize就是我们设置的缓存文件最大值，比如可以设置int
