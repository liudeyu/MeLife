package com.gzmelife.app.tools;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.gzmelife.app.hhd.TimeNode;

/**
 * 20161012PMS菜谱结构
 */
public class PmsFile {
	String TAG = "PmsFile";
	/** 文件头 录波数据起始地址 4字节（低字节在前，高字节在后，下同） */
	public final int offsetLuboStart = 0;
	/** 录波数据大小N 4字节 */
	public final int offsetLuboLenth = 4;
	/** 时间节点事件起始地址 4字节 */
	public final int offsettimesStart = 8;
	/** 时间节点事件数据大小 4字节 */
	public final int offsettimesLenth = 12;
	/** 图片起始地址 4字节 */
	public final int offsetJPGStart = 16;
	/** 图片数据大小M 4字节 */
	public final int offsetJPGLenth = 20;
	/** 菜谱描述起始地址 4字节 */
	public final int offsetCaiStart = 24;
	/** 菜谱描述数据大小K 4字节 */
	public final int offsetCaiLenth = 28;
	/** 菜谱ID号 4字节 */
	public final int offsetID = 32;
	/** 炉子ID 4字节 */
	public final int offsetPMSID = 36;

	/** 保留 4字节 */
	public final int offsetRes1 = 40;
	/** 保留 4字节 */
	public final int offsetRes2 = 44;
	/** 保留 4字节 */
	public final int offsetRes3 = 48;
	/** 保留 4字节 */
	public final int offsetRes4 = 52;
	/** 保留 4字节 */
	public final int offsetRes5 = 56;
	/** 保留 4字节 */

	// 录波数据 录波数据 N字节
	// 图片数据 图片数据 M字节
	// 菜谱描述 菜谱文字描述 K字节
	public byte[] PMS_Data;// 原始解析数据
	private int LuboStart;
	private int LuboLenth;
	private int timestart;
	private int TimeLenth;
	private int JPGStart;
	private int JPGLenth;
	private int CaiStart;
	private int CaiLenth;
	/** 菜谱ID */
	public int ID;//
	public int PMSID;
	public int[] Res = new int[5];// 保留
	/** 录波数组 */
	public byte[] bufLubo;//
	/** 时间节点数组 */
	public byte[] bufTime;//
	/** 图像数据 */
	public byte[] bufJPG;//
	/** 菜谱描述数据 */
	public byte[] bufCai;//
	/** 图像 */
	public Bitmap bitmap;//
	/** 菜谱描述 */
	public String text;
	/** 时间节点容器//20161011时间(2)+重量(2)+提示(36)+食材id(4)+【食材名称(16)+食材重量(4)】 */
	public List<TimeNode> listTimeNode;
	public PmsFile(byte[] bufData) {
		this.PMS_Data = bufData;
		JX();
	}
	public PmsFile() {
		listTimeNode = new ArrayList<TimeNode>();
	}
	public void setJPG(Bitmap bitmap) {
		this.bitmap = bitmap;
		updateJPGBuf();
	}
	public void setText(String text) {
		this.text = text;
		try {
			updateCaiBuf();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	// 将Image写入到图像数组
	public void updateJPGBuf() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
		bufJPG = baos.toByteArray();
	}
	// 将描述写入菜谱描述缓冲数组
	public void updateCaiBuf() throws UnsupportedEncodingException {
		// bufCai = Encoding.GetEncoding("gb2312").GetBytes(text);
		bufCai = text.getBytes("gb2312");
	}
	// 将时间节点写入到缓冲数组
	public void updatetimesBuf() {
		// 当时间节点发生改变时，修改文件有，修改存储数组
		TimeLenth = (int) (listTimeNode.size() * 180);// 更新时间节点长度
		byte[] bufT = new byte[TimeLenth];
		int i = 0;
		for (TimeNode timeNode : listTimeNode) {
			timeNode.updateBuf();// 更新40字节数组
			for (int j = 0; j < 180; j++) {
				bufT[i * 180 + j] = timeNode.bufTime[j];
			}
			i++;
		}
		bufTime = bufT;// 更新存储数组
		System.out.println("bufTime"+bufTime.length);
	}



	public List<TimeNode> getTimeNode(){
		return listTimeNode;
	}

	public void setTimeNode(List<TimeNode> listTimeNode){
		this.listTimeNode = listTimeNode;
		updatetimesBuf();// 更新整个时间数组
	}

	/**
	 * 20161011设值每个时间节点
	 * @param index
	 * @param tips
	 * @param foodIDs
	 * @param foodNames 食材名称(16)
	 * @param foodWgts 食材重量(4)
	 * @param time
	 * @param bufTime
	 */
	public void setTipsTimeNode(int index, String tips,int[] foodIDs, String[] foodNames, int[] foodWgts, int time,byte[] bufTime) {
		TimeNode timeNode = new TimeNode();
		timeNode.foodIDs=foodIDs;
		timeNode.FoodNames = foodNames;
		timeNode.FoodWgts = foodWgts;
		timeNode.times = time;
		timeNode.Tips = tips;
		timeNode.bufTime = bufTime;
		listTimeNode.add(timeNode);
		updatetimesBuf();// 更新整个时间数组
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

	/** 从指定区域读取无符号32位值 */
	int readReg(int offset) {
		int value;

		value = (int) ((PMS_Data[offset] & 0xFF)
				| ((PMS_Data[offset + 1] & 0xFF) << 8)
				| ((PMS_Data[offset + 2] & 0xFF) << 16)
				| ((PMS_Data[offset + 3] & 0xFF) << 24));
		return value;
	}

	void JX() {
		// 根据偏移量读取数据值
		LuboStart = readReg(offsetLuboStart);
		LuboLenth = readReg(offsetLuboLenth);
		timestart = readReg(offsettimesStart);
		TimeLenth = readReg(offsettimesLenth);
		JPGStart = readReg(offsetJPGStart);
		JPGLenth = readReg(offsetJPGLenth);
		CaiStart = readReg(offsetCaiStart);
		CaiLenth = readReg(offsetCaiLenth);
		Log.i(TAG, "LuboStart:"+LuboStart+
				" LuboLenth:"+LuboLenth
				+"  timestart:"+timestart
				+" TimeLenth"+TimeLenth
				+" JPGStart:" + JPGStart
				+"JPGLenth:"+JPGLenth
				+ "CaiStart:"+ CaiStart+" CaiLenth:"+CaiLenth);
		ID = readReg(offsetID);
		PMSID = readReg(offsetPMSID);
		Res[0] = readReg(offsetRes1);
		Res[1] = readReg(offsetRes2);
		Res[2] = readReg(offsetRes3);
		Res[3] = readReg(offsetRes4);
		Res[4] = readReg(offsetRes5);
		try {
			// 分离录波数组
			bufLubo = new byte[LuboLenth];
			for (int i = 0; i < LuboLenth; i++) {
				bufLubo[i] = PMS_Data[i + LuboStart];
			}
			// 分离时间节点数组
			bufTime = new byte[TimeLenth];
			for (int i = 0; i < TimeLenth; i++) {
				bufTime[i] = PMS_Data[i + timestart];
			}
			// 将时间节点数据存储到listTimeNode
			listTimeNode = new ArrayList<TimeNode>();
			for (int i = 0; i < bufTime.length / 180; i++) {
				TimeNode tn = new TimeNode(bufTime, 180 * i);
				listTimeNode.add(tn);
			}
			if (JPGLenth > 0) {
				// 分离图像数组
				bufJPG = new byte[JPGLenth];
				for (int i = 0; i < JPGLenth; i++) {
					bufJPG[i] = PMS_Data[i + JPGStart];
				}
				bitmap = BitmapFactory
						.decodeByteArray(bufJPG, 0, bufJPG.length);
			}
			if (CaiLenth > 0) {
				// 分离菜谱数组
				bufCai = new byte[CaiLenth];
				for (int i = 0; i < CaiLenth; i++) {
					bufCai[i] = PMS_Data[i + CaiStart];
				}
				// 存储菜谱数组到Descripstion
				text = new String(bufCai, "gb2312");
			}
		} catch (Exception ex) {
			return;
		}
	}
	public void savaPMSData() {
		if (bufTime != null)
			TimeLenth = (int) bufTime.length;
		System.out.println("TimeLenth"+TimeLenth);
		if (bufLubo != null)
			LuboLenth = (int) bufLubo.length;
		if (bufJPG != null)
			JPGLenth = (int) bufJPG.length;
		if (bufCai != null)
			CaiLenth = (int) bufCai.length;
		int fileLenth = (int) (60 + LuboLenth + TimeLenth + JPGLenth + CaiLenth);// 数据总长度
		byte[] bufFile = new byte[fileLenth];// 定义一个数组
		LuboStart = 60;
		timestart = LuboStart + LuboLenth;
		JPGStart = timestart + TimeLenth;
		CaiStart = JPGStart + JPGLenth;
		System.arraycopy(convertToBytes(LuboStart), 0, bufFile,
				offsetLuboStart, 4);
		System.arraycopy(convertToBytes(LuboLenth), 0, bufFile,
				offsetLuboLenth, 4);
		System.arraycopy(convertToBytes(timestart), 0, bufFile,
				offsettimesStart, 4);
		System.arraycopy(convertToBytes(TimeLenth), 0, bufFile,
				offsettimesLenth, 4);
		System.arraycopy(convertToBytes(JPGStart), 0, bufFile, offsetJPGStart,
				4);
		System.arraycopy(convertToBytes(JPGLenth), 0, bufFile, offsetJPGLenth,
				4);
		System.arraycopy(convertToBytes(CaiStart), 0, bufFile, offsetCaiStart,
				4);
		System.arraycopy(convertToBytes(CaiLenth), 0, bufFile, offsetCaiLenth,
				4);
		System.arraycopy(convertToBytes(ID), 0, bufFile, offsetID, 4);
		System.arraycopy(convertToBytes(PMSID), 0, bufFile, offsetPMSID, 4);
		System.arraycopy(convertToBytes(Res[0]), 0, bufFile, offsetRes1, 4);
		System.arraycopy(convertToBytes(Res[1]), 0, bufFile, offsetRes2, 4);
		System.arraycopy(convertToBytes(Res[2]), 0, bufFile, offsetRes3, 4);
		System.arraycopy(convertToBytes(Res[3]), 0, bufFile, offsetRes4, 4);
		System.arraycopy(convertToBytes(Res[4]), 0, bufFile, offsetRes5, 4);
		if (bufLubo != null)
			System.arraycopy(bufLubo, 0, bufFile, LuboStart, LuboLenth);
		if (bufTime != null)
			System.arraycopy(bufTime, 0, bufFile, timestart, TimeLenth);
		if (bufJPG != null)
			System.arraycopy(bufJPG, 0, bufFile, JPGStart, JPGLenth);
		if (bufCai != null)
			System.arraycopy(bufCai, 0, bufFile, CaiStart, CaiLenth);

		PMS_Data = bufFile;
	}
	public byte[] getBufLubo() {
		return bufLubo;
	}
	public void setBufLubo(byte[] bufLubo) {
		this.bufLubo = bufLubo;
	}









//20161010以下为没用到的方法（函数）
	public void inserTimeNode(int index, TimeNode timeNode) {
		// 在bufTime的指定位置插入一个时间节点
		listTimeNode.add(index, timeNode);
		updatetimesBuf();
	}
	public void inserTimeNode(TimeNode timeNode) {
		// 在bufTime的指定位置插入一个时间节点
		// 根据新节点的时间，自动判别插入位置
		int index = 0;
		while (index < listTimeNode.size()
				&& timeNode.times > listTimeNode.get(index).times) {
			index++;
		}
		listTimeNode.add(index, timeNode);
		updatetimesBuf();
	}

	public void deletTimeNode(int index) {
		// Bitmap bp = new Bitmap();
		// 删除时间节点
		listTimeNode.remove(index);
		updatetimesBuf();
	}
	private static int ConvertToint(byte[] bufT, int offset) {
		// 将4字节数组转换成32位无符号数据
		int value = 0;
		value = (int) ((bufT[offset] & 0xFF) | ((bufT[offset + 1] & 0xFF) << 8) | ((bufT[offset + 2] & 0xFF) << 16) | ((bufT[offset + 3] & 0xFF) << 24));
		return value;
	}
}
