package com.webapp.user.bean;

import com.webapp.common.bean.AbstractBean;
import com.webapp.common.bean.DataTypeTrans;

//用户bean
public class UserCheckInHisBean extends AbstractBean {
	public UserCheckInHisBean() {
		getTableName().append("USER_CHECKIN_HIS");
		getKeyCols().add("HIS_ID");
	}

	public long getHisId() {
		return DataTypeTrans.transToLong(getAttrValue("HIS_ID"));
	}

	public void setHisId(long value) {
		setAttrValue("HIS_ID", value);
	}

	public long getUserId() {
		return DataTypeTrans.transToLong(getAttrValue("USER_ID"));
	}

	public void setUserId(long userId) {
		setAttrValue("USER_ID", userId);
	}

	
	public String getCreateDate() {
		return DataTypeTrans.transToString(getAttrValue("CREATE_DATE"));
	}

	public void setCreateDate(String createDate) {
		setAttrValue("CREATE_DATE", createDate);
	}

}
