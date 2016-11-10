package com.gzmelife.app.device;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.net.wifi.WifiManager;

import com.gzmelife.app.bean.DeviceNameAndIPBean;
import com.gzmelife.app.tools.MyLog;
/**
 * UDP接收局域网电磁炉设备
 * @author chenxiaoyan
 *
 */
public class DeviceUtil {
	/* 发送广播端的socket */
	private DatagramSocket multicastSocket;

	private InetAddress receiveAddress;

	private List<String> deviceIPList;
	private List<DeviceNameAndIPBean> deviceList;

	private OnReceiver onReceiver;

	private Thread thread = null;
	
	private static WifiManager.MulticastLock lock;

	public DeviceUtil(Context context, OnReceiver mOnReceiver) {
		this.onReceiver = mOnReceiver;
		
		deviceIPList = new ArrayList<String>();
		deviceList = new ArrayList<DeviceNameAndIPBean>();
		if (multicastSocket == null) {
			/* 创建socket实例 */
			try {
				multicastSocket = new MulticastSocket(Config.SERVER_HOST_PORT);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		WifiManager manager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
		this.lock= manager.createMulticastLock("UDPwifi"); 
		
		if (receiveAddress == null) {
			try {
				receiveAddress = InetAddress
						.getByName(Config.LOCAL_IP);
//				if (!receiveAddress.isMulticastAddress()) {// 测试是否为多播地址
//					try {
//						throw new Exception("请使用多播地址");
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//				try {
//					multicastSocket.setTimeToLive(1);
//				} catch (IOException e1) {
//					e1.printStackTrace();
//				}
//				multicastSocket.joinGroup(receiveAddress);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}

		if (thread == null) {
			thread = new Thread(new Runnable() {
				@Override
				public void run() {
					byte buf[] = new byte[1024];
					DatagramPacket dp = null;
					dp = new DatagramPacket(buf, buf.length, receiveAddress,
							Config.SERVER_HOST_PORT);
					while (null != multicastSocket && !multicastSocket.isClosed()) {
						try {
							lock.acquire();
							
							multicastSocket.receive(dp);
							String name = new String(buf, 0, dp.getLength()).trim();
							if (name.contains(",")) {
								name = name.substring(0, name.indexOf(","));
							}
							String ip = dp.getAddress().getHostAddress();
							if (!deviceIPList.contains(ip) && name.contains("PMS")) {
								MyLog.i(MyLog.TAG_I_INFO, "接收到数据:" + name + "," + ip);

								DeviceNameAndIPBean bean = new DeviceNameAndIPBean();
								bean.setIp(ip);
								bean.setName(name);
								deviceIPList.add(ip);
								deviceList.add(bean); // 设备的名称，ip
								if (onReceiver != null) {
									onReceiver.refreshData(deviceList);
								}
							}
							
							lock.release();
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			});
		}
	}

	public interface OnReceiver {
		void refreshData(List<DeviceNameAndIPBean> list);
	}

	public void startSearch() {
		thread.start();
	}

	public void closeSearch() {

		if (multicastSocket != null) {
			multicastSocket.close();
			multicastSocket = null;
		}
	}
}
