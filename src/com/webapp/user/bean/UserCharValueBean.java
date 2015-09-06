package com.webapp.user.bean;

import com.webapp.common.bean.AbstractBean;
import com.webapp.common.bean.DataTypeTrans;

//用户bean
public class UserCharValueBean extends AbstractBean{
	public UserCharValueBean(){
		getTableName().append("USER_CHAR_VALUE");
		getKeyCols().add("USER_CHAR_VALUE_ID");
	}
	
	public long getUserCharId() {
		return DataTypeTrans.transToLong(getAttrValue("USER_CHAR_ID"));
	}
	public void setUserCharId(long userId) {
		setAttrValue("USER_CHAR_ID",userId);
	}
	public long getUserCharValueId() {
		return DataTypeTrans.transToLong(getAttrValue("USER_CHAR_VALUE_ID"));
	}
	public void setUserCharValueId(long userId) {
		setAttrValue("USER_CHAR_VALUE_ID",userId);
	}
	
	public long getCharValueId() {
		return DataTypeTrans.transToLong(getAttrValue("CHAR_VALUE_ID"));
	}
	public void setCharValueId(long userId) {
		setAttrValue("CHAR_VALUE_ID",userId);
	}
	
	public String getCreateDate() {
		return DataTypeTrans.transToString(getAttrValue("CREATE_DATE"));
	}
	public void setCreateDate(String createDate) {
		setAttrValue("CREATE_DATE",createDate);
	}
	
}
