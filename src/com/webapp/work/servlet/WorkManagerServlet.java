package com.webapp.work.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.ContextLoaderListener;

import com.webapp.common.servlet.AbstractServlet;
import com.webapp.common.util.ExceptionUtil;
import com.webapp.common.util.TimeUtil;
import com.webapp.work.dao.WorkManagerDAO;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

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
		response.setContentType("text/html;charset=utf-8");
		String method = request.getParameter("method");

		if (StringUtils.isEmpty(method)) {
			returnFailResult("参数method不能为空", response);
			return;
		}
		
		try {
			if (method.equals("getWorkInfo")){//加载简历信息
				JSONObject workJson = super.getJsonFromReq(request,"workInfo");
						
				long workId = workJson.getLong("workId");
				long readUserId = getLongFromJSON(workJson,"readUserId",false);
				
				//String token = userJson.getString("token");
				//先判断token是否正确，如果不正确则提示重新登陆

				//加载用户信息
				JSONObject retJson = workDao.getWorkInfo(workId);
				//如果不是工作发布者来看这个工作，则记录查看日志
				if (readUserId>0&&retJson.getLong("userId")!=readUserId){
					workDao.newLookHis(readUserId,workId);
				}
				returnSuccessResult(retJson, response);
			}else if (method.equals("newWork")){//新增简历信息
				JSONObject workJson = super.getJsonFromReq(request,"workInfo");
				String verifyCode = workJson.getString("verifyCode");// 图片验证码
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

				//新增简历
				JSONObject retJson = workDao.newWork(workJson);
				retJson.put("msg", "发布成功！");
				returnSuccessResult(retJson, response);
			}else if (method.equals("modWork")){//修改工作
				JSONObject workJson = super.getJsonFromReq(request,"workInfo");
				String verifyCode = workJson.getString("verifyCode");// 图片验证码
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

				//修改工作
				workDao.modWork(workJson);
				JSONObject retJson =  new JSONObject();
				retJson.put("msg", "修改成功！");
				returnSuccessResult(retJson, response);
			}else if (method.equals("getWorkList")){//加载工作列表
				JSONObject workJson = super.getJsonFromReq(request,"workInfo");
				
				long userId = workJson.containsKey("userId")==true?workJson.getLong("userId"):0;
				long exceptUserId = workJson.containsKey("exceptUserId")==true?workJson.getLong("exceptUserId"):0;
				
				String page = workJson.getString("page");
				//工作类型，partTime,fullTime
				String workType = workJson.getString("workType");
				//工作城市
				String cityName = workJson.containsKey("cityName")==true?workJson.getString("cityName"):"";
				//工作名称
				String workName = super.getStringFromJSON(workJson, "workName", false);
				//工作区域
				String workArea = super.getStringFromJSON(workJson, "workArea", false);
				//经验要求
				String expectExperience =  super.getStringFromJSON(workJson, "expectExperience", false);
				//年龄要求
				String expectAge = super.getStringFromJSON(workJson, "expectAge", false);
				//学历要求
				String expectDegree = super.getStringFromJSON(workJson, "expectDegree", false);
				//加载生效的工作
				String effExpDateFlag = super.getStringFromJSON(workJson, "effExpDateFlag", false);
				//工作岗位
				String postCode = super.getStringFromJSON(workJson, "postCode", false);
				
				//模糊查询条件，值可能是工作id，也可能是工作名称
				String searchKey = super.getStringFromJSON(workJson, "searchKey", false);

				//排序要求
				String orderBy = super.getStringFromJSON(workJson, "orderBy", false);//取值包括:newest 最新/best 评价最好
				Map<String,Object> paramsIn = new HashMap();
				if (userId>0){
					paramsIn.put("userId", userId);
				}
				if (exceptUserId>0){
					paramsIn.put("exceptUserId", exceptUserId);
				}
				if (!StringUtils.isEmpty(page)){
					paramsIn.put("page", page);
				}
				
				if (!StringUtils.isEmpty(workType)){
					String[] workTypes =null;
					if (workType!=null){
						workTypes = workType.split(",");
					}
					
					paramsIn.put("workTypes", workTypes);
				}
				if (!StringUtils.isEmpty(cityName)){
					paramsIn.put("cityName", cityName);
				}
				if (!StringUtils.isEmpty(workName)){
					paramsIn.put("workName", workName);
				}
				
				if (!StringUtils.isEmpty(effExpDateFlag)){
					paramsIn.put("effExpDateFlag", effExpDateFlag);
				}
				if (!StringUtils.isEmpty(postCode)){
					paramsIn.put("postCode", postCode);
				}
				if (!StringUtils.isEmpty(workArea)){
					String[] areas = workArea.split(",");
					paramsIn.put("workArea", areas);
				}
				if (!StringUtils.isEmpty(expectExperience)){
					paramsIn.put("expectExperience", expectExperience);
				}
				if (!StringUtils.isEmpty(expectAge)){
					paramsIn.put("expectAge", expectAge);
				}
				if (!StringUtils.isEmpty(expectDegree)){
					paramsIn.put("expectDegree", expectDegree);
				}
				
				if (!StringUtils.isEmpty(orderBy)){
					paramsIn.put("orderBy", orderBy);
				}
				
				if (!StringUtils.isEmpty(searchKey)){
					Pattern pattern = Pattern.compile("[0-9]*");
					Boolean isDigital = pattern.matcher(searchKey).matches();
					if (isDigital) {
						paramsIn.put("workId", searchKey);
					}else{
						paramsIn.put("workName", searchKey);
					}
				}
				
				//加载简历列表
				JSONArray resumeList = workDao.getWorkList(paramsIn);
				returnSuccessResult(resumeList, response);
			} else if (method.equals("getFocusWorkList")){//加载关注工作列表
				JSONObject workJson = super.getJsonFromReq(request,"workInfo");
				long userId = workJson.containsKey("userId")==true?workJson.getLong("userId"):0;
				int page = workJson.getInt("page");
				String workType = workJson.getString("workType");
				
				String[] workTypes =null;
				if (workType!=null){
					workTypes = workType.split(",");
				}
				//从session中取用户当前所在城市
				//String token = userJson.getString("token");
				//先判断token是否正确，如果不正确则提示重新登陆

				//加载简历列表
				JSONArray resumeList = workDao.getFocusWorkList(userId,workTypes,page);
				returnSuccessResult(resumeList, response);
			} else if (method.equals("getLookedWorkList")){//加载查看过的工作列表
				JSONObject workJson = super.getJsonFromReq(request,"workInfo");
				long userId = workJson.containsKey("userId")==true?workJson.getLong("userId"):0;
				int page = workJson.getInt("page");
				String workType = workJson.getString("workType");
				
				String[] workTypes =null;
				if (workType!=null){
					workTypes = workType.split(",");
				}
				//从session中取用户当前所在城市
				//String token = userJson.getString("token");
				//先判断token是否正确，如果不正确则提示重新登陆

				//加载简历列表
				JSONArray resumeList = workDao.getLookedWorkList(userId,workTypes,page);
				returnSuccessResult(resumeList, response);
			} else if (method.equals("addFocus")) {// 增加关注
				JSONObject focusJson = super.getJsonFromReq(request,"focusInfo");
				long userId = focusJson.getLong("userId");
				long workId = focusJson.getLong("workId");
				// String token = userJson.getString("token");
				// 先判断token是否正确，如果不正确则提示重新登陆

				// 加载用户信息
				long workIdFocusNumber = workDao.addFocus(userId, workId);
				JSONObject retJson = new JSONObject();
				retJson.put("focusNumber", workIdFocusNumber);
				returnSuccessResult(retJson, response);
			} else if (method.equals("bookInterview")) {// 记录预约面试信息
				JSONObject bookJson = super.getJsonFromReq(request,"bookInfo");
				long resumeId = super.getLongFromJSON(bookJson,"resumeId",true);
				long workId = super.getLongFromJSON(bookJson,"workId",true);
				// String token = userJson.getString("token");
				// 先判断token是否正确，如果不正确则提示重新登陆
				
				// 记录预约面试信息
				workDao.bookInterview(resumeId,workId,bookJson);
				JSONObject retJson = new JSONObject();
				retJson.put("msg", "录用成功!");
				returnSuccessResult(retJson, response);
			}else if (method.equals("hiredStaff")) {// 录用员工
				JSONObject hireJson = super.getJsonFromReq(request,"hiredInfo");
				long resumeId = super.getLongFromJSON(hireJson,"resumeId",true);
				long workId = super.getLongFromJSON(hireJson,"workId",true);
				// String token = userJson.getString("token");
				// 先判断token是否正确，如果不正确则提示重新登陆
				
				// 记录录用信息
				workDao.hiredStaff(resumeId,workId,hireJson);
				JSONObject retJson = new JSONObject();
				retJson.put("msg", "录用成功!");
				returnSuccessResult(retJson, response);
			}else if (method.equals("getWorkListByStaffId")) {// 用员工id加载工作列表，包括已完成的工作，预约面试的的工作，已录用的工作
				JSONObject condJson = super.getJsonFromReq(request,"workInfo");
				long staffUserId = super.getLongFromJSON(condJson,"staffUserId",true);
				String state = super.getStringFromJSON(condJson, "state", false);
				int page = 1;
				//long workId = super.getLongFromJSON(hireJson,"workId",true);
				// String token = userJson.getString("token");
				// 先判断token是否正确，如果不正确则提示重新登陆
				
				// 记录录用信息
				JSONArray resumeList = workDao.getWorkListByStaffId(staffUserId,state,page);
				returnSuccessResult(resumeList, response);
			}else if (method.equals("checkInOrOut")) {// 员工签到
				JSONObject condJson = super.getJsonFromReq(request,"workInfo");
				long userId = super.getLongFromJSON(condJson,"userId",true);//用户id
				long workId = super.getLongFromJSON(condJson,"workId",true);//工作id
				String checkType = super.getStringFromJSON(condJson, "checkType", true);//签到or签退
				String positionInfo = null;
				
				if ("checkIn".equals(checkType)){
					positionInfo = super.getStringFromJSON(condJson, "checkInPosition", false);//位置信息
				}else{
					positionInfo = super.getStringFromJSON(condJson, "checkOutPosition", false);//位置信息
				}
				
				
				JSONObject positionJsonObj = null;
				if (!StringUtils.isEmpty(positionInfo)){
					positionJsonObj = JSONObject.fromObject(positionInfo);
				}
				//long workId = super.getLongFromJSON(hireJson,"workId",true);
				// String token = userJson.getString("token");
				// 先判断token是否正确，如果不正确则提示重新登陆
				
				// 记录录用信息
				workDao.checkInOrOut(userId,workId,checkType,positionJsonObj);
				JSONObject retJson = new JSONObject();
				retJson.put("msg", checkType+"成功");
				retJson.put("checkTime", TimeUtil.getLocalTimeString());//返回签到退时间

				returnSuccessResult(retJson, response);
			}else if (method.equals("delFocus")) {// 删除关注
				JSONObject condJson = super.getJsonFromReq(request,"focusInfo");
				long userId = super.getLongFromJSON(condJson,"userId",true);
				long workId = super.getLongFromJSON(condJson,"workId",true);
				
				// String token = userJson.getString("token");
				// 先判断token是否正确，如果不正确则提示重新登陆
				
				// 删除关注
				workDao.delFocus(userId,workId);
				JSONObject retJson = new JSONObject();
				retJson.put("msg", "删除成功");
				
				returnSuccessResult(retJson, response);
			}else if (method.equals("queryAddStaff")){//查询员工并录用
				JSONObject hireJson = super.getJsonFromReq(request,"hiredInfo");
				String cellphone = super.getStringFromJSON(hireJson,"cellphone",true);
				long workId = super.getLongFromJSON(hireJson,"workId",true);
				
				
				// 记录录用信息
				workDao.queryAddStaff(cellphone,workId,hireJson);
				JSONObject retJson = new JSONObject();
				retJson.put("msg", "录用成功!");
				returnSuccessResult(retJson, response);
			}else if (method.equals("saveAssessment")) {// 保存评价信息
				JSONObject assessmentInfo = super.getJsonFromReq(request,"assessmentInfo");
				
				workDao.saveAssessment(assessmentInfo);
				JSONObject retJson = new JSONObject();
				retJson.put("msg", "评价成功!");
				returnSuccessResult(retJson, response);
			}else if (method.equals("modCheckInOutTime")){//调整签到签退时间
				JSONObject condJson = super.getJsonFromReq(request,"workInfo");
				
				long userWorkId = super.getLongFromJSON(condJson, "userWorkId", true);
				String newCheckInTime = super.getStringFromJSON(condJson, "newCheckInTime", true);
				String newCheckOutTime = super.getStringFromJSON(condJson, "newCheckOutTime", true);
				
				workDao.modCheckInOutTime(userWorkId,newCheckInTime,newCheckOutTime);
				JSONObject retJson = new JSONObject();
				retJson.put("msg", "修改成功!");
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
