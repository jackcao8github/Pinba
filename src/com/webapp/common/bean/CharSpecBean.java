package com.webapp.common.bean;

import com.webapp.common.bean.AbstractBean;
import com.webapp.common.bean.DataTypeTrans;

//用户bean
public class CharSpecBean extends AbstractBean{
	public CharSpecBean(){
		getTableName().append("CHAR_SPEC");
		getKeyCols().add("CHAR_SPEC_ID");
	}
	
	public long getCharSpecId() {
		return DataTypeTrans.transToLong(getAttrValue("CHAR_SPEC_ID"));
	}
	public void setCharSpecId(long userId) {
		setAttrValue("CHAR_SPEC_ID",userId);
	}
	
	public String getName() {
		return DataTypeTrans.transToString(getAttrValue("NAME"));
	}
	public void setName(String userName) {
		setAttrValue("NAME",userName);
	}
	public String getCode() {
		return DataTypeTrans.transToString(getAttrValue("CODE"));
	}
	public void setCode(String userName) {
		setAttrValue("CODE",userName);
	}
	
	public String getType() {
		return DataTypeTrans.transToString(getAttrValue("TYPE"));
	}
	public void setType(String userName) {
		setAttrValue("TYPE",userName);
	}
	
	public long getLength() {
		return DataTypeTrans.transToLong(getAttrValue("LENGTH"));
	}
	public void setLength(long userId) {
		setAttrValue("LENGTH",userId);
	}
	
	public String getCreateDate() {
		return DataTypeTrans.transToString(getAttrValue("CREATE_DATE"));
	}
	public void setCreateDate(String createDate) {
		setAttrValue("CREATE_DATE",createDate);
	}
	
}
