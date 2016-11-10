package com.gzmelife.app.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.gzmelife.app.bean.DeviceNameAndIPBean;
import com.gzmelife.app.bean.UserInfoBean;

public class SharedPreferenceUtil {
	public final static String USER_INFO = "user_info";  // 文件名
	
	public final static String FIRST_USE = "first_use";
	public final static String LOGIN_USER = "login_user";
	public final static String USER_ACCOUNT = "user_account";
	public final static String USER_PWD = "user_pwd";
	private final static String PUSH_SWITCH = "push_switch";
	private final static String LoginState = "LoginState";
	
	private final static String PMS_NAME = "pms_name";
	private final static String PMS_WIFI_NAME = "pms_wifi_name";
	private final static String PMS_IP = "pms_ip";

	/**
	 * 设置连接PMS信息
	 */
	public static void setPmsInfo(Context context, DeviceNameAndIPBean bean) {
		SharedPreferences shared = context.getSharedPreferences(USER_INFO, 0);
		SharedPreferences.Editor editor = shared.edit();

		editor.putString(PMS_NAME, bean.getName());
		editor.putString(PMS_WIFI_NAME, bean.getWifiName());
		editor.putString(PMS_IP, bean.getIp());
		editor.commit();
	}
	
	/**
	 * 获取PMS信息:pmsName/pmsWifiName/pmsIp
	 */
	public static DeviceNameAndIPBean getPmsInfo(Context context) {
		SharedPreferences shared = context.getSharedPreferences(USER_INFO, 0);
		DeviceNameAndIPBean bean = new DeviceNameAndIPBean();
		bean.setName(shared.getString(PMS_NAME, ""));
		bean.setWifiName(shared.getString(PMS_WIFI_NAME, ""));
		bean.setIp(shared.getString(PMS_IP, ""));
		if (TextUtils.isEmpty(bean.getIp())) {
			return null;
		} else {
			return bean;
		}
	}
	
	/**
	 * 获取账户
	 */
	public static String getUserAccount(Context context) {
		SharedPreferences shared = context.getSharedPreferences(USER_INFO, 0);
		return shared.getString(USER_ACCOUNT, "");
	}
	
	/**
	 * 获取密码
	 */
	public static String getUserPwd(Context context) {
		SharedPreferences shared = context.getSharedPreferences(USER_INFO, 0);
		return shared.getString(USER_PWD, "");
	}
	
	/**
	 * 设置账户
	 */
	public static void setUserAccount(Context context, String account) {
		SharedPreferences shared = context.getSharedPreferences(USER_INFO, 0);
		SharedPreferences.Editor editor = shared.edit();

		editor.putString(USER_ACCOUNT, account);
		editor.commit();
	}
	
	/**
	 * 设置密码
	 */
	public static void setUserPwd(Context context, String pwd) {
		SharedPreferences shared = context.getSharedPreferences(USER_INFO, 0);
		SharedPreferences.Editor editor = shared.edit();

		editor.putString(USER_PWD, pwd);
		editor.commit();
	}
	
	/**
	 * 获取推送开关，1：开，0：关
	 */
	public static int getPushSwitch(Context context) {
		SharedPreferences shared = context.getSharedPreferences(USER_INFO, 0);
		return shared.getInt(PUSH_SWITCH, 1);
	}

	/**
	 * 保存推送设置，1：开，0：关
	 */
	public static void setPushSwitch(Context context, int flag) {
		SharedPreferences shared = context.getSharedPreferences(USER_INFO, 0);
		SharedPreferences.Editor editor = shared.edit();

		editor.putInt(PUSH_SWITCH, flag);
		
		editor.commit();
	}
	
	public static boolean isFirstUse(Context context){
		SharedPreferences shared = context.getSharedPreferences(USER_INFO, 0);
		return shared.getInt(FIRST_USE, 0) == 0;
	}
	
	public static void setUsed(Context context){
		SharedPreferences shared = context.getSharedPreferences(USER_INFO, 0);
		SharedPreferences.Editor edit = shared.edit();
		edit.putInt(FIRST_USE, 1);
		edit.commit();
	}
	
	public static void setUser(Context context,UserInfoBean user){
		SharedPreferences shared = context.getSharedPreferences(USER_INFO, 0);
		SharedPreferences.Editor edit = shared.edit();
		if(user==null){
			edit.putString(LOGIN_USER, "");
		}else {
			Gson g = new Gson();
			String gStr = g.toJson(user);
			edit.putString(LOGIN_USER, gStr);
		}
		edit.commit();
	}
	public static UserInfoBean getUser(Context context){
		SharedPreferences shared = context.getSharedPreferences(USER_INFO, 0);
		String json = shared.getString(LOGIN_USER, null);
		if(TextUtils.isEmpty(json)){
			return null;
		}else {
			Gson g = new Gson();
			return g.fromJson(json, UserInfoBean.class);
		}
	}
	
	public static int getLoginState(Context context){
		SharedPreferences shared = context.getSharedPreferences(USER_INFO, 0);
		return shared.getInt(LoginState, 0);
	}
	
	public static void setLoginState(Context context,int state){
		SharedPreferences shared = context.getSharedPreferences(USER_INFO, 0);
		SharedPreferences.Editor edit = shared.edit();
		edit.putInt(LoginState, state);
		edit.commit();
	}
}
