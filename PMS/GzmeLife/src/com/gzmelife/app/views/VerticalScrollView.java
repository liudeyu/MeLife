package com.gzmelife.app.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.ScrollView;

public class VerticalScrollView extends ScrollView {

	 private int mTouchSlop;
	    private int mLastMotionX;
	    private int mLastMotionY;
	    private int mActivePointerId = -1;
	    private boolean mIsBeingDragged = false;


	    public VerticalScrollView(Context context) {
	        super(context);
	        initView();
	    }

	    public VerticalScrollView(Context context, AttributeSet attrs) {
	        super(context, attrs);
	        initView();
	    }

	    public VerticalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
	        super(context, attrs, defStyleAttr);
	        initView();
	    }

	    private void initView() {
	        final ViewConfiguration configuration = ViewConfiguration.get(getContext());
	        mTouchSlop = configuration.getScaledTouchSlop();
	    }


	    @Override
	    public boolean onInterceptTouchEvent(MotionEvent ev) {
	        final int action = ev.getAction();
	        switch (action & MotionEvent.ACTION_MASK) {
	            case MotionEvent.ACTION_DOWN:
	                mLastMotionX = (int) ev.getX();
	                mLastMotionY =(int) ev.getY();
	                mActivePointerId = ev.getPointerId(0);
	                mIsBeingDragged = false;
	                break;
	            case MotionEvent.ACTION_POINTER_UP:
	                onSecondaryPointerUp(ev);
	                break;
	            case MotionEvent.ACTION_MOVE:
	                if (!mIsBeingDragged){
	                    if (mLastMotionY > mTouchSlop) {
	                        mIsBeingDragged = true;
	                        if (mLastMotionX > mLastMotionY) {
	                            return false;
	                        }
	                    }
	                }else{
	                    return false;
	                }
	                break;
	        }
	        return super.onInterceptTouchEvent(ev);
	    }

	    private void onSecondaryPointerUp(MotionEvent ev) {
	        final int pointerIndex = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >>
	                MotionEvent.ACTION_POINTER_INDEX_SHIFT;
	        final int pointerId = ev.getPointerId(pointerIndex);
	        if (pointerId == mActivePointerId) {
	            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
	            mLastMotionX = (int) ev.getX(newPointerIndex);
	            mLastMotionY = (int) ev.getY(newPointerIndex);
	            mActivePointerId = ev.getPointerId(newPointerIndex);
	        }
	    }
}