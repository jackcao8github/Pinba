package com.webapp.user.bean;

import com.webapp.common.bean.AbstractBean;
import com.webapp.common.bean.DataTypeTrans;

//用户bean
public class UserFocusBean extends AbstractBean{
	public UserFocusBean(){
		getTableName().append("USER_FOCUS");
		getKeyCols().add("FOCUS_ID");
	}
	
	public long getFocusId() {
		return DataTypeTrans.transToLong(getAttrValue("FOCUS_ID"));
	}
	public void setFocusId(long userId) {
		setAttrValue("FOCUS_ID",userId);
	}
	public long getUserId() {
		return DataTypeTrans.transToLong(getAttrValue("USER_ID"));
	}
	public void setUserId(long userId) {
		setAttrValue("USER_ID",userId);
	}
	
	public long getProdId() {
		return DataTypeTrans.transToLong(getAttrValue("PROD_ID"));
	}
	public void setProdId(long userId) {
		setAttrValue("PROD_ID",userId);
	}
	
	public String getCreateDate() {
		return DataTypeTrans.transToString(getAttrValue("CREATE_DATE"));
	}
	public void setCreateDate(String createDate) {
		setAttrValue("CREATE_DATE",createDate);
	}
	
}
