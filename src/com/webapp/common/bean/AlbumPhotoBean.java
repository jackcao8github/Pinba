package com.webapp.common.bean;

import com.webapp.common.bean.AbstractBean;
import com.webapp.common.bean.DataTypeTrans;

//用户bean
public class AlbumPhotoBean extends AbstractBean{
	public AlbumPhotoBean(){
		getTableName().append("ALBUM_PHOTO");
		getKeyCols().add("PHOTO_ID");
	}
	
	public long getAlbumId() {
		return DataTypeTrans.transToLong(getAttrValue("ALBUM_ID"));
	}
	public void setAlbumId(long userId) {
		setAttrValue("ALBUM_ID",userId);
	}
	public long getPhotoId() {
		return DataTypeTrans.transToLong(getAttrValue("PHOTO_ID"));
	}
	public void setPhotoId(long userId) {
		setAttrValue("PHOTO_ID",userId);
	}
	public String getPhotoPath() {
		return DataTypeTrans.transToString(getAttrValue("PHOTO_PATH"));
	}
	public void setPhotoPath(String userName) {
		setAttrValue("PHOTO_PATH",userName);
	}
	
	public long getPhotoName() {
		return DataTypeTrans.transToLong(getAttrValue("PHOTO_NAME"));
	}
	public void setPhotoName(long value) {
		setAttrValue("PHOTO_NAME",value);
	}
	
	public String getPhotoData() {
		return DataTypeTrans.transToString(getAttrValue("PHOTO_DATA"));
	}
	public void setPhotoData(String value) {
		setAttrValue("PHOTO_DATA",value);
	}
	public String getCreateDate() {
		return DataTypeTrans.transToString(getAttrValue("CREATE_DATE"));
	}
	public void setCreateDate(String createDate) {
		setAttrValue("CREATE_DATE",createDate);
	}
}
