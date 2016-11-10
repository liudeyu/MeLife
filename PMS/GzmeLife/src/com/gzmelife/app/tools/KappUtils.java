package com.gzmelife.app.tools;

import java.security.MessageDigest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gzmelife.app.R;
import com.gzmelife.app.device.Config;

@SuppressLint("InflateParams")
public class KappUtils {
	public static final String FLAG_FAQ_DETAIL_1 = "FAQ_detail_1";
	public static final String FLAG_FAQ_DETAIL = "FAQ_detail";
	public static final String FLAG_ABOUT_US = "about_us";
	public static final String FLAG_SYSTEM_MSG_DETAIL = "system_msg_detail";
	
	public static final String ACTION_PMS_STATUS = "com.gzmelife.app.action.pmsstatus";
	 private static final char[] DIGITS_LOWER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
         'e', 'f'};
	public static void showToast(Context context, String content) {
		if(context != null && content != null){
			Toast toast = new Toast(context);
			View view=LayoutInflater.from(context).inflate(R.layout.layout_toast, null);
			TextView tv=(TextView) view.findViewById(R.id.tv);
			tv.setText(content);
			toast.setView(view);
//			toast.setGravity(Gravity.BOTTOM, 0, DensityUtil.dip2px(context, 60));
			toast.show();
		}
	}
	
	public static void getLocalIP(Context context) {
		// 获取wifi服务
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		// 判断wifi是否开启
		if (!wifiManager.isWifiEnabled()) {
			// wifiManager.setWifiEnabled(true);
			showToast(context, "wifi未连接");
		}
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();
		Config.LOCAL_IP = DataUtil.getIp(ipAddress);
		Config.clientPort = DataUtil.getIpEndByte(ipAddress);
		Config.BROADCAST_IP = Config.LOCAL_IP.substring(0, Config.LOCAL_IP.lastIndexOf('.') + 1) + "255";
//		MyLog.i(MyLog.TAG_I_INFO, "Config.BROADCAST_IP = " + Config.BROADCAST_IP);
		MyLog.i(MyLog.TAG_I_INFO, "本机IP:" + Config.LOCAL_IP);
	}
	
	 public static String md5(String str) {
	        if (str == null) {
	            return null;
	        }
	        try {
	            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
	            messageDigest.update(str.getBytes());
	            return new String(encodeHex(messageDigest.digest()));
	        } catch (Exception e) {
	            throw new RuntimeException(e);
	        }
	    }

	    /**
	     * Converts an array of bytes into an array of characters representing the hexadecimal values of each byte in order.
	     * The returned array will be double the length of the passed array, as it takes two characters to represent any
	     * given byte.
	     *
	     * @param data a byte[] to convert to Hex characters
	     * @return A char[] containing hexadecimal characters
	     */
	    protected static char[] encodeHex(final byte[] data) {
	        final int l = data.length;
	        final char[] out = new char[l << 1];
	        // two characters form the hex value.
	        for (int i = 0, j = 0; i < l; i++) {
	            out[j++] = DIGITS_LOWER[(0xF0 & data[i]) >>> 4];
	            out[j++] = DIGITS_LOWER[0x0F & data[i]];
	        }
	        return out;
	    }
}
