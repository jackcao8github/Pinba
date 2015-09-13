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

public class ResumeManagerDAO extends AbstractDAO {

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
		retJson.put("resumeType", userBean.getResumeType());

		// 返回简历特征信息
		params.clear();
		params.put("RESUME_ID", "" + resumeId);
		List<AbstractBean> result = getBeans(UserResumeCharBean.class, params);
		if (result != null && result.size() > 0) {
			CharSpecDAO charSpecDao = new CharSpecDAO();
			charSpecDao.setDataSource(this.getDataSource());
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
		CharSpecDAO charSpecDao = new CharSpecDAO();
		charSpecDao.setDataSource(this.getDataSource());
		for (String key : keySet) {
			long charId = charSpecDao.getCharId(key);
			if (charId > 0) {
				UserResumeCharBean resumeCharBean = new UserResumeCharBean();
				resumeCharBean.setResumeId(resumeId);
				resumeCharBean.setCharId(charId);
				resumeCharBean.setValue(resumeInfo.getString(key));

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
		CharSpecDAO charSpecDao = new CharSpecDAO();
		charSpecDao.setDataSource(this.getDataSource());
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
			CharSpecDAO charSpecDao = new CharSpecDAO();
			charSpecDao.setDataSource(this.getDataSource());
			JSONArray resumeList = new JSONArray();
			for (AbstractBean abBean : result){
				UserResumeBean resumeBean = (UserResumeBean) abBean;
				// 返回用户基本信息
				JSONObject retJson = new JSONObject();
				retJson.put("userId", resumeBean.getUserId());
				retJson.put("resumeId", resumeBean.getResumeId());
				retJson.put("resumeName", resumeBean.getResumeName());
				retJson.put("resumeType", resumeBean.getResumeType());
				retJson.put("createDate", resumeBean.getCreateDate());
				// 返回简历特征信息
				params.clear();
				params.put("RESUME_ID", "" + resumeBean.getResumeId());
				List<AbstractBean> resumeCharResult = getBeans(UserResumeCharBean.class, params);
				if (resumeCharResult != null && resumeCharResult.size() > 0) {
					for (AbstractBean resumeCharAbBean : resumeCharResult) {
						UserResumeCharBean resumeCharBean = (UserResumeCharBean) resumeCharAbBean;

						retJson.put(charSpecDao.getCode(resumeCharBean.getCharId()), resumeCharBean.getValue());
					}
				}
				resumeList.add(retJson);
			}
			return resumeList;
		}
		
		return null;
	}
}
