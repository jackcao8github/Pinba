package com.webapp.common.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.util.StringUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.webapp.common.util.TimeUtil;
import com.webapp.common.util.VerifyImage;



public class AbstractServlet extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 791860056481208661L;

	public AbstractServlet()
    {
    }

    public void init(ServletConfig config)
        throws ServletException
    {
        super.init(config);
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
	
	/* 返回成功提示信息 */
	protected void returnSuccessResult(JSONArray retJsonArray, HttpServletResponse response) {
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (retJsonArray==null){
			returnFailResult("没有加载到数据",response);
		}
		out.write(retJsonArray.toString());
	}
	
	
	/**
	 * 从json对象里取指定名称的取值
	 * @param jsonObj
	 * @param exceptionFlag 如果json中没有指定的名称则报异常
	 * @return
	 * @throws Exception 
	 */
	protected long getLongFromJSON(JSONObject jsonObj,String name,boolean exceptionFlag) throws Exception{
		if (jsonObj.containsKey(name)){
			String value = jsonObj.getString(name);
			if (StringUtils.isEmpty(value)){
				return 0;
			}else{
				return Long.valueOf(value);
			}
		} else {
			if (exceptionFlag==true){
				throw new Exception("没有传入必须的参数:"+name);
			}else{
				return 0L;
			}
		}
	}
	/**
	 * 从json对象里取指定名称的取值
	 * @param jsonObj
	 * @param exceptionFlag 如果json中没有指定的名称则报异常
	 * @return
	 * @throws Exception 
	 */
	protected String getStringFromJSON(JSONObject jsonObj,String name,boolean exceptionFlag) throws Exception{
		if (jsonObj.containsKey(name)){
			String value = jsonObj.getString(name);
			if (StringUtils.isEmpty(value)){
				if (exceptionFlag==true){
					throw new Exception("没有传入必须的参数:"+name);
				}else{
					return null;
				}
			}else{
				return value;
			}
		} else {
			if (exceptionFlag==true){
				throw new Exception("没有传入必须的参数:"+name);
			}else{
				return null;
			}
		}
	}
	
	//从请求里取得json格式参数
	protected JSONObject getJsonFromReq(HttpServletRequest request,String paramName) throws Exception{
		String paramJsonStr = request.getParameter(paramName);
		if (StringUtils.isEmpty(paramJsonStr)) {
			throw new Exception("参数"+paramName+"不能为空!");
		}
		paramJsonStr = URLDecoder.decode(paramJsonStr, "UTF-8");
		JSONObject condJson = JSONObject.fromObject(paramJsonStr);
		
		return condJson;
	}
	
	protected JSONArray getJsonArrayFromReq(HttpServletRequest request,String paramName) throws Exception{
		String paramJsonStr = request.getParameter(paramName);
		if (StringUtils.isEmpty(paramJsonStr)) {
			throw new Exception("参数"+paramName+"不能为空!");
		}
		paramJsonStr = URLDecoder.decode(paramJsonStr, "UTF-8");
		JSONArray condJson = JSONArray.fromObject(paramJsonStr);
		
		return condJson;
	}
}
