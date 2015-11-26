package com.webapp.user.dao;

import java.util.HashMap;
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
import com.webapp.user.bean.UserProdCharBean;
import com.webapp.user.bean.UserResumeBean;
import com.webapp.user.bean.UserResumeCharBean;
import com.webapp.user.bean.UserResumeLookHisBean;
import com.webapp.user.bean.UserBean;
import com.webapp.user.bean.UserProdBean;
import com.webapp.work.bean.BatchPayHisBean;
import com.webapp.work.dao.WorkManagerDAO;

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

		UserResumeBean resumeBean = (UserResumeBean) getBean(UserResumeBean.class, params);
		if (resumeBean == null) {
			throw new Exception("参数resumeId" + resumeId + "不正确!");
		}
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
		params.put("VALUE", "" + resumeBean.getResumeId());
		params.put("CHAR_ID", ""+charSpecDao.getCharId("resumeId"));
		long sendNumber = super.getBeanCount(UserProdCharBean.class, params);
		retJson.put("resumeSendNumber", sendNumber);
		
		
		//返回查看次数
		params.clear();
		params.put("RESUME_ID", "" + resumeBean.getResumeId());
		long lookNumber = super.getBeanCount(UserResumeLookHisBean.class, params);
		retJson.put("lookNumber", lookNumber);

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

	/**
	 * 加载用户的简历
	 * @param userId
	 * @param page
	 * @return
	 * @throws Exception
	 */
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
				params.put("VALUE", "" + resumeBean.getResumeId());
				params.put("CHAR_ID", ""+charSpecDao.getCharId("resumeId"));
				long sendNumber = super.getBeanCount(UserProdCharBean.class, params);
				retJson.put("resumeSendNumber", sendNumber);
				
				
				//返回查看次数
				params.clear();
				params.put("RESUME_ID", "" + resumeBean.getResumeId());
				long lookNumber = super.getBeanCount(UserResumeLookHisBean.class, params);
				retJson.put("lookNumber", lookNumber);
				
				resumeList.add(retJson);
			}
			return resumeList;
		}
		
		return new JSONArray();
	}
	
	/**
	 * 加载工作收到的简历
	 * @param workId 收到简历的工作id
	 * @param resumeState 简历状态
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public JSONArray getResumeInWorkList(long workId,String resumeState, int page) throws Exception {
		Map<String, Object> params = new HashMap();
		params.put("PROD_ID", "" + workId);
		if (!StringUtils.isEmpty(resumeState)){
			params.put("STATE", resumeState);
		}

		List<AbstractBean> result = getBeans(UserProdBean.class, params);

		if (result!=null&&result.size()>0){
			//取工作信息
			WorkManagerDAO workDao = new WorkManagerDAO();
			workDao.setDataSource(getDataSource());
			
			JSONObject workJson = workDao.getWorkInfo(workId);
			
			JSONArray resumeList = new JSONArray();
			for (AbstractBean abBean : result){
				UserProdBean hisBean = (UserProdBean)abBean;
				
				params.clear();
				params.put("USER_PROD_ID", "" + hisBean.getUserProdId());
				params.put("CHAR_ID", charSpecDao.getCharId("resumeId"));//简历id
				
				UserProdCharBean charBean = (UserProdCharBean) super.getBean(UserProdCharBean.class, params);
				
				if (charBean==null)continue;
				JSONObject retJson = this.getResumeInfo(Long.valueOf(charBean.getValue()));
				if (retJson!=null){
					//返回工作信息
					retJson.put("workInfo",workJson);
					//返回用户与工作的关系id
					retJson.put("userWorkId", hisBean.getUserProdId());
					
					//返回用户绑定的支付宝帐号和户名
					params.clear();
					params.put("USER_ID", "" + hisBean.getUserId());
					UserBean userBean = (UserBean) super.getBean(UserBean.class, params);
					if (userBean!=null){
						retJson.put("alipayNo", userBean.getAlipayNo());
						retJson.put("alipayName", userBean.getAlipayName());
					}
					
					//返回工作过程信息，例如签到签退信息及工资发放信息
					params.clear();
					params.put("USER_PROD_ID", "" + hisBean.getUserProdId());
					
					List<AbstractBean> charList =  super.getBeans(UserProdCharBean.class, params);
					if (charList!=null){
						for (AbstractBean bean:charList){
							charBean = (UserProdCharBean) bean;
							retJson.put(charSpecDao.getCode(charBean.getCharId()),charBean.getValue());
						}
					}
					
					//返回支付批次和状态
					params.clear();
					params.put("USER_WORK_ID", "" + hisBean.getUserProdId());
					params.put("ORDERBY", "CREATE_DATE DESC");
					params.put("FIRSTROW", "");

					
					BatchPayHisBean payHisBean = (BatchPayHisBean) super.getBean(BatchPayHisBean.class, params);
					if (payHisBean!=null){
						retJson.put("batchNo", payHisBean.getBatchNo());
						retJson.put("state", payHisBean.getState());
						retJson.put("remark", payHisBean.getRemark());
					}
					resumeList.add(retJson);
				}
			}
			return resumeList;
		}
		
		return new JSONArray();
	}
	
	
	public long sendResume(long prodId,long resumeId) throws Exception{
		//根据resumeId取userId
		Map<String, Object> params = new HashMap();
		params.put("RESUME_ID", "" + resumeId);
		
		UserResumeBean resumeBean = (UserResumeBean) super.getBean(UserResumeBean.class, params);
		if (resumeBean==null){
			throw new Exception("简历id:"+resumeId+"不存在!");
		}
		params.clear();
		params.put("PROD_ID", "" + prodId);
		params.put("USER_ID", "" + resumeBean.getUserId());
		UserProdBean userWorkBean = (UserProdBean) super.getBean(UserProdBean.class, params);
		if (userWorkBean!=null){
			throw new Exception("你已投递过!");
		}
		
		UserProdBean focusBean = new UserProdBean();
		focusBean.setProdId(prodId);
		focusBean.setUserId(resumeBean.getUserId());
		focusBean.setState("newArrived");//初始状态为新到简历，后续可变更为reserved,hired
		focusBean.setCreateDate(TimeUtil.getLocalTimeString());
		
		long userProdId = (long) super.insertBean(focusBean);
		
		UserProdCharBean charBean = new UserProdCharBean();
		charBean.setUserProdId(userProdId);
		charBean.setCharId(charSpecDao.getCharId("resumeId"));
		charBean.setValue(""+resumeId);
		charBean.setCreateDate(TimeUtil.getLocalTimeString());
		
		super.insertBean(charBean);
		
		params.clear();
		params.put("PROD_ID", "" + prodId);
		long count = super.getBeanCount(UserProdBean.class, params);
				
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
