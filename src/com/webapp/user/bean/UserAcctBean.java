package com.webapp.user.bean;

import com.webapp.common.bean.AbstractBean;
import com.webapp.common.bean.DataTypeTrans;

//用户bean
public class UserAcctBean extends AbstractBean{
	public UserAcctBean(){
		getTableName().append("USER_ACCT");
		getKeyCols().add("ACCT_ID");
	}
	
	public long getAcctId() {
		return DataTypeTrans.transToLong(getAttrValue("ACCT_ID"));
	}
	public void setAcctId(long userId) {
		setAttrValue("ACCT_ID",userId);
	}
	public long getUserId() {
		return DataTypeTrans.transToLong(getAttrValue("USER_ID"));
	}
	public void setUserId(long userId) {
		setAttrValue("USER_ID",userId);
	}
	public String getAcctType() {
		return DataTypeTrans.transToString(getAttrValue("ACCT_TYPE"));
	}
	public void setAcctType(String userName) {
		setAttrValue("ACCT_TYPE",userName);
	}
	
	public long getValue() {
		return DataTypeTrans.transToLong(getAttrValue("VALUE"));
	}
	public void setValue(long value) {
		setAttrValue("VALUE",value);
	}
	public String getCreateDate() {
		return DataTypeTrans.transToString(getAttrValue("CREATE_DATE"));
	}
	public void setCreateDate(String createDate) {
		setAttrValue("CREATE_DATE",createDate);
	}
	
}
