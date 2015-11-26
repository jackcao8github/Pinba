package com.webapp.user.bean;

import com.webapp.common.bean.AbstractBean;
import com.webapp.common.bean.DataTypeTrans;

//用户参加过的工作数量
public class VUserWorkStatBean extends AbstractBean{
	public VUserWorkStatBean(){
		getTableName().append("v_user_work_stat");
	}
	
	public long getUserId() {
		return DataTypeTrans.transToLong(getAttrValue("USER_ID"));
	}
	public void setUserId(long userId) {
		setAttrValue("USER_ID",userId);
	}
	
	public long getWorkNum() {
		return DataTypeTrans.transToLong(getAttrValue("WORK_NUM"));
	}
	public void setWorkNum(long userId) {
		setAttrValue("WORK_NUM",userId);
	}
}
