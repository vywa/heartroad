package com.hykj.fragment.subject;

import java.io.File;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.hykj.Constant;
import com.hykj.R;
import com.hykj.activity.subject.NewSubjectPicActivity;
import com.hykj.activity.subject.SubjectDetailActivity;
import com.hykj.manager.MyLoactionManager;


public class SubjectReplyAddFragment extends Fragment implements OnClickListener{
	
	private ImageButton ib_loc;
	private ImageButton ib_pic;
	public static final String FILE_NAME = "camtemp.jpg";
	
	public static final int REQUEST_CODE_CAMERA = 1;
	public static final int REQUEST_CODE_PICKPHOTO = 2;
	public static final int REQUEST_CODE_CROP = 3;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_subjectadd_reply, container, false);
		
		ib_loc = (ImageButton) view.findViewById(R.id.ib_subject_loc);
		ib_pic = (ImageButton) view.findViewById(R.id.ib_subject_pic);
		
		ib_loc.setOnClickListener(this);
		ib_pic.setOnClickListener(this);
		
		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ib_subject_loc:
			Intent intentLoc = new Intent(getActivity(),MyLoactionManager.class);
			intentLoc.putExtra("locInfo", SubjectDetailActivity.locInfo);
			getActivity().startActivityForResult(intentLoc, 0);
			break;
		case R.id.ib_subject_pic:
			Intent intentPic = new Intent(getActivity(),NewSubjectPicActivity.class);
			intentPic.putExtra("imagePath", SubjectDetailActivity.imagePath);
			getActivity().startActivityForResult(intentPic, 0);
			break;
		}
	}
}
