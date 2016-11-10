package com.gzmelife.app.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

/**
 * @author leobert.lan
 * @date 2015年9月7日
 */
public class GridViewForScrollView extends GridView {
	
	
	private boolean isTouch;
	public GridViewForScrollView(Context paramContext) {
		super(paramContext);
	}

	public GridViewForScrollView(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
	}

	public GridViewForScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	
		int expandSpec= MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
					MeasureSpec.AT_MOST);
			super.onMeasure(widthMeasureSpec, expandSpec);
		
	}	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		return  isTouch;
	}

	public boolean isTouch() {
		return isTouch;
	}

	public void setTouch(boolean isTouch) {
		this.isTouch = isTouch;
	}

}