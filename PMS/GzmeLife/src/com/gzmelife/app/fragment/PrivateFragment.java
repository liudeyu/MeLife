package com.gzmelife.app.fragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.http.RequestParams;

import com.gzmelife.app.views.SwipeMenu;
import com.gzmelife.app.views.SwipeMenuItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gzmelife.Status.CloudPMSFileStatus2;
import com.gzmelife.Status.localPMSFileStatus;
import com.gzmelife.app.KappAppliction;
import com.gzmelife.app.R;
import com.gzmelife.app.UrlInterface;
import com.gzmelife.app.activity.CookBookDetailActivity;
import com.gzmelife.app.activity.DeviceDetailActivity;
import com.gzmelife.app.activity.NetCookBookDetailActivity;
import com.gzmelife.app.adapter.FindCollectionMenuBookAdapter;
import com.gzmelife.app.adapter.LvFoodCookAdapter;
import com.gzmelife.app.bean.CookBookBean;
import com.gzmelife.app.bean.FindCollectionMenuBookBean;
import com.gzmelife.app.bean.UserInfoBean;
import com.gzmelife.app.dao.FoodMaterialDAO;
import com.gzmelife.app.db.DBUtils;
import com.gzmelife.app.device.Config;
import com.gzmelife.app.device.SocketTool;
import com.gzmelife.app.tools.DensityUtil;
import com.gzmelife.app.tools.FileUtil;
import com.gzmelife.app.tools.FileUtils;
import com.gzmelife.app.tools.KappUtils;
import com.gzmelife.app.tools.ShowDialogUtil;
import com.gzmelife.app.tools.pop_dlg;
import com.gzmelife.app.tools.pop_dlg.dialog;
import com.gzmelife.app.views.PullToRefreshSwipeMenuListView;
import com.gzmelife.app.views.PullToRefreshSwipeMenuListView.IXListViewListener;
import com.gzmelife.app.views.PullToRefreshSwipeMenuListView.OnMenuItemClickListener;
import com.gzmelife.app.views.RefreshTime;
import com.gzmelife.app.views.SwipeMenuCreator;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

/**
 * 私厨：管理手机中的菜谱（本地）管理收藏（云端）
 */
public class PrivateFragment extends Fragment
		implements OnMenuItemClickListener, OnItemClickListener, IXListViewListener {
	private ImageView iv_titleLeft;
	private Boolean isFirstStart = true;
	private Context context;
	private AlertDialog dlg;
	private Handler mHandler;
	LvFoodCookAdapter lvFoodCookAdapter;

	FindCollectionMenuBookAdapter adapter;

	RadioGroup rg_foodOption;
	PullToRefreshSwipeMenuListView lv_food;
	TextView tv_tag;
	File localPmsFile; // 本地文件夹
	List<CookBookBean> localCookBookBeans;// 本地数据源
	List<CookBookBean> netCookBookBeans;// 网络数据源

	private LocalBroadcastManager broadcastManager;
	private SocketTool socketTool;

	boolean isLocal;
	String TAG = "PrivateFragment";
	UserInfoBean user;
	private List<FindCollectionMenuBookBean> findCollectionMenuBookBeanList;

	private String state;
	public int flag = 0;
	public int flagSstate = 1;
	final int REQUEST_CODE = 101;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.i(TAG, "onCreateView()");
		// TODO Auto-generated method stub
		return LayoutInflater.from(getActivity()).inflate(R.layout.fragment_cookbook, null);
	}

	private Boolean isActivityHidden = false;

	@Override
	public void onPause() {
		Log.i(TAG, "onPause()");
		// TODO Auto-generated method stub
		isActivityHidden = true;
		super.onPause();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// isActivityHidden=false;
		updatePmsStatus();
		Log.i(TAG, "onResume()");

		// if(isFirstStart){
		// isFirstStart=false;
		// return;
		// }
		// if (flag == 1) {
		// if (localPMSFileStatus.queryDirty()) {
		// showDlg();
		// onRefresh();
		// }else{
		// if (localCookBookBeans.size() == 0) {
		// pop_dlg dlg = new pop_dlg(getActivity(), new dialog() {
		//
		// @Override
		// public void onclick(pop_dlg pop, View v) {
		// // TODO Auto-generated method stub
		// pop.dismiss();
		// }
		//
		// @Override
		// public View initLayout(pop_dlg pop, LayoutInflater flater) {
		// // TODO Auto-generated method stub
		// View v = flater.inflate(R.layout.dialog_tishi_local, null);
		// TextView ok = (TextView) v.findViewById(R.id.ok);
		// ok.setOnClickListener(pop);
		// return v;
		// }
		// });
		// }
		// lvFoodCookAdapter.setData(localCookBookBeans);
		// }
		// }
		// else if(flag==2){
		// if(CloudPMSFileStatus2.queryDirty()){
		// localCookBookBeans.clear();
		// lvFoodCookAdapter.notifyDataSetChanged();
		// if (KappAppliction.liLogin == 1) {
		// user = KappAppliction.myApplication.getUser();
		// showCollectionCookBookMenu();
		// } else {
		// KappUtils.showToast(context, "暂未登录");
		//
		// // Intent intent = new Intent(getActivity(),
		// // LoginActivity.class);
		// // startActivity(intent);
		// // return;
		// }
		//// flagSstate = 2;
		// }else{
		// pop_dlg dlg=new pop_dlg(getActivity(), new dialog() {
		//
		// @Override
		// public void onclick(pop_dlg pop, View v) {
		// // TODO Auto-generated method stub
		// pop.dismiss();
		// }
		//
		// @Override
		// public View initLayout(pop_dlg pop, LayoutInflater flater) {
		// // TODO Auto-generated method stub
		// View v = flater.inflate(R.layout.dialog_tishi1, null);
		// TextView ok = (TextView) v.findViewById(R.id.ok);
		// ok.setOnClickListener(pop);
		// return v;
		// }
		// });
		// }
		// }
	}

	/** 更新PMS状态：连接、故障... */
	private void updatePmsStatus() {
		Log.i(TAG, "updatePmsStatus()");
		if (TextUtils.isEmpty(Config.SERVER_HOST_NAME)) {
			iv_titleLeft.setImageResource(R.drawable.icon05);
		} else {
			if (Config.PMS_ERRORS.size() > 0) {
				iv_titleLeft.setImageResource(R.drawable.icon06);
			} else {
				iv_titleLeft.setImageResource(R.drawable.icon04);
			}
			if (Config.isConnext = true) {
				iv_titleLeft.setImageResource(R.drawable.icon04);
			} else {
				iv_titleLeft.setImageResource(R.drawable.icon06);
			}
		}
	}

	BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "onReceive()");
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (action.equals(KappUtils.ACTION_PMS_STATUS)) {
				updatePmsStatus();
			}
		}
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "onDestroy()");
		broadcastManager.unregisterReceiver(receiver);

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.i(TAG, "onActivityCreated()");
		super.onActivityCreated(savedInstanceState);
		// String state=
		context = this.getActivity();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(KappUtils.ACTION_PMS_STATUS);
		broadcastManager = LocalBroadcastManager.getInstance(getActivity());
		broadcastManager.registerReceiver(receiver, intentFilter);

		localCookBookBeans = new ArrayList<CookBookBean>();
		netCookBookBeans = new ArrayList<CookBookBean>();
		findCollectionMenuBookBeanList = new ArrayList<FindCollectionMenuBookBean>();

		if (this.getActivity().getIntent().getStringExtra("flag") != null) {
			state = this.getActivity().getIntent().getStringExtra("flag");
		} else {
			state = "0";
		}

		/**
		 * 添加用于测试和模拟的网络数据源
		 */
		// for (int i = 0; i < 4; i++) {
		// CookBookBean cookBookBean = new CookBookBean();
		// cookBookBean.setName("辣子鸡");
		// cookBookBean.setPath("");
		// cookBookBean.setTime("2016-10-25");
		// netCookBookBeans.add(cookBookBean);
		// }

		rg_foodOption = (RadioGroup) getView().findViewById(R.id.rg_foodOption);

		rg_foodOption.check(R.id.rb_cookbook);
		flag = 1;

		lv_food = (PullToRefreshSwipeMenuListView) getView().findViewById(R.id.lv_food);
		tv_tag = (TextView) getView().findViewById(R.id.tv_tag);
		lv_food.setPullRefreshEnable(true);
		lv_food.setPullLoadEnable(true);
		lv_food.setXListViewListener((PullToRefreshSwipeMenuListView.IXListViewListener) this);
		mHandler = new Handler();
		getFile();
		lvFoodCookAdapter = new LvFoodCookAdapter(getActivity());
		lv_food.setAdapter(lvFoodCookAdapter);
		for (int i = 0; i < localCookBookBeans.size(); i++) {
			CookBookBean cookBookBean = localCookBookBeans.get(i);
			String name = cookBookBean.getName();
			String name2;
			try {
				name2 = getFiles();
				// String[] s=name2.split(";");
				// for(int j=0;j<s.length;j++){
				// if(name.equals(s[j])){
				// localCookBookBeans.remove(i);
				// localCookBookBeans.add(j, cookBookBean);
				// }
				// }
				if (name.equals(name2)) {
					// if (!str.equals("")) {
					localCookBookBeans.remove(i);
					localCookBookBeans.add(0, cookBookBean);
					// }
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		if (localCookBookBeans.size() == 0) {
			pop_dlg dlg = new pop_dlg(getActivity(), new dialog() {

				@Override
				public void onclick(pop_dlg pop, View v) {
					// TODO Auto-generated method stub
					pop.dismiss();
				}

				@Override
				public View initLayout(pop_dlg pop, LayoutInflater flater) {
					// TODO Auto-generated method stub
					View v = flater.inflate(R.layout.dialog_tishi_local, null);
					TextView ok = (TextView) v.findViewById(R.id.ok);
					ok.setOnClickListener(pop);
					return v;
				}
			});
		}
		lvFoodCookAdapter.setData(localCookBookBeans);

		SwipeMenuCreator menuCreator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu arg0) {
				// TODO Auto-generated method stub
				// 菜单类别
				switch (arg0.getViewType()) {

				case 0:
					createMenu(arg0);
					break;

				case 1:
					createMenu1(arg0);
					break;
				}
			}

			/**
			 * 创建类型0菜单
			 */
			private void createMenu(SwipeMenu arg0) {

				SwipeMenuItem menuItem1 = new SwipeMenuItem(getActivity());
				menuItem1.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
				menuItem1.setWidth(dp2px(90));
				// 菜单标题
				menuItem1.setTitle("置顶");
				// 标题大小
				menuItem1.setTitleSize(18);
				// 标题的颜色
				menuItem1.setTitleColor(Color.WHITE);
				arg0.addMenuItem(menuItem1);

				SwipeMenuItem menuItem2 = new SwipeMenuItem(getActivity());
				menuItem2.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
				// menuItem2.setIcon(R.drawable.ic_action_discard);
				menuItem2.setWidth(dp2px(90));
				// 菜单标题
				menuItem2.setTitle("删除");
				// 标题大小
				menuItem2.setTitleSize(18);
				// 标题的颜色置顶
				menuItem2.setTitleColor(Color.WHITE);
				arg0.addMenuItem(menuItem2);
			}

			/**
			 * 创建类型1菜单
			 */
			private void createMenu1(SwipeMenu arg0) {

				SwipeMenuItem menuItem1 = new SwipeMenuItem(getActivity());
				menuItem1.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
				menuItem1.setWidth(dp2px(90));
				// 菜单标题
				menuItem1.setTitle("置顶");
				// 标题大小
				menuItem1.setTitleSize(18);
				// 标题的颜色
				menuItem1.setTitleColor(Color.WHITE);
				arg0.addMenuItem(menuItem1);

				SwipeMenuItem menuItem2 = new SwipeMenuItem(getActivity());
				menuItem2.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
				menuItem2.setWidth(90);
				// 菜单标题
				menuItem2.setTitle("删除");
				// 标题大小
				menuItem2.setTitleSize(18);
				// 标题的颜色置顶
				menuItem1.setTitleColor(Color.WHITE);

				arg0.addMenuItem(menuItem2);
			}

		};
		lv_food.setMenuCreator(menuCreator);
		lv_food.setOnItemClickListener(this);
		lv_food.setOnMenuItemClickListener(this);

		iv_titleLeft = (ImageView) getView().findViewById(R.id.iv_left);
		int normal = DensityUtil.dip2px(context, 12);
		iv_titleLeft.setPadding(normal, normal, normal, normal);
		iv_titleLeft.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!TextUtils.isEmpty(Config.SERVER_HOST_NAME)) { // 未连接，点击无效
					startActivity(new Intent(context, DeviceDetailActivity.class));
				}
			}
		});

		updatePmsStatus();

		if (flagSstate == 2) {
			flag = 2;
			rg_foodOption.check(R.id.rb_good);
			tv_tag.setText("管理收藏");
			localCookBookBeans.clear();
			lvFoodCookAdapter.notifyDataSetChanged();
			if (KappAppliction.getLiLogin() == 1) {
				user = KappAppliction.myApplication.getUser();
				showCollectionCookBookMenu();
			} else {
				KappUtils.showToast(context, "暂未登录");

				// Intent intent = new Intent(getActivity(),
				// LoginActivity.class);
				// startActivity(intent);
				// return;
			}
		}

		rg_foodOption.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rb_cookbook:
					Log.i(TAG, "点击本地:" + "rb_cookbook");
					flag = 1;
					tv_tag.setText("管理手机中菜谱");
					localCookBookBeans.clear();
					getFile();
					lvFoodCookAdapter = new LvFoodCookAdapter(getActivity());
					lv_food.setAdapter(lvFoodCookAdapter);

					for (int i = 0; i < localCookBookBeans.size(); i++) {
						CookBookBean cookBookBean = localCookBookBeans.get(i);
						String name = cookBookBean.getName();
						String name2;
						try {
							name2 = getFiles();
							// String[] s=name2.split(";");
							// for(int j=0;j<s.length;j++){
							// if(name.equals(s[j])){
							// localCookBookBeans.remove(i);
							// localCookBookBeans.add(j, cookBookBean);
							// }
							// }
							if (name.equals(name2)) {
								localCookBookBeans.remove(i);
								localCookBookBeans.add(0, cookBookBean);
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
					if (localCookBookBeans.size() == 0) {
						pop_dlg dlg = new pop_dlg(getActivity(), new dialog() {

							@Override
							public void onclick(pop_dlg pop, View v) {
								// TODO Auto-generated method stub
								pop.dismiss();
							}

							@Override
							public View initLayout(pop_dlg pop, LayoutInflater flater) {
								// TODO Auto-generated method stub
								View v = flater.inflate(R.layout.dialog_tishi_local, null);
								TextView ok = (TextView) v.findViewById(R.id.ok);
								ok.setOnClickListener(pop);
								return v;
							}
						});
					}
					lvFoodCookAdapter.setData(localCookBookBeans);

					flagSstate = 1;
					break;
				case R.id.rb_good:
					Log.i(TAG, "点击云端：" + "rb_good");
					flag = 2;
					tv_tag.setText("管理收藏");
					localCookBookBeans.clear();
					lvFoodCookAdapter.notifyDataSetChanged();
					if (KappAppliction.getLiLogin() == 1) {
						user = KappAppliction.myApplication.getUser();
						showCollectionCookBookMenu();
					} else {
						KappUtils.showToast(context, "暂未登录");

						// Intent intent = new Intent(getActivity(),
						// LoginActivity.class);
						// startActivity(intent);
						// return;
					}
					// if (localCookBookBeans.size() == 0) {
					// pop_dlg dlg = new pop_dlg(getActivity(), new dialog() {
					//
					// @Override
					// public void onclick(pop_dlg pop, View v) {
					// // TODO Auto-generated method stub
					// pop.dismiss();
					// }
					//
					// @Override
					// public View initLayout(pop_dlg pop, LayoutInflater
					// flater) {
					// // TODO Auto-generated method stub
					// View v = flater.inflate(R.layout.dialog_tishi_cloud,
					// null);
					// TextView ok = (TextView) v.findViewById(R.id.ok);
					// ok.setOnClickListener(pop);
					// return v;
					// }
					// });
					// }
					flagSstate = 2;
					break;
				default:
					break;
				}
			}
		});
		// lv_food.setOnItemClickListener(new OnItemClickListener() {
		// @Override
		// public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
		// long arg3) {
		// if(rg_foodOption.getCheckedRadioButtonId()==R.id.rb_cookbook){
		// Intent intent=new Intent(getActivity(),CookBookDetailActivity.class
		// );
		// intent.putExtra("filePath", localCookBookBeans.get(arg2).getPath() );
		// Log.i(TAG,"path:"+ localCookBookBeans.get(arg2).getPath());
		// startActivity(intent);
		// }else if(rg_foodOption.getCheckedRadioButtonId()==R.id.rb_good){
		// Intent intent=new
		// Intent(getActivity(),NetCookBookDetailActivity.class );
		// startActivity(intent);
		// }
		// }
		// });

	}

	File file;
	private ArrayList<String> mList;
	private CookBookBean str;
	FileUtil fileUtils = new FileUtil();

	public void getFile() {
		Log.i(TAG, "getFile()");
		localPmsFile = new File(FileUtils.PMS_FILE_PATH);
		Log.i(TAG, "localPmsFile-->" + FileUtils.PMS_FILE_PATH);
		if (localPmsFile.isDirectory()) {
			Log.i(TAG, "==============有===================>");
			for (int i = 0; i < localPmsFile.list().length; i++) {
				Log.i(TAG, "==============有=1==================>");
				file = new File(localPmsFile.list()[i]);
				Log.i(TAG, "getFile() fileLastModifyTime-->" + file.getPath() + "--" + file.getAbsolutePath() + "--"
						+ file.getParent());
				Log.i(TAG, "==============有=1==================>" + file.getName());
				if (file.getName().endsWith(".pms") || file.getName().endsWith(".PMS")) {
					Log.i(TAG, "==============有==12=================>");
					CookBookBean cookBookBean = new CookBookBean();
					Log.i(TAG, "new CookBookBean()");
					cookBookBean.setName(file.getName());
					cookBookBean.setPath(FileUtils.PMS_FILE_PATH + file.getPath());
					Log.i("PMS", "file_path2=" + cookBookBean.getPath());
					File abfile = new File(cookBookBean.getPath());
					String s = dateFormat(abfile.lastModified());
					cookBookBean.setTime(s);
					Log.i(TAG, "cookBookBean " + cookBookBean.getTime());
					cookBookBean.setId(FoodMaterialDAO.getCategoryId(file.getName()));
					localCookBookBeans.add(cookBookBean);

				}
			}
			/*// lvFoodCookAdapter.setData(localCookBookBeans);
			// 将sd卡中的pms文件时间按照从大到小的顺序排列
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			int length = localCookBookBeans.size();
			for (int i = 1; i < length; i++) {
				for (int focus = 1; focus < length; focus++) {
					try {
						Date d1 = sdf.parse(localCookBookBeans.get(focus).getTime());
						Date d2 = sdf.parse(localCookBookBeans.get(focus - 1).getTime());
						if ((d1.getTime() - d2.getTime()) > 0) {
							CookBookBean changebean = localCookBookBeans.get(focus);
							localCookBookBeans.remove(focus);
							localCookBookBeans.add(focus - 1, changebean);
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}*/
			// 将置顶的文件放到列表的最前面
			dbUtils = new DBUtils(getActivity());
			values = new ContentValues();
			Cursor cursor = dbUtils.queryAll("c_time");
			while (cursor.moveToNext()) {
				String c_name = cursor.getString(cursor.getColumnIndex("c_name"));
				for (int i = 0; i < localCookBookBeans.size(); i++) {
					if (c_name.equalsIgnoreCase(localCookBookBeans.get(i).getName())) {
						CookBookBean firstbean = localCookBookBeans.get(i);
						localCookBookBeans.remove(i);
						localCookBookBeans.add(0, firstbean);
					}
				}
			}
		} else {
			Log.i(TAG, "================<===================>");
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Log.i(TAG, "onItemClick()");
		if (position >= 0) {
			if (rg_foodOption.getCheckedRadioButtonId() == R.id.rb_cookbook) {
				Intent intent = new Intent(getActivity(), CookBookDetailActivity.class);
				intent.putExtra("filePath", localCookBookBeans.get(position - 1).getPath());
				intent.putExtra("cookId", localCookBookBeans.get(position - 1).getId() + "");
				System.out.println("cookId====" + localCookBookBeans.get(position - 1).getId() + "");
				Log.i(TAG, "path:" + localCookBookBeans.get(position - 1).getPath());
				startActivityForResult(intent, 10231);
			} else if (rg_foodOption.getCheckedRadioButtonId() == R.id.rb_good) {
				localCookBookBeans.clear();
				if (findCollectionMenuBookBeanList.size() != 0 || findCollectionMenuBookBeanList != null) {
					Intent intent = new Intent(getActivity(), NetCookBookDetailActivity.class);
					intent.putExtra("menuBookId", findCollectionMenuBookBeanList.get(position - 1).getId());
					startActivityForResult(intent, 10123);
				} else {
					Toast.makeText(getActivity(), "您还没有收藏,您可以去美食看看菜谱", Toast.LENGTH_SHORT).show();
				}

			}
		}

		// ApplicationInfo appInfo = (ApplicationInfo) lv_food
		// .getItemAtPosition(position);
		// open(appInfo);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == REQUEST_CODE) {
			switch (requestCode) {
			case 10123:
				System.out.println(",,,,,,");
				localCookBookBeans.clear();
				lvFoodCookAdapter.notifyDataSetChanged();
				if (KappAppliction.getLiLogin() == 1) {
					user = KappAppliction.myApplication.getUser();
					showCollectionCookBookMenu();
				} else {
					KappUtils.showToast(context, "暂未登录");
				}
				break;
			case 10231:
				localCookBookBeans.clear();
				getFile();
				lvFoodCookAdapter = new LvFoodCookAdapter(getActivity());
				lv_food.setAdapter(lvFoodCookAdapter);
				for (int i = 0; i < localCookBookBeans.size(); i++) {
					CookBookBean cookBookBean = localCookBookBeans.get(i);
					String name = cookBookBean.getName();
					String name2;
					try {
						name2 = getFiles();
						// String[] s=name2.split(";");
						// for(int j=0;j<s.length;j++){
						// if(name.equals(s[j])){
						// localCookBookBeans.remove(i);
						// localCookBookBeans.add(j, cookBookBean);
						// }
						// }
						if (name.equals(name2)) {
							localCookBookBeans.remove(i);

							localCookBookBeans.add(0, cookBookBean);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				lvFoodCookAdapter.setData(localCookBookBeans);
				flagSstate = 1;
				break;
			default:
				break;
			}
		}
	}

	private URL url = null;
	private DBUtils dbUtils;
	private ContentValues values;

	// 由于得到一个InputStream对象是所有文件处理前必须的操作，所以将这个操作封装成了一个方法
	public InputStream getInputStream(String urlStr) throws IOException {
		InputStream is = null;
		try {
			url = new URL(urlStr);
			HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
			is = urlConn.getInputStream();

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return is;
	}

	// 数据库保存修改
	public void saveFile(String writaName) {
		/*
		 * FileOutputStream out = null; try { out =
		 * context.openFileOutput("sichu", Context.MODE_PRIVATE);
		 * out.write(writaName.getBytes("UTF-8")); } catch (Exception e) {
		 * e.printStackTrace(); } finally { try { out.close(); } catch
		 * (Exception e) { e.printStackTrace(); } }
		 */

		dbUtils = new DBUtils(getActivity());
		values = new ContentValues();
		Cursor cursor = dbUtils.queryWhere("c_name = ?", new String[] { writaName });
		values.put("c_time", dateFormat(System.currentTimeMillis()));
		if (cursor.getCount() > 0) {
			dbUtils.update(values, "c_name = ?", new String[] { writaName });
		} else {
			values.put("c_name", writaName);
			dbUtils.insert(values);
		}
	}

	// 数据库删除
	public void deleteFile(String writaName) {
		dbUtils = new DBUtils(getActivity());
		values = new ContentValues();
		Cursor cursor = dbUtils.queryWhere("c_name = ?", new String[] { writaName });
		if (cursor.getCount() > 0) {
			dbUtils.delete("c_name = ?", new String[] { writaName });
		}
	}

	public String getFiles() throws Exception {
		Log.i(TAG, "getFiles()");
		FileInputStream in = null;
		ByteArrayOutputStream bout = null;
		ArrayList<String> readList = new ArrayList<String>();
		byte[] buf = new byte[1024];
		bout = new ByteArrayOutputStream();
		int length = 0;
		in = context.openFileInput("sichu"); // 获得输入流
		while ((length = in.read(buf)) != -1) {
			bout.write(buf, 0, length);
		}
		byte[] content = bout.toByteArray();
		System.out.println("content======" + new String(content, "UTF-8"));
		// filecontentEt.setText(new String(content,"UTF-8")); //设置文本框为读取的内容
		String readName = new String(content, "UTF-8");
		// filecontentEt.invalidate(); //刷新屏幕
		readList.add(readName);
		try {
			in.close();
			bout.close();
		} catch (Exception e) {
		}
		return readName;
	}

	@Override
	public void onMenuItemClick(int arg0, SwipeMenu arg1, int arg2) {
		// List<String> idList = new ArrayList<String>();
		// ApplicationInfo appInfo = (ApplicationInfo) lv_food
		// .getItemAtPosition(arg0);
		// idList.add(localCookBookBeans.get(arg0).toString());
		Log.i(TAG, "onMenuItemClick-->");
		switch (arg2) {
		// 菜单1：赞一下
		case 0:
			if (flag == 1) {
				Log.i(TAG, "onMenuItemClick-->flag=1");
				if (arg0 == 0) {
					KappUtils.showToast(context, "已经是置顶状态");
				} else {
					String str2 = "";
					str = localCookBookBeans.get(arg0);

					// mList = new ArrayList<String>();
					// for (int i = 0; i < localCookBookBeans.size(); i++) {
					// String name = localCookBookBeans.get(i).getName();
					// try {
					// saveFile(name);
					// mList.add(name);
					// if (mList.equals("")) {
					// }
					// } catch (Exception e) {
					// e.printStackTrace();
					// }
					// }
					// String string = mList.get(arg0);
					// mList.remove(arg0);
					// mList.add(0, string);
					// System.out.println("mList======" + mList);
					localCookBookBeans.remove(arg0);
					localCookBookBeans.add(0, str);
					for (int i = 0; i < localCookBookBeans.size(); i++) {
						str2 += localCookBookBeans.get(i).getName();
						if (i != localCookBookBeans.size()) {
							str2 += ";";
						}
					}
					saveFile(str.getName());
					lvFoodCookAdapter.notifyDataSetChanged();
				}

			} else if (flag == 2) {
				Log.i(TAG, "onMenuItemClick-->flag=2");
				if (arg0 == 0) {
					KappUtils.showToast(context, "已经是置顶状态");
				} else {
					FindCollectionMenuBookBean str = findCollectionMenuBookBeanList.get(arg0);
					findCollectionMenuBookBeanList.remove(arg0);
					findCollectionMenuBookBeanList.add(0, str);
					adapter.notifyDataSetChanged();
					topCookBookMenu(str.getId(), user.getId());
				}

			}
			break;

		// 菜单2：删除
		case 1:

			if (flag == 1) {
				File f = new File(localCookBookBeans.get(arg0).getPath());
				if (f.exists()) {
					// 如果路径存在
					if (f.isDirectory()) {
						// 如果是文件夹
						File[] childFiles = f.listFiles();
						// 获取文件夹下所有文件
						if (childFiles == null || childFiles.length == 0) {
							// 如果为空文件夹
							f.delete();// 删除文件夹
							return;
						}
						for (int i = 0; i < childFiles.length; i++) {
							// 删除文件夹下所有文件
							childFiles[i].delete();
							deleteFile(childFiles[i].getName());
						}
						f.delete();
						localCookBookBeans.remove(arg0);
						lvFoodCookAdapter.notifyDataSetChanged();
						KappUtils.showToast(context, "删除成功");
						// 删除文件夹
					} else {
						f.delete();
						deleteFile(f.getName());
						localCookBookBeans.remove(arg0);
						lvFoodCookAdapter.notifyDataSetChanged();
						KappUtils.showToast(context, "删除成功");
					}
				} else {
					KappUtils.showToast(context, "删除失败");
				}
			} else if (flag == 2) {
				delCollectionCookBookMenu(findCollectionMenuBookBeanList.get(arg0).getUserCollectionId());
				findCollectionMenuBookBeanList.remove(arg0);

				adapter.notifyDataSetChanged();
			}

			// System.out.println(appInfo.loadLabel(getPackageManager()));
			// localCookBookBeans.remove(appInfo);
			// FoodMaterialDAO.deleteLocalFoodMaterialLevelThree(idList);

			break;
		}
	}

	/**
	 * 删除文件夹所有内容
	 *
	 */
	public void deleteFile(File file) {

		if (file.exists()) { // 判断文件是否存在
			if (file.isFile()) { // 判断是否是文件
				file.delete(); // delete()方法 你应该知道 是删除的意思;
			} else if (file.isDirectory()) { // 否则如果它是一个目录
				File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
				for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
					this.deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
				}
			}
			file.delete();
		} else {
			//
		}
	}

	private void topCookBookMenu(String id, String uid) {
		// 置顶菜谱
		showDlg();
		RequestParams params = new RequestParams(UrlInterface.URL_TOPMENUBOOK);
		params.addBodyParameter("id", id);
		params.addBodyParameter("uid", uid);
		x.http().post(params, new CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
				closeDlg();
				Gson gson = new Gson();
				JSONObject obj;
				try {
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

					} else {
						KappUtils.showToast(context, "置顶成功！");
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

	private void delCollectionCookBookMenu(String id) {
		// 删除收藏的菜谱
		showDlg();
		RequestParams params = new RequestParams(UrlInterface.URL_DELETECOLLECTION);
		params.addBodyParameter("userCollectionId", id);

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

					KappUtils.showToast(context, "删除成功");

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

	private void showCollectionCookBookMenu() {
		Log.i(TAG, "showCollectionCookBookMenu");
		// 查询收藏的菜谱
		showDlg();
		RequestParams params = new RequestParams(UrlInterface.URL_FINDCOLLECTIONMENUBOOK);
		params.addBodyParameter("userId", user.getId());

		x.http().post(params, new CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
				closeDlg();
				Gson gson = new Gson();
				JSONObject obj;
				try {
					obj = new JSONObject(result.trim());
					findCollectionMenuBookBeanList.clear();
					findCollectionMenuBookBeanList = gson.fromJson(
							obj.getJSONObject("data").getJSONArray("menubooks").toString(),
							new TypeToken<List<FindCollectionMenuBookBean>>() {
							}.getType());
					localCookBookBeans.clear();

					if (findCollectionMenuBookBeanList.size() == 0) {
						pop_dlg dlg = new pop_dlg(getActivity(), new dialog() {

							@Override
							public void onclick(pop_dlg pop, View v) {
								// TODO Auto-generated method stub
								pop.dismiss();
							}

							@Override
							public View initLayout(pop_dlg pop, LayoutInflater flater) {
								// TODO Auto-generated method stub
								View v = flater.inflate(R.layout.dialog_tishi_cloud, null);
								TextView ok = (TextView) v.findViewById(R.id.ok);
								ok.setOnClickListener(pop);
								return v;
							}
						});
						// KappUtils.showToast(context, "您还没有收藏菜谱,您可以去美食看看菜谱");
					} else {
						adapter = new FindCollectionMenuBookAdapter(context);
						lv_food.setAdapter(adapter);
						adapter.setData(findCollectionMenuBookBeanList);
					}
					System.out.println(">>>>>========" + findCollectionMenuBookBeanList.size());
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

	public void showDlg() {
		if (null != this && null != dlg && !dlg.isShowing()) {
			dlg.show();
		} else if (null != this && null == dlg) {
			dlg = ShowDialogUtil.getShowDialog(getActivity(), R.layout.dialog_progressbar, 0, 0, false);
		}
	}

	public void closeDlg() {
		if (null != this && null != dlg && dlg.isShowing()) {
			dlg.dismiss();
		}
	}

	/**
	 * 将传入的dp值转换为px
	 */
	protected int dp2px(int dp) {

		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
	}

	private String dateFormat(long time) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		return format.format(new Date(time));
	}

	public void onRefresh() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				SimpleDateFormat df = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
				RefreshTime.setRefreshTime(context, df.format(new Date()));
				onLoad();
				// localCookBookBeans.clear();
				if (flagSstate == 1) {
					localCookBookBeans.clear();
					getFile();
					lvFoodCookAdapter = new LvFoodCookAdapter(getActivity());
					lv_food.setAdapter(lvFoodCookAdapter);
					for (int i = 0; i < localCookBookBeans.size(); i++) {
						CookBookBean cookBookBean = localCookBookBeans.get(i);
						String name = cookBookBean.getName();
						String name2;
						try {
							name2 = getFiles();
							// String[] s=name2.split(";");
							// for(int j=0;j<s.length;j++){
							// if(name.equals(s[j])){
							// localCookBookBeans.remove(i);
							// localCookBookBeans.add(j, cookBookBean);
							// }
							// }
							if (name.equals(name2)) {
								localCookBookBeans.remove(i);
								localCookBookBeans.add(0, cookBookBean);
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
					lvFoodCookAdapter.setData(localCookBookBeans);
					flagSstate = 1;
					lvFoodCookAdapter.notifyDataSetChanged();

					closeDlg();
				} else if (flagSstate == 2) {
					localCookBookBeans.clear();
					lvFoodCookAdapter.notifyDataSetChanged();
					if (KappAppliction.getLiLogin() == 1) {
						user = KappAppliction.myApplication.getUser();
						showCollectionCookBookMenu();
					} else {
						KappUtils.showToast(context, "暂未登录");
					}
					flagSstate = 2;
				}
			}
		}, 2000);
	}

	public void onLoadMore() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				onLoad();
			}
		}, 2000);
	}

	private void onLoad() {
		lv_food.setRefreshTime(RefreshTime.getRefreshTime(context));
		lv_food.stopRefresh();

		lv_food.stopLoadMore();

	}

	private pop_dlg mcurPop;
	private Long curTime = (long) 0;

	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		Log.i(TAG, "调用onHiddenChanged:" + String.valueOf(hidden));
		Date curData = new Date();
		Long curData1;
		curData1 = curData.getTime();
		Long yu;

		yu = curData1 - curTime;
		Log.i(TAG, "调用onHiddenChanged时间间隔：" + String.valueOf(yu));
		if (yu < 10) {
			curTime = curData1;
			return;
		}
		curTime = curData1;

		if (hidden) {

		} else {
			if (flag == 1) {
				if (localPMSFileStatus.queryDirty()) {
					showDlg();
					onRefresh();
				} else {
					if (localCookBookBeans.size() == 0) {
						mcurPop = new pop_dlg(getActivity(), new dialog() {

							@Override
							public void onclick(pop_dlg pop, View v) {
								// TODO Auto-generated method stub
								pop.dismiss();
							}

							@Override
							public View initLayout(pop_dlg pop, LayoutInflater flater) {
								// TODO Auto-generated method stub
								View v = flater.inflate(R.layout.dialog_tishi_local, null);
								TextView ok = (TextView) v.findViewById(R.id.ok);
								ok.setOnClickListener(pop);
								return v;
							}
						});
					}
					lvFoodCookAdapter.setData(localCookBookBeans);
				}
			} else if (flag == 2) {
				if (CloudPMSFileStatus2.queryDirty()) {
					localCookBookBeans.clear();
					lvFoodCookAdapter.notifyDataSetChanged();
					if (KappAppliction.getLiLogin() == 1) {
						user = KappAppliction.myApplication.getUser();
						showCollectionCookBookMenu();
					} else {
						KappUtils.showToast(context, "暂未登录");

						// Intent intent = new Intent(getActivity(),
						// LoginActivity.class);
						// startActivity(intent);
						// return;
					}
					// flagSstate = 2;
				} 
//				else {
//					mcurPop = new pop_dlg(getActivity(), new dialog() {
//
//						@Override
//						public void onclick(pop_dlg pop, View v) {
//							// TODO Auto-generated method stub
//							pop.dismiss();
//						}
//
//						@Override
//						public View initLayout(pop_dlg pop, LayoutInflater flater) {
//							// TODO Auto-generated method stub
//							View v = flater.inflate(R.layout.dialog_tishi1, null);
//							TextView ok = (TextView) v.findViewById(R.id.ok);
//							ok.setOnClickListener(pop);
//							return v;
//						}
//					});
//				}
			}
		}

		super.onHiddenChanged(hidden);
	}
}
