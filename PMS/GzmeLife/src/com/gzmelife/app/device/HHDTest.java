package com.gzmelife.app.device;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

/**
 * Created by HHD on 2016/10/21.
 *
 */
public abstract class HHDTest {


    class 递归 {    /*创建类*/
        public void main(String[] args) {
            System.out.println(dg(100));
        }
        int dg (int i) {     /*定义变量 */
            int sum;

            if (i == 1)         /*假设条件*/
                return 1;

            else
                sum = i + dg(i - 1);     /*1~100的和的表达式*/

            return sum;              /*返回结果*/
        }
    }



    /** 在未到的毫秒 */
    private final long mMillisInFuture;
    /** 倒计时间隔 */
    private final long mCountdownInterval;
    /** 停止的毫秒 */
    private long mStopTimeInFuture;

    public HHDTest(long millisInFuture, long countDownInterval){
        mMillisInFuture = millisInFuture;
        mCountdownInterval = countDownInterval;
    }

    public final void cancel(){
        mHandler.removeMessages(MSG);
    }

    public synchronized final HHDTest start(){
        if (mMillisInFuture <= 0) {
            onFinish();
            return this;
        }
        mStopTimeInFuture = SystemClock.elapsedRealtime() + mMillisInFuture;
        mHandler.sendMessage(mHandler.obtainMessage(MSG));
        return this;
    }

    public abstract void onTick(long millisUntilFinished);

    public abstract void onFinish();

    private static final int MSG = 1;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            synchronized (HHDTest.this) {
                /** 剩下的毫秒 */
                final long millisLeft = mStopTimeInFuture - SystemClock.elapsedRealtime();
                if (millisLeft <= 0) {
                    onFinish();
                } else if (millisLeft < mCountdownInterval) {
                    sendMessageDelayed(obtainMessage(MSG), millisLeft);//millisLeft后挂起入队的消息
                } else {
                    /** 最后一个开始 */
                    long lastTickStart = SystemClock.elapsedRealtime();
                    onTick(millisLeft);
                    /** 延迟 */
                    long delay = lastTickStart + mCountdownInterval - SystemClock.elapsedRealtime();
                    while (delay < 0) delay += mCountdownInterval;
                    sendMessageDelayed(obtainMessage(MSG), delay);
                }
            }
        }
    };

}
