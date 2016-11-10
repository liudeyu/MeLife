package com.gzmelife.app.bean;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class GoodFoodFirstBean implements Serializable {
	private int fcId; // 一级分类id
	private String fcName; // 一级分类名称
	private List<GoodFoodSecondBean> foodStoreList;
	
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
	public List<GoodFoodSecondBean> getSecondCategorieList() {
		return foodStoreList;
	}
	public void setSecondCategorieList(List<GoodFoodSecondBean> foodStoreList) {
		this.foodStoreList = foodStoreList;
	}
}
