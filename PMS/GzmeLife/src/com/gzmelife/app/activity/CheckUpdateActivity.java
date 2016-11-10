package com.gzmelife.app.activity;

import java.io.File;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Handler.Callback;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gzmelife.app.R;
import com.gzmelife.app.UrlInterface;
import com.gzmelife.app.bean.UpdatePmsBean;
import com.gzmelife.app.device.Config;
import com.gzmelife.app.device.SocketTool;
import com.gzmelife.app.tools.FileUtil;
import com.gzmelife.app.tools.HttpDownloader;
import com.gzmelife.app.tools.KappUtils;
import com.gzmelife.app.tools.MyLogger;
import com.gzmelife.app.tools.ShowDialogUtil;
import com.gzmelife.app.tools.Tools;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

@ContentView(R.layout.activity_check_update)
/**
 * 更新PMS版本界面（当前PMS版本号为）
 *
 */
public class CheckUpdateActivity extends BaseActivity implements OnClickListener {

	MyLogger HHDLog=MyLogger.HHDLog();

	@ViewInject(R.id.tv_title)
	TextView tv_title;
	@ViewInject(R.id.tv_appVersion)
	TextView tv_appVersion;
	@ViewInject(R.id.update_pms_text)
	TextView update_pms_text;
	
	
	
	UpdatePmsBean bean;
	
	public static String isNewest = "";
	
	private Context context;
	
	private String urlfilepath;
	
	private SocketTool socketTool;
	
	private Dialog pDlg;
	private  List<UpdatePmsBean> list;
	
	public static File result1 ;
	
	private File localPmsFile;
	
	private String fileName="";
	
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		
		context = this;
		initView();
	}
	public void closePDlg() {
		if (pDlg != null && pDlg.isShowing()) {
			pDlg.dismiss();
		}
	}

	private void initView() {
		tv_title.setText("更新固件");
		getFile();
		tv_appVersion.setText("当前app版本号为:	" + Tools.getVersion(context));
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.update_pms:
				if (TextUtils.isEmpty(Config.SERVER_HOST_NAME)) {
					KappUtils.showToast(context, "暂未连接设备，请连接");
				} else {
					downLoadMenuBookFile();
				}
				
				break;
			case R.id.btn_updateApp:
				isNewest = "当前已经是最新版本";
				UmengUpdateAgent.setUpdateOnlyWifi(false);
				UmengUpdateAgent.setUpdateAutoPopup(false);
				UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
				    @Override
				    public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
				        switch (updateStatus) {
					        case UpdateStatus.Yes: // has update
					            UmengUpdateAgent.showUpdateDialog(context, updateInfo);
					            break;
					        case UpdateStatus.No: // has no update
					        	if (!isNewest.equals("")) {
					        		KappUtils.showToast(context, isNewest);
					        	}
					            break;
	//				        case UpdateStatus.NoneWifi: // none wifi
	//				            Toast.makeText(mContext, "没有wifi连接， 只在wifi下更新", Toast.LENGTH_SHORT).show();
	//				            break;
					        case UpdateStatus.Timeout: // time out
					        	KappUtils.showToast(context, "获取超时，请稍后重试");
					            break;
				        }
				        isNewest = "";
				        UmengUpdateAgent.setUpdateOnlyWifi(true);
				    }
				});
				UmengUpdateAgent.update(context);
				break;
		}
	}

	Handler handler = new Handler(new Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			closePDlg();
			switch (msg.what) {
			case 0:
				pDlg = ShowDialogUtil.getShowDialog(CheckUpdateActivity.this, R.layout.dialog_progressbar_2,
						FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT, 0, 0, false);
				ShowDialogUtil.setTitle("正在更新PMS固件，请稍候");
				break;
			case 1:
				closePDlg();
				KappUtils.showToast(context, "更新PMS固件失败");
				try {
				File file = new File(FileUtil.SDPATH+"pms/"+ list.get(0).getName()+".bin");
				if(file.exists()){
					file.delete();
				}
				} catch (Exception e) {
					// TODO: handle exception
				}
				break;
			case 2:
				KappUtils.showToast(context, "更新PMS固件成功");
				fileName = list.get(0).getName();
				update_pms_text.setText("当前PMS版本号为:	" + fileName);
				break;

			case 5001:// 进度条开始
				processer = ShowDialogUtil.getShowDialog(CheckUpdateActivity.this, R.layout.dialog_progressbar_2,
						RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, 0, 0,
						false);
				break;
			case 5002:// 进度完下载中
				Bundle bu = msg.getData();

				ShowDialogUtil.setProgress(bu.getInt("cursize"), bu.getInt("allSize"));
				break;
			case 5003:// 进度条完成
				if (processer != null && processer.isShowing()) {
					processer.dismiss();
					processer = null;

				}
				
				// localPMSFileStatus.setDirty();
				// KappUtils.showToast(
				// CheckUpdateActivity.this,
				// "下载完成");
				break;
			case 5004:// 进度条失败
				// Bundle bu1=msg.getData();
				// int code=bu1.getInt("code");
				// if(code==-1)
				// KappUtils.showToast(context, "下载发生错误");
				// if(code==1)
				// KappUtils.showToast(
				// CheckUpdateActivity.this,
				// "该菜谱已下载！");
				if (processer != null && processer.isShowing()) {
					processer.dismiss();
					processer = null;
				}
				break;
			}
			
			
			return false;
		}
	});
	
	private File curUpdata_pms_file;
	public void getFile() {
		localPmsFile = new File(FileUtil.SDPATH+"pms/");
		if (localPmsFile.isDirectory()) {
			for (int i = 0; i < localPmsFile.list().length; i++) {
				result1 = new File(localPmsFile.list()[i]);
				if (result1.getName().endsWith(".bin")
						|| result1.getName().endsWith(".BIN")) {
					String[] str=result1.getName().split(".bin");
					update_pms_text.setText("当前PMS版本号为:	" + str[0]);
					fileName=str[0];
					curUpdata_pms_file=result1;

				}
			}
			// lvFoodCookAdapter.setData(localCookBookBeans);
		} else {
		}
	}
	
	
	private void downLoadMenuBookFile() {
		// 下载
//		showDlg();
		RequestParams params = new RequestParams(
				UrlInterface.URL_UPDATE_PMS);

		x.http().post(params, new CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
//				closeDlg();
				
				Gson gson = new Gson();
				JSONObject obj;
				try {
					obj = new JSONObject(result.trim());
					list= gson.fromJson(obj
							.getJSONObject("data")
							.getJSONArray("fileUpdate").toString(),
							new TypeToken<List<UpdatePmsBean>>() {
							}.getType());
					String msg = obj.getString("msg");
					//update_pms_text.setText("当前PMS版本号为:	" + list.get(0).getName());
					if (!msg.equals("成功")) {
						KappUtils.showToast(context, msg);
					} else {
						if(fileName!=null&&fileName.equals(list.get(0).getName())){
							KappUtils.showToast(
									CheckUpdateActivity.this,
									"当前PMS程序已经是最新版本");
						}else {
						new Thread(new Runnable() {
							@Override
							public void run() {
								Looper.prepare();
								try {
									if(fileName!=null&&fileName.equals(list.get(0).getName())){
										KappUtils.showToast(
												CheckUpdateActivity.this,
												"当前PMS程序已经是最新版本");
									}else{
										if(curUpdata_pms_file!=null){
											curUpdata_pms_file.delete();
										}
										HttpDownloader httpDownLoader = new HttpDownloader();
										httpDownLoader.setOnLoaderListener(new HttpDownloader.loaderListener() {
											
											@Override
											public void onStart(int cursize, int allSize) {
												// TODO Auto-generated method stub
												Message msg=new Message();
												msg.what=5001;
//												processer.show();
												handler.sendMessage(msg);
											}
											
											@Override
											public void onLoading(int cursize, int allSize) {
												// TODO Auto-generated method stub
												Message msg=new Message();
												msg.what=5002;
//												processer.show();
												Bundle bu=new Bundle();
												bu.putInt("cursize", cursize);
												bu.putInt("allSize", allSize);
												msg.setData(bu);
												handler.sendMessage(msg);
											}
											
											@Override
											public void onError(int code) {
												// TODO Auto-generated method stub
//												Log.i(TAG, "onLoading");
												Message msg=new Message();
												msg.what=5004;
//												processer.show();
												Bundle bu=new Bundle();
												bu.putInt("code", code);
												msg.setData(bu);
												handler.sendMessage(msg);
											}
											
											@Override
											public void OnComplete(int code) {
												// TODO Auto-generated method stub
												Message msg=new Message();
												msg.what=5003;
//												processer.show();
												handler.sendMessage(msg);
											}
										});
										result1 = httpDownLoader
												.downfilePms(UrlInterface.URL_HOST
														+ list.get(0).getPath(), "pms/",
														list.get(0).getName()+".bin" );
										// showDlg();
										if (result1 != null) {
//											fileResult = 0;
//											if (TextUtils.isEmpty(Config.SERVER_HOST_NAME)) {
//												KappUtils.showToast(context, "暂未连接设备，请连接");
//											} else {
//												if (Config.isConnext = true) {
//													
//													sendFileToPMS();
//												}
//											}
											if(result1.length()!=0){
											if (TextUtils.isEmpty(Config.SERVER_HOST_NAME)) {
												KappUtils.showToast(context, "暂未连接设备，请连接");
											} else {
												if (Config.isConnext = true) {
													sendFileToPMS();
												}
											}
											}
										}  else if (result1 == null) {
//											fileResult = -1;
											KappUtils.showToast(
													CheckUpdateActivity.this,
//													"更新PMS程序失败");
													"从服务器下载PMS程序失败");
										}
									}
									

								} catch (Exception e) {
									// TODO: handle exception
								}

								Looper.loop();
							}
						}).start();
					}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				// TODO Auto-generated method stub
				closeDlg();
				KappUtils.showToast(CheckUpdateActivity.this, "更新PMS程序失败");
			}

			@Override
			public void onCancelled(CancelledException cex) {
				// TODO Auto-generated method stub
				closeDlg();
				KappUtils.showToast(CheckUpdateActivity.this, "更新PMS程序失败");
			}

			@Override
			public void onFinished() {
				// TODO Auto-generated method stub
				closeDlg();
			}

		});

	}
	private Dialog processer;

	private void sendFileToPMS() {
		handler.sendEmptyMessage(0);

		if (socketTool == null) {
			socketTool = new SocketTool(context, new SocketTool.OnReceiver() {
				@Override
				public void onSuccess(List<String> cookBookFileList, int flag,
						int now, int all) {
					switch (flag) {
					case 7:
						handler.sendEmptyMessage(2);
						break;
					case 8:
						ShowDialogUtil.setProgress(now, all);
						if (TextUtils.isEmpty(Config.SERVER_HOST_NAME)) {
							closePDlg();
							System.out.println("更新失败停止" + 1 + "");
						}
						// timeCntHeart++;
						// timeCntHeartNow++;
						// if(timeCntHeart==0){
						// a=now;
						// }
						// if(timeCntHeart%5==0){
						// b=now;
						// }
						// if(){
						//
						// }
						break;
					case 0:
						closePDlg();
						System.out.println("更新失败停止" + 20 + "");
						break;
					}
				}

				@Override
				public void onFailure(int flag) {
					closePDlg();
					handler.sendEmptyMessage(1);
				}
			});
		}
		
		if (!socketTool.isStartHeartTimer()) {
			// socketTool.heartTimer;
			new Thread(new Runnable() {
				@Override
				public void run() {
					Looper.prepare();
					socketTool.closeSocket();
					socketTool.initClientSocket(); // 根据不同的ip，建立不同的socket
					socketTool.uploadFirmwareInfo();
					socketTool.startHeartTimer();
					HHDLog.w("这里启动了心跳");
					Looper.loop();
				}
			}).start();

		} else {
			// socketTool.initClientSocket(); // 根据不同的ip，建立不同的socket
			socketTool.uploadCookbookInfo();
		}
		// new Thread(new Runnable() {
		// @Override
		// public void run() {
		//
		// }
		// }).start();
	}
	
}
