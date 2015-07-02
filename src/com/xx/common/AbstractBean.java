package com.xx.common;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

//各种表级bean的基类
public class AbstractBean {
	private StringBuffer tableName = new StringBuffer();
	
	//bean的主键attr
	private Set<String> keyAttrs = new HashSet<String>();
	
	//bean的每个attr的取值
	private Map<String,AttrValue> attrValues = new HashMap<String,AttrValue>();

	public StringBuffer getTableName() {
		return tableName;
	}

	public void setTableName(StringBuffer tableName) {
		this.tableName = tableName;
	}

	

	public Set<String> getKeyCols() {
		return keyAttrs;
	}

	public void setKeyCols(Set<String> keyCols) {
		this.keyAttrs = keyCols;
	}

	public Object getAttrValue(String colName){
		if (attrValues.containsKey(colName)){
			return attrValues.get(colName).getNewValue();
		}else{
			return null;
		}
	}
	
	public void setAttrValue(String colName,Object newValue){
		if (attrValues.containsKey(colName)){
			attrValues.get(colName).setNewValue(newValue);
		}else{
			attrValues.put(colName, new AttrValue(newValue));
		}
	}
	
	public PreparedSqlAndParams insertSql(){
		PreparedSqlAndParams sqlAndParams = new PreparedSqlAndParams();
		
		return sqlAndParams;
	}
	public PreparedSqlAndParams updateSql(){
		PreparedSqlAndParams sqlAndParams = new PreparedSqlAndParams();
		return sqlAndParams;
	}
	public PreparedSqlAndParams deleteSql(){
		PreparedSqlAndParams sqlAndParams = new PreparedSqlAndParams();
		return sqlAndParams;
	}
}
