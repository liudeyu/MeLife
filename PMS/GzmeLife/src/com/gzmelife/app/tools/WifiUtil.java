package com.gzmelife.app.tools;

import java.util.List;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class WifiUtil {
	private static WifiManager mWm;
	
	/** 打开wifi开关，且返回之前的wifi开关状态 */
	public static boolean openWifi(Context context) {
		boolean result;
		mWm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		result = mWm.isWifiEnabled();
		if (!result) {
			mWm.setWifiEnabled(true);
		}
		return result;
	}
	
	public static WifiInfo getWifiInfo() {
		return mWm.getConnectionInfo();
	}
	
	// 扫描网络
    public static void startScan() {
    	mWm.startScan();
    }
	
	public static boolean connectWifi(String ssid, Boolean disableOthers) {
		MyLog.d("ssid=" + ssid);
		WifiConfiguration config = createWifiConfiguration(ssid);
//		mWm.addNetwork(config); 
		return mWm.enableNetwork(mWm.addNetwork(config), disableOthers);
	}
	
	public static boolean connectWifi(WifiConfiguration config, Boolean disableOthers) {
		MyLog.d("ssid=" + config.SSID);
		return mWm.enableNetwork(config.networkId, disableOthers);
	}
	
	public static int getWifiStatus(Context context) {
		mWm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		return mWm.getWifiState();
	}
	
	public static List<ScanResult> getWifiList() {
//		List<ScanResult> wifiResult = mWm.getScanResults();
		return mWm.getScanResults();
	}
	
	public static List<WifiConfiguration> getWifiConfigurationList() {
		return mWm.getConfiguredNetworks();
	}
	
	public static WifiConfiguration createWifiConfiguration(String SSID/*, String Password, int Type*/) {  
          WifiConfiguration config = new WifiConfiguration();    
          config.allowedAuthAlgorithms.clear();  
          config.allowedGroupCiphers.clear();  
          config.allowedKeyManagement.clear();  
          config.allowedPairwiseCiphers.clear();  
          config.allowedProtocols.clear();  
          config.SSID = "\"" + SSID + "\"";    
           
          WifiConfiguration tempConfig = isExsits(SSID);            
          if (tempConfig != null) {   
              mWm.removeNetwork(tempConfig.networkId);   
          } 
           
//          if(Type == 1) { //WIFICIPHER_NOPASS 
//               config.wepKeys[0] = "";  
               config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);  
//               config.wepTxKeyIndex = 0;  
//          }  
//          if(Type == 2) { //WIFICIPHER_WEP 
//              config.hiddenSSID = true; 
//              config.wepKeys[0]= "\""+Password+"\"";  
//              config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);  
//              config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);  
//              config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);  
//              config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);  
//              config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);  
//              config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);  
//              config.wepTxKeyIndex = 0;  
//          }  
//          if(Type == 3) { //WIFICIPHER_WPA 
//	          config.preSharedKey = "\""+Password+"\"";  
//	          config.hiddenSSID = true;    
//	          config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);    
//	          config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);                          
//	          config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);                          
//	          config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);                     
//	          //config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);   
//	          config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP); 
//	          config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP); 
//	          config.status = WifiConfiguration.Status.ENABLED;    
//          } 
          return config;  
    }  
	
	private static WifiConfiguration isExsits(String SSID) {   
        List<WifiConfiguration> existingConfigs = mWm.getConfiguredNetworks();   
        for (WifiConfiguration existingConfig : existingConfigs) {   
	        if (existingConfig.SSID.equals("\""+SSID+"\"")) {   
	             return existingConfig;   
	        }   
        }   
        return null;    
    }

	public static boolean isEnable(Context context) {
		mWm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		return mWm.getWifiState() == WifiManager.WIFI_STATE_ENABLED;
	} 
}
