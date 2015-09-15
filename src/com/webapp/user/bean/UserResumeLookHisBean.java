package com.webapp.user.bean;

import com.webapp.common.bean.AbstractBean;
import com.webapp.common.bean.DataTypeTrans;

//用户bean
public class UserResumeLookHisBean extends AbstractBean{
	public UserResumeLookHisBean(){
		getTableName().append("USER_RESUME_LOOKHIS");
		getKeyCols().add("LOOKHIS_ID");
	}
	
	public long getLookHisId() {
		return DataTypeTrans.transToLong(getAttrValue("LOOKHIS_ID"));
	}
	public void setLookHisId(long userId) {
		setAttrValue("LOOKHIS_ID",userId);
	}
	public long getUserId() {
		return DataTypeTrans.transToLong(getAttrValue("USER_ID"));
	}
	public void setUserId(long userId) {
		setAttrValue("USER_ID",userId);
	}
	
	public long getResumeId() {
		return DataTypeTrans.transToLong(getAttrValue("RESUME_ID"));
	}
	public void setResumeId(long userId) {
		setAttrValue("RESUME_ID",userId);
	}
	
	public String getCreateDate() {
		return DataTypeTrans.transToString(getAttrValue("CREATE_DATE"));
	}
	public void setCreateDate(String createDate) {
		setAttrValue("CREATE_DATE",createDate);
	}
	
}
