package com.webapp.user.bean;

import com.webapp.common.bean.AbstractBean;
import com.webapp.common.bean.DataTypeTrans;

//用户bean
public class VUserHeadPicBean extends AbstractBean{
	public VUserHeadPicBean(){
		getTableName().append("v_user_headpic");
	}
	
	public long getUserId() {
		return DataTypeTrans.transToLong(getAttrValue("USER_ID"));
	}
	public void setUserId(long userId) {
		setAttrValue("USER_ID",userId);
	}
	
	public long getPhotoId() {
		return DataTypeTrans.transToLong(getAttrValue("PHOTO_ID"));
	}
	public void setPhotoId(long userId) {
		setAttrValue("PHOTO_ID",userId);
	}
	
	
}
