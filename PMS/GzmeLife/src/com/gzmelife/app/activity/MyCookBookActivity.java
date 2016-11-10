package com.gzmelife.app.activity;

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
import org.xutils.view.annotation.ContentView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gzmelife.app.R;
import com.gzmelife.app.UrlInterface;
import com.gzmelife.app.adapter.FindCollectionMenuBookAdapter;
import com.gzmelife.app.adapter.LvFoodCookAdapter;
import com.gzmelife.app.bean.CookBookBean;
import com.gzmelife.app.bean.FindCollectionMenuBookBean;
import com.gzmelife.app.bean.UserInfoBean;
import com.gzmelife.app.device.Config;
import com.gzmelife.app.tools.DensityUtil;
import com.gzmelife.app.tools.KappUtils;
import com.gzmelife.app.tools.ShowDialogUtil;

@ContentView(R.layout.my_cookbook)
public class MyCookBookActivity extends BaseActivity implements
OnMenuItemClickListener, OnItemClickListener{
	private ImageView iv_titleLeft;
	private Context context;
	private AlertDialog dlg;

	LvFoodCookAdapter lvFoodCookAdapter;

	FindCollectionMenuBookAdapter adapter;

	RadioGroup rg_foodOption;
	SwipeMenuListView lv_food;
	TextView tv_tag;
	List<CookBookBean> netCookBookBeans;// 网络数据源
	boolean isLocal;
	String TAG = "PrivateFragment";
	UserInfoBean user;
	private List<FindCollectionMenuBookBean> findCollectionMenuBookBeanList;

	private String state;
	public  int flag = 0;
	public  int flagSstate = 0;
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		updatePmsStatus();
	}
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		showCollectionCookBookMenu();
		netCookBookBeans = new ArrayList<CookBookBean>();
		findCollectionMenuBookBeanList = new ArrayList<FindCollectionMenuBookBean>();
		
		flag=1;
	
		
		lv_food = (SwipeMenuListView) findViewById(R.id.lv_food);
		tv_tag = (TextView) findViewById(R.id.tv_tag);


		SwipeMenuCreator menuCreator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu arg0) {

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

				SwipeMenuItem menuItem1 = new SwipeMenuItem(MyCookBookActivity.this);
				menuItem1.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,  
	                    0xCE)));
				menuItem1.setWidth(dp2px(90));
				// 菜单标题
				menuItem1.setTitle("置顶");
				// 标题大小
				menuItem1.setTitleSize(18);
				// 标题的颜色
				menuItem1.setTitleColor(Color.WHITE);
				arg0.addMenuItem(menuItem1);

				SwipeMenuItem menuItem2 = new SwipeMenuItem(MyCookBookActivity.this);
				menuItem2.setBackground(new ColorDrawable(Color.rgb(0xF9,  
	                    0x3F, 0x25)));
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

				SwipeMenuItem menuItem1 = new SwipeMenuItem(MyCookBookActivity.this);
				menuItem1.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,  
	                    0xCE)));
				menuItem1.setWidth(dp2px(90));
				// 菜单标题
				menuItem1.setTitle("置顶");
				// 标题大小
				menuItem1.setTitleSize(18);
				// 标题的颜色
				menuItem1.setTitleColor(Color.WHITE);
				arg0.addMenuItem(menuItem1);

				SwipeMenuItem menuItem2 = new SwipeMenuItem(MyCookBookActivity.this);
				menuItem2.setBackground(new ColorDrawable(Color.rgb(0xF9,  
	                    0x3F, 0x25)));
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

		iv_titleLeft = (ImageView) findViewById(R.id.iv_left);
		int normal = DensityUtil.dip2px(context, 12);
		iv_titleLeft.setPadding(normal, normal, normal, normal);
		iv_titleLeft.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!TextUtils.isEmpty(Config.SERVER_HOST_NAME)) { // 未连接，点击无效
					startActivity(new Intent(context,
							DeviceDetailActivity.class));
				}
			}
		});

		updatePmsStatus();
	}
	
	private void updatePmsStatus() {
		if (TextUtils.isEmpty(Config.SERVER_HOST_NAME)) {
			iv_titleLeft.setImageResource(R.drawable.icon05);
		} else {
			if (Config.PMS_ERRORS.size() > 0) {
				iv_titleLeft.setImageResource(R.drawable.icon06);
			} else {
				iv_titleLeft.setImageResource(R.drawable.icon04);
			}
			if(Config.isConnext=true){
				iv_titleLeft.setImageResource(R.drawable.icon04);
			}else{
				iv_titleLeft.setImageResource(R.drawable.icon06);
			}
		}
	}




	
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
			if (findCollectionMenuBookBeanList.size() != 0
					|| findCollectionMenuBookBeanList != null) {
				Intent intent = new Intent(MyCookBookActivity.this,
						NetCookBookDetailActivity.class);
				intent.putExtra("menuBookId", findCollectionMenuBookBeanList
						.get(position).getId());
				startActivity(intent);
			} else {
				Toast.makeText(MyCookBookActivity.this, "您还没有收藏,您可以去美食看看菜谱",
						Toast.LENGTH_SHORT).show();
			}

		// ApplicationInfo appInfo = (ApplicationInfo) lv_food
		// .getItemAtPosition(position);
		// open(appInfo);
	}

	@Override
	public void onMenuItemClick(int arg0, SwipeMenu arg1, int arg2) {
		// List<String> idList = new ArrayList<String>();
		// ApplicationInfo appInfo = (ApplicationInfo) lv_food
		// .getItemAtPosition(arg0);
		// idList.add(localCookBookBeans.get(arg0).toString());
		switch (arg2) {
		// 菜单1：赞一下
		case 0:
				
				if(arg0==0){
					KappUtils.showToast(context, "已经是置顶状态");
				}else{
					FindCollectionMenuBookBean str = findCollectionMenuBookBeanList.get(arg0);
					findCollectionMenuBookBeanList.remove(arg0);
					findCollectionMenuBookBeanList.add(0, str);
					adapter.notifyDataSetChanged();
					topCookBookMenu(str.getId(),user.getId());
				}
				
			break;

		// 菜单2：删除
		case 1:

				delCollectionCookBookMenu(findCollectionMenuBookBeanList.get(
						arg0).getUserCollectionId());
				findCollectionMenuBookBeanList.remove(arg0);
				
				adapter.notifyDataSetChanged();

			// System.out.println(appInfo.loadLabel(getPackageManager()));
			// localCookBookBeans.remove(appInfo);
			// FoodMaterialDAO.deleteLocalFoodMaterialLevelThree(idList);

			break;
		}
	}


	
	private void topCookBookMenu(String id,String uid) {
		// 置顶菜谱
		showDlg();
		RequestParams params = new RequestParams(
				UrlInterface.URL_TOPMENUBOOK);
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
					String msg=obj.getString("msg");
					if(!msg.equals("成功")){
							KappUtils.showToast(context, msg);
						
					}else{
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
		RequestParams params = new RequestParams(
				UrlInterface.URL_DELETECOLLECTION);
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
		// 查询收藏的菜谱
		showDlg();
		RequestParams params = new RequestParams(
				UrlInterface.URL_FINDCOLLECTIONMENUBOOK);
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
					findCollectionMenuBookBeanList = gson.fromJson(obj
							.getJSONObject("data").getJSONArray("menubooks")
							.toString(),
							new TypeToken<List<FindCollectionMenuBookBean>>() {
							}.getType());

					if (findCollectionMenuBookBeanList.size() == 0) {

						KappUtils.showToast(context, "您还没有收藏菜谱,您可以去美食看看菜谱");
					} else {
						adapter = new FindCollectionMenuBookAdapter(context);
						lv_food.setAdapter(adapter);
						adapter.setData(findCollectionMenuBookBeanList);
					}
					System.out.println(">>>>>========"+findCollectionMenuBookBeanList.size());
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
			dlg = ShowDialogUtil.getShowDialog(MyCookBookActivity.this,
					R.layout.dialog_progressbar, 0, 0, false);
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

		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}

	private String dateFormat(long time) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
				Locale.getDefault());
		return format.format(new Date(time));
	}

}
