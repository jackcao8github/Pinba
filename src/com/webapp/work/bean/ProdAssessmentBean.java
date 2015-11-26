package com.webapp.work.bean;

import com.webapp.common.bean.AbstractBean;
import com.webapp.common.bean.DataTypeTrans;

//产品评价bean
public class ProdAssessmentBean extends AbstractBean {
	public ProdAssessmentBean() {
		getTableName().append("PROD_ASSESSMENT");
		getKeyCols().add("ASSESSMENT_ID");
	}

	public long getAssessmentId() {
		return DataTypeTrans.transToLong(getAttrValue("ASSESSMENT_ID"));
	}

	public void setAssessmentId(long value) {
		setAttrValue("ASSESSMENT_ID", value);
	}

	public long getUserId() {
		return DataTypeTrans.transToLong(getAttrValue("USER_ID"));
	}

	public void setUserId(long value) {
		setAttrValue("USER_ID", value);
	}

	public long getProdId() {
		return DataTypeTrans.transToLong(getAttrValue("PROD_ID"));
	}

	public void setProdId(long value) {
		setAttrValue("PROD_ID", value);
	}

	public String getAssessmentType() {
		return DataTypeTrans.transToString(getAttrValue("ASSESSMENT_TYPE"));
	}

	public void setAssessmentType(String value) {
		setAttrValue("ASSESSMENT_TYPE", value);
	}

	public String getCreateDate() {
		return DataTypeTrans.transToString(getAttrValue("CREATE_DATE"));
	}

	public void setCreateDate(String value) {
		setAttrValue("CREATE_DATE", value);
	}
	public long getAssessmentValue() {
		return DataTypeTrans.transToLong(getAttrValue("ASSESSMENT_VALUE"));
	}

	public void setAssessmentValue(long value) {
		setAttrValue("ASSESSMENT_VALUE", value);
	}
	
	public long getAlbumId() {
		return DataTypeTrans.transToLong(getAttrValue("ALBUM_ID"));
	}

	public void setAlbumId(long value) {
		setAttrValue("ALBUM_ID", value);
	}
}
