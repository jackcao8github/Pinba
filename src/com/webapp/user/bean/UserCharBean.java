package com.webapp.user.bean;

import com.webapp.common.bean.AbstractBean;
import com.webapp.common.bean.DataTypeTrans;

//用户bean
public class UserCharBean extends AbstractBean{
	public UserCharBean(){
		getTableName().append("USER_CHAR");
		getKeyCols().add("USER_CHAR_ID");
	}
	
	public long getUserCharId() {
		return DataTypeTrans.transToLong(getAttrValue("USER_CHAR_ID"));
	}
	public void setUserCharId(long userId) {
		setAttrValue("USER_CHAR_ID",userId);
	}
	public long getUserId() {
		return DataTypeTrans.transToLong(getAttrValue("USER_ID"));
	}
	public void setUserId(long userId) {
		setAttrValue("USER_ID",userId);
	}
	
	public long getCharId() {
		return DataTypeTrans.transToLong(getAttrValue("CHAR_ID"));
	}
	public void setCharId(long userId) {
		setAttrValue("CHAR_ID",userId);
	}
	
	public String getValue() {
		return DataTypeTrans.transToString(getAttrValue("VALUE"));
	}
	public void setValue(String userName) {
		setAttrValue("VALUE",userName);
	}
	public String getCreateDate() {
		return DataTypeTrans.transToString(getAttrValue("CREATE_DATE"));
	}
	public void setCreateDate(String createDate) {
		setAttrValue("CREATE_DATE",createDate);
	}
	
}
