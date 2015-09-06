package com.webapp.user.bean;

import com.webapp.common.bean.AbstractBean;
import com.webapp.common.bean.DataTypeTrans;

//用户bean
public class UserAcctHisBean extends AbstractBean{
	public UserAcctHisBean(){
		getTableName().append("USER_ACCT_HIS");
		getKeyCols().add("ACCT_HISID");
	}
	
	public long getAcctId() {
		return DataTypeTrans.transToLong(getAttrValue("ACCT_ID"));
	}
	public void setAcctId(long userId) {
		setAttrValue("ACCT_ID",userId);
	}
	public long getAcctHisId() {
		return DataTypeTrans.transToLong(getAttrValue("ACCT_HISID"));
	}
	public void setAcctHisId(long userId) {
		setAttrValue("ACCT_HISID",userId);
	}
	public String getValueOPType() {
		return DataTypeTrans.transToString(getAttrValue("VALUE_OPTYPE"));
	}
	public void setValueOPType(String userName) {
		setAttrValue("VALUE_OPTYPE",userName);
	}
	public String getRemark() {
		return DataTypeTrans.transToString(getAttrValue("REMARK"));
	}
	public void setRemark(String userName) {
		setAttrValue("REMARK",userName);
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
