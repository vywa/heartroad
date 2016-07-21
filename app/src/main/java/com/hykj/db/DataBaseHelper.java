package com.hykj.db;

import android.content.Context;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.hykj.Constant;

public class DataBaseHelper extends SQLiteOpenHelper {

	public DataBaseHelper(Context context) {
		super(context, Constant.DB_NAME, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		String sql = "create table patient(userId Integer(20) PRIMARY KEY NOT NUll,fullname VARCHAR(10) ,sex VARCHAR(5) ,age INTEGER(5),birthday VARCHAR(20),height VARCHAR(10) ,weight VARCHAR(10) ,address VARCHAR(30),phonenumber VERCHAR(20) ,email VARCHAR(30) ,username VARCHAR(20),icon VARCHAR(100),qq VARCHAR(20),weixin VARCHAR(30),weibo VARCHAR(30),longitude DECIMAL(30),latitude DECIMAL(30),subhospital VARCHAR(20),Patientremember INTEGER(5),myselfdoctor VARCHAR(10),islogin BOOLEAN(10),logaddr VARCHAR(50),modtime BIGINT(50));";
		db.execSQL(sql);
		sql = "create table bsinfo(measureTime BIGINT PRIMARY KEY,userId VARCHAR(20) NOT NULL,bsValue VARCHAR(20) NOT NULL,measureType VARCHAR(10) NOT NULL,uploadSuccess VARCHAR(10) NOT NULL);";
		db.execSQL(sql);
		sql = "create table bpinfo(measureTime BIGINT PRIMARY KEY,userId VARCHAR(20) NOT NULL,highBP VARCHAR(20) NOT NULL,lowBP VARCHAR(20) NOT NULL,heartRate VARCHAR(20) NOT NULL,uploadSuccess VARCHAR(10) NOT NULL);";
		db.execSQL(sql);
		sql = "create table subjectinfo(userId VARCHAR(20) PRIMARY KEY,title VARCHAR(50) NOT NULL,content VARCHAR(200) NOT NULL,locInfo VARCHAR(20) ,latitude VARCHAR(20) ,longitude VARCHAR(20) ,"
				+ "videoUrl VARCHAR(30) ,videoPath VARCHAR(30),soundUrl VARCHAR(30) ,soundPath VARCHAR(30),fileUrl VARCHAR(30) ,filePath VARCHAR(30) ,"
				+ "imgUrls VARCHAR(200) ,imagePath VARCHAR(200));";
		db.execSQL(sql);
		sql = "create table subjects(subjectId VARCHAR(20) PRIMARY KEY,title VARCHAR(50) NOT NULL,content VARCHAR(200) NOT NULL,locInfo VARCHAR(20) ,latitude VARCHAR(20) ,longitude VARCHAR(20),"
				+ "videoUrl VARCHAR(30) ,soundUrl VARCHAR(30),fileUrl VARCHAR(30),imgUrls VARCHAR(512),author VARCHAR(20),replyNum INTEGER,isLiked BOOLEAN,likeCount INTEGER,publishTime VARCHAR(20),subjectType INTEGER,authorPhotoImgUrl VARCHAR(30),"
				+ "isCollection BOOLEAN,userId VARCHAR(20));";
		db.execSQL(sql);
		sql = "create table remind(_id INTEGER  PRIMARY KEY AUTOINCREMENT,userId INTEGER(20)  NOT NUll,type INTEGER(5) NOT NULl,contents VARCHAR(100) NOT NULL,time VARCHAR(10) NOT NULL,repeat VARCHAR(20),islocked VARCHAR(10))";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}
