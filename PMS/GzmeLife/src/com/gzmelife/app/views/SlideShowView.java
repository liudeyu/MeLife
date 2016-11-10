package com.gzmelife.app.views;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;
import org.xutils.common.Callback.CancelledException;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.http.RequestParams;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gzmelife.app.KappAppliction;
import com.gzmelife.app.R;
import com.gzmelife.app.UrlInterface;
import com.gzmelife.app.activity.BaseActivity;
import com.gzmelife.app.activity.LoginActivity;
import com.gzmelife.app.activity.NetCookBookDetailActivity;
import com.gzmelife.app.activity.StandardFoodMaterialDetailsActivity;
import com.gzmelife.app.adapter.StandardFoodMaterialAdapter;
import com.gzmelife.app.bean.CategoryFirstBean;
import com.gzmelife.app.bean.CategorySecondBean;
import com.gzmelife.app.bean.GoodFoodFinAdBean;
import com.gzmelife.app.bean.UserInfoBean;
import com.gzmelife.app.fragment.GoodFoodFragment;
import com.gzmelife.app.tools.ImgLoader;
import com.gzmelife.app.tools.KappUtils;
import com.gzmelife.app.tools.SharedPreferenceUtil;
import com.gzmelife.app.tools.ShowDialogUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

/**
 * ViewPager实现的轮播图广告自定义视图，如京东首页的广告轮播图效果； 既支持自动轮播页面也支持手势滑动切换页面
 * 
 * @author caizhiming
 *
 */

public class SlideShowView extends FrameLayout {

	private String flagState;
	// 轮播图图片数量
	private final static int IMAGE_COUNT = 5;
	// 自动轮播的时间间隔
	private final static int TIME_INTERVAL = 5;
	// 自动轮播启用开关
	private final static boolean isAutoPlay = true;

	// 自定义轮播图的资源ID
	private int[] imagesResIds;
	// 放轮播图片的ImageView 的list
	private List<ImageView> imageViewsList;
	// 放圆点的View的list
	private List<View> dotViewsList;

	private ViewPager viewPager;
	// 当前轮播页
	private int currentItem = 0;
	// 定时任务
	private ScheduledExecutorService scheduledExecutorService;
	public static List<GoodFoodFinAdBean> goodFoodFinAdBeanList;
	private int flag = 0;
	private List<Bitmap> bitMap;
	private ImageView[] indicator_imgs;

	Context context;

	// Handler
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			
			super.handleMessage(msg);
			viewPager.setCurrentItem(currentItem);

		}

	};
	private LinearLayout linear;
	private MyPagerAdapter adapter;

	public void getAds(List<GoodFoodFinAdBean> goodFoodFinAdBeanList) {
		this.goodFoodFinAdBeanList = goodFoodFinAdBeanList;
		try {
			initUI(context);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}

	public SlideShowView(Context context) {
		this(context, null);
		//  Auto-generated constructor stub
	}

	public SlideShowView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		// Auto-generated constructor stub
	}

	public SlideShowView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		this.context = context;
		initData();

		// findAd();
		try {
			initUI(context);
		} catch (IOException e) {
			//  Auto-generated catch block
			e.printStackTrace();
		}
		if (isAutoPlay) {
			startPlay();
		}

	}

	/**
	 * 开始轮播图切换
	 */
	private void startPlay() {
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		scheduledExecutorService.scheduleAtFixedRate(new SlideShowTask(), 1, 4,
				TimeUnit.SECONDS);
	}

	/**
	 * 停止轮播图切换
	 */
	private void stopPlay() {
		scheduledExecutorService.shutdown();
	}

	/**
	 * 初始化相关Data
	 */
	private void initData() {

		// imagesResIds = new int[] { R.drawable.tu3, R.drawable.tu3,
		// R.drawable.tu3, R.drawable.tu3, R.drawable.tu3,
		//
		// };
		//
		imageViewsList = new ArrayList<ImageView>();
		// dotViewsList = new ArrayList<View>();

	}

	/**
	 * 初始化Views等UI
	 * 
	 * @throws IOException
	 */
	private void initUI(Context context) throws IOException {
		LayoutInflater.from(context).inflate(R.layout.layout_slideshow, this,
				true);
		linear = (LinearLayout) findViewById(R.id.indicator);
		linear.removeAllViews();
		if (goodFoodFinAdBeanList != null) {
			indicator_imgs = new ImageView[goodFoodFinAdBeanList.size()];
			// (new MyPagerAdapter()).notify();
			if (goodFoodFinAdBeanList.size() > 5) {
				for (int i = 0; i < 5; i++) {
					ImageView view = new ImageView(context);
					// new
					new ImgLoader(context).showPic(goodFoodFinAdBeanList.get(i)
							.getLogoPath(), view);
					final int id = goodFoodFinAdBeanList.get(i).getId();
					view.setScaleType(ScaleType.FIT_XY);
					imageViewsList.add(view);
					view.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							
							Intent intent = new Intent(
									SlideShowView.this.context,
									NetCookBookDetailActivity.class);
							intent.putExtra("menuBookId", String.valueOf(id));
							(SlideShowView.this.context).startActivity(intent);

						}
					});
					ImageView imageView = new ImageView(context);

					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
							10, 10);
					params.leftMargin = 5;
					params.rightMargin = 5;
					// linear.addView(imageView, params);
					// dotViewsList.add(imageView);

					imageView.setLayoutParams(params);
					indicator_imgs[i] = imageView;
					if (i == 0) {
						indicator_imgs[i]
								.setBackgroundResource(R.drawable.dot_black);
					} else {
						indicator_imgs[i]
								.setBackgroundResource(R.drawable.dot_white);
					}
					linear.addView(indicator_imgs[i]);
				}
			} else {
				for (int i = 0; i < goodFoodFinAdBeanList.size(); i++) {
					ImageView view = new ImageView(context);
					// new
					new ImgLoader(context).showPic(goodFoodFinAdBeanList.get(i)
							.getLogoPath(), view);
					final int id = goodFoodFinAdBeanList.get(i).getId();
					view.setScaleType(ScaleType.FIT_XY);
					imageViewsList.add(view);
					view.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							
							Intent intent = new Intent(
									SlideShowView.this.context,
									NetCookBookDetailActivity.class);
							intent.putExtra("menuBookId", String.valueOf(id));
							(SlideShowView.this.context).startActivity(intent);

						}
					});
					ImageView imageView = new ImageView(context);

					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
							10, 10);
					params.leftMargin = 5;
					params.rightMargin = 5;

					imageView.setLayoutParams(params);
					indicator_imgs[i] = imageView;
					if (i == 0) {
						indicator_imgs[i]
								.setBackgroundResource(R.drawable.dot_black);
					} else {
						indicator_imgs[i]
								.setBackgroundResource(R.drawable.dot_white);
					}
					linear.addView(indicator_imgs[i]);
					
				}
			}
			
		}
		// http://121.41.86.29:8888/pms/upload/foodCategory/20160301163633241698.png

		// dotViewsList.add(findViewById(R.id.v_dot1));
		// dotViewsList.add(findViewById(R.id.v_dot2));
		// dotViewsList.add(findViewById(R.id.v_dot3));
		// dotViewsList.add(findViewById(R.id.v_dot4));
		// dotViewsList.add(findViewById(R.id.v_dot5));

		viewPager = (ViewPager) findViewById(R.id.viewPager);
		viewPager.setFocusable(true);
		adapter = new MyPagerAdapter();
		viewPager.setAdapter(adapter);
		viewPager.setOnPageChangeListener(new MyPageChangeListener());

	}

	/**
	 * 填充ViewPager的页面适配器
	 * 
	 * @author caizhiming
	 */
	private class MyPagerAdapter extends PagerAdapter {

		@Override
		public void destroyItem(View container, int position, Object object) {
			
			// ((ViewPag.er)container).removeView((View)object);
			((ViewPager) container).removeView(imageViewsList.get(position));
		}

		@Override
		public Object instantiateItem(View container, int position) {
			

			((ViewPager) container).addView(imageViewsList.get(position));
			return imageViewsList.get(position);
		}

		@Override
		public int getCount() {
			
			return imageViewsList.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			
			return arg0 == arg1;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
			

		}

		@Override
		public Parcelable saveState() {
			
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
			

		}

		@Override
		public void finishUpdate(View arg0) {
			

		}
		@Override
		public int getItemPosition(Object object) {
			
			return POSITION_NONE;
		}

	}

	/**
	 * ViewPager的监听器 当ViewPager中页面的状态发生改变时调用
	 * 
	 * @author caizhiming
	 */
	private class MyPageChangeListener implements OnPageChangeListener {

		boolean isAutoPlay = false;

		@Override
		public void onPageScrollStateChanged(int arg0) {
			
			switch (arg0) {
			case 1:// 手势滑动，空闲中
				isAutoPlay = false;
				break;
			case 2:// 界面切换中
				isAutoPlay = true;
				break;
			case 0:// 滑动结束，即切换完毕或者加载完毕
					// 当前为最后一张，此时从右向左滑，则切换到第一张
				if (viewPager.getCurrentItem() == viewPager.getAdapter()
						.getCount() - 1 && !isAutoPlay) {
					viewPager.setCurrentItem(0);
				}
				// 当前为第一张，此时从左向右滑，则切换到最后一张
				else if (viewPager.getCurrentItem() == 0 && !isAutoPlay) {
					viewPager
							.setCurrentItem(viewPager.getAdapter().getCount() - 1);
				}
				break;
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			

		}

		@Override
		public void onPageSelected(int pos) {
			

			currentItem = pos;
				for (int i = 0; i < indicator_imgs.length; i++) {
					if (i == pos) {
						indicator_imgs[i]
								.setBackgroundResource(R.drawable.dot_black);
					} else {
						indicator_imgs[i]
								.setBackgroundResource(R.drawable.dot_white);
					}
					// indicator_imgs[i].setBackgroundResource(R.drawable.dot_black);
				}
				for (int i = 0; i < indicator_imgs.length; i++) {
					if (i == pos) {
						indicator_imgs[i]
								.setBackgroundResource(R.drawable.dot_black);
					} else {
						indicator_imgs[i]
								.setBackgroundResource(R.drawable.dot_white);
					}
					// indicator_imgs[i].setBackgroundResource(R.drawable.dot_black);
				}

			// indicator_imgs[pos].setBackgroundResource(R.drawable.dot_white);
			// currentItem = pos;
		}
	}

	/**
	 * 执行轮播图切换任务
	 *
	 * @author caizhiming
	 */
	private class SlideShowTask implements Runnable {

		@Override
		public void run() {
			
			synchronized (viewPager) {
				currentItem = (currentItem + 1) % imageViewsList.size();
				handler.obtainMessage().sendToTarget();
			}
		}

	}

	/**
	 * 销毁ImageView资源，回收内存
	 * 
	 * @author caizhiming
	 */
	private void destoryBitmaps() {

		for (int i = 0; i < IMAGE_COUNT; i++) {
			ImageView imageView = imageViewsList.get(i);
			Drawable drawable = imageView.getDrawable();
			if (drawable != null) {
				// 解除drawable对view的引用
				drawable.setCallback(null);
			}
		}
	}

}
