package com.webapp.common.bean;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.webapp.common.dao.SysSeqDAO;

//各种表级bean的基类
public abstract class AbstractBean {
	private StringBuffer tableName = new StringBuffer();

	// bean的主键attr
	private Set<String> keyAttrs = new HashSet<String>();
	
	private static SysSeqDAO seqDAO = null;

	// bean的每个attr的取值
	private Map<String, AttrValue> attrValues = new HashMap<String, AttrValue>();

	
	public AbstractBean(){
		ApplicationContext context = new ClassPathXmlApplicationContext("Application-Context.xml");
		if (seqDAO==null){
			seqDAO = (SysSeqDAO) context.getBean("seqDAOProxy");
		}
	}
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
	
	
	/**
	 * 返回主键字段取值
	 * @return
	 */
	public Object getKeyColValue(){
		String keyColName = keyAttrs.iterator().next();
		Object keyColValue = attrValues.get(keyColName).getNewValue();
		return keyColValue;
	}

	public Object getAttrValue(String colName) {
		if (attrValues.containsKey(colName)) {
			return attrValues.get(colName).getNewValue();
		} else {
			return null;
		}
	}

	public void setAttrValue(String colName, Object newValue) {
		if (attrValues.containsKey(colName)&&newValue!=null) {
			attrValues.get(colName).setNewValue(newValue);
		} else if (newValue!=null){
			attrValues.put(colName, new AttrValue(newValue));
		}
	}

	/*生成insert语句*/
	public PreparedSqlAndParams insertSql() {
		PreparedSqlAndParams sqlAndParams = new PreparedSqlAndParams();
		//先取seq
		if (keyAttrs.size()>0){
			Iterator it = keyAttrs.iterator();
			while(it.hasNext()){
				String keyColName = it.next().toString();
				
				AttrValue keyValue = null;
				//没设置过主键字段取值
				if (keyValue==null||keyValue.getNewValue()==null){
					long newid = seqDAO.getNewId(this.tableName.toString(), keyColName);
					this.setAttrValue(keyColName, newid);
				}
			}
		}
		
		
		
		Iterator it = attrValues.entrySet().iterator();
		
		sqlAndParams.sql.append(" INSERT INTO ").append(this.tableName).append(" (");
		StringBuffer colNames = new StringBuffer();
		StringBuffer params = new StringBuffer();
		sqlAndParams.args = new Object[attrValues.size()];
		
		int i=0;
		while(it.hasNext()){
			Entry ent = (Entry) it.next();
			if (colNames.length()>0){
				colNames.append(",");
			}
			colNames.append(ent.getKey().toString());
			if (params.length()>0){
				params.append(",");
			}
			params.append("?");
			
			sqlAndParams.args[i] = ((AttrValue)ent.getValue()).getNewValue();
			i++;
		}
		sqlAndParams.sql.append(colNames).append(") values (").append(params).append(")");
		
		return sqlAndParams;
	}

	public PreparedSqlAndParams updateSql() {
		PreparedSqlAndParams sqlAndParams = new PreparedSqlAndParams();
		Iterator it = attrValues.entrySet().iterator();
		
		sqlAndParams.sql.append(" UPDATE ").append(this.tableName).append(" SET ");
		StringBuffer colNames = new StringBuffer();
		StringBuffer params = new StringBuffer();
		sqlAndParams.args = new Object[attrValues.size()];
		
		int i=0;
		while(it.hasNext()){
			Entry ent = (Entry) it.next();
			if (colNames.length()>0){
				colNames.append(",");
			}
			colNames.append(ent.getKey().toString()).append("=?");
			sqlAndParams.args[i] = ((AttrValue)ent.getValue()).getNewValue();
			i++;
		}
		String keyColName = keyAttrs.iterator().next();
		String keyColValue = attrValues.get(keyColName).getNewValue().toString();
		sqlAndParams.sql.append(colNames).append(" where ").append(keyColName).append("='").append(keyColValue).append("'");
		
		return sqlAndParams;
	}

	public PreparedSqlAndParams deleteSql() {
		PreparedSqlAndParams sqlAndParams = new PreparedSqlAndParams();
		Iterator it = attrValues.entrySet().iterator();
		
		sqlAndParams.sql.append(" delete from ").append(this.tableName);
		
		String keyColName = keyAttrs.iterator().next();
		String keyColValue = attrValues.get(keyColName).getNewValue().toString();
		sqlAndParams.sql.append(" where ").append(keyColName).append("=").append(keyColValue);
		
		return sqlAndParams;
	}
	
	public PreparedSqlAndParams getBeansSql(Map<String,String> params,boolean forupdate){
		PreparedSqlAndParams sqlAndParams = new PreparedSqlAndParams();
		Iterator it = attrValues.entrySet().iterator();
		
		sqlAndParams.sql.append("SELECT ");
		StringBuffer colNames = new StringBuffer();
		
		while(it.hasNext()){
			Entry ent = (Entry) it.next();
			if (colNames.length()>0){
				colNames.append(",");
			}
			colNames.append(ent.getKey().toString());
		}
		if (colNames.length()==0){
			sqlAndParams.sql.append(" * ").append(" FROM ").append(this.tableName);
		}else{
			sqlAndParams.sql.append(colNames).append(" FROM ").append(this.tableName);
		}
		
		if (params!=null&&params.size()>0){
			StringBuffer wherecolNames = new StringBuffer();
			Iterator it2 = params.entrySet().iterator();
			sqlAndParams.args = new Object[params.size()];
			
			int i=0;
			while(it2.hasNext()){
				Entry ent = (Entry) it2.next();
				if (wherecolNames.length()>0){
					wherecolNames.append(" AND ");
				}
				wherecolNames.append(ent.getKey().toString()).append("=?");
				sqlAndParams.args[i] = ent.getValue();
				i++;
			}
			sqlAndParams.sql.append(" WHERE ").append(wherecolNames);
		}
		
		if (forupdate==true){
			sqlAndParams.sql.append(" FOR UPDATE");
		}
		
		return sqlAndParams;
	}
}
