package com.hykj.activity.usermanagement;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.hykj.R;
import com.hykj.activity.BaseActivity;
import com.hykj.adapter.QuestionAdapter;
import com.hykj.entity.QuestionEntity;

/**
 * @author 作者 : zhaoyu
 * @version 创建时间：2016年1月22日 下午3:06:34 类说明：常见问题
 */
public class QuestionActivity extends BaseActivity {
    private ImageView mImg_back;
    private ListView lv;
    private List<QuestionEntity> data;

    @Override
    public void init() {
        setContentView(R.layout.activity_question);
        mImg_back = (ImageView) findViewById(R.id.question_img_back);
        mImg_back.setOnClickListener(this);
        lv = (ListView) findViewById(R.id.question_lv);
        getData();
        QuestionAdapter adapter = new QuestionAdapter(data, this);
        lv.setAdapter(adapter);
    }

    private void getData() {
        data = new ArrayList<QuestionEntity>();
        data.add(new QuestionEntity("天衡慢病大管家回复的问题的医生是不是真正的医生？",
                "是的。天衡团队在招募医生的时候会要求医生提供医师资格证明和所在医院的工作证明，同时对这些信息进行仔细的人工审核，确保无误才会上线"));
        data.add(new QuestionEntity(
                "年龄比较大的吃哪些东西好呢？",
                "理想的血压为120/80mmHg，正常血压为130/85mmHg以下。130～139/85～89mmHg为临界高血压，为正常高限；140～159/90～99mmHg为高血压一期，此时机体无任何器质性病变，只是单纯高血压；160～179/100～109mmHg为高血压二期，此时有左心室肥厚、心脑肾损害等器质性病变，但功能还在代偿状态；180/110mmHg以上为高血压三期。"));
        data.add(new QuestionEntity(
                "高血压饮食可以治疗吗？",
                "理想的血压为120/80mmHg，正常血压为130/85mmHg以下。130～139/85～89mmHg为临界高血压，为正常高限；140～159/90～99mmHg为高血压一期，此时机体无任何器质性病变，只是单纯高血压；160～179/100～109mmHg为高血压二期，此时有左心室肥厚、心脑肾损害等器质性病变，但功能还在代偿状态；180/110mmHg以上为高血压三期。"));
    }

    @Override
    public void click(View v) {
        switch (v.getId()) {
            case R.id.question_img_back:
                finish();
                break;

            default:
                break;
        }
    }

}
