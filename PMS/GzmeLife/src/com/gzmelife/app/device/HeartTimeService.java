package com.gzmelife.app.device;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * 心跳服务
 */
public class HeartTimeService extends Service {
    public HeartTimeService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
