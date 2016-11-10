package com.gzmelife.app.views.pulldown;


import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

/**
 * @author wangxiao1@cjsc.com.cn
 * @date 2013-7-9
 * 
 */
public class PullDownScrollView extends LinearLayout {

    private static final String TAG = "PullDownScrollView";

    private int refreshTargetTop = -60;
    private int headContentHeight;

    private RefreshListener refreshListener;

    private RotateAnimation animation;
    private RotateAnimation reverseAnimation;
    
    private final static int RATIO = 2;
    private int preY = 0;
    private boolean isElastic = false;
    private int startY;
    private int state;
    
    private String note_release_to_refresh = "�ɿ�����";
    private String note_pull_to_refresh = "����ˢ��";
    private String note_refreshing = "���ڸ���...";
    
    private IPullDownElastic mElastic;
    

    public PullDownScrollView(Context context) {
        super(context);
        init();

    }

    public PullDownScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        animation = new RotateAnimation(0, -180,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(250);
        animation.setFillAfter(true);

        reverseAnimation = new RotateAnimation(-180, 0,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        reverseAnimation.setInterpolator(new LinearInterpolator());
        reverseAnimation.setDuration(200);
        reverseAnimation.setFillAfter(true);
    }
    /**
     * ˢ�¼���
     * @param listener
     */
    public void setRefreshListener(RefreshListener listener) {
        this.refreshListener = listener;
    }
    /**
     * ��������
     * @param elastic
     */
    public void setPullDownElastic(IPullDownElastic elastic) {
        mElastic = elastic;
        
        headContentHeight = mElastic.getElasticHeight();
        refreshTargetTop = - headContentHeight;
        LayoutParams lp = new LinearLayout.LayoutParams(
                LayoutParams.FILL_PARENT, headContentHeight);
        lp.topMargin = refreshTargetTop;
        addView(mElastic.getElasticLayout(), 0, lp);
    }
    
    /**
     * ���ø�����ʾ��
     * @param pullToRefresh ����ˢ����ʾ��
     * @param releaseToRefresh �ɿ�ˢ����ʾ��
     * @param refreshing ����ˢ����ʾ��
     */
    public void setRefreshTips(String pullToRefresh, String releaseToRefresh, String refreshing) {
        note_pull_to_refresh = pullToRefresh;
        note_release_to_refresh = releaseToRefresh;
        note_refreshing = refreshing;
    }
    /*
     * �÷���һ���ontouchEvent һ���� (non-Javadoc)
     * 
     * @see
     * android.view.ViewGroup#onInterceptTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Logger.d(TAG, "onInterceptTouchEvent");
        printMotionEvent(ev);
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            preY = (int) ev.getY();
        }
        if (ev.getAction() == MotionEvent.ACTION_MOVE) {

            Logger.d(TAG, "isElastic:" + isElastic + " canScroll:"+ canScroll() + " ev.getY() - preY:"+(ev.getY() - preY));
            if (!isElastic && canScroll()
                    && (int) ev.getY() - preY >= headContentHeight / (3*RATIO)
                    && refreshListener != null && mElastic != null) {

                isElastic = true;
                startY = (int) ev.getY();
                Logger.i(TAG, "��moveʱ���¼��λ��startY:" + startY);
                return true;
            }

        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Logger.d(TAG, "onTouchEvent");
        printMotionEvent(event);
        handleHeadElastic(event);
        return super.onTouchEvent(event);
    }

    private void handleHeadElastic(MotionEvent event) {
        if (refreshListener != null && mElastic != null) {
            switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Logger.i(TAG, "down");
                break;
            case MotionEvent.ACTION_UP:

                Logger.i(TAG, "up");
                if (state != IPullDownElastic.REFRESHING && isElastic) {
                    
                    if (state == IPullDownElastic.DONE) {
                        // ʲô������
                        setMargin(refreshTargetTop);
                    }
                    if (state == IPullDownElastic.PULL_To_REFRESH) {
                        state = IPullDownElastic.DONE;
                        setMargin(refreshTargetTop);
                        changeHeaderViewByState(state, false);
                        Logger.i(TAG, "������ˢ��״̬����done״̬");
                    }
                    if (state == IPullDownElastic.RELEASE_To_REFRESH) {
                        state = IPullDownElastic.REFRESHING;
                        setMargin(0);
                        changeHeaderViewByState(state, false);
                        onRefresh();
                        Logger.i(TAG, "���ɿ�ˢ��״̬����done״̬");
                    }

                }
                isElastic = false;
                break;
            case MotionEvent.ACTION_MOVE:
                Logger.i(TAG, "move");
                int tempY = (int) event.getY();
                
                if (state != IPullDownElastic.REFRESHING && isElastic) {
                    // ��������ȥˢ����
                    if (state == IPullDownElastic.RELEASE_To_REFRESH) {
                        if (((tempY - startY) / RATIO < headContentHeight)
                                && (tempY - startY) > 0) {
                            state = IPullDownElastic.PULL_To_REFRESH;
                            changeHeaderViewByState(state, true);
                            Logger.i(TAG, "���ɿ�ˢ��״̬ת�䵽����ˢ��״̬");
                        } else if (tempY - startY <= 0) {
                            state = IPullDownElastic.DONE;
                            changeHeaderViewByState(state, false);
                            Logger.i(TAG, "���ɿ�ˢ��״̬ת�䵽done״̬");
                        }
                    }
                    if (state == IPullDownElastic.DONE) {
                        if (tempY - startY > 0) {
                            state = IPullDownElastic.PULL_To_REFRESH;
                            changeHeaderViewByState(state, false);
                        }
                    }
                    if (state == IPullDownElastic.PULL_To_REFRESH) {
                        // ���������Խ���RELEASE_TO_REFRESH��״̬
                        if ((tempY - startY) / RATIO >= headContentHeight) {
                            state = IPullDownElastic.RELEASE_To_REFRESH;
                            changeHeaderViewByState(state, false);
                            Logger.i(TAG, "��done��������ˢ��״̬ת�䵽�ɿ�ˢ��");
                        } else if (tempY - startY <= 0) {
                            state = IPullDownElastic.DONE;
                            changeHeaderViewByState(state, false);
                            Logger.i(TAG, "��DOne��������ˢ��״̬ת�䵽done״̬");
                        }
                    }
                    if (tempY - startY > 0) {
                        setMargin((tempY - startY)/2 + refreshTargetTop);
                    }
                }
                break;
            }
        }
    }
    
    /**
     * 
     */
    private void setMargin(int top) {
        LinearLayout.LayoutParams lp = (LayoutParams) mElastic.getElasticLayout()
                .getLayoutParams();
        lp.topMargin = top;
        // �޸ĺ�ˢ��
        mElastic.getElasticLayout().setLayoutParams(lp);
        mElastic.getElasticLayout().invalidate();
    }

    private void changeHeaderViewByState(int state, boolean isBack) {

        mElastic.changeElasticState(state, isBack);
        switch (state) {
        case IPullDownElastic.RELEASE_To_REFRESH:
            mElastic.showArrow(View.VISIBLE);
            mElastic.showProgressBar(View.GONE);
            mElastic.showLastUpdate(View.VISIBLE);
            mElastic.setTips(note_release_to_refresh);

            mElastic.clearAnimation();
            mElastic.startAnimation(animation);
            Logger.i(TAG, "��ǰ״̬���ɿ�ˢ��");
            break;
        case IPullDownElastic.PULL_To_REFRESH:
            mElastic.showArrow(View.VISIBLE);
            mElastic.showProgressBar(View.GONE);
            mElastic.showLastUpdate(View.VISIBLE);
            mElastic.setTips(note_pull_to_refresh);

            mElastic.clearAnimation();

            // ����RELEASE_To_REFRESH״̬ת������
            if (isBack) {
                mElastic.startAnimation(reverseAnimation);
            }
            Logger.i(TAG, "��ǰ״̬������ˢ��");
            break;
        case IPullDownElastic.REFRESHING:
            mElastic.showArrow(View.GONE);
            mElastic.showProgressBar(View.VISIBLE);
            mElastic.showLastUpdate(View.GONE);
            mElastic.setTips(note_refreshing);

            mElastic.clearAnimation();
            Logger.i(TAG, "��ǰ״̬,����ˢ��...");
            break;
        case IPullDownElastic.DONE:
            mElastic.showProgressBar(View.GONE);
            mElastic.clearAnimation();
//            arrowImageView.setImageResource(R.drawable.goicon);
            // tipsTextview.setText("����ˢ��");
            // lastUpdatedTextView.setVisibility(View.VISIBLE);
            Logger.i(TAG, "��ǰ״̬��done");
            break;
        }
    }

    private void onRefresh() {
        // downTextView.setVisibility(View.GONE);
//        scroller.startScroll(0, i, 0, 0 - i);
//        invalidate();
        if (refreshListener != null) {
            refreshListener.onRefresh(this);
        }
    }

    /**
     * 
     */
    @Override
    public void computeScroll() {
//        if (scroller.computeScrollOffset()) {
//            int i = this.scroller.getCurrY();
//            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.refreshView
//                    .getLayoutParams();
//            int k = Math.max(i, refreshTargetTop);
//            lp.topMargin = k;
//            this.refreshView.setLayoutParams(lp);
//            this.refreshView.invalidate();
//            invalidate();
//        }
    }

    /**
     * ����ˢ���¼���UIˢ����ɺ����ص��˷���
     * @param text һ�㴫�룺���ϴθ���ʱ��:12:23��
     */
    public void finishRefresh(String text) {
        if (mElastic == null) {
            Logger.e(TAG, "finishRefresh mElastic:" + mElastic);
            return;
        }
        if (state == IPullDownElastic.DONE) {
            Logger.e(TAG, "==> finishRefresh state has already done");
        }
        state = IPullDownElastic.DONE;
        if (text != null) {
            mElastic.setLastUpdateText(text);
        }
        changeHeaderViewByState(state,false);
        Logger.i(TAG, "==>ִ����=====finishRefresh "+ text);

        mElastic.showArrow(View.VISIBLE);
        mElastic.showLastUpdate(View.VISIBLE);
        setMargin(refreshTargetTop);
//        scroller.startScroll(0, i, 0, refreshTargetTop);
//        invalidate();
    }

    private boolean canScroll() {
        View childView;
        if (getChildCount() > 1) {
            childView = this.getChildAt(1);
            if (childView instanceof AbsListView) {
                int top = ((AbsListView) childView).getChildAt(0).getTop();
                int pad = ((AbsListView) childView).getListPaddingTop();
                if ((Math.abs(top - pad)) < 3
                        && ((AbsListView) childView).getFirstVisiblePosition() == 0) {
                    return true;
                } else {
                    return false;
                }
            } else if (childView instanceof ScrollView) {
                if (((ScrollView) childView).getScrollY() == 0) {
                    return true;
                } else {
                    return false;
                }
            }

        }
        return canScroll(this);
    }
    
    /**
     * ������д�˷������Լ����������ӿؼ���Ŀǰֻ����AbsListView��ScrollView
     * @param view
     * @return
     */
    public boolean canScroll(PullDownScrollView view) {
        return false;
    }

    private void printMotionEvent(MotionEvent event) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            Logger.d(TAG, "down");
            break;
        case MotionEvent.ACTION_MOVE:
            Logger.d(TAG, "move");
            break;
        case MotionEvent.ACTION_UP:
            Logger.d(TAG, "up");
        default:
            break;
        }
    }
    /**
     * ˢ�¼����ӿ�
     */
    public interface RefreshListener {
        public void onRefresh(PullDownScrollView view);
    }

}
