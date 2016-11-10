package com.gzmelife.app.bean;

public class AuditInformationBean {
	private String id;
	private String cookbookName;
	private String date;
	private String auditResult;
	private String tip;
	private boolean isShowTip;
	
	public boolean isShowTip() {
		return isShowTip;
	}
	public void setShowTip(boolean isShowTip) {
		this.isShowTip = isShowTip;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCookbookName() {
		return cookbookName;
	}
	public void setCookbookName(String cookbookName) {
		this.cookbookName = cookbookName;
	}
	public String getAuditResult() {
		return auditResult;
	}
	public void setAuditResult(String auditResult) {
		this.auditResult = auditResult;
	}
	public String getTip() {
		return tip;
	}
	public void setTip(String tip) {
		this.tip = tip;
	}
}
