package com.gzmelife.app.bean;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class FindTowMenuCategoryFirstBean implements Serializable {
	private int mfcId; // 一级分类id
	private String mfcName; // 一级分类名称
	private String mfcLogoPath;
	private List<FindTowMenuCategorySecondBean> secondCategorieList;

	public String getMfcLogoPath() {
		return mfcLogoPath;
	}

	public void setMfcLogoPath(String mfcLogoPath) {
		this.mfcLogoPath = mfcLogoPath;
	}

	public int getMfcId() {
		return mfcId;
	}

	public void setMfcId(int mfcId) {
		this.mfcId = mfcId;
	}

	public String getMfcName() {
		return mfcName;
	}

	public void setMfcName(String mfcName) {
		this.mfcName = mfcName;
	}

	public List<FindTowMenuCategorySecondBean> getSecondCategorieList() {
		return secondCategorieList;
	}

	public void setSecondCategorieList(
			List<FindTowMenuCategorySecondBean> secondCategorieList) {
		this.secondCategorieList = secondCategorieList;
	}

}
