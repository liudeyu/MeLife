package com.gzmelife.app.tools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;


public class  WIFITOOL {

public static  String getCurConnectWfName(Activity activity){
	String ret=null;
	
	if(activity==null)
		return null;
	WifiManager wfMagager=(WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
	int wifiState=wfMagager.getWifiState();
	WifiInfo wfInfor=wfMagager.getConnectionInfo();
	ret=wfInfor!=null?wfInfor.getSSID():null;
	return ret;
}
public static void openWifiSetting(Activity activity){
	Intent intern=new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
	activity.startActivity(intern);
}
}
