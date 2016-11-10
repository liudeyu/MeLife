package com.gzmelife.app.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import com.google.gson.Gson;
import com.gzmelife.Status.localPMSFileStatus;
import com.gzmelife.Status.smartPotStatu;
import com.gzmelife.app.KappAppliction;
import com.gzmelife.app.R;
import com.gzmelife.app.UrlInterface;
import com.gzmelife.app.adapter.CookBookStopAdapter;
import com.gzmelife.app.hhd.AddStepActivity;
import com.gzmelife.app.hhd.TimeNodeFood;
import com.gzmelife.app.bean.UserInfoBean;
import com.gzmelife.app.dao.Share_data_entity;
import com.gzmelife.app.device.Config;
import com.gzmelife.app.device.SocketTool;
import com.gzmelife.app.tools.BeSureDialog;
import com.gzmelife.app.tools.BeSureDialog.OnSelected;
import com.gzmelife.app.tools.CameraUtil;
import com.gzmelife.app.tools.DataUtil;
import com.gzmelife.app.tools.FileUtil;
import com.gzmelife.app.tools.ImageShower;
import com.gzmelife.app.tools.KappUtils;
import com.gzmelife.app.tools.MyLog;
import com.gzmelife.app.tools.MyLogger;
import com.gzmelife.app.tools.PmsFile;
import com.gzmelife.app.tools.ShowDialogUtil;
import com.gzmelife.app.hhd.TimeNode;
import com.gzmelife.app.views.ListViewForScrollView;
import com.gzmelife.app.views.TipConfirmView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

@ContentView(R.layout.activity_cook_book_detail)

/**
 * 界面【菜谱详情&编辑菜谱(保存)】
 */
public class CookBookDetailActivity extends BaseActivity implements OnClickListener {

	private String smartFileName = null;
	/** 20161013存放时间节点每个食物 */
	ArrayList<TimeNodeFood> timeNodeFoodList = new ArrayList<>();

	MyLogger HHDLog=MyLogger.HHDLog();

	@ViewInject(R.id.lv_step)
	ListViewForScrollView lv_step;
	String TAG = "CookBookDetailActivity";
	@ViewInject(R.id.iv_photo)
	ImageView iv_photo;
	@ViewInject(R.id.et_name)
	EditText et_name;
	@ViewInject(R.id.et_describe)
	EditText et_describe;
	@ViewInject(R.id.tv_material2)
	TextView tv_material2;
	@ViewInject(R.id.tv_title)
	TextView tv_title;
	@ViewInject(R.id.btn_uploading)
	Button btn_uploading;
	@ViewInject(R.id.iv_titleRight)
	ImageView iv_titleRight;
	@ViewInject(R.id.btn_etCookBook)
	Button btn_etCookBook;
	@ViewInject(R.id.tv_describe)
	TextView tv_describe;
	@ViewInject(R.id.iv_edtPhoto)
	ImageView iv_edtPhoto;
	@ViewInject(R.id.iv_titleLeft)
	ImageView iv_titleLeft;
	@ViewInject(R.id.ll3)
	LinearLayout ll3;
	@ViewInject(R.id.ll_bottom)
	LinearLayout ll_bottom;
	@ViewInject(R.id.btn_titleRight)
	Button btn_titleRight;
	@ViewInject(R.id.tv_title_left)
	TextView tv_title_left;
	CookBookStopAdapter cookBookStopAdapter;
	String sdPath = null;
	private SocketTool socketTool;
	public static String filePath;
	private Dialog pDlg;
	private Button btn_cancel;
	List<TimeNode> listTimeNode;
	private Context context;
	private PopupWindow sharpPopupWindow, sharpPopupWindow2;
	private boolean isSharp;
	TimeNode timeNode;
	File file;
	CameraUtil cameraUtil;
	public final int EDT_STEP_REQUESTCODE = 0X1000;
	public final int ADD_STEP_REQUESTCODE = 0X2000;
	public static Bitmap bitmap = null;
	UserInfoBean user;
	private String urlfilepath;
	public static CookBookDetailActivity detail = null;
	final int REQUEST_CODE = 101;

	public static int backgroundResource = 0;

	public static boolean ivState = false;

	private int position;
	private boolean isChick = false;
	/** 上个界面传的状态 */
	private boolean state = false;
	// 菜谱ID
	private String cookId;

	/** 20161012PMS菜谱结构 */
	private PmsFile pmsFile;

	private int flag = 0;

	private String editString;

	private String linshiString;

	private int flagStart = 0;

	public static String newFilePath;
	public static boolean stat = false;

	private String newName;
	private String newContent;
	private Bitmap newLog;
	private int intFlag = 0;
	private Gson gson;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		context = this;
		detail = this;
		initView();
		cookId = getIntent().getStringExtra("cookId");
		state = getIntent().getBooleanExtra("state", false);
		position = getIntent().getIntExtra("position", 0);
		// listTimeNode.add((TimeNode) getIntent()
		// .getSerializableExtra("timeNode"));
		timeNode = (TimeNode) getIntent().getSerializableExtra("timeNode");
		// cookBookStopAdapter.notifyDataSetChanged();
		HHDLog.w("cookId="+cookId+"，timeNode="+timeNode+"，state="+String.valueOf(state)+"，cookId="+cookId);
		if (state) {
			getFileInfo1();/** 编辑保存后返回之后显示 */
			setType(true);
		} else {
			setType(false);
			getFileInfo();/** 私厨界面进入菜谱详情界面时执行 */
		}

		if (KappAppliction.getLiLogin() == 1) {
			user = KappAppliction.myApplication.getUser();

		} else {
			// KappUtils.showToast(context, "暂未登录");
			return;
		}
		// update();
		//Log.i(TAG, "=============================>onCreate");
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		//Log.i(TAG, "=============================>onRestart");
	}

	@Override
	protected void onResume() {
		super.onResume();
		// update();
		HHDLog.v("界面【菜谱详情&编辑菜谱】");
	}

	@Override
	protected void onStop() {
		super.onStop();
		Config.Name = null;
		//Log.i(TAG, "=============================>onStop");
	}

	private void initView() {
		btn_uploading.setOnClickListener(this);
		iv_titleRight.setVisibility(View.VISIBLE);
		iv_titleRight.setImageResource(R.drawable.icn_more);
		iv_titleRight.setOnClickListener(this);
		iv_titleLeft.setOnClickListener(this);
		findViewById(R.id.btn_sharp2).setOnClickListener(this);
		btn_etCookBook.setOnClickListener(this);
		listTimeNode = new ArrayList<TimeNode>();

		cookBookStopAdapter = new CookBookStopAdapter(this, listTimeNode);
		lv_step.setAdapter(cookBookStopAdapter);

		iv_photo.setOnClickListener(this);
		setType(false);
	}

	/** 是否为编辑状态：false=不是编辑*/
	private void setType(boolean isEdt) {
		cookBookStopAdapter.setEdt(isEdt);
		btn_etCookBook.setVisibility(isEdt ? View.INVISIBLE : View.VISIBLE);
		// tv_describe.setVisibility(isEdt ? View.VISIBLE : View.GONE);
		iv_edtPhoto.setVisibility(isEdt ? View.VISIBLE : View.GONE);
		iv_edtPhoto.setOnClickListener(this);
		ll3.setVisibility(isEdt ? View.GONE : View.VISIBLE);
		ll_bottom.setVisibility(isEdt ? View.GONE : View.VISIBLE);
		btn_titleRight.setVisibility(isEdt ? View.VISIBLE : View.GONE);
		btn_titleRight.setOnClickListener(this);
		iv_titleRight.setVisibility(isEdt ? View.GONE : View.VISIBLE);
		tv_title.setText(isEdt ? "编辑菜谱" : "菜谱详情");
		tv_title_left.setVisibility(View.VISIBLE);
		tv_title_left.setText(isEdt ? "菜谱详情" : "本地菜谱");
		et_name.setEnabled(isEdt);

		// if (btn_etCookBook.getVisibility() == View.GONE
		// && tv_title_left.getText().equals("菜谱详情")
		// && btn_titleRight.getText().equals("保存")) {
		// isLongChick(true);
		// } else {
		// isLongChick(false);
		// }
		et_describe.setEnabled(isEdt);
	}

	/** 长按删除 */
	private void isLongChick(boolean isEdt) {
		if (isEdt == true) {
			lv_step.setOnItemLongClickListener(new OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
					if (listTimeNode.size() > 1) {
						TipConfirmView.showConfirmDialog(context, "是否确认删除?", new OnClickListener() {
							@Override
							public void onClick(View v) {
								TipConfirmView.dismiss();
								listTimeNode.remove(position);
								KappUtils.showToast(context, "步骤删除成功");
								cookBookStopAdapter.notifyDataSetInvalidated();
								pmsFile.getTimeNode().remove(position);
							}
						});
					} else {
						KappUtils.showToast(context, "必需保留一个步骤");
					}
					return false;
				}
			});

		} else if (isEdt == false) {
			lv_step.setOnItemLongClickListener(new OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
					// TipConfirmView.showConfirmDialog(context, "是否确认删除?",
					// new OnClickListener() {
					// @Override
					// public void onClick(View v) {
					// TipConfirmView.dismiss();
					// listTimeNode.remove(position);
					// KappUtils.showToast(context, "步骤删除成功");
					// cookBookStopAdapter
					// .notifyDataSetInvalidated();
					// }
					// });
					return false;
				}
			});
		}
	}

	int timeCntHeart = 0;
	int timeCntHeartNow = 0;
	int a = 0, b = 0;

	private void sendFileToPMS() {
		Log.i(TAG, "sendFileToPMS-->");
		handler.sendEmptyMessage(0);

		if (socketTool == null) {
			socketTool = new SocketTool(context, new SocketTool.OnReceiver() {
				@Override
				public void onSuccess(List<String> cookBookFileList, int flag, int now, int all) {
					// Log.i(TAG, "net
					// sendFileToPMS-->onSuccess-->cookBookFileList.size"+String.valueOf(cookBookFileList.size())
					// +"flag:"+String.valueOf(flag));
					switch (flag) {
						case 7:
							handler.sendEmptyMessage(2);
							break;
						case 8:
							ShowDialogUtil.setProgress(now, all);
							/** 暂停上传 */
							if (TextUtils.isEmpty(Config.SERVER_HOST_NAME)) {
								closePDlg();
								HHDLog.e("上传失败停止" + 1 + "");
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
							HHDLog.e("上传失败停止" + 20 + "");
							break;
					}
				}

				@Override
				public void onFailure(int flag) {
					Log.i(TAG, "onFailure(int flag)-->" + String.valueOf(flag));
					closePDlg();

					if (flag == 50000) {
						handler.sendEmptyMessage(50000);
					} else {
						handler.sendEmptyMessage(1);
					}
				}
			});
		}
		if (filePath == null || !filePath.contains("/")) {
			KappUtils.showToast(context, "文件错误");
			return;
		}
		if (!socketTool.isStartHeartTimer()) {
			// socketTool.heartTimer;
			new Thread(new Runnable() {
				@Override
				public void run() {
					Looper.prepare();
					socketTool.closeSocket();
					socketTool.initClientSocket(); // 根据不同的ip，建立不同的socket
					socketTool.uploadCookbookInfo();
					socketTool.startHeartTimer();
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

	public void closePDlg() {
		if (pDlg != null && pDlg.isShowing()) {
			pDlg.dismiss();
		}
	}

	Handler handler = new Handler(new Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			closePDlg();
			switch (msg.what) {
				case 0:
					pDlg = ShowDialogUtil.getShowDialog(CookBookDetailActivity.this, R.layout.dialog_progressbar_2,
							FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT, 0, 0, false);
					ShowDialogUtil.setTitle("正在传送菜谱，请稍候");
					ShowDialogUtil.setCancelButton(View.VISIBLE);//20161010显示取消按钮
					break;
				case 1:
					closePDlg();
					KappUtils.showToast(context, "上传文件到智能锅失败");
					// socketTool.closeSocket();
					break;
				case 2:
					KappUtils.showToast(context, "上传文件到智能锅成功");

					smartPotStatu smart = new smartPotStatu();
					smart.setDirty();
					break;
				case 50000:
					KappUtils.showToast(context, "智能锅拒绝接受上传文件");
					closePDlg();
					// socketTool.closeSocket();
					break;
			}
			return false;
		}
	});


	@Override
	protected void onDestroy() {

		super.onDestroy();
		if (socketTool != null) {
			socketTool.closeSocket();
			socketTool = null;
		}
		if(bitmap!=null){
			bitmap.recycle();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_uploading:
				// if (KappAppliction.state == 1) {
				// sendFileToPMS();
				// } else if (KappAppliction.state == 2) {
				// KappUtils.showToast(context, "暂未连接设备，请连接");
				// }
				if (Config.cancelTransfer){
					Config.cancelTransfer=false;
				}
				if (TextUtils.isEmpty(Config.SERVER_HOST_NAME)) {
					KappUtils.showToast(context, "暂未连接设备，请连接");
				} else {
					if (Config.isConnext = true) {
						sendFileToPMS();
					}
				}
				break;

			case R.id.iv_titleRight:
				showSharp();
				break;

			case R.id.btn_sharp:
				isSharp = true;
				showSharp2();
				sharpPopupWindow.dismiss();
				break;

			case R.id.btn_upload:
				// if (KappAppliction.state == 1) {
				// sendFileToPMS();
				// } else if (KappAppliction.state == 2) {
				// KappUtils.showToast(context, "暂未连接设备，请连接");
				// }
				if (TextUtils.isEmpty(Config.SERVER_HOST_NAME)) {
					KappUtils.showToast(context, "暂未连接设备，请连接");
				} else {
					if (Config.isConnext = true) {
						sendFileToPMS();
					}
				}
				sharpPopupWindow.dismiss();
				break;
			case R.id.btn_cancel:
				sharpPopupWindow.dismiss();
				break;
			case R.id.btn_wechat:
				Log.i(TAG, "点击btn_wechat");
				flag = 1;
				if (KappAppliction.getLiLogin() == 1) {
					// checkPmsFile(file.getName());
					if (stat == false) {
						checkPmsFile(file.getName());
					} else {
						file = new File(newFilePath);
						checkPmsFile(file.getName());
						// uploadHeadPortrait(newFilePath, file.getName());
					}

				} else {
					KappUtils.showToast(context, "暂未登录");
					Intent intent = new Intent(CookBookDetailActivity.this, LoginActivity.class);
					startActivity(intent);
					CookBookDetailActivity.this.finish();
				}

				break;

			case R.id.btn_wechat2:
				Log.i(TAG, "点击btn_wechat2");
				flag = 2;
				if (KappAppliction.getLiLogin() == 1) {
					// 1
					if (stat == false) {
						checkPmsFile(file.getName());
					} else {
						file = new File(newFilePath);
						checkPmsFile(file.getName());
						// uploadHeadPortrait(newFilePath, file.getName());
					}
					// ShareParams wechat2 = new ShareParams();
					// wechat2.setTitle("分享美食,分享快乐!");
					// wechat2.setText("美易来真给力,我们一起看看吧!");
					// wechat2.setImageUrl("http://121.41.86.29:8888/pms/attached/image/20160519/20160519161641_192.png");
					//
					// wechat2.setUrl("http://itpang.com/food/food/index.php/Home/Index/details.html?id="+cookId+"&is_show=1");
					// wechat2.setShareType(Platform.SHARE_WEBPAGE);
					//
					// Platform weixin2 = ShareSDK.getPlatform(context,
					// WechatMoments.NAME);
					//
					// if(weixin2.isClientValid()){
					// System.out.println("安装了微信");
					// }else {
					// System.out.println("没有安装了微信");
					// }
					// // weixin.setPlatformActionListener(MainActivity.this);
					// weixin2.share(wechat2);
					// OnekeyShare oks1 = new OnekeyShare();
					// oks1.setPlatform("WechatMoments");
					// oks1.setTitle("分享美食,分享快乐!");
					// oks1.setTitleUrl("http://itpang.com/food/food/index.php/Home/Index/details.html"+"?id="+cookId);
					// oks1.setText("http://itpang.com/food/food/index.php/Home/Index/details.html"+"?id="+cookId);
					//
					// oks1.setImagePath("http://121.41.86.29:8999/khj/jsp/share1.jsp");
					// oks1.setUrl("http://121.41.86.29:8999/khj/jsp/share1.jsp");
					// oks1.setComment("暂无评论");
					// oks1.setSite("美易来");
					// oks1.setSiteUrl("http://121.41.86.29:8999/khj/jsp/share1.jsp");
					// oks1.setImageArray(null);
					// oks1.disableSSOWhenAuthorize();
					// oks1.show(context);
					// share.setPlatform("WechatMoments");
					// share.initShare("分享美食,分享快乐!", "美易来真给力,我们一起看看吧!",
					// "https://mmbiz.qlogo.cn/mmbiz/GtfaHt6jjjUzlS8CiatpDBdl66q9s86O1CZ4cLeXfncibUIcMxGrjbDFXUwgsIdtDHMavJUMy61ZJRqF0xa5icibyg/0?wx_fmt=png",
					// "http://itpang.com/food/food/index.php/Home/Index/details.html?id="+cookId,null);
					// share.startShare();
				} else {
					KappUtils.showToast(context, "暂未登录");
					Intent intent = new Intent(CookBookDetailActivity.this, LoginActivity.class);
					startActivity(intent);
					CookBookDetailActivity.this.finish();
				}

				break;
			case R.id.btn_upload2:
				// 上传文件到后台
				if (KappAppliction.getLiLogin() == 1) {
					if (stat == false) {
						Log.i(TAG, "stat:false,filePath-->" + filePath + ";fileName-->" + file.getName());
						uploadHeadPortrait(filePath, file.getName(), "1");
					} else {
						Log.i(TAG, "stat:true,filePath-->" + newFilePath + ";fileName-->" + file.getName());
						uploadHeadPortrait(newFilePath, file.getName(), "1");
					}
				} else {
					KappUtils.showToast(context, "暂未登录");
					Intent intent = new Intent(CookBookDetailActivity.this, LoginActivity.class);
					startActivity(intent);
					CookBookDetailActivity.this.finish();
				}
				break;
			case R.id.btn_sharp2:
				showSharp2();
				break;
			case R.id.btn_etCookBook:/** 编辑菜谱按钮 */
				editString = file.getName().substring(0, file.getName().lastIndexOf("."));
				Log.i(TAG, "filename--->" + file.getName());
				if (TextUtils.isEmpty(Config.Name)) {
					Log.i(TAG, "Config.Name==null--->et_name-->" + editString);
					et_name.setText(editString);
				} else {
					et_name.setText(Config.Name);
					Log.i(TAG, "Config.Name!=null--->et_name-->" + Config.Name);
				}

				setType(true);
				isLongChick(true);
				break;
			case R.id.iv_titleLeft:
				if (btn_etCookBook.getVisibility() == View.VISIBLE) {
					Intent mIntent = new Intent();
					setResult(REQUEST_CODE, mIntent);
					// setResult(resultCode, data);
					finish();
				} else {
					isLongChick(false);
					setType(false);
					getFileInfo();
				}
				break;
			case R.id.iv_edtPhoto:
				cameraUtil = new CameraUtil(this, this);
				cameraUtil.toCameraPhoto(true);
				break;

			case R.id.btn_titleRight:/** 保存按钮 */
				if (TextUtils.isEmpty(et_name.getText().toString().trim())) {
					KappUtils.showToast(context, "菜谱名不能为空！");
					return;
				}
				update();
				if (state) {
					getfileInfoByName1(Config.Name + ".pms");/** 加载菜谱数据 */
					setType(true);
				} else {
					setType(false);
					getfileInfoByName1(Config.Name + ".pms");
				}
				isLongChick(false);
				if (!TextUtils.isEmpty(et_name.getText())) {
					setType(false);
				} else {
					setType(true);
				}
				break;

			case R.id.iv_photo:
				if (CookBookDetailActivity.ivState == false) {
					Intent intent = new Intent(CookBookDetailActivity.this, ImageShower.class);
					startActivity(intent);
				} else {

				}

				break;
		}
	}

	/**
	 * 分享
	 *
	 * @param //headpath
	 */
	private void uploadTempFile(String filepath, String fileName) {
		Log.i(TAG, "uploadTempFile(String filepath, String fileName)-->" + filepath + "--" + fileName);
		RequestParams entity = new RequestParams(UrlInterface.URL_PMSFILETEMPPORARY);
		MyLog.i(TAG, "路径：" + UrlInterface.URL_PMSFILETEMPPORARY);
		entity.setMultipart(true);
		entity.addBodyParameter("userId", user.getId());
		entity.addBodyParameter("fileName", fileName);
		entity.addBodyParameter("file", new File(filepath), "multipart/form-data");
		x.http().post(entity, new CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
				try {
					Log.i(TAG, "net uploadTempFile-->onSuccess:" + result);
					JSONObject jsonObject = new JSONObject(result);

					String status = jsonObject.getString("status");
					// if (!msg.equals("成功")) {
					if (!status.equals("10001")) {
						Log.i(TAG, "net uploadTempFile-->onSuccess:" + "非10001");
						KappUtils.showToast(context, "分享失败!");

					} else {
						Log.i(TAG, "net uploadTempFile-->onSuccess:" + "10001" + ":flag=" + String.valueOf(flag));
						cookId = jsonObject.getJSONObject("data").getString("id");
						if (flag == 1) {
							if (KappAppliction.getLiLogin() == 1) {
								Share_data_entity data = new Share_data_entity();
								data.setTargeURL("http://itpang.com/food/food/index.php/Home/Index/details.html?id="
										+ cookId + "&is_show=1");
								data.setImageURL(
										UrlInterface.URL_HOST+"attached/image/20160519/20160519161641_192.png");
								data.setText("美易来真给力,我们一起看看吧!");
								data.setTitle("分享美食,分享快乐!");

								share_Test share = new share_Test();
								share.setData(CookBookDetailActivity.this, data);

								share.share(share_Test.platform.weixin);
								// ShareParams wechat = new ShareParams();
								// wechat.setTitle("分享美食,分享快乐!");
								// wechat.setText("美易来真给力,我们一起看看吧!");
								// wechat.setImageUrl("http://121.41.86.29:8888/pms/attached/image/20160519/20160519161641_192.png");
								//
								// wechat.setUrl("http://itpang.com/food/food/index.php/Home/Index/details.html?id="+cookId+"&is_show=1");
								// wechat.setShareType(Platform.SHARE_WEBPAGE);
								//
								// Platform weixin =
								// ShareSDK.getPlatform(context,
								// Wechat.NAME);
								////
								// if(weixin.isClientValid()){
								// System.out.println("安装了微信");
								// }else {
								// System.out.println("没有安装了微信");
								// }
								// //
								// weixin.setPlatformActionListener(MainActivity.this);
								// weixin.share(wechat);
							} else {
								KappUtils.showToast(context, "暂未登录");
								Intent intent = new Intent(CookBookDetailActivity.this, LoginActivity.class);
								startActivity(intent);
								CookBookDetailActivity.this.finish();
							}
						} else if (flag == 2) {
							if (KappAppliction.getLiLogin() == 1) {
								Share_data_entity data = new Share_data_entity();
								data.setTargeURL("http://itpang.com/food/food/index.php/Home/Index/details.html?id="
										+ cookId + "&is_show=1");
								data.setImageURL(
										UrlInterface.URL_HOST+"attached/image/20160519/20160519161641_192.png");
								data.setText("美易来真给力,我们一起看看吧!");
								data.setTitle("分享美食,分享快乐!");

								share_Test share = new share_Test();
								share.setData(CookBookDetailActivity.this, data);

								share.share(share_Test.platform.weixinCircle);
								// ShareParams wechat2 = new ShareParams();
								// wechat2.setTitle("分享美食,分享快乐!");
								// wechat2.setText("美易来真给力,我们一起看看吧!");
								// wechat2.setImageUrl("http://121.41.86.29:8888/pms/attached/image/20160519/20160519161641_192.png");
								//
								// wechat2.setUrl("http://itpang.com/food/food/index.php/Home/Index/details.html?id="+cookId+"&is_show=1");
								// wechat2.setShareType(Platform.SHARE_WEBPAGE);
								//
								// Platform weixin2 =
								// ShareSDK.getPlatform(context,
								// WechatMoments.NAME);
								//
								// if(weixin2.isClientValid()){
								// System.out.println("安装了微信");
								// }else {
								// System.out.println("没有安装了微信");
								// }
								// //
								// weixin.setPlatformActionListener(MainActivity.this);
								// weixin2.share(wechat2);
							} else {
								KappUtils.showToast(context, "暂未登录");
								Intent intent = new Intent(CookBookDetailActivity.this, LoginActivity.class);
								startActivity(intent);
								CookBookDetailActivity.this.finish();
							}
						}
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				Log.i(TAG, "net uploadTempFile-->onError:");
				KappUtils.showToast(context, "分享失败！");
			}

			@Override
			public void onCancelled(CancelledException cex) {
			}

			@Override
			public void onFinished() {
			}
		});
	}

	/**
	 * 上传菜谱文件到后台
	 *
	 * @param //headpath
	 */
	private void checkPmsFile(String fileName) {
		Log.i(TAG, "checkPmsFile(fileName)-->" + fileName);
		fileName = fileName.replace(".pms", "");
		RequestParams entity = new RequestParams(UrlInterface.URL_PMSFILECHECK);
		MyLog.i(TAG, "路径：" + UrlInterface.URL_PMSFILECHECK);
		entity.addBodyParameter("userId", user.getId());
		entity.addBodyParameter("fileName", fileName);
		x.http().post(entity, new CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
				try {
					Log.i(TAG, "net checkPmsFile(fileName)-->onSuccess-->" + result);
					JSONObject jsonObject = new JSONObject(result);
					urlfilepath = jsonObject.getJSONObject("data").getString("count");
					String msg = jsonObject.getString("msg");
					if (!msg.equals("成功")) {
						KappUtils.showToast(context, msg);

					} else {
						if (urlfilepath.equals("2")) {
							BeSureDialog beSureDialog = new BeSureDialog(context, "是否确认覆盖?");
							beSureDialog.setOnSelected(new OnSelected() {

								@Override
								public void onSureSelected() {
									// TODO Auto-generated method stub

									if (stat == false) {
										// checkPmsFile(file.getName());
										uploadTempFile(filePath, file.getName());
									} else {
										file = new File(newFilePath);
										// checkPmsFile(file.getName());
										uploadTempFile(newFilePath, file.getName());
										// uploadHeadPortrait(newFilePath,
										// file.getName());
									}
								}

								@Override
								public void onNotSureSelected() {
									// TODO Auto-generated method stub

								}
							});
							beSureDialog.show();
						} else if (urlfilepath.equals("1")) {
							// uploadTempFile(filePath, file.getName());
							if (stat == false) {
								// checkPmsFile(file.getName());
								uploadTempFile(filePath, file.getName());
							} else {
								file = new File(newFilePath);
								// checkPmsFile(file.getName());
								uploadTempFile(newFilePath, file.getName());
								// uploadHeadPortrait(newFilePath,
								// file.getName());
							}
						}
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
			}

			@Override
			public void onCancelled(CancelledException cex) {
			}

			@Override
			public void onFinished() {
			}
		});
	}

	/**
	 * 上传菜谱文件到后台
	 *
	 * @param //headpath
	 */
	private void uploadHeadPortrait(String filepath, String fileName, String type) {

		fileName = fileName.replace(".pms", "");
		Log.i(TAG, "uploadHeadPortrait-->");
		Log.i(TAG, "uploadHeadPortrait-->filepath:" + filepath + "fileName:" + fileName + "type" + type);

		RequestParams entity = new RequestParams(UrlInterface.URL_PMSFILEUPLOADBYBYTE);
		MyLog.i(TAG, "路径：" + UrlInterface.URL_PMSFILEUPLOADBYBYTE);
		entity.setMultipart(true);
		entity.addBodyParameter("userId", user.getId());
		entity.addBodyParameter("fileName", fileName);
		entity.addBodyParameter("type", type);
		entity.addBodyParameter("file", new File(filepath), "multipart/form-data");
		x.http().post(entity, new CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
				try {
					Log.i(TAG, "uploadHeadPortrait-->onSuccess-->" + result);
					JSONObject jsonObject = new JSONObject(result);
					urlfilepath = jsonObject.getJSONObject("data").getString("path");
					String msg = jsonObject.getString("msg");
					String status = jsonObject.getString("status");
					if (status.equals("10001")) {
						// KappUtils.showToast(context, msg);
						// CloudPMSFileStatus2.setDirty();
						KappUtils.showToast(context, "上传文件成功！");
					} else if (status.equals("10007")) {
						BeSureDialog dlg = new BeSureDialog(CookBookDetailActivity.this, "当前用户所导入的菜谱名称重复,是否覆盖?");
						dlg.setOnSelected(new OnSelected() {

							@Override
							public void onSureSelected() {
								// TODO Auto-generated method stub
								if (stat == false) {
									Log.i(TAG, "stat:false,filePath-->" + filePath + ";fileName-->" + file.getName());
									uploadHeadPortrait(filePath, file.getName(), "2");
								} else {
									Log.i(TAG, "stat:true,filePath-->" + newFilePath + ";fileName-->" + file.getName());
									uploadHeadPortrait(newFilePath, file.getName(), "2");
								}
							}

							@Override
							public void onNotSureSelected() {
								// TODO Auto-generated method stub

							}
						});
						dlg.show();
					} else {
						KappUtils.showToast(context, msg);
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				KappUtils.showToast(context, "上传文件失败！");
			}

			@Override
			public void onCancelled(CancelledException cex) {
			}

			@Override
			public void onFinished() {
			}
		});
	}

	private void showSharp() {
		sharpPopupWindow = new PopupWindow(new View(context), LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT,
				true);
		final View view = getLayoutInflater().inflate(R.layout.layout_sharp_window, null);
		view.findViewById(R.id.btn_sharp).setOnClickListener(this);
		view.findViewById(R.id.btn_upload).setOnClickListener(this);
		view.findViewById(R.id.btn_cancel).setOnClickListener(this);
		sharpPopupWindow.setTouchable(true);
		sharpPopupWindow.setTouchInterceptor(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Log.i("mengdd", "onTouch : ");
				return false;
				// 这里如果返回true的话，touch事件将被拦截
				// 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
			}
		});
		sharpPopupWindow.setContentView(view);
		// 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
		// 我觉得这里是API的一个bug
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.alpha = 0.7f;
		getWindow().setAttributes(lp);
		// ColorDrawable dw = new ColorDrawable(0xb0000000);
		// sharpPopupWindow.setBackgroundDrawable(dw);
		// sharpPopupWindow.setBackgroundDrawable([);(getResources().getColor(R.color.transparent_gray_bg));
		// 设置好参数之后再show
		sharpPopupWindow.showAtLocation(findViewById(R.id.layout_all), Gravity.BOTTOM, 0, 0);
		sharpPopupWindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				if (isSharp) {
					isSharp = false;
				} else {
					WindowManager.LayoutParams lp = getWindow().getAttributes();
					lp.alpha = 1f;
					getWindow().setAttributes(lp);
				}
			}
		});
	}

	private void showSharp2() {
		sharpPopupWindow2 = new PopupWindow(new View(context), LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT,
				true);
		View view = getLayoutInflater().inflate(R.layout.layout_sharp_window2, null);
		view.findViewById(R.id.btn_wechat).setOnClickListener(this);
		view.findViewById(R.id.btn_wechat2).setOnClickListener(this);
		view.findViewById(R.id.btn_upload2).setOnClickListener(this);
		sharpPopupWindow2.setTouchable(true);
		sharpPopupWindow2.setTouchInterceptor(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Log.i("mengdd", "onTouch : ");
				return false;
				// 这里如果返回true的话，touch事件将被拦截
				// 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
			}
		});
		sharpPopupWindow2.setContentView(view);
		// 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
		// 我觉得这里是API的一个bug
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.alpha = 0.7f;
		getWindow().setAttributes(lp);
		ColorDrawable dw = new ColorDrawable(ContextCompat.getColor(context, R.color.main_bg_color));
		sharpPopupWindow2.setBackgroundDrawable(dw);
		// sharpPopupWindow.setBackgroundDrawable([);(getResources().getColor(R.color.transparent_gray_bg));
		// 设置好参数之后再show
		sharpPopupWindow2.showAtLocation(findViewById(R.id.layout_all), Gravity.BOTTOM, 0, 0);
		sharpPopupWindow2.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 1f;
				getWindow().setAttributes(lp);
			}
		});
	}

	/** 根据菜谱名获取相应菜谱的数据 */
	void getfileInfoByName1(String name) {
		// filePath = getIntent().getStringExtra("filePath");
		// filePath=name;
		filePath = FileUtil.PMSPATH + name;
		if (filePath != null) {
			file = new File(filePath);
		} else {
			KappUtils.showToast(context, "无文件");
			return;
		}
		MyLog.i(TAG, "--->" + file.getName());
		if (!file.getName().endsWith(".pms") && !file.getName().endsWith(".PMS")) {
			TipConfirmView.showConfirmDialog2(context, "不是PMS格式的文件，无法打开", new OnClickListener() {
				@Override
				public void onClick(View v) {
					TipConfirmView.dismiss();
					CookBookDetailActivity.this.finish();
				}
			});
			return;
		}
		if (DataUtil.isnotnull(newName)) {
			et_name.setText(newName);
		}
		if (DataUtil.isnotnull(newContent)) {
			et_describe.setText(newContent);
		}
		// editString = file.getName().substring(0,
		// file.getName().lastIndexOf("."));
		// if (editString.length() > 9) {
		// editString = editString.substring(0, 9);
		// et_name.setText(editString + "...");
		// } else {
		// et_name.setText(editString);
		// }
		// et_name.setText(file.getName().substring(0,
		// file.getName().lastIndexOf(".")));
		// editString =
		// file.getName().substring(0,file.getName().lastIndexOf("."));
		// if (editString.length() > 17) {
		// editString = editString.substring(0, 17);
		// et_name.setText(editString+"...");
		// }else {
		// et_name.setText(editString);
		// }
		/** 设置菜谱名 */
		et_name.setText(file.getName().substring(0, file.getName().lastIndexOf(".")));
		InputStream in;
		try {
			in = new FileInputStream(file);
			byte b[] = new byte[(int) file.length()]; // 创建合适文件大小的数组
			in.read(b); // 读取文件中的内容到b[]数组
			in.close();
			// EncodingUtils.getString("", "");
			PmsFile pmsFile = new PmsFile(b);

			/**设置菜谱主图 Lotus 2016-07-26 */
			if (pmsFile.bitmap == null) {
				iv_photo.setImageDrawable(getResources().getDrawable(R.drawable.icon_pms_file));
			}else{
				bitmap = pmsFile.bitmap;
				iv_photo.setImageBitmap(bitmap);
				ivState = false;
			}

			//			if (newLog == null) {
			//				// iv_photo.setBackgroundResource(R.drawable.icon_pms_file);
			//				// ivState = true;
			//				// backgroundResource = R.drawable.icon_pms_file;
			//				iv_photo.setImageDrawable(getResources().getDrawable(R.drawable.icon_pms_file));
			//
			//			} else {
			//				iv_photo.setImageBitmap(newLog);
			//				bitmap = pmsFile.bitmap;
			//				ivState = false;
			//			}

			// et_describe.setText(pmsFile.text);

			StringBuffer material = new StringBuffer();
			listTimeNode.clear();
			MyLog.d("pmsFile.listTimeNode != null : " + (listTimeNode != null));
			TimeNode node = new TimeNode();

			if (pmsFile.listTimeNode != null) {
				if (pmsFile.listTimeNode.size() == 0) {
					pmsFile.listTimeNode.add(node);
				}
				for (int i = 0; i < pmsFile.listTimeNode.size(); i++) {
					for (int j = 0; j < pmsFile.listTimeNode.get(i).FoodNames.length; j++) {
						if (!TextUtils.isEmpty(pmsFile.listTimeNode.get(i).FoodNames[j])) {
							material.append(pmsFile.listTimeNode.get(i).FoodNames[j]);
							if (i != pmsFile.listTimeNode.size() - 1
									&& j != pmsFile.listTimeNode.get(i).FoodNames.length - 1) {
								material.append(";");
							}
						}
					}
				}
				listTimeNode.addAll(pmsFile.listTimeNode);
				// listTimeNode.set(Config.position, timeNode);
			}
			// String[] str=material.toString().split(";");
			tv_material2.setText(material.toString());/** 设置菜谱全部食材 */
			cookBookStopAdapter.notifyDataSetChanged();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** 编辑保存后返回之后显示 */
	private void getFileInfo1() {
		filePath = getIntent().getStringExtra("filePath");
		if (filePath != null) {
			file = new File(filePath);
		} else {
			KappUtils.showToast(context, "无文件");
			return;
		}
		MyLog.i(TAG, "--->" + file.getName());
		if (!file.getName().endsWith(".pms") && !file.getName().endsWith(".PMS")) {
			TipConfirmView.showConfirmDialog2(context, "不是PMS格式的文件，无法打开", new OnClickListener() {
				@Override
				public void onClick(View v) {
					TipConfirmView.dismiss();
					CookBookDetailActivity.this.finish();
				}
			});
			return;
		}
		if (DataUtil.isnotnull(newName)) {
			et_name.setText(newName);
		}
		if (DataUtil.isnotnull(newContent)) {
			et_describe.setText(newContent);/** 设值菜谱描述 */
		}
		// editString = file.getName().substring(0,
		// file.getName().lastIndexOf("."));
		// if (editString.length() > 9) {
		// editString = editString.substring(0, 9);
		// et_name.setText(editString + "...");
		// } else {
		// et_name.setText(editString);
		// }
		// et_name.setText(file.getName().substring(0,
		// file.getName().lastIndexOf(".")));
		// editString =
		// file.getName().substring(0,file.getName().lastIndexOf("."));
		// if (editString.length() > 17) {
		// editString = editString.substring(0, 17);
		// et_name.setText(editString+"...");
		// }else {
		// et_name.setText(editString);
		// }
		et_name.setText(file.getName().substring(0, file.getName().lastIndexOf(".")));
		InputStream in;
		try {
			in = new FileInputStream(file);
			byte b[] = new byte[(int) file.length()]; // 创建合适文件大小的数组
			in.read(b); // 读取文件中的内容到b[]数组
			in.close();
			// EncodingUtils.getString("", "");
			pmsFile = new PmsFile(b);
			if (newLog == null) {
				// iv_photo.setBackgroundResource(R.drawable.icon_pms_file);
				// ivState = true;
				// backgroundResource = R.drawable.icon_pms_file;
				iv_photo.setImageDrawable(getResources().getDrawable(R.drawable.icon_pms_file));
			} else {
				iv_photo.setImageBitmap(newLog);
				bitmap = pmsFile.bitmap;
				ivState = false;
			}

			/** 设值菜谱全部食材 */
			// et_describe.setText(pmsFile.text);
			StringBuffer material = new StringBuffer();
			listTimeNode.clear();
			MyLog.d("pmsFile.listTimeNode != null : " + (listTimeNode != null));
			TimeNode node = new TimeNode();
			if (pmsFile.listTimeNode != null) {
				if (pmsFile.listTimeNode.size() == 0) {
					pmsFile.listTimeNode.add(node);
				}
				for (int i = 0; i < pmsFile.listTimeNode.size(); i++) {
					for (int j = 0; j < pmsFile.listTimeNode.get(i).FoodNames.length; j++) {
						if (!TextUtils.isEmpty(pmsFile.listTimeNode.get(i).FoodNames[j])) {
							material.append(pmsFile.listTimeNode.get(i).FoodNames[j]);
							if (i != pmsFile.listTimeNode.size() - 1 && j != pmsFile.listTimeNode.get(i).FoodNames.length - 1) {
								material.append(";");/** 在每个食材名称后面拼接分号 */
							}
						}
					}
				}
				listTimeNode.addAll(pmsFile.listTimeNode);
				listTimeNode.set(Config.position, timeNode);
			}
			// String[] str=material.toString().split(";");
			tv_material2.setText(material.toString());/** 设值菜谱全部食材 */
			cookBookStopAdapter.notifyDataSetChanged();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** 获取菜谱文件信息 */
	private void getFileInfo() {
		filePath = getIntent().getStringExtra("filePath");
		if (filePath != null) {
			file = new File(filePath);
		} else {
			KappUtils.showToast(context, "无文件");
			return;
		}
		MyLog.i(TAG, "--->" + file.getName());
		if (!file.getName().endsWith(".pms") && !file.getName().endsWith(".PMS")) {
			TipConfirmView.showConfirmDialog2(context, "不是PMS格式的文件，无法打开", new OnClickListener() {
				@Override
				public void onClick(View v) {
					TipConfirmView.dismiss();
					CookBookDetailActivity.this.finish();
				}
			});
			return;
		}
		// if (et_name.getText().length()<12) {
		// editString = et_name.getText().toString().substring(10,
		// et_name.getText().length());
		// }
		editString = file.getName().substring(0, file.getName().lastIndexOf("."));
		if (editString.length() > 9) {
			editString = editString.substring(0, 9);
			et_name.setText(editString + "...");

		} else {
			if (TextUtils.isEmpty(Config.Name)) {
				if (editString.length() > 9) {
					editString = editString.substring(0, 9);
					et_name.setText(editString + "...");
				} else {
					et_name.setText(editString);
				}
			} else {
				// et_name.setText(Config.Name);
				if (Config.Name.length() > 9) {
					editString = Config.Name.substring(0, 9);
					et_name.setText(editString + "...");
				} else {
					et_name.setText(Config.Name);
				}
			}
		}
		// et_name.setText(file.getName().substring(0,
		// file.getName().lastIndexOf(".")));
		InputStream in;
		try {
			in = new FileInputStream(file);
			byte b[] = new byte[(int) file.length()]; // 创建合适文件大小的数组
			in.read(b); // 读取文件中的内容到b[]数组
			in.close();
			// EncodingUtils.getString("", "");
			pmsFile = new PmsFile(b);
			if (pmsFile.bitmap == null) {
				// iv_photo.setBackgroundResource(R.drawable.icon_pms_file);
				// ivState = true;
				// backgroundResource = R.drawable.icon_pms_file;
				iv_photo.setImageDrawable(getResources().getDrawable(R.drawable.icon_pms_file));
			} else {
				iv_photo.setImageBitmap(pmsFile.bitmap);
				bitmap = pmsFile.bitmap;
				ivState = false;
			}

			et_describe.setText(pmsFile.text);
			StringBuffer material = new StringBuffer();
			listTimeNode.clear();
			MyLog.d("pmsFile.listTimeNode != null : " + (pmsFile.listTimeNode != null));
			TimeNode node = new TimeNode();
			if (pmsFile.listTimeNode != null) {
				if (pmsFile.listTimeNode.size() == 0) {
					pmsFile.listTimeNode.add(node);
				}
				for (int i = 0; i < pmsFile.listTimeNode.size(); i++) {
					for (int j = 0; j < pmsFile.listTimeNode.get(i).FoodNames.length; j++) {
						if (!TextUtils.isEmpty(pmsFile.listTimeNode.get(i).FoodNames[j])) {
							material.append(pmsFile.listTimeNode.get(i).FoodNames[j]);/** 拼接每个食材名称 */

							String foodName = pmsFile.listTimeNode.get(i).FoodNames[j];
							int foodWgt = (int) pmsFile.listTimeNode.get(i).FoodWgts[j];
							timeNodeFoodList.add(new TimeNodeFood(foodName, foodWgt));
							if (i != pmsFile.listTimeNode.size() - 1
									&& j != pmsFile.listTimeNode.get(i).FoodNames.length - 1) {
								material.append(";");
							}
						}
					}
				}
				listTimeNode.addAll(pmsFile.listTimeNode);
				//加自动生成菜谱名称
				/** 按重量排序食材排序 */
				for (int i = 0; i < timeNodeFoodList.size() - 1; i++) {
					for (int j = 1; j < timeNodeFoodList.size() - i; j++) {
						TimeNodeFood max = new TimeNodeFood();
						if (timeNodeFoodList.get(j - 1).getWeight() < timeNodeFoodList.get(j).getWeight()) {
							max = timeNodeFoodList.get(j - 1);
							timeNodeFoodList.set((j - 1), timeNodeFoodList.get(j));
							timeNodeFoodList.set(j, max);
						}
					}
				}
				if (timeNodeFoodList.size()<1){
					smartFileName=null;
				}
				if (timeNodeFoodList.size()==1){
					smartFileName="炒"+ timeNodeFoodList.get(0).getName();
				}
				if (timeNodeFoodList.size()>1){
					smartFileName=timeNodeFoodList.get(0).getName()+"炒"+ timeNodeFoodList.get(1).getName();
				}
			}
			// String[] str=material.toString().split(";");
			tv_material2.setText(material.toString());
			cookBookStopAdapter.notifyDataSetChanged();
			localPMSFileStatus.setDirty();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 编辑步骤节点
	 *
	 * @param timeNode 时间节点：listTimeNode.get(0);
	 * @param startTime 开始的时间
	 * @param endTime 结束的时间
	 * @param isEdt 是否为编辑状态
	 * @param position 当前的List位置
	 */
	public void edtStep(TimeNode timeNode, int startTime, int endTime, boolean isEdt, int position) {
		// update();
		intFlag = 1;
		Intent intent = new Intent(context, AddStepActivity.class);
		if (isEdt) {
			intent.putExtra("edt_describe", timeNode.Tips);
			intent.putExtra("timeNode", timeNode);
			intent.putExtra("startTime", startTime);
			intent.putExtra("endTime", endTime);
			intent.putExtra("isEdt", isEdt);
			intent.putExtra("position", position);
			Config.position = position;
			if (flagStart != 1) {
				intent.putExtra("filePath", filePath);
			} else {
				intent.putExtra("filePath", newFilePath);
			}
			newName = et_name.getText().toString();
			newContent = et_describe.getText().toString();
			iv_photo.setDrawingCacheEnabled(true);
			if (iv_photo.getDrawingCache() != null) {
				newLog = iv_photo.getDrawingCache();
			}
			iv_photo.setDrawingCacheEnabled(false);
			startActivityForResult(intent, EDT_STEP_REQUESTCODE);

			HHDLog.w("timeNode.Tips=" + timeNode.Tips + "，timeNode=" + timeNode + "，startTime=" + startTime + "，endTime=" + endTime + "，isEdt=" + isEdt + "，position=" + position);

			// CookBookDetailActivity.this.finish();
		} else {
			intent.putExtra("timeNode", timeNode);
			intent.putExtra("startTime", startTime);
			intent.putExtra("endTime", endTime);
			intent.putExtra("isEdt", isEdt);
			intent.putExtra("position", position);
			startActivityForResult(intent, ADD_STEP_REQUESTCODE);
		}
	}

	public void getData(int position, TimeNode timeNode) {
		// position = arg2.getIntExtra("position", 0);

		listTimeNode = new ArrayList<TimeNode>();
		listTimeNode.add(timeNode);
		// listTimeNode.set(position,timeNode);
		cookBookStopAdapter = new CookBookStopAdapter(this, listTimeNode);
		cookBookStopAdapter.notifyDataSetChanged();
		update();
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		if (arg1 == RESULT_OK) {
			int position;
			switch (arg0) {
				case EDT_STEP_REQUESTCODE:
					position = arg2.getIntExtra("position", 0);
					listTimeNode.set(position, (TimeNode) arg2.getSerializableExtra("timeNode"));
					cookBookStopAdapter.notifyDataSetChanged();
					this.pmsFile.getTimeNode().set(position, (TimeNode) arg2.getSerializableExtra("timeNode"));
					//updates();
					break;
				case ADD_STEP_REQUESTCODE:
					position = arg2.getIntExtra("position", 0);
					listTimeNode.add(position + 1, (TimeNode) arg2.getSerializableExtra("timeNode"));
					cookBookStopAdapter.notifyDataSetChanged();
					this.pmsFile.getTimeNode().add(position + 1, (TimeNode) arg2.getSerializableExtra("timeNode"));
					//updates();
					break;
				default:
					String path = cameraUtil.getImgPath(arg0, arg1, arg2);
					bitmap = BitmapFactory.decodeFile(path);
					iv_photo.setImageBitmap(bitmap);
					break;
			}
		}
		super.onActivityResult(arg0, arg1, arg2);
	}

	private void upDatePMSTimeNode(int position,TimeNode tn) {

	}

	/** 保存编辑后的菜谱//Lotus 2016-07-26 只使用旧名称 */
	public void update() {
		Log.i(TAG, "update()");
		// if (btn_etCookBook.getVisibility() == View.GONE) {
		// isLongChick(true);
		// } else {
		// isLongChick(false);
		// }
		/*gson = new Gson();
		String gStr = gson.toJson(this.pmsFile.getTimeNode());*/
		PmsFile pmsFile = new PmsFile();
		iv_photo.setDrawingCacheEnabled(true);
		if (iv_photo.getDrawingCache() != null)
			pmsFile.setJPG(iv_photo.getDrawingCache());
		iv_photo.setDrawingCacheEnabled(false);


		pmsFile.setText(et_describe.getText().toString());

		/*for (int i = 0; i < listTimeNode.size(); i++) {
			TimeNode node = listTimeNode.get(i);

			node.foodNames.add(node.FoodNames);
			System.out.println(node.FoodNames.length);
			pmsFile.setTipsTimeNode(i, node.Tips, node.foodIDs, node.FoodNames, node.FoodWgts, node.times);
		}*/
		pmsFile.setTimeNode(this.pmsFile.getTimeNode());
		pmsFile.setBufLubo(this.pmsFile.getBufLubo());
		/*Gson gson1 = new Gson();
		String gStr1 = gson1.toJson(pmsFile.getTimeNode());*/
		pmsFile.savaPMSData();
		String oldfilename = file.getName().substring(0, file.getName().lastIndexOf("."));
		String newfilename = "";

		if (smartFileName==null){
			smartFileName=oldfilename;
		} else {
			if (smartFileName.length()>9){
				smartFileName=smartFileName.substring(0,9);
			}
		}

		if(!et_name.getText().toString().trim().equals(oldfilename)){
			newfilename = et_name.getText().toString().trim();
			if (newfilename.length() > 9) {
				linshiString = newfilename.substring(0, 9);

				if (!DataUtil.isHaveChinese(linshiString)){/** 如果新的名称没有中文 */
					linshiString=smartFileName;
				}

				et_name.setText(linshiString + "...");
			}
		}else {
			newfilename = oldfilename;
			if (!DataUtil.isHaveChinese(newfilename)){/** 如果新的名称没有中文 */
				newfilename=smartFileName;
			}
		}
		/*if (intFlag == 1) {
			newfilename = oldfilename;
		} else {
			if (!TextUtils.isEmpty(et_name.getText())) {
				newfilename = et_name.getText().toString().trim();
				if (newfilename.length() > 9) {
					linshiString = newfilename.substring(0, 9);
					et_name.setText(linshiString + "...");
				}
			} else {
				KappUtils.showToast(context, "菜谱名不能为空！");
				return;
			}
		}*/
		Log.i(TAG, "oldfilename->" + oldfilename + ";newfilename->" + newfilename);

		Config.Name = newfilename;

		File newFilePath1 = null;
		// isLongChick(false);
		if (!oldfilename.equals(newfilename)) {
			newFilePath1 = new File(file.getParent(), newfilename + ".pms");
			Log.i(TAG, "newFilePath1-->" + newFilePath1);
			// newFilePath = file.getParent() + newfilename + ".pms";
			newFilePath = newFilePath1.getPath();
			// MyLog.i(TAG, newFilePath);
			Log.i(TAG, "newFilePath-->" + newFilePath);
			stat = file.renameTo(newFilePath1);
			Log.i(TAG, "stat-->" + String.valueOf(stat));
			flagStart = 1;
		} else {
			newFilePath1 = file;
		}
		// 例如:/storage/sdcard1/pms/西红柿炒蛋.pms
		// 建立输出字节流
		FileOutputStream fos;
		try {
			if (stat == false) {
				if (!file.exists()) {
					file.createNewFile();
				}
				fos = new FileOutputStream(file);
				// 用FileOutputStream 的write方法写入字节数组
				fos.write(pmsFile.PMS_Data);
				Log.i(TAG, "写入成功");
				System.out.println("写入成功");
				fos.close();
			} else {
				if (!newFilePath1.exists()) {
					newFilePath1.createNewFile();
				}
				fos = new FileOutputStream(newFilePath1);
				// 用FileOutputStream 的write方法写入字节数组
				fos.write(pmsFile.PMS_Data);
				Log.i(TAG, "写入成功");
				System.out.println("新文件写入成功");
				fos.close();
			}
			// if (newFilePath1 == null) {
			//
			// } else {
			//
			// }
			// 为了节省IO流的开销，需要关闭
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("写入失败1");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("写入失败2");
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent mIntent = new Intent();
			setResult(REQUEST_CODE, mIntent);
			// setResult(resultCode, data);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}






	//20161011以下为没有被使用到的方法（函数）
	/** 20161011更新菜谱所有数据
	 *
	 * Lotus 2016-07-26 只使用旧名称
	 */
	public void updates() {
		gson = new Gson();
		String gStr = gson.toJson(this.pmsFile.getTimeNode());
		PmsFile pmsFile = new PmsFile();

		iv_photo.setDrawingCacheEnabled(true);
		if (iv_photo.getDrawingCache() != null)
			pmsFile.setJPG(iv_photo.getDrawingCache());
		iv_photo.setDrawingCacheEnabled(false);

		if (!TextUtils.isEmpty(et_describe.getText())) {
			pmsFile.setText(et_describe.getText().toString());
		}

		for (int i = 0; i < listTimeNode.size(); i++) {
			TimeNode node = listTimeNode.get(i);
			node.foodNames.add(node.FoodNames);
			System.out.println(node.FoodNames.length);
			pmsFile.setTipsTimeNode(i, node.Tips, node.foodIDs, node.FoodNames, node.FoodWgts, node.times,node.bufTime);

			System.out.println("i="+i+"；FoodNames="+node.FoodNames+"FoodWgts="+node.FoodWgts);
		}
		Gson gson1 = new Gson();
		String gStr1 = gson1.toJson(pmsFile.getTimeNode());
		pmsFile.setBufLubo(this.pmsFile.getBufLubo());

		pmsFile.savaPMSData();

		// 建立输出字节流
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(file);
			// 用FileOutputStream 的write方法写入字节数组
			fos.write(pmsFile.PMS_Data);
			Log.i(TAG, "写入成功");
			fos.close();
			this.pmsFile = pmsFile;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void getfileInfoByName(String name) {
		// filePath = getIntent().getStringExtra("filePath");
		filePath = name;
		if (filePath != null) {
			file = new File(filePath);
		} else {
			KappUtils.showToast(context, "无文件");
			return;
		}
		MyLog.i(TAG, "--->" + file.getName());
		if (!file.getName().endsWith(".pms") && !file.getName().endsWith(".PMS")) {
			TipConfirmView.showConfirmDialog2(context, "不是PMS格式的文件，无法打开", new OnClickListener() {
				@Override
				public void onClick(View v) {
					TipConfirmView.dismiss();
					CookBookDetailActivity.this.finish();
				}
			});
			return;
		}
		// if (et_name.getText().length()<12) {
		// editString = et_name.getText().toString().substring(10,
		// et_name.getText().length());
		// }
		editString = file.getName().substring(0, file.getName().lastIndexOf("."));
		if (editString.length() > 9) {
			editString = editString.substring(0, 9);
			et_name.setText(editString + "...");

		} else {
			if (TextUtils.isEmpty(Config.Name)) {
				if (editString.length() > 9) {
					editString = editString.substring(0, 9);
					et_name.setText(editString + "...");
				} else {
					et_name.setText(editString);
				}
			} else {
				// et_name.setText(Config.Name);
				if (Config.Name.length() > 9) {
					editString = Config.Name.substring(0, 9);
					et_name.setText(editString + "...");
				} else {
					et_name.setText(Config.Name);
				}
			}
		}
		// et_name.setText(file.getName().substring(0,
		// file.getName().lastIndexOf(".")));
		InputStream in;
		try {
			in = new FileInputStream(file);
			byte b[] = new byte[(int) file.length()]; // 创建合适文件大小的数组
			in.read(b); // 读取文件中的内容到b[]数组
			in.close();
			// EncodingUtils.getString("", "");
			PmsFile pmsFile = new PmsFile(b);
			if (pmsFile.bitmap == null) {
				// iv_photo.setBackgroundResource(R.drawable.icon_pms_file);
				// ivState = true;
				// backgroundResource = R.drawable.icon_pms_file;
				iv_photo.setImageDrawable(getResources().getDrawable(R.drawable.icon_pms_file));
			} else {
				iv_photo.setImageBitmap(pmsFile.bitmap);
				bitmap = pmsFile.bitmap;
				ivState = false;
			}

			et_describe.setText(pmsFile.text);

			/** 20161011食材名称 */
			StringBuffer material = new StringBuffer();
			listTimeNode.clear();
			MyLog.d("pmsFile.listTimeNode != null : " + (pmsFile.listTimeNode != null));
			TimeNode node = new TimeNode();
			if (pmsFile.listTimeNode != null) {
				if (pmsFile.listTimeNode.size() == 0) {
					pmsFile.listTimeNode.add(node);
				}
				for (int i = 0; i < pmsFile.listTimeNode.size(); i++) {
					for (int j = 0; j < pmsFile.listTimeNode.get(i).FoodNames.length; j++) {
						if (!TextUtils.isEmpty(pmsFile.listTimeNode.get(i).FoodNames[j])) {
							material.append(pmsFile.listTimeNode.get(i).FoodNames[j]);//20161012拼接食材名称
							if (i != pmsFile.listTimeNode.size() - 1 && j != pmsFile.listTimeNode.get(i).FoodNames.length - 1) {
								material.append(";");
							}
						}
					}
				}
				listTimeNode.addAll(pmsFile.listTimeNode);
			}
			// String[] str=material.toString().split(";");
			tv_material2.setText(material.toString());
			cookBookStopAdapter.notifyDataSetChanged();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}
