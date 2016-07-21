package com.hykj.activity.usermanagement;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.hykj.R;
import com.hykj.activity.BaseActivity;
import com.hykj.adapter.AboutAdapter;
import com.hykj.entity.AboutEntity;
import com.hykj.entity.QuestionEntity;

/**
 * @author 作者 : zhaoyu
 * @version 创建时间：2016年1月22日 下午3:37:45 类说明：关于天衡
 */
public class AboutActivity extends BaseActivity {

	private ImageView mImg_back;
	private ListView lv;
	private List<AboutEntity> data;

	@Override
	public void init() {
		setContentView(R.layout.activity_about);
		mImg_back = (ImageView) findViewById(R.id.about_img_back);
		mImg_back.setOnClickListener(this);
		lv = (ListView) findViewById(R.id.about_lv);
		getData();
		AboutAdapter adapter = new AboutAdapter(data, this);
		lv.setAdapter(adapter);
	}

	private void getData() {
		data = new ArrayList<AboutEntity>();
		data.add(new AboutEntity(
				"天衡医疗简介",
				"天衡高血压糖尿病专科 全国连锁的高血压、糖尿病专业治疗机构。2009年成立首家分院，现已在北京、内蒙古、吉林、宁夏、陕西等地区设立了30家分院，服务患者10万多人。 十六年研究，一流方案 专科在北京建有高血压糖尿病研究中心，已经对高血压糖尿病的病因、发病机制、诊断分型、预防、治疗、患者日常管理、饮......."));
		data.add(new AboutEntity(
				"天衡医疗简介",
				"天衡高血压糖尿病专科 全国连锁的高血压、糖尿病专业治疗机构。2009年成立首家分院，现已在北京、内蒙古、吉林、宁夏、陕西等地区设立了30家分院，服务患者10万多人。 十六年研究，一流方案 专科在北京建有高血压糖尿病研究中心，已经对高血压糖尿病的病因、发病机制、诊断分型、预防、治疗、患者日常管理、饮......."));
		data.add(new AboutEntity(
				"天衡医疗简介",
				"天衡高血压糖尿病专科 全国连锁的高血压、糖尿病专业治疗机构。2009年成立首家分院，现已在北京、内蒙古、吉林、宁夏、陕西等地区设立了30家分院，服务患者10万多人。 十六年研究，一流方案 专科在北京建有高血压糖尿病研究中心，已经对高血压糖尿病的病因、发病机制、诊断分型、预防、治疗、患者日常管理、饮......."));
	}

	@Override
	public void click(View v) {
		switch (v.getId()) {
		case R.id.about_img_back:
			finish();
			break;

		default:
			break;
		}
	}
}
