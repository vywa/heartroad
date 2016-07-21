package com.hykj.utils;

import com.hykj.App;

import android.widget.Toast;

public class MyToast {
	
	static Toast toast;
	
	public static void show(String msg){
		if (toast == null) {
            toast = Toast.makeText(App.getContext(), msg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
        }
        toast.show();
	}
}
