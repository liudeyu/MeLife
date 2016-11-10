package com.gzmelife.app.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name = "deviceNameAndIPBean")
public class DeviceNameAndIPBean {
	@Column(isId = true, name = "id")
	private int id;
	@Column(name = "name")
	private String name;
	@Column(name = "wifiName")
	private String wifiName;
	@Column(name = "ip")
	private String ip;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getWifiName() {
		return wifiName;
	}
	public void setWifiName(String wifiName) {
		this.wifiName = wifiName;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "id:" + id + ";name:" + name + ";wifiName:" + wifiName + ";ip:" + ip;
	}	
	
	public boolean isSame(DeviceNameAndIPBean b) {
		return name.equals(b.getName())
			&& wifiName.equals(b.getWifiName()) && ip.equals(b.getIp());
	}
	
	/** PMS名字和wifi名字一样，但是PMS的ip不一样 */ 
	public boolean isSameNameAndWifiName(DeviceNameAndIPBean b) {
		return name.equals(b.getName()) && wifiName.equals(b.getWifiName()) && !ip.equals(b.getIp());
	}
}
