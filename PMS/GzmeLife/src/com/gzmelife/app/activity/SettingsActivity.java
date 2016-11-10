package com.gzmelife.app.activity;

import java.io.File;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import cn.jpush.android.api.JPushInterface;

import com.gzmelife.app.R;
import com.gzmelife.app.tools.DataCleanManager;
import com.gzmelife.app.tools.KappUtils;
import com.gzmelife.app.tools.MyLog;
import com.gzmelife.app.tools.SharedPreferenceUtil;
import com.nostra13.universalimageloader.utils.StorageUtils;

@ContentView(R.layout.activity_settings)
public class SettingsActivity extends BaseActivity implements android.view.View.OnClickListener {
	@ViewInject(R.id.tv_title)
	TextView tv_title;
	
	@ViewInject(R.id.cb_isOpen)
	CheckBox cb_isOpen;
	
	@ViewInject(R.id.layout_clear_cash)
	View layout_clear_cash;
	@ViewInject(R.id.layout_update)
	View layout_update;
	@ViewInject(R.id.layout_system_msg)
	View layout_system_msg;
	@ViewInject(R.id.tv_title_left)
	TextView tv_title_left;
	private Context context;
	
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		
		context = this;
		initView();
	}

	private void initView() {
		tv_title.setText("设置");
		tv_title_left.setVisibility(View.VISIBLE);
		tv_title_left.setText("个人中心");
		layout_update.setOnClickListener(this);
		layout_clear_cash.setOnClickListener(this);
		layout_system_msg.setOnClickListener(this);
		
		cb_isOpen.setChecked(SharedPreferenceUtil.getPushSwitch(context) == 1);
		cb_isOpen.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				SharedPreferenceUtil.setPushSwitch(context, isChecked ? 1 : 0);
				if(isChecked==false){
					try {
						JPushInterface.stopPush(getApplicationContext());
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
			case R.id.layout_update:
				intent = new Intent(this, CheckUpdateActivity.class);
				startActivity(intent);
				break;
			case R.id.layout_system_msg:
				intent = new Intent(this, SystemMsgActivity.class);
				startActivity(intent);
				break;
			case R.id.layout_clear_cash:
				System.gc();
				showDlg();
				String filePath = StorageUtils.getCacheDirectory(context)
						.getAbsolutePath();
				try {
					MyLog.d("缓存：" + DataCleanManager.getFolderSize(new File(filePath)));
				} catch (Exception e) {
					e.printStackTrace();
				}
				DataCleanManager.deleteFolderFile(filePath, true);
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						closeDlg();
						KappUtils.showToast(context, "缓存清理成功");
					}
				}, 500);
				break;
		}
	}

}
