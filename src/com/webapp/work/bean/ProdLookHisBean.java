package com.webapp.work.bean;

import com.webapp.common.bean.AbstractBean;
import com.webapp.common.bean.DataTypeTrans;

//用户bean
public class ProdLookHisBean extends AbstractBean{
	public ProdLookHisBean(){
		getTableName().append("PROD_LOOKHIS");
		getKeyCols().add("LOOKHIS_ID");
	}
	
	public long getLookHisId() {
		return DataTypeTrans.transToLong(getAttrValue("LOOKHIS_ID"));
	}
	public void setLookHisId(long userId) {
		setAttrValue("LOOKHIS_ID",userId);
	}
	public long getUserId() {
		return DataTypeTrans.transToLong(getAttrValue("USER_ID"));
	}
	public void setUserId(long userId) {
		setAttrValue("USER_ID",userId);
	}
	
	public long getProdId() {
		return DataTypeTrans.transToLong(getAttrValue("PROD_ID"));
	}
	public void setProdId(long userId) {
		setAttrValue("PROD_ID",userId);
	}
	
	public String getCreateDate() {
		return DataTypeTrans.transToString(getAttrValue("CREATE_DATE"));
	}
	public void setCreateDate(String createDate) {
		setAttrValue("CREATE_DATE",createDate);
	}
	
}
