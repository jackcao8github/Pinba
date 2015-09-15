package com.webapp.user.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.ContextLoaderListener;

import com.webapp.common.servlet.AbstractServlet;
import com.webapp.common.util.ExceptionUtil;
import com.webapp.user.dao.ResumeManagerDAO;
import com.webapp.user.dao.UserManagerDAO;

public class ResumeManagerServlet extends AbstractServlet {
	private ResumeManagerDAO resumeDao = null;

	public ResumeManagerServlet() {
		super();
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		resumeDao = (ResumeManagerDAO) ContextLoaderListener.getCurrentWebApplicationContext().getBean("resumeDAOProxy");
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
			if (method.equals("getResumeInfo")){//加载简历信息
				String resumeInfo = hreq.getParameter("resumeInfo");
				JSONObject resumeJson = JSONObject.fromObject(resumeInfo);
				if (StringUtils.isEmpty(resumeInfo)) {
					returnFailResult("参数resumeInfo不能为空", response);
					return;
				}
				long resumeId = resumeJson.getLong("resumeId");
				long readUserId = resumeJson.containsKey("readUserId")==true?resumeJson.getLong("readUserId"):0L;
				//String token = userJson.getString("token");
				//先判断token是否正确，如果不正确则提示重新登陆

				//加载用户信息
				JSONObject retJson = resumeDao.getResumeInfo(resumeId);
				//如果不是工作发布者来看这个工作，则记录查看日志
				if (readUserId>0L&&retJson.getLong("userId")!=readUserId){
					resumeDao.newLookHis(readUserId,resumeId);
				}
				
				returnSuccessResult(retJson, response);
			}else if (method.equals("newResume")){//新增简历信息
				String resumeInfo = hreq.getParameter("resumeInfo");
				JSONObject resumeJson = JSONObject.fromObject(resumeInfo);
				if (StringUtils.isEmpty(resumeInfo)) {
					returnFailResult("参数resumeInfo不能为空", response);
					return;
				}
				//String token = userJson.getString("token");
				//先判断token是否正确，如果不正确则提示重新登陆

				//新增简历
				JSONObject retJson = resumeDao.newResume(resumeJson);
				retJson.put("msg", "新增成功！");
				returnSuccessResult(retJson, response);
			}else if (method.equals("modResume")){//修改简历信息
				String resumeInfo = hreq.getParameter("resumeInfo");
				JSONObject resumeJson = JSONObject.fromObject(resumeInfo);
				if (StringUtils.isEmpty(resumeInfo)) {
					returnFailResult("参数resumeInfo不能为空", response);
					return;
				}
				//String token = userJson.getString("token");
				//先判断token是否正确，如果不正确则提示重新登陆

				//新增简历
				resumeDao.modResume(resumeJson);
				JSONObject retJson =  new JSONObject();
				retJson.put("msg", "修改成功！");
				returnSuccessResult(retJson, response);
			}else if (method.equals("getResumeList")){//加载简历列表
				String resumeInfo = hreq.getParameter("resumeInfo");
				JSONObject resumeJson = JSONObject.fromObject(resumeInfo);
				if (StringUtils.isEmpty(resumeInfo)) {
					returnFailResult("参数resumeInfo不能为空", response);
					return;
				}
				long resumeId = resumeJson.getLong("userId");
				int page = resumeJson.getInt("page");
				//String token = userJson.getString("token");
				//先判断token是否正确，如果不正确则提示重新登陆

				//加载简历列表
				JSONArray resumeList = resumeDao.getResumeList(resumeId,page);
				returnSuccessResult(resumeList, response);
			}else if (method.equals("sendResume")) {// 发送简历
				String sendResumeInfo = hreq.getParameter("sendResumeInfo");

				if (StringUtils.isEmpty(sendResumeInfo)) {
					returnFailResult("参数focusInfo不能为空", response);
					return;
				}
				JSONObject sendResumeJson = JSONObject.fromObject(sendResumeInfo);
				long workId = sendResumeJson.getLong("workId");
				long resumeId = sendResumeJson.getLong("resumeId");
				// String token = userJson.getString("token");
				// 先判断token是否正确，如果不正确则提示重新登陆

				// 加载用户信息
				long workIdFocusNumber = resumeDao.sendResume(workId, resumeId);
				JSONObject retJson = new JSONObject();
				retJson.put("sendResumeNumber", workIdFocusNumber);
				retJson.put("msg", "发送成功!");
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
