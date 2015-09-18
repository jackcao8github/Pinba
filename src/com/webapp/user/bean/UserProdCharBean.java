package com.webapp.user.bean;

import com.webapp.common.bean.AbstractBean;
import com.webapp.common.bean.DataTypeTrans;

//用户bean
public class UserProdCharBean extends AbstractBean{
	public UserProdCharBean(){
		getTableName().append("USER_PROD_CHAR");
		getKeyCols().add("USER_PROD_CHAR_ID");
	}
	
	public long getUserProdId() {
		return DataTypeTrans.transToLong(getAttrValue("USER_PROD_ID"));
	}
	public void setUserProdId(long userId) {
		setAttrValue("USER_PROD_ID",userId);
	}
	public long getUserProdCharId() {
		return DataTypeTrans.transToLong(getAttrValue("USER_PROD_CHAR_ID"));
	}
	public void setUserProdCharId(long userId) {
		setAttrValue("USER_PROD_CHAR_ID",userId);
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
