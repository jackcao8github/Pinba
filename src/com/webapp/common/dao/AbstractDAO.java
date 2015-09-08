package com.webapp.common.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webapp.common.bean.AbstractBean;
import com.webapp.common.bean.PreparedSqlAndParams;

public abstract class AbstractDAO {
	private DataSource dataSource;

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public Object insertBean(AbstractBean abstractBean) {
		JdbcTemplate jt = new JdbcTemplate(getDataSource());
		PreparedSqlAndParams sqlAndParams = abstractBean.insertSql();
		jt.update(sqlAndParams.sql.toString(), sqlAndParams.args);
		return abstractBean.getKeyColValue();
	}

	public void updateBean(AbstractBean abstractBean) {
		JdbcTemplate jt = new JdbcTemplate(getDataSource());
		PreparedSqlAndParams sqlAndParams = abstractBean.updateSql();
		jt.update(sqlAndParams.sql.toString(), sqlAndParams.args);
	}

	public void deleteBean(AbstractBean abstractBean) {
		JdbcTemplate jt = new JdbcTemplate(getDataSource());
		PreparedSqlAndParams sqlAndParams = abstractBean.deleteSql();
		jt.update(sqlAndParams.sql.toString(), sqlAndParams.args);
	}

	public List<AbstractBean> getBeans(final Class clazz, Map<String, String> params) {
		return getBeans(clazz, params, false);
	}

	public List<AbstractBean> getBeans(final Class clazz, Map<String, String> params, boolean forupdate) {
		PreparedSqlAndParams sqlAndParams = null;
		List result = null;
		try {
			sqlAndParams = ((AbstractBean) clazz.newInstance()).getBeansSql(params, forupdate);
			JdbcTemplate jt = new JdbcTemplate(getDataSource());
			result = jt.query(sqlAndParams.sql.toString(), sqlAndParams.args, new RowMapper() {
				@Override
				public Object mapRow(ResultSet resultset, int i) throws SQLException {
				
						AbstractBean bean = null;
						try {
							bean = (AbstractBean) clazz.newInstance();
						} catch (InstantiationException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
						int count = resultset.getMetaData().getColumnCount();
						for (int ii = 1; ii <= count; ii++) {
							String colName = resultset.getMetaData().getColumnName(ii);

							bean.setAttrValue(colName, resultset.getObject(ii));
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
		}
		return result;
	}
}
