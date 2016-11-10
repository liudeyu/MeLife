package com.gzmelife.app.bean;

import java.io.Serializable;

@SuppressWarnings("serial")
public class UserInfoBean implements Serializable {
	private String id;
	private String userName;
	private String nickName;
	private String logoPath;
	private String autograph;
	private String sex;
	private String email;
	private String createTime;
	private String contacter;
	private String mobilePhone;
	private String contacterProvinceName;
	private String contacterCityName;
	private String contacterDistrictName;
	private String contacterAddress;
	private String birthProvinceId;
	private String birthCityId;
	private String birthProvinceName;
	private String birthCityName;
	private String dwellingProvinceId;
	private String dwellingCityId;
	private String dwellingDistrictId;
	private String dwellingProvinceName;
	private String dwellingCityName;
	private String dwellingDistrictName;
	private String dwellingAddress;
	private String cuisineMenuCategoryIds; //菜系ids (多个id用英文逗号分隔)
	private String cuisineMenuCategory; // 菜系 (多个菜系用英文逗号分隔)
	private String flavorMenuCategoryIds; // 口味ids (多个id用英文逗号分隔)
	private String flavorMenuCategory; // 口味 (多个口味用英文逗号分隔)
	private String password;
	private String type;
	private String platform;
	private String validCode;
	private String equipments;
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getDwellingProvinceId() {
		return dwellingProvinceId;
	}
	public void setDwellingProvinceId(String dwellingProvinceId) {
		this.dwellingProvinceId = dwellingProvinceId;
	}
	public String getDwellingCityId() {
		return dwellingCityId;
	}
	public void setDwellingCityId(String dwellingCityId) {
		this.dwellingCityId = dwellingCityId;
	}
	public String getDwellingDistrictId() {
		return dwellingDistrictId;
	}
	public void setDwellingDistrictId(String dwellingDistrictId) {
		this.dwellingDistrictId = dwellingDistrictId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getPlatform() {
		return platform;
	}
	public void setPlatform(String platform) {
		this.platform = platform;
	}
	public String getValidCode() {
		return validCode;
	}
	public void setValidCode(String validCode) {
		this.validCode = validCode;
	}
	public String getBirthProvinceId() {
		return birthProvinceId;
	}
	public void setBirthProvinceId(String birthProvinceId) {
		this.birthProvinceId = birthProvinceId;
	}
	public String getBirthCityId() {
		return birthCityId;
	}
	public void setBirthCityId(String birthCityId) {
		this.birthCityId = birthCityId;
	}
	public String getEquipments() {
		return equipments;
	}
	public void setEquipments(String equipments) {
		this.equipments = equipments;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getLogoPath() {
		return logoPath;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public void setLogoPath(String logoPath) {
		this.logoPath = logoPath;
	}
	public String getAutograph() {
		return autograph;
	}
	public void setAutograph(String autograph) {
		this.autograph = autograph;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getContacter() {
		return contacter;
	}
	public void setContacter(String contacter) {
		this.contacter = contacter;
	}
	public String getMobilePhone() {
		return mobilePhone;
	}
	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}
	public String getContacterProvinceName() {
		return contacterProvinceName;
	}
	public void setContacterProvinceName(String contacterProvinceName) {
		this.contacterProvinceName = contacterProvinceName;
	}
	public String getContacterCityName() {
		return contacterCityName;
	}
	public void setContacterCityName(String contacterCityName) {
		this.contacterCityName = contacterCityName;
	}
	public String getContacterDistrictName() {
		return contacterDistrictName;
	}
	public void setContacterDistrictName(String contacterDistrictName) {
		this.contacterDistrictName = contacterDistrictName;
	}
	public String getContacterAddress() {
		return contacterAddress;
	}
	public void setContacterAddress(String contacterAddress) {
		this.contacterAddress = contacterAddress;
	}
	public String getBirthProvinceName() {
		return birthProvinceName;
	}
	public void setBirthProvinceName(String birthProvinceName) {
		this.birthProvinceName = birthProvinceName;
	}
	public String getBirthCityName() {
		return birthCityName;
	}
	public void setBirthCityName(String birthCityName) {
		this.birthCityName = birthCityName;
	}
	public String getDwellingProvinceName() {
		return dwellingProvinceName;
	}
	public void setDwellingProvinceName(String dwellingProvinceName) {
		this.dwellingProvinceName = dwellingProvinceName;
	}
	public String getDwellingCityName() {
		return dwellingCityName;
	}
	public void setDwellingCityName(String dwellingCityName) {
		this.dwellingCityName = dwellingCityName;
	}
	public String getDwellingDistrictName() {
		return dwellingDistrictName;
	}
	public void setDwellingDistrictName(String dwellingDistrictName) {
		this.dwellingDistrictName = dwellingDistrictName;
	}
	public String getDwellingAddress() {
		return dwellingAddress;
	}
	public void setDwellingAddress(String dwellingAddress) {
		this.dwellingAddress = dwellingAddress;
	}
	public String getCuisineMenuCategoryIds() {
		return cuisineMenuCategoryIds;
	}
	public void setCuisineMenuCategoryIds(String cuisineMenuCategoryIds) {
		this.cuisineMenuCategoryIds = cuisineMenuCategoryIds;
	}
	public String getCuisineMenuCategory() {
		return cuisineMenuCategory;
	}
	public void setCuisineMenuCategory(String cuisineMenuCategory) {
		this.cuisineMenuCategory = cuisineMenuCategory;
	}
	public String getFlavorMenuCategoryIds() {
		return flavorMenuCategoryIds;
	}
	public void setFlavorMenuCategoryIds(String flavorMenuCategoryIds) {
		this.flavorMenuCategoryIds = flavorMenuCategoryIds;
	}
	public String getFlavorMenuCategory() {
		return flavorMenuCategory;
	}
	public void setFlavorMenuCategory(String flavorMenuCategory) {
		this.flavorMenuCategory = flavorMenuCategory;
	}
}
