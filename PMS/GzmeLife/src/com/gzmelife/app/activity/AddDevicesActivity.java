package com.gzmelife.app.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.espressif.iot.esptouch.demo_activity.EspWifiAdminSimple;
import com.gzmelife.app.KappAppliction;
import com.gzmelife.app.R;
import com.gzmelife.app.adapter.AddDeviceAdapter;
import com.gzmelife.app.bean.DeviceNameAndIPBean;
import com.gzmelife.app.device.Config;
import com.gzmelife.app.device.DeviceUtil;
import com.gzmelife.app.device.SocketTool;
import com.gzmelife.app.fragment.DeviceFragment;
import com.gzmelife.app.tools.KappUtils;
import com.gzmelife.app.views.ListViewForScrollView;

public class AddDevicesActivity extends BaseActivity {
	private TextView tv_title;
	private TextView tv_number;
	private TextView tv_title_left;
	private Button bt_addDevice;
	
	private DeviceUtil deviceUtil;
	private SocketTool socketTool;
	
	private ListViewForScrollView lv_data;
	
	private List<DeviceNameAndIPBean> list = new ArrayList<DeviceNameAndIPBean>();
	private AddDeviceAdapter adapter;
	
	private DeviceNameAndIPBean selectBean;
	
	private TimeCountOut outtime;
	
	private Context context;
	
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_add_device);
	
		context = this;
		initView();
		getDevices();
		
		lv_data = (ListViewForScrollView) findViewById(R.id.lv_data);
		adapter = new AddDeviceAdapter(context, list, new AddDeviceAdapter.OnReceiver() {
			@Override
			public void onClick(final int position) {
				selectBean = list.get(position);
				Config.SERVER_HOST_IP = selectBean.getIp();
				
				showDlg();
				// 连接成功，跳到成功界面
				if (socketTool == null) {
					socketTool = new SocketTool(context,
						new SocketTool.OnReceiver() {
							@Override
							public void onSuccess(List<String> cookBookFileList, int flag, int now, int all) {
								closeDlg();
								handler.sendEmptyMessage(1);
							}

							@Override
							public void onFailure(int flag) {
								closeDlg();
								Looper.prepare();
								KappUtils.showToast(context, "与PMS连接中断");
								KappAppliction.state=2;
								Looper.loop();
							}
						});
				}
				new Thread(new Runnable() {
					@Override
					public void run() {
						socketTool.initClientSocket(); // 根据不同的ip，建立不同的socket
						connect();
					}
				}).start();
				outtime = new TimeCountOut(10 * 1000, 1000, null);
				outtime.start();
			}
		});
		lv_data.setAdapter(adapter);
		
		tv_number = (TextView) findViewById(R.id.tv_number);
		
		bt_addDevice = (Button) findViewById(R.id.bt_addDevice);
		bt_addDevice.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(AddDevicesActivity.this, AddNewDeviceActivity.class);
				startActivity(intent);
			}
		});
	}
	
	private void connect() {
		socketTool.splitInstruction(Config.bufConnect, null);
		//接收PSM数据**********************************************************************************
//		socketTool.receiveMessage();
		//接收PSM数据**********************************************************************************
	}

	private void initView() {
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("添加设备");
		tv_title_left = (TextView) findViewById(R.id.tv_title_left);
		tv_title_left.setVisibility(View.VISIBLE);
		tv_title_left.setText("设备中心");
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		//接收PSM数据**********************************************************************************
//		socketTool.receiveMessage();
		//接收PSM数据**********************************************************************************
	}
	private void getDevices() {
		// 连接wifi的状况下，获取局域网内电磁炉
		if (!TextUtils.isEmpty(new EspWifiAdminSimple(this).getWifiConnectedSsid())) {
			KappUtils.getLocalIP(context);
			deviceUtil = new DeviceUtil(context, new DeviceUtil.OnReceiver() {
				@Override
				public void refreshData(List<DeviceNameAndIPBean> _list) {
					list.clear();
					list.addAll(_list);
					handler.sendEmptyMessage(0);
				}
			});
			deviceUtil.startSearch();
		}
	}
	
	Handler handler = new android.os.Handler(new Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
				case 0:
					tv_number.setText("局域网内已发现设备 (" + list.size() + ")");
					adapter.notifyDataSetChanged();
					break;
				case 1:
					Config.SERVER_HOST_NAME = selectBean.getName();
					KappUtils.showToast(context, "连接成功");
					KappAppliction.state=1;
					DeviceFragment.isClearList = true;
					startActivity(new Intent(context, MainActivity.class));
					AddDevicesActivity.this.finish();
					break;
			}
			return false;
		}
	});
	
	@Override
	protected void onPause() {
		super.onPause();
		if (deviceUtil != null) {
			deviceUtil.closeSearch();
		}
		if (socketTool != null) {
			socketTool.closeSocket();
		}
		if (outtime != null) {
			outtime.cancel();
		}
	}
	
}

