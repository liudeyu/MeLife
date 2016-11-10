package com.gzmelife.app.views.bannerview;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory.Options;

import java.util.ArrayList;

import com.gzmelife.app.R;
import com.gzmelife.app.UrlInterface;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class BannerView extends LinearLayout {
	private static final int DELAY = 5000;
	private Context context;
	public FrameLayout rootView;
	public CustomIndicator indicator;
	public BaseLibViewPager pager;
	public DisplayImageOptions options;
	public ImageLoader imageLoader;// 图片加载类

	private ArrayList<ImageView> views = new ArrayList<ImageView>();
	private ArrayList<String> data = new ArrayList<String>();

	private OnItemClickListener onItemClickListener;

	private BannerAdapter adapter;

	private Handler handler;
	private Runnable runnable;

	public interface OnItemClickListener {
		void onItemClick(View v, int position);
	}

	public OnItemClickListener getOnItemClickListener() {
		return onItemClickListener;
	}

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}

	public BannerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
		initUImageLoader();
	}

	public BannerView(Context context) {
		super(context);
		init(context);
		initUImageLoader();
	}

	public void setData(ArrayList<String> data) {
		this.data = data;
	}

	private void init(Context context) {
		this.context = context;
		View root = View.inflate(context, R.layout.view_banner, null);
		addView(root);
		rootView = (FrameLayout) root.findViewById(R.id.banner);
		indicator = (CustomIndicator) root.findViewById(R.id.indicator);
		pager = (BaseLibViewPager) root.findViewById(R.id.pager);

	}

	public void clear() {
		BannerView.this.pager.removeAllViews();
	}

	public void config(int w, int h, ArrayList<String> picUrls) {
		ViewGroup.LayoutParams params = BannerView.this.rootView
				.getLayoutParams();
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		params.height = dm.widthPixels * h / w;
		params.width = dm.widthPixels;
		BannerView.this.rootView.setLayoutParams(params);
		data.clear();
		data.addAll(picUrls);
		initBanner();
	}

	public void initBanner() {
		views.clear();
		for (int i = 0; i < data.size(); i++) {
			final ImageView imageView = (ImageView) View.inflate(context,
					R.layout.view_banner_item, null);
			final int index = i;
			imageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (onItemClickListener != null) {
						onItemClickListener.onItemClick(imageView, index);
					}
				}
			});
			views.add(imageView);
		}
		if (data.size() > 1) {
			BannerView.this.indicator.initIndicator(context, data.size(), 0);
		} else {
			BannerView.this.indicator.removeAll();
		}
		if (adapter == null) {
			adapter = new BannerAdapter();
			pager.setOffscreenPageLimit(adapter.getCount());
			BannerView.this.pager.setAdapter(adapter);
		} else {
			adapter.notifyDataSetChanged();
		}
		BannerView.this.pager
				.setOnPageChangeListener(new OnPageChangeAdapter() {
					@Override
					public void onPageSelected(int index) {
						BannerView.this.indicator.changeIndiccator(index);
						if (handler != null && runnable != null) {
							handler.removeCallbacks(runnable);
							handler.postDelayed(runnable, DELAY);
						}
					}
				});
		handler = new Handler();
		runnable = new Runnable() {

			@Override
			public void run() {
				if (adapter.getCount() == 0) {
					return;
				}
				int cur = BannerView.this.pager.getCurrentItem();
				cur = (++cur) % adapter.getCount();
				BannerView.this.pager.setCurrentItem(cur);
				BannerView.this.indicator.changeIndiccator(cur);
				handler.postDelayed(runnable, DELAY);
			}
		};
		if (data.size() > 1) {
			handler.postDelayed(runnable, DELAY);
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (handler != null && runnable != null) {
			handler.removeCallbacks(runnable);
		}
	}

	private class BannerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((View) object);
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			ImageView view = views.get(position);
			if (!TextUtils.isEmpty(data.get(position)))
				setImage(view, data.get(position));
			container.addView(view);
			return view;
		}
	}

	private void initUImageLoader() {
		Options decodingOptions = new Options();
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.c_loading)
				.showImageForEmptyUri(R.drawable.c_loading_failure).showImageOnFail(R.drawable.c_loading_failure)
				.decodingOptions(decodingOptions).cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				.defaultDisplayImageOptions(options).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory().diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.diskCacheSize(50 * 1024 * 1024).tasksProcessingOrder(QueueProcessingType.LIFO).writeDebugLogs()
				.build();
		ImageLoader.getInstance().init(config);
		imageLoader = ImageLoader.getInstance();
	}
	
	public void setImage(final ImageView iv, String url) {
		if (TextUtils.isEmpty(url))
			return;

		if (url.contains(UrlInterface.URL_HOST)) {

		} else {
			url = UrlInterface.URL_HOST + url;
			System.out.println(">>>>>url>>>"+url);
		}
		imageLoader.displayImage(url, iv, new SimpleImageLoadingListener() {

			@Override
			public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
				// TODO Auto-generated method stub
				// iv.setImageBitmap(arg2);
			}

		});
	}

}
