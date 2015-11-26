package com.webapp.user.bean;

import com.webapp.common.bean.AbstractBean;
import com.webapp.common.bean.DataTypeTrans;

//用户bean
public class VUserMsgBean extends AbstractBean{
	public VUserMsgBean(){
		getTableName().append("v_user_msg");
	}
	
	public long getUserId() {
		return DataTypeTrans.transToLong(getAttrValue("USER_ID"));
	}
	public void setUserId(long value) {
		setAttrValue("USER_ID",value);
	}
	
	public long getFriendId() {
		return DataTypeTrans.transToLong(getAttrValue("FRIEND_ID"));
	}
	public void setFriendId(long value) {
		setAttrValue("FRIEND_ID",value);
	}
	
	public String getState() {
		return DataTypeTrans.transToString(getAttrValue("STATE"));
	}
	public void setState(String value) {
		setAttrValue("STATE",value);
	}
	
	public String getMsgType() {
		return DataTypeTrans.transToString(getAttrValue("MSG_TYPE"));
	}
	public void setMsgType(String value) {
		setAttrValue("MSG_TYPE",value);
	}
	
	public String getMsgContent() {
		return DataTypeTrans.transToString(getAttrValue("MSG_CONTENT"));
	}
	public void setMsgContent(String value) {
		setAttrValue("MSG_CONTENT",value);
	}
	
	public String getCreateDate() {
		return DataTypeTrans.transToString(getAttrValue("CREATE_DATE"));
	}
	public void setCreateDate(String value) {
		setAttrValue("CREATE_DATE",value);
	}
}
