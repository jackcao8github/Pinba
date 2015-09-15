package com.webapp.user.bean;

import com.webapp.common.bean.AbstractBean;
import com.webapp.common.bean.DataTypeTrans;

//用户bean
public class UserResumeSendHisBean extends AbstractBean {
	public UserResumeSendHisBean() {
		getTableName().append("USER_RESUME_SENDHIS");
		getKeyCols().add("SENDHIS_ID");
	}

	public long getSendHisId() {
		return DataTypeTrans.transToLong(getAttrValue("SENDHIS_ID"));
	}

	public void setSendHisId(long userId) {
		setAttrValue("SENDHIS_ID", userId);
	}

	public long getResumeId() {
		return DataTypeTrans.transToLong(getAttrValue("RESUME_ID"));
	}

	public void setResumeId(long userId) {
		setAttrValue("RESUME_ID", userId);
	}

	public long getProdId() {
		return DataTypeTrans.transToLong(getAttrValue("PROD_ID"));
	}

	public void setProdId(long userId) {
		setAttrValue("PROD_ID", userId);
	}

	public String getCreateDate() {
		return DataTypeTrans.transToString(getAttrValue("CREATE_DATE"));
	}

	public void setCreateDate(String createDate) {
		setAttrValue("CREATE_DATE", createDate);
	}

}
