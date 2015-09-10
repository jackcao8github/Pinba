package com.webapp.common.servlet;

import java.io.IOException;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import com.webapp.common.util.ExceptionUtil;
import com.webapp.common.util.RSAUtils;
import com.webapp.common.util.VerifyImage;



public class CommonServlet extends AbstractServlet
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 6744874793765278105L;

	public CommonServlet()
    {
    }

    public void init(ServletConfig config)
        throws ServletException
    {
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
			if (method.equals("getRSAPublicKey")){
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
			} else if (method.equals("sendVerifyCode")){
				String userInfo = hreq.getParameter("userInfo");
				JSONObject userJson = JSONObject.fromObject(userInfo);
				if (StringUtils.isEmpty(userInfo)) {
					returnFailResult("参数userInfo不能为空", response);
					return;
				}
				String cellphone = userJson.getString("cellphone");
				//向指定用户手机发送短信验证码
				
				JSONObject retJson = new JSONObject();
				retJson.put("msg", "发送成功");
				returnSuccessResult(retJson, response);
			}
		} catch (Exception e) {
			returnFailResult(ExceptionUtil.getMessage(e), response);
			return;
		}
	}

}
