package com.gzmelife.app.activity;

import java.util.ArrayList;
import java.util.List;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.espressif.iot.esptouch.demo_activity.EspWifiAdminSimple;
import com.gzmelife.app.KappAppliction;
import com.gzmelife.app.R;
import com.gzmelife.app.adapter.PMSWifiAdapter;
import com.gzmelife.app.device.Config;
import com.gzmelife.app.device.SocketTool;
import com.gzmelife.app.fragment.DeviceFragment;
import com.gzmelife.app.tools.DateUtil;
import com.gzmelife.app.tools.KappUtils;
import com.gzmelife.app.tools.MyLog;
import com.gzmelife.app.tools.WifiUtil;

@ContentView(R.layout.activity_add_device_by_pms_wifi)
public class AddDeviceByPMSWifiActivity extends BaseActivity {
	@ViewInject(R.id.tv_title)
	TextView tv_title;
	@ViewInject(R.id.tv_title_left)
	TextView tv_title_left;
	
	@ViewInject(R.id.layout_no_data)
	View layout_no_data;
	
	@ViewInject(R.id.lv_pmsWifi)
	ListView lv_pmsWifi;
	
	private TimeCountOut outTime;  // 与wifi建立连接
	private TimeCountOut outtime; // 与PMS建立连接
	private int failCount = 0; // 失败一次+1，等于3时，失败3次，给出提示，连接失败
	
	private String tempName;
	
	private SocketTool socketTool;
	
	private List<ScanResult> wifiList = new ArrayList<ScanResult>();
	
	private PMSWifiAdapter adapter;
	
	private Context context;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		
		init();
		initView();
	}

	private void init() {
		context = this;
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		//接收PSM数据**********************************************************************************
//		socketTool.receiveMessage();
		//接收PSM数据**********************************************************************************
	}
	
	@Override
	protected void onPause() {
		super.onPause();
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
	protected void onDestroy() {
		super.onDestroy();
		if (socketTool != null) {
			socketTool.closeSocket();
			socketTool = null;
		}
	}

	private void initView() {
		tv_title.setText("直连模式");
		tv_title_left.setVisibility(View.VISIBLE);
		tv_title_left.setText("添加新设备");
		adapter = new PMSWifiAdapter(context, wifiList, new PMSWifiAdapter.OnReceiver() {
			@Override
			public void onClick(final int position) {
				if (!WifiUtil.isEnable(context)) {
					KappUtils.showToast(context, "wifi被关闭，请开启后重试");
					return;
				}
				
				showDlg();
				WifiUtil.connectWifi(wifiList.get(position).SSID, true);
				outTime = new TimeCountOut(1000 * 15, 1000, new OnEvent() {
					@Override
					public void onFinish() {
						MyLog.d((WifiUtil.getWifiInfo() != null) + "," + !TextUtils.isEmpty(new EspWifiAdminSimple(context).getWifiConnectedSsid())
								+ ",WifiUtil.getWifiInfo().getSSID()=" + WifiUtil.getWifiInfo().getSSID()
								+ ",wifiList.get(position).SSID)=" + wifiList.get(position).SSID);
						if (WifiUtil.getWifiInfo() != null
								&& !TextUtils.isEmpty(new EspWifiAdminSimple(context).getWifiConnectedSsid())
								&& WifiUtil.getWifiInfo().getSSID().equals("\"" + wifiList.get(position).SSID + "\"")) {
							KappUtils.showToast(context, "连接wifi成功");
							
							tempName = wifiList.get(position).SSID;
							handler.sendEmptyMessage(2);
						} else {
							handler.sendEmptyMessage(4);
						}
					}

					@Override
					public void onTick(long millisUntilFinished) {
						MyLog.d("onTick ." + millisUntilFinished);
						if (WifiUtil.getWifiInfo() != null
								&& !TextUtils.isEmpty(new EspWifiAdminSimple(context).getWifiConnectedSsid())
								&& WifiUtil.getWifiInfo().getSSID().equals("\"" + wifiList.get(position).SSID + "\"")) {
							KappUtils.showToast(context, "连接wifi成功");
							MyLog.d("onTick 连接wifi成功." + millisUntilFinished);
							outTime.cancel();
							outTime = null;
							
							tempName = wifiList.get(position).SSID;
							handler.sendEmptyMessage(2);
						}
					}
				});
				outTime.start();
			}
		});
		lv_pmsWifi.setAdapter(adapter);
		
		showDlg();
		new Thread(new Runnable() {
			@Override
			public void run() {
				Looper.prepare();
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
			
				List<ScanResult> wifiTempList = WifiUtil.getWifiList();
				
				for (int i = 0; i < wifiTempList.size(); i++) {
					ScanResult bean = wifiTempList.get(i);
					if (bean.capabilities.equals("[ESS]") && bean.SSID.startsWith("PMS_")) { // 无密码，且PMS_开头
						wifiList.add(bean);
					}
				}
				
				handler.sendEmptyMessage(0);
				Looper.loop();
			}
		}).start();
	}
	
	Handler handler = new Handler(new Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			if (msg.what == 0) {
				closeDlg();
				if (wifiList.size() == 0) {
					layout_no_data.setVisibility(View.VISIBLE);
					lv_pmsWifi.setVisibility(View.GONE);
				} else {
					layout_no_data.setVisibility(View.GONE);
					lv_pmsWifi.setVisibility(View.VISIBLE);
					
					adapter.notifyDataSetChanged();
				}
			} else if (msg.what == 1) {
				closeDlg();
				if (outtime != null) {
					outtime.cancel();
					outtime = null;
				}
				KappUtils.showToast(context, "连接电磁炉成功");
				socketTool.splitInstruction(Config.bufSetTime, new DateUtil().getCurrentTime());
				System.out.print("----对时功能3----"+new DateUtil().getCurrentTime());
				KappAppliction.state=1;
				DeviceFragment.isClearList = true;
				startActivity(new Intent(context, MainActivity.class));
				AddDeviceByPMSWifiActivity.this.finish();
			} else if (msg.what == 3) {
				failCount++;
				if (failCount == 3) {
					KappAppliction.state=2;
					KappUtils.showToast(context, "与PMS连接失败");
					closeDlg();
					if (outtime != null) {
						outtime.cancel();
						outtime = null;
					}
				}
			} else if (msg.what == 2) { // 连接PMS的wifi成功，进行指令连接
				showDlg();
				failCount = 0;
				Config.SERVER_HOST_IP = Config.SERVER_HOST_DEFAULT_IP;
				// 连接成功，跳到成功界面
				if (socketTool == null) {
					socketTool = new SocketTool(context,
						new SocketTool.OnReceiver() {
							@Override
							public void onSuccess(List<String> cookBookFileList, int flag, int now, int all) {
								Config.SERVER_HOST_NAME = tempName;
								handler.sendEmptyMessage(1);
							}

							@Override
							public void onFailure(int flag) {
								handler.sendEmptyMessage(3);
							}
						});
				}
				// 倒计时10秒，10秒内没有指令与PMS连接成功，则给出提示，且去掉转圈
				outtime = new TimeCountOut(10 * 1000, 1000, new OnEvent() {
					@Override
					public void onFinish() {
						failCount = 2;
						handler.sendEmptyMessage(3);
					}

					@Override
					public void onTick(long millisUntilFinished) {
						MyLog.d("PMS指令连接倒计时：" + millisUntilFinished);
						if (millisUntilFinished / 1000 % 3 == 0) {
							new Thread(new Runnable() {
								@Override
								public void run() {
									socketTool.initClientSocket(); // 根据不同的ip，建立不同的socket
									socketTool.splitInstruction(Config.bufConnect, null);
								}
							}).start();
						}
					}
				});
				outtime.start();
			} else if (msg.what == 4) {
				wifiList.clear();
				List<ScanResult> wifiTempList = WifiUtil.getWifiList();
				for (int i = 0; i < wifiTempList.size(); i++) {
					ScanResult bean = wifiTempList.get(i);
					if (bean.capabilities.equals("[ESS]") && bean.SSID.startsWith("PMS1_")) { // 无密码，且PMS_开头
						wifiList.add(bean);
					}
				}
				adapter.notifyDataSetChanged();
				KappAppliction.state=2;
				KappUtils.showToast(context, "连接失败");
			}
			return false;
		}
	});
}
