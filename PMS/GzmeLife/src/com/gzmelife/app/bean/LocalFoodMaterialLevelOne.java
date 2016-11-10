package com.gzmelife.app.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name = "localFoodMaterialLevelOne")
/**
 * 标准食材库一级
 */
public class LocalFoodMaterialLevelOne {
	@Column(isId = true, name = "id")
	private int id;
	@Column(name = "name")
	private String name;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public LocalFoodMaterialLevelOne setName(String name) {
		this.name = name;
		return this;
	}
}
