package com.hykj;

import android.os.Environment;

public abstract class Constant {

    public static final String CHECKVERSION = "version/pinfo";

    public static final String AUTOLOGIN="reglog/fresh?";//自动登录
    public static final String GETSELFINFO="account/patientInfo?";//首页获取个人信息

    public static String ADD_BLOOD_PRESSURE = "bloodPressure/upload?";
    public static String ADD_BLOOD_SUGAR = "bloodSugger/upload?";

    public static String HOSPITAL_LIST = "hospital/showAll";
    public static String DOCTOR_LIST = "docter/queryDoctor?";
    public static String GET_PERSON_INFO = "info/doctorInfo?";

    public static String BLOODPRESSURE_HISTORY = "bloodPressure/show?";
    public static String BLOODSUGAR_HISTORY = "bloodSugger/show?";

    public static String BLOODSUGAR_HOMEPAGE = "bloodSugger/latestDay?";// 获取一天血糖数据
    public static String BLOODPRESSURE_HOMEPAGE = "bloodPressure/latestDay?";// 获取最近一条血压数据

    public static String DEVICE_BIND = "device/bind?";
    public static String DEVICE_UNBIND = "device/unbind?";

    public static String COLLECTION = "collection/dailyNews?";// 资讯
    public static String GETCOLLECTION = "collection/viewNews?";// 查看收藏
    public static String DSUBJECT = "collection/ddailyNews?";// 删除收藏

    public static String PUSHINFO = "dnews/queryList?";

    public static String DIETADD = "diet/add?";// 添加饮食情况
    public static String SPORTADD = "sport/add?";// 添加运动情况
    public static String DIETQUERY = "diet/query?";// 查询饮食情况
    public static String SPORTQUERY = "sport/query?";// 查询运动情况
    public static String DIETDELETE = "diet/delete?";// 删除饮食
    public static String SPORTDELETE = "sport/delete?";// 删除运动
    public static String MEDICALRECORD = "mrecord/add?";// 上传病历
    public static String MEDICALRECORDQUARY = "mrecord/pquery?";// 查询病历
    public static String REVIEWPRESCRIPTION = "recipe/patient/queryList?";// 查询处方

    public static String UPLOAD_FILE = "multiMedia/upload";
    public static String PUBLISH_SUBJECT = "subject/add?";
    public static String GET_SUBJECTS_LIST = "subject/queryList?";
    public static String GET_SUBJECTS_BESTLIST = "subject/perfectList?";
    public static String GET_SUBJECTS_SELFLIST = "subject/selfSubject?";
    public static String SUBJECT_LIST_REPLY = "replysubject/queryList?";
    public static String SUBJECT_REPLY = "replysubject/add?";
    public static String SUBJECT_LIKE = "subject/like?";
    public static String SUBJECT_COLLIST = "collection/viewSubject?";
    public static String SUBJECT_COLL = "collection/subject?";
    public static String SUBJECT_DELCOLL = "collection/dsubject?";

    public static String LOGIN = "reglog/patient/username?" + App.appendHeader();//登录

    public static String THIRDPART_LOGIN = "reglog/thirdPart?" + App.appendHeader();


    public static String SMS_REGIST = "account/smsSend?";
    public static String MAIL_REGIST = "account/mailSend?";
    public static String BIND_RELATIVES = "account/bindRelative?";
    public static String GETCODE_PASSWORD = "account/findPassword?";

    public static String CHANGE_PASSWORD = "account/updatePassword?";//忘记密码
    public static String CHANGE_THIRD = "account/changeThird?";//绑定三方帐号
    public static String VALIDATE = "account/validate?";//绑定发送验证码
    public static String CHANGE = "account/change?";//绑定用户名
    public static String UPDATA_INFO = "info/update?";
    public static String UNBINDVALIDATE ="account/selfSend?";//解绑发送验证码
    public static String UNBIND ="account/unbind?";//解绑


    public static String MAIL_RECEIVE = "account/mailReceive?";
    public static String SMS_RECEIVE = "account/smsReceive?";

    public static final String ROOF = Environment.getExternalStorageDirectory()
            + "/THealth";
    public static final String DOWNLOAD_FILEPATH = Environment
            .getExternalStorageDirectory() + "/THealth/Download";
    public static final String TEMP_FILEPATH = Environment
            .getExternalStorageDirectory() + "/THealth/Temp";
    public static final String SOUND_FILEPATH = Environment
            .getExternalStorageDirectory() + "/THealth/Sound";
    public static final String VIDEO_FILEPATH = Environment
            .getExternalStorageDirectory() + "/THealth/Video";
    public static final String IMAGE_FILEPATH = Environment
            .getExternalStorageDirectory() + "/THealth/Image";
    public static final String LOG_FILEPATH = Environment
            .getExternalStorageDirectory() + "/THealth/Log";

    public static final int GET_DATA_SUCCESS = 20;
    public static final int GET_DATA_ANALYZE_ERROR = 21;
    public static final int GET_DATA_NETWORK_ERROR = 22;
    public static final int GET_DATA_SERVER_ERROR = 23;
    public static final int GET_DATA_NULL = 24;

    public static final String DB_NAME = "thealth.db";

}
