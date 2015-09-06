package com.webapp.common.bean;

//bean属性取值
public class AttrValue {
	//初始值
	private Object orgValue = null;
	//修改后值
	private Object newValue = null;
	
	public AttrValue(Object newValue){
		this.newValue=newValue;
	}
	public Object getOrgValue() {
		return orgValue;
	}
	public void setOrgValue(Object orgValue) {
		this.orgValue = orgValue;
	}
	public Object getNewValue() {
		return newValue;
	}
	public void setNewValue(Object newValue) {
		if (this.newValue==null){
			this.newValue = newValue;
		}else{
			//如果原来有值，则把旧值转移到orgValue中
			if (this.orgValue==null){
				this.orgValue = this.newValue;
			}
			this.newValue=newValue;
		}
	}
}
