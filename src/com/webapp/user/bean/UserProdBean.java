package com.webapp.user.bean;

import com.webapp.common.bean.AbstractBean;
import com.webapp.common.bean.DataTypeTrans;

//用户bean
public class UserProdBean extends AbstractBean {
	public UserProdBean() {
		getTableName().append("USER_PROD");
		getKeyCols().add("USER_PROD_ID");
	}

	public long getUserProdId() {
		return DataTypeTrans.transToLong(getAttrValue("USER_PROD_ID"));
	}

	public void setUserProdId(long userId) {
		setAttrValue("USER_PROD_ID", userId);
	}

	public long getUserId() {
		return DataTypeTrans.transToLong(getAttrValue("USER_ID"));
	}

	public void setUserId(long userId) {
		setAttrValue("USER_ID", userId);
	}

	public long getProdId() {
		return DataTypeTrans.transToLong(getAttrValue("PROD_ID"));
	}

	public void setProdId(long userId) {
		setAttrValue("PROD_ID", userId);
	}

	public String getState() {
		return DataTypeTrans.transToString(getAttrValue("STATE"));
	}

	public void setState(String createDate) {
		setAttrValue("STATE", createDate);
	}

	public String getCreateDate() {
		return DataTypeTrans.transToString(getAttrValue("CREATE_DATE"));
	}

	public void setCreateDate(String createDate) {
		setAttrValue("CREATE_DATE", createDate);
	}
	public String getEffDate() {
		return DataTypeTrans.transToString(getAttrValue("EFF_DATE"));
	}

	public void setEffDate(String createDate) {
		setAttrValue("EFF_DATE", createDate);
	}
	
	public String getExpDate() {
		return DataTypeTrans.transToString(getAttrValue("EXP_DATE"));
	}

	public void setExpDate(String createDate) {
		setAttrValue("EXP_DATE", createDate);
	}
}
