package com.webapp.user.dao;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import com.webapp.common.util.PreparedSqlAndParams;
import com.webapp.user.bean.UserBean;

public class UserDAO {
	private DataSource dataSource;

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void insertUser(UserBean user) {
		JdbcTemplate jt = new JdbcTemplate(getDataSource());
		PreparedSqlAndParams sqlAndParams = user.insertSql();
		jt.update(sqlAndParams.sql.toString(),sqlAndParams.args);
	}

	public static void main(String[] args) {

		ApplicationContext context = new FileSystemXmlApplicationContext("html/WEB-INF/Application-Context.xml");

		UserBean user = new UserBean();

		UserDAO userDAO = (UserDAO) context.getBean("userDAOProxy");
		userDAO.insertUser(user);
	}
}
