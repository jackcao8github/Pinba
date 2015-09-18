package com.webapp.user.bean;

import com.webapp.common.bean.AbstractBean;
import com.webapp.common.bean.DataTypeTrans;

//用户bean
public class UserPositionBean extends AbstractBean {
	public UserPositionBean() {
		getTableName().append("USER_POSITION");
		getKeyCols().add("POSITION_ID");
	}

	public long getPositionId() {
		return DataTypeTrans.transToLong(getAttrValue("POSITION_ID"));
	}

	public void setPositionId(long userId) {
		setAttrValue("POSITION_ID", userId);
	}

	public long getUserId() {
		return DataTypeTrans.transToLong(getAttrValue("USER_ID"));
	}

	public void setUserId(long userId) {
		setAttrValue("USER_ID", userId);
	}

	public String getLatitude() {
		return DataTypeTrans.transToString(getAttrValue("LATITUDE"));
	}

	public void setLatitude(String userId) {
		setAttrValue("LATITUDE", userId);
	}

	public String getLongitude() {
		return DataTypeTrans.transToString(getAttrValue("LONGITUDE"));
	}

	public void setLongitude(String userId) {
		setAttrValue("LONGITUDE", userId);
	}
	public String getCityName() {
		return DataTypeTrans.transToString(getAttrValue("CITY_NAME"));
	}

	public void setCityName(String userId) {
		setAttrValue("CITY_NAME", userId);
	}
	public String getCreateDate() {
		return DataTypeTrans.transToString(getAttrValue("CREATE_DATE"));
	}

	public void setCreateDate(String createDate) {
		setAttrValue("CREATE_DATE", createDate);
	}

}
