package com.beifengorm.tableinfo;

import java.util.List;

public class TableInfo {

	private String classname;
	private String tablename;
	private Id id;
	private List<Property> plist;
	private List<OnetoMany> onelist;
	private List<ManytoOne> manylist;
	public String getClassname() {
		return classname;
	}
	public void setClassname(String classname) {
		this.classname = classname;
	}
	public String getTablename() {
		return tablename;
	}
	public void setTablename(String tablename) {
		this.tablename = tablename;
	}
	public Id getId() {
		return id;
	}
	public void setId(Id id) {
		this.id = id;
	}
	public List<Property> getPlist() {
		return plist;
	}
	public void setPlist(List<Property> plist) {
		this.plist = plist;
	}
	public List<OnetoMany> getOnelist() {
		return onelist;
	}
	public void setOnelist(List<OnetoMany> onelist) {
		this.onelist = onelist;
	}
	public List<ManytoOne> getManylist() {
		return manylist;
	}
	public void setManylist(List<ManytoOne> manylist) {
		this.manylist = manylist;
	}
}
