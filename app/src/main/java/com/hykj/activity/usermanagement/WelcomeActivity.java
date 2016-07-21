package com.hykj.activity.usermanagement;

import java.util.ArrayList;

import com.hykj.App;
import com.hykj.R;
import com.hykj.adapter.ImageAdapter;
import com.hykj.adapter.WelImageAdapter;
import com.hykj.utils.Coder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.ViewPager;
import android.app.Activity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class WelcomeActivity extends Activity {
    private ViewPager viewPager;
    private ArrayList<ImageView> imageLists = new ArrayList<ImageView>();
    private ViewGroup viewGroup;
    private ImageView[] imageDots;
    private Button mBtn_go;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.addActivity(this);
        setContentView(R.layout.activity_welcome);
        SharedPreferences share = getSharedPreferences("welcome", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = share.edit();

        editor.putString("welcome", "welcome");

        editor.commit();
        initView();
    }

    /**
     * 初始化组件
     */
    private void initView() {
        viewPager = (ViewPager) findViewById(R.id.welcome_viewpager);
        viewGroup = (ViewGroup) findViewById(R.id.welcome_point_group);

        ImageView image1 = new ImageView(this);
        image1.setScaleType(ImageView.ScaleType.FIT_XY);
        image1.setImageResource(R.drawable.image1);
        ImageView image2 = new ImageView(this);
        image2.setScaleType(ImageView.ScaleType.FIT_XY);
        image2.setImageResource(R.drawable.image2);
        ImageView image3 = new ImageView(this);
        image3.setScaleType(ImageView.ScaleType.FIT_XY);
        image3.setImageResource(R.drawable.image3);

        imageLists.add(image1);
        imageLists.add(image2);
        imageLists.add(image3);
        viewGroup.removeAllViews();
        imageDots = new ImageView[imageLists.size()];
        for (int i = 0; i < imageDots.length; i++) {
            imageDots[i] = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(30, 30);
            params.gravity = Gravity.CENTER;
            params.setMargins(35, 0, 35, 0);

            imageDots[i].setLayoutParams(params);

            viewGroup.addView(imageDots[i]);
        }
        imageDots[0].setImageResource(R.drawable.banner_point1);
        imageDots[1].setImageResource(R.drawable.banner_point2);
        imageDots[2].setImageResource(R.drawable.banner_point2);
        WelImageAdapter imageAdapter = new WelImageAdapter(imageLists);
        viewPager.setAdapter(imageAdapter);
        imageAdapter.notifyDataSetChanged();
        viewPager.setCurrentItem(0);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (viewPager.getCurrentItem() == 0) {
                    imageDots[0].setImageResource(R.drawable.banner_point1);
                    imageDots[1].setImageResource(R.drawable.banner_point2);
                    imageDots[2].setImageResource(R.drawable.banner_point2);
                    mBtn_go.setVisibility(View.GONE);
                }
                if (viewPager.getCurrentItem() == 1) {
                    imageDots[0].setImageResource(R.drawable.banner_point2);
                    imageDots[1].setImageResource(R.drawable.banner_point1);
                    imageDots[2].setImageResource(R.drawable.banner_point2);
                    mBtn_go.setVisibility(View.GONE);
                }
                if (viewPager.getCurrentItem() == 2) {
                    imageDots[0].setImageResource(R.drawable.banner_point2);
                    imageDots[1].setImageResource(R.drawable.banner_point2);
                    imageDots[2].setImageResource(R.drawable.banner_point1);
                    mBtn_go.setVisibility(View.VISIBLE);
                }
            }
        });

        mBtn_go = (Button) findViewById(R.id.welcome_btn_go);
        mBtn_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                WelcomeActivity.this.startActivity(intent);
                WelcomeActivity.this.finish();
            }
        });
    }


}