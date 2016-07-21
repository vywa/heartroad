package com.hykj.activity.usermanagement;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hykj.R;
import com.hykj.activity.BaseActivity;

/**
 * @author 作者 : zhaoyu
 * @version 创建时间：2016年1月19日 上午9:44:54 类说明：健康评估
 */
public class HealthRecordActivity extends BaseActivity {
	private ImageView mImg_back;
	private TextView mTv_level, mTv_damage, mTv_disease, mTv_result,
			mTv_danger;
	private TextView mTv_dangernum, mTv_damagenum, mTv_diseasenum;
	private TextView mTv_again;//重新评估
	@Override
	public void init() {
		setContentView(R.layout.activity_healthrecord);

		initViews();
	}

	private void initViews() {
		mImg_back = (ImageView) findViewById(R.id.healthrecord_imgv_back);
		mImg_back.setOnClickListener(this);
		mTv_level = (TextView) findViewById(R.id.healthrecord_tv_level);
		mTv_damage = (TextView) findViewById(R.id.healthrecord_tv_damage);
		mTv_disease = (TextView) findViewById(R.id.healthrecord_tv_disease);
		mTv_danger = (TextView) findViewById(R.id.healthrecord_tv_danger);
		mTv_result = (TextView) findViewById(R.id.healthrecord_tv_result);
		mTv_again=(TextView) findViewById(R.id.healthrecord_tv_again);
		mTv_again.setOnClickListener(this);
		mTv_dangernum = (TextView) findViewById(R.id.healthrecord_tv_dangernum);
		mTv_damagenum = (TextView) findViewById(R.id.healthrecord_tv_damagenum);
		mTv_diseasenum = (TextView) findViewById(R.id.healthrecord_tv_diseasenum);

		Bundle b = getIntent().getBundleExtra("result");
		int level = b.getInt("hightBloodPressure");
		// TODO
		String result = b.getString("result");
		String[] strs = result.split(":");
		String str = strs[1];

		Bundle b1 = b.getBundle("affiliatedClinicalDisease");
		Bundle b2 = b.getBundle("targetOrganDamage");
		Bundle b3 = b.getBundle("riskFactor");
		int one = 0;
		StringBuilder str_one = new StringBuilder();
		int two = 0;
		StringBuilder str_two = new StringBuilder();
		int three = 0;
		StringBuilder str_three = new StringBuilder();
		if (!TextUtils.isEmpty(b1.getString("cardiovascularDisease"))) {
			one++;
			str_one.append("心血管病;\r\n");
		}
		if (!TextUtils.isEmpty(b1.getString("cerebralVascularDisease"))) {
			one++;
			str_one.append("脑血管病;\r\n");
		}
		if (!TextUtils.isEmpty(b1.getString("kidneyDisease"))) {
			one++;
			str_one.append("肾脏疾病;\r\n");
		}
		if (!TextUtils.isEmpty(b1.getString("peripheralVascularDisease"))) {
			one++;
			str_one.append("外周血管病;\r\n");
		}
		if (!TextUtils.isEmpty(b1.getString("retinopathy"))) {
			one++;
			str_one.append("视网膜病变;\r\n");
		}
		if (!TextUtils.isEmpty(b1.getString("diabetesMelliitus"))) {
			one++;
			str_one.append("糖尿病;\r\n");
		}
		if (!TextUtils.isEmpty(b2.getString("leftVentricularHypertrophy"))) {
			two++;
			str_two.append("左心室肥厚;\r\n");
		}
		if (!TextUtils.isEmpty(b2.getString("neckArteries"))) {
			two++;
			str_two.append("颈动脉内膜中层厚度>=0.9mm或有斑块;\r\n");
		}
		if (!TextUtils.isEmpty(b2.getString("ankleArteries"))) {
			two++;
			str_two.append("肱-踝动脉脉搏波速度>=14m/s;\r\n");
		}
		if (!TextUtils.isEmpty(b2.getString("limbArteries"))) {
			two++;
			str_two.append("踝/臂血压指数<0.9;\r\n");
		}
		if (!TextUtils.isEmpty(b2.getString("kidneyBall"))) {
			two++;
			str_two.append("肾小球滤过率降低或血清肌酐轻度升高;\r\n");
		}
		if (!TextUtils.isEmpty(b2.getString("urineProtein"))) {
			two++;
			str_two.append("微量白蛋白尿或白蛋白/肌酐比>=3.5mg/mmol;\r\n");
		}
		if (!TextUtils.isEmpty(b3.getString("cigerate"))) {
			three++;
			str_three.append("吸烟;\r\n");
		}
		if (!TextUtils.isEmpty(b3.getString("suggerEndure"))) {
			three++;
			str_three.append("糖耐量受损和/或空腹血糖受损;\r\n");
		}
		if (!TextUtils.isEmpty(b3.getString("bloodFatException"))) {
			three++;
			str_three.append("血脂异常;\r\n");
		}
		if (!TextUtils.isEmpty(b3.getString("vesselherit"))) {
			three++;
			str_three.append("早发心血管病家族史;\r\n");
		}
		if (!TextUtils.isEmpty(b3.getString("fat"))) {
			three++;
			str_three.append("肥胖或腹型肥胖;\r\n");
		}
		if (!TextUtils.isEmpty(b3.getString("hcy"))) {
			three++;
			str_three.append("血同型半胱氨酸升高;\r\n");
		}
		if (!TextUtils.isEmpty(b3.getString("hsCRP"))) {
			three++;
			str_three.append("hs-CRP升高;\r\n");
		}
		if (!TextUtils.isEmpty(b3.getString("physicalActivity"))) {
			three++;
			str_three.append("缺乏体力活动;\r\n");
		}

		mTv_level.setText(level + "级");
		mTv_result.setText(str);
		mTv_danger.setText(str_three.toString());
		mTv_damage.setText(str_two.toString());
		mTv_disease.setText(str_one.toString());
		mTv_damagenum.setText(two + "");
		mTv_dangernum.setText(three+"");
		mTv_diseasenum.setText(one + "");

	}

	@Override
	public void click(View v) {
		switch (v.getId()) {
		case R.id.healthrecord_imgv_back:
			finish();
			break;
		case R.id.healthrecord_tv_again:
			finish();
			startActivity(new Intent(this,QuestionnaireActivity.class));
			break;
		default:
			break;
		}
	}

}
