package com.webapp.user.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.ContextLoaderListener;

import com.webapp.common.util.ExceptionUtil;
import com.webapp.common.util.RSAUtils;
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
		String method = hreq.getParameter("method");

		if (StringUtils.isEmpty(method)) {
			returnFailResult("参数method不能为空", response);
			return;
		}
		
		try {
			if (method.equals("newUser")) {
				String userInfo = hreq.getParameter("userInfo");
				JSONObject userJson = JSONObject.fromObject(userInfo);
				if (StringUtils.isEmpty(userInfo)) {
					returnFailResult("参数userInfo不能为空", response);
					return;
				}
				userDao.newUser(userJson);
				userJson.put("loginCode", userJson.get("cellphone"));
				JSONObject retJson = userDao.login(userJson);
				returnSuccessResult(retJson, response);
			} else if (method.equals("modUser")) {
				String userInfo = hreq.getParameter("userInfo");
				JSONObject userJson = JSONObject.fromObject(userInfo);
				if (StringUtils.isEmpty(userInfo)) {
					returnFailResult("参数userInfo不能为空", response);
					return;
				}
				
				userDao.modUser(userJson);
				JSONObject retJson = new JSONObject();
				retJson.put("msg", "修改成功");
				returnSuccessResult(retJson, response);
			} else if (method.equals("login")) {
				String userInfo = hreq.getParameter("userInfo");
				JSONObject userJson = JSONObject.fromObject(userInfo);
				if (StringUtils.isEmpty(userInfo)) {
					returnFailResult("参数userInfo不能为空", response);
					return;
				}
				
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
				//使用私钥对密码进行解密
				/*String password = userJson.getString("password");
				String privateKeyEx = (String) request.getSession().getAttribute("__SESSION_PRIVATE_KEY1__"); 
				String privateKeyMo = (String) request.getSession().getAttribute("__SESSION_PRIVATE_KEY2__"); 

				RSAPrivateKey privateKey = RSAUtils.getPrivateKey(privateKeyMo, privateKeyEx);
				String descrypedPwd = RSAUtils.decryptByPrivateKey(password, privateKey); //解密后的密码,password是提交过来的密码 
				
				int ind = descrypedPwd.lastIndexOf(" ");
				if (ind>0){
					descrypedPwd = descrypedPwd.substring(ind, descrypedPwd.length());
				}
				userJson.put("password",descrypedPwd);*/
				JSONObject retJson = userDao.login(userJson);
				returnSuccessResult(retJson, response);
			} else if (method.equals("getRSAPublicKey")){
				KeyPair keyPair = RSAUtils.getKeys();
				RSAPrivateKey privateKey = (RSAPrivateKey)keyPair.getPrivate();
				RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
				
				//模    
		        String modulus = publicKey.getModulus().toString();    
		        //公钥指数    
		        String public_exponent = publicKey.getPublicExponent().toString();  
		        //私钥指数    
		        String private_exponent = privateKey.getPrivateExponent().toString();    
				
				request.getSession().setAttribute("__SESSION_PRIVATE_KEY1__", private_exponent);
				request.getSession().setAttribute("__SESSION_PRIVATE_KEY2__", modulus);

				//返回rsa加密指数和模
				JSONObject retJson = new JSONObject();
				retJson.put("publicKeyExponent",publicKey.getPublicExponent().toString());//指数
				retJson.put("publicKeyModulus", publicKey.getModulus().toString());//模
				
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
