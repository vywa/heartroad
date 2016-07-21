package com.hykj.activity.usermanagement;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hykj.R;
import com.hykj.activity.BaseActivity;
import com.hykj.fragment.usermanagement.Integration_HistoryFragment;
import com.hykj.fragment.usermanagement.Integration_RuleFragment;

/**
 * @author 作者 : zhaoyu
 * @version 创建时间：2016年1月20日 下午2:52:01 类说明：积分中心
 */
public class IntegrationActivity extends BaseActivity {
	private ImageView mTv_back;
	private TextView  mTv_rule, mTv_his;
	private Fragment mFg_rule, mFg_his;
	

	@Override
	public void init() {
		setContentView(R.layout.activity_integration);
		initView();
	}

	private void initView() {
		mTv_back = (ImageView) findViewById(R.id.integration_tv_back);
		mTv_back.setOnClickListener(this);
		mTv_rule = (TextView) findViewById(R.id.integation_tv_rule);
		mTv_his = (TextView) findViewById(R.id.integation_tv_history);
		mTv_rule.setOnClickListener(this);
		mTv_his.setOnClickListener(this);
		mFg_rule = new Integration_RuleFragment();
		mFg_his=new Integration_HistoryFragment();
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		if (!mFg_rule.isAdded()) {
			ft.add(R.id.integartion_framelayout, mFg_rule);
			ft.commit();
		}
	}

	@Override
	public void click(View v) {
		switch (v.getId()) {
		case R.id.integration_tv_back:
			finish();
			break;
		case R.id.integation_tv_rule:
			FragmentManager fm_rule=getSupportFragmentManager();
			FragmentTransaction ft_rule=fm_rule.beginTransaction();
			if(!mFg_his.isAdded()){
				ft_rule.show(mFg_rule);
			}else{
				ft_rule.show(mFg_rule).hide(mFg_his);
			}
			ft_rule.commit();
			break;
		case R.id.integation_tv_history:
			FragmentManager fm_his=getSupportFragmentManager();
			FragmentTransaction ft_his=fm_his.beginTransaction();
			if(!mFg_his.isAdded()){
				ft_his.add(R.id.integartion_framelayout, mFg_his).hide(mFg_rule);
			}else{
				ft_his.show(mFg_his).hide(mFg_rule);
			}
			ft_his.commit();
			break;
		default:
			break;
		}
	}

}
