package com.gzmelife.app.activity;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.espressif.iot.esptouch.demo_activity.EspWifiAdminSimple;
import com.gzmelife.app.R;
import com.gzmelife.app.tools.KappUtils;
import com.gzmelife.app.tools.WifiUtil;

@ContentView(R.layout.activity_add_new_device)
public class AddNewDeviceActivity extends BaseActivity {
	@ViewInject(R.id.tv_title)
	TextView tv_title;
	
	@ViewInject(R.id.btn_wifi_router)
	Button btn_wifi_router;
	@ViewInject(R.id.btn_pms_wifi)
	Button btn_pms_wifi;
	@ViewInject(R.id.tv_title_left)
	TextView tv_title_left;
	private Context context;
	
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		
		context = this;
		initView();
	}

	private void initView() {
		tv_title.setText("添加新设备");		
		tv_title_left.setVisibility(View.VISIBLE);
		tv_title_left.setText("添加设备");
		btn_wifi_router.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (TextUtils.isEmpty(new EspWifiAdminSimple(context).getWifiConnectedSsid())) {
					KappUtils.showToast(context, "请先连接wifi");
				} else {
					Intent intent = new Intent(context, RouterTipActivity.class);
					startActivity(intent);
				}
			}
		});
		btn_pms_wifi.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				WifiUtil.openWifi(context);
				Intent intent = new Intent(context, AddDeviceByPMSWifiActivity.class);
				startActivity(intent);
			}
		});
	}
}