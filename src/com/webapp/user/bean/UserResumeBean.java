package com.webapp.user.bean;

import com.webapp.common.bean.AbstractBean;
import com.webapp.common.bean.DataTypeTrans;

//用户bean
public class UserResumeBean extends AbstractBean{
	public UserResumeBean(){
		getTableName().append("USER_RESUME");
		getKeyCols().add("RESUME_ID");
	}
	
	public long getResumeId() {
		return DataTypeTrans.transToLong(getAttrValue("RESUME_ID"));
	}
	public void setResumeId(long userId) {
		setAttrValue("RESUME_ID",userId);
	}
	public long getUserId() {
		return DataTypeTrans.transToLong(getAttrValue("USER_ID"));
	}
	public void setUserId(long userId) {
		setAttrValue("USER_ID",userId);
	}
	public String getResumeName() {
		return DataTypeTrans.transToString(getAttrValue("RESUME_NAME"));
	}
	public void setResumeName(String userName) {
		setAttrValue("RESUME_NAME",userName);
	}
	public String getResumeType() {
		return DataTypeTrans.transToString(getAttrValue("RESUME_TYPE"));
	}
	public void setResumeType(String userType) {
		setAttrValue("RESUME_TYPE",userType);
	}
	public String getCreateDate() {
		return DataTypeTrans.transToString(getAttrValue("CREATE_DATE"));
	}
	public void setCreateDate(String createDate) {
		setAttrValue("CREATE_DATE",createDate);
	}
	
}
