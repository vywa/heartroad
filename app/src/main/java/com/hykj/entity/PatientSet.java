/**
 * 
 */
package com.hykj.entity;

/**
 * @author zhaoyu
 * @version 1.0
 * 创建时间：2015年12月11日  类说明
 */
public class PatientSet {
    private Integer Patientid;//用户id*
    private Integer Patientphonenumber;//电话号*
    private String Patientaddr;//地址*
    private double longitude;//经度
    private double latitude;//纬度
    private String Patientqq;//qq*
    private String Patientname;//姓名*
    private String Patientemail;//邮箱*
    private String Patientweixin;//微信*
    private String Patientweibo;//微博*
    private Integer Patientage;//年龄*
    private String Patientbirthday;//出生日期*
    private String Patienticon;//头像*
    private Integer Patientweight;//体重*
    private Integer Patientheight;//身高*
    private String Patientlogname;//登录名*
    private String Patientlogpasswd;//登录密码
    private String Patientgender;//性别*
    private String Patientsubhospital;//子医院
    private Integer Patientremember;//属于哪个科室（1 糖尿病    2 高血压）
    private String myselfdoctor;//自己的医生
    public static boolean islogin = false;//是否登录
    private Integer Patientcaptcha;//验证码
    private String Patientlogaddr;//登录地址
	
	public PatientSet() {
		super();
	}

	public PatientSet(Integer patientid, Integer patientphonenumber,
			String patientaddr, double longitude, double latitude,
			String patientqq, String patientname, String patientemail,
			String patientweixin, String patientweibo, Integer patientage,
			String patientbirthday, String patienticon, Integer patientweight,Integer patientheight,
			String patientlogname, String patientlogpasswd,
			String patientgender, String patientsubhospital,
			Integer patientremember, String myselfdoctor, Integer patientcaptcha,String Patientlogaddr) {
		super();
		Patientid = patientid;
		Patientphonenumber = patientphonenumber;
		Patientaddr = patientaddr;
		this.longitude = longitude;
		this.latitude = latitude;
		Patientqq = patientqq;
		Patientname = patientname;
		Patientemail = patientemail;
		Patientweixin = patientweixin;
		Patientweibo = patientweibo;
		Patientage = patientage;
		Patientbirthday = patientbirthday;
		Patienticon = patienticon;
		Patientweight = patientweight;
		Patientheight=patientheight;
		Patientlogname = patientlogname;
		Patientlogpasswd = patientlogpasswd;
		Patientgender = patientgender;
		Patientsubhospital = patientsubhospital;
		Patientremember = patientremember;
		this.myselfdoctor = myselfdoctor;
		Patientcaptcha = patientcaptcha;
		Patientaddr=patientaddr;
	}
	public Integer getPatientid() {
		return Patientid;
	}

	
	public void setPatientid(Integer patientid) {
		Patientid = patientid;
	}

	
	public Integer getPatientphonenumber() {
		return Patientphonenumber;
	}

	
	public void setPatientphonenumber(Integer patientphonenumber) {
		Patientphonenumber = patientphonenumber;
	}

	
	public String getPatientaddr() {
		return Patientaddr;
	}


	public void setPatientaddr(String patientaddr) {
		Patientaddr = patientaddr;
	}

	
	public double getLongitude() {
		return longitude;
	}

	
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	
	public String getPatientqq() {
		return Patientqq;
	}

	
	public void setPatientqq(String patientqq) {
		Patientqq = patientqq;
	}

	
	public String getPatientname() {
		return Patientname;
	}

	
	public void setPatientname(String patientname) {
		Patientname = patientname;
	}

	
	public String getPatientemail() {
		return Patientemail;
	}

	
	public void setPatientemail(String patientemail) {
		Patientemail = patientemail;
	}

	
	public String getPatientweixin() {
		return Patientweixin;
	}

	
	public void setPatientweixin(String patientweixin) {
		Patientweixin = patientweixin;
	}


	public String getPatientweibo() {
		return Patientweibo;
	}


	public void setPatientweibo(String patientweibo) {
		Patientweibo = patientweibo;
	}


	public Integer getPatientage() {
		return Patientage;
	}

	
	public void setPatientage(Integer patientage) {
		Patientage = patientage;
	}

	
	public String getPatientbirthday() {
		return Patientbirthday;
	}

	
	public void setPatientbirthday(String patientbirthday) {
		Patientbirthday = patientbirthday;
	}

	
	public String getPatienticon() {
		return Patienticon;
	}

	
	public void setPatienticon(String patienticon) {
		Patienticon = patienticon;
	}

	
	public Integer getPatientweight() {
		return Patientweight;
	}

	public void setPatientweight(Integer patientweight) {
		Patientweight = patientweight;
	}

	
	public String getPatientlogname() {
		return Patientlogname;
	}

	
	public void setPatientlogname(String patientlogname) {
		Patientlogname = patientlogname;
	}

	
	public String getPatientlogpasswd() {
		return Patientlogpasswd;
	}

	
	public void setPatientlogpasswd(String patientlogpasswd) {
		Patientlogpasswd = patientlogpasswd;
	}

	
	public String getPatientgender() {
		return Patientgender;
	}

	
	public void setPatientgender(String patientgender) {
		Patientgender = patientgender;
	}

	
	public String getPatientsubhospital() {
		return Patientsubhospital;
	}

	
	public void setPatientsubhospital(String patientsubhospital) {
		Patientsubhospital = patientsubhospital;
	}

	
	public Integer getPatientremember() {
		return Patientremember;
	}

	
	public void setPatientremember(Integer patientremember) {
		Patientremember = patientremember;
	}

	
	public String getMyselfdoctor() {
		return myselfdoctor;
	}

	
	public void setMyselfdoctor(String myselfdoctor) {
		this.myselfdoctor = myselfdoctor;
	}

	
	public static boolean isIslogin() {
		return islogin;
	}

	
	public static void setIslogin(boolean islogin) {
		PatientSet.islogin = islogin;
	}

	
	public Integer getPatientcaptcha() {
		return Patientcaptcha;
	}

	
	public void setPatientcaptcha(Integer patientcaptcha) {
		Patientcaptcha = patientcaptcha;
	}

	
	public Integer getPatientheight() {
		return Patientheight;
	}

	
	public void setPatientheight(Integer patientheight) {
		Patientheight = patientheight;
	}

	
	/**
	 * @return the patientlogaddr
	 */
	public String getPatientlogaddr() {
		return Patientlogaddr;
	}

	/**
	 * @param patientlogaddr the patientlogaddr to set
	 */
	public void setPatientlogaddr(String patientlogaddr) {
		Patientlogaddr = patientlogaddr;
	}

	@Override
	public String toString() {
		return "PatientSet [Patientid=" + Patientid + ", Patientphonenumber="
				+ Patientphonenumber + ", Patientaddr=" + Patientaddr
				+ ", longitude=" + longitude + ", latitude=" + latitude
				+ ", Patientqq=" + Patientqq + ", Patientname=" + Patientname
				+ ", Patientemail=" + Patientemail + ", Patientweixin="
				+ Patientweixin + ", Patientweibo=" + Patientweibo
				+ ", Patientage=" + Patientage + ", Patientbirthday="
				+ Patientbirthday + ", Patienticon=" + Patienticon
				+ ", Patientweight=" + Patientweight + ", Patientheight="
				+ Patientheight + ", Patientlogname=" + Patientlogname
				+ ", Patientlogpasswd=" + Patientlogpasswd + ", Patientgender="
				+ Patientgender + ", Patientsubhospital=" + Patientsubhospital
				+ ", Patientremember=" + Patientremember + ", myselfdoctor="
				+ myselfdoctor + ", Patientcaptcha=" + Patientcaptcha + "]";
	}


    
   
}

