package com.gzmelife.app.activity;

import java.util.ArrayList;
import java.util.List;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.espressif.iot.esptouch.demo_activity.EspWifiAdminSimple;
import com.gzmelife.app.KappAppliction;
import com.gzmelife.app.R;
import com.gzmelife.app.adapter.DeviceCenterAdapter;
import com.gzmelife.app.bean.DeviceNameAndIPBean;
import com.gzmelife.app.dao.DevicesDAO;
import com.gzmelife.app.device.Config;
import com.gzmelife.app.device.SocketTool;
import com.gzmelife.app.fragment.DeviceFragment;
import com.gzmelife.app.tools.DateUtil;
import com.gzmelife.app.tools.KappUtils;
import com.gzmelife.app.tools.MyLog;
import com.gzmelife.app.tools.SharedPreferenceUtil;
import com.gzmelife.app.tools.WifiUtil;
import com.gzmelife.app.views.TipConfirmView;

@ContentView(R.layout.activity_device_center)
public class DeviceCenterActivity extends BaseActivity implements OnClickListener {
	@ViewInject(R.id.tv_title)
	TextView tv_title;
	
	@ViewInject(R.id.iv_titleRight)
	ImageView iv_titleRight;
	
	@ViewInject(R.id.lv_pms)
	ListView lv_pms; // 离线设备
	
	private DeviceCenterAdapter deviceAdapter;
	private List<DeviceNameAndIPBean> deviceList = new ArrayList<DeviceNameAndIPBean>();
	private DeviceNameAndIPBean connectDeviceBean;
	
	private TimeCountOut outTime;  // 与wifi建立连接
	private TimeCountOut outtime;  // 与PMS建立连接
	
	private SocketTool socketTool;
	
	private Context context;
	
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		
		context = this;
		initView();
		showDeviceList();
	}

	private void initView() {
		tv_title.setText("我的设备中心");		
		iv_titleRight.setVisibility(View.VISIBLE);
		iv_titleRight.setImageResource(R.drawable.icon01);
		
		deviceAdapter = new DeviceCenterAdapter(context, deviceList);
		lv_pms.setAdapter(deviceAdapter);
		lv_pms.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				connectDeviceBean = deviceList.get(position);
				if (connectDeviceBean.getName().equals(Config.SERVER_HOST_NAME)
						&& connectDeviceBean.getWifiName().equals(new EspWifiAdminSimple(context).getWifiConnectedSsid())
						&& connectDeviceBean.getIp().equals(Config.SERVER_HOST_IP)) {
					KappUtils.showToast(context, "当前已连接该设备");
					DeviceCenterActivity.this.finish();//20161028
					return;
				}
				Config.SERVER_HOST_NAME = "";
				h.sendEmptyMessage(2);
			}
		});
		lv_pms.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				deletePMSDevice(position);
				return true;
			}
		});
		
		iv_titleRight.setOnClickListener(this);
	}
	
	private void showDeviceList() {
		List<DeviceNameAndIPBean> deviceListTemp = new DevicesDAO().getAllDevices();
		if (deviceListTemp != null && deviceListTemp.size() > 0) { // 本地没有设备数据
			deviceList.clear();
			deviceList.addAll(deviceListTemp);
			deviceAdapter.notifyDataSetChanged();
		}
	}
	
	Handler h = new Handler(new Callback() {
		@Override
		public boolean handleMessage(final Message msg) {
			switch (msg.what) {
				case 0:
					OnEvent onEvent = new OnEvent() {
						@Override
						public void onTick(long millisUntilFinished) {
							MyLog.d("onTick ." + millisUntilFinished);
							if (WifiUtil.getWifiInfo() != null
									&& connectDeviceBean != null
									&& !TextUtils.isEmpty(new EspWifiAdminSimple(context).getWifiConnectedSsid())
									&& WifiUtil.getWifiInfo().getSSID().equals("\"" + connectDeviceBean.getWifiName() + "\"")) {
								KappUtils.showToast(context, "连接wifi成功");
								MyLog.d("onTick 连接wifi成功." + millisUntilFinished);
								
								h.sendEmptyMessage(1);
							}
						}
						
						@Override
						public void onFinish() {
							if (WifiUtil.getWifiInfo() != null && connectDeviceBean != null
									&& !TextUtils.isEmpty(new EspWifiAdminSimple(context).getWifiConnectedSsid())
									&& WifiUtil.getWifiInfo().getSSID().equals("\"" + connectDeviceBean.getWifiName() + "\"")) {
								KappUtils.showToast(context, "连接wifi成功");
								
								h.sendEmptyMessage(1);
							} else {
								if (connectDeviceBean != null) {
									KappUtils.showToast(context, "连接wifi失败");
									connectDeviceBean = null;
								}
							}
						}
					};
					outTime = new TimeCountOut(1000 * 15, 1000, onEvent);
					outTime.start();
					break;
				case 1:
					KappUtils.getLocalIP(context);
					outTime.cancel();
					outTime = null;
					Config.SERVER_HOST_IP = connectDeviceBean.getIp();
					// 倒计时10秒，10秒内没有指令与PMS连接成功，则给出提示，且去掉转圈
					outtime = new TimeCountOut(10 * 1000, 1000, new OnEvent() {
						@Override
						public void onFinish() {
							handler.sendEmptyMessage(0);
						}

						@Override
						public void onTick(long millisUntilFinished) {
						}
					});
					
					initSocketTool();
					socketTool.firstConnect(); // 根据不同的ip，建立不同的socket
					outtime.start();
					break;
				case 2:
					showDlg();
					connectPMS();
					break;
			}
			return false;
		}
	});
	
	Handler handler = new Handler(new Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			closeDlg();
			switch (msg.what) {
				case -1:
					break;
				case 0:
					if (connectDeviceBean != null) {
						if (outtime != null) {
							outtime.cancel();
							outtime = null;
						}
						MyLog.d("与PMS的指令连接失败,清除connectDeviceBean");
						KappUtils.showToast(context, "与PMS的指令连接失败");
						connectDeviceBean = null;
					}
					break;
				case 1:
					break;
				case 2: // PMS连接成功
					KappUtils.showToast(context, "与PMS连接成功");
					socketTool.splitInstruction(Config.bufSetTime, new DateUtil().getCurrentTime());
					System.out.print("----对时功能4----"+new DateUtil().getCurrentTime());
					KappAppliction.state=1;
					if (connectDeviceBean != null) {
						Config.SERVER_HOST_NAME = connectDeviceBean.getName();
						connectDeviceBean = null;
					}
					
					DeviceFragment.isClearList = true;
					DeviceCenterActivity.this.finish();
					break;
				case 3:  // 删除文件失败
					break;
				case 5:  // 删除文件成功
					break;
			}
			return false;
		}
	});
	
	private void initSocketTool() {
		if (socketTool != null) {
			socketTool.closeSocket();
			socketTool = null;
		}
		if (socketTool == null) {
			socketTool = new SocketTool(context, new SocketTool.OnReceiver() {
				@Override
				public void onSuccess(List<String> cookBookFileList, int flag, int now, int all) {
					switch (flag) {
						case 0:
							break;
						case 1:
							break;
						case 2:
							break;
						case 3:
							break;
						case 4:
							handler.sendEmptyMessage(2);
							break;
						case 5:
							break;
						case 9:
							break;
					}
				}
				
				@Override
				public void onFailure(int flag) {
					switch (flag) {
						case -1:
							break;
						default:
							Config.SERVER_HOST_NAME = "";
							handler.sendEmptyMessage(flag);
							break;
					}
				}
			});
		}
		socketTool.initClientSocket();
	}
	
	private void connectPMS() {
		// 拿到PMS信息，判断当前网络下是否有那个wifi，有的话则连接上wifi，然后判断PMS的ip是否存在，然后与PMS连接
		boolean isOpenWifi = WifiUtil.openWifi(context);
		if (!isOpenWifi) {
			while (!WifiUtil.isEnable(context)) {
				MyLog.d("开启wifi中");
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			WifiUtil.startScan();
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	
		List<WifiConfiguration> wifiTempList = WifiUtil.getWifiConfigurationList();
		boolean isExist = false;
		for (int i = 0; i < wifiTempList.size(); i++) {
			WifiConfiguration wifiConfiguration = wifiTempList.get(i);
			String str=wifiConfiguration.SSID.substring(1, wifiConfiguration.SSID.length()-1);
			if (str.equals(connectDeviceBean.getWifiName())) { // 当前可以连接指定wifi
				isExist = true;
				WifiUtil.connectWifi(wifiConfiguration, true);
				h.sendEmptyMessage(0);
				break;
			}
			
			if (i == wifiTempList.size() - 1) {
				List<ScanResult> wifiScanList = WifiUtil.getWifiList();
				for (int j = 0; j < wifiScanList.size(); j++) {
					ScanResult scanResult = wifiScanList.get(j);
					if (scanResult.SSID.equals(connectDeviceBean.getWifiName()) && scanResult.capabilities.contains("[ESS]")) {
						isExist = true;
						WifiUtil.connectWifi(scanResult.SSID, true);
						h.sendEmptyMessage(0);
						break;
					}
				}
			}
		}
		
		if (!isExist) {
			KappUtils.showToast(context, "wifi：" + connectDeviceBean.getWifiName() + "不在范围内或已被清除配置信息");
			closeDlg();
		}
	}

	private void deletePMSDevice(int position) {
		final DeviceNameAndIPBean bean = deviceList.get(position);
		TipConfirmView.showConfirmDialog(context, "是否确认删除Wifi为\"" + bean.getWifiName()
				+ "\"网络下的设备\"" + bean.getName()+ "\"？", new OnClickListener() {
			@Override
			public void onClick(View v) {
				TipConfirmView.dismiss();
				if (new DevicesDAO().deleteDeviceById(bean.getId())) {
					showDeviceList();
					DeviceNameAndIPBean bean2 = SharedPreferenceUtil.getPmsInfo(context);
					if (bean.getName().equals(bean2.getName()) && bean.getIp().equals(bean2.getIp())
							&& bean.getWifiName().equals(bean2.getWifiName())) {
						bean2 = new DeviceNameAndIPBean();
						SharedPreferenceUtil.setPmsInfo(context, bean2);
					}
					KappUtils.showToast(context, "删除成功");
					if (socketTool != null) {
						socketTool.closeSocket();
						socketTool = null;
					}
				} else {
					KappUtils.showToast(context, "删除失败");
				}
			}
		});
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (socketTool != null) {
			socketTool.closeSocket();
		}
		
		if (outTime != null) {
			outTime.cancel();
			outTime = null;
		}
		if (outtime != null) {
			outtime.cancel();
			outtime = null;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.iv_titleRight:
				Intent intent = new Intent(context, AddDevicesActivity.class);
				startActivity(intent);
				break;
		}
	}
}