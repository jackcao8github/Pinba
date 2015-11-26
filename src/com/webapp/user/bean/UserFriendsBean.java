package com.webapp.user.bean;

import com.webapp.common.bean.AbstractBean;
import com.webapp.common.bean.DataTypeTrans;

//用户好友bean
public class UserFriendsBean extends AbstractBean{
	public UserFriendsBean(){
		getTableName().append("USER_FRIENDS");
		getKeyCols().add("USER_FRIEND_ID");
	}
	
	public long getUserFriendId() {
		return DataTypeTrans.transToLong(getAttrValue("USER_FRIEND_ID"));
	}
	public void getUserFriendId(long value) {
		setAttrValue("USER_FRIEND_ID",value);
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
	public String getState() {
		return DataTypeTrans.transToString(getAttrValue("STATE"));
	}
	public void setState(String value) {
		setAttrValue("STATE",value);
	}
	public String getCreateDate() {
		return DataTypeTrans.transToString(getAttrValue("CREATE_DATE"));
	}
	public void setCreateDate(String createDate) {
		setAttrValue("CREATE_DATE",createDate);
	}
}
