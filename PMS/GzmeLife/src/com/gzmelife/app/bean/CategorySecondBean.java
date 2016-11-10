package com.gzmelife.app.bean;

import java.io.Serializable;

@SuppressWarnings("serial")
public class CategorySecondBean implements Serializable {
	private int scId; // 二级分类id
	private String scName; // 二级分类名称
	private String scLogoPath; // 二级分类图片
	
	public int getScId() {
		return scId;
	}
	public void setScId(int scId) {
		this.scId = scId;
	}
	public String getScName() {
		return scName;
	}
	public void setScName(String scName) {
		this.scName = scName;
	}
	public String getScLogoPath() {
		return scLogoPath;
	}
	public void setScLogoPath(String scLogoPath) {
		this.scLogoPath = scLogoPath;
	}
}
