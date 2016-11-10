package com.handmark.pulltorefresh.library;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

public class CustomGridview extends GridView{
	
	public CustomGridview(Context context) {  
        super(context);  
    }  
  
    public CustomGridview(Context context, AttributeSet attrs) {  
        super(context, attrs);  
    }  
  
    public CustomGridview(Context context, AttributeSet attrs, int defStyle) {  
        super(context, attrs, defStyle);  
    }  
  
	   @Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		   int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);  
	       super.onMeasure(widthMeasureSpec, expandSpec);  
	}
}
