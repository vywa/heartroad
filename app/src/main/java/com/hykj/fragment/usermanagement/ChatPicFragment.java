package com.hykj.fragment.usermanagement;

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


public class ChatPicFragment extends Fragment implements OnClickListener{
	
	private ImageButton ib_camera;
	private ImageButton ib_pic;
	public static final String FILE_NAME = "camtemp.jpg";
	
	public static final int REQUEST_CODE_CAMERA = 1;
	public static final int REQUEST_CODE_PICKPHOTO = 2;
	public static final int REQUEST_CODE_CROP = 3;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_chat_pic, container, false);
		
		ib_camera = (ImageButton) view.findViewById(R.id.ib_chatpic_camera);
		ib_pic = (ImageButton) view.findViewById(R.id.ib_chatpic_pic);
		
		ib_camera.setOnClickListener(this);
		ib_pic.setOnClickListener(this);
		
		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ib_chatpic_camera:
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Constant.TEMP_FILEPATH, FILE_NAME)));
			getActivity().startActivityForResult(intent,REQUEST_CODE_CAMERA);
			break;
		case R.id.ib_chatpic_pic:
			Intent intentPic = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			getActivity().startActivityForResult(intentPic, REQUEST_CODE_PICKPHOTO);
			break;
		}
	}
}
