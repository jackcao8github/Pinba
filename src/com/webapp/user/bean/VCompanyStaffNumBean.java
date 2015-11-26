package com.webapp.user.bean;

import com.webapp.common.bean.AbstractBean;
import com.webapp.common.bean.DataTypeTrans;

//用户bean
public class VCompanyStaffNumBean extends AbstractBean{
	public VCompanyStaffNumBean(){
		getTableName().append("v_company_staff_num");
	}
	
	public long getUserId() {
		return DataTypeTrans.transToLong(getAttrValue("USER_ID"));
	}
	public void setUserId(long userId) {
		setAttrValue("USER_ID",userId);
	}
	
	public long getStaffNum() {
		return DataTypeTrans.transToLong(getAttrValue("STAFF_NUM"));
	}
	public void setStaffNum(long userId) {
		setAttrValue("STAFF_NUM",userId);
	}
	
	
}
