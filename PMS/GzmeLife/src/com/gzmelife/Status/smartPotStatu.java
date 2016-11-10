package com.gzmelife.Status;

import android.util.Log;

/*
 * 该类是记录上传智能锅数据的状态记录类，
 * 该类公共变量isDirty，
 * 使用方法：在整个程序中，任何地方都是new出一个smartPotStatu实例
 * 调用公开的方法，就可以实现同步
 * 设计原因：
 * 整个框架没有记录类，该类设计师为了解决单一的bug才设计的
 */
public class smartPotStatu {
private String TAG="smartPotStatu";
private static boolean isDirty=false;
	
public void setDirty(){
	setDirty(true);
	Log.i(TAG, "setDirty-->isDirty:"+String.valueOf(isDirty));
}
public boolean isDirty(){
	boolean ret=false;
	synchronized (smartPotStatu.class) {
		ret=isDirty;
	}
	return ret;
}
private void setDirty(boolean dirty){
	synchronized (smartPotStatu.class) {
		isDirty=dirty;
	}
}
/*
 * 外部调用，注意：一旦调用，isDirty将会自定设置为false
 */
public boolean queryDirty(){
	boolean ret;
	ret=isDirty();
	setDirty(false);
	Log.i(TAG, "queryDirty-->isDirty:"+String.valueOf(isDirty));
	return ret;
}
}
