package com.webapp.work.servlet;

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
import com.webapp.work.dao.WorkManagerDAO;

public class WorkManagerServlet extends AbstractServlet {
	private WorkManagerDAO workDao = null;

	public WorkManagerServlet() {
		super();
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		workDao = (WorkManagerDAO) ContextLoaderListener.getCurrentWebApplicationContext().getBean("workDAOProxy");
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
			if (method.equals("getWorkInfo")){//加载简历信息
				String workInfo = hreq.getParameter("workInfo");
				JSONObject resumeJson = JSONObject.fromObject(workInfo);
				if (StringUtils.isEmpty(workInfo)) {
					returnFailResult("参数workInfo不能为空", response);
					return;
				}
				long workId = resumeJson.getLong("workId");
				//String token = userJson.getString("token");
				//先判断token是否正确，如果不正确则提示重新登陆

				//加载用户信息
				JSONObject retJson = workDao.getWorkInfo(workId);
				returnSuccessResult(retJson, response);
			}else if (method.equals("newWork")){//新增简历信息
				String workInfo = hreq.getParameter("workInfo");
				JSONObject resumeJson = JSONObject.fromObject(workInfo);
				if (StringUtils.isEmpty(workInfo)) {
					returnFailResult("参数workInfo不能为空", response);
					return;
				}
				//String token = userJson.getString("token");
				//先判断token是否正确，如果不正确则提示重新登陆

				//新增简历
				JSONObject retJson = workDao.newWork(resumeJson);
				retJson.put("msg", "新增成功！");
				returnSuccessResult(retJson, response);
			}else if (method.equals("modWork")){//修改简历信息
				String workInfo = hreq.getParameter("workInfo");
				JSONObject resumeJson = JSONObject.fromObject(workInfo);
				if (StringUtils.isEmpty(workInfo)) {
					returnFailResult("参数workInfo不能为空", response);
					return;
				}
				//String token = userJson.getString("token");
				//先判断token是否正确，如果不正确则提示重新登陆

				//新增简历
				workDao.modWork(resumeJson);
				JSONObject retJson =  new JSONObject();
				retJson.put("msg", "修改成功！");
				returnSuccessResult(retJson, response);
			}else if (method.equals("getWorkList")){//加载简历列表
				String workInfo = hreq.getParameter("workInfo");
				JSONObject resumeJson = JSONObject.fromObject(workInfo);
				if (StringUtils.isEmpty(workInfo)) {
					returnFailResult("参数workInfo不能为空", response);
					return;
				}
				long workId = resumeJson.getLong("userId");
				int page = resumeJson.getInt("page");
				String workType = resumeJson.getString("workType");
				
				String[] workTypes =null;
				if (workType!=null){
					workTypes = workType.split(",");
				}
				//String token = userJson.getString("token");
				//先判断token是否正确，如果不正确则提示重新登陆

				//加载简历列表
				JSONArray resumeList = workDao.getWorkList(workId,workTypes,page);
				returnSuccessResult(resumeList, response);
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
