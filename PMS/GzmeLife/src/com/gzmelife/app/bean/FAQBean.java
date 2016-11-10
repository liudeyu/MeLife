package com.gzmelife.app.bean;

public class FAQBean {
	private String id;
	private String errorNumber;
	private String errorName;
	private String content;
	
	public String getErrorNumber() {
		return errorNumber;
	}
	public void setErrorNumber(String errorNumber) {
		this.errorNumber = errorNumber;
	}
	public String getErrorName() {
		return errorName;
	}
	public void setErrorName(String errorName) {
		this.errorName = errorName;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
}
