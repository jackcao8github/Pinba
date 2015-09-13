package com.webapp.common.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.webapp.common.util.ExceptionUtil;

public class PageServlet extends AbstractServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 622256005959520360L;
	public PageServlet()
    {
    }

    public void init(ServletConfig config)
        throws ServletException
    {
        super.init(config);
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpServletRequest hreq = (HttpServletRequest) request;
		HttpServletResponse hres = (HttpServletResponse) response;
		response.setContentType("text/html;charset=utf-8");
		String qryStr = hreq.getQueryString();

		if (StringUtils.isEmpty(qryStr)) {
			response.sendRedirect(hreq.getContextPath()+"/index.html");
			return;
		}
		
		try {
			
		} catch (Exception e) {
			returnFailResult(ExceptionUtil.getMessage(e), response);
			return;
		}
	}
}
