package com.webapp.user.bean;

import com.webapp.common.bean.AbstractBean;
import com.webapp.common.bean.DataTypeTrans;

//用户bean
public class UserResumeCharBean extends AbstractBean{
	public UserResumeCharBean(){
		getTableName().append("USER_RESUME_CHAR");
		getKeyCols().add("RESUME_CHAR_ID");
	}
	
	public long getResumeId() {
		return DataTypeTrans.transToLong(getAttrValue("RESUME_ID"));
	}
	public void setResumeId(long userId) {
		setAttrValue("RESUME_ID",userId);
	}
	public long getResumeCharId() {
		return DataTypeTrans.transToLong(getAttrValue("RESUME_CHAR_ID"));
	}
	public void setResumeCharId(long userId) {
		setAttrValue("RESUME_CHAR_ID",userId);
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
