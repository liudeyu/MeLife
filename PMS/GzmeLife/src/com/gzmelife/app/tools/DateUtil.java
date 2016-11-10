package com.gzmelife.app.tools;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;

@SuppressLint("SimpleDateFormat")
public class DateUtil {
	/**
	 * 返回该日期与 1970 年 1 月 1 日午夜之间相差的毫秒数
	 */
	@SuppressWarnings("deprecation")
	public static long getLongTime(Date date) {
		return Date.parse(date.toString());
	}
	
	/**
	 * 返回当前日期日期与 1970 年 1 月 1 日午夜之间相差的毫秒数
	 */
	@SuppressWarnings("deprecation")
	public static long getLongTime() {
		return Date.parse(new Date().toString());
	}
	
	/**
	 * 返回当前日期数组数据，年月日时分秒 的顺序，年减去了2000，用于PMS计算
	 */
	public static byte[] getCurrentTime(int min) {
		long time = System.currentTimeMillis() - 1000 * 60 * min;  
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");  
        Date d1 = new Date(time);  
        String[] t1 = format.format(d1).split("-");  
        byte[] bytes = new byte[6];
        for (int i = 0; i < t1.length; i++) {
        	if (i == 0) {
        		bytes[i] = Byte.parseByte(Integer.parseInt(t1[i]) - 2000 + "");
        	} 
//        	else if (i == 3) {
//        		bytes[i] = 0x09;
//        	} 
        	else {
        		bytes[i] = Byte.parseByte(t1[i]);
        	}
        }
		return bytes;
	}
	
	
	/**
	 * 返回当前日期数组数据，年月日时分秒 的顺序，年减去了2000，用于PMS计算
	 * ss-mm-HH-dd-MM-yyyy
	 */
	public static byte[] getCurrentTime() {
		long time = System.currentTimeMillis() ;  
        SimpleDateFormat format = new SimpleDateFormat("ss-mm-HH-dd-MM-yyyy");  
        Date d1 = new Date(time);  
        String[] t1 = format.format(d1).split("-");  
        byte[] bytes = new byte[6];
        for (int i = 0; i < t1.length; i++) {
        	if (i == t1.length-1) {
        		bytes[i] = Byte.parseByte(Integer.parseInt(t1[i]) - 2000 + "");
        	} 
//        	else if (i == 3) {
//        		bytes[i] = 0x09;
//        	} 
        	else {
        		bytes[i] = Byte.parseByte(t1[i]);
        	}
        }
		return bytes;
	}
	
	/**
	 * 返回当前日期减去min分钟后的时间 yyyy-MM-dd HH:mm:ss
	 */
	public static String getCurrentTimeString(int min) {
		long time = System.currentTimeMillis() - 1000 * 60 * min;  
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
        Date d1 = new Date(time);  
        return format.format(d1);
	}
	
	/**
	 * 根据音乐文件的时长毫秒数转化为 03:14 这样的格式
	 */
	public static String getMusicDurationOfMinute(long time) {
		time /= 1000; // 得到秒数
		int minute = (int) (time / 60); 
		int second = (int) (time % 60);
		return DataUtil.addZeroBeforeNumber(minute) + ":" + DataUtil.addZeroBeforeNumber(second);
	}
	
	/**
	 * 根据文件的时长毫秒数转化为 02:03:14 这样的格式
	 */
	public static String getMusicDuration(long time) {
		time /= 1000; // 得到秒数
		int minute = (int) (time / 60); 
		int second = (int) (time % 60);
		int hour = (int) (minute / 60); 
		minute %= 60;
		return DataUtil.addZeroBeforeNumber(hour) + ":" + DataUtil.addZeroBeforeNumber(minute) + ":" + DataUtil.addZeroBeforeNumber(second);
	}
	
	/**
	 * 根据时长秒数转化为 02:03:14 这样的格式
	 */
	public static String getDuration(long time) {
		int minute = (int) (time / 60); 
		int second = (int) (time % 60);
		int hour = (int) (minute / 60); 
		minute %= 60;
		return DataUtil.addZeroBeforeNumber(hour) + ":" + DataUtil.addZeroBeforeNumber(minute) + ":" + DataUtil.addZeroBeforeNumber(second);
	}
	
	/** 传入时分参数，12:02，得到当前年月日下，12:02:00的毫秒数 */
	public static long getLongTime(String time) {
		String[] t = time.split(":");
		Calendar calendar = Calendar.getInstance();
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 
				calendar.get(Calendar.DAY_OF_MONTH), Integer.parseInt(t[0]), Integer.parseInt(t[1]), 0);
		return calendar.getTimeInMillis();
	}
	
	/**
     * 将十六进制的字符串转化为十进制的数值
     */
    public static int HexToDec(String hexStr) {
        Map<String, Integer> hexMap = prepareDate(); // 先准备对应关系数据
        int length = hexStr.length();
        int result = 0; // 保存最终的结果
        for (int i = 0; i < length - 1; i++) {
            result += hexMap.get(hexStr.subSequence(i, i + 1)) * Math.pow(16, length - 1 - i);
        }
        System.out.println("hexStr=" + hexStr + ",result=" + result);
        return result;
    }
 
    /**
     * 准备十六进制字符对应关系。如("1",1)...("A",10),("B",11)
     */
    public static HashMap<String, Integer> prepareDate() {
        HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
        for (int i = 1; i <= 9; i++) {
            hashMap.put(i + "", i);
        }
        hashMap.put("a", 10);
        hashMap.put("b", 11);
        hashMap.put("c", 12);
        hashMap.put("d", 13);
        hashMap.put("e", 14);
        hashMap.put("f", 15);
        return hashMap;
    }
}