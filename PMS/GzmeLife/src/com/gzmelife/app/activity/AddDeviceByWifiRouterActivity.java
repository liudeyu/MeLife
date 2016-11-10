package com.gzmelife.app.activity;

import java.util.List;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.espressif.iot.esptouch.EsptouchTask;
import com.espressif.iot.esptouch.IEsptouchResult;
import com.espressif.iot.esptouch.IEsptouchTask;
import com.espressif.iot.esptouch.demo_activity.EspWifiAdminSimple;
import com.gzmelife.app.KappAppliction;
import com.gzmelife.app.R;
import com.gzmelife.app.bean.DeviceNameAndIPBean;
import com.gzmelife.app.device.Config;
import com.gzmelife.app.device.DeviceUtil;
import com.gzmelife.app.device.SocketTool;
import com.gzmelife.app.fragment.DeviceFragment;
import com.gzmelife.app.tools.DateUtil;
import com.gzmelife.app.tools.KappUtils;

@ContentView(R.layout.activity_add_device_by_wifi_router)
public class AddDeviceByWifiRouterActivity extends BaseActivity implements
		OnClickListener {
	@ViewInject(R.id.tv_title)
	TextView tv_title;
	@ViewInject(R.id.et_ssid)
	EditText et_ssid;
	@ViewInject(R.id.et_pwd)
	EditText et_pwd;
	@ViewInject(R.id.tv_title_left)
	TextView tv_title_left;
	private SocketTool socketTool;
	private DeviceUtil deviceUtil;
	private EspWifiAdminSimple mWifiAdmin; // wifi管理模块
	private Context context;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		init();
		initView();
	}

	private void init() {
		context = this;
		mWifiAdmin = new EspWifiAdminSimple(this);
	}

	
	@Override
	protected void onResume() {
		super.onResume();
		//接收PSM数据**********************************************************************************
//		socketTool.receiveMessage();
		//接收PSM数据**********************************************************************************
	}
	private void initView() {
		tv_title.setText("无线路由器");
		et_ssid.setText(mWifiAdmin.getWifiConnectedSsid());
		tv_title_left.setVisibility(View.VISIBLE);
		tv_title_left.setText("添加新设备");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_confirm:
			if (et_pwd.getText().toString().equals("")) {
				KappUtils.showToast(context, "请输入密码");
			} else {
				String apSsid = mWifiAdmin.getWifiConnectedSsid();
				String apPassword = et_pwd.getText().toString();
				String apBssid = mWifiAdmin.getWifiConnectedBssid();
				String isSsidHiddenStr = "NO";
				new EsptouchAsyncTask3().execute(apSsid, apBssid, apPassword,
						isSsidHiddenStr, "1");
			}
			break;
		case R.id.tv_tip:
			startActivity(new Intent(context, RouterTipActivity.class));
			break;
		}
	}

	private class EsptouchAsyncTask3 extends
			AsyncTask<String, Void, List<IEsptouchResult>> {
		private IEsptouchTask mEsptouchTask;
		// without the lock, if the user tap confirm and cancel quickly enough,
		// the bug will arise. the reason is follows:
		// 0. task is starting created, but not finished
		// 1. the task is cancel for the task hasn't been created, it do nothing
		// 2. task is created
		// 3. Oops, the task should be cancelled, but it is running
		private final Object mLock = new Object();

		@Override
		protected void onPreExecute() {
			showDlg();
		}

		@Override
		protected List<IEsptouchResult> doInBackground(String... params) {
			int taskResultCount = -1;
			synchronized (mLock) {
				String apSsid = params[0];
				String apBssid = params[1];
				String apPassword = params[2];
				String isSsidHiddenStr = params[3];
				String taskResultCountStr = params[4];
				boolean isSsidHidden = false;
				if (isSsidHiddenStr.equals("YES")) {
					isSsidHidden = true;
				}
				taskResultCount = Integer.parseInt(taskResultCountStr);
				mEsptouchTask = new EsptouchTask(apSsid, apBssid, apPassword,
						isSsidHidden, context);
				// mEsptouchTask.setEsptouchListener(myListener);
			}
			List<IEsptouchResult> resultList = mEsptouchTask
					.executeForResults(taskResultCount);
			return resultList;
		}

		@Override
		protected void onPostExecute(List<IEsptouchResult> result) {
			IEsptouchResult firstResult = result.get(0);
			// check whether the task is cancelled and no results received
			if (!firstResult.isCancelled()) {
				int count = 0;
				// max results to be displayed, if it is more than
				// maxDisplayCount,
				// just show the count of redundant ones
				final int maxDisplayCount = 1;
				// the task received some results including cancelled while
				// executing before receiving enough results
				if (firstResult.isSuc()) {
					// StringBuilder sb = new StringBuilder();
					for (IEsptouchResult resultInList : result) {
						// sb.append("Esptouch success, bssid = "
						// + resultInList.getBssid()
						// + ",InetAddress = "
						// + resultInList.getInetAddress()
						// .getHostAddress() + "\n");
						Config.SERVER_HOST_IP = resultInList.getInetAddress()
								.getHostAddress();
						// socketTool.initClientSocket();
						KappUtils.showToast(context, "PMS的ip："
								+ Config.SERVER_HOST_IP);
						count++;
						if (count >= maxDisplayCount) {
							break;
						}
					}
					// if (count < result.size()) {
					// sb.append("\nthere's " + (result.size() - count)
					// + " more result(s) without showing\n");
					// }
					// closeDlg();
					// KappUtils.showToast(context, sb.toString());

					// 连接成功，跳到成功界面
					if (socketTool == null) {
						socketTool = new SocketTool(context,
								new SocketTool.OnReceiver() {
									@Override
									public void onSuccess(
											List<String> cookBookFileList,
											int flag, int now, int all) {
										closeDlg();
										deviceUtil = new DeviceUtil(context,
												new DeviceUtil.OnReceiver() {
													@Override
													public void refreshData(
															List<DeviceNameAndIPBean> _list) {
														Message msg = new Message();
														msg.what = 0;
														msg.obj = _list;
														handler.sendMessage(msg);
													}
												});
										deviceUtil.startSearch();
									}

									@Override
									public void onFailure(int flag) {
										handler.sendEmptyMessage(1);
									}
								});
					}
					new Thread(new Runnable() {
						@Override
						public void run() {
							socketTool.initClientSocket(); // 根据不同的ip，建立不同的socket
							socketTool.splitInstruction(Config.bufConnect, null);
						}
					}).start();

					// mProgressDialog.setMessage(sb.toString());
				} else {
					closeDlg();
					KappUtils.showToast(context, "配置失败");
					// mProgressDialog.setMessage("Esptouch fail");
				}
			}
		}
	}

	Handler handler = new android.os.Handler(new Callback() {
		@SuppressWarnings("unchecked")
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				List<DeviceNameAndIPBean> _list = (List<DeviceNameAndIPBean>) msg.obj;
				for (DeviceNameAndIPBean bean : _list) {
					if (bean.getIp().equals(Config.SERVER_HOST_IP)) {
						Config.SERVER_HOST_NAME = bean.getName();
						KappUtils.showToast(context, "连接成功");
						socketTool.splitInstruction(Config.bufSetTime,
								new DateUtil().getCurrentTime());
						System.out.print("----对时功能5----"
								+ new DateUtil().getCurrentTime());
						KappAppliction.state = 1;
						DeviceFragment.isClearList = true;
						startActivity(new Intent(context, MainActivity.class));
						AddDeviceByWifiRouterActivity.this.finish();
						break;
					}
				}
				break;
			case 1:
				KappUtils.showToast(context, "与PMS连接失败");
				KappAppliction.state = 2;
				closeDlg();
				break;
			}
			return false;
		}
	});
	// private IEsptouchListener myListener = new IEsptouchListener() {
	// @Override
	// public void onEsptouchResultAdded(final IEsptouchResult result) {
	// onEsptoucResultAddedPerform(result);
	// }
	// };
	//
	// private void onEsptoucResultAddedPerform(final IEsptouchResult result) {
	// runOnUiThread(new Runnable() {
	// @Override
	// public void run() {
	// // String text = result.getBssid() + " is connected to the wifi";
	// // KappUtils.showToast(context, text);
	// }
	// });
	// }
}
