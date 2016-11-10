package com.gzmelife.app.bean;

import java.util.List;

public class ProvinceModel extends AddressBaseBean {
	private List<CityModel> children;

	public ProvinceModel() {
		super();
	}

	public ProvinceModel(String id, String name, List<CityModel> cityList) {
		super();
		this.id = id;
		this.name = name;
		this.children = cityList;
	}

	public List<CityModel> getCityList() {
		return children;
	}

	public void setCityList(List<CityModel> cityList) {
		this.children = cityList;
	}

}
