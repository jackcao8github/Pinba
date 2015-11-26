package com.webapp.user.bean;

import com.webapp.common.bean.AbstractBean;
import com.webapp.common.bean.DataTypeTrans;

//用户bean
public class UserAlbumBean extends AbstractBean{
	public UserAlbumBean(){
		getTableName().append("USER_ALBUM");
		getKeyCols().add("USER_ALBUM_ID");
	}
	public long getUserAlbumId() {
		return DataTypeTrans.transToLong(getAttrValue("USER_ALBUM_ID"));
	}
	public void setUserAlbumId(long value) {
		setAttrValue("USER_ALBUM_ID",value);
	}
	public long getAlbumId() {
		return DataTypeTrans.transToLong(getAttrValue("ALBUM_ID"));
	}
	public void setAlbumId(long userId) {
		setAttrValue("ALBUM_ID",userId);
	}
	public long getUserId() {
		return DataTypeTrans.transToLong(getAttrValue("USER_ID"));
	}
	public void setUserId(long userId) {
		setAttrValue("USER_ID",userId);
	}
	
	public String getAlbumType() {
		return DataTypeTrans.transToString(getAttrValue("ALBUM_TYPE"));
	}
	public void setAlbumType(String value) {
		setAttrValue("ALBUM_TYPE",value);
	}
	
	public String getCreateDate() {
		return DataTypeTrans.transToString(getAttrValue("CREATE_DATE"));
	}
	public void setCreateDate(String createDate) {
		setAttrValue("CREATE_DATE",createDate);
	}
	
}
