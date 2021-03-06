package com.webapp.common.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.webapp.common.bean.AbstractBean;
import com.webapp.common.bean.PreparedSqlAndParams;

public abstract class AbstractDAO {
	Log log = LogFactory.getLog(AbstractDAO.class);
	private DataSource dataSource;

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	protected Object insertBean(AbstractBean abstractBean) {
		JdbcTemplate jt = new JdbcTemplate(getDataSource());
		PreparedSqlAndParams sqlAndParams = abstractBean.insertSql();
		log.info(sqlAndParams.sql);
		if (sqlAndParams.args != null)
			log.info(getParamsStr(sqlAndParams.args));
		jt.update(sqlAndParams.sql.toString(), sqlAndParams.args);
		return abstractBean.getKeyColValue();
	}

	protected void updateBean(AbstractBean abstractBean) {
		JdbcTemplate jt = new JdbcTemplate(getDataSource());
		PreparedSqlAndParams sqlAndParams = abstractBean.updateSql();
		if (sqlAndParams == null) {
			return;
		}
		log.info(sqlAndParams.sql);
		if (sqlAndParams.args != null)
			log.info(getParamsStr(sqlAndParams.args));
		jt.update(sqlAndParams.sql.toString(), sqlAndParams.args);
	}

	protected void deleteBean(AbstractBean abstractBean) {
		JdbcTemplate jt = new JdbcTemplate(getDataSource());
		PreparedSqlAndParams sqlAndParams = abstractBean.deleteSql();
		log.info(sqlAndParams.sql);
		if (sqlAndParams.args != null)
			log.info(getParamsStr(sqlAndParams.args));
		jt.update(sqlAndParams.sql.toString(), sqlAndParams.args);
	}

	protected List<AbstractBean> getBeans(final Class clazz,
			Map<String, Object> params) {
		return getBeans(clazz, params, false);
	}

	protected AbstractBean getBean(final Class clazz, Map<String, Object> params) {
		List beans = getBeans(clazz, params, false);

		if (beans == null || beans.size() == 0) {
			return null;
		} else {
			return (AbstractBean) beans.get(0);
		}
	}

	private Map<Class, AbstractBean> cachedBeans = new HashMap();

	public AbstractBean newBean(Class clazz) throws Exception {
		AbstractBean retBean = null;
		if (cachedBeans.containsKey(clazz)) {
			AbstractBean cachedBean = cachedBeans.get(clazz);
			retBean = cachedBean.deepCopy();
		} else {
			AbstractBean newbean = (AbstractBean) clazz.newInstance();
			cachedBeans.put(clazz, newbean);

			retBean = (AbstractBean) newbean.deepCopy();// 深拷贝生成新对象，提高效效率
		}

		return retBean;
	}

	protected List<AbstractBean> getBeans(final Class clazz,
			Map<String, Object> params, boolean forupdate) {
		PreparedSqlAndParams sqlAndParams = null;
		List result = null;
		try {
			sqlAndParams = ((AbstractBean) newBean(clazz)).getBeansSql(params,
					forupdate);
			log.info(sqlAndParams.sql);
			if (sqlAndParams.args != null)
				log.info(getParamsStr(sqlAndParams.args));
			JdbcTemplate jt = new JdbcTemplate(getDataSource());
			result = jt.query(sqlAndParams.sql.toString(), sqlAndParams.args,
					new RowMapper() {
						@Override
						public Object mapRow(ResultSet resultset, int i)
								throws SQLException {
							AbstractBean bean = null;
							try {
								bean = newBean(clazz);
								int count = resultset.getMetaData()
										.getColumnCount();
								for (int ii = 1; ii <= count; ii++) {
									String colName = resultset.getMetaData()
											.getColumnName(ii);

									bean.setAttrValue(colName.toUpperCase(),
											resultset.getObject(ii));
								}
							} catch (InstantiationException e) {
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							} catch (CloneNotSupportedException e) {
								e.printStackTrace();
							} catch (Exception e) {
								e.printStackTrace();
							}

							return bean;
						}
					});
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public long getBeanCount(final Class clazz, Map<String, Object> params) {
		PreparedSqlAndParams sqlAndParams = null;
		try {
			sqlAndParams = ((AbstractBean) newBean(clazz))
					.getBeanCountSql(params);
			log.info(sqlAndParams.sql);
			if (sqlAndParams.args != null)
				log.info(getParamsStr(sqlAndParams.args));
			JdbcTemplate jt = new JdbcTemplate(getDataSource());
			SqlRowSet rowSet = jt.queryForRowSet(sqlAndParams.sql.toString(),
					sqlAndParams.args);
			if (rowSet != null) {
				rowSet.first();
				return rowSet.getLong("RESULT");
			}
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	private String getParamsStr(Object[] parms){
		StringBuffer str = new StringBuffer();
		if (parms!=null){
			for (int i=0;i<parms.length;i++){
				if (str.length()>0){
					str.append(",");
				}
				str.append(parms[i].toString());
			}
		}
		return str.toString();
	}
}
