package com.webapp.work.bean;

import com.webapp.common.bean.AbstractBean;
import com.webapp.common.bean.DataTypeTrans;

//用户bean
public class BatchPayHisBean extends AbstractBean{
	public BatchPayHisBean(){
		getTableName().append("BATCH_PAY_HIS");
		getKeyCols().add("PAY_HIS_ID");
	}
	
	public long getHisId() {
		return DataTypeTrans.transToLong(getAttrValue("PAY_HIS_ID"));
	}
	public void setHisId(long userId) {
		setAttrValue("PAY_HIS_ID",userId);
	}
	public long getUserWorkId() {
		return DataTypeTrans.transToLong(getAttrValue("USER_WORK_ID"));
	}
	public void setUserWorkId(long userId) {
		setAttrValue("USER_WORK_ID",userId);
	}
	
	public String getBatchNo() {
		return DataTypeTrans.transToString(getAttrValue("BATCH_NO"));
	}
	public void setBatchNo(String value) {
		setAttrValue("BATCH_NO",value);
	}
	public String getSeq() {
		return DataTypeTrans.transToString(getAttrValue("SEQ"));
	}
	public void setSeq(String value) {
		setAttrValue("SEQ",value);
	}
	
	public String getMoney() {
		return DataTypeTrans.transToString(getAttrValue("MONEY"));
	}
	public void setMoney(String value) {
		setAttrValue("MONEY",value);
	}
	
	
	public String getState() {
		return DataTypeTrans.transToString(getAttrValue("STATE"));
	}
	public void setState(String value) {
		setAttrValue("STATE",value);
	}
	
	public String getRemark() {
		return DataTypeTrans.transToString(getAttrValue("REMARK"));
	}
	public void setRemark(String value) {
		setAttrValue("REMARK",value);
	}
	
	public String getCreateDate() {
		return DataTypeTrans.transToString(getAttrValue("CREATE_DATE"));
	}
	public void setCreateDate(String value) {
		setAttrValue("CREATE_DATE",value);
	}
	public String getUpdateDate() {
		return DataTypeTrans.transToString(getAttrValue("UPDATE_DATE"));
	}
	public void setUpdateDate(String value) {
		setAttrValue("UPDATE_DATE",value);
	}
	
	public String getDestAlipayNo() {
		return DataTypeTrans.transToString(getAttrValue("DEST_ALIPAY_NO"));
	}
	public void setDestAlipayNo(String value) {
		setAttrValue("DEST_ALIPAY_NO",value);
	}
	
	public String getDestAlipayName() {
		return DataTypeTrans.transToString(getAttrValue("DEST_ALIPAY_NAME"));
	}
	public void setDestAlipayName(String value) {
		setAttrValue("DEST_ALIPAY_NAME",value);
	}
	public String getAlipayBatchNo() {
		return DataTypeTrans.transToString(getAttrValue("ALIPAY_BATCH_NO"));
	}
	public void setAlipayBatchNo(String value) {
		setAttrValue("ALIPAY_BATCH_NO",value);
	}
	
}
