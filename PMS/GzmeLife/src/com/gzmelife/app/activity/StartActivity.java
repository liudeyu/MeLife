package com.gzmelife.app.activity;

import java.util.ArrayList;
import java.util.List;

import com.gzmelife.app.R;
import com.gzmelife.app.tools.KappUtils;
import com.mob.tools.gui.ViewPagerAdapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class StartActivity extends Activity implements OnClickListener,
		OnPageChangeListener {

	private ViewPager vp;
	private List<View> views;
	// 引导图片资源

	private static final int[] pics = { R.drawable.start01,

	R.drawable.start02, R.drawable.start03 };
	// 记录当前选中位置

	private int currentIndex;

	private ViewPagerAdapter vpAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);

		views = new ArrayList<View>();
		LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,

				LinearLayout.LayoutParams.WRAP_CONTENT);

		for (int i = 0; i < pics.length; i++) {

			ImageView iv = new ImageView(this);

			iv.setLayoutParams(mParams);

			iv.setImageResource(pics[i]);

			views.add(iv);
		}
		vp = (ViewPager) findViewById(R.id.viewpager);
		vpAdapter = new ViewPagerAdapter(views);

		vp.setAdapter(vpAdapter);

		// 绑定回调

		vp.setOnPageChangeListener(this);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int position = (Integer) v.getTag();
		setCurView(position);
		setCurDot(position);

	}

	private void setCurView(int position)

	{

		if (position < 0 || position >= pics.length) {

			return;

		}

		vp.setCurrentItem(position);

	}

	private void setCurDot(int positon)

	{

		if (positon < 0 || positon > pics.length - 1 || currentIndex == positon) {

			return;

		}

		currentIndex = positon;

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub
		
	}

	public class ViewPagerAdapter extends PagerAdapter {

		// 界面列表

		private List<View> views;

		public ViewPagerAdapter(List<View> views) {

			this.views = views;

		}

		// 销毁arg1位置的界面

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {

			((ViewPager) arg0).removeView(views.get(arg1));

		}

		@Override
		public void finishUpdate(View arg0) {

			// TODO Auto-generated method stub

		}

		// 获得当前界面数

		@Override
		public int getCount() {

			if (views != null)

			{

				return views.size();

			}

			return 0;

		}

		// 初始化arg1位置的界面

		@Override
		public Object instantiateItem(View arg0, int arg1) {

			((ViewPager) arg0).addView(views.get(arg1), 0);
			if (arg1 == 2) {
				views.get(2).setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(StartActivity.this,
            					MainActivity.class);
            			startActivity(intent);
            			finish();
					}
				});
			}

			return views.get(arg1);

		}

		// 判断是否由对象生成界面

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {

			return (arg0 == arg1);

		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {

			// TODO Auto-generated method stub

		}

		@Override
		public Parcelable saveState() {

			// TODO Auto-generated method stub

			return null;

		}

		@Override
		public void startUpdate(View arg0) {

			// TODO Auto-generated method stub

		}
	}
}
