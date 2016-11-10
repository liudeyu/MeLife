package com.gzmelife.app.device;

import java.util.ArrayList;
import java.util.List;


/**
 * 设备相关
 * @author chenxiaoyan
 *
 */
public class Config {

	/** 当前心跳的时间 */
	public static int timeCntHeart = 0;
	/** 菜谱-》PMS文件总长度 */
	public static int numDownZie = -1;
	/** 菜谱-》PMS进度长度 */
	public static int numDownNow = -1;
	/**一个文件数据*/
	public static byte[] bufSendFile = new byte[10 * 1024 * 1024];
	/** 帧标记 */
	public static int frmIndex = 0;
	/** 其他用户正在发指令：false=不是别人的指令 */
	public static boolean isOtherInstruction = false;
	/** 20161009取消文件传输：true=取消*/
	public static boolean cancelTransfer = false;


	public static int flag=0;
	/** 本地文件位置 */
	public static int position = 0;
	/** 设备连接状态 */
	public static boolean isConnext = false;
	/** 推送的自定消息ID */
	public static String Id = null;
	/** 菜谱名称 */
	public static String Name = null;
	public static String NewName = null;
	/** 服务器ip   默认IP */
	public static final String SERVER_HOST_DEFAULT_IP = "192.168.4.1";
	/** 真实服务器IP*/
	public static String SERVER_HOST_IP = "192.168.4.1";

	public static final int SERVER_HOST_PORT = 50000; // 端口
	//	public static final int SERVER_HOST_PORT = 9898; // 端口

	/** 服务器名称 */
	public static String SERVER_HOST_NAME = "";

	public static String BROADCAST_IP = "224.0.0.255"; // 手机ip前三位，最后一位固定255
	//手机本地IP
	public static String LOCAL_IP = "224.0.0.1";
	//电磁炉状态
	//电流
	public static String SYSTEM_A = "0A";
	//电压
	public static String SYSTEM_V = "0V";
	//功率
	public static String SYSTEM_W = "0W";
	//电磁炉温度
	public static String PMS_TEMP = "0度";
	//室内温度
	public static String ROOM_TEMP = "0度";
	//状态
	public static String PMS_STATUS = "";
	//电磁炉错误信息
	public static List<String> PMS_ERRORS = new ArrayList<String>();

	public static byte clientPort = -1; // 客户端IP的最后一个字节



	// F0 00 发送/响应帧 配置WiFi模块

	/** F1 00 发送/响应帧 开关机 控制功能，对PMS进行控制 */
	public static byte[] bufOnOFF = { (byte) 0xF1, 0x00 };
	/** F1 01 发送/响应帧 功率减 */
	public static byte[] bufDePower = { (byte) 0xF1, 0x01 };
	/** F1 02 发送/响应帧 功率加 */
	public static byte[] bufInPower = { (byte) 0xF1, 0x02 };
	/** F1 03 发送/响应帧 录波 */
	public static byte[] bufRecorder = { (byte) 0xF1, 0x03 };
	/** F1 04 发送/响应帧 录波反演 */
	public static byte[] bufInversion = { (byte) 0xF1, 0x04 };
	/** F2 00 发送/响应帧 对时功能 */
	public static byte[] bufSetTime = { (byte) 0xF2, 0x00 };

	/** F3 00 发送/响应帧 获取录波文件数量 遍历文件 */
	public static byte[] bufGetFileNum = { (byte) 0xF3, 0x00 };
	/** F3 01 发送/响应帧 查询录波文件列表 */
	public static byte[] bufListFile = { (byte) 0xF3, 0x01 };
	/** F3 01 发送/响应帧 查询录波文件列表结束 */
	public static byte[] bufListFileOver = { (byte) 0xF3, 0x02 };

	/** F4 00 发送/响应帧 获取录波文件大小 上召录波文件*/
	public static byte[] bufFileLenth = { (byte) 0xF4, 0x00 };
	/** F4 01：发送/响应帧 上召录波数据//下载（上送）录波数据 */
	public static byte[] bufFileAck = { (byte) 0xF4, 0x01 };// ok
	/** F4 02：发送/响应帧 录波发送结束 */
	public static byte[] bufFileStop = { (byte) 0xF4, 0x02 };
	/** F4 02：发送/响应帧 中断录波传输 */
	public static byte[] bufFileCancel = { (byte) 0xF4, 0x03 };

	/** F5 00 发送/确认帧 下发录波文件大小 下发录波文件 */
	public static byte[] bufDownFileInfo = { (byte) 0xF5, 0x00 };
	/** F5 01 发送/确认帧 下发录波数据 */
	public static byte[] bufDownFileData = { (byte) 0xF5, 0x01 };
	/** F5 02 发送/确认帧 数据发送结束 */
	public static byte[] bufDownFileStop = { (byte) 0xF5, 0x02 };
	/** F5 02 发送/确认帧 数据发送结束 */
	public static byte[] bufDownFileCancel = { (byte) 0xF5, 0x03 };

	/** F6 00 发送/确认帧 删除装置生成录波文件操作 删除文件 */
	public static byte[] bufDelSelfFile = { (byte) 0xF6, 0x00 };
	/** F6 01 发送/确认帧 删除APP下载菜谱文件操作 */
	public static byte[] bufDelDownFile = { (byte) 0xF6, 0x01 };

	/** F7 00 发送/响应帧 查询状态，包括功率、温度等 */
	public static byte[] bufStatus = { (byte) 0xF7, 0x00 };

	/** F8 00 发送/响应帧 连接确认报文，回复PMS的MAC */
	public static byte[] bufConnect = { (byte) 0xF8, 0x00 };
	// 01 发送/响应帧 非主客户端抢占控制权，成为主客户端。 无效，只能连接一个手机，后面连接的自动得到控制权
	//	public static byte[] bufGrap = { (byte) 0xF8, 0x01 };
	/** F8 02 发送/响应帧 心跳报文 */
	public static byte[] bufHearbeat = { (byte) 0xF8, 0x02 };// 连接

	/*public static int[] errorCode = {
			0x0001,// IGBT过温
			0x0002,// IGBT过压
			0x0004,// 系统电压异常
			0x0008,// 电流异常
			0x0010,// 无锅
			0x0020,// 线圈过热
			0x0040,// IR(红外测温) Err
			0x0080,// SD错误
			0x0100,// 反演暂时停机
			0x0200 // 串口接收数据错误
	};*/

	public static int[] errorCode = {
			0x0001,//"  IGBT温度过高  ",
			0x0002,//"  IGBT过压保护  ",
			0x0004,//"  供电电压异常  ",
			0x0008,//"    过流保护    ",
			0x0010,//"  未检测到锅具  ",
			0x0020,//"  线圈过热保护  ",
			0x0040,//" 控制板通讯异常 ",
			0x0080,//"    干锅保护    ",
			0x0100,//"  长时间无脉冲  ",
			0x0200,//"  浪涌保护      ",
			0x0400,//"  SPI Flash故障 ",
			0x0800,//" 人机板串口异常 ",
			//警告代码
			0x1000,//"  测温元件异常  ",
			0x2000,//"  存储卡异常    ",
			0x4000,//"  称重元件异常  ",
	};

	public static String[] errorCode_str = {
			"0x0001",//"  IGBT温度过高  ",
			"0x0002",//"  IGBT过压保护  ",
			"0x0004",//"  供电电压异常  ",
			"0x0008",//"    过流保护    ",
			"0x0010",//"  未检测到锅具  ",
			"0x0020",//"  线圈过热保护  ",
			"0x0040",//" 控制板通讯异常 ",
			"0x0080",//"    干锅保护    ",
			"0x0100",//"  长时间无脉冲  ",
			"0x0200",//"  浪涌保护      ",
			"0x0400",//"  SPI Flash故障 ",
			"0x0800",//" 人机板串口异常 ",
			//警告代码
			"0x1000",//"  测温元件异常  ",
			"0x2000",//"  存储卡异常    ",
			"0x4000",//"  称重元件异常  ",
	};

	public static String[] errorDesc = {
			"  IGBT温度过高  ",		//0x0001
			"  IGBT过压保护  ",		//0x0002
			"  供电电压异常  ",		//0x0004
			"    过流保护    ",		//0x0008
			"  未检测到锅具  ",		//0x0010
			"  线圈过热保护  ",		//0x0020
			" 控制板通讯异常 ",		//0x0040
			"    干锅保护    ",		//0x0080
			"  长时间无脉冲  ",		//0x0100
			"  浪涌保护      ",		//0x0200
			"  SPI Flash故障 ",		//0x0400
			" 人机板串口异常 ",		//0x0800
			//警告代码
			"  测温元件异常  ",		//0x1000
			"  存储卡异常    ",		//0x2000
			"  称重元件异常  ",		//0x4000
	};

	/*public static String[] errorDesc = { "IGBT过温", "IGBT过压", "系统电压异常", "电流异常", "无锅",
			"线圈过热", "人机串口接收异常", "干锅保护", "过流", "浪涌保护" , "SPI Flash故障", "人机串口发送异常", "IR ERR", "SD卡错误", "称重元件异常"};*/
}
