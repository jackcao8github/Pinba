package com.webapp.common.bean;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.StringUtils;

import com.webapp.common.dao.SysSeqDAO;

//各种表级bean的基类
public abstract class AbstractBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2881911431019638343L;

	private StringBuffer tableName = new StringBuffer();

	// bean的主键attr
	private Set<String> keyAttrs = new HashSet<String>();

	private static SysSeqDAO seqDAO = null;

	// bean的每个attr的取值
	private Map<String, AttrValue> attrValues = new HashMap<String, AttrValue>();

	public AbstractBean() {
		ApplicationContext context = new ClassPathXmlApplicationContext("Application-Context.xml");
		if (seqDAO == null) {
			seqDAO = (SysSeqDAO) context.getBean("seqDAOProxy");
		}

	}

	public AbstractBean deepCopy() throws Exception {
		// 将该对象序列化成流,因为写在流里的是对象的一个拷贝，而原对象仍然存在于JVM里面。所以利用这个特性可以实现对象的深拷贝
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		ObjectOutputStream oos = new ObjectOutputStream(bos);

		oos.writeObject(this);

		// 将流序列化成对象
		ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());

		ObjectInputStream ois = new ObjectInputStream(bis);

		return (AbstractBean) ois.readObject();
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
	 * 
	 * @return
	 */
	public Object getKeyColValue() {
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
		if (attrValues.containsKey(colName) && newValue != null) {
			attrValues.get(colName).setNewValue(newValue);
		} else if (newValue != null) {
			attrValues.put(colName, new AttrValue(newValue));
		}
	}

	/* 生成insert语句 */
	public PreparedSqlAndParams insertSql() {
		PreparedSqlAndParams sqlAndParams = new PreparedSqlAndParams();
		// 先取seq
		if (keyAttrs.size() > 0) {
			Iterator it = keyAttrs.iterator();
			while (it.hasNext()) {
				String keyColName = it.next().toString();

				AttrValue keyValue = attrValues.get(keyColName);
				// 没设置过主键字段取值
				if (keyValue == null || keyValue.getNewValue() == null) {
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

		int i = 0;
		while (it.hasNext()) {
			Entry ent = (Entry) it.next();
			if (colNames.length() > 0) {
				colNames.append(",");
			}
			colNames.append(ent.getKey().toString());
			if (params.length() > 0) {
				params.append(",");
			}
			params.append("?");

			sqlAndParams.args[i] = ((AttrValue) ent.getValue()).getNewValue();
			i++;
		}
		sqlAndParams.sql.append(colNames).append(") values (").append(params).append(")");

		return sqlAndParams;
	}

	public PreparedSqlAndParams updateSql() {
		// 如果attrValues中数量和keyAttrs数量一致,则可以认为不需要更新任何
		if (attrValues.size() == keyAttrs.size()) {
			return null;
		}
		PreparedSqlAndParams sqlAndParams = new PreparedSqlAndParams();
		Iterator it = attrValues.entrySet().iterator();

		sqlAndParams.sql.append(" UPDATE ").append(this.tableName).append(" SET ");
		StringBuffer colNames = new StringBuffer();
		StringBuffer params = new StringBuffer();
		sqlAndParams.args = new Object[attrValues.size()];

		int i = 0;
		while (it.hasNext()) {
			Entry ent = (Entry) it.next();
			if (colNames.length() > 0) {
				colNames.append(",");
			}
			colNames.append(ent.getKey().toString()).append("=?");
			sqlAndParams.args[i] = ((AttrValue) ent.getValue()).getNewValue();
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

	public PreparedSqlAndParams getBeansSql(Map<String, Object> params, boolean forupdate) {
		PreparedSqlAndParams sqlAndParams = new PreparedSqlAndParams();
		Iterator it = attrValues.entrySet().iterator();

		sqlAndParams.sql.append("SELECT ");
		StringBuffer colNames = new StringBuffer();

		while (it.hasNext()) {
			Entry ent = (Entry) it.next();
			if (colNames.length() > 0) {
				colNames.append(",");
			}
			colNames.append(ent.getKey().toString());
		}
		if (colNames.length() == 0) {
			sqlAndParams.sql.append(" * ").append(" FROM ").append(this.tableName);
		} else {
			sqlAndParams.sql.append(colNames).append(" FROM ").append(this.tableName);
		}
		
		if (params != null && params.size() > 0) {
			String orderByCol = null;
			if (params.containsKey("ORDERBY")){//排序条件
				orderByCol = (String) params.get("ORDERBY");
				params.remove("ORDERBY");
			}
			String pageNo = null;
			if (params.containsKey("PAGE")){//分页条件
				pageNo = (String) params.get("PAGE");
				params.remove("PAGE");
			}
			
			String[] likeColArr = null;
			if (params.containsKey("LIKECOL")) {// 分页条件
				String likeColstr = (String) params.get("LIKECOL");
				likeColArr = likeColstr.split(",");
				params.remove("LIKECOL");
			}
			
			StringBuffer wherecolNames = new StringBuffer();
			Iterator it2 = params.entrySet().iterator();

			List<Object> argList = new ArrayList();
			while (it2.hasNext()) {
				Entry ent = (Entry) it2.next();
				if (wherecolNames.length() > 0) {
					wherecolNames.append(" AND ");
				}
				Object value = ent.getValue();
				if (value instanceof String[]) {
					wherecolNames.append(ent.getKey().toString()).append(" IN (");

					String[] values = (String[]) value;
					for (int i = 0; i < values.length; i++) {
						if (i > 0) {
							wherecolNames.append(",");
						}
						wherecolNames.append("?");
						argList.add(values[i]);
					}

					wherecolNames.append(")");
				} else {
					wherecolNames.append(ent.getKey().toString());
					if (likeColArr!=null&&ArrayUtils.contains(likeColArr, ent.getKey().toString())){
						wherecolNames.append(" like ? ");
						argList.add("%"+ent.getValue()+"%");
					}else{
						wherecolNames.append(" = ? ");
						argList.add(ent.getValue());
					}
					
					
				}
			}
			sqlAndParams.sql.append(" WHERE ").append(wherecolNames);
			
			if (!StringUtils.isEmpty(orderByCol)){
				sqlAndParams.sql.append(" ORDER BY ").append(orderByCol);
			}
			if (!StringUtils.isEmpty(pageNo)){//每页20条数据
				long startLineNo = (Long.valueOf(pageNo)-1)*20;//计算起始行数
				sqlAndParams.sql.append(" LIMIT ").append(startLineNo).append(",").append("20");
			}
			sqlAndParams.args = argList.toArray();
		}

		if (forupdate == true) {
			sqlAndParams.sql.append(" FOR UPDATE");
		}

		return sqlAndParams;
	}
	
	public PreparedSqlAndParams getBeanCountSql(Map<String, Object> params) {
		PreparedSqlAndParams sqlAndParams = new PreparedSqlAndParams();
		Iterator it = attrValues.entrySet().iterator();

		sqlAndParams.sql.append("SELECT COUNT(1) AS RESULT");
		sqlAndParams.sql.append(" FROM ").append(this.tableName);

		if (params != null && params.size() > 0) {
			StringBuffer wherecolNames = new StringBuffer();
			Iterator it2 = params.entrySet().iterator();

			List<Object> argList = new ArrayList();
			while (it2.hasNext()) {
				Entry ent = (Entry) it2.next();
				if (wherecolNames.length() > 0) {
					wherecolNames.append(" AND ");
				}
				Object value = ent.getValue();
				if (value instanceof String[]) {
					wherecolNames.append(ent.getKey().toString()).append(" IN (");

					String[] values = (String[]) value;
					for (int i = 0; i < values.length; i++) {
						if (i > 0) {
							wherecolNames.append(",");
						}
						wherecolNames.append("?");
						argList.add(values[i]);
					}

					wherecolNames.append(")");
				} else {
					wherecolNames.append(ent.getKey().toString()).append("=?");
					argList.add(ent.getValue());
				}
			}
			sqlAndParams.sql.append(" WHERE ").append(wherecolNames);

			sqlAndParams.args = argList.toArray();
		}

		return sqlAndParams;
	}
}
