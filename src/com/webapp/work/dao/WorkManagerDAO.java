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
import com.webapp.work.bean.ProdBean;
import com.webapp.work.bean.ProdCharBean;

public class WorkManagerDAO extends AbstractDAO {

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
			CharSpecDAO charSpecDao = new CharSpecDAO();
			charSpecDao.setDataSource(this.getDataSource());
			for (AbstractBean abBean : result) {
				ProdCharBean userCharBean = (ProdCharBean) abBean;

				retJson.put(charSpecDao.getCode(userCharBean.getCharId()), userCharBean.getValue());
			}
		}

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
		CharSpecDAO charSpecDao = new CharSpecDAO();
		charSpecDao.setDataSource(this.getDataSource());
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
		CharSpecDAO charSpecDao = new CharSpecDAO();
		charSpecDao.setDataSource(this.getDataSource());
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

	public JSONArray getWorkList(long userId,String[] workTypes, int page) throws Exception {
		Map<String, Object> params = new HashMap();
		params.put("USER_ID", "" + userId);
		if (workTypes!=null&&workTypes.length>0){
			params.put("PROD_TYPE", workTypes);
		}

		List<AbstractBean> result = getBeans(ProdBean.class, params);

		if (result!=null&&result.size()>0){
			CharSpecDAO charSpecDao = new CharSpecDAO();
			charSpecDao.setDataSource(this.getDataSource());
			JSONArray prodList = new JSONArray();
			for (AbstractBean abBean : result){
				ProdBean prodBean = (ProdBean) abBean;
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
				params.put("PROD_ID", "" + prodBean.getProdId());
				List<AbstractBean> prodCharResult = getBeans(ProdCharBean.class, params);
				if (prodCharResult != null && prodCharResult.size() > 0) {
					for (AbstractBean prodCharAbBean : prodCharResult) {
						ProdCharBean prodCharBean = (ProdCharBean) prodCharAbBean;

						retJson.put(charSpecDao.getCode(prodCharBean.getCharId()), prodCharBean.getValue());
					}
				}
				prodList.add(retJson);
			}
			return prodList;
		}
		
		return null;
	}
}
