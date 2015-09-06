package com.webapp.common.bean;


//bean
public class SysSeqBean extends AbstractBean{
	public SysSeqBean(){
		getTableName().append("SYS_SEQ");
		getKeyCols().add("SEQ_NAME");
	}
	
	
	public long getStartBy() {
		return DataTypeTrans.transToLong(getAttrValue("START_BY"));
	}
	public void setStartBy(long userId) {
		setAttrValue("START_BY",userId);
	}
	
	public long getIncrementBy() {
		return DataTypeTrans.transToLong(getAttrValue("INCREAMENT_BY"));
	}
	public void setIncrementBy(long userId) {
		setAttrValue("INCREAMENT_BY",userId);
	}
	public long getLastNumber() {
		return DataTypeTrans.transToLong(getAttrValue("LAST_NUMBER"));
	}
	public void setLastNumber(long userId) {
		setAttrValue("LAST_NUMBER",userId);
	}
	
	public long getJVMStepBy() {
		return DataTypeTrans.transToLong(getAttrValue("JVM_STEP_BY"));
	}
	public void setJVMStepBy(long userId) {
		setAttrValue("JVM_STEP_BY",userId);
	}
	public String getTabName() {
		return DataTypeTrans.transToString(getAttrValue("TAB_NAME"));
	}
	public void setTabName(String userName) {
		setAttrValue("TAB_NAME",userName);
	}
	public String getColName() {
		return DataTypeTrans.transToString(getAttrValue("COL_NAME"));
	}
	public void setColName(String userType) {
		setAttrValue("COL_NAME",userType);
	}
	
	public String getSeqName() {
		return DataTypeTrans.transToString(getAttrValue("SEQ_NAME"));
	}
	public void setSeqName(String userName) {
		setAttrValue("SEQ_NAME",userName);
	}
}
