package com.webapp.user.bean;

import com.webapp.common.bean.AbstractBean;
import com.webapp.common.bean.DataTypeTrans;

//用户bean
public class UserFeelingBean extends AbstractBean{
	public UserFeelingBean(){
		getTableName().append("USER_FEELING");
		getKeyCols().add("FEELING_ID");
	}
	
	public long getFeelingId() {
		return DataTypeTrans.transToLong(getAttrValue("FEELING_ID"));
	}
	public void setFeelingId(long userId) {
		setAttrValue("FEELING_ID",userId);
	}
	public long getUserId() {
		return DataTypeTrans.transToLong(getAttrValue("USER_ID"));
	}
	public void setUserId(long userId) {
		setAttrValue("USER_ID",userId);
	}
	public long getImageId() {
		return DataTypeTrans.transToLong(getAttrValue("FEELING_IMAGE"));
	}
	public void setImageId(long userId) {
		setAttrValue("FEELING_IMAGE",userId);
	}
	public String getFeelingText() {
		return DataTypeTrans.transToString(getAttrValue("FEELING_TEXT"));
	}
	public void setFeelingText(String userName) {
		setAttrValue("FEELING_TEXT",userName);
	}
	public String getCreateDate() {
		return DataTypeTrans.transToString(getAttrValue("CREATE_DATE"));
	}
	public void setCreateDate(String createDate) {
		setAttrValue("CREATE_DATE",createDate);
	}
	
}
