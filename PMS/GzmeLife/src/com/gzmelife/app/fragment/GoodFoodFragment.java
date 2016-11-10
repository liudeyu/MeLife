package com.gzmelife.app.fragment;

import it.carlom.stikkyheader.core.StikkyHeaderBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.http.RequestParams;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gzmelife.app.R;
import com.gzmelife.app.UrlInterface;
import com.gzmelife.app.activity.AllFoodActivity;
import com.gzmelife.app.activity.CollectionActivity;
import com.gzmelife.app.activity.CookBookStandardFoodMaterialDetailsActivity;
import com.gzmelife.app.activity.DeviceDetailActivity;
import com.gzmelife.app.activity.GoodFoodSearchActivity;
import com.gzmelife.app.activity.NetCookBookDetailActivity;
import com.gzmelife.app.adapter.CookFoodBeanAdapter;
import com.gzmelife.app.adapter.GVClassAdapter;
import com.gzmelife.app.adapter.LvFoodClassAdapter;
import com.gzmelife.app.adapter.StandardFoodMaterialAdapter;
import com.gzmelife.app.bean.CategoryFirstBean;
import com.gzmelife.app.bean.CoonFoodMenuBean;
import com.gzmelife.app.bean.FindCollectionMenuBookBean;
import com.gzmelife.app.bean.FindSecondCategoryBean;
import com.gzmelife.app.bean.GoodFoodFinAdBean;
import com.gzmelife.app.device.Config;
import com.gzmelife.app.tools.DensityUtil;
import com.gzmelife.app.tools.IOnFocusListenable;
import com.gzmelife.app.tools.KappUtils;
import com.gzmelife.app.tools.MyLog;
import com.gzmelife.app.tools.MyScrollView;
import com.gzmelife.app.tools.MyScrollView.OnScrollListener;
import com.gzmelife.app.tools.ShowDialogUtil;
import com.gzmelife.app.views.GridViewForScrollView;
import com.gzmelife.app.views.MyListView;
import com.gzmelife.app.views.SlideShowView;
import com.gzmelife.app.views.bannerview.BannerView;

public class GoodFoodFragment extends Fragment implements OnScrollListener,
		IOnFocusListenable, SwipeRefreshLayout.OnRefreshListener,
		com.gzmelife.app.views.bannerview.BannerView.OnItemClickListener {
	
	CookFoodBeanAdapter cookFoodBeanAdapter;
	GVClassAdapter gvClassAdapter;
	GridViewForScrollView gv_foodclass;
	// RecyclerView rv_goodfood;
	MyListView rv_goodfood;
	FrameLayout fl_cookbook;
	ExpandableListView lv_foodclass;
	RadioGroup rg_foodOption;
	LvFoodClassAdapter lvFoodClassAdapter;
	String TAG = "GoodFoodFragment";
	Button btn_allfood;
	Button btn_search;
	Button btn_localcookbook;
	private MyScrollView myScrollView;
	LinearLayout search01, search02;
	RelativeLayout rlayout;

	SlideShowView slideshowView;

	BannerView bannerView;

	RadioGroup rb_group;
	// 最新
	RadioButton rb_new;
	// 最热
	RadioButton rb_hot;
	// 推荐
	RadioButton rb_recommend;

	private AlertDialog dlg;
	private int searchLayoutTop;
	private StandardFoodMaterialAdapter standardAdapter;
	private List<CategoryFirstBean> categoryFirstBeanList;

	private Context context;

	public List<GoodFoodFinAdBean> goodFoodFinAdBeanList;

	private List<CoonFoodMenuBean> categoryCoonFoodMenuBeanList;

	private List<FindCollectionMenuBookBean> findCollectionMenuBookBeanList;

	private List<FindSecondCategoryBean> findSecondCategoryBeanList;

	private ImageView iv_titleLeft;

	private int flagState;
	private int height;
	public int flag1;
	Bundle bundle1;
	boolean hasFocus;
	private List<Bitmap> bitMap;

	private static final int REFRESH_COMPLETE = 0X110;
	private SwipeRefreshLayout mSwipeLayout;
	private SwipeRefreshLayout mSwipeLayout_shicai;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case REFRESH_COMPLETE:
				// mDatas.addAll(Arrays.asList("Lucene", "Canvas", "Bitmap"));
				// mAdapter.notifyDataSetChanged();
				updatePmsStatus();
				findAd();
				findSecondCookBook();
				showStandardLib();
				RadioButton radioButton = (RadioButton) getView().findViewById(
						rb_group.getCheckedRadioButtonId());
				int id = rb_group.getCheckedRadioButtonId();
				if (id == 2131362078) {
					rb_new.setChecked(true);
					showCookBook(1);
				} else if (id == 2131362070) {
					rb_hot.setChecked(true);
					showCookBook(2);
				} else if (id == 2131362071) {
					rb_recommend.setChecked(true);
					showCookBook(3);
				}
				mSwipeLayout.setRefreshing(false);
				break;

			}
		};
	};
	private int height2;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i(TAG, "onCreateView-->");
		return LayoutInflater.from(getActivity()).inflate(
				R.layout.fragment_goodfood, null);

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		// showAd();
		Log.i(TAG, "onActivityCreated-->");
		initView();
		initScroll();
		try {
			registerBoradcastReceiver();
		} catch (Exception e) {
			// TODO: handle exception
		}
		// IntentFilter intentFilter = new IntentFilter();
		// intentFilter.addAction(KappUtils.ACTION_PMS_STATUS);
		// broadcastManager = LocalBroadcastManager.getInstance(getActivity());
		// broadcastManager.registerReceiver(receiver, intentFilter);

	}

	public void registerBoradcastReceiver() {
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction("aaa");
		// 注册广播
		try {
			getActivity().registerReceiver(receiver, myIntentFilter);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(KappUtils.ACTION_PMS_STATUS)) {
				updatePmsStatus();
			}
		}
	};

	private void initView() {
		Log.i(TAG, "initView-->");
		// TODO Auto-generated method stub
		context = this.getActivity();
		rb_group = (RadioGroup) getView().findViewById(R.id.rb_group);
		// slideshowView = new SlideShowView(context);
		// slideshowView = (SlideShowView) getView().findViewById(
		// R.id.slideshowView);
		bannerView = (BannerView) getView().findViewById(R.id.slideshowView);
		bannerView.setOnItemClickListener(this);
		mSwipeLayout = (SwipeRefreshLayout) getView().findViewById(
				R.id.id_swipe_ly);
		mSwipeLayout.setOnRefreshListener(this);
		mSwipeLayout.setColorScheme(android.R.color.holo_green_dark,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
		rb_new = (RadioButton) getView().findViewById(R.id.rb_new);
		rb_hot = (RadioButton) getView().findViewById(R.id.rb_hot);
		rb_recommend = (RadioButton) getView().findViewById(R.id.rb_recommend);
		btn_localcookbook = (Button) getView().findViewById(
				R.id.btn_localcookbook);
		iv_titleLeft = (ImageView) getView().findViewById(R.id.iv_left);
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
		// rv_goodfood = (RecyclerView)
		// getView().findViewById(R.id.rv_goodfood);
		rv_goodfood = (MyListView) getView().findViewById(R.id.rv_goodfood);
		rv_goodfood.setDivider(null);
		gv_foodclass = (GridViewForScrollView) getView().findViewById(
				R.id.gv_foodclass);
		fl_cookbook = (FrameLayout) getView().findViewById(R.id.fl_cookbook);
		lv_foodclass = (ExpandableListView) getView().findViewById(
				R.id.lv_foodclass);
		// mSwipeLayout_shicai = (SwipeRefreshLayout)
		// getView().findViewById(R.id.id_swipe_ly_shicai);
		// mSwipeLayout_shicai.setOnRefreshListener(this);
		// mSwipeLayout_shicai.setColorScheme(android.R.color.holo_green_dark,
		// android.R.color.holo_green_light,
		// android.R.color.holo_orange_light, android.R.color.holo_red_light);
		rg_foodOption = (RadioGroup) getView().findViewById(R.id.rg_foodOption);
		rg_foodOption.check(R.id.rb_cookbook);
		btn_allfood = (Button) getView().findViewById(R.id.btn_allfood);
		btn_search = (Button) getView().findViewById(R.id.btn_search);

		findAd();
		// cookFoodAdapter = new CookFoodAdapter(getActivity());
		findSecondCookBook();
		GridLayoutManager mgr = new GridLayoutManager(getActivity(), 2);
		// rv_goodfood.setHasFixedSize(true);
		// rv_goodfood.setLayoutManager(mgr);
		StikkyHeaderBuilder.stickTo(rv_goodfood)
				.setHeader(R.id.ll_head, (ViewGroup) getView())
				.minHeightHeader(DensityUtil.dip2px(getActivity(), 50)).build();
		// rv_goodfood.setAdapter(cookFoodAdapter);
		showStandardLib();
		lv_foodclass.setItemsCanFocus(true);
		lv_foodclass.setGroupIndicator(null);
		rb_new.setChecked(true);
		showCookBook(1);

		if (flag1 == 1) {
			rb_new.setChecked(true);
			showCookBook(1);
		} else if (flag1 == 2) {
			rb_hot.setChecked(true);
			showCookBook(2);
		} else if (flag1 == 3) {
			rb_recommend.setChecked(true);
			showCookBook(3);

		}
		rb_group.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				switch (checkedId) {
				case R.id.rb_new:
					flag1 = 1;
					showCookBook(1);
					break;
				case R.id.rb_hot:
					flag1 = 2;
					showCookBook(2);
					break;
				case R.id.rb_recommend:
					flag1 = 3;
					showCookBook(3);
					break;
				default:
					flag1 = 0;
					break;
				}
			}
		});
		// lv_foodclass.setAdapter(lvFoodClassAdapter);
		rg_foodOption.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rb_cookbook:
					Log.i(TAG, "点击rb_cookbook");
					
					myScrollView.setVisibility(View.VISIBLE);
					fl_cookbook.setVisibility(View.VISIBLE);
					lv_foodclass.setVisibility(View.GONE);
					break;
				case R.id.rb_good:
					Log.i(TAG, "点击rb_good");
					myScrollView.setVisibility(View.GONE);
					lv_foodclass.setVisibility(View.VISIBLE);
					fl_cookbook.setVisibility(View.GONE);
					break;
				default:
					break;
				}
			}
		});

		btn_allfood.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), AllFoodActivity.class);
				startActivity(intent);
			}
		});
		btn_search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),
						GoodFoodSearchActivity.class);
				startActivity(intent);
			}
		});

		btn_localcookbook.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i(TAG, "onClick-->");
				// TODO Auto-generated method stub
				// Toast.makeText(getActivity(), "点击了", 0).show();
				// MainActivity activity = (MainActivity) getActivity();
				// activity.localFlag = 1;
				// activity.setVis(1);
				// activity.localFlag = 0;
				Intent intent = new Intent(getActivity(),
						CollectionActivity.class);
				intent.putExtra("FLAG", "flag");
				Log.i(TAG, "btn_localcookbook onClick->跳转CollectionActivity：flag：");
				startActivity(intent);
				// android.app.FragmentManager fm =
				// activity.getFragmentManager();
				// android.app.FragmentTransaction ft = fm.beginTransaction();
				// if (activity.cookBookFragment == null) {
				// activity.cookBookFragment = new PrivateFragment();
				// activity.ft.add(R.id.fl_content, activity.cookBookFragment);
				// }
				// activity.ft.show( activity.cookBookFragment);

				// ft.replace(R.id.fl_content, activity.cookBookFragment);

				// FragmentManager fm = getActivity().getFragmentManager();
				// //替换为TwoFragment
				// fm.beginTransaction().replace(R.id.fl_content,new
				// PrivateFragment()).commit();
			}
		});
		getActivity().onWindowFocusChanged(hasFocus);

	}

	private void initScroll() {
		// TODO Auto-generated method stub
		Log.i(TAG, "initScroll-->");
		myScrollView = (MyScrollView) getView().findViewById(R.id.scroll);
		search01 = (LinearLayout) getView().findViewById(R.id.search01);
		search02 = (LinearLayout) getView().findViewById(R.id.search02);
		rlayout = (RelativeLayout) getView().findViewById(R.id.rlayout);
		myScrollView.setOnScrollListener(this);

	}

	// 监听滚动Y值变化，通过addView和removeView来实现悬停效果
	@Override
	public void onScroll(int scrollY) {
		height = scrollY;
		if (scrollY >= rlayout.getBottom()) {
			if (rb_group.getParent() != search01) {
				search02.removeView(rb_group);
				search01.addView(rb_group);
			}
		} else {
			if (rb_group.getParent() != search02) {
				search01.removeView(rb_group);
				search02.addView(rb_group);
			}
		}

	}

	public void getState(int btnState) {
		if (btnState == 1) {
			rb_new.setChecked(true);
			showCookBook(1);
		} else if (btnState == 2) {
			rb_hot.setChecked(true);
			showCookBook(2);
		} else if (btnState == 3) {
			rb_recommend.setChecked(true);
			showCookBook(3);
		}
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		updatePmsStatus();
	}

	private void updatePmsStatus() {
		Log.i(TAG, "updatePmsStatus-->");
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

	public void showDlg() {
		if (null != this && null != dlg && !dlg.isShowing()) {
			dlg.show();
		} else if (null != this && null == dlg) {
			dlg = ShowDialogUtil.getShowDialog(getActivity(),
					R.layout.dialog_progressbar, 0, 0, false);
		}
	}

	public void closeDlg() {
		if (null != this && null != dlg && dlg.isShowing()) {
			dlg.dismiss();
		}
	}

	private void findSecondCookBook() {
		// 二级菜谱分类
		Log.i(TAG, "findSecondCookBook-->");
		showDlg();
		RequestParams params = new RequestParams(
				UrlInterface.URL_FINDSECONDCATEGORY);
		params.addBodyParameter("recommend", "1");
		params.addBodyParameter("rows", "8");

		x.http().post(params, new CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
				closeDlg();
				Gson gson = new Gson();
				JSONObject obj;
				try {
					obj = new JSONObject(result);
					findSecondCategoryBeanList = gson.fromJson(obj
							.getJSONObject("data")
							.getJSONArray("menuCategorys").toString(),
							new TypeToken<List<FindSecondCategoryBean>>() {
							}.getType());

					gvClassAdapter = new GVClassAdapter(getActivity(),
							findSecondCategoryBeanList);

					gv_foodclass.setAdapter(gvClassAdapter);

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

	private void showCookBook(final int flag) {
		// 美食最新、最热、推荐菜谱
		Log.i(TAG, "showCookBook("+String.valueOf(flag)+")-->");
		showDlg();
		RequestParams params = new RequestParams(UrlInterface.URL_FINDMENUBOOK);
		params.addBodyParameter("categoryId", "");
		params.addBodyParameter("order", flag + "");
		params.addBodyParameter("name", "");
		params.addBodyParameter("page", 1 + "");

		x.http().post(params, new CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
				closeDlg();
				Gson gson = new Gson();
				JSONObject obj;
				try {
					obj = new JSONObject(result);
					categoryCoonFoodMenuBeanList = gson.fromJson(obj
							.getJSONObject("data").getJSONArray("menubooks")
							.toString(),
							new TypeToken<List<CoonFoodMenuBean>>() {
							}.getType());
					flagState = flag;

					cookFoodBeanAdapter = new CookFoodBeanAdapter(
							getActivity(), categoryCoonFoodMenuBeanList,
							flagState + "");
					rv_goodfood.setAdapter(cookFoodBeanAdapter);
					if (categoryCoonFoodMenuBeanList.size() <= 4) {
						search01.removeView(rb_group);
						search02.addView(rb_group);
					}
					// rv_goodfood.removeView(child);
					cookFoodBeanAdapter.notifyDataSetChanged();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (categoryCoonFoodMenuBeanList.size() == 0) {
					KappUtils.showToast(context, "还没有设置推荐哦");
				}
			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				// TODO Auto-generated method stub
				closeDlg();
//				KappUtils.showToast(context, "获取数据失败");
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

	private void showStandardLib() {
		// 标准食材库
		Log.i(TAG, "showStandardLib-->");
		if (standardAdapter == null) {
			RequestParams params = new RequestParams(
					UrlInterface.URL_FOOD_MATERIAL_LIB);
			showDlg();
			x.http().post(params, new CommonCallback<String>() {
				@Override
				public void onSuccess(String result) {
					Log.i(TAG, "net showStandardLib--onSuccess--:"+result);
					closeDlg();
					Gson gson = new Gson();
					JSONObject obj;
					try {
						obj = new JSONObject(result);
						categoryFirstBeanList = gson.fromJson(
								obj.getJSONObject("data")
										.getJSONArray("towFoodStoreCategorys")
										.toString(),
								new TypeToken<List<CategoryFirstBean>>() {
								}.getType());

						standardAdapter = new StandardFoodMaterialAdapter(
								context, categoryFirstBeanList,
								new StandardFoodMaterialAdapter.OnReceiver() {
									@Override
									public void onClick(int groupPosition,
											int position, int ChildPosition) {
										Log.i(TAG, "net StandardFoodMaterialAdapter.OnReceiver()--onClick--:"+"");
										if (position != -1) {
											Intent intent = new Intent(
													context,
													CookBookStandardFoodMaterialDetailsActivity.class);
											intent.putExtra("category",
													categoryFirstBeanList
															.get(groupPosition));
											intent.putExtra("position",
													position);
											intent.putExtra("ChildPosition",
													ChildPosition);
											Log.i(TAG, "net StandardFoodMaterialAdapter.OnReceiver()--onClick--:"+"跳转："
													+ "CookBookStandardFoodMaterialDetailsActivity");
											
											startActivity(intent);
										}
									}
								});
						lv_foodclass.setAdapter(standardAdapter);
						for (int i = 0; i < standardAdapter.getGroupCount(); i++) {
							lv_foodclass.expandGroup(i);
						}
						lv_foodclass
								.setOnGroupClickListener(new OnGroupClickListener() {
									@Override
									public boolean onGroupClick(
											ExpandableListView parent, View v,
											int groupPosition, long id) {
										return true;
									}
								});
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

				@Override
				public void onError(Throwable ex, boolean isOnCallback) {
					closeDlg();
				}

				@Override
				public void onCancelled(CancelledException cex) {
					closeDlg();
				}

				@Override
				public void onFinished() {
					closeDlg();
				}
			});
		}
	}

	private void findAd() {
		// 广告
		Log.i(TAG, "findAd-->");
		showDlg();
		RequestParams params = new RequestParams(UrlInterface.URL_FINDAD);

		x.http().post(params, new CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
				closeDlg();
				Gson gson = new Gson();
				JSONObject obj;
				try {
					obj = new JSONObject(result);
					goodFoodFinAdBeanList = gson.fromJson(
							obj.getJSONObject("data").getJSONArray("ads")
									.toString(),
							new TypeToken<List<GoodFoodFinAdBean>>() {
							}.getType());

					// for(int i=0;i<goodFoodFinAdBeanList.size();i++){
					// // goodFoodFinAdBeanList.get(i).getLogoPath();
					// // try {
					// // bitMap=new ArrayList<Bitmap>();
					// //// bitMap.add(getBitmap(UrlInterface.URL_HOST
					// //// + goodFoodFinAdBeanList.get(i).getLogoPath()));
					// ////
					// getBitmap(goodFoodFinAdBeanList.get(i).getLogoPath());
					// // } catch (IOException e) {
					// // // TODO Auto-generated catch block
					// // e.printStackTrace();
					// // }
					// }
					GoodFoodFinAdBean finAdBean = new GoodFoodFinAdBean();

					if (goodFoodFinAdBeanList != null) {
						ArrayList<String> picUrls = new ArrayList<String>();
						if (goodFoodFinAdBeanList.size() > 5) {
							for (int i = 0; i < 5; i++) {
								picUrls.add(goodFoodFinAdBeanList.get(i)
										.getLogoPath());
							}

							bannerView.config(750, 400, picUrls);
						} else {
							for (int i = 0; i < goodFoodFinAdBeanList.size(); i++) {
								picUrls.add(goodFoodFinAdBeanList.get(i)
										.getLogoPath());
							}

							bannerView.config(750, 400, picUrls);
						}
					}
					// slideshowView.getAds(goodFoodFinAdBeanList);
					// // slideshowView.setData(goodFoodFinAdBeanList);
					// slideshowView.goodFoodFinAdBeanList=goodFoodFinAdBeanList;

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

	public static Bitmap getBitmap(String path) throws IOException {

		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5000);
		conn.setRequestMethod("GET");
		if (conn.getResponseCode() == 200) {
			InputStream inputStream = conn.getInputStream();
			Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
			return bitmap;
		}
		return null;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == -1) {
			switch (requestCode) {
			case 1001:
				String result = data.getExtras().getString("flagState");
				MyLog.d("解析结果：" + result);
				if (result.equals(1)) {
					rb_new.setChecked(true);
					showCookBook(1);
				} else if (result.equals(2)) {
					rb_hot.setChecked(true);
					showCookBook(2);
				} else if (result.equals(3)) {
					rb_recommend.setChecked(true);
					showCookBook(3);
				}

				break;
			}
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		if (hasFocus) {
			searchLayoutTop = rlayout.getBottom();// 获取searchLayout的顶部位置
		}
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		if(isNetworkAvailable(getContext())==true){
			mHandler.sendEmptyMessageDelayed(REFRESH_COMPLETE, 2000);
		}else{
			KappUtils.showToast(context, "获取数据失败");
		}
		
	}

	/** 
     * 检测当的网络（WLAN、3G/2G）状态 
     * @param context Context 
     * @return true 表示网络可用 
     */  
    public static boolean isNetworkAvailable(Context context) {  
        ConnectivityManager connectivity = (ConnectivityManager) context  
                .getSystemService(Context.CONNECTIVITY_SERVICE);  
        if (connectivity != null) {  
            NetworkInfo info = connectivity.getActiveNetworkInfo();  
            if (info != null && info.isConnected())   
            {  
                // 当前网络是连接的  
                if (info.getState() == NetworkInfo.State.CONNECTED)   
                {  
                    // 当前所连接的网络可用  
                    return true;  
                }  
            }  
        }  
        return false;  
    }
	
	@Override
	public void onItemClick(View v, int position) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(context, NetCookBookDetailActivity.class);
		intent.putExtra("menuBookId",
				String.valueOf(goodFoodFinAdBeanList.get(position).getId()));
		(context).startActivity(intent);
	}
	
	public void reloadUI(){
		if (!TextUtils.isEmpty(Config.SERVER_HOST_NAME)) {
			iv_titleLeft.setImageResource(R.drawable.icon04);
		} else {
			iv_titleLeft.setImageResource(R.drawable.icon05);
		}
	}

}
