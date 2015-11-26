package com.webapp.common.servlet;

import java.io.IOException;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.webapp.common.util.CachedMsgVerifyCodeUtil;
import com.webapp.common.util.ExceptionUtil;
import com.webapp.common.util.RSAUtils;
import com.webapp.common.util.SMSSendUtil;
import com.webapp.common.util.VerifyImage;

import net.sf.json.JSONObject;

public class CommonServlet extends AbstractServlet {

	private static final long serialVersionUID = 6744874793765278105L;

	public CommonServlet() {
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
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
			if (method.equals("getRSAPublicKey")) {
				KeyPair keyPair = RSAUtils.getKeys();
				RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
				RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

				// 模
				String modulus = publicKey.getModulus().toString();
				// 公钥指数
				String public_exponent = publicKey.getPublicExponent().toString();
				// 私钥指数
				String private_exponent = privateKey.getPrivateExponent().toString();

				request.getSession().setAttribute("__SESSION_PRIVATE_KEY1__", private_exponent);
				request.getSession().setAttribute("__SESSION_PRIVATE_KEY2__", modulus);

				// 返回rsa加密指数和模
				JSONObject retJson = new JSONObject();
				retJson.put("publicKeyExponent", publicKey.getPublicExponent().toString());// 指数
				retJson.put("publicKeyModulus", publicKey.getModulus().toString());// 模

				returnSuccessResult(retJson, response);
			} else if (method.equals("sendRegisterVerifyCode") || method.equals("sendChangePassVerifyCode") || method.equals("sendChangeCellphoneVerifyCode")) {// 发送用户注册验证码,密码修改验证码，更换手机号码验证码
				JSONObject userJson = super.getJsonFromReq(request, "userInfo");
				String cellphone = userJson.getString("cellphone");
				// 判断该号码在5分钟内是否发送过验证码，如果已发送过则无须再次发送
				String cachedVerifyCode = CachedMsgVerifyCodeUtil.getCachedData(cellphone);
				if (cachedVerifyCode == null) {
					// 随机生成并向指定用户手机发送短信验证码
					VerifyImage image = new VerifyImage();
					String verifyCode = image.createValue();

					String[] params = new String[1];
					params[0] = verifyCode;
					
					String templateId = null;
					if (method.equals("sendRegisterVerifyCode")){//注册时的短信验证码
						templateId =  SMSSendUtil.REGISTER_VERIFYCODE_TEMPLATE;
					} else if (method.equals("sendChangePassVerifyCode")){//修改密码时的短信验证码
						templateId =  SMSSendUtil.CHANGE_PASS_TEMPLATE;
					}else if (method.equals("sendChangeCellphoneVerifyCode")){//修改手机号时的短信验证码
						templateId =  SMSSendUtil.CHANGE_CELLPHONE_TEMPLATE;
					}

					JSONObject retJson = SMSSendUtil.sendSMS(cellphone, templateId, params);

					if ("success".equals(retJson.getString("flag"))) {
						// 存储验证码到数据库，5分钟有效
						CachedMsgVerifyCodeUtil.addCachedData(cellphone, verifyCode);
						retJson.put("msg", "发送成功!");
						returnSuccessResult(retJson, response);
					} else {
						returnFailResult("发送失败:" + retJson.getString("statusMsg"), response);
					}
				} else {
					returnFailResult("已发送过，请不要重复发送!", response);
				}
			}
		} catch (Exception e) {
			returnFailResult(ExceptionUtil.getMessage(e), response);
			return;
		}
	}

}
