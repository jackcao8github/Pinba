package com.webapp.work.bean;

import com.webapp.common.bean.AbstractBean;
import com.webapp.common.bean.DataTypeTrans;

//用户bean
public class ProdBean extends AbstractBean{
	public ProdBean(){
		getTableName().append("PROD");
		getKeyCols().add("PROD_ID");
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
	public String getProdType() {
		return DataTypeTrans.transToString(getAttrValue("PROD_TYPE"));
	}
	public String getEffDate() {
		return DataTypeTrans.transToString(getAttrValue("EFF_DATE"));
	}
	public String getExpDate() {
		return DataTypeTrans.transToString(getAttrValue("EXP_DATE"));
	}
	public String getCreateDate() {
		return DataTypeTrans.transToString(getAttrValue("CREATE_DATE"));
	}
	public String getProdName() {
		return DataTypeTrans.transToString(getAttrValue("PROD_NAME"));
	}
	public String getContent() {
		return DataTypeTrans.transToString(getAttrValue("CONTENT"));
	}
	public void setProdId(long prodId) {
		setAttrValue("PROD_ID",prodId);
	}
	public void setProdType(String prodType) {
		setAttrValue("PROD_TYPE",prodType);
	}
	public void setEffDate(String effDate) {
		setAttrValue("EFF_DATE",effDate);
	}
	public void setExpDate(String expDate) {
		setAttrValue("EXP_DATE",expDate);
	}
	public void setCreateDate(String createDate) {
		setAttrValue("CREATE_DATE",createDate);
	}
	public void setProdName(String title) {
		setAttrValue("PROD_NAME",title);
	}
	public void setContent(String content) {
		setAttrValue("CONTENT",content);
	}
	
}
