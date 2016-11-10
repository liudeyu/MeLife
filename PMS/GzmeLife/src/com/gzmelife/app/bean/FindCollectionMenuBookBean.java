package com.gzmelife.app.bean;

public class FindCollectionMenuBookBean {

	private String id;
	private String name;
	private String logoPath;
	private String createTime;
	private String userCollectionId;

	public String getUserCollectionId() {
		return userCollectionId;
	}

	public void setUserCollectionId(String userCollectionId) {
		this.userCollectionId = userCollectionId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLogoPath() {
		return logoPath;
	}

	public void setLogoPath(String logoPath) {
		this.logoPath = logoPath;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

}
