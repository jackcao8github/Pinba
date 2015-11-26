package com.webapp.user.bean;

import com.webapp.common.bean.AbstractBean;
import com.webapp.common.bean.DataTypeTrans;

//用户好友消息bean
public class UserFriendsMsgBean extends AbstractBean{
	public UserFriendsMsgBean(){
		getTableName().append("USER_FRIENDS_MSG");
		getKeyCols().add("MSG_ID");
	}
	
	public long getMsgId() {
		return DataTypeTrans.transToLong(getAttrValue("MSG_ID"));
	}
	public void setMsgId(long userId) {
		setAttrValue("MSG_ID",userId);
	}
	public long getUserId() {
		return DataTypeTrans.transToLong(getAttrValue("USER_ID"));
	}
	public void setUserId(long userId) {
		setAttrValue("USER_ID",userId);
	}
	
	public long getFriendId() {
		return DataTypeTrans.transToLong(getAttrValue("FRIEND_ID"));
	}
	public void setFriendId(long userId) {
		setAttrValue("FRIEND_ID",userId);
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
	public void setCreateDate(String createDate) {
		setAttrValue("CREATE_DATE",createDate);
	}
	public String getState() {
		return DataTypeTrans.transToString(getAttrValue("STATE"));
	}
	public void setState(String value) {
		setAttrValue("STATE",value);
	}
}
