package com.gzmelife.app.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name = "localFoodMaterialLevelThree")
public class SerachFoodBean {
	@Column(isId = true, name = "id")
	private int id;
	@Column(name = "name")
	private String name;
	@Column(name = "weight")
	private int weight;
	@Column(name = "pid")
	private int c_id;
	private String c_name;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
	public int getC_id() {
		return c_id;
	}
	public void setC_id(int c_id) {
		this.c_id = c_id;
	}
	public String getC_name() {
		return c_name;
	}
	public void setC_name(String c_name) {
		this.c_name = c_name;
	}
	

	
}
