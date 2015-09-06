package com.webapp.user.bean;

import com.webapp.common.bean.AbstractBean;
import com.webapp.common.bean.DataTypeTrans;

//用户bean
public class UserBean extends AbstractBean{
	public UserBean(){
		getTableName().append("USER");
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
	public String getCreateDate() {
		return DataTypeTrans.transToString(getAttrValue("CREATE_DATE"));
	}
	public void setCreateDate(String createDate) {
		setAttrValue("CREATE_DATE",createDate);
	}
	public String getAuthState() {
		return DataTypeTrans.transToString(getAttrValue("USER_AUTHSTATE"));
	}
	public void setAuthState(String authState) {
		setAttrValue("USER_AUTHSTATE",authState);
	}
	public String getPassword() {
		return DataTypeTrans.transToString(getAttrValue("PASSWORD"));
	}
	public void setPassword(String password) {
		setAttrValue("PASSWORD",password);
	}
	public String getUserCode() {
		return DataTypeTrans.transToString(getAttrValue("USER_CODE"));
	}
	public void setUserCode(String UserCode) {
		setAttrValue("USER_CODE",UserCode);
	}
	public long getUserCredit() {
		return DataTypeTrans.transToLong(getAttrValue("USER_CREDIT"));
	}
	public void setUserCredit(long UserCredit) {
		setAttrValue("USER_CREDIT",UserCredit);
	}
	
	public String getUserLevel() {
		return DataTypeTrans.transToString(getAttrValue("USER_LEVEL"));
	}
	public void setUserLevel(String UserCredit) {
		setAttrValue("USER_LEVEL",UserCredit);
	}
	
	public String getToken() {
		return DataTypeTrans.transToString(getAttrValue("TOKEN"));
	}
	public void setToken(String UserCredit) {
		setAttrValue("TOKEN",UserCredit);
	}
}
