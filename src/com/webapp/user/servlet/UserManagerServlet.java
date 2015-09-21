package com.webapp.user.servlet;

import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.ContextLoaderListener;

import com.webapp.common.servlet.AbstractServlet;
import com.webapp.common.util.CachedMsgVerifyCodeUtil;
import com.webapp.common.util.CachedUserPostitionUtil;
import com.webapp.common.util.ExceptionUtil;
import com.webapp.user.dao.UserManagerDAO;

public class UserManagerServlet extends AbstractServlet {
	private UserManagerDAO userDao = null;

	public UserManagerServlet() {
		super();
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		userDao = (UserManagerDAO) ContextLoaderListener.getCurrentWebApplicationContext().getBean("userDAOProxy");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpServletRequest hreq = (HttpServletRequest) request;
		response.setContentType("text/html;charset=utf-8");
		String method = hreq.getParameter("method");
		if (StringUtils.isEmpty(method)) {
			returnFailResult("参数method不能为空", response);
			return;
		}

		try {
			if (method.equals("newUser")) {// 新增用户
				String userInfo = hreq.getParameter("userInfo");

				if (StringUtils.isEmpty(userInfo)) {
					returnFailResult("参数userInfo不能为空", response);
					return;
				}
				JSONObject userJson = JSONObject.fromObject(userInfo);
				String msgVerifyCode = super.getStringFromJSON(userJson, "msgVerifyCode", true);
				String cellphone = super.getStringFromJSON(userJson, "cellphone", true);
				//将验证码和存储的验证码进行比较
				String cachedVerifyCode = CachedMsgVerifyCodeUtil.getCachedData(cellphone);
				if (!StringUtils.equals(msgVerifyCode, cachedVerifyCode)){
					returnFailResult("短信验证码不正确或已超时!", response);
					return;
				}
				// 新建用户
				long userId = userDao.newUser(userJson);
				
				// 立即登陆后返回
				JSONObject loginInfo = new JSONObject();
				loginInfo.put("loginCode", userId);
				loginInfo.put("password", userJson.getString("password"));

				JSONObject retJson = userDao.login(loginInfo);
				returnSuccessResult(retJson, response);
			} else if (method.equals("modUser")) {// 修改用户
				String userInfo = hreq.getParameter("userInfo");

				if (StringUtils.isEmpty(userInfo)) {
					returnFailResult("参数userInfo不能为空", response);
					return;
				}
				JSONObject userJson = JSONObject.fromObject(userInfo);
				userDao.modUser(userJson);
				JSONObject retJson = new JSONObject();
				retJson.put("msg", "修改成功");
				returnSuccessResult(retJson, response);
			} else if (method.equals("login")) {// 登陆
				String userInfo = hreq.getParameter("userInfo");

				if (StringUtils.isEmpty(userInfo)) {
					returnFailResult("参数userInfo不能为空", response);
					return;
				}
				JSONObject userJson = JSONObject.fromObject(userInfo);
				String verifyCode = userJson.getString("verifyCode");// 图片验证码
				Object verifyCodeObj = request.getSession().getAttribute("__SESSION_VERIFY__");
				String verifyCodeInSession = null;
				if (verifyCodeObj != null) {
					verifyCodeInSession = verifyCodeObj.toString();
				}

				if (StringUtils.equalsIgnoreCase(verifyCode, verifyCodeInSession)) {

				} else {
					returnFailResult("验证码不正确!", response);
					return;
				}
				// 使用私钥对密码进行解密
				/*
				 * String password = userJson.getString("password"); String
				 * privateKeyEx = (String)
				 * request.getSession().getAttribute("__SESSION_PRIVATE_KEY1__"
				 * ); String privateKeyMo = (String)
				 * request.getSession().getAttribute
				 * ("__SESSION_PRIVATE_KEY2__");
				 * 
				 * RSAPrivateKey privateKey =
				 * RSAUtils.getPrivateKey(privateKeyMo, privateKeyEx); String
				 * descrypedPwd = RSAUtils.decryptByPrivateKey(password,
				 * privateKey); //解密后的密码,password是提交过来的密码
				 * 
				 * int ind = descrypedPwd.lastIndexOf(" "); if (ind>0){
				 * descrypedPwd = descrypedPwd.substring(ind,
				 * descrypedPwd.length()); }
				 * userJson.put("password",descrypedPwd);
				 */
				JSONObject retJson = userDao.login(userJson);
				returnSuccessResult(retJson, response);
			} else if (method.equals("changePass")) {// 修改密码
				String userInfo = hreq.getParameter("userInfo");

				if (StringUtils.isEmpty(userInfo)) {
					returnFailResult("参数userInfo不能为空", response);
					return;
				}
				JSONObject userJson = JSONObject.fromObject(userInfo);
				String msgVerifyCode = userJson.getString("msgVerifyCode");
				String newpass = userJson.getString("newPassword");
				String cellphone = userJson.getString("cellphone");
				// TODO 找到用户手机，判断短信验证码

				// 修改密码
				long userId = userDao.getUserIdByCellPhone(cellphone);
				userJson.clear();
				userJson.put("password", newpass);
				userJson.put("userId", userId);

				userDao.modUser(userJson);
				JSONObject retJson = new JSONObject();
				retJson.put("msg", "修改成功");
				returnSuccessResult(retJson, response);
			} else if (method.equals("getUserInfo")) {// 加载用户信息
				String userInfo = hreq.getParameter("userInfo");

				if (StringUtils.isEmpty(userInfo)) {
					returnFailResult("参数userInfo不能为空", response);
					return;
				}
				JSONObject userJson = JSONObject.fromObject(userInfo);
				long userId = userJson.getLong("userId");
				// String token = userJson.getString("token");
				// 先判断token是否正确，如果不正确则提示重新登陆

				// 加载用户信息
				JSONObject retJson = userDao.getUserInfo(userId);
				returnSuccessResult(retJson, response);
			} else if (method.equals("logUserPosition")){//记录用户坐标
				String userInfo = hreq.getParameter("userInfo");

				if (StringUtils.isEmpty(userInfo)) {
					returnFailResult("参数userInfo不能为空", response);
					return;
				}
				JSONObject userJson = JSONObject.fromObject(userInfo);
				long userId = userJson.getLong("userId");
				String latitude = userJson.getString("latitude");
				String longitude = userJson.getString("longitude");
				String cityName = userJson.getString("cityName");

				CachedUserPostitionUtil.addCacheData(userId, cityName, latitude, longitude);
				// 记录用户位置
				userDao.logUserPosition(userId,cityName,latitude,longitude);
				JSONObject retJson = new JSONObject();
				retJson.put("msg", "记录成功!");
				returnSuccessResult(retJson, response);
			}else if (method.equals("saveHeadPic")){//保存用户头像
				String userInfo = hreq.getParameter("userInfo");

				if (StringUtils.isEmpty(userInfo)) {
					returnFailResult("参数userInfo不能为空", response);
					return;
				}
				JSONObject userJson = JSONObject.fromObject(userInfo);
				long userId = userJson.getLong("userId");
				String userHeadPicData = userJson.getString("userHeadPicData");

				// 保存用户头像
				userDao.saveHeadPic(userId,userHeadPicData);
				JSONObject retJson = new JSONObject();
				retJson.put("msg", "记录成功!");
				returnSuccessResult(retJson, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
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
