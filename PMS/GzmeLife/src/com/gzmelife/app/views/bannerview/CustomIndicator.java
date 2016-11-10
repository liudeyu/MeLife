package com.gzmelife.app.views.bannerview;

import java.util.ArrayList;

import com.gzmelife.app.R;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * 自定义indicator
 */
public class CustomIndicator extends LinearLayout {

	private int cResid = R.drawable.dot_black;
	private int nResid = R.drawable.dot_white;

	public ArrayList<ImageView> indicators = new ArrayList<ImageView>();

	public CustomIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void configIndicator(int cResid, int nResid) {
		this.cResid = cResid;
		this.nResid = nResid;
	}

	/**
	 * 初始化indicator
	 *
	 * @param context
	 * @param size
	 *            indicator个数
	 * @param curIndex
	 *            当前显示位置
	 */
	public void initIndicator(Context context, int size, int curIndex) {
		removeAll();
		indicators.clear();
		for (int i = 0; i < size; i++) {
			ImageView indicator = new ImageView(context);
			indicator.setPadding(4, 6, 4, 6);
			indicator.setImageResource(nResid);
			this.addView(indicator);
			indicators.add(indicator);
		}
		if (size == 0) {
			return;
		}
		indicators.get(0).setImageResource(cResid);
		changeIndiccator(curIndex);
	}

	public void initIndicator(Context context, ViewPager pager) {
		removeAll();
		indicators.clear();
		if (pager.getAdapter().getCount() == 1) {
			return;
		}
		for (int i = 0; i < pager.getAdapter().getCount(); i++) {
			ImageView indicator = new ImageView(context);
			int padding = Func.Dp2Px(getContext(), 3);
			indicator.setPadding(padding, padding, padding, padding);
			indicator.setImageResource(nResid);
			this.addView(indicator);
			indicators.add(indicator);
		}
		if (pager.getAdapter().getCount() > 0) {
			indicators.get(0).setImageResource(cResid);
		}
		changeIndiccator(pager.getCurrentItem());
		pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int i, float v, int i1) {

			}

			@Override
			public void onPageSelected(int arg0) {
				changeIndiccator(arg0);
			}

			@Override
			public void onPageScrollStateChanged(int i) {

			}
		});
	}

	/**
	 * 切换当前显示位置
	 *
	 * @param curIndex
	 *            要显示的位置
	 */
	public void changeIndiccator(int curIndex) {
		for (int i = 0; i < indicators.size(); i++) {
			if (i == curIndex) {
				indicators.get(i).setImageResource(cResid);
			} else {
				indicators.get(i).setImageResource(nResid);
			}
		}
	}

	/**
	 * 清空indicator
	 */
	public void removeAll() {
		for (int i = 0; i < indicators.size(); i++) {
			this.removeView(indicators.get(i));
		}
	}

}
