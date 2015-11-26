package com.webapp.user.bean;

import com.webapp.common.bean.AbstractBean;
import com.webapp.common.bean.DataTypeTrans;

//公司线下发工资统计
public class VOfflinePayStatBean extends AbstractBean{
	public VOfflinePayStatBean(){
		getTableName().append("v_offline_pay_stat");
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
