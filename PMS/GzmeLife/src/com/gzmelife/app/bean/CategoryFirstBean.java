package com.gzmelife.app.bean;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class CategoryFirstBean implements Serializable {
	private int fcId; // 一级分类id
	private String fcName; // 一级分类名称
	private List<CategorySecondBean> secondCategorieList;
	
	public int getFcId() {
		return fcId;
	}
	public void setFcId(int fcId) {
		this.fcId = fcId;
	}
	public String getFcName() {
		return fcName;
	}
	public void setFcName(String fcName) {
		this.fcName = fcName;
	}
	public List<CategorySecondBean> getSecondCategorieList() {
		return secondCategorieList;
	}
	public void setSecondCategorieList(List<CategorySecondBean> secondCategorieList) {
		this.secondCategorieList = secondCategorieList;
	}
}
