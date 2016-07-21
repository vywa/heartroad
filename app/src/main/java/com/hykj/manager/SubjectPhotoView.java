package com.hykj.manager;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.hykj.R;
import com.hykj.utils.MyLog;
import com.hykj.view.HackyViewPager;

import java.util.ArrayList;

public class SubjectPhotoView extends Activity {
	private HackyViewPager mViewPager;

	private static String[] sDrawables ;
	private static int currentItem ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_subject_photoview);
		mViewPager = (HackyViewPager) findViewById(R.id.view_pager);
        
		setContentView(mViewPager);

		ArrayList<String> arr = (ArrayList<String>) getIntent().getSerializableExtra("imageUrls");
		
		for (int i = 0; i < arr.size(); i++) {
			arr.set(i, arr.get(i).replace(".jpg", ".jpg"));//_HQuality.jpg
		}
		
		sDrawables = new String[arr.size()];
		
		for (int i = 0; i < arr.size(); i++) {
			sDrawables[i] = arr.get(i);
		}
		
		currentItem = getIntent().getIntExtra("currentItem", 0);
		
		mViewPager.setAdapter(new SamplePagerAdapter());
		
		mViewPager.setCurrentItem(currentItem);
	}
	

	static class SamplePagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return sDrawables.length;
		}

		@Override
		public View instantiateItem(ViewGroup container, int position) {
			final MyPhotoView photoView = new MyPhotoView(container.getContext());
			
			String url = sDrawables[position];

			photoView.setImageUri(url);
			container.addView(photoView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			
			return photoView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

	}

	/*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.viewpager_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menuLockItem = menu.findItem(R.id.menu_lock);
        toggleLockBtnTitle();
        menuLockItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				toggleViewPagerScrolling();
				toggleLockBtnTitle();
				return true;
			}
		});

        return super.onPrepareOptionsMenu(menu);
    }
    
    private void toggleViewPagerScrolling() {
    	if (isViewPagerActive()) {
    		((HackyViewPager) mViewPager).toggleLock();
    	}
    }
    
    private void toggleLockBtnTitle() {
    	boolean isLocked = false;
    	if (isViewPagerActive()) {
    		isLocked = ((HackyViewPager) mViewPager).isLocked();
    	}
    	String title = (isLocked) ? "nulock": "lock";
    	if (menuLockItem != null) {
    		menuLockItem.setTitle(title);
    	}
    }

    private boolean isViewPagerActive() {
    	return (mViewPager != null && mViewPager instanceof HackyViewPager);
    }
    
	@Override
	protected void onSaveInstanceState(@NonNull Bundle outState) {
		if (isViewPagerActive()) {
			outState.putBoolean(ISLOCKED_ARG, ((HackyViewPager) mViewPager).isLocked());
    	}
		super.onSaveInstanceState(outState);
	}*/
	
	
}
