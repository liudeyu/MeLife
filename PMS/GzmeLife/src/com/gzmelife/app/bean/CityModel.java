package com.gzmelife.app.bean;

import java.util.List;

public class CityModel  extends AddressBaseBean {
	private List<DistrictModel> children;

	public CityModel() {
		super();
	}

	public CityModel(String id, String name, List<DistrictModel> districtList) {
		super();
		this.id = id;
		this.name = name;
		this.children = districtList;
	}

	public List<DistrictModel> getDistrictList() {
		return children;
	}

	public void setDistrictList(List<DistrictModel> districtList) {
		this.children = districtList;
	}

}
