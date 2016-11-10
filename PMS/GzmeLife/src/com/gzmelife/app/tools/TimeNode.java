package com.gzmelife.app.tools;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/** 20161012封装时间节点 */
public class TimeNode implements Serializable{
	// 时间节点1 2字节
	// 总重量变化 2字节（重量增加或减少，大于10000则是减小）
	// 事件提示汉字1 36字节用于PMS液晶显示文字提示）
	// 食材一 ID 4字节
	// 食材一名称 16字节
	// 食材一 重量 4字节
	// 。。。。。。 。。。。。。
	// 食材五 ID 4字节
	// 食材五 名称 16字节
	// 食材五 重量 4字节
	// 保留字段1 4字节
	// 保留字段2 4字节
	// 保留字段3 4字节
	// 保留字段4 4字节
	// 保留字段5 4字节
	private static final long serialVersionUID = 1L;
	public byte[] bufTime = new byte[180];
	/** 每步骤的时间节点（单位：秒） */
	public int times = 0;
	/** 重量变化 */
	public int wetsTemp = 0;
	/** 文字提示，最大36字节空间，18汉字 */
	public String Tips = "";
	public int[] foodIDs = new int[5];
	/** 20161011食材名称（最多5个食材） */
	public String[] FoodNames = new String[5];
	/** 20161011食材重量 */
	public int[] FoodWgts = new int[5];

	public List foodNames=new ArrayList<String>();
	public String[] NewFoodNames = new String[5];
	public int[] Res = new int[5];
	/** 使用数组构造时间节点,并且解析 */
	public TimeNode(byte[] bufTime, int index) {
		for (int i = 0; i < 180; i++) {
			this.bufTime[i] = bufTime[i + index];
		}
		updateString();
	}
	/** 使用数组构造时间节点 */
	public TimeNode(byte[] bufTime) {
		this.bufTime = bufTime;
		updateString();
	}
	/** 使用参数构造时间节点 */
	public TimeNode() {
		//
	}
	/** 从菜谱数解析参数 */
	public void updateString() {
		times = ((bufTime[0] & 0xFF) | ((bufTime[1] & 0xFF) << 8));
		wetsTemp = ((bufTime[2] & 0xFF) | ((bufTime[3] & 0xFF) << 8));
		try {
			// Tips = Encoding.GetEncoding("gb2312").GetString(bufTime, 4,
			// 36);
			Tips = new String(bufTime, 4, 36, "gb2312");
			// Tips = Tips.Remove(Tips.indexOf("\0"));//删除多余的填充0
			Tips = Tips.replace("\0", "");
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		for (int i = 0; i < 5; i++) {
			foodIDs[i] = ConvertToint(bufTime, 40 + 24 * i);
			if (FoodNames != null) {//20161013食材名称
				try {
					FoodNames[i] = new String(bufTime, 40 + 4 + 24 * i, 16, "gb2312");
					FoodNames[i] = FoodNames[i].replace("\0", "");
				} catch (Exception exception) {
					exception.printStackTrace();
					;
				}
			}
			FoodWgts[i] = ConvertToint(bufTime, 40 + 20 + 24 * i);
		}

		for (int i = 0; i < 5; i++) {
			Res[i] = ConvertToint(bufTime, 160 + 4 * i);
		}
	}
	/** 从参数组成菜谱数组 */
	public void updateBuf() {
		// Array.Clear(bufTime, 0, bufTime.length);//清空数组
		bufTime = new byte[bufTime.length];
		// 写时间点
		bufTime[0] = (byte) (times & 0xFF);
		bufTime[1] = (byte) ((times )>> 8);
		//bufTime[1] = (byte) ((times & 0xFF) << 8);
		// 写重量变化数据
		bufTime[2] = (byte) (wetsTemp & 0xFF);
		//bufTime[3] = (byte) ((wetsTemp & 0xFF) << 8);
		bufTime[3] = (byte) ((wetsTemp) >> 8);
		byte[] bufTips = null;
		try {
			bufTips = Tips.getBytes("gb2312");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		for (int i = 0; i < (bufTips.length > 36 ? 36 : bufTips.length); i++) // 最大长度是36字节
		{
			bufTime[4 + i] = bufTips[i];
		}

		/**
		 * 写食材库 Lotus 2016-07-26 修改
		 */
		//for (int i = 0; i < 5; i++) {
		for (int i = 0; i < foodIDs.length; i++) {
			System.arraycopy(convertToBytes(foodIDs[i]), 0, bufTime,
					40 + 24 * i, 4);
		
		}
		for(int j=0;j<FoodNames.length;j++){
			if (FoodNames[j] != null) {
				byte[] bufName = null;
				try {
					bufName = FoodNames[j].getBytes("gb2312");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				System.arraycopy(bufName, 0, bufTime, 40 + 4 + 24 * j,
						bufName.length > 16 ? 16 : bufName.length);
			}
			System.arraycopy(convertToBytes(FoodWgts[j]), 0, bufTime,
					40 + 20 + 24 * j, 4);
		}
		// 写保留数据
		for (int i = 0; i < 5; i++) {
			System.arraycopy(convertToBytes(Res[i]), 0, bufTime,
					160 + 4 * i, 4);
		}
	}
	private static byte[] convertToBytes(int value) {
		// 将无符号32位数据转换成4字节数组
		byte[] abyte = new byte[4];
		// "&" 与（AND），对两个整型操作数中对应位执行布尔代数，两个位都为1时输出1，否则0。
		abyte[0] = (byte) (0xff & value);
		// ">>"右移位，若为正数则高位补0，若为负数则高位补1
		abyte[1] = (byte) ((0xff00 & value) >> 8);
		abyte[2] = (byte) ((0xff0000 & value) >> 16);
		abyte[3] = (byte) ((0xff000000 & value) >> 24);
		return abyte;
	}
	private static int ConvertToint(byte[] bufT, int offset) {
		// 将4字节数组转换成32位无符号数据
		int value = 0;
		value = (int) ((bufT[offset] & 0xFF) | ((bufT[offset + 1] & 0xFF) << 8) | ((bufT[offset + 2] & 0xFF) << 16) | ((bufT[offset + 3] & 0xFF) << 24));
		return value;
	}
}