package com.webapp.work.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.webapp.common.bean.AbstractBean;
import com.webapp.common.dao.AbstractDAO;
import com.webapp.common.dao.CharSpecDAO;
import com.webapp.common.util.TimeUtil;
import com.webapp.user.bean.UserFocusBean;
import com.webapp.user.bean.UserResumeSendHisBean;
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
	// 加载简历信息
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
		long resumeNumber = super.getBeanCount(UserResumeSendHisBean.class, params);
		retJson.put("resumeNumber", resumeNumber);
		//返回当前用户的其它工作数
		params.put("USER_ID", "" + prodBean.getUserId());
		long workNumber = super.getBeanCount(ProdBean.class, params);
		retJson.put("workNumber", workNumber);
		return retJson;
	}

	// 新增简历
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

	// 修改简历
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
				long resumeNumber = super.getBeanCount(UserResumeSendHisBean.class, params);
				retJson.put("resumeNumber", resumeNumber);
				
				prodList.add(retJson);
			}
			
		}
		
		return prodList;
	}
	
	public void newLookHis(long userId,long workId){
		ProdLookHisBean hisBean = new ProdLookHisBean();
		hisBean.setUserId(userId);
		hisBean.setProdId(workId);
		hisBean.setCreateDate(TimeUtil.getLocalTimeString());
		super.insertBean(hisBean);
	}
	
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
}

