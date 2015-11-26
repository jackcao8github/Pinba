package com.webapp.user.bean;

import com.webapp.common.bean.AbstractBean;
import com.webapp.common.bean.DataTypeTrans;

//用户线下收入统计
public class VUserOfflineWageStatBean extends AbstractBean{
	public VUserOfflineWageStatBean(){
		getTableName().append("v_user_offline_wage_stat");
	}
	
	public long getUserId() {
		return DataTypeTrans.transToLong(getAttrValue("USER_ID"));
	}
	public void setUserId(long userId) {
		setAttrValue("USER_ID",userId);
	}
	
	public double getAmount() {
		return DataTypeTrans.transToDouble(getAttrValue("AMOUNT"));
	}
	public void setAmount(double value) {
		setAttrValue("AMOUNT",value);
	}
}
