package com.webapp.common.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

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
}
