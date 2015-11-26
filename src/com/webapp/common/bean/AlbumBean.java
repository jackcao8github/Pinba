package com.webapp.common.bean;

import com.webapp.common.bean.AbstractBean;
import com.webapp.common.bean.DataTypeTrans;

//相册bean
public class AlbumBean extends AbstractBean{
	public AlbumBean(){
		getTableName().append("ALBUM");
		getKeyCols().add("ALBUM_ID");
	}
	public long getAlbumId() {
		return DataTypeTrans.transToLong(getAttrValue("ALBUM_ID"));
	}
	public void setAlbumId(long userId) {
		setAttrValue("ALBUM_ID",userId);
	}
	
	public String getAlbumName() {
		return DataTypeTrans.transToString(getAttrValue("ALBUM_NAME"));
	}
	public void setAlbumName(String value) {
		setAttrValue("ALBUM_NAME",value);
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
