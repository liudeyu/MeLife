//package com.gzmelife.app.device;
//
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.io.UnsupportedEncodingException;
//import java.net.Socket;
//import java.util.ArrayList;
//import java.util.List;
//
//import android.app.Activity;
//import android.content.Context;
//import android.os.CountDownTimer;
//import android.os.Handler;
//import android.os.Handler.Callback;
//import android.os.Looper;
//import android.os.Message;
//import android.text.TextUtils;
//import android.util.Log;
//
//import com.gzmelife.app.KappAppliction;
//import com.gzmelife.app.activity.CookBookDetailActivity;
//import com.gzmelife.app.fragment.DeviceFragment;
//import com.gzmelife.app.tools.DataUtil;
//import com.gzmelife.app.tools.FileUtils;
//import com.gzmelife.app.tools.KappUtils;
//import com.gzmelife.app.tools.MyLog;
//
///** Socket通信类 */
//public class SocketToolForState {
//	private Socket socket;
//	private OutputStream output;
//	private InputStream input;
//
//	private Context context;
//	private Activity activity;
//
//	private boolean isSendCMD = false; // 当前正在发送命令
//	/** 若指令发送不成功，3S后重发指令 */
//	private int timeCnt = 0;
//	private int timeCntHeart = 0;
//	private byte[] bufLastTemp;
//
//	/** 记录重发次数，若超过3次则进行重连的操作 */
//	private int MaxReCnt = 0;
//	/** 是否超时 */
//	private boolean RecTimeOut = false;
//
//	private int num = 0;
//	private byte[] bufTemp = new byte[256 * 256];
//
//	/** 指令发送是否成功，也作为是否处于空闲状态的判断，进行指令重发或心跳 */
//	private boolean ConFalg = false;
//
//	private HeartTimeCount heartTimer;
//
//	private OnReceiver receiver;
//
//	private int fileNum = 0;
//	private int frmIndex = 0;
//	private int maxIndex = 0;
//
//	/** 下发文件的总大小 */
//	private int numDownZie = 0;
//	/** 已经下发了的大小 */
//	private int numDownNow = 0;
//	// private int numUpZie = 0; // 上传到手机来的文件的大小
//	/** 手机已经接收了的大小 */
//	private int numUpNow = 0;
//
//	private byte[] bufRecFile;
//	 private byte[] bufSendFile = new byte[10 * 1024 * 1024];
//
//	private int MaxPacket = 2 * 1024; // 一次最大下发大小
//
//	private List<String> downFileList = new ArrayList<String>(); // 菜谱文件列表
//	private List<String> selfFileList = new ArrayList<String>(); // 录波文件列表
//
//	private byte[] bufACK = { 0x00, 0x00 };// ok
//	/** 当三次重发失败后，三次重建tcp连接并发送指令。若为true，表示连接成功，不用再继续重建与连接，若三次后还为false，则给出失败提示 */
//	private boolean isConnected = false;
//
//	/** 将当前获取第几帧的值转化为byte数组来传 */
//	private void ACK(int index) {
//		bufACK[0] = (byte) (index % 256);
//		bufACK[1] = (byte) (index / 256);
//	}
//
//	// public SocketTool() {
//	// }
//
//	public SocketToolForState(Context context, Activity activity, OnReceiver onReceiver) {
//		this.context = context;
//		this.activity = activity;
//		this.receiver = onReceiver;
//	}
//
//	public SocketToolForState(Context context, OnReceiver onReceiver) {
//		this.context = context;
//		this.receiver = onReceiver;
//	}
//
//	public boolean isStartHeartTimer() {
//		return startHeart;
//	}
//
//	private boolean startHeart = false;
//
//	/** 启动心跳计时 */
//	public void startHeartTimer() {
//		heartTimer = new HeartTimeCount(Long.MAX_VALUE, 1000);
//		heartTimer.start();
//		startHeart = true;
//		ConFalg = true;
//	}
//
//	/** 关闭连接 */
//	public void closeSocket() {
//		try {
//			if (output != null) {
//				output.close();
//				output = null;
//			}
//			if (socket != null) {
//				socket.close();
//				socket = null;
//			}
//			if (heartTimer != null) {
//				heartTimer.cancel();
//				heartTimer = null;
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//
//	/** 每次的首次连接pms时，调用此方法，给三次连接机会，调用连接方法 */
//	public void firstConnect() {
//		isConnected = false;
//		connectTimes = 3;
//		connectHandler.sendEmptyMessage(0);
//	}
//
//	/** 根绝ip与端口初始化socket连接 */
//	public void initClientSocket() {
//		try {
//			MyLog.d("init tcp socket");
//			// 连接服务器
//			socket = new Socket(Config.SERVER_HOST_IP, Config.SERVER_HOST_PORT);
//			// 获取输出流
//			// writer = new BufferedWriter(new
//			// OutputStreamWriter(socket.getOutputStream(), "utf-8"));
//			output = socket.getOutputStream();
//
//			input = socket.getInputStream();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	/** socket发送数据且接收数据 */
//	public byte[] sendMessage(final byte[] msg) {
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				if (socket == null || output == null || socket.isClosed()) {
//					if (socket == null) {
//						Log.e("MyError", "请先初始化socket和output,"
//								+ (socket == null) + "," + (output == null));
//						System.out.print("请先初始化socket和output,"
//								+ (socket == null) + "," + (output == null));
//					} else {
//						Log.e("MyError", "请先初始化socket和output,"
//								+ (socket == null) + "," + (output == null)
//								+ "," + (socket.isClosed()));
//						System.out.print("请先初始化socket和output,"
//								+ (socket == null) + "," + (output == null)
//								+ "," + (socket.isClosed()));
//					}
//					if (receiver != null) {
//						receiver.onFailure(0);
//						System.out.print("不处理");
//					}
//					// initClientSocket();
//					System.out.print("----不处理----");
//					return;
//					// return null;
//				}
//				try {
//					// if (msg[3] == (byte) 0xF3 && msg[4] == (byte) 0x01) {
//					// System.out.println("--->file发送数据, msg[4]=" + msg[4]);
//					System.out.println("SocketToolForState--->file发送msg[4]："
//							+ Integer.toHexString(msg[4]));
//					System.out.print("SocketToolForState发送.");
//					for (int i = 0; i < msg.length; i++) {
//						// if (DataUtil.hexToTen(msg[i]) != 0) {
//						System.out.print(Integer.toHexString(DataUtil
//								.hexToTen(msg[i])) + " ");
//						// }
//					}
//					System.out.println("SocketToolForState.发送结束");
//					// }
//				} catch (Exception e1) {
//					e1.printStackTrace();
//				}
//				try {
//						output.write(msg);
//
//						output.flush();
//
//						System.out.println("SocketToolForState.发送msg"+msg);
//
//				} catch (IOException e) {
//					e.printStackTrace();
//					return;
//				}
//
//				byte[] resultTemp = new byte[1024 * 5];
//				int len = 0;
//				try {
//					len = input.read(resultTemp);
//					System.out.println("--->len=" + len);
//					if (len == -1) {
//						Log.e("MyError", "SocketToolForState未得到返回结果");
//						System.out.println("SocketToolForState未得到返回结果" );
//						// if (receiver != null) {
//						// receiver.onSuccess(null);
//						// }
//					} else {
//						byte[] result = new byte[len];
//						for (int i = 0; i < len; i++) {
//							result[i] = resultTemp[i];
//							Log.e("MyError", "--------> last:"
//									+ DataUtil.hexToTen(result[i]));
//							System.out.print(Integer.toHexString(DataUtil
//									.hexToTen(result[i])) + " ");
//						}
//
//						// if (/*pp == 0 && */result[3] == (byte) 0xF3 &&
//						// result[4] == (byte) 0x01) {
//						// pp = 1;
//						Log.e("MyError", "len:" + result.length + ", last:"
//								+ DataUtil.hexToTen(result[result.length - 1]));
//						System.out.println("SocketToolForState--->file接收返回数据：len="
//								+ result.length);
//						System.out.println("SocketToolForState--->接收");
//						for (int i = 0; i < result.length; i++) {
////							System.out.print( "--->file接收：" + Integer
////									.toHexString(result[i]) + ",");
////							System.out.print(/* "--->file接收：" + */result[i] + ",");
//							Log.e("MyError", "SocketToolForState-------->"+Integer
//									.toHexString(result[i]) + ",");
//
////							System.out.print( "--->file接收：" + Integer.toHexString(DataUtil
////									.hexToTen(result[i])) + ",");
//
//						}
//						System.out.println("SocketToolForState--->接收结束");
//						// }
//						Message msg = new Message();
//						msg.obj = result;
//						handler.sendMessage(msg);
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				// return result;
//			}
//		}).start();
//		return msg;
//	}
//
//	public interface OnReceiver {
//		/** flag 0: 不处理，1：下载成功，2：下载失败,3:下载数据的百分比,4:连接成功,5:删除文件成功，6：获取设备状态成功,
//		  * 7 : 传文件到智能锅成功，8：传文件到智能锅的百分比
//		  */
//		void onSuccess(List<String> cookBookFileList, int flag, int now, int all);
//
//		/**
//		 * flag 默认为0;-1：下载文件，文件大小=0;
//		 */
//		void onFailure(int flag);
//	}
//
//	/** 单指令，只要发送指令，不需要传参数，如连接、心跳等 */
//	public void PMS_Send(byte[] bufTemp1) {
//		int addNum = 0;
//		byte[] bufTemp = new byte[bufTemp1.length + 5];
//
//		bufTemp[0] = (byte) 0xA5;
//
//		bufTemp[1] = (byte) (bufTemp1.length % 256 + 1);
//		bufTemp[2] = (byte) (bufTemp1.length / 256);
//
//		for (int i = 0; i < bufTemp1.length; i++) {
//			bufTemp[i + 3] = bufTemp1[i];
//		}
//
//		bufTemp[bufTemp1.length + 3] = Config.clientPort;
//
//		for (int i = 1; i < bufTemp1.length + 4; i++) {
//			addNum += bufTemp[i];
//		}
//		bufTemp[bufTemp1.length + 4] = (byte) (addNum % 256);
//
//		try {
//			isSendCMD = true;
//			System.out.println("--->发送连接、心跳");
//
//			timeCnt = 0;
//			timeCntHeart = 0;
//
//			bufLastTemp = new byte[bufTemp.length];
//			for (int i = 0; i < bufTemp.length; i++) {
//				bufLastTemp[i] = bufTemp[i];
//				System.out.print("--->发送数据"+Integer.toHexString(DataUtil
//						.hexToTen(bufTemp[i])) + " ");
//			}
//
//			sendMessage(bufTemp);
//			System.out.print("--->发送数据bufTemp"+bufTemp + " ");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	/** 发送指令与参数 */
//	public void PMS_Send(byte[] bufTemp1, byte[] bufTemp2) {
//		int addNum = 0;
//		int i = 0;
//		int len = bufTemp1.length + bufTemp2.length + 1;
//		byte[] bufTemp = new byte[len + 4];
//		bufTemp[0] = (byte) 0xA5;
//		bufTemp[1] = (byte) (len % 256);
//		bufTemp[2] = (byte) ((len / 256) % 256);
//		bufTemp[3] = bufTemp1[0];
//		bufTemp[4] = bufTemp1[1];
//
//		if (Config.clientPort == -1) {
//			MyLog.e("未得到该设备的ip末位地址");
//			return;
//		}
//		bufTemp[5] = Config.clientPort;
//
//		for (i = 6; i < len + 3; i++) {
//			bufTemp[i] = bufTemp2[i - 6];
//		}
//
//		for (i = 1; i < len + 3; i++) {
//			addNum += bufTemp[i];
//		}
//		bufTemp[len + 3] = (byte) (addNum % 256);
//
//		try {
//			isSendCMD = true;
//			sendMessage(bufTemp);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		timeCnt = 0;
//		timeCntHeart = 0;
//
//		// 记录所发的指令，用于后续若指令失败时重发
//		bufLastTemp = new byte[bufTemp.length];
//		for (i = 0; i < bufTemp.length; i++) {
//			bufLastTemp[i] = bufTemp[i];
//		}
//	}
//
//	/** 拿到数据后先判断校验码等信息，若检验正确再去处理否则给出提示 */
//	Handler handler = new Handler(new Callback() {
//		@Override
//		public boolean handleMessage(Message msg) {
//			byte[] result = (byte[]) msg.obj;
//			try {
//				boolean UartS = false;
//				int len = 0;
//				boolean run = true;
//				while (run) {
//					if (UartS) {
//						if (num < 3) {
//							bufTemp[num] = result[num];
//							num++;
//						} else if (num == 3) {
//							len = DataUtil.hexToTen(bufTemp[1])
//									+ DataUtil.hexToTen(bufTemp[2]) * 256;
//							bufTemp[num] = result[num];
//							num++;
//						} else if (num > 3) {
//							if (num < len + 3 && num < result.length) {
//								bufTemp[num] = result[num];
//								num++;
////								System.out.print( "------" + Integer.toHexString(DataUtil
////										.hexToTen(bufTemp[num])) + ",");
////								System.out.print( "!!!!!：" + Integer.toHexString(DataUtil
////										.hexToTen(result[num])) + ",");
//							} else {
//								if (num < result.length) {
//									bufTemp[num] = result[num];
//									num++;
//									System.out.print( "aaaa：" + Integer.toHexString(DataUtil
//											.hexToTen(bufTemp[num])) + ",");
//								}
//
//								int check = 0;
//								for (int i = 1; i < num - 1; i++) {
//									int temp = bufTemp[i];
//									if (temp < 0) {
//										temp += 256;
//									}
//									check += temp;
//								}
//								if (DataUtil.hexToTen(bufTemp[num - 1]) == (check % 256)) {
//									System.out.println("正确的len=" + len);
//									System.out.println("****-11111****" + DataUtil.hexToTen(bufTemp[6]));
//									Done(bufTemp, num);
//									run = false;
//									return true;
//								} else {
//									System.out.println("数据包和校验失败" + ", bufTemp[num - 1]=" + DataUtil.hexToTen(bufTemp[num - 1])
//											+ ", check % 256=" + (check % 256) + ", len=" + len);
//									if (len > 0) {
//										run = false;
//									}
//								}
//
//								if (len > 0) {
//									UartS = false;
//									num = 0;
//									System.out.print( "bbbb："   );
//								}
//							}
//						}
//					} else {
//						// 未接收数据包开头时，数据接收超时为1天
//						// fs.ReadTimeout = 60 * 60 * 1000;
//						byte A0 = (byte) 0xA5;
//						byte aa = result[0];
//						// Log.d("MyDebug", "[0]=" + aa + "," + A0 + ","
//						// + (aa == A0));
//						if (aa == A0) {
//							num = 0;
//							bufTemp[num] = aa;
//							UartS = true;
//							num++;
//
//							timeCnt = 0;
//							timeCntHeart = 0;
//							RecTimeOut = false;
//							System.out.print( "cccc：" + Integer.toHexString(DataUtil
//									.hexToTen(bufTemp[num])) + ",");
//						} else {
//							// Log.e("MyError", "第一个字节不是OxA5");
//							// MessageBox.Show("Error");
//							System.out.print( "第一个字节不是OxA5");
//						}
//					}
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//				Log.e("MyError", "PMS设备断开连接！");
//				if (receiver != null) {
//					receiver.onFailure(0);
//				}
//				ConFalg = false;
//				return false;
//			}
//			return false;
//		}
//	});
//
//	/** 得到pms返回的数据，根据实际情况做不同的处理 */
//	private void Done(byte[] buf, int num) {
//		ConFalg = true;
//		isSendCMD = false;
//
//		System.out.println("正确收到返回结果, buf[3]=" + buf[3]);
//		switch (buf[3]) {
//
//		case (byte) 0xF3: // 文件列表
//			if (buf[4] == 0x00) { // 得到文件数目
//
//				Log.e("log2", buf[6]+"");
//				Log.e("log3", buf[7]+"");
//
//				fileNum =DataUtil.hexToTen(bufTemp[6]) + DataUtil.hexToTen(bufTemp[7]) * 256;
//				if (DeviceFragment.fileFlag) {
//					MyLog.i("PMS录波文件列表总数：" + fileNum);
//					System.out.println("PMS录波文件列表总数：" + fileNum);
//					selfFileList.clear();
//				} else {
//					MyLog.i("PMS菜谱文件列表总数：" + fileNum);
//					System.out.println("PMS菜谱文件列表总数：" + fileNum);
//					downFileList.clear();
//				}
//
//				if (fileNum >= 0) {
//					frmIndex = 1;
//					maxIndex = fileNum / 25;
//					if ((fileNum % 25) > 0) {
//						maxIndex++;
//					}
//					System.out.println("请求获取文件列表第" + frmIndex + "帧");
//					MyLog.d("请求获取文件列表第" + frmIndex + "帧");
//					ACK(frmIndex);
//					PMS_Send(Config.bufListFile, bufACK);
//				} else {
//					if (receiver != null) {
//						receiver.onSuccess(null, 0, 0, 0);
//					}
//				}
//			} else if (buf[4] == 0x01) { // 得到文件名称
//				String filename = "";
//
//				MyLog.d("收到获取文件列表第" + frmIndex + "帧");
//				System.out.println("收到获取文件列表第" + frmIndex + "帧");
//				for (int index = 8; index < num - 40; index += 40) { // 9->8
//					String aa = "";
//					try {
//						aa = new String(buf, index, 40, "gbk");
//					} catch (UnsupportedEncodingException e1) {
//						e1.printStackTrace();
//					}
//					// Encoding.Default.GetString(buf, index, 40);
//					try {
//						filename = aa.replace("\0", "");
//					} catch (Exception e) {
//						filename = aa;
//					}
//
//					if (DeviceFragment.fileFlag) {
//						selfFileList.add(filename);
//						MyLog.d("录波：" + frmIndex + ":" + filename);
//						System.out.println("录波：" + frmIndex + ":" + filename);
//					} else {
//						downFileList.add(filename);
//						MyLog.d("菜谱：" + frmIndex + ":" + filename);
//						System.out.println("菜谱：" + frmIndex + ":" + filename);
//					}
//				}
//
//				if (DeviceFragment.fileFlag) {
//					if (selfFileList.size() < fileNum) { // 加帧判断
//						frmIndex++;
//
//						MyLog.d("录波:" + "请求文件列表第" + frmIndex + "帧");
//						System.out.println("录波:" + "请求文件列表第" + frmIndex + "帧");
//						ACK(frmIndex);
//						PMS_Send(Config.bufListFile, bufACK);
//					} else {
//						MyLog.d("录波:请求文件列表获取结束帧");
//						System.out.println("录波:请求文件列表获取结束帧");
//						PMS_Send(Config.bufListFileOver);
//					}
//				} else {
//					if (downFileList.size() < fileNum) { // 加帧判断
//						frmIndex++;
//						System.out.println("菜谱:" + "请求文件列表第" + frmIndex + "帧");
//						MyLog.d("菜谱:" + "请求文件列表第" + frmIndex + "帧");
//						ACK(frmIndex);
//						PMS_Send(Config.bufListFile, bufACK);
//					} else {
//						System.out.println("菜谱:请求文件列表获取结束帧");
//						MyLog.d("菜谱:请求文件列表获取结束帧");
//						PMS_Send(Config.bufListFileOver);
//					}
//				}
//			} else if (buf[4] == 0x02) { // 遍历完成
//				if (DeviceFragment.fileFlag) {
//					MyLog.d("录波：收到遍历文件结束帧");
//					System.out.println("录波：收到遍历文件结束帧");
//					if (receiver != null) {
//						receiver.onSuccess(selfFileList, 0, 0, 0);
//					}
//				} else {
//					MyLog.d("菜谱：收到遍历文件结束帧");
//					System.out.println("菜谱：收到遍历文件结束帧");
//					if (receiver != null) {
//						receiver.onSuccess(downFileList, 0, 0, 0);
//					}
//				}
//			}
//			break;
//		case (byte) 0xF4: // 上召文件
//			if (buf[4] == 0x01) { // 得到文件数据
//				upFile(buf/*, num*/);
//			} else if (buf[4] == 0x00) { // 得到文件长度
//				int fileLen = DataUtil.hexToTen(buf[6])
//						+ DataUtil.hexToTen(buf[7]) * 256
//						+ DataUtil.hexToTen(buf[8]) * 256 * 256
//						+ DataUtil.hexToTen(buf[9]) * 256 * 256 * 256;
//				MyLog.d("文 件长度为：" + fileLen);
//				if (fileLen == 0) {
//					if (receiver != null) {
//						receiver.onFailure(-1);
//					}
//					return;
//				}
//				numUpNow = 0;
//
//				// numUpZie = fileLen;
//				bufRecFile = new byte[fileLen];
//
//				frmIndex = 1;
//				maxIndex = fileLen / MaxPacket;
//				if ((fileLen % MaxPacket) > 0) {
//					maxIndex++;
//				}
//
//				MyLog.d("请求第" + frmIndex + "帧");
//				ACK(frmIndex);
//				PMS_Send(Config.bufFileAck, bufACK);
//			} else if (buf[4] == 0x02) {
//				MyLog.d("收到文件结束帧");
//
//				DeviceFragment.saveFileName = FileUtils.PMS_FILE_PATH
//						+ FileUtils.getFileName(DeviceFragment.saveFileName);
//				FileUtils.writeTextToFile(DeviceFragment.saveFileName, bufRecFile);
////				updateLocalFile();
//
//				if (receiver != null) {
//					receiver.onSuccess(null, 1, 0, 0);
//				}
//			}
//			break;
//		 case (byte) 0xF5: // 下传文件
//			 if (buf[4] == 0x00) { // 发送文件大小和文件名，得到确认
//				 if (buf[6] == 0x01) {
//					 numDownNow = 0;
//					 MyLog.d("收到文件下载--确认下载");
//
//					 frmIndex = 1;
//					 DownFile(frmIndex);
//				 } else if (buf[6] == 0x00) {
//					 if (receiver != null) {
//						 receiver.onFailure(0);
//					 }
//				 }
//			 } else if (buf[4] == 0x01) { // 发送文件一帧，得到确认
//				 if (buf[6] == 0x01) {
//					 MyLog.d("收到文件下载--下载第" + frmIndex + "帧");
//
//					 frmIndex++;
//
//					 if (receiver != null) {
//						 receiver.onSuccess(null, 8, numDownNow, numDownZie);
//					 }
//					 if (numDownZie > numDownNow) {
//						 DownFile(frmIndex);
//					 } else {
//						 MyLog.d("请求文件下载--下载结束帧");
//						 PMS_Send(Config.bufDownFileStop);
//					 }
//				 } else if (buf[6] == 0x00) {
//					 MyLog.d("收到文件下载--重发第" + frmIndex + "帧");
//					 DownFile(frmIndex);
//				 }
//			 } else if (buf[4] == 0x02) {
//				 MyLog.d("收到文件下载--下载结束帧");
//				 if (receiver != null) {
//					 receiver.onSuccess(null, 7, 0, 0);
//				 }
//			 }
//		 	break;
//		case (byte) 0xF6:
//			if (buf[4] == (byte) 0x00) {
//				if (buf[6] == 0x01) {
//					if (receiver != null) {
//						receiver.onSuccess(null, 5, 0, 0);
//					}
//				} else {
//					if (receiver != null) {
//						receiver.onFailure(0);
//					}
//				}
//			} else if (buf[4] == (byte) 0x01) {
//				if (buf[6] == 0x01) {
//					if (receiver != null) {
//						receiver.onSuccess(null, 5, 0, 0);
//					}
//				} else {
//					if (receiver != null) {
//						receiver.onFailure(0);
//					}
//				}
//			}
//			break;
//		case (byte) 0xF7:
//			if (buf[4] == (byte) 0x00) {
//				Config.SYSTEM_A = ((DataUtil.hexToTen(buf[6]) + 256
//						* DataUtil.hexToTen(buf[7])) * 1650.0 / 48803.38944) + "A";
//				Config.SYSTEM_V = ((DataUtil.hexToTen(buf[8]) + 256
//						* DataUtil.hexToTen(buf[9])) / 10.0) + "V";
//				Config.SYSTEM_W = DataUtil.hexToTen(buf[15]) * 10 + "W";
//				Config.PMS_TEMP = ((DataUtil.hexToTen(buf[10]) + 256
//						* DataUtil.hexToTen(buf[11])) / 100.0) + "度";
//				Config.ROOM_TEMP = ((DataUtil.hexToTen(buf[12]) + 256
//						* DataUtil.hexToTen(buf[13])) / 100.0) + "度";
//				switch (DataUtil.hexToTen(buf[14])) {
//					case 0:
//						Config.PMS_STATUS = "POWEROFF";
//						break;
//					case 1:
//						Config.PMS_STATUS = "POWERON";
//						break;
//					case 2:
//						Config.PMS_STATUS = "POWERSTANDBY";
//						break;
//					case 3:
//						Config.PMS_STATUS = "POWERHALT";
//						break;
//				}
//
//				Config.PMS_ERRORS.clear();
//				for (int i = 16; i < 20; i++) {
//					MyLog.d(i + ":"
//							+ Integer.toHexString(DataUtil.hexToTen(buf[i])));
//					int b = buf[i];
//					if (b != 0) {
//						for (int j = 0; j < Config.errorCode.length; j++) {
//							if ((b / Config.errorCode[j]) % 2 == 1) {
//								MyLog.d("error:"
//										+ Integer.toHexString(DataUtil
//												.hexToTen(buf[i])) + "."
//										+ Config.errorDesc[j]);
//								Config.PMS_ERRORS.add(j + "");
//							}
//						}
//					}
//				}
//				MyLog.d("获取状态成功." + Config.SYSTEM_A + "." + Config.SYSTEM_V
//						+ "." + Config.SYSTEM_W + "." + Config.PMS_TEMP + "."
//						+ Config.ROOM_TEMP + "." + Config.PMS_STATUS);
//				if (receiver != null) {
//					receiver.onSuccess(null, 6, 0, 0);
//				}
//			} else {
//				if (receiver != null) {
//					receiver.onFailure(0);
//				}
//			}
//			break;
//		case (byte) 0xF8:
//			/**
//			 * 链接成功
//			 */
//			if (buf[4] == (byte) 0x00) { // 连接确认报文，回复PMS的MAC
//				ConFalg = true;
//				isConnected = true;
//
//				// MAC已无效
//				// String mac = "";
//				// for (int i = 6; i < 6 + 12; i++) {
//				// if (i == 17 || i % 2 == 0) {
//				// mac += buf[i];
//				// } else {
//				// mac += buf[i] + "-";
//				// }
//				// }
//				if (receiver != null) {
//					receiver.onSuccess(null, 4, 0, 0);
//				}
//
////				 RefreshFileList(); //获取文件
//			} else if (buf[4] == (byte) 0x02) { // 心跳报文
//				// numXT++;
//				// tv_desc.setText(tv_desc.getText().toString() + "\n"
//				// + "收到心跳回复包, numXT:" + numXT);
////				PMS_Send(Config.bufStatus);
//			}
//			break;
//		}
//		// case (byte) 0xF1: { // 控制
//				// if (buf[4] == (byte) 0x00) { // 开关机
//				// tv_desc.setText(tv_desc.getText().toString() + "\n"
//				// + "执行开关机操作成功");
//				// Toast.makeText(MainActivity.this, "执行开关机操作成功",
//				// Toast.LENGTH_LONG).show();
//				// } else if (buf[4] == (byte) 0x01) { // 功率减
//				// tv_desc.setText(tv_desc.getText().toString() + "\n"
//				// + "执行功率减操作成功");
//				// Toast.makeText(MainActivity.this, "功率减", Toast.LENGTH_LONG)
//				// .show();
//				// } else if (buf[4] == (byte) 0x02) { // 功率加
//				// tv_desc.setText(tv_desc.getText().toString() + "\n"
//				// + "执行功率加操作成功");
//				// Toast.makeText(MainActivity.this, "功率加", Toast.LENGTH_LONG)
//				// .show();
//				// } else if (buf[4] == (byte) 0x03) { // 录波
//				// tv_desc.setText(tv_desc.getText().toString() + "\n"
//				// + "执行录波操作成功");
//				// Toast.makeText(MainActivity.this, "录波", Toast.LENGTH_LONG)
//				// .show();
//				// } else if (buf[4] == (byte) 0x04) { // 录波反演
//				// tv_desc.setText(tv_desc.getText().toString() + "\n"
//				// + "执行录波反演操作成功");
//				// Toast.makeText(MainActivity.this, "录波反演", Toast.LENGTH_LONG)
//				// .show();
//				// }
//				// }
//				// break;
//				// case (byte) 0xF2: { // 对时
//				// if (buf[4] == (byte) 0x00) {
//				// String min = "0";
//				// if (et_min.getText().toString().length() > 0) {
//				// min = et_min.getText().toString();
//				// }
//				// Toast.makeText(
//				// MainActivity.this,
//				// "对时完成:" + DateUtil.getCurrentTimeString(Integer.parseInt(min)),
//				// Toast.LENGTH_LONG)
//				// .show();
//				// tv_desc.setText(tv_desc.getText() + "\n" + "对时完成，所设置的当前时间："
//				// + DateUtil.getCurrentTimeString(Integer.parseInt(min)));
//				// }
//				// }
//				// break;
//	}
//
//	/** 从pms下载文件到手机时，根据当前第几次下载，计算出指令来发送  */
//	private void DownFile(int index) {
//		// 所需下载长度为剩下的长度  但不大于最大请求长度
//        int lenth = numDownZie - numDownNow;
//        if (lenth > MaxPacket) {
//            lenth = MaxPacket;
//        }
//
//        byte[] bufR = new byte[lenth + 2];
//        bufR[0] = (byte)(index % 256);
//        bufR[1] = (byte)(index / 256);
//
//
//        for (int i = 0; i < lenth; i++) {
//            bufR[i + 2] = bufSendFile[(index - 1) * MaxPacket + i];
//        }
//
//        MyLog.d("请求文件下载--下载第" + frmIndex + "帧");
//        PMS_Send(Config.bufDownFileData, bufR);
//        numDownNow = numDownNow + lenth;
//    }
//
//	/** 重发机制和心跳 */
//	class HeartTimeCount extends CountDownTimer {
//		public HeartTimeCount(long millisInFuture, long countDownInterval) {
//			super(millisInFuture, countDownInterval);
//		}
//
//		@Override
//		public void onFinish() {
//		}
//
//		@Override
//		public void onTick(long millisUntilFinished) {
//			 MyLog.d(MyLog.TAG_D, Config.SERVER_HOST_NAME + "," + (socket != null) + "," + (socket.isConnected()));
//			if (!TextUtils.isEmpty(Config.SERVER_HOST_NAME) && socket != null && socket.isConnected()) {
//				MyLog.d("ConFalg=" + ConFalg + ", timeCnt=" + timeCnt);
//				timeCnt++;
//				if (timeCnt > 3) {
//					RecTimeOut = true;
//					timeCnt = 0;
//				}
//
//				if (ConFalg) {
//					// 如果是发送超时等，三次重发机会，否则尝试重连
//					if (RecTimeOut && isSendCMD) {
//						MaxReCnt++;
//						// KappUtils.showToast(context, "重发第" + MaxReCnt + "次");
//						MyLog.i(MyLog.TAG_I_INFO, "重发上一帧");
//						// socketTool.sendMessage(bufLastTemp, bufLastNum);
//						sendMessage(bufLastTemp);
//						isSendCMD = true;
//						timeCnt = 0;
//						RecTimeOut = false;
//
//						if (MaxReCnt > 2) {
//							MaxReCnt = 0;
//							ConFalg = false;
//							MyLog.i(MyLog.TAG_I_INFO, "达到最大重发次数,重新建立连接");
//							KappUtils.showToast(context, "操作失败，进行重连");
//							connectTimes = 3;
//							isConnected = false;
//							connectHandler.sendEmptyMessage(0);
//						}
//					}
//
//					/** 心跳计时，30S无操作发送心跳指令 */
//					timeCntHeart++;
//					if (timeCntHeart % 10 == 0) {
//						MyLog.d("心跳计时:" + timeCntHeart);
//					}
//					if (timeCntHeart > 120 && ConFalg) {
//						MyLog.i(MyLog.TAG_I_INFO, "请求发送心跳包");
//						PMS_Send(Config.bufHearbeat);
//						timeCntHeart = 0;
//					}
//				}
//			}
//		}
//	}
//
//	/** 三次重建tcp连接与指令的机会 */
//	int connectTimes = 3;
//
//	/** 关于连接模块做的处理 */
//	Handler connectHandler = new Handler(new Callback() {
//		@Override
//		public boolean handleMessage(Message msg) {
//			switch (msg.what) {
//				case 0:
//					if (!isConnected && connectTimes > 0) {
//						MyLog.d("重连剩余次数：" + connectTimes);
//						connectTimes--;
//						connectHandler.sendEmptyMessage(1);
//						new Thread(new Runnable() {
//							@Override
//							public void run() {
//								Looper.prepare();
//								closeSocket();
//								initClientSocket();
//								PMS_Send(Config.bufConnect);
//
//								new Handler().postDelayed(new Runnable() {
//									@Override
//									public void run() {
//										connectHandler.sendEmptyMessage(0);
//									}
//								}, 2000);
//								Looper.loop();
//							}
//						}).start();
//					} else {
//						if (!isConnected) {
//							if (receiver != null) {
//								receiver.onFailure(0);
//							}
//							KappAppliction.state=2;
//							KappUtils.showToast(context, "与PMS连接已经断开");
//						}
//					}
//					break;
//				case 1:
//					// KappUtils.showToast(context, "重连剩余次数：" + connectTimes);
//					break;
//			}
//			return false;
//		}
//	});
//
//	/** 从PMS下载数据到手机本地
//	 * buf 当前接收到的传来的数据，可以得到此次数据的长度以及数据
//	 */
//	private void upFile(byte[] buf/*, int num*/) {
//		int fileLen = DataUtil.hexToTen(buf[1]) + DataUtil.hexToTen(buf[2]) * 256 - 5;
//
//		MyLog.d("收到第"
//				+ (DataUtil.hexToTen(buf[6]) + DataUtil.hexToTen(buf[7]) * 256)
//				+ "帧,长度=" + fileLen + ",当前接收总长度：" + (numUpNow + fileLen));
//		for (int i = 0; i < fileLen; i++) {
//			bufRecFile[numUpNow + i] = buf[i + 8];
//		}
//		// 40380 40124
//		numUpNow += fileLen;
//
//		if (receiver != null) { // 界面上实时显示当前下载进度
//			receiver.onSuccess(null, 3, numUpNow, bufRecFile.length);
//		}
//
//		// 接收完毕发送结束指令，否则继续请求数据
//		if (numUpNow == bufRecFile.length) {
//			MyLog.d("请求发送结束帧");
//			PMS_Send(Config.bufFileStop);
//		} else {
//			frmIndex++;
//
//			MyLog.d("请求第" + frmIndex + "帧");
//			ACK(frmIndex);
//			PMS_Send(Config.bufFileAck, bufACK);
//		}
//	}
//
////	private void updateLocalFile() {
////		File file = new File(Environment.getExternalStorageDirectory()
////				.getAbsolutePath() + File.separator + "pms");
////		if (!file.exists()) {
////			file.mkdir();
////		}
////		MyLog.d("本地文件目录：" + file.getAbsolutePath());
////		List<String> phoneFileList = new ArrayList<String>();
////		if (file.isDirectory()) {
////			File[] files = file.listFiles();
////			if (files != null && files.length > 0) {
////				for (int i = 0; i < files.length; i++) {
////					phoneFileList.add(files[i].getName());
////				}
////			}
////		}
////		MyLog.d("手机本地PMS文件数目：" + phoneFileList.size());
////	}
//
//	/** 传送手机里面的菜谱文件到pms里面,根据文件路径得出所需要的指令并传给pms */
//	public void doSendFile() {
//		File file = new File(CookBookDetailActivity.filePath);
//
//		// 文件名长度的限制
//		String fileName = CookBookDetailActivity.filePath.substring(CookBookDetailActivity.filePath.lastIndexOf('/'));
//		MyLog.d("文件名: " + fileName);
//		if (fileName.length() > 39) {
//			Looper.prepare();
//			KappUtils.showToast(context, "文件名称超长，请重新选择");
//			Looper.loop();
//			if (receiver != null) {
//				receiver.onFailure(0);
//			}
//			return;
//		}
//
//		// 要发送的文件数据
//		byte[] bufFile = FileUtils.getBytesFromFile(file);
//		MyLog.d("文件大小: " + bufFile.length);
//		int check = 0;
//		bufSendFile = new byte[bufFile.length];
//		for (int i = 0; i < bufFile.length; i++) {
//			bufSendFile[i] = bufFile[i];
//			check += bufFile[i];
//		}
//
//		// 文件名的字节长度
//		byte[] arr = null;
//		try {
//			arr = fileName.getBytes("gb2312");
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//
//		// 所传参数，文件名长度+文件长度（均为字节长度）
//		byte[] bufFileInfo = new byte[arr.length + 8];
//
//		bufFileInfo[0] = (byte) (bufFile.length % 256);
//		bufFileInfo[1] = (byte) ((bufFile.length / 256) % 256);
//		bufFileInfo[2] = (byte) ((bufFile.length / 256 / 256) % 256);
//		bufFileInfo[3] = (byte) ((bufFile.length / 256 / 256 / 256) % 256);
//
//		bufFileInfo[4] = (byte) (check % 256);
//		bufFileInfo[5] = (byte) ((check / 256) % 256);
//		bufFileInfo[6] = (byte) ((check / 256 / 256) % 256);
//		bufFileInfo[7] = (byte) ((check / 256 / 256 / 256) % 256);
//
//		numDownNow = 0;
//		numDownZie = (int) bufFile.length;
//		MyLog.d("下发文件大小：" + numDownZie);
//
//		for (int i = 0; i < arr.length; i++) {
//			bufFileInfo[i + 8] = arr[i];
//		}
//		PMS_Send(Config.bufDownFileInfo, bufFileInfo);
//	}
//}
