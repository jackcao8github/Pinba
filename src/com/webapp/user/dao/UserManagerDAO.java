package com.webapp.user.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.webapp.common.bean.AbstractBean;
import com.webapp.common.dao.AbstractDAO;
import com.webapp.common.util.AcctTypeConsts;
import com.webapp.common.util.CharSpecConsts;
import com.webapp.common.util.MD5Util;
import com.webapp.common.util.TimeUtil;
import com.webapp.user.bean.UserAcctBean;
import com.webapp.user.bean.UserBean;
import com.webapp.user.bean.UserCharBean;

public class UserManagerDAO extends AbstractDAO {
	public JSONObject login(JSONObject userJson) throws Exception {
		//
		String loginCode = userJson.getString("loginCode");// loginCode可能是手机号，userId
		String password = userJson.getString("password");

		UserBean userBean = null;
		// 长度11位表示是手机号
		if (loginCode.length() == 11) {
			Map<String, String> params = new HashMap();
			params.put("CHAR_ID", "" + CharSpecConsts.CELLPHONE);// CHAR_ID=1表示手机
			params.put("VALUE", loginCode);
			List result = getBeans(UserCharBean.class, params);
			if (result != null && result.size() > 0) {
				UserCharBean charBean = (UserCharBean) result.get(0);
				params.clear();
				params.put("USER_ID", "" + charBean.getUserId());
				params.put("PASSWORD", password);
				List result2 = getBeans(UserBean.class, params);
				if (result2 != null && result2.size() > 0) {
					userBean = (UserBean) result2.get(0);
				} else {
					throw new Exception("帐号或密码不正确!");
				}
			} else {
				throw new Exception("帐号或密码不正确!");
			}
		} else {
			Map<String, String> params = new HashMap();
			params.put("USER_ID", loginCode);
			params.put("PASSWORD", password);
			List result = getBeans(UserBean.class, params);
			if (result != null && result.size() > 0) {
				userBean = (UserBean) result.get(0);
			} else {
				throw new Exception("帐号或密码不正确!");
			}
		}

		JSONObject retJson = new JSONObject();
		retJson.put("userId", userBean.getUserId());
		retJson.put("userCode", userBean.getUserCode());
		retJson.put("userCredit", userBean.getUserCredit());
		retJson.put("userLevel", userBean.getUserLevel());
		retJson.put("userName", userBean.getUserName());
		retJson.put("userType", userBean.getUserType());
		retJson.put("userAuthState", userBean.getAuthState());
		retJson.put("balance", "0");
		retJson.put("points", "0");

		Map<String, String> params = new HashMap();
		params.put("USER_ID", "" + userBean.getUserId());
		List<AbstractBean> result = getBeans(UserAcctBean.class, params);
		if (result != null && result.size() > 0) {
			for (AbstractBean abBean : result) {
				UserAcctBean acctBean = (UserAcctBean) abBean;

				if (acctBean.getAcctType() == AcctTypeConsts.BALANCE) {
					// 返回余额
					retJson.put(AcctTypeConsts.BALANCE, acctBean.getValue());
				} else if (acctBean.getAcctType() == AcctTypeConsts.POINTS) {
					// 返回积分数
					retJson.put(AcctTypeConsts.POINTS, acctBean.getValue());
				}
			}
		}

		params.clear();
		params.put("USER_ID", "" + userBean.getUserId());
		result.clear();
		result = getBeans(UserCharBean.class, params);
		if (result != null && result.size() > 0) {
			for (AbstractBean abBean : result) {
				UserCharBean userCharBean = (UserCharBean) abBean;

				if (userCharBean.getCharId() == CharSpecConsts.SEX) {
					retJson.put(CharSpecConsts.getCode(CharSpecConsts.SEX), userCharBean.getValue());
				} else if (userCharBean.getCharId() == CharSpecConsts.CELLPHONE) {
					retJson.put(CharSpecConsts.getCode(CharSpecConsts.CELLPHONE), userCharBean.getValue());
				} else if (userCharBean.getCharId() == CharSpecConsts.EMAIL) {
					retJson.put(CharSpecConsts.getCode(CharSpecConsts.EMAIL), userCharBean.getValue());
				}
			}
		}

		// 返回一个新的token
		StringBuffer tokenSrc = new StringBuffer();
		tokenSrc.append(userBean.getUserId()).append(userBean.getPassword()).append(TimeUtil.getLocalTimeString());
		String newToken = MD5Util.MD5(tokenSrc.toString());
		retJson.put("userToken", newToken);

		// 更新user表的token
		userBean.setToken(newToken);
		super.updateBean(userBean);

		return retJson;
	}

	/* 新创建一个用户 */
	public void newUser(JSONObject userJson) throws Exception {
		// 新建用户
		UserBean user = new UserBean();
		user.setAuthState("no");
		// user.setUserCode(userJson.getString("userCode"));
		user.setPassword(userJson.getString("password"));
		user.setUserCredit(0L);
		user.setUserLevel("LV1");
		user.setUserName(userJson.getString("userName"));
		user.setUserType(userJson.getString("userType"));
		user.setCreateDate(TimeUtil.getLocalTimeString());
		Object beanId = insertBean(user);
		long userId = Long.valueOf(beanId.toString());
		// 新建帐户
		UserAcctBean acctBean = new UserAcctBean();
		acctBean.setUserId(userId);
		acctBean.setAcctType(AcctTypeConsts.POINTS);// 积分帐户
		acctBean.setValue(0l);
		acctBean.setCreateDate(TimeUtil.getLocalTimeString());

		super.insertBean(acctBean);
		acctBean = new UserAcctBean();
		acctBean.setUserId(userId);
		acctBean.setAcctType(AcctTypeConsts.BALANCE);// 现金帐户
		acctBean.setValue(0l);
		acctBean.setCreateDate(TimeUtil.getLocalTimeString());

		super.insertBean(acctBean);

		acctBean = new UserAcctBean();
		acctBean.setUserId(userId);
		acctBean.setAcctType(AcctTypeConsts.DEPOSIT);// 保证金帐户
		acctBean.setValue(0l);
		acctBean.setCreateDate(TimeUtil.getLocalTimeString());

		super.insertBean(acctBean);
		// 新建特征
		if (userJson.containsKey("cellphone")) {
			Map<String, String> params = new HashMap();
			params.put("CHAR_ID", "" + CharSpecConsts.CELLPHONE);// CHAR_ID=1表示手机
			params.put("VALUE", userJson.getString("cellphone"));
			List result = getBeans(UserCharBean.class, params);
			if (result != null && result.size() > 0) {
				throw new Exception("号码:" + userJson.getString("cellphone") + "已注册过!");
			}

			UserCharBean charBean = new UserCharBean();
			charBean.setCharId(CharSpecConsts.CELLPHONE);
			charBean.setUserId(userId);
			charBean.setCreateDate(TimeUtil.getLocalTimeString());
			charBean.setValue(userJson.getString("cellphone"));
			super.insertBean(charBean);
		}
		if (userJson.containsKey("email")) {
			UserCharBean charBean = new UserCharBean();
			charBean.setCharId(CharSpecConsts.EMAIL);
			charBean.setUserId(userId);
			charBean.setCreateDate(TimeUtil.getLocalTimeString());
			charBean.setValue(userJson.getString("email"));
			super.insertBean(charBean);
		}
		if (userJson.containsKey("address")) {
			UserCharBean charBean = new UserCharBean();
			charBean.setCharId(CharSpecConsts.ADDRESS);
			charBean.setUserId(userId);
			charBean.setCreateDate(TimeUtil.getLocalTimeString());
			charBean.setValue(userJson.getString("address"));
			super.insertBean(charBean);
		}
		if (userJson.containsKey("coords")) {
			UserCharBean charBean = new UserCharBean();
			charBean.setCharId(CharSpecConsts.COORDS);
			charBean.setUserId(userId);
			charBean.setCreateDate(TimeUtil.getLocalTimeString());
			charBean.setValue(userJson.getString("COORDS"));
			super.insertBean(charBean);
		}
		// 赠送积分
	}

	/* 新创建一个用户 */
	public void modUser(JSONObject userJson) {
		// 新建用户
		UserBean user = new UserBean();
		user.setUserId(userJson.getLong("userId"));// 修改操作时必须有userId

		user.setAuthState(userJson.getString("authState"));
		user.setUserCode(userJson.getString("userCode"));
		user.setPassword(userJson.getString("password"));
		user.setUserName(userJson.getString("userName"));
		user.setUserType(userJson.getString("userType"));
		updateBean(user);
	}

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("Application-Context.xml");

		UserBean user = new UserBean();
		user.setAuthState("no");
		user.setUserCode("caojian");
		user.setPassword("123123");
		user.setUserCredit(1000L);
	 //user.setUserId(2L);
		user.setUserLevel("LV1");
		user.setUserName("caojian");
		user.setUserType("person");

		// DateFormat.getDateTimeInstance(2, 2, Locale.CHINESE).format(new
		// java.util.Date());
		user.setCreateDate(TimeUtil.getLocalTimeString());

		UserManagerDAO userDAO = (UserManagerDAO) context.getBean("userDAOProxy");

		user.setUserCode("caojian22");
		user.setUserName("caojian22" );
		//user.setUserId(3006L);

		userDAO.insertBean(user);

	}
}
