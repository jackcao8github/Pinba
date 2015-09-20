package com.webapp.work.dao;

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
import com.webapp.common.util.TimeUtil;
import com.webapp.user.bean.UserFocusBean;
import com.webapp.user.bean.UserProdBean;
import com.webapp.user.bean.UserProdCharBean;
import com.webapp.user.bean.UserResumeBean;
import com.webapp.user.dao.ResumeManagerDAO;
import com.webapp.user.dao.UserManagerDAO;
import com.webapp.work.bean.ProdBean;
import com.webapp.work.bean.ProdCharBean;
import com.webapp.work.bean.ProdLookHisBean;

public class WorkManagerDAO extends AbstractDAO {
	private static CharSpecDAO charSpecDao = null;

	public WorkManagerDAO(){
		charSpecDao = new CharSpecDAO();
		charSpecDao.setDataSource(this.getDataSource());
	}
	/**
	 * 加载工作详情
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
		//返回用户信息
		UserManagerDAO userDao = new UserManagerDAO();
		userDao.setDataSource(this.getDataSource());
		JSONObject userInfo = userDao.getUserInfo(prodBean.getUserId());
		if (userInfo!=null){
			retJson.put("userInfo", userInfo);
		}
		
		//返回关注次数
		params.put("PROD_ID", "" + workId);
		long focusNumber = super.getBeanCount(UserFocusBean.class, params);
		retJson.put("focusNumber", focusNumber);
		
		//返回查看次数
		params.put("PROD_ID", "" + workId);
		long lookNumber = super.getBeanCount(ProdLookHisBean.class, params);
		retJson.put("lookNumber", lookNumber);
		
		//返回收到简历数
		params.put("PROD_ID", "" + workId);
		long resumeNumber = super.getBeanCount(UserProdBean.class, params);
		retJson.put("resumeNumber", resumeNumber);
		//返回当前用户的其它工作数
		params.put("USER_ID", "" + prodBean.getUserId());
		long workNumber = super.getBeanCount(ProdBean.class, params);
		retJson.put("workNumber", workNumber);
		return retJson;
	}

	/**
	 * 新增工作信息
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

	/**修改工作信息
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
	 * @param userId
	 * @param workTypes
	 * @param cityName
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public JSONArray getWorkList(long userId,String[] workTypes,String cityName ,int page) throws Exception {
		ResumeManagerDAO resumeDao = new ResumeManagerDAO();
		resumeDao.setDataSource(this.getDataSource());
		
		Map<String, Object> params = new HashMap();
		if (userId>0){
			params.put("USER_ID", "" + userId);
		}
		
		if (workTypes!=null&&workTypes.length>0){
			params.put("PROD_TYPE", workTypes);
		}
		if (cityName!=null&&cityName.length()>0){
			String cityId = charSpecDao.getRealValue("workCity", cityName);
			params.put("CITY_ID", "" + cityId);
		}
		if (page>0){
			params.put("PAGE", "" + page);
		}
		JSONArray prodList = new JSONArray();
		List<AbstractBean> result = getBeans(ProdBean.class, params);

		if (result!=null&&result.size()>0){
			UserManagerDAO userDao = new UserManagerDAO();
			userDao.setDataSource(this.getDataSource());
			
			
			for (AbstractBean abBean : result){
				ProdBean prodBean = (ProdBean) abBean;
				// 返回用户基本信息
				JSONObject retJson = new JSONObject();
				retJson.put("userId", prodBean.getUserId());
				
				JSONObject userInfo = userDao.getUserInfo(prodBean.getUserId());
				retJson.put("userName", userInfo.getString("userName"));//返回用户名
				retJson.put("userCredit", userInfo.getString("userCredit"));//返回用户信用度
				retJson.put("workName", prodBean.getProdName());
				retJson.put("workType", prodBean.getProdType());
				/*retJson.put("workType", charSpecDao.getDisplayValue("workType",prodBean.getProdType()));*/
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
				
				//如果是求职则同时返回简历信息
				if (retJson.containsKey("resumeId")){
					JSONObject resumeInfo = resumeDao.getResumeInfo(retJson.getLong("resumeId"));
					if (resumeInfo!=null){
						retJson.put("resumeInfo", resumeInfo);
					}
				}
				
				//返回关注次数
				params.put("PROD_ID", "" + prodBean.getProdId());
				long focusNumber = super.getBeanCount(UserFocusBean.class, params);
				retJson.put("focusNumber", focusNumber);
				
				//返回查看次数
				params.put("PROD_ID", "" + prodBean.getProdId());
				long lookNumber = super.getBeanCount(ProdLookHisBean.class, params);
				retJson.put("lookNumber", lookNumber);
				
				//返回收到简历数
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
	 * @param userId
	 * @param workTypes
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public JSONArray getFocusWorkList(long userId,String[] workTypes,int page) throws Exception {
		ResumeManagerDAO resumeDao = new ResumeManagerDAO();
		resumeDao.setDataSource(this.getDataSource());
		
		Map<String, Object> params = new HashMap();
		if (userId>0){
			params.put("USER_ID", "" + userId);
		}
		JSONArray prodList = new JSONArray();
		List<AbstractBean> result = getBeans(UserFocusBean.class, params);

		if (result!=null&&result.size()>0){
			UserManagerDAO userDao = new UserManagerDAO();
			userDao.setDataSource(this.getDataSource());
			
			
			for (AbstractBean abBean : result){
				UserFocusBean focusBean = (UserFocusBean) abBean;
				params.clear();
				params.put("PROD_ID", focusBean.getProdId());
				
				ProdBean prodBean = (ProdBean) super.getBean(ProdBean.class, params);
				if (prodBean==null){
					continue;
				}
				// 返回用户基本信息
				JSONObject retJson = new JSONObject();
				retJson.put("userId", prodBean.getUserId());
				
				JSONObject userInfo = userDao.getUserInfo(prodBean.getUserId());
				retJson.put("userName", userInfo.getString("userName"));//返回用户名
				retJson.put("userCredit", userInfo.getString("userCredit"));//返回用户信用度
				retJson.put("workName", prodBean.getProdName());
				retJson.put("workType", prodBean.getProdType());
				/*retJson.put("workType", charSpecDao.getDisplayValue("workType",prodBean.getProdType()));*/
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
				
				//如果是求职则同时返回简历信息
				if (retJson.containsKey("resumeId")){
					JSONObject resumeInfo = resumeDao.getResumeInfo(retJson.getLong("resumeId"));
					if (resumeInfo!=null){
						retJson.put("resumeInfo", resumeInfo);
					}
				}
				
				//返回关注次数
				params.put("PROD_ID", "" + prodBean.getProdId());
				long focusNumber = super.getBeanCount(UserFocusBean.class, params);
				retJson.put("focusNumber", focusNumber);
				
				//返回查看次数
				params.put("PROD_ID", "" + prodBean.getProdId());
				long lookNumber = super.getBeanCount(ProdLookHisBean.class, params);
				retJson.put("lookNumber", lookNumber);
				
				//返回收到简历数
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
	 * @param userId
	 * @param workTypes
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public JSONArray getLookedWorkList(long userId,String[] workTypes,int page) throws Exception {
		ResumeManagerDAO resumeDao = new ResumeManagerDAO();
		resumeDao.setDataSource(this.getDataSource());
		
		Map<String, Object> params = new HashMap();
		if (userId>0){
			params.put("USER_ID", "" + userId);
		}
		JSONArray prodList = new JSONArray();
		List<AbstractBean> result = getBeans(ProdLookHisBean.class, params);

		if (result!=null&&result.size()>0){
			UserManagerDAO userDao = new UserManagerDAO();
			userDao.setDataSource(this.getDataSource());
			
			Set<Long> prodIdSet = new HashSet();//过滤重复的工作
			
			for (AbstractBean abBean : result){
				ProdLookHisBean focusBean = (ProdLookHisBean) abBean;
				
				if (prodIdSet.contains(focusBean.getProdId())){
					continue;
				}
				prodIdSet.add(focusBean.getProdId());
				params.clear();
				params.put("PROD_ID", focusBean.getProdId());
				
				ProdBean prodBean = (ProdBean) super.getBean(ProdBean.class, params);
				if (prodBean==null){
					continue;
				}
				// 返回用户基本信息
				JSONObject retJson = new JSONObject();
				retJson.put("userId", prodBean.getUserId());
				
				JSONObject userInfo = userDao.getUserInfo(prodBean.getUserId());
				retJson.put("userName", userInfo.getString("userName"));//返回用户名
				retJson.put("userCredit", userInfo.getString("userCredit"));//返回用户信用度
				retJson.put("workName", prodBean.getProdName());
				retJson.put("workType", prodBean.getProdType());
				/*retJson.put("workType", charSpecDao.getDisplayValue("workType",prodBean.getProdType()));*/
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
				
				//如果是求职则同时返回简历信息
				if (retJson.containsKey("resumeId")){
					JSONObject resumeInfo = resumeDao.getResumeInfo(retJson.getLong("resumeId"));
					if (resumeInfo!=null){
						retJson.put("resumeInfo", resumeInfo);
					}
				}
				
				//返回关注次数
				params.put("PROD_ID", "" + prodBean.getProdId());
				long focusNumber = super.getBeanCount(UserFocusBean.class, params);
				retJson.put("focusNumber", focusNumber);
				
				//返回查看次数
				params.put("PROD_ID", "" + prodBean.getProdId());
				long lookNumber = super.getBeanCount(ProdLookHisBean.class, params);
				retJson.put("lookNumber", lookNumber);
				
				//返回收到简历数
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
	 * @param userId
	 * @param workId
	 */
	public void newLookHis(long userId,long workId){
		ProdLookHisBean hisBean = new ProdLookHisBean();
		hisBean.setUserId(userId);
		hisBean.setProdId(workId);
		hisBean.setCreateDate(TimeUtil.getLocalTimeString());
		super.insertBean(hisBean);
	}
	
	/**
	 * 记录工作关注记录
	 * @param userId
	 * @param workId
	 * @return
	 * @throws Exception
	 */
	public long addFocus(long userId,long workId) throws Exception{
		Map<String, Object> params = new HashMap();
		params.put("USER_ID", "" + userId);
		params.put("PROD_ID", "" + workId);
		UserFocusBean oldFocusBean = (UserFocusBean) super.getBean(UserFocusBean.class, params);
		if (oldFocusBean!=null){
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
	
	/**删除关注的工作
	 * @param userId
	 * @param workId
	 * @throws Exception
	 */
	public void delFocus(long userId,long workId) throws Exception{
		Map<String, Object> params = new HashMap();
		params.put("USER_ID", "" + userId);
		params.put("PROD_ID", "" + workId);
		UserFocusBean oldFocusBean = (UserFocusBean) super.getBean(UserFocusBean.class, params);
		if (oldFocusBean==null){
			return;
		}

		super.deleteBean(oldFocusBean);
	}
	
	
	/**
	 * 记录员工录用信息
	 * @param resumeId
	 * @param workId
	 * @param hireJson
	 * @throws Exception
	 */
	public void hiredStaff(long resumeId,long workId,JSONObject hireJson) throws Exception{
		Map<String, Object> params = new HashMap();
		params.put("RESUME_ID", "" + resumeId);
		UserResumeBean userResumeBean = (UserResumeBean) super.getBean(UserResumeBean.class, params);
		if (userResumeBean==null){
			throw new Exception("未找到此工作收到的简历!");
		}
		params.clear();
		params.put("PROD_ID", "" + workId);
		params.put("USER_ID", "" + userResumeBean.getUserId());
		
		UserProdBean userWorkBean = (UserProdBean) super.getBean(UserProdBean.class, params);
		userWorkBean.setState("hired");
		
		super.updateBean(userWorkBean);

		//加载已有特征
		params.clear();
		params.put("USER_PROD_ID", "" + userWorkBean.getUserProdId());
		List<AbstractBean> charList = super.getBeans(UserProdCharBean.class, params);
		
		// 新建特征
		Set<String> keySet = hireJson.keySet();
		for (String key : keySet) {
			long charId = charSpecDao.getCharId(key);
			if (charId > 0) {
				boolean existflag = false;
				//如果特征已存在则更新特征值
				if (charList!=null&&charList.size()>0){
					for (AbstractBean abBean : charList){
						UserProdCharBean charBean = (UserProdCharBean) abBean;
						if (charBean.getCharId()==charId){
							charBean.setValue(hireJson.getString(key));
							super.updateBean(charBean);
							existflag = true;
							continue;
						}
					}
				}
				if (existflag==false){
					UserProdCharBean prodCharBean = new UserProdCharBean();
					prodCharBean.setUserProdId(userWorkBean.getUserProdId());
					prodCharBean.setCharId(charId);
					prodCharBean.setValue(hireJson.getString(key));
					prodCharBean.setCreateDate(TimeUtil.getLocalTimeString());
					super.insertBean(prodCharBean);
				}
			}
		}
	}
	
	/**
	 * 记录员工预约面试信息
	 * @param resumeId
	 * @param workId
	 * @param hireJson
	 * @throws Exception
	 */
	public void bookInterview(long resumeId, long workId, JSONObject bookJson)
			throws Exception {
		Map<String, Object> params = new HashMap();
		params.put("RESUME_ID", "" + resumeId);
		UserResumeBean userResumeBean = (UserResumeBean) super.getBean(
				UserResumeBean.class, params);
		if (userResumeBean == null) {
			throw new Exception("未找到此工作收到的简历!");
		}
		params.clear();
		params.put("PROD_ID", "" + workId);
		params.put("USER_ID", "" + userResumeBean.getUserId());

		UserProdBean userWorkBean = (UserProdBean) super.getBean(
				UserProdBean.class, params);
		userWorkBean.setState("reserved");

		super.updateBean(userWorkBean);

		// 加载已有特征
		params.clear();
		params.put("USER_PROD_ID", "" + userWorkBean.getUserProdId());
		List<AbstractBean> charList = super.getBeans(UserProdCharBean.class,
				params);
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
	}
	
	/**
	 * 查询员工投过简历的工作
	 * @param userId
	 * @param state
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public JSONArray getWorkListByStaffId(long userId,String state,int page) throws Exception {
		ResumeManagerDAO resumeDao = new ResumeManagerDAO();
		resumeDao.setDataSource(this.getDataSource());
		
		Map<String, Object> params = new HashMap();
		if (userId>0){
			params.put("USER_ID", "" + userId);
		}
		if (!StringUtils.isEmpty(state)){
			params.put("STATE", state);
		}
		
		JSONArray prodList = new JSONArray();
		List<AbstractBean> result = getBeans(UserProdBean.class, params);

		if (result!=null&&result.size()>0){
			UserManagerDAO userDao = new UserManagerDAO();
			userDao.setDataSource(this.getDataSource());
			
			
			for (AbstractBean abBean : result){
				UserProdBean userWorkBean = (UserProdBean) abBean;
				
				params.clear();
				params.put("PROD_ID", "" + userWorkBean.getProdId());
				
				ProdBean prodBean = (ProdBean) super.getBean(ProdBean.class, params);
				
				// 返回用户基本信息
				JSONObject retJson = new JSONObject();
				retJson.put("state", userWorkBean.getState());
				
				retJson.put("userId", prodBean.getUserId());
				
				JSONObject userInfo = userDao.getUserBasicInfo(prodBean.getUserId());
				retJson.put("userName", userInfo.getString("userName"));//返回用户名
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
				
				//返回员工工作信息
				params.clear();
				params.put("USER_PROD_ID", "" + userWorkBean.getUserProdId());
				List<AbstractBean> useProdCharList = super.getBeans(UserProdCharBean.class, params);
				
				if (useProdCharList!=null&&useProdCharList.size()>0){
					for (AbstractBean abUserProdCharBean:useProdCharList){
						UserProdCharBean userProdCharBean = (UserProdCharBean) abUserProdCharBean;
						
						retJson.put(charSpecDao.getCode(userProdCharBean.getCharId()), userProdCharBean.getValue());
					}
				}
				
				prodList.add(retJson);
			}
		}
		
		return prodList;
	}
	
	/**员工签到或签退
	 * @param userId
	 * @param workId
	 * @param checkType 取值：checkIn签到，checkOut签退
	 * @param positionJsonObj
	 * @throws Exception
	 */
	public void checkInOrOut(long userId, long workId,String checkType, JSONObject positionJsonObj)
			throws Exception {
		Map<String, Object> params = new HashMap();
		params.put("USER_ID", "" + userId);
		params.put("PROD_ID", "" + workId);

		UserProdBean userWorkBean = (UserProdBean) super.getBean(
				UserProdBean.class, params);
		if (userWorkBean == null) {
			throw new Exception("根据userid和workId未找到数据!");
		}
		userWorkBean.setState(checkType);
		super.updateBean(userWorkBean);
		
		JSONObject charJson = new JSONObject();
		//生成签到和签退时间
		if ("checkIn".equals(checkType)){
			charJson.put("checkInTime", TimeUtil.getLocalTimeString());
			charJson.put("checkInPosition", positionJsonObj.toString());
		}else{
			charJson.put("checkOutTime", TimeUtil.getLocalTimeString());
			charJson.put("checkOutPosition", positionJsonObj.toString());
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
}

