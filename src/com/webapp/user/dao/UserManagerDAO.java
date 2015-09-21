package com.webapp.user.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.webapp.common.bean.AbstractBean;
import com.webapp.common.dao.AbstractDAO;
import com.webapp.common.dao.CharSpecDAO;
import com.webapp.common.util.AcctTypeConsts;
import com.webapp.common.util.CharSpecConsts;
import com.webapp.common.util.MD5Util;
import com.webapp.common.util.TimeUtil;
import com.webapp.user.bean.UserAcctBean;
import com.webapp.user.bean.UserAlbumBean;
import com.webapp.user.bean.UserAlbumPhotoBean;
import com.webapp.user.bean.UserBean;
import com.webapp.user.bean.UserCharBean;
import com.webapp.user.bean.UserPositionBean;
import com.webapp.user.bean.VUserHeadPicBean;

public class UserManagerDAO extends AbstractDAO {
	private static CharSpecDAO charSpecDao = null;

	public UserManagerDAO(){
		charSpecDao = new CharSpecDAO();
		charSpecDao.setDataSource(this.getDataSource());
	}
	public JSONObject login(JSONObject userJson) throws Exception {
		//
		String loginCode = userJson.getString("loginCode");// loginCode可能是手机号，userId
		String password = userJson.getString("password");

		UserBean userBean = null;
		// loginCode先当作userId来登陆，然后再当作手机号来登陆
		Map<String, Object> params = new HashMap();
		params.put("USER_ID", loginCode);
		userBean = (UserBean) getBean(UserBean.class, params);
		if (userBean != null) {
			if (!password.equals(userBean.getPassword())) {
				throw new Exception("密码不正确!");
			}
		} else {
			params.clear();
			params.put("CHAR_ID", "" + CharSpecConsts.CELLPHONE);// CHAR_ID=1表示手机
			params.put("VALUE", loginCode);
			UserCharBean charBean = (UserCharBean) getBean(UserCharBean.class, params);
			if (charBean != null) {
				params.clear();
				params.put("USER_ID", "" + charBean.getUserId());
				params.put("PASSWORD", password);
				userBean = (UserBean) getBean(UserBean.class, params);
				if (userBean == null) {
					throw new Exception("密码不正确!");
				}
			} else {
				throw new Exception("帐号不存在!");
			}
		}

		JSONObject retJson = new JSONObject();
		retJson.put("userId", userBean.getUserId());
		retJson.put("userType", userBean.getUserType());
		// 返回一个新的token
		StringBuffer tokenSrc = new StringBuffer();
		tokenSrc.append(userBean.getUserId()).append(userBean.getPassword()).append(TimeUtil.getLocalTimeString());
		String newToken = MD5Util.MD5(tokenSrc.toString());
		retJson.put("userToken", newToken);

		// 返回用户手机
		params.clear();
		params.put("CHAR_ID", "" + CharSpecConsts.CELLPHONE);// CHAR_ID=1表示手机
		params.put("VALUE", loginCode);
		UserCharBean charBean = (UserCharBean) getBean(UserCharBean.class, params);
		if (charBean != null) {
			retJson.put("cellphone", charBean.getValue());
		}
		// 更新user表的token
		userBean.setToken(newToken);
		super.updateBean(userBean);

		return retJson;
	}

	/* 新创建一个用户 */
	public long newUser(JSONObject userJson) throws Exception {
		// 判断手机号是否用过
		if (userJson.containsKey("cellphone")) {
			Map<String, Object> params = new HashMap();
			params.put("CHAR_ID", "" + CharSpecConsts.CELLPHONE);// CHAR_ID=1表示手机
			params.put("VALUE", userJson.getString("cellphone"));
			UserCharBean charBean = (UserCharBean) getBean(UserCharBean.class, params);
			if (charBean != null) {
				throw new Exception("号码:" + userJson.getString("cellphone") + "已注册过!");
			}
		}
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
		Set<String> keySet = userJson.keySet();
		for (String key : keySet) {
			long charId = charSpecDao.getCharId(key);
			if (charId > 0) {
				UserCharBean newbean = new UserCharBean();
				newbean.setUserId(userId);
				newbean.setCharId(charId);
				newbean.setValue(userJson.getString(key));
				newbean.setCreateDate(TimeUtil.getLocalTimeString());
				
				super.insertBean(newbean);
			}
		}

		// 赠送积分

		return userId;
	}

	/* 更新一个用户 */
	public void modUser(JSONObject userJson) throws Exception {
		// 更新基本信息
		UserBean user = new UserBean();
		user.setUserId(userJson.getLong("userId"));// 修改操作时必须有userId

		if (userJson.containsKey("authState")) {
			user.setAuthState(userJson.getString("authState"));
		}
		if (userJson.containsKey("userCode")) {
			user.setUserCode(userJson.getString("userCode"));
		}
		if (userJson.containsKey("password")) {
			user.setPassword(userJson.getString("password"));
		}
		if (userJson.containsKey("userName")) {
			user.setUserName(userJson.getString("userName"));
		}
		if (userJson.containsKey("userType")) {
			user.setUserType(userJson.getString("userType"));
		}
		updateBean(user);

		// 更新user char
		Set<String> keySet = userJson.keySet();
		for (String key : keySet) {
			long charId = charSpecDao.getCharId(key);
			if (charId > 0) {
				Map<String, Object> params = new HashMap();
				params.put("USER_ID", userJson.getString("userId"));
				params.put("CHAR_ID", "" + charId);

				UserCharBean bean = (UserCharBean) super.getBean(UserCharBean.class, params);
				if (bean != null) {
					bean.setValue(userJson.getString(key));

					updateBean(bean);
				} else {
					UserCharBean newbean = new UserCharBean();
					newbean.setUserId(userJson.getLong("userId"));
					newbean.setCharId(charId);
					newbean.setValue(userJson.getString(key));

					super.insertBean(newbean);
				}
			}
		}
	}

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("Application-Context.xml");

		UserBean user = new UserBean();
		user.setAuthState("no");
		user.setUserCode("caojian");
		user.setPassword("123123");
		user.setUserCredit(1000L);
		// user.setUserId(2L);
		user.setUserLevel("LV1");
		user.setUserName("caojian");
		user.setUserType("person");

		// DateFormat.getDateTimeInstance(2, 2, Locale.CHINESE).format(new
		// java.util.Date());
		user.setCreateDate(TimeUtil.getLocalTimeString());

		UserManagerDAO userDAO = (UserManagerDAO) context.getBean("userDAOProxy");

		user.setUserCode("caojian22");
		user.setUserName("caojian22");
		// user.setUserId(3006L);

		userDAO.insertBean(user);

	}

	/* 根据手机号码加载用户信息 */
	public long getUserIdByCellPhone(String cellphone) throws Exception {
		Map<String, Object> params = new HashMap();
		params.put("CHAR_ID", "" + CharSpecConsts.CELLPHONE);// CHAR_ID=1表示手机
		params.put("VALUE", cellphone);
		UserCharBean charBean = (UserCharBean) getBean(UserCharBean.class, params);
		if (charBean == null) {
			throw new Exception("号码:" + cellphone + "未注册过!");
		}

		long userId = charBean.getUserId();

		return userId;
	}

	public JSONObject getUserBasicInfo(long userId) throws Exception {
		Map<String, Object> params = new HashMap();
		params.put("USER_ID", "" + userId);

		UserBean userBean = (UserBean) getBean(UserBean.class, params);
		if (userBean == null) {
			throw new Exception("参数userId" + userId + "不正确!");
		}

		// 返回用户基本信息
		JSONObject retJson = new JSONObject();
		retJson.put("userId", userBean.getUserId());
		retJson.put("userCode", userBean.getUserCode());
		retJson.put("userCredit", userBean.getUserCredit());
		retJson.put("userLevel", userBean.getUserLevel());
		retJson.put("userName", userBean.getUserName());
		retJson.put("userType", userBean.getUserType());
		retJson.put("userAuthState", userBean.getAuthState());
		
		return retJson;
	}
	
	public JSONObject getUserInfo(long userId) throws Exception {
		Map<String, Object> params = new HashMap();
		params.put("USER_ID", "" + userId);

		UserBean userBean = (UserBean) getBean(UserBean.class, params);
		if (userBean == null) {
			throw new Exception("参数userId" + userId + "不正确!");
		}

		// 返回用户基本信息
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

		// 返回用户帐户信息
		params.clear();
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

		// 返回用户特征信息
		params.clear();
		params.put("USER_ID", "" + userBean.getUserId());
		result.clear();
		result = getBeans(UserCharBean.class, params);
		if (result != null && result.size() > 0) {
			for (AbstractBean abBean : result) {
				UserCharBean userCharBean = (UserCharBean) abBean;

				retJson.put(this.charSpecDao.getCode(userCharBean.getCharId()), userCharBean.getValue());
			}
		}
		//返回用户头像图片id
		params.clear();
		params.put("USER_ID", "" + userBean.getUserId());
		VUserHeadPicBean userHeadPicBean = (VUserHeadPicBean) super.getBean(VUserHeadPicBean.class, params);
		if (userHeadPicBean!=null){
			retJson.put("headPicId", userHeadPicBean.getPhotoId());
		}else{
			retJson.put("headPicId", 1);//默认返回imageId=1
		}
		return retJson;
	}
	
	public String getUserNameByUserId(long userId) throws Exception{
		Map<String, Object> params = new HashMap();
		params.put("USER_ID", "" + userId);

		UserBean userBean = (UserBean) getBean(UserBean.class, params);
		if (userBean == null) {
			throw new Exception("参数userId" + userId + "不正确!");
		}

		return userBean.getUserName();
	}
	
	
	/**记录用户位置信息
	 * @param userId
	 * @param latitude
	 * @param longitude
	 */
	public void logUserPosition(long userId,String cityName,String latitude,String longitude){
		UserPositionBean hisBean = new UserPositionBean();
		
		hisBean.setUserId(userId);
		hisBean.setLatitude(latitude);
		hisBean.setLongitude(longitude);
		hisBean.setCityName(cityName);
		hisBean.setCreateDate(TimeUtil.getLocalTimeString());
		
		super.insertBean(hisBean);
	}
	
	
	/**
	 * 保存用户头像数据
	 * @param userId
	 * @param picData
	 * @throws Exception
	 */
	public void saveHeadPic(long userId,String picData) throws Exception{
		//判断是否已有头像,如果有则更新，没有则新建
		Map<String, Object> params = new HashMap();
		params.put("USER_ID", "" + userId);
		params.put("ALBUM_TYPE", "default");
		
		UserAlbumBean userAlbumBean = (UserAlbumBean) super.getBean(UserAlbumBean.class, params);
		if (userAlbumBean==null){
			//新建默认相册后在相册内增加照片
			userAlbumBean = (UserAlbumBean) super.newBean(UserAlbumBean.class);
			userAlbumBean.setUserId(userId);
			userAlbumBean.setAlbumName("default");//类型为默认相册
			userAlbumBean.setAlbumType("default");//类型为默认相册
			userAlbumBean.setCreateDate(TimeUtil.getLocalTimeString());
			
			long albumId = (long) super.insertBean(userAlbumBean);
			
			UserAlbumPhotoBean photoBean =  (UserAlbumPhotoBean) super.newBean(UserAlbumPhotoBean.class);
			photoBean.setAlbumId(albumId);
			photoBean.setPhotoData(picData);
			photoBean.setCreateDate(TimeUtil.getLocalTimeString());
			
			super.insertBean(photoBean);
		}else{
			//更新相册内的照片
			params.clear();
			params.put("ALBUM_ID", userAlbumBean.getAlbumId());
			
			UserAlbumPhotoBean photoBean = (UserAlbumPhotoBean) super.getBean(UserAlbumPhotoBean.class, params);
			if (photoBean!=null){
				photoBean.setPhotoData(picData);
				
				super.updateBean(photoBean);
			}else{
				photoBean =  (UserAlbumPhotoBean) super.newBean(UserAlbumPhotoBean.class);
				photoBean.setAlbumId(userAlbumBean.getAlbumId());
				photoBean.setPhotoData(picData);
				photoBean.setCreateDate(TimeUtil.getLocalTimeString());
				
				super.insertBean(photoBean);
			}
		}
	}
	
	/**
	 * 加载用户头像图片数据
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public String getHeadPic(long userId) throws Exception{
		//判断是否已有头像,如果有则更新，没有则新建
		Map<String, Object> params = new HashMap();
		params.put("USER_ID", "" + userId);
		params.put("ALBUM_TYPE", "default");
		
		UserAlbumBean userAlbumBean = (UserAlbumBean) super.getBean(UserAlbumBean.class, params);
		if (userAlbumBean!=null){
			//更新相册内的照片
			params.clear();
			params.put("ALBUM_ID", userAlbumBean.getAlbumId());
			
			UserAlbumPhotoBean photoBean = (UserAlbumPhotoBean) super.getBean(UserAlbumPhotoBean.class, params);
			if (photoBean!=null){
				return photoBean.getPhotoData();
			}
		}
		
		return null;
	}
	
	public String getPhotoData(long photoId) throws Exception {
		Map<String, Object> params = new HashMap();
		params.put("PHOTO_ID", photoId);

		UserAlbumPhotoBean photoBean = (UserAlbumPhotoBean) super.getBean(
				UserAlbumPhotoBean.class, params);
		if (photoBean != null) {
			return photoBean.getPhotoData();
		}
		return null;
	}
}
