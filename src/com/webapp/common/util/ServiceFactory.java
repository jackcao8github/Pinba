package com.webapp.common.util;

import org.springframework.web.context.ContextLoaderListener;

import com.webapp.common.dao.AbstractDAO;

public class ServiceFactory {
	//返回指定名称的dao
	public static AbstractDAO getDAO(String daoName) {
		AbstractDAO dao = (AbstractDAO) ContextLoaderListener.getCurrentWebApplicationContext().getBean(daoName + "Proxy");
		return dao;
	}
}
