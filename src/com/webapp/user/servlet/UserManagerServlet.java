package com.webapp.user.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.ContextLoaderListener;

import com.webapp.common.util.ExceptionUtil;
import com.webapp.user.dao.UserManagerDAO;

public class UserManagerServlet extends HttpServlet {
	private UserManagerDAO userDao = null;

	public UserManagerServlet() {
		super();
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		userDao = (UserManagerDAO) ContextLoaderListener.getCurrentWebApplicationContext().getBean("userDAOProxy");
	}

	/* 返回失败提示信息 */
	protected void returnFailResult(String msg, HttpServletResponse response) {
		JSONObject retJson = new JSONObject();
		retJson.put("flag", "fail");
		retJson.put("msg", msg);

		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
		}
		out.write(retJson.toString());
	}

	/* 返回成功提示信息 */
	protected void returnSuccessResult(JSONObject retJson, HttpServletResponse response) {
		PrintWriter out = null;
		try {
			retJson.put("flag", "success");
			out = response.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
		}
		out.write(retJson.toString());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpServletRequest hreq = (HttpServletRequest) request;
		HttpServletResponse hres = (HttpServletResponse) response;
		response.setContentType("text/html;charset=utf-8");
		String userInfo = hreq.getParameter("userInfo");
		String method = hreq.getParameter("method");

		if (StringUtils.isEmpty(method)) {
			returnFailResult("参数method不能为空", response);
			return;
		}
		if (StringUtils.isEmpty(userInfo)) {
			returnFailResult("参数userInfo不能为空", response);
			return;
		}
		try {
			JSONObject userJson = JSONObject.fromObject(userInfo);
			if (method.equals("newUser")) {
				userDao.newUser(userJson);
				userJson.put("loginCode", userJson.get("cellphone"));
				JSONObject retJson = userDao.login(userJson);
				returnSuccessResult(retJson, response);
			} else if (method.equals("modUser")) {
				userDao.modUser(userJson);
				JSONObject retJson = new JSONObject();
				retJson.put("msg", "修改成功");
				returnSuccessResult(retJson, response);
			} else if (method.equals("login")) {
				String verifyCode = userJson.getString("verifyCode");// 图片验证码
				Object verifyCodeObj = request.getSession().getAttribute("__SESSION_VERIFY__");
				String verifyCodeInSession = null;
				if (verifyCodeObj!=null){
					verifyCodeInSession = verifyCodeObj.toString();
				}
				

				if (StringUtils.equalsIgnoreCase(verifyCode, verifyCodeInSession)) {

				} else {
					returnFailResult("验证码不正确!", response);
					return;
				}
				JSONObject retJson = userDao.login(userJson);
				returnSuccessResult(retJson, response);
			}
		} catch (Exception e) {
			returnFailResult(ExceptionUtil.getMessage(e), response);
			return;
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -5222960410864824004L;

}
