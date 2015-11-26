package com.webapp.work.bean;

import com.webapp.common.bean.AbstractBean;
import com.webapp.common.bean.DataTypeTrans;

//产品评价bean
public class ProdAssessmentAlbumBean extends AbstractBean {
	public ProdAssessmentAlbumBean() {
		getTableName().append("PROD_ASSESSMENT_ALBUM");
		getKeyCols().add("ASSESSMENT_ALBUM_ID");
	}
	public long getAssessmentAlbumId() {
		return DataTypeTrans.transToLong(getAttrValue("ASSESSMENT_ALBUM_ID"));
	}

	public void setAssessmentAlbumId(long value) {
		setAttrValue("ASSESSMENT_ALBUM_ID", value);
	}
	public long getAssessmentId() {
		return DataTypeTrans.transToLong(getAttrValue("ASSESSMENT_ID"));
	}

	public void setAssessmentId(long value) {
		setAttrValue("ASSESSMENT_ID", value);
	}

	public String getCreateDate() {
		return DataTypeTrans.transToString(getAttrValue("CREATE_DATE"));
	}

	public void setCreateDate(String value) {
		setAttrValue("CREATE_DATE", value);
	}
	public long getAlbumId() {
		return DataTypeTrans.transToLong(getAttrValue("ALBUM_ID"));
	}

	public void setAlbumId(long value) {
		setAttrValue("ALBUM_ID", value);
	}
}
