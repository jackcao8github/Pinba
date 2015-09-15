package com.webapp.user.dao;

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
import com.webapp.user.bean.UserResumeBean;
import com.webapp.user.bean.UserResumeCharBean;
import com.webapp.user.bean.UserResumeLookHisBean;
import com.webapp.user.bean.UserResumeSendHisBean;

public class ResumeManagerDAO extends AbstractDAO {
	private static CharSpecDAO charSpecDao = null;

	public ResumeManagerDAO(){
		charSpecDao = new CharSpecDAO();
		charSpecDao.setDataSource(this.getDataSource());
	}
	// 加载简历信息
	public JSONObject getResumeInfo(long resumeId) throws Exception {
		Map<String, Object> params = new HashMap();
		params.put("RESUME_ID", "" + resumeId);

		UserResumeBean userBean = (UserResumeBean) getBean(UserResumeBean.class, params);
		if (userBean == null) {
			throw new Exception("参数resumeId" + resumeId + "不正确!");
		}
		// 返回用户基本信息
		JSONObject retJson = new JSONObject();
		retJson.put("userId", userBean.getUserId());
		retJson.put("resumeName", userBean.getResumeName());
		retJson.put("resumeType", charSpecDao.getDisplayValue("resumeType",userBean.getResumeType()));
		
		// 返回简历特征信息
		params.clear();
		params.put("RESUME_ID", "" + resumeId);
		List<AbstractBean> result = getBeans(UserResumeCharBean.class, params);
		if (result != null && result.size() > 0) {
			for (AbstractBean abBean : result) {
				UserResumeCharBean userCharBean = (UserResumeCharBean) abBean;

				retJson.put(charSpecDao.getCode(userCharBean.getCharId()), userCharBean.getValue());
			}
		}

		return retJson;
	}

	// 新增简历
	public JSONObject newResume(JSONObject resumeInfo) throws Exception {
		JSONObject retJson = new JSONObject();
		UserResumeBean resumeBean = new UserResumeBean();
		resumeBean.setUserId(resumeInfo.getLong("userId"));
		resumeBean.setResumeName(resumeInfo.getString("resumeName"));
		resumeBean.setResumeType(resumeInfo.getString("resumeType"));
		resumeBean.setCreateDate(TimeUtil.getLocalTimeString());
		long resumeId = (long) super.insertBean(resumeBean);

		// 新建特征
		Set<String> keySet = resumeInfo.keySet();
		for (String key : keySet) {
			long charId = charSpecDao.getCharId(key);
			if (charId > 0) {
				UserResumeCharBean resumeCharBean = new UserResumeCharBean();
				resumeCharBean.setResumeId(resumeId);
				resumeCharBean.setCharId(charId);
				resumeCharBean.setValue(resumeInfo.getString(key));
				resumeCharBean.setCreateDate(TimeUtil.getLocalTimeString());
				super.insertBean(resumeCharBean);
			}
		}

		return retJson;
	}

	// 修改简历
	public void modResume(JSONObject resumeInfo) throws Exception {
		// 更新基本信息
		UserResumeBean resumeBean = new UserResumeBean();
		resumeBean.setResumeId(resumeInfo.getLong("resumeId"));
		if (resumeInfo.containsKey("resumeName"))
			resumeBean.setResumeName(resumeInfo.getString("resumeName"));
		if (resumeInfo.containsKey("resumeType"))
			resumeBean.setResumeType(resumeInfo.getString("resumeType"));
		super.updateBean(resumeBean);

		// 更新user char
		Set<String> keySet = resumeInfo.keySet();
		for (String key : keySet) {
			long charId = charSpecDao.getCharId(key);
			if (charId > 0) {
				Map<String, Object> params = new HashMap();
				params.put("RESUME_ID", resumeInfo.getString("resumeId"));
				params.put("CHAR_ID", "" + charId);

				UserResumeCharBean bean = (UserResumeCharBean) super.getBean(UserResumeCharBean.class, params);
				if (bean != null) {
					bean.setValue(resumeInfo.getString(key));

					updateBean(bean);
				} else {
					UserResumeCharBean newbean = new UserResumeCharBean();
					newbean.setResumeId(resumeInfo.getLong("resumeId"));
					newbean.setCharId(charId);
					newbean.setValue(resumeInfo.getString(key));

					super.insertBean(newbean);
				}
			}
		}
	}

	public JSONArray getResumeList(long userId, int page) throws Exception {
		Map<String, Object> params = new HashMap();
		params.put("USER_ID", "" + userId);

		List<AbstractBean> result = getBeans(UserResumeBean.class, params);

		if (result!=null&&result.size()>0){
			JSONArray resumeList = new JSONArray();
			for (AbstractBean abBean : result){
				UserResumeBean resumeBean = (UserResumeBean) abBean;
				// 返回用户基本信息
				JSONObject retJson = new JSONObject();
				retJson.put("userId", resumeBean.getUserId());
				retJson.put("resumeId", resumeBean.getResumeId());
				retJson.put("resumeName", resumeBean.getResumeName());
				retJson.put("resumeType", charSpecDao.getDisplayValue("resumeType",resumeBean.getResumeType()));
				retJson.put("createDate", resumeBean.getCreateDate());
				// 返回简历特征信息
				params.clear();
				params.put("RESUME_ID", "" + resumeBean.getResumeId());
				List<AbstractBean> resumeCharResult = getBeans(UserResumeCharBean.class, params);
				if (resumeCharResult != null && resumeCharResult.size() > 0) {
					for (AbstractBean resumeCharAbBean : resumeCharResult) {
						UserResumeCharBean resumeCharBean = (UserResumeCharBean) resumeCharAbBean;
						String charCode = charSpecDao.getCode(resumeCharBean.getCharId());
						if (!retJson.containsKey(charCode))
							retJson.put(charCode, resumeCharBean.getValue());
					}
				}
				//返回简历投递次数
				params.clear();
				params.put("RESUME_ID", "" + resumeBean.getResumeId());
				long sendNumber = super.getBeanCount(UserResumeSendHisBean.class, params);
				retJson.put("resumeSendNumber", sendNumber);
				
				
				//返回查看次数
				params.put("RESUME_ID", "" + resumeBean.getResumeId());
				long lookNumber = super.getBeanCount(UserResumeLookHisBean.class, params);
				retJson.put("lookNumber", lookNumber);
				
				resumeList.add(retJson);
			}
			return resumeList;
		}
		
		return new JSONArray();
	}
	
	public long sendResume(long prodId,long resumeId) throws Exception{
		Map<String, Object> params = new HashMap();
		params.put("PROD_ID", "" + prodId);
		params.put("RESUME_ID", "" + resumeId);
		UserResumeSendHisBean oldFocusBean = (UserResumeSendHisBean) super.getBean(UserResumeSendHisBean.class, params);
		if (oldFocusBean!=null){
			throw new Exception("你已投递过!");
		}
		UserResumeSendHisBean focusBean = new UserResumeSendHisBean();
		focusBean.setProdId(prodId);
		focusBean.setResumeId(resumeId);
		focusBean.setCreateDate(TimeUtil.getLocalTimeString());
		
		super.insertBean(focusBean);
		
		params.clear();
		params.put("PROD_ID", "" + prodId);
		long count = super.getBeanCount(UserResumeSendHisBean.class, params);
				
		return count;
	}
	
	
	/**
	 * 增加简历查看记录
	 * @param userId
	 * @param resumeId
	 */
	public void newLookHis(long userId,long resumeId){
		UserResumeLookHisBean hisBean = new UserResumeLookHisBean();
		hisBean.setUserId(userId);
		hisBean.setResumeId(resumeId);
		hisBean.setCreateDate(TimeUtil.getLocalTimeString());
		super.insertBean(hisBean);
	}
}
