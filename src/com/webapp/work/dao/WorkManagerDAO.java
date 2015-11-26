package com.webapp.work.dao;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.util.StringUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.webapp.common.bean.AbstractBean;
import com.webapp.common.dao.AbstractDAO;
import com.webapp.common.dao.CharSpecDAO;
import com.webapp.common.util.CharSpecConsts;
import com.webapp.common.util.SMSSendUtil;
import com.webapp.common.util.ServiceFactory;
import com.webapp.common.util.TimeUtil;
import com.webapp.user.bean.UserFocusBean;
import com.webapp.user.bean.UserProdBean;
import com.webapp.user.bean.UserProdCharBean;
import com.webapp.user.bean.UserResumeBean;
import com.webapp.user.bean.UserResumeCharBean;
import com.webapp.user.dao.ResumeManagerDAO;
import com.webapp.user.dao.UserManagerDAO;
import com.webapp.work.bean.BatchPayHisBean;
import com.webapp.work.bean.ProdAssessmentBean;
import com.webapp.work.bean.ProdBean;
import com.webapp.work.bean.ProdCharBean;
import com.webapp.work.bean.ProdLookHisBean;

public class WorkManagerDAO extends AbstractDAO {
	private static CharSpecDAO charSpecDao = null;

	public WorkManagerDAO() {
		charSpecDao = new CharSpecDAO();
		charSpecDao.setDataSource(this.getDataSource());
	}

	/**
	 * 加载工作详情
	 * 
	 * @param workId
	 * @return
	 * @throws Exception
	 */
	public JSONObject getWorkInfo(long workId) throws Exception {
		Map<String, Object> params = new HashMap();
		params.put("PROD_ID", "" + workId);

		ProdBean prodBean = (ProdBean) getBean(ProdBean.class, params);
		if (prodBean == null) {
			throw new Exception("参数workId" + workId + "不正确!");
		}

		// 返回用户基本信息
		JSONObject retJson = new JSONObject();
		retJson.put("userId", prodBean.getUserId());
		retJson.put("workName", prodBean.getProdName());
		retJson.put("workType", prodBean.getProdType());
		retJson.put("workId", prodBean.getProdId());
		retJson.put("effDate", prodBean.getEffDate());
		retJson.put("expDate", prodBean.getExpDate());
		retJson.put("content", prodBean.getContent());
		retJson.put("createDate", prodBean.getCreateDate());
		retJson.put("cityId", prodBean.getCityId());
		// 返回简历特征信息
		params.clear();
		params.put("PROD_ID", "" + workId);
		List<AbstractBean> result = getBeans(ProdCharBean.class, params);
		if (result != null && result.size() > 0) {
			for (AbstractBean abBean : result) {
				ProdCharBean userCharBean = (ProdCharBean) abBean;

				retJson.put(charSpecDao.getCode(userCharBean.getCharId()), userCharBean.getValue());
			}
		}
		// 返回用户信息
		UserManagerDAO userDao = new UserManagerDAO();
		userDao.setDataSource(this.getDataSource());
		JSONObject userInfo = userDao.getUserInfo(prodBean.getUserId());
		if (userInfo != null) {
			retJson.put("userInfo", userInfo);
		}

		// 返回关注次数
		params.put("PROD_ID", "" + workId);
		long focusNumber = super.getBeanCount(UserFocusBean.class, params);
		retJson.put("focusNumber", focusNumber);

		// 返回查看次数
		params.put("PROD_ID", "" + workId);
		long lookNumber = super.getBeanCount(ProdLookHisBean.class, params);
		retJson.put("lookNumber", lookNumber);

		// 返回收到简历数
		params.put("PROD_ID", "" + workId);
		long resumeNumber = super.getBeanCount(UserProdBean.class, params);
		retJson.put("resumeNumber", resumeNumber);
		// 返回当前用户的其它工作数
		params.put("USER_ID", "" + prodBean.getUserId());
		long workNumber = super.getBeanCount(ProdBean.class, params);
		retJson.put("workNumber", workNumber);
		return retJson;
	}
	public JSONObject getWorkBasicInfo(long workId) throws Exception {
		Map<String, Object> params = new HashMap();
		params.put("PROD_ID", "" + workId);

		ProdBean prodBean = (ProdBean) getBean(ProdBean.class, params);
		if (prodBean == null) {
			throw new Exception("参数workId" + workId + "不正确!");
		}

		// 返回用户基本信息
		JSONObject retJson = new JSONObject();
		retJson.put("userId", prodBean.getUserId());
		retJson.put("workName", prodBean.getProdName());
		retJson.put("workType", prodBean.getProdType());
		retJson.put("workId", prodBean.getProdId());
		retJson.put("effDate", prodBean.getEffDate());
		retJson.put("expDate", prodBean.getExpDate());
		retJson.put("content", prodBean.getContent());
		retJson.put("createDate", prodBean.getCreateDate());
		retJson.put("cityId", prodBean.getCityId());
		// 返回简历特征信息
		params.clear();
		params.put("PROD_ID", "" + workId);
		List<AbstractBean> result = getBeans(ProdCharBean.class, params);
		if (result != null && result.size() > 0) {
			for (AbstractBean abBean : result) {
				ProdCharBean userCharBean = (ProdCharBean) abBean;

				retJson.put(charSpecDao.getCode(userCharBean.getCharId()), userCharBean.getValue());
			}
		}
		// 返回用户信息
		UserManagerDAO userDao = new UserManagerDAO();
		userDao.setDataSource(this.getDataSource());
		JSONObject userInfo = userDao.getUserInfo(prodBean.getUserId());
		if (userInfo != null) {
			retJson.put("userInfo", userInfo);
		}

		return retJson;
	}
	/**
	 * 新增工作信息
	 * 
	 * @param workInfo
	 * @return
	 * @throws Exception
	 */
	public JSONObject newWork(JSONObject workInfo) throws Exception {
		JSONObject retJson = new JSONObject();
		ProdBean prodBean = new ProdBean();
		prodBean.setUserId(workInfo.getLong("userId"));
		if (workInfo.containsKey("workName"))
			prodBean.setProdName(workInfo.getString("workName"));
		if (workInfo.containsKey("workType"))
			prodBean.setProdType(workInfo.getString("workType"));

		if (workInfo.containsKey("effDate"))
			prodBean.setEffDate(workInfo.getString("effDate"));

		if (workInfo.containsKey("expDate"))
			prodBean.setExpDate(workInfo.getString("expDate"));

		if (workInfo.containsKey("content"))
			prodBean.setContent(workInfo.getString("content"));

		if (workInfo.containsKey("cityName")) {
			String cityName = workInfo.getString("cityName");
			String cityId = charSpecDao.getRealValue("workCity", cityName);
			if (StringUtils.isEmpty(cityId)) {
				throw new Exception("根据城市名称找不到城市id:" + cityName);
			}
			prodBean.setCityId(cityId);
		}

		prodBean.setCreateDate(TimeUtil.getLocalTimeString());
		long workId = (long) super.insertBean(prodBean);

		// 新建特征
		Set<String> keySet = workInfo.keySet();
		for (String key : keySet) {
			long charId = charSpecDao.getCharId(key);
			if (charId > 0) {
				ProdCharBean prodCharBean = new ProdCharBean();
				prodCharBean.setProdId(workId);
				prodCharBean.setCharId(charId);
				prodCharBean.setValue(workInfo.getString(key));
				prodCharBean.setCreateDate(TimeUtil.getLocalTimeString());
				super.insertBean(prodCharBean);
			}
		}

		return retJson;
	}

	/**
	 * 修改工作信息
	 * 
	 * @param workInfo
	 * @throws Exception
	 */
	public void modWork(JSONObject workInfo) throws Exception {
		// 更新基本信息
		ProdBean prodBean = new ProdBean();
		prodBean.setProdId(workInfo.getLong("workId"));
		if (workInfo.containsKey("workName"))
			prodBean.setProdName(workInfo.getString("workName"));
		if (workInfo.containsKey("workType"))
			prodBean.setProdType(workInfo.getString("workType"));

		if (workInfo.containsKey("effDate"))
			prodBean.setEffDate(workInfo.getString("effDate"));

		if (workInfo.containsKey("expDate"))
			prodBean.setExpDate(workInfo.getString("expDate"));

		if (workInfo.containsKey("content"))
			prodBean.setContent(workInfo.getString("content"));

		super.updateBean(prodBean);

		// 更新user char
		Set<String> keySet = workInfo.keySet();
		for (String key : keySet) {
			long charId = charSpecDao.getCharId(key);
			if (charId > 0) {
				Map<String, Object> params = new HashMap();
				params.put("PROD_ID", workInfo.getString("workId"));
				params.put("CHAR_ID", "" + charId);

				ProdCharBean bean = (ProdCharBean) super.getBean(ProdCharBean.class, params);
				if (bean != null) {
					bean.setValue(workInfo.getString(key));

					updateBean(bean);
				} else {
					ProdCharBean newbean = new ProdCharBean();
					newbean.setProdId(workInfo.getLong("workId"));
					newbean.setCharId(charId);
					newbean.setValue(workInfo.getString(key));
					newbean.setCreateDate(TimeUtil.getLocalTimeString());
					super.insertBean(newbean);
				}
			}
		}
	}

	/**
	 * 加载工作列表
	 * 
	 * @param userId
	 * @param workTypes
	 * @param cityName
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public JSONArray getWorkList(Map<String,Object> paramsIn) throws Exception {
		ResumeManagerDAO resumeDao = new ResumeManagerDAO();
		resumeDao.setDataSource(this.getDataSource());

		Map<String, Object> params = new HashMap();
		if (paramsIn.containsKey("userId")) {
			params.put("USER_ID", "" + paramsIn.get("userId"));
		}
		
		if (paramsIn.containsKey("workId")) {
			params.put("PROD_ID", "" + paramsIn.get("workId"));
		}
		if (paramsIn.containsKey("exceptUserId")) {
			params.put("WHERESQL", "USER_ID != " + paramsIn.get("exceptUserId"));
		}
		
		if (paramsIn.containsKey("workName")) {
			params.put("PROD_NAME", paramsIn.get("workName"));
			params.put("LIKECOL", "PROD_NAME");
		}
		if (paramsIn.containsKey("postCode")) {
			StringBuffer existsSql = new StringBuffer();
			existsSql.append("SELECT 1 FROM PROD_CHAR X WHERE X.PROD_ID=prod.PROD_ID AND X.CHAR_ID=25 AND x.VALUE='").append(paramsIn.get("postCode")).append("'");
			
			List<String> sqlList=  new ArrayList();
			sqlList.add(existsSql.toString());
			params.put("EXISTSSQL",sqlList );
		}
		if (paramsIn.containsKey("workArea")) {
			String[] areas = (String[]) paramsIn.get("workArea");
			
			StringBuffer existsSql = new StringBuffer();
			existsSql.append("SELECT 1 FROM PROD_CHAR X WHERE X.PROD_ID=prod.PROD_ID AND X.CHAR_ID=12 AND x.VALUE in(");
			for (int i=0;i<areas.length;i++){
				existsSql.append("'").append(areas[i]).append("'");
				if (i+1!=areas.length){
					existsSql.append(",");
				}
			}
			existsSql.append(")");
			List<String> sqlList= (List<String>) params.get("EXISTSSQL");
			if (sqlList==null){
				sqlList=  new ArrayList();
			}
			
			sqlList.add(existsSql.toString());
			params.put("EXISTSSQL",sqlList );
		}
		if (paramsIn.containsKey("expectExperience")) {
			String experience = (String) paramsIn.get("expectExperience");
			
			StringBuffer existsSql = new StringBuffer();
			existsSql.append("SELECT 1 FROM PROD_CHAR X WHERE X.PROD_ID=prod.PROD_ID AND X.CHAR_ID=18 AND x.VALUE ='");
			
			existsSql.append(experience).append("'");
			List<String> sqlList= (List<String>) params.get("EXISTSSQL");
			if (sqlList==null){
				sqlList=  new ArrayList();
			}
			
			sqlList.add(existsSql.toString());
			params.put("EXISTSSQL",sqlList );
		}
		if (paramsIn.containsKey("expectAge")) {
			String experience = (String) paramsIn.get("expectAge");
			
			StringBuffer existsSql = new StringBuffer();
			existsSql.append("SELECT 1 FROM PROD_CHAR X WHERE X.PROD_ID=prod.PROD_ID AND X.CHAR_ID=19 AND x.VALUE ='");
			
			existsSql.append(experience).append("'");
			List<String> sqlList= (List<String>) params.get("EXISTSSQL");
			if (sqlList==null){
				sqlList=  new ArrayList();
			}
			
			sqlList.add(existsSql.toString());
			params.put("EXISTSSQL",sqlList );
		}
		if (paramsIn.containsKey("expectDegree")) {
			String expectDegree = (String) paramsIn.get("expectDegree");
			
			StringBuffer existsSql = new StringBuffer();
			existsSql.append("SELECT 1 FROM PROD_CHAR X WHERE X.PROD_ID=prod.PROD_ID AND X.CHAR_ID=17 AND x.VALUE ='");
			
			existsSql.append(expectDegree).append("'");
			List<String> sqlList= (List<String>) params.get("EXISTSSQL");
			if (sqlList==null){
				sqlList=  new ArrayList();
			}
			
			sqlList.add(existsSql.toString());
			params.put("EXISTSSQL",sqlList );
		}
		
		if (paramsIn.containsKey("workTypes")) {
			params.put("PROD_TYPE", paramsIn.get("workTypes"));
		}
		if (paramsIn.containsKey("cityName")) {
			String cityId = charSpecDao.getRealValue("workCity", paramsIn.get("cityName").toString());
			params.put("CITY_ID", "" + cityId);
		}
		if (paramsIn.containsKey("page")) {
			params.put("PAGE", paramsIn.get("page"));
		}
		if (paramsIn.containsKey("effExpDateFlag")) {// 加载生效时间内的
			params.put("effExpDateFlag", paramsIn.get("effExpDateFlag"));
		}
		if (paramsIn.containsKey("orderBy")) {
			String orderby = (String) paramsIn.get("orderBy");
			if ("newest".equals(orderby)){//最新
				params.put("ORDERBY","EFF_DATE DESC" );
			}else{//best 评价最好
				params.put("ORDERBY","ASSESSMENT DESC" );
			}
		}
		

		JSONArray prodList = new JSONArray();
		List<AbstractBean> result = getBeans(ProdBean.class, params);

		if (result != null && result.size() > 0) {
			UserManagerDAO userDao = new UserManagerDAO();
			userDao.setDataSource(this.getDataSource());

			for (AbstractBean abBean : result) {
				ProdBean prodBean = (ProdBean) abBean;
				// 返回用户基本信息
				JSONObject retJson = new JSONObject();
				retJson.put("userId", prodBean.getUserId());

				JSONObject userInfo = userDao.getUserInfo(prodBean.getUserId());
				retJson.put("userName", userInfo.getString("userName"));// 返回用户名
				retJson.put("userCredit", userInfo.getString("userCredit"));// 返回用户信用度
				retJson.put("workName", prodBean.getProdName());
				retJson.put("workType", prodBean.getProdType());
				retJson.put("workId", prodBean.getProdId());
				retJson.put("effDate", prodBean.getEffDate());
				retJson.put("expDate", prodBean.getExpDate());
				retJson.put("content", prodBean.getContent());
				retJson.put("createDate", prodBean.getCreateDate());
				// 返回简历特征信息
				params.clear();
				params.put("PROD_ID", "" + prodBean.getProdId());
				List<AbstractBean> prodCharResult = getBeans(ProdCharBean.class, params);
				if (prodCharResult != null && prodCharResult.size() > 0) {
					for (AbstractBean prodCharAbBean : prodCharResult) {
						ProdCharBean prodCharBean = (ProdCharBean) prodCharAbBean;

						retJson.put(charSpecDao.getCode(prodCharBean.getCharId()), prodCharBean.getValue());
					}
				}

				// 如果是求职则同时返回简历信息
				if (retJson.containsKey("resumeId")) {
					JSONObject resumeInfo = resumeDao.getResumeInfo(retJson.getLong("resumeId"));
					if (resumeInfo != null) {
						retJson.put("resumeInfo", resumeInfo);
					}
				}

				// 返回关注次数
				params.put("PROD_ID", "" + prodBean.getProdId());
				long focusNumber = super.getBeanCount(UserFocusBean.class, params);
				retJson.put("focusNumber", focusNumber);

				// 返回查看次数
				params.put("PROD_ID", "" + prodBean.getProdId());
				long lookNumber = super.getBeanCount(ProdLookHisBean.class, params);
				retJson.put("lookNumber", lookNumber);

				// 返回收到简历数
				params.put("PROD_ID", "" + prodBean.getProdId());
				long resumeNumber = super.getBeanCount(UserProdBean.class, params);
				retJson.put("resumeNumber", resumeNumber);

				prodList.add(retJson);
			}

		}

		return prodList;
	}

	/**
	 * 加载关注的工作列表
	 * 
	 * @param userId
	 * @param workTypes
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public JSONArray getFocusWorkList(long userId, String[] workTypes, int page) throws Exception {
		ResumeManagerDAO resumeDao = new ResumeManagerDAO();
		resumeDao.setDataSource(this.getDataSource());

		Map<String, Object> params = new HashMap();
		if (userId > 0) {
			params.put("USER_ID", "" + userId);
		}
		JSONArray prodList = new JSONArray();
		List<AbstractBean> result = getBeans(UserFocusBean.class, params);

		if (result != null && result.size() > 0) {
			UserManagerDAO userDao = new UserManagerDAO();
			userDao.setDataSource(this.getDataSource());

			for (AbstractBean abBean : result) {
				UserFocusBean focusBean = (UserFocusBean) abBean;
				params.clear();
				params.put("PROD_ID", focusBean.getProdId());

				ProdBean prodBean = (ProdBean) super.getBean(ProdBean.class, params);
				if (prodBean == null) {
					continue;
				}
				// 返回用户基本信息
				JSONObject retJson = new JSONObject();
				retJson.put("userId", prodBean.getUserId());

				JSONObject userInfo = userDao.getUserInfo(prodBean.getUserId());
				retJson.put("userName", userInfo.getString("userName"));// 返回用户名
				retJson.put("userCredit", userInfo.getString("userCredit"));// 返回用户信用度
				retJson.put("workName", prodBean.getProdName());
				retJson.put("workType", prodBean.getProdType());
				/*
				 * retJson.put("workType",
				 * charSpecDao.getDisplayValue("workType",prodBean.getProdType()
				 * ));
				 */
				retJson.put("workId", prodBean.getProdId());
				retJson.put("effDate", prodBean.getEffDate());
				retJson.put("expDate", prodBean.getExpDate());
				retJson.put("content", prodBean.getContent());
				retJson.put("createDate", prodBean.getCreateDate());
				// 返回简历特征信息
				params.clear();
				params.put("PROD_ID", "" + prodBean.getProdId());
				List<AbstractBean> prodCharResult = getBeans(ProdCharBean.class, params);
				if (prodCharResult != null && prodCharResult.size() > 0) {
					for (AbstractBean prodCharAbBean : prodCharResult) {
						ProdCharBean prodCharBean = (ProdCharBean) prodCharAbBean;

						retJson.put(charSpecDao.getCode(prodCharBean.getCharId()), prodCharBean.getValue());
					}
				}

				// 如果是求职则同时返回简历信息
				if (retJson.containsKey("resumeId")) {
					JSONObject resumeInfo = resumeDao.getResumeInfo(retJson.getLong("resumeId"));
					if (resumeInfo != null) {
						retJson.put("resumeInfo", resumeInfo);
					}
				}

				// 返回关注次数
				params.put("PROD_ID", "" + prodBean.getProdId());
				long focusNumber = super.getBeanCount(UserFocusBean.class, params);
				retJson.put("focusNumber", focusNumber);

				// 返回查看次数
				params.put("PROD_ID", "" + prodBean.getProdId());
				long lookNumber = super.getBeanCount(ProdLookHisBean.class, params);
				retJson.put("lookNumber", lookNumber);

				// 返回收到简历数
				params.put("PROD_ID", "" + prodBean.getProdId());
				long resumeNumber = super.getBeanCount(UserProdBean.class, params);
				retJson.put("resumeNumber", resumeNumber);

				prodList.add(retJson);
			}

		}

		return prodList;
	}

	/**
	 * 加载查看过的工作列表
	 * 
	 * @param userId
	 * @param workTypes
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public JSONArray getLookedWorkList(long userId, String[] workTypes, int page) throws Exception {
		ResumeManagerDAO resumeDao = new ResumeManagerDAO();
		resumeDao.setDataSource(this.getDataSource());

		Map<String, Object> params = new HashMap();
		if (userId > 0) {
			params.put("USER_ID", "" + userId);
		}
		params.put("PAGE", "" + page);
		params.put("ORDERBY", "CREATE_DATE DESC");
		JSONArray prodList = new JSONArray();
		List<AbstractBean> result = getBeans(ProdLookHisBean.class, params);

		if (result != null && result.size() > 0) {
			UserManagerDAO userDao = new UserManagerDAO();
			userDao.setDataSource(this.getDataSource());

			/*Set<Long> prodIdSet = new HashSet();*/// 过滤重复的工作

			for (AbstractBean abBean : result) {
				ProdLookHisBean focusBean = (ProdLookHisBean) abBean;

				/*if (prodIdSet.contains(focusBean.getProdId())) {
					continue;
				}
				prodIdSet.add(focusBean.getProdId());*/
				params.clear();
				params.put("PROD_ID", focusBean.getProdId());

				ProdBean prodBean = (ProdBean) super.getBean(ProdBean.class, params);
				if (prodBean == null) {
					continue;
				}
				// 返回用户基本信息
				JSONObject retJson = new JSONObject();
				retJson.put("userId", prodBean.getUserId());

				JSONObject userInfo = userDao.getUserInfo(prodBean.getUserId());
				retJson.put("userName", userInfo.getString("userName"));// 返回用户名
				retJson.put("userCredit", userInfo.getString("userCredit"));// 返回用户信用度
				retJson.put("workName", prodBean.getProdName());
				retJson.put("workType", prodBean.getProdType());
				/*
				 * retJson.put("workType",
				 * charSpecDao.getDisplayValue("workType",prodBean.getProdType()
				 * ));
				 */
				retJson.put("workId", prodBean.getProdId());
				retJson.put("effDate", prodBean.getEffDate());
				retJson.put("expDate", prodBean.getExpDate());
				retJson.put("content", prodBean.getContent());
				retJson.put("createDate", prodBean.getCreateDate());
				// 返回简历特征信息
				params.clear();
				params.put("PROD_ID", "" + prodBean.getProdId());
				List<AbstractBean> prodCharResult = getBeans(ProdCharBean.class, params);
				if (prodCharResult != null && prodCharResult.size() > 0) {
					for (AbstractBean prodCharAbBean : prodCharResult) {
						ProdCharBean prodCharBean = (ProdCharBean) prodCharAbBean;

						retJson.put(charSpecDao.getCode(prodCharBean.getCharId()), prodCharBean.getValue());
					}
				}

				// 如果是求职则同时返回简历信息
				if (retJson.containsKey("resumeId")) {
					JSONObject resumeInfo = resumeDao.getResumeInfo(retJson.getLong("resumeId"));
					if (resumeInfo != null) {
						retJson.put("resumeInfo", resumeInfo);
					}
				}

				// 返回关注次数
				params.put("PROD_ID", "" + prodBean.getProdId());
				long focusNumber = super.getBeanCount(UserFocusBean.class, params);
				retJson.put("focusNumber", focusNumber);

				// 返回查看次数
				params.put("PROD_ID", "" + prodBean.getProdId());
				long lookNumber = super.getBeanCount(ProdLookHisBean.class, params);
				retJson.put("lookNumber", lookNumber);

				// 返回收到简历数
				params.put("PROD_ID", "" + prodBean.getProdId());
				long resumeNumber = super.getBeanCount(UserProdBean.class, params);
				retJson.put("resumeNumber", resumeNumber);

				prodList.add(retJson);
			}

		}

		return prodList;
	}

	/**
	 * 记录工作查看记录
	 * 
	 * @param userId
	 * @param workId
	 */
	public void newLookHis(long userId, long workId) {
		ProdLookHisBean hisBean = new ProdLookHisBean();
		hisBean.setUserId(userId);
		hisBean.setProdId(workId);
		hisBean.setCreateDate(TimeUtil.getLocalTimeString());
		super.insertBean(hisBean);
	}

	/**
	 * 记录工作关注记录
	 * 
	 * @param userId
	 * @param workId
	 * @return
	 * @throws Exception
	 */
	public long addFocus(long userId, long workId) throws Exception {
		Map<String, Object> params = new HashMap();
		params.put("USER_ID", "" + userId);
		params.put("PROD_ID", "" + workId);
		UserFocusBean oldFocusBean = (UserFocusBean) super.getBean(UserFocusBean.class, params);
		if (oldFocusBean != null) {
			throw new Exception("你已关注过!");
		}
		UserFocusBean focusBean = new UserFocusBean();
		focusBean.setUserId(userId);
		focusBean.setProdId(workId);
		focusBean.setCreateDate(TimeUtil.getLocalTimeString());

		super.insertBean(focusBean);

		params.clear();
		params.put("PROD_ID", "" + workId);
		long count = super.getBeanCount(UserFocusBean.class, params);

		return count;
	}

	/**
	 * 删除关注的工作
	 * 
	 * @param userId
	 * @param workId
	 * @throws Exception
	 */
	public void delFocus(long userId, long workId) throws Exception {
		Map<String, Object> params = new HashMap();
		params.put("USER_ID", "" + userId);
		params.put("PROD_ID", "" + workId);
		UserFocusBean oldFocusBean = (UserFocusBean) super.getBean(UserFocusBean.class, params);
		if (oldFocusBean == null) {
			return;
		}

		super.deleteBean(oldFocusBean);
	}

	/**
	 * 记录员工录用信息
	 * 
	 * @param resumeId
	 * @param workId
	 * @param hireJson
	 * @throws Exception
	 */
	public void hiredStaff(long resumeId, long workId, JSONObject hireJson) throws Exception {
		Map<String, Object> params = new HashMap();
		params.put("RESUME_ID", "" + resumeId);
		UserResumeBean userResumeBean = (UserResumeBean) super.getBean(UserResumeBean.class, params);
		if (userResumeBean == null) {
			throw new Exception("未找到此工作收到的简历!");
		}
		params.clear();
		params.put("PROD_ID", "" + workId);
		params.put("USER_ID", "" + userResumeBean.getUserId());

		UserProdBean userWorkBean = (UserProdBean) super.getBean(UserProdBean.class, params);
		userWorkBean.setState("hired");

		super.updateBean(userWorkBean);

		// 加载已有特征
		params.clear();
		params.put("USER_PROD_ID", "" + userWorkBean.getUserProdId());
		List<AbstractBean> charList = super.getBeans(UserProdCharBean.class, params);

		// 新建特征
		Set<String> keySet = hireJson.keySet();
		for (String key : keySet) {
			long charId = charSpecDao.getCharId(key);
			if (charId > 0) {
				boolean existflag = false;
				// 如果特征已存在则更新特征值
				if (charList != null && charList.size() > 0) {
					for (AbstractBean abBean : charList) {
						UserProdCharBean charBean = (UserProdCharBean) abBean;
						if (charBean.getCharId() == charId) {
							charBean.setValue(hireJson.getString(key));
							super.updateBean(charBean);
							existflag = true;
							continue;
						}
					}
				}
				if (existflag == false) {
					UserProdCharBean prodCharBean = new UserProdCharBean();
					prodCharBean.setUserProdId(userWorkBean.getUserProdId());
					prodCharBean.setCharId(charId);
					prodCharBean.setValue(hireJson.getString(key));
					prodCharBean.setCreateDate(TimeUtil.getLocalTimeString());
					super.insertBean(prodCharBean);
				}
			}
		}

		// 发员工录用通知短信----------------------------------------------------------开始
		// 取工作名称
		String workName = null;
		params.clear();
		params.put("PROD_ID", "" + workId);
		ProdBean workBean = (ProdBean) super.getBean(ProdBean.class, params);
		workName = workBean.getProdName();

		String[] msgparams = new String[4];
		msgparams[0] = workName;// 工作名称
		msgparams[1] = hireJson.getString("workTimeBegin");// 工作开始时间
		msgparams[2] = hireJson.getString("workTimeEnd");// 工作结束时间
		msgparams[3] = hireJson.getString("workAddress");// 工作地点

		// 取得简历用户的手机号码
		UserManagerDAO userDao = new UserManagerDAO();
		userDao.setDataSource(this.getDataSource());
		String cellphone = userDao.getCellPhoneByUserId(userResumeBean.getUserId());
		SMSSendUtil.sendSMS(cellphone, SMSSendUtil.STAFFHIRED_TEMPLATE, msgparams);
		// 发员工录用通知短信----------------------------------------------------------结束
	}

	/**
	 * 记录员工预约面试信息
	 * 
	 * @param resumeId
	 * @param workId
	 * @param hireJson
	 * @throws Exception
	 */
	public void bookInterview(long resumeId, long workId, JSONObject bookJson) throws Exception {
		Map<String, Object> params = new HashMap();
		params.put("RESUME_ID", "" + resumeId);
		UserResumeBean userResumeBean = (UserResumeBean) super.getBean(UserResumeBean.class, params);
		if (userResumeBean == null) {
			throw new Exception("未找到此工作收到的简历!");
		}
		params.clear();
		params.put("PROD_ID", "" + workId);
		params.put("USER_ID", "" + userResumeBean.getUserId());

		UserProdBean userWorkBean = (UserProdBean) super.getBean(UserProdBean.class, params);
		userWorkBean.setState("reserved");

		super.updateBean(userWorkBean);

		// 加载已有特征
		params.clear();
		params.put("USER_PROD_ID", "" + userWorkBean.getUserProdId());
		List<AbstractBean> charList = super.getBeans(UserProdCharBean.class, params);
		// 新建特征
		Set<String> keySet = bookJson.keySet();
		for (String key : keySet) {
			long charId = charSpecDao.getCharId(key);
			if (charId > 0) {
				boolean existflag = false;
				// 如果特征已存在则更新特征值
				if (charList != null && charList.size() > 0) {
					for (AbstractBean abBean : charList) {
						UserProdCharBean charBean = (UserProdCharBean) abBean;
						if (charBean.getCharId() == charId) {
							charBean.setValue(bookJson.getString(key));
							super.updateBean(charBean);
							existflag = true;
							continue;
						}
					}
				}
				if (existflag == false) {
					UserProdCharBean prodCharBean = new UserProdCharBean();
					prodCharBean.setUserProdId(userWorkBean.getUserProdId());
					prodCharBean.setCharId(charId);
					prodCharBean.setValue(bookJson.getString(key));
					prodCharBean.setCreateDate(TimeUtil.getLocalTimeString());
					super.insertBean(prodCharBean);
				}
			}
		}

		// 发送预约面试短信----------------------------------------------------------开始
		// 取工作名称
		String workName = null;
		String staffName = null;
		params.clear();
		params.put("PROD_ID", "" + workId);
		ProdBean workBean = (ProdBean) super.getBean(ProdBean.class, params);
		workName = workBean.getProdName();

		params.clear();
		params.put("RESUME_ID", "" + resumeId);
		params.put("CHAR_ID", "" + CharSpecConsts.REAL_NAME);
		UserResumeCharBean resumeChar = (UserResumeCharBean) super.getBean(UserResumeCharBean.class, params);
		if (resumeChar != null) {
			staffName = resumeChar.getValue();
		} else {
			staffName = "求职者";
		}

		String[] msgparams = new String[4];
		msgparams[0] = staffName;// 接收人，取简历上的真实姓名
		msgparams[1] = bookJson.getString("interviewTime");// 预约面试时间
		msgparams[2] = bookJson.getString("interviewAddress");// 预约面试地点
		msgparams[3] = workName;// 工作名称

		// 取得简历用户的手机号码
		UserManagerDAO userDao = new UserManagerDAO();
		userDao.setDataSource(this.getDataSource());
		String cellphone = userDao.getCellPhoneByUserId(userResumeBean.getUserId());
		SMSSendUtil.sendSMS(cellphone, SMSSendUtil.INTERVIEW_NOTICE_TEMPLATE, msgparams);
		// 发送预约面试短信----------------------------------------------------------结束
	}

	/**
	 * 查询员工投过简历的工作
	 * 
	 * @param userId
	 * @param state
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public JSONArray getWorkListByStaffId(long userId, String state, int page) throws Exception {
		ResumeManagerDAO resumeDao = new ResumeManagerDAO();
		resumeDao.setDataSource(this.getDataSource());

		Map<String, Object> params = new HashMap();
		if (userId > 0) {
			params.put("USER_ID", "" + userId);
		}
		if (!StringUtils.isEmpty(state)) {
			params.put("STATE", state);
		}

		JSONArray prodList = new JSONArray();
		List<AbstractBean> result = getBeans(UserProdBean.class, params);

		if (result != null && result.size() > 0) {
			UserManagerDAO userDao = new UserManagerDAO();
			userDao.setDataSource(this.getDataSource());

			for (AbstractBean abBean : result) {
				UserProdBean userWorkBean = (UserProdBean) abBean;

				params.clear();
				params.put("PROD_ID", "" + userWorkBean.getProdId());

				ProdBean prodBean = (ProdBean) super.getBean(ProdBean.class, params);

				// 返回用户基本信息
				JSONObject retJson = new JSONObject();
				retJson.put("state", userWorkBean.getState());

				retJson.put("userId", prodBean.getUserId());

				JSONObject userInfo = userDao.getUserBasicInfo(prodBean.getUserId());
				retJson.put("userName", userInfo.getString("userName"));// 返回用户名
				retJson.put("workName", prodBean.getProdName());
				retJson.put("workType", prodBean.getProdType());
				retJson.put("workId", prodBean.getProdId());
				retJson.put("effDate", prodBean.getEffDate());
				retJson.put("expDate", prodBean.getExpDate());
				retJson.put("content", prodBean.getContent());
				retJson.put("createDate", prodBean.getCreateDate());
				// 返回工作特征信息
				params.clear();
				params.put("PROD_ID", "" + prodBean.getProdId());
				List<AbstractBean> prodCharResult = getBeans(ProdCharBean.class, params);
				if (prodCharResult != null && prodCharResult.size() > 0) {
					for (AbstractBean prodCharAbBean : prodCharResult) {
						ProdCharBean prodCharBean = (ProdCharBean) prodCharAbBean;

						retJson.put(charSpecDao.getCode(prodCharBean.getCharId()), prodCharBean.getValue());
					}
				}

				// 返回员工工作信息
				params.clear();
				params.put("USER_PROD_ID", "" + userWorkBean.getUserProdId());
				List<AbstractBean> useProdCharList = super.getBeans(UserProdCharBean.class, params);

				if (useProdCharList != null && useProdCharList.size() > 0) {
					for (AbstractBean abUserProdCharBean : useProdCharList) {
						UserProdCharBean userProdCharBean = (UserProdCharBean) abUserProdCharBean;

						retJson.put(charSpecDao.getCode(userProdCharBean.getCharId()), userProdCharBean.getValue());
					}
				}
				
				//返回支付信息
				params.clear();
				params.put("USER_WORK_ID", "" + userWorkBean.getUserProdId());
				params.put("ORDERBY", "CREATE_DATE DESC");
				params.put("FIRSTROW", "TRUE");

				BatchPayHisBean payHisBean = (BatchPayHisBean) super.getBean(BatchPayHisBean.class, params);
				if (payHisBean!=null){
					retJson.put("batchNo", payHisBean.getBatchNo());
					retJson.put("payState", payHisBean.getState());
					retJson.put("payRemark", payHisBean.getRemark());
				}

				prodList.add(retJson);
			}
		}

		return prodList;
	}

	/**
	 * 员工签到或签退
	 * 
	 * @param userId
	 * @param workId
	 * @param checkType
	 *            取值：checkIn签到，checkOut签退
	 * @param positionJsonObj
	 * @throws Exception
	 */
	public void checkInOrOut(long userId, long workId, String checkType, JSONObject positionJsonObj) throws Exception {
		Map<String, Object> params = new HashMap();
		params.put("USER_ID", "" + userId);
		params.put("PROD_ID", "" + workId);

		UserProdBean userWorkBean = (UserProdBean) super.getBean(UserProdBean.class, params);
		if (userWorkBean == null) {
			throw new Exception("根据userid和workId未找到数据!");
		}
		userWorkBean.setState(checkType);
		super.updateBean(userWorkBean);

		JSONObject charJson = new JSONObject();
		// 生成签到和签退时间
		if ("checkIn".equals(checkType)) {
			charJson.put("checkInTime", TimeUtil.getLocalTimeString());
			charJson.put("checkInPosition", positionJsonObj.toString());
		} else {
			String checkOutTime = TimeUtil.getLocalTimeString();
			charJson.put("checkOutTime",checkOutTime );
			charJson.put("checkOutPosition", positionJsonObj.toString());
			
			//取签到时间
			params.clear();
			params.put("CHAR_ID", "47");//签到时间
			params.put("USER_PROD_ID", "" + userWorkBean.getUserProdId());
			
			String checkInTime = null;
			UserProdCharBean upCharBean = (UserProdCharBean) super.getBean(UserProdCharBean.class, params);
			if (upCharBean!=null){
				checkInTime = upCharBean.getValue();
			}else{
				throw new Exception("没有签到时间!");
			}
			JSONObject retJson = calTimeAndMoney(userWorkBean.getUserProdId(),checkInTime,checkOutTime);
			charJson.putAll(retJson);
		}

		
		// 记录时间和位置信息
		Set<String> keySet = charJson.keySet();
		for (String key : keySet) {
			long charId = charSpecDao.getCharId(key);
			if (charId > 0) {
				UserProdCharBean prodCharBean = new UserProdCharBean();
				prodCharBean.setUserProdId(userWorkBean.getUserProdId());
				prodCharBean.setCharId(charId);
				prodCharBean.setValue(charJson.getString(key));
				prodCharBean.setCreateDate(TimeUtil.getLocalTimeString());
				super.insertBean(prodCharBean);
			}
		}
	}
	
	//计算应付工资
	private JSONObject calTimeAndMoney(long userWorkId,String checkInTime,String checkOutTime){
		JSONObject retJson = new JSONObject();
		Map<String, Object> params = new HashMap();
		params.put("USER_PROD_ID", ""+userWorkId);
		UserProdBean userWorkBean = (UserProdBean) super.getBean(UserProdBean.class, params);
		
		//取工作上配置的工资计算时间单位和值
		params.clear();
		params.put("CHAR_ID", new String[]{"14","53"});//14为薪资单位额度，53为薪资单位
		params.put("PROD_ID", "" + userWorkBean.getProdId());
		
		List<AbstractBean> sallaryBeans = super.getBeans(ProdCharBean.class, params);
		if (sallaryBeans!=null&&sallaryBeans.size()>0){
			String value = null;
			String unit = null;
			for (AbstractBean bean:sallaryBeans){
				ProdCharBean workCharBean = (ProdCharBean) bean;
				if (workCharBean.getCharId()==14){
					value = workCharBean.getValue();
				}else{
					unit = workCharBean.getValue();
				}
			}
			
			
			//签退时间-签到时间，得到工作时长，然后根据单位进行换算后*单位额度
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); 
			try 
			{ 
				Date beginDate = format.parse(checkInTime); 
				Date endDate= format.parse(checkOutTime); 
				double timeValue =endDate.getTime()-beginDate.getTime();
				
				if (unit.equals("hour")){
					double hour = timeValue/(60*60*1000);
					double f = formatDouble(hour);
					retJson.put("workDuration",f+"小时");
					retJson.put("totalPay", formatDouble(f*Double.valueOf(value)));
				} else if (unit.equals("day")){
					double day = timeValue/(24*60*60*1000);
					double f = formatDouble(day);
					retJson.put("workDuration", f+"天");
					retJson.put("totalPay", formatDouble(f*Double.valueOf(value)));
				}
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		return retJson;
	}

	private double formatDouble(double value){
		BigDecimal   fHour   =   new   BigDecimal(value);  
		return  fHour.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();  
	}
	
	public void queryAddStaff(String cellphone, long workId, JSONObject hireJson) throws Exception {
		// 通过手机号找到用户并选择一个简历
		UserManagerDAO userDao = new UserManagerDAO();
		userDao.setDataSource(getDataSource());

		long userId = userDao.getUserIdByCellPhone(cellphone);
		ResumeManagerDAO resumeDao = new ResumeManagerDAO();
		resumeDao.setDataSource(getDataSource());
		JSONArray resumeList = resumeDao.getResumeList(userId, 1);
		if (resumeList != null && resumeList.size() > 0) {
			long resumeId = resumeList.getJSONObject(0).getLong("resumeId");

			this.hiredStaff(resumeId, workId, hireJson);
		} else {
			throw new Exception("号码为" + cellphone + "的用户还未增加过简历!");
		}
	}

	public JSONArray getCanPayStaffList(long workId) throws Exception {
		JSONArray staffList = new JSONArray();
		Map<String, Object> params = new HashMap();
		params.put("PROD_ID", "" + workId);
		params.put("STATE", "checkOut");// 签退状态

		List<AbstractBean> list = super.getBeans(UserProdBean.class, params);
		if (list != null) {
			UserManagerDAO userDao = new UserManagerDAO();
			userDao.setDataSource(this.getDataSource());

			for (AbstractBean bean : list) {
				UserProdBean realBean = (UserProdBean) bean;
				long staffId = realBean.getUserId();
				// 判断员工是否绑定了支付宝，有支付宝才可发工资
				// 判断员工是否已发过，没发过的才能发放
				JSONObject staffJson = new JSONObject();
				JSONObject basicInfo = userDao.getUserBasicInfo(staffId);
				if (basicInfo != null) {// 取支付宝帐号和名称
					staffJson.put("alipayNo", basicInfo.getString("alipayNo"));
					staffJson.put("alipayName", basicInfo.getString("alipayName"));
				}
				// 取真实姓名

				// 取签到签退时间计算应发工资

			}
		}

		return staffList;
	}

	/**记录工资发放日志
	 * 工作发布人确认可以发工资时调用此接口，记录发放目标帐户及金额，批次
	 * @param payObject
	 * @throws Exception
	 */
	public void logBatchPay(JSONObject payObject) throws Exception {
		JSONArray receiverList = payObject.getJSONArray("receiverList");
		String batchNo = payObject.getString("batchNo");

		StringBuffer detail = new StringBuffer();
		for (int i = 0; i < receiverList.size(); i++) {
			JSONObject obj = receiverList.getJSONObject(i);

			BatchPayHisBean hisBean = (BatchPayHisBean) super.newBean(BatchPayHisBean.class);
			hisBean.setUserWorkId(obj.getLong("userWorkId"));
			// 记录工资发放批次号
			hisBean.setBatchNo(batchNo);
			// 记录工资发放序号
			//hisBean.setSeq(obj.getString("seq"));
			// 记录工资发放金额
			hisBean.setMoney(obj.getString("money"));
			hisBean.setDestAlipayNo(obj.getString("targetAcctNo"));
			hisBean.setDestAlipayName(obj.getString("targetAcctName"));
			hisBean.setState("toPay");// toPay等待支付,paying支付中，表示提交到alipay ,success 支付成功,fail 支付失败

			hisBean.setCreateDate(TimeUtil.getLocalTimeString());
			super.insertBean(hisBean);
		}
	}
	
	/**
	 * 当调用支付宝接口进行支付时，更新支付状态为支付中，记录支付批次id
	 * @param receiverJson
	 */
	public void logAlipayHis(JSONObject receiverJson){
		String batchNo = receiverJson.getString("batchNo");
		JSONArray recList = receiverJson.getJSONArray("receiverList");
		for (int i=0;i<recList.size();i++){
			JSONObject obj = recList.getJSONObject(i);
			Map<String, Object> params = new HashMap();
			params.put("PAY_HIS_ID", obj.getString("payHisId"));
			
			BatchPayHisBean hisBean = (BatchPayHisBean) super.getBean(BatchPayHisBean.class, params);
			if (hisBean!=null){
				hisBean.setAlipayBatchNo(batchNo);
				hisBean.setState("paying");
				hisBean.setSeq(obj.getString("seq"));
				hisBean.setUpdateDate(TimeUtil.getLocalTimeString());
				
				super.updateBean(hisBean);
			}
		}
	}

	/**
	 * 根据支付宝返回的支付结果，更新支付状态
	 * @param resultArray
	 * @throws Exception
	 */
	public void updateBatchPay(JSONArray resultArray) throws Exception {
		for (int i = 0; i < resultArray.size(); i++) {
			JSONObject obj = resultArray.getJSONObject(i);

			Map<String, Object> params = new HashMap();
			params.put("ALIPAY_BATCH_NO", obj.getString("batchNo"));
			params.put("SEQ", obj.getString("seq"));

			BatchPayHisBean hisBean = (BatchPayHisBean) super.getBean(BatchPayHisBean.class, params);
			if (hisBean != null) {
				hisBean.setState(obj.getString("state"));// paying
															// 支付中，表示提交到alipay
															// ,success
															// 支付成功,fail 支付失败
				hisBean.setRemark(obj.getString("remark"));
				hisBean.setUpdateDate(TimeUtil.getLocalTimeString());
				super.updateBean(hisBean);
			}
		}
	}

	/**
	 * 记录工作评价信息
	 * @param assessmentInfo
	 * @throws Exception
	 */
	public void saveAssessment(JSONObject assessmentInfo) throws Exception{
		long workId = assessmentInfo.getLong("workId");//被评价工作
		long userId = assessmentInfo.getLong("userId");//评价用户
		
		JSONArray valueList = assessmentInfo.getJSONArray("assessmentList");
		if (valueList.size()>0){
			for (int i=0;i<valueList.size();i++){
				JSONObject obj = valueList.getJSONObject(i);
				
				ProdAssessmentBean bean = (ProdAssessmentBean) super.newBean(ProdAssessmentBean.class);
				bean.setProdId(workId);
				bean.setUserId(userId);
				bean.setAssessmentType(obj.getString("assessType"));
				bean.setAssessmentValue(obj.getLong("assessValue"));
				
				super.insertBean(bean);
			}
		}else{
			throw new Exception("评价信息不完整");
		}
	}
	
	public void modCheckInOutTime(long userWorkId, String newCheckInTime, String newCheckOutTime)
			throws Exception {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date beginDate = format.parse(newCheckInTime);
		Date endDate = format.parse(newCheckOutTime);
		if (endDate.getTime() <= beginDate.getTime()) {
			throw new Exception("签退时间不能小于等于签到时间!");
		}
		UserProdCharBean chekInBean = (UserProdCharBean) super.newBean(UserProdCharBean.class);
		
		chekInBean.setUserProdId(userWorkId);
		chekInBean.setCharId(71);//
		chekInBean.setValue(newCheckInTime);
		chekInBean.setCreateDate(TimeUtil.getLocalTimeString());
		super.insertBean(chekInBean);
		
		UserProdCharBean chekOutBean = (UserProdCharBean) super.newBean(UserProdCharBean.class);
		
		chekOutBean.setUserProdId(userWorkId);
		chekOutBean.setCharId(72);//
		chekOutBean.setValue(newCheckOutTime);
		chekOutBean.setCreateDate(TimeUtil.getLocalTimeString());
		super.insertBean(chekOutBean);
		
		JSONObject retJson = calTimeAndMoney(userWorkId,newCheckInTime,newCheckOutTime);
		
		UserProdCharBean newTimeLengthBean = (UserProdCharBean) super.newBean(UserProdCharBean.class);
		
		newTimeLengthBean.setUserProdId(userWorkId);
		newTimeLengthBean.setCharId(73);//
		newTimeLengthBean.setValue(retJson.getString("workDuration"));
		newTimeLengthBean.setCreateDate(TimeUtil.getLocalTimeString());
		super.insertBean(newTimeLengthBean);
		
		UserProdCharBean newMoneyBean = (UserProdCharBean) super.newBean(UserProdCharBean.class);
		
		newMoneyBean.setUserProdId(userWorkId);
		newMoneyBean.setCharId(74);//
		newMoneyBean.setValue(retJson.getString("totalPay"));
		newMoneyBean.setCreateDate(TimeUtil.getLocalTimeString());
		super.insertBean(newMoneyBean);
	}
	
	/**
	 * @return
	 * @throws Exception
	 */
	public JSONArray getToPayList() throws Exception{
		JSONArray retArr = new JSONArray();
		Map<String, Object> params = new HashMap();
		params.put("WHERESQL", "ALIPAY_BATCH_NO IS NULL");
		params.put("STATE", "toPay");
		List<AbstractBean> topayList =  super.getBeans(BatchPayHisBean.class, params);
		
		JSONArray topayJsonArr = new JSONArray();
		if (topayList!=null&&topayList.size()>0){
			Map<Long,JSONObject> workIdInfoMap = new HashMap();
			for (AbstractBean bean:topayList){
				BatchPayHisBean toPayBean = (BatchPayHisBean)bean;
				JSONObject payJson = new JSONObject();
				payJson.put("alipayNo", toPayBean.getDestAlipayNo());
				payJson.put("alipayName", toPayBean.getDestAlipayName());
				payJson.put("money", toPayBean.getMoney());
				payJson.put("userWorkId", toPayBean.getUserWorkId());
				payJson.put("payHisid", toPayBean.getHisId());
				
				long workId = this.getWorkIdByUserWorkId(toPayBean.getUserWorkId());
				if (workId>0){
					JSONObject workInfo = null;
					
					workInfo = workIdInfoMap.get(workId);
					if (workInfo!=null){
						
					}else{
						workInfo = this.getWorkBasicInfo(workId);
						workIdInfoMap.put(workId, workInfo);
					}
					
					if (workInfo!=null){
						payJson.put("workName", workInfo.getString("workName"));
						payJson.put("companyName", workInfo.getJSONObject("userInfo").getString("userName"));
					}
				}
				topayJsonArr.add(payJson);
			}
		}
		
		return topayJsonArr;
	}
	
	public long getWorkIdByUserWorkId(long userWorkId) throws Exception{
		Map<String, Object> params = new HashMap();
		params.put("USER_PROD_ID", userWorkId);

		UserProdBean prodBean = (UserProdBean) super.getBean(UserProdBean.class, params);
		if (prodBean!=null){
			long workId = prodBean.getProdId();
			
			return workId;
		}
		return 0;
	}
	public JSONObject getWorkInfoByUserWorkId(long userWorkId) throws Exception{
		Map<String, Object> params = new HashMap();
		params.put("USER_PROD_ID", userWorkId);

		UserProdBean prodBean = (UserProdBean) super.getBean(UserProdBean.class, params);
		if (prodBean!=null){
			long workId = prodBean.getProdId();
			
			return this.getWorkBasicInfo(workId);
		}
		return null;
	}
}
