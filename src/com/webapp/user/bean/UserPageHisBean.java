package com.webapp.user.bean;

import com.webapp.common.bean.AbstractBean;
import com.webapp.common.bean.DataTypeTrans;

//用户bean
public class UserPageHisBean extends AbstractBean {
	public UserPageHisBean() {
		getTableName().append("USER_PAGE_HIS");
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

	public String getNewPageId() {
		return DataTypeTrans.transToString(getAttrValue("NEW_PAGE_ID"));
	}

	public void setNewPageId(String userId) {
		setAttrValue("NEW_PAGE_ID", userId);
	}

	public String getOldPageId() {
		return DataTypeTrans.transToString(getAttrValue("OLD_PAGE_ID"));
	}

	public void setOldPageId(String userId) {
		setAttrValue("OLD_PAGE_ID", userId);
	}
	public String getCreateDate() {
		return DataTypeTrans.transToString(getAttrValue("CREATE_DATE"));
	}

	public void setCreateDate(String createDate) {
		setAttrValue("CREATE_DATE", createDate);
	}

}
