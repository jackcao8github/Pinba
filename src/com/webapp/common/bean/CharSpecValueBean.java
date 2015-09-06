package com.webapp.common.bean;

import com.webapp.common.bean.AbstractBean;
import com.webapp.common.bean.DataTypeTrans;

//用户bean
public class CharSpecValueBean extends AbstractBean{
	public CharSpecValueBean(){
		getTableName().append("CHAR_SPEC_VALUE");
		getKeyCols().add("CHAR_VALUE_ID");
	}
	
	public long getCharSpecId() {
		return DataTypeTrans.transToLong(getAttrValue("CHAR_SPEC_ID"));
	}
	public void setCharSpecId(long userId) {
		setAttrValue("CHAR_SPEC_ID",userId);
	}
	public long getCharValueId() {
		return DataTypeTrans.transToLong(getAttrValue("CHAR_VALUE_ID"));
	}
	public void setCharValueId(long userId) {
		setAttrValue("CHAR_VALUE_ID",userId);
	}
	public String getDisplayValue() {
		return DataTypeTrans.transToString(getAttrValue("DISPLAY_VALUE"));
	}
	public void setDisplayValue(String userName) {
		setAttrValue("DISPLAY_VALUE",userName);
	}
	public String getRealValue() {
		return DataTypeTrans.transToString(getAttrValue("REAL_VALUE"));
	}
	public void setRealValue(String userName) {
		setAttrValue("REAL_VALUE",userName);
	}
	
	
	public String getCreateDate() {
		return DataTypeTrans.transToString(getAttrValue("CREATE_DATE"));
	}
	public void setCreateDate(String createDate) {
		setAttrValue("CREATE_DATE",createDate);
	}
	
}
