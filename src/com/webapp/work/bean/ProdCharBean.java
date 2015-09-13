package com.webapp.work.bean;

import com.webapp.common.bean.AbstractBean;
import com.webapp.common.bean.DataTypeTrans;

//用户bean
public class ProdCharBean extends AbstractBean{
	public ProdCharBean(){
		getTableName().append("PROD_CHAR");
		getKeyCols().add("PROD_CHAR_ID");
	}
	
	public long getProdCharId() {
		return DataTypeTrans.transToLong(getAttrValue("PROD_CHAR_ID"));
	}
	public void setProdCharId(long userId) {
		setAttrValue("PROD_CHAR_ID",userId);
	}
	public long getProdId() {
		return DataTypeTrans.transToLong(getAttrValue("PROD_ID"));
	}
	public void setProdId(long userId) {
		setAttrValue("PROD_ID",userId);
	}
	
	public long getCharId() {
		return DataTypeTrans.transToLong(getAttrValue("CHAR_ID"));
	}
	public void setCharId(long userId) {
		setAttrValue("CHAR_ID",userId);
	}
	
	public String getValue() {
		return DataTypeTrans.transToString(getAttrValue("VALUE"));
	}
	public void setValue(String userName) {
		setAttrValue("VALUE",userName);
	}
	public String getCreateDate() {
		return DataTypeTrans.transToString(getAttrValue("CREATE_DATE"));
	}
	public void setCreateDate(String createDate) {
		setAttrValue("CREATE_DATE",createDate);
	}
	
}
