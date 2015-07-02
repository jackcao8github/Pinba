package com.xx.bean;

import java.sql.Date;

import com.xx.common.AbstractBean;
import com.xx.common.DataTypeTrans;

//用户bean
public class UserBean extends AbstractBean{
	public UserBean(){
		getTableName().append("T_USER");
		getKeyCols().add("USER_ID");
	}
	
	
	public long getUserId() {
		return DataTypeTrans.transToLong(getAttrValue("USER_ID"));
	}
	public void setUserId(long userId) {
		setAttrValue("USER_ID",userId);
	}
	public String getUserName() {
		return DataTypeTrans.transToString(getAttrValue("USER_NAME"));
	}
	public void setUserName(String userName) {
		setAttrValue("USER_NAME",userName);
	}
	public String getUserType() {
		return DataTypeTrans.transToString(getAttrValue("USER_TYPE"));
	}
	public void setUserType(String userType) {
		setAttrValue("USER_TYPE",userType);
	}
	public Date getCreateDate() {
		return DataTypeTrans.transToDate(getAttrValue("CREATE_DATE"));
	}
	public void setCreateDate(Date createDate) {
		setAttrValue("CREATE_DATE",createDate);
	}
	public String getAuthState() {
		return DataTypeTrans.transToString(getAttrValue("AUTH_STATE"));
	}
	public void setAuthState(String authState) {
		setAttrValue("AUTH_STATE",authState);
	}
	public String getPassword() {
		return DataTypeTrans.transToString(getAttrValue("PASSWORD"));
	}
	public void setPassword(String password) {
		setAttrValue("PASSWORD",password);
	}
	public String getLoginCode() {
		return DataTypeTrans.transToString(getAttrValue("LOGIN_CODE"));
	}
	public void setLoginCode(String loginCode) {
		setAttrValue("LOGIN_CODE",loginCode);
	}
	public String getAuthMethod() {
		return DataTypeTrans.transToString(getAttrValue("AUTH_METHOD"));
	}
	public void setAuthMethod(String authMethod) {
		setAttrValue("AUTH_METHOD",authMethod);
	}
}
