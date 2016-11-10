package com.gzmelife.app.bean;

import java.io.Serializable;

@SuppressWarnings("serial")
public class FindTowMenuCategorySecondBean implements Serializable {
	private int mscId; // 二级分类id
	private String mscName; // 二级分类名称
	private String mscLogoPath; // 二级分类图片
	private boolean isChecked;
	
	public boolean isChecked() {
		return isChecked;
	}
	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
	public int getMscId() {
		return mscId;
	}
	public void setMscId(int mscId) {
		this.mscId = mscId;
	}
	public String getMscName() {
		return mscName;
	}
	public void setMscName(String mscName) {
		this.mscName = mscName;
	}
	public String getMscLogoPath() {
		return mscLogoPath;
	}
	public void setMscLogoPath(String mscLogoPath) {
		this.mscLogoPath = mscLogoPath;
	}
	
	
}
