package com.gzmelife.app.activity;

import java.util.List;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.gzmelife.app.R;
import com.gzmelife.app.adapter.PMSErrorAdapter;
import com.gzmelife.app.device.Config;
import com.gzmelife.app.device.SocketTool;
import com.gzmelife.app.fragment.DeviceFragment;
import com.gzmelife.app.tools.KappUtils;
import com.gzmelife.app.tools.MyLog;
import com.gzmelife.app.views.ListViewForScrollView;

@ContentView(R.layout.activity_device_detail)
public class DeviceDetailActivity extends BaseActivity {
	@ViewInject(R.id.tv_title)
	private TextView tv_title;
	@ViewInject(R.id.tv_v)
	TextView tv_v;
	@ViewInject(R.id.tv_a)
	TextView tv_a;
	@ViewInject(R.id.tv_w)
	TextView tv_w;
	@ViewInject(R.id.tv_pmsTemp)
	TextView tv_pmsTemp;
	@ViewInject(R.id.tv_roomTemp)
	TextView tv_roomTemp;
	@ViewInject(R.id.tv_status)
	TextView tv_status;
	@ViewInject(R.id.tv_title_left)
	TextView tv_title_left;
	@ViewInject(R.id.lv_error)
	ListViewForScrollView lv_error;
	
	private PMSErrorAdapter adapter;
	
	private SocketTool socketTool;
	
	private Context context;
	
	private Thread t;
	
	private boolean state=false;
	
	
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
	
		context = this;
		initView();
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		//接收PSM数据**********************************************************************************
//		socketTool.receiveMessage();
		//接收PSM数据**********************************************************************************
		getDeviceInfo();
	}
	
	private void getDeviceInfo() {
		socketTool = new SocketTool(context, new SocketTool.OnReceiver() {
			@Override
			public void onSuccess(List<String> cookBookFileList, int flag, int now, int all) {
				if (flag == 6) {
					handler.sendEmptyMessage(0);
				}
			}
			
			@Override
			public void onFailure(int flag) {
				handler.sendEmptyMessage(1);
			}
		});
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				socketTool.initClientSocket(); // 根据不同的ip，建立不同的socket
				socketTool.splitInstruction(Config.bufStatus, null);
				state=false;
				System.out.print("----请求设备状态1----");
			}
		}).start();
		
		
	}
	
	private void initView() {
		tv_title.setText("设备状态");
		tv_title_left.setVisibility(View.VISIBLE);
		tv_title_left.setText("设备");
		tv_title.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(state==true){
					if (socketTool != null) {
//						 t.interrupt();
						socketTool.closeSocket(); 
						socketTool=null;
						Intent intent = new Intent();
						intent.putExtra("socket", "socket");
						setResult(RESULT_OK, intent);
						DeviceFragment d=new DeviceFragment();
						d.socket="socket";
						DeviceDetailActivity.this.finish();
						System.out.print("----收到设备状态----");
					}
				}else{
					Toast.makeText(DeviceDetailActivity.this, "请稍后", Toast.LENGTH_SHORT).show();
					System.out.print("----正在请求设备状态----");
				}
				
			}
		});
	}

	Handler handler = new android.os.Handler(new Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
				case 0:
					state=true;
					tv_v.setText(Config.SYSTEM_V);
					tv_a.setText(Config.SYSTEM_A);
					tv_w.setText(Config.SYSTEM_W);
					tv_pmsTemp.setText(Config.PMS_TEMP);
					tv_roomTemp.setText(Config.ROOM_TEMP);
					tv_status.setText(Config.PMS_STATUS);
					
//					Config.PMS_ERRORS.add("0");
					adapter = new PMSErrorAdapter(context, Config.PMS_ERRORS, new PMSErrorAdapter.OnReceiver() {
						@Override
						public void onClick(String position) {
							Intent intent = new Intent(context, AboutUsActivity.class);
							intent.putExtra("flag", KappUtils.FLAG_FAQ_DETAIL_1);
//							intent.putExtra("id", list.get(position - 1).getId());
							intent.putExtra("name", position);
							MyLog.i(MyLog.TAG_I_INFO, "position=====:"+ position);
							startActivity(intent);
						}
					});
					lv_error.setAdapter(adapter);
					// 发送广播，四个主界面左上角图标变更.首次连接成功查询状态，之后每次心跳成功后发送查询
					Intent intent = new Intent();
		            intent.setAction(KappUtils.ACTION_PMS_STATUS);
		            context.sendBroadcast(intent);
//		            t.interrupt();
		            System.out.print("----请求设备状态成功----");
		            // 等待1秒，继续查询
		            new Handler().postDelayed(new Runnable(){   
		                public void run() { 
		                	handler.sendEmptyMessage(2);
		                }   
		             }, 9000);   
					break;
				case 1:
					KappUtils.showToast(context, "获取设备状态失败");
					break;
				case 2:
					if (socketTool != null) {
						socketTool.splitInstruction(Config.bufStatus, null);
						state=false;
						System.out.print("----请求设备状态----");
					}
					break;
			}
			return false;
		}
	});
	
	
	@Override
	protected void onPause() {
		super.onPause();
		if (socketTool != null) {
			socketTool.closeSocket(); 
			socketTool=null;
		}
	}
	
	
	
}

