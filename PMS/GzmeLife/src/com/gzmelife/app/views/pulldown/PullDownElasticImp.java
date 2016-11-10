package com.gzmelife.app.views.pulldown;

import com.gzmelife.app.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * @author wangxiao1@cjsc.com.cn
 * @date   2013-7-10
 * Ĭ�������ؼ�����ʵ��
 */
public class PullDownElasticImp implements IPullDownElastic {
    private View refreshView;
    private ImageView arrowImageView;
    private int headContentHeight;
    private ProgressBar progressBar;
    private TextView tipsTextview;
    private TextView lastUpdatedTextView;
    
    private Context mContext;
    public PullDownElasticImp(Context context) {
        mContext = context;
        init();
    }
    

    private void init() {
        // ˢ����ͼ���˵ĵ�view
        refreshView = LayoutInflater.from(mContext).inflate(
                R.layout.refresh_top_item, null);

        // ָʾ��view
        arrowImageView = (ImageView) refreshView
                .findViewById(R.id.head_arrowImageView);
        // ˢ��bar
        progressBar = (ProgressBar) refreshView
                .findViewById(R.id.head_progressBar);
        // ������ʾtext
        tipsTextview = (TextView) refreshView.findViewById(R.id.refresh_hint);
        // ������ʾʱ��
        lastUpdatedTextView = (TextView) refreshView
                .findViewById(R.id.refresh_time);

        headContentHeight = dip2px(mContext, 50);
    }

    /**
     * �����ֻ��ķֱ��ʴ� dp �ĵ�λ ת��Ϊ px(����)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    /**
     * @return
     * 
     */
    @Override
    public View getElasticLayout() {
        return refreshView;
    }

    /**
     * @return
     * 
     */
    @Override
    public int getElasticHeight() {
        return headContentHeight;
    }

    /**
     * @param show
     * 
     */
    @Override
    public void showArrow(int visibility) {
        arrowImageView.setVisibility(visibility);
    }

    /**
     * @param animation
     * 
     */
    @Override
    public void startAnimation(Animation animation) {
        arrowImageView.startAnimation(animation);
    }

    /**
     * 
     * 
     */
    @Override
    public void clearAnimation() {
        arrowImageView.clearAnimation();
    }

    /**
     * @param show
     * 
     */
    @Override
    public void showProgressBar(int visibility) {
        progressBar.setVisibility(visibility);
    }

    /**
     * @param tips
     * 
     */
    @Override
    public void setTips(String tips) {
        tipsTextview.setText(tips);
    }

    /**
     * @param show
     * 
     */
    @Override
    public void showLastUpdate(int visibility) {
        lastUpdatedTextView.setVisibility(visibility);
    }

    /**
     * @param text
     * 
     */
    public void setLastUpdateText(String text) {
        lastUpdatedTextView.setText(text);
    }


    /**
     * @param state
     * @param isBack
     * 
     */
    @Override
    public void changeElasticState(int state, boolean isBack) {
        // TODO Auto-generated method stub
        
    }

}
