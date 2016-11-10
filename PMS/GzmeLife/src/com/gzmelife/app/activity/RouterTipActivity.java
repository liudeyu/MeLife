package com.gzmelife.app.activity;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.espressif.iot.esptouch.demo_activity.EspWifiAdminSimple;
import com.gzmelife.app.R;
import com.gzmelife.app.tools.KappUtils;

@ContentView(R.layout.actvitiy_router_tip)
public class RouterTipActivity extends BaseActivity{
	@ViewInject(R.id.tv_title)
	TextView tv_title;
	
	@ViewInject(R.id.iv_img)
	ImageView iv_img;
	@ViewInject(R.id.tv_title_left)
	TextView tv_title_left;
	private Context context;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		context = this;
		initView();
	}
	
	private void initView() {
		tv_title.setText("配置指南");
		tv_title_left.setVisibility(View.VISIBLE);
		tv_title_left.setText("添加新设备");
		iv_img.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (TextUtils.isEmpty(new EspWifiAdminSimple(context).getWifiConnectedSsid())) {
					KappUtils.showToast(context, "请先连接wifi");
				} else {
					Intent intent = new Intent(context, AddDeviceByWifiRouterActivity.class);
					startActivity(intent);
				}
			}
		});
	}
}
