package com.gzmelife.app.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.friends.Wechat.ShareParams;
import cn.sharesdk.wechat.moments.WechatMoments;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gzmelife.Status.CloudPMSFileStatus2;
import com.gzmelife.Status.localPMSFileStatus;
import com.gzmelife.app.ImageToDetailActivity;
import com.gzmelife.app.KappAppliction;
import com.gzmelife.app.R;
import com.gzmelife.app.UrlInterface;
import com.gzmelife.app.adapter.CookBookStepAdapter;
import com.gzmelife.app.adapter.LvCommentAdapter;
import com.gzmelife.app.bean.CookBookMenuBookBean;
import com.gzmelife.app.bean.CookBookMenuBookCommentsBean;
import com.gzmelife.app.bean.CookBookMenuBookStepsBean;
import com.gzmelife.app.bean.UserInfoBean;
import com.gzmelife.app.device.SocketTool;
import com.gzmelife.app.fragment.PrivateFragment;
import com.gzmelife.app.hhd.AddStepActivity;
import com.gzmelife.app.tools.CameraUtil;
import com.gzmelife.app.tools.FileUtils;
import com.gzmelife.app.tools.HttpDownloader;
import com.gzmelife.app.tools.ImgLoader;
import com.gzmelife.app.tools.KappUtils;
import com.gzmelife.app.tools.MyLog;
import com.gzmelife.app.tools.PmsFile;
import com.gzmelife.app.tools.ShowDialogUtil;
import com.gzmelife.app.hhd.TimeNode;
import com.gzmelife.app.views.ListViewForScrollView;
import com.gzmelife.app.views.TipConfirmView;

@SuppressLint("NewApi")
@ContentView(R.layout.activity_net_cook_book_detail)
public class NetCookBooksDetailActivity extends BaseActivity implements
		OnClickListener {
	@ViewInject(R.id.lv_step)
	ListViewForScrollView lv_step;
	@ViewInject(R.id.lv_comment)
	ListViewForScrollView lv_comment;
	String TAG = "NetCookBookDetailActivity";
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

	@ViewInject(R.id.iv_titleRight)
	ImageView iv_titleRight;

	@ViewInject(R.id.tv_describe)
	TextView tv_describe;

	@ViewInject(R.id.iv_titleLeft)
	ImageView iv_titleLeft;
	@ViewInject(R.id.ll3)
	LinearLayout ll3;

	@ViewInject(R.id.btn_enshrine)
	Button btn_enshrine;
	@ViewInject(R.id.btn_download)
	Button btn_download;
	@ViewInject(R.id.btn_comment)
	Button btn_comment;

	CookBookStepAdapter cookBookStopAdapter;
	String sdPath = null;
	private SocketTool socketTool;
	public static String filePath;
	private Dialog pDlg;
	List<TimeNode> listTimeNode;
	private Context context;
	private PopupWindow sharpPopupWindow, sharpPopupWindow2;
	private boolean isSharp;
	CameraUtil cameraUtil;
	public final int EDT_STEP_REQUESTCODE = 0X1000;
	public final int ADD_STEP_REQUESTCODE = 0X2000;
	LvCommentAdapter lvCommentAdapter;
	PrivateFragment fragment;
	final int REQUEST_CODE = 101;

	File file;

	private List<CookBookMenuBookBean> cookBookMenuBookBeanList = new ArrayList<CookBookMenuBookBean>();
	private List<CookBookMenuBookStepsBean> cookBookMenuBookStepsBeanList = new ArrayList<CookBookMenuBookStepsBean>();
	private List<CookBookMenuBookCommentsBean> cookBookMenuBookCommentsBeanList = new ArrayList<CookBookMenuBookCommentsBean>();

	UserInfoBean user;
	private String menuBookId;
	String collectionId;
	private String flagState;
	private int flag;
	private Bitmap bitmap = null;
	private String bitmapUrl;
	private String urlfilepath;

	private String bookName;
	int nunber;
	private int fileResult = 110;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		context = this;
		menuBookId = getIntent().getStringExtra("menuBookId");
		flagState = getIntent().getStringExtra("flagState");
		nunber = getIntent().getIntExtra("number", 0);
		flag = getIntent().getIntExtra("flag", 0);

		// Intent mIntent = new Intent();
		// mIntent.putExtra("nunber", nunber);
		// setResult(RESULT_OK,mIntent);
		if (KappAppliction.getLiLogin() == 1) {
			user = KappAppliction.myApplication.getUser();
			if (user != null) {
				showCookBookMenu();
			}

		} else {
			KappUtils.showToast(context, "暂未登录");
			Intent intent = new Intent(NetCookBooksDetailActivity.this,
					LoginActivity.class);
			startActivity(intent);
			NetCookBooksDetailActivity.this.finish();
			return;
		}

		// initView();
		tv_title.setText("菜谱详情");
		// getFileInfo();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();

	}

	@Override
	protected void onResume() {

		super.onResume();
	}

	@Override
	protected void onStop() {

		super.onStop();
	}

	private void downFile(String path) {
		String urlStr = UrlInterface.URL_HOST + path;
		// String path1= File.separator;
		// String fileName="2.mp3";
		OutputStream output = null;
		try {
			/*
			 * 通过URL取得HttpURLConnection 要网络连接成功，需在AndroidMainfest.xml中进行权限配置
			 * <uses-permission android:name="android.permission.INTERNET" />
			 */
			URL url = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			// 取得inputStream，并将流中的信息写入SDCard

			/*
			 * 写前准备 1.在AndroidMainfest.xml中进行权限配置 <uses-permission
			 * android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
			 * 取得写入SDCard的权限 2.取得SDCard的路径：
			 * Environment.getExternalStorageDirectory() 3.检查要保存的文件上是否已经存在
			 * 4.不存在，新建文件夹，新建文件 5.将input流中的信息写入SDCard 6.关闭流
			 */
			String SDCard = Environment.getExternalStorageDirectory()
					.getAbsolutePath();
			// String pathName=SDCard+"/"+path1+"/"+fileName;//文件存储路径
			String pathName = FileUtils.PMS_FILE_PATH;// 文件存储路径
			File file = new File(pathName);
			InputStream input = conn.getInputStream();
			if (file.exists()) {
				System.out.println("exits");
				FileUtils.getFileName(FileUtils.PMS_FILE_PATH);
				return;
			} else {
				// String dir=SDCard+"/"+path;
				String dir = FileUtils.PMS_FILE_PATH;
				new File(dir).mkdir();// 新建文件夹
				file.createNewFile();// 新建文件
				output = new FileOutputStream(file);
				// 读取大文件
				byte[] buffer = new byte[4 * 1024];
				while (input.read(buffer) != -1) {
					output.write(buffer);
				}
				output.flush();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				output.close();
				System.out.println("success");
			} catch (IOException e) {
				System.out.println("fail");
				e.printStackTrace();
			}
		}
	}

	private void getFileInfo() {
		filePath = getIntent().getStringExtra("filePath");
		if (filePath != null) {
			file = new File(filePath);
		} else {
			KappUtils.showToast(context, "无文件");
			return;
		}
		MyLog.i(TAG, "--->" + file.getName());
		if (!file.getName().endsWith(".pms")
				&& !file.getName().endsWith(".PMS")) {
			TipConfirmView.showConfirmDialog2(context, "不是PMS格式的文件，无法打开",
					new OnClickListener() {
						@Override
						public void onClick(View v) {
							TipConfirmView.dismiss();
							NetCookBooksDetailActivity.this.finish();
						}
					});
			return;
		}
		et_name.setText(file.getName().substring(0,
				file.getName().lastIndexOf(".")));
		InputStream in;
		try {
			in = new FileInputStream(file);
			byte b[] = new byte[(int) file.length()]; // 创建合适文件大小的数组
			in.read(b); // 读取文件中的内容到b[]数组
			in.close();
			PmsFile pmsFile = new PmsFile(b);
			iv_photo.setImageBitmap(pmsFile.bitmap);
			bitmap = pmsFile.bitmap;
			et_describe.setText(pmsFile.text);
			StringBuffer material = new StringBuffer();
			listTimeNode.clear();
			MyLog.d("pmsFile.listTimeNode != null : "
					+ (pmsFile.listTimeNode != null));
			if (pmsFile.listTimeNode != null) {
				for (int i = 0; i < pmsFile.listTimeNode.size(); i++) {
					for (int j = 0; j < pmsFile.listTimeNode.get(i).FoodNames.length; j++) {
						if (!TextUtils
								.isEmpty(pmsFile.listTimeNode.get(i).FoodNames[j])) {
							material.append(pmsFile.listTimeNode.get(i).FoodNames[j]);
							if (i != pmsFile.listTimeNode.size() - 1
									&& j != pmsFile.listTimeNode.get(i).FoodNames.length - 1) {
								material.append(";");
							}
						}
					}
				}
				listTimeNode.addAll(pmsFile.listTimeNode);
			}
			tv_material2.setText(material.toString());
			cookBookStopAdapter.notifyDataSetChanged();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

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
						break;
					}
				}

				@Override
				public void onFailure(int flag) {
					handler.sendEmptyMessage(1);
				}
			});
		}
		if (filePath == null || !filePath.contains("/")) {
			KappUtils.showToast(context, "文件错误");
			return;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				socketTool.initClientSocket(); // 根据不同的ip，建立不同的socket
				socketTool.uploadCookbookInfo();
			}
		}).start();
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	@SuppressLint("NewApi")
	private void initView() {

		if (collectionId.equals("0")) {
			Drawable drawable = this.getResources().getDrawable(
					R.drawable.noshoucang);
			btn_enshrine.setCompoundDrawablesRelativeWithIntrinsicBounds(null,
					drawable, null, null);
			btn_enshrine.setOnClickListener(this);
		} else {
			Drawable drawable = this.getResources().getDrawable(
					R.drawable.yesshoucang);
			btn_enshrine.setCompoundDrawablesRelativeWithIntrinsicBounds(null,
					drawable, null, null);
			btn_enshrine.setOnClickListener(this);
		}
		btn_download.setOnClickListener(this);
		btn_comment.setOnClickListener(this);
		iv_photo.setOnClickListener(this);
		iv_titleRight.setVisibility(View.VISIBLE);
		iv_titleRight.setImageResource(R.drawable.icn_more);
		iv_titleRight.setOnClickListener(this);
		iv_titleLeft.setOnClickListener(this);

		listTimeNode = new ArrayList<TimeNode>();
		// 添加
		for (int i = 0; i < 6; i++) {
			TimeNode timeNode = new TimeNode();
			timeNode.Tips = "============" + i;
			listTimeNode.add(timeNode);
		}

		tv_title.setText("菜谱详情");

	}

	public void edtStep(CookBookMenuBookStepsBean bean, int startTime,
			int endTime, boolean isEdt, int position) {
		Intent intent = new Intent(context, AddStepActivity.class);
		if (isEdt) {
			intent.putExtra("timeNode", bean);
			intent.putExtra("startTime", startTime);
			intent.putExtra("endTime", endTime);
			intent.putExtra("isEdt", isEdt);
			intent.putExtra("position", position);
			startActivityForResult(intent, EDT_STEP_REQUESTCODE);
		} else {
			intent.putExtra("isEdt", isEdt);
			intent.putExtra("position", position);
			startActivityForResult(intent, ADD_STEP_REQUESTCODE);
		}
	}

	private void showCookBookMenu() {
		// 美食菜谱详情
		showDlg();
		RequestParams params = new RequestParams(
				UrlInterface.URL_FINDMENUBOOKDETAILS);
		params.addBodyParameter("menuBookId", menuBookId);
		params.addBodyParameter("userId", user.getId());

		x.http().post(params, new CommonCallback<String>() {
			private String img_dailog;

			@Override
			public void onSuccess(String result) {
				closeDlg();
				Gson gson = new Gson();
				JSONObject obj;
				try {
					obj = new JSONObject(result.trim());
					System.out.println(">>>>>>>result=====" + result);
					// 菜谱
					CookBookMenuBookBean bean = gson.fromJson(obj
							.getJSONObject("data").getJSONObject("menuBook")
							.toString(), new TypeToken<CookBookMenuBookBean>() {
					}.getType());
					// CookBookBeanDataBean mallBeanGson = gson.fromJson(result,
					// new TypeToken<CookBookBeanDataBean>(){}.getType());

					// CookBookMenuBookBean bean=mallBeanGson.data.menu;
					if (bean == null) {
						KappUtils.showToast(context, obj.getString("msg"));
					}
					collectionId = bean.getCollectionId();
					initView();

					bookName = bean.getName();
					et_name.setText(bean.getName());
					if (bean.getLogoPath().equals("")) {
						iv_photo.setImageResource(R.drawable.icon_pms_file);
					} else {
						new ImgLoader(context).showPic(bean.getLogoPath(),
								iv_photo);
					}
					bitmapUrl = bean.getLogoPath();
					et_describe.setText(bean.getDescribes());
					tv_material2.setText(bean.getFoods());
					// 菜谱制作步骤
					cookBookMenuBookStepsBeanList = gson.fromJson(obj
							.getJSONObject("data")
							.getJSONArray("menubooksteps").toString(),
							new TypeToken<List<CookBookMenuBookStepsBean>>() {
							}.getType());

					cookBookStopAdapter = new CookBookStepAdapter(context,
							cookBookMenuBookStepsBeanList);
					// cookBookStopAdapter = new
					// CookBookStepAdapter(NetCookBookDetailActivity.this,
					// cookBookMenuBookStepsBeanList);
					lv_step.setAdapter(cookBookStopAdapter);
					// 菜谱评论
					cookBookMenuBookCommentsBeanList = gson
							.fromJson(
									obj.getJSONObject("data")
											.getJSONArray("menubookcomments")
											.toString(),
									new TypeToken<List<CookBookMenuBookCommentsBean>>() {
									}.getType());
					lvCommentAdapter = new LvCommentAdapter(context,
							cookBookMenuBookCommentsBeanList);
					lv_comment.setAdapter(lvCommentAdapter);

					// for (int i = 0; i < standardAdapter.getGroupCount(); i++)
					// {
					// rv_goodfood.expandGroup(i);
					// }

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				// TODO Auto-generated method stub
				closeDlg();
			}

			@Override
			public void onCancelled(CancelledException cex) {
				// TODO Auto-generated method stub
				closeDlg();
			}

			@Override
			public void onFinished() {
				// TODO Auto-generated method stub
				closeDlg();
			}

		});

	}

	private void delCollectionCookBookMenu(String id) {
		// 删除收藏的菜谱
		showDlg();
		RequestParams params = new RequestParams(
				UrlInterface.URL_DELETECOLLECTION);
		params.addBodyParameter("userCollectionId", id);
		params.addBodyParameter("userId", user.getId());
		x.http().post(params, new CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
				closeDlg();
				Gson gson = new Gson();
				JSONObject obj;
				try {
					List list = new ArrayList();
					obj = new JSONObject(result.trim());
					// list = gson.fromJson(obj
					// .getJSONObject("data").toString(),
					// new TypeToken<List>() {
					// }.getType());
					// if(list){
					//
					// }
					String msg = obj.getString("msg");
					if (!msg.equals("成功")) {
						KappUtils.showToast(context, msg);
						return;
					} else {
						KappUtils.showToast(NetCookBooksDetailActivity.this,
								"取消收藏菜谱成功");
						collectionId = 0 + "";
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				// TODO Auto-generated method stub
				closeDlg();
				KappUtils.showToast(context, "删除失败");
			}

			@Override
			public void onCancelled(CancelledException cex) {
				// TODO Auto-generated method stub
				closeDlg();
				KappUtils.showToast(context, "删除失败");
			}

			@Override
			public void onFinished() {
				// TODO Auto-generated method stub
				closeDlg();
			}

		});

	}

	private void saveCookBook() {
		// 收藏菜谱
		showDlg();
		RequestParams params = new RequestParams(
				UrlInterface.URL_SAVECOLLECTION);
		params.addBodyParameter("menuBookId", menuBookId);
		params.addBodyParameter("userId", user.getId());
		x.http().post(params, new CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
				closeDlg();
				Gson gson = new Gson();
				JSONObject obj;
				try {
					obj = new JSONObject(result.trim());
					System.out.println(">>result>>" + result);
					String msg = obj.getString("msg");
					if (msg.equals("成功")) {
						collectionId = 1 + "";
					} else {
						return;
					}
					System.out.println(">>>>result====" + result);
					// 菜谱
					// CookBookMenuBookBean bean = gson.fromJson(obj
					// .getJSONObject("data").getJSONObject("menuBook")
					// .toString(), new TypeToken<CookBookMenuBookBean>() {
					// }.getType());
					// CookBookBeanDataBean mallBeanGson = gson.fromJson(result,
					// new TypeToken<CookBookBeanDataBean>(){}.getType());

					System.out.println("collectionIdSave>>>>>>>>"
							+ collectionId);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				// TODO Auto-generated method stub
				closeDlg();
				KappUtils.showToast(NetCookBooksDetailActivity.this, "收藏菜谱失败");
			}

			@Override
			public void onCancelled(CancelledException cex) {
				// TODO Auto-generated method stub
				closeDlg();
				KappUtils.showToast(NetCookBooksDetailActivity.this, "收藏菜谱失败");
			}

			@Override
			public void onFinished() {
				// TODO Auto-generated method stub
				closeDlg();
			}

		});

	}

	private void downLoadMenuBookFile() {
		// 下载
		showDlg();
		RequestParams params = new RequestParams(
				UrlInterface.URL_DOWNLOADMENUBOOKFILE);
		params.addBodyParameter("id", menuBookId);
		params.addBodyParameter("uid", user.getId());

		x.http().post(params, new CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
				closeDlg();
				Gson gson = new Gson();
				JSONObject obj;
				try {
					obj = new JSONObject(result.trim());
					urlfilepath = obj.getJSONObject("data").getString("path");
					String msg = obj.getString("msg");
					if (!msg.equals("成功")) {
						KappUtils.showToast(context, msg);
					} else {
						new Thread(new Runnable() {
							@Override
							public void run() {
								Looper.prepare();
								try {
									HttpDownloader httpDownLoader = new HttpDownloader();
									int result1 = httpDownLoader
											.downfile(UrlInterface.URL_HOST
													+ urlfilepath, "pms/",
													bookName);
									// showDlg();
									if (result1 == 0) {
										fileResult = 0;
										localPMSFileStatus.setDirty();
										KappUtils.showToast(
												NetCookBooksDetailActivity.this,
												"下载成功！");
									} else if (result1 == 1) {
										fileResult = 1;
										KappUtils.showToast(
												NetCookBooksDetailActivity.this,
												"该菜谱已下载！");
									} else if (result1 == -1) {
										fileResult = -1;
										KappUtils.showToast(
												NetCookBooksDetailActivity.this,
												"下载失败！");
									}

								} catch (Exception e) {
									// TODO: handle exception
								}

								Looper.loop();
							}
						}).start();

						// if (fileResult == 0) {
						// Toast.makeText(NetCookBookDetailActivity.this,
						// "下载成功！", Toast.LENGTH_SHORT).show();
						// } else if (fileResult == 1) {
						// Toast.makeText(NetCookBookDetailActivity.this,
						// "该菜谱已下载！", Toast.LENGTH_SHORT).show();
						// } else if (fileResult == -1) {
						// Toast.makeText(NetCookBookDetailActivity.this,
						// "下载失败！", Toast.LENGTH_SHORT).show();
						// }
						// KappUtils.showToast(context, "获取文件路径成功！");
						// KappUtils.showToast(NetCookBookDetailActivity.this,
						// "下载成功！");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				// TODO Auto-generated method stub
				closeDlg();
				KappUtils.showToast(NetCookBooksDetailActivity.this, "收藏菜谱失败");
			}

			@Override
			public void onCancelled(CancelledException cex) {
				// TODO Auto-generated method stub
				closeDlg();
				KappUtils.showToast(NetCookBooksDetailActivity.this, "收藏菜谱失败");
			}

			@Override
			public void onFinished() {
				// TODO Auto-generated method stub
				closeDlg();
			}

		});

	}

	private void closePDlg() {
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
				pDlg = ShowDialogUtil.getShowDialog(
						NetCookBooksDetailActivity.this,
						R.layout.dialog_progressbar_2,
						FrameLayout.LayoutParams.MATCH_PARENT,
						FrameLayout.LayoutParams.MATCH_PARENT, 0, 0, false);
				ShowDialogUtil.setTitle("正在传送菜谱，请稍候");
				break;
			case 1:
				KappUtils.showToast(context, "上传文件到智能锅失败");
				break;
			case 2:
				KappUtils.showToast(context, "上传文件到智能锅成功");
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
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_comment:
			Intent intent = new Intent(context, CommentDetailActivity.class);
			intent.putExtra("menuBookId", menuBookId);
			startActivityForResult(intent, 10234);
			break;
		case R.id.btn_download:
			// 下载菜谱文件
			downLoadMenuBookFile();
			break;
		case R.id.iv_titleRight:
			showSharp();
			break;
		case R.id.btn_sharp:
			isSharp = true;
			showSharp2();
			sharpPopupWindow.dismiss();
			break;
		case R.id.btn_cancel:
			sharpPopupWindow.dismiss();
			break;
		case R.id.btn_upload:
			downLoadMenuBookFile();
			break;
		case R.id.btn_wechat:
			ShareParams wechat = new ShareParams();
			wechat.setTitle("分享美食,分享快乐!");
			wechat.setText("美易来真给力,我们一起看看吧!");
			wechat.setImageUrl(UrlInterface.URL_HOST+"attached/image/20160519/20160519161641_192.png");
		
			wechat.setUrl("http://itpang.com/food/food/index.php/Home/Index/details.html?id="+menuBookId+"&is_show=1");
			wechat.setShareType(Platform.SHARE_WEBPAGE);
		
			Platform weixin = ShareSDK.getPlatform(context,
					Wechat.NAME);
		
			if(weixin.isClientValid()){
				System.out.println("安装了微信");
			}else {
				System.out.println("没有安装了微信");
			}
//			weixin.setPlatformActionListener(MainActivity.this);
			weixin.share(wechat);
//			OnekeyShare oks = new OnekeyShare();
//			oks.disableSSOWhenAuthorize();
//			oks.setPlatform("Wechat");
//			oks.setTitle("分享美食,分享快乐!");
////			oks.setTitleUrl("http://itpang.com/food/food/index.php/Home/Index/details.html"+"?id="+menuBookId);
//			oks.setText("美易来真给力,我们一起看看吧!");
//			
//			oks.setImagePath("https://mmbiz.qlogo.cn/mmbiz/GtfaHt6jjjUzlS8CiatpDBdl66q9s86O1CZ4cLeXfncibUIcMxGrjbDFXUwgsIdtDHMavJUMy61ZJRqF0xa5icibyg/0?wx_fmt=png");
//			
////			oks.setUrl("http://itpang.com/food/food/index.php/Home/Index/details.html?id="+menuBookId);
//			oks.setUrl("www.baidu.com");
//			oks.setComment("暂无评论");
//			oks.setSite("美易来");
//			oks.setSiteUrl("http://121.41.86.29:8999/khj/jsp/share.jsp");
//			oks.setImageArray(null);
//			oks.show(context);;
//			share.setPlatform("Wechat");
//        	share.initShare("分享美食,分享快乐!", "美易来真给力,我们一起看看吧!", "https://mmbiz.qlogo.cn/mmbiz/GtfaHt6jjjUzlS8CiatpDBdl66q9s86O1CZ4cLeXfncibUIcMxGrjbDFXUwgsIdtDHMavJUMy61ZJRqF0xa5icibyg/0?wx_fmt=png", "http://itpang.com/food/food/index.php/Home/Index/details.html?id="+menuBookId,null);
//        	share.startShare();
			break;
		case R.id.btn_wechat2:
			ShareParams wechat2 = new ShareParams();
			wechat2.setTitle("分享美食,分享快乐!");
			wechat2.setText("美易来真给力,我们一起看看吧!");
			wechat2.setImageUrl(UrlInterface.URL_HOST+"attached/image/20160519/20160519161641_192.png");
		
			wechat2.setUrl("http://itpang.com/food/food/index.php/Home/Index/details.html?id="+menuBookId+"&is_show=1");
			wechat2.setShareType(Platform.SHARE_WEBPAGE);
		
			Platform weixin2 = ShareSDK.getPlatform(context,
					WechatMoments.NAME);
		
			if(weixin2.isClientValid()){
				System.out.println("安装了微信");
			}else {
				System.out.println("没有安装了微信");
			}
//			weixin.setPlatformActionListener(MainActivity.this);
			weixin2.share(wechat2);
//			OnekeyShare oks1 = new OnekeyShare();
//			oks1.disableSSOWhenAuthorize();
//			oks1.setPlatform("WechatMoments");
//			oks1.setTitle("分享美食,分享快乐!");
////			oks1.setTitleUrl("http://itpang.com/food/food/index.php/Home/Index/details.html"+"?id="+menuBookId);
//			oks1.setText("美易来真给力,我们一起看看吧!");
//			
//			oks1.setImagePath("https://mmbiz.qlogo.cn/mmbiz/GtfaHt6jjjUzlS8CiatpDBdl66q9s86O1CZ4cLeXfncibUIcMxGrjbDFXUwgsIdtDHMavJUMy61ZJRqF0xa5icibyg/0?wx_fmt=png");
//			oks1.setUrl("https://mmbiz.qlogo.cn/mmbiz/GtfaHt6jjjUzlS8CiatpDBdl66q9s86O1CZ4cLeXfncibUIcMxGrjbDFXUwgsIdtDHMavJUMy61ZJRqF0xa5icibyg/0?wx_fmt=png");
//			oks1.setComment("暂无评论");
//			oks1.setSite("美易来");
//			oks1.setSiteUrl("https://mmbiz.qlogo.cn/mmbiz/GtfaHt6jjjUzlS8CiatpDBdl66q9s86O1CZ4cLeXfncibUIcMxGrjbDFXUwgsIdtDHMavJUMy61ZJRqF0xa5icibyg/0?wx_fmt=png");
//			oks1.setImageArray(null);
//			oks1.show(context);
//			share.setPlatform("WechatMoments");
//        	share.initShare("分享美食,分享快乐!", "美易来真给力,我们一起看看吧!", "https://mmbiz.qlogo.cn/mmbiz/GtfaHt6jjjUzlS8CiatpDBdl66q9s86O1CZ4cLeXfncibUIcMxGrjbDFXUwgsIdtDHMavJUMy61ZJRqF0xa5icibyg/0?wx_fmt=png", "http://itpang.com/food/food/index.php/Home/Index/details.html?id="+menuBookId,null);
//        	share.startShare();
			break;
		case R.id.btn_upload2:
			break;
		case R.id.btn_sharp2:
			showSharp2();
			break;
		case R.id.iv_titleLeft:
			// Toast.makeText(getActivity(), "点击了", 0).show();

			// MainActivity main=new MainActivity();
			// if(flagState.equals("1")){
			// main.setView("1");
			// }else if(flagState.equals("2")){
			// main.setView("2");
			// }else if(flagState.equals("3")){
			// main.setView("3");
			// }

			// Intent intent1=new
			// Intent(NetCookBookDetailActivity.this,MainActivity.class);
			// intent1.putExtra("flagState", flagState);
			// startActivity(intent1);
			nunber++;
			Intent mIntent = new Intent();
			mIntent.putExtra("nunber", nunber);
			setResult(REQUEST_CODE, mIntent);
			NetCookBooksDetailActivity.this.finish();
			break;
		case R.id.btn_enshrine:
			// 收藏

			if (collectionId.equals("0")) {
				saveCookBook();
				KappUtils.showToast(NetCookBooksDetailActivity.this, "收藏菜谱成功");
				// btn_enshrine.setBackgroundResource(R.drawable.yesshoucang);
				Drawable drawable = this.getResources().getDrawable(
						R.drawable.yesshoucang);
				btn_enshrine.setCompoundDrawablesRelativeWithIntrinsicBounds(
						null, drawable, null, null);
				collectionId = 1 + "";
				CloudPMSFileStatus2.setDirty();
			} else {
				delCollectionCookBookMenu(collectionId);
				// btn_enshrine.setBackgroundResource(R.drawable.noshoucang);
				Drawable drawable = this.getResources().getDrawable(
						R.drawable.noshoucang);
				btn_enshrine.setCompoundDrawablesRelativeWithIntrinsicBounds(
						null, drawable, null, null);
				collectionId = 0 + "";
				CloudPMSFileStatus2.setDirty();
			}

			break;
		case R.id.iv_photo:
			Intent intents = new Intent(NetCookBooksDetailActivity.this,
					ImageToDetailActivity.class);
			iv_photo.getBackground();
			// Bundle bundle = new Bundle();
			// bundle.putParcelable("bitmap", bitmap);
			// intent.putExtras(bundle);
			// startActivity(intent);

			intents.putExtra("bitmapUrl", bitmapUrl);
			startActivity(intents);

			// LayoutInflater inflater = LayoutInflater
			// .from(NetCookBookDetailActivity.this);
			// View imgEntryView = inflater.inflate(R.layout.detail_dailog,
			// null); // 加载自定义的布局文件
			// final AlertDialog dialog = new AlertDialog.Builder(
			// NetCookBookDetailActivity.this).create();
			// ImageView img1 = (ImageView) imgEntryView
			// .findViewById(R.id.imageView);
			// // img1.setImageBitmap(bitmapUrl);
			// new ImgLoader(this).showPic(bitmapUrl, img1);
			// dialog.setView(imgEntryView); // 自定义dialog
			// dialog.show();
			// imgEntryView.setOnClickListener(new OnClickListener() {
			// public void onClick(View paramView) {
			// dialog.cancel();
			// }
			// });
			break;
		}
	}

	private void showSharp() {
		sharpPopupWindow = new PopupWindow(new View(context),
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);
		final View view = getLayoutInflater().inflate(
				R.layout.layout_sharp_window4, null);
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
		sharpPopupWindow.showAtLocation(findViewById(R.id.layout_all),
				Gravity.BOTTOM, 0, 0);
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
		sharpPopupWindow2 = new PopupWindow(new View(context),
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);
		View view = getLayoutInflater().inflate(R.layout.layout_sharp_window3,
				null);
		view.findViewById(R.id.btn_wechat).setOnClickListener(this);
		view.findViewById(R.id.btn_wechat2).setOnClickListener(this);
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
		ColorDrawable dw = new ColorDrawable(ContextCompat.getColor(context,
				R.color.main_bg_color));
		sharpPopupWindow2.setBackgroundDrawable(dw);
		// sharpPopupWindow.setBackgroundDrawable([);(getResources().getColor(R.color.transparent_gray_bg));
		// 设置好参数之后再show
		sharpPopupWindow2.showAtLocation(findViewById(R.id.layout_all),
				Gravity.BOTTOM, 0, 0);
		sharpPopupWindow2.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 1f;
				getWindow().setAttributes(lp);
			}
		});
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		if (arg1 == RESULT_OK) {
			int position;
			switch (arg0) {
			case EDT_STEP_REQUESTCODE:
				position = arg2.getIntExtra("position", 0);
				listTimeNode.set(position,
						(TimeNode) arg2.getSerializableExtra("timeNode"));
				cookBookStopAdapter.notifyDataSetChanged();
				break;
			case ADD_STEP_REQUESTCODE:
				position = arg2.getIntExtra("position", 0);
				listTimeNode.add(position + 1,
						(TimeNode) arg2.getSerializableExtra("timeNode"));
				cookBookStopAdapter.notifyDataSetChanged();
				break;
			case 10234:
				showCookBookMenu();
				break;
			default:
				String path = cameraUtil.getImgPath(arg0, arg1, arg2);
				iv_photo.setImageBitmap(BitmapFactory.decodeFile(path));
				break;
			}
		}
		super.onActivityResult(arg0, arg1, arg2);
	}

}
