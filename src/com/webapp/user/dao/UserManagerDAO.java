package com.webapp.user.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.StringUtils;

import com.webapp.common.bean.AbstractBean;
import com.webapp.common.bean.AlbumPhotoBean;
import com.webapp.common.dao.AbstractDAO;
import com.webapp.common.dao.AlbumManagerDAO;
import com.webapp.common.dao.CharSpecDAO;
import com.webapp.common.util.AcctTypeConsts;
import com.webapp.common.util.CachedUserPostitionUtil;
import com.webapp.common.util.CharSpecConsts;
import com.webapp.common.util.MD5Util;
import com.webapp.common.util.MsgTypeConsts;
import com.webapp.common.util.TimeUtil;
import com.webapp.user.bean.UserAcctBean;
import com.webapp.user.bean.UserAcctHisBean;
import com.webapp.user.bean.UserAlbumBean;
import com.webapp.user.bean.UserBean;
import com.webapp.user.bean.UserCharBean;
import com.webapp.user.bean.UserFeelingBean;
import com.webapp.user.bean.UserFriendsBean;
import com.webapp.user.bean.UserFriendsMsgBean;
import com.webapp.user.bean.UserPageHisBean;
import com.webapp.user.bean.UserPositionBean;
import com.webapp.user.bean.UserProdBean;
import com.webapp.user.bean.VCompanyStaffNumBean;
import com.webapp.user.bean.VOfflinePayStatBean;
import com.webapp.user.bean.VOnlinePayStatBean;
import com.webapp.user.bean.VUserHeadPicBean;
import com.webapp.user.bean.VUserMsgBean;
import com.webapp.user.bean.VUserOfflineWageStatBean;
import com.webapp.user.bean.VUserOnlineWageStatBean;
import com.webapp.user.bean.VUserWorkStatBean;

public class UserManagerDAO extends AbstractDAO {
	private static CharSpecDAO charSpecDao = null;

	public UserManagerDAO() {
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
		retJson.put("authState", userBean.getAuthState());// 返回认证状态,默认为no
		if (!StringUtils.isEmpty(userBean.getAlipayNo())) {
			retJson.put("alipayNo", userBean.getAlipayNo());// 返回支付宝帐号
			retJson.put("alipayName", userBean.getAlipayName());
		}

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
		acctBean.setAcctType(AcctTypeConsts.PREPAY);// 现金帐户
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
		//生成默认简历
		ResumeManagerDAO resumeDao = new ResumeManagerDAO();
		resumeDao.setDataSource(getDataSource());
		
		JSONObject resumeInfo = new JSONObject();
		resumeInfo.put("userId", userId);
		resumeInfo.put("resumeName", "默认简历");
		resumeInfo.put("resumeType", "partTime");
		resumeInfo.put("sex", userJson.getString("sex"));
		resumeInfo.put("age", userJson.getString("age"));
		
		resumeDao.newResume(resumeInfo);
		// 赠送500积分
		this.topup(userId, "points", "500","参加注册送500积分活动!");
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
		//支付宝帐号
		if (userJson.containsKey("alipayNo")) {
			user.setAlipayNo(userJson.getString("alipayNo"));
		}
		//支付宝户名
		if (userJson.containsKey("alipayName")) {
			user.setAlipayName(userJson.getString("alipayName"));
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
		String str = "1^jack.cao@163.com^??^1.92^F^TRANSFER_AMOUNT_NOT_ENOUGH^20151018528573772^20151018214952|";

		String[] arr = str.split("\\^");
		
		/*ApplicationContext context = new ClassPathXmlApplicationContext("Application-Context.xml");

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

		userDAO.insertBean(user);*/

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

	/* 根据用户ID加载手机号码 */
	public String getCellPhoneByUserId(long userId) throws Exception {
		Map<String, Object> params = new HashMap();
		params.put("CHAR_ID", "" + CharSpecConsts.CELLPHONE);// CHAR_ID=1表示手机
		params.put("USER_ID", "" + userId);
		UserCharBean charBean = (UserCharBean) getBean(UserCharBean.class, params);
		if (charBean == null) {
			throw new Exception("未找到用户id:" + userId + "登记的手机号码!");
		}

		return charBean.getValue();
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
		retJson.put("alipayNo", userBean.getAlipayNo());
		retJson.put("alipayName", userBean.getAlipayName());
		
		return retJson;
	}

	/**
	 * 加载用户详情
	 * @param userId
	 * @return
	 * @throws Exception
	 */
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
		retJson.put("alipayNo", userBean.getAlipayNo());
		retJson.put("alipayName", userBean.getAlipayName());
		retJson.put("prepay", 0);
		retJson.put("points", 0);
		retJson.put("deposit", 0);

		// 返回用户心情
		params.clear();
		params.put("USER_ID", "" + userBean.getUserId());
		params.put("ORDERBY", "CREATE_DATE DESC");
		params.put("FIRSTROW", "TRUE");// 只按照时间倒序加载第一条

		UserFeelingBean feelingBean = (UserFeelingBean) super.getBean(UserFeelingBean.class, params);
		if (feelingBean != null) {
			retJson.put("feelingText", feelingBean.getFeelingText());// 心情文字
			retJson.put("feelingImage", feelingBean.getImageId());// 心情图片
		}

		// 返回用户帐户信息
		params.clear();
		params.put("USER_ID", "" + userBean.getUserId());
		List<AbstractBean> result = getBeans(UserAcctBean.class, params);
		if (result != null && result.size() > 0) {
			for (AbstractBean abBean : result) {
				UserAcctBean acctBean = (UserAcctBean) abBean;
					// 返回余额
				retJson.put(acctBean.getAcctType(), acctBean.getValue()/100.0);
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
		// 返回用户头像图片id
		params.clear();
		params.put("USER_ID", "" + userBean.getUserId());
		VUserHeadPicBean userHeadPicBean = (VUserHeadPicBean) super.getBean(VUserHeadPicBean.class, params);
		if (userHeadPicBean != null) {
			retJson.put("headPicId", userHeadPicBean.getPhotoId());
		} else {
			retJson.put("headPicId", 1);// 默认返回imageId=1
		}
		//返回用户统计类信息
		if ("private".equals(userBean.getUserType())){//个人用户
			//返回参加工作数量
			params.clear();
			params.put("USER_ID", userBean.getUserId());
			
			VUserWorkStatBean workNum = (VUserWorkStatBean) super.getBean(VUserWorkStatBean.class, params);
			if (workNum!=null){
				retJson.put("workNum", workNum.getWorkNum());
			}else{
				retJson.put("workNum", 0);
			}
			//返回线上收款金额
			params.clear();
			params.put("USER_ID", userBean.getUserId());
			
			VUserOnlineWageStatBean onlineWage = (VUserOnlineWageStatBean) super.getBean(VUserOnlineWageStatBean.class, params);
			if (workNum!=null){
				retJson.put("onlineWage", onlineWage.getAmount());
			}else{
				retJson.put("onlineWage", 0);
			}
			//返回线下收款金额
			params.clear();
			params.put("USER_ID", userBean.getUserId());
			
			VUserOfflineWageStatBean offlineWage = (VUserOfflineWageStatBean) super.getBean(VUserOfflineWageStatBean.class, params);
			if (workNum!=null){
				retJson.put("offlineWage", offlineWage.getAmount());
			}else{
				retJson.put("offlineWage", 0);
			}
		}else{//公司用户
			//返回员工数
			params.clear();
			params.put("USER_ID", userBean.getUserId());
			
			VCompanyStaffNumBean staffNum = (VCompanyStaffNumBean) super.getBean(VCompanyStaffNumBean.class, params);
			if (staffNum!=null){
				retJson.put("staffNum", staffNum.getStaffNum());
			}else{
				retJson.put("staffNum", 0);
			}
			
			
			//线上发放工资数
			params.clear();
			params.put("USER_ID", userBean.getUserId());
			
			VOnlinePayStatBean onlinePayAmount = (VOnlinePayStatBean) super.getBean(VOnlinePayStatBean.class, params);
			if(onlinePayAmount!=null){
				retJson.put("onlinePayAmount", onlinePayAmount.getAmount());
			}else{
				retJson.put("onlinePayAmount", 0);
			}
			
			
			//线下发放工资数
			params.clear();
			params.put("USER_ID", userBean.getUserId());
			
			VOfflinePayStatBean offlinePayAmount = (VOfflinePayStatBean) super.getBean(VOfflinePayStatBean.class, params);
			if(offlinePayAmount!=null){
				retJson.put("offlinePayAmount", offlinePayAmount.getAmount());
			}else{
				retJson.put("offlinePayAmount", 0);
			}
		}
		return retJson;
	}

	public String getUserNameByUserId(long userId) throws Exception {
		Map<String, Object> params = new HashMap();
		params.put("USER_ID", "" + userId);

		UserBean userBean = (UserBean) getBean(UserBean.class, params);
		if (userBean == null) {
			throw new Exception("参数userId" + userId + "不正确!");
		}

		return userBean.getUserName();
	}

	/**
	 * 记录用户位置信息
	 * 
	 * @param userId
	 * @param latitude
	 * @param longitude
	 */
	public void logUserPosition(long userId, String cityName, String latitude, String longitude) {
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
	 * 
	 * @param userId
	 * @param picData
	 * @throws Exception
	 */
	public void saveHeadPic(long userId, String picData) throws Exception {
		AlbumManagerDAO albumManager = new AlbumManagerDAO();
		albumManager.setDataSource(getDataSource());
		// 判断是否已有头像,如果有则更新，没有则新建
		Map<String, Object> params = new HashMap();
		params.put("USER_ID", "" + userId);
		params.put("ALBUM_TYPE", "default");

		UserAlbumBean userAlbumBean = (UserAlbumBean) super.getBean(UserAlbumBean.class, params);
		if (userAlbumBean == null) {
			// 新建默认相册后在相册内增加照片
			long albumId = albumManager.newAlbum("default","default",new String[]{picData});
			userAlbumBean = (UserAlbumBean) super.newBean(UserAlbumBean.class);
			userAlbumBean.setUserId(userId);
			userAlbumBean.setAlbumId(albumId);
			userAlbumBean.setAlbumType("default");// 类型为默认相册
			userAlbumBean.setCreateDate(TimeUtil.getLocalTimeString());

			super.insertBean(userAlbumBean);
		} else {
			// 更新相册内的照片
			List<AbstractBean> list = albumManager.getPhotos(userAlbumBean.getAlbumId());
			if (list != null&&list.size()>0) {
				AlbumPhotoBean photoBean = (AlbumPhotoBean) list.get(0);
				photoBean.setPhotoData(picData);

				super.updateBean(photoBean);
			} else {
				AlbumPhotoBean photoBean = (AlbumPhotoBean) super.newBean(AlbumPhotoBean.class);
				photoBean.setAlbumId(userAlbumBean.getAlbumId());
				photoBean.setPhotoData(picData);
				photoBean.setCreateDate(TimeUtil.getLocalTimeString());

				super.insertBean(photoBean);
			}
		}
	}

	/**
	 * 加载用户头像图片数据
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public long getHeadPic(long userId) throws Exception {
		// 判断是否已有头像,如果有则更新，没有则新建
		Map<String, Object> params = new HashMap();
		params.put("USER_ID", "" + userId);
		params.put("ALBUM_TYPE", "default");

		UserAlbumBean userAlbumBean = (UserAlbumBean) super.getBean(UserAlbumBean.class, params);
		if (userAlbumBean != null) {
			AlbumManagerDAO albumManager = new AlbumManagerDAO();
			albumManager.setDataSource(getDataSource());

			List<AbstractBean> list = albumManager.getPhotos(userAlbumBean.getAlbumId());
			if (list != null&&list.size()>0) {
				AlbumPhotoBean photoBean = (AlbumPhotoBean) list.get(0);
				return photoBean.getPhotoId();
			}
		}

		return 0;
	}

	public JSONArray getAuthPic(long userId) {
		JSONArray picIds = new JSONArray();
		Map<String, Object> params = new HashMap();
		params.put("USER_ID", "" + userId);
		params.put("ALBUM_TYPE", "auth");// 认证相册

		UserAlbumBean userAlbumBean = (UserAlbumBean) super.getBean(UserAlbumBean.class, params);
		if (userAlbumBean != null) {
			params.clear();
			params.put("ALBUM_ID", "" + userAlbumBean.getAlbumId());
			List<AbstractBean> result = super.getBeans(AlbumPhotoBean.class, params);
			if (result != null && result.size() > 0) {

				for (AbstractBean bean : result) {
					AlbumPhotoBean realBean = (AlbumPhotoBean) bean;
					picIds.add(realBean.getPhotoId());
				}
			}
		}
		return picIds;
	}

	/**保存头像
	 * @param userId
	 * @param authPicData
	 * @throws Exception
	 */
	public void saveAuthPic(long userId, String authPicData) throws Exception {
		AlbumManagerDAO albumManager = new AlbumManagerDAO();
		albumManager.setDataSource(getDataSource());
		// 判断是否已有头像,如果有则更新，没有则新建
		Map<String, Object> params = new HashMap();
		params.put("USER_ID", "" + userId);
		params.put("ALBUM_TYPE", "auth");// 认证相册

		UserAlbumBean userAlbumBean = (UserAlbumBean) super.getBean(UserAlbumBean.class, params);
		if (userAlbumBean == null) {
			long albumId = albumManager.newAlbum("auth","auth",new String[]{authPicData});
			// 新建默认相册后在相册内增加照片
			userAlbumBean = (UserAlbumBean) super.newBean(UserAlbumBean.class);
			userAlbumBean.setUserId(userId);
			userAlbumBean.setAlbumId(albumId);
			userAlbumBean.setAlbumType("auth");// 类型为认证相册
			userAlbumBean.setCreateDate(TimeUtil.getLocalTimeString());

			super.insertBean(userAlbumBean);
		} else {
			AlbumPhotoBean photoBean = (AlbumPhotoBean) super.newBean(AlbumPhotoBean.class);
			photoBean.setAlbumId(userAlbumBean.getAlbumId());
			photoBean.setPhotoData(authPicData);
			photoBean.setCreateDate(TimeUtil.getLocalTimeString());

			super.insertBean(photoBean);
		}
	}

	/**新增心情
	 * @param userId
	 * @param feelingText
	 * @param picData
	 * @throws Exception
	 */
	public void saveUserFeeling(long userId, String feelingText, String picData) throws Exception {
		AlbumManagerDAO albumManager = new AlbumManagerDAO();
		albumManager.setDataSource(getDataSource());
		
		long photoId = 0;
		if (!StringUtils.isEmpty(picData)) {
			// 判断是否已有心情相册,如果有则增加图片，没有则新建相册后增加图片
			Map<String, Object> params = new HashMap();
			params.put("USER_ID", "" + userId);
			params.put("ALBUM_TYPE", "feeling");// 心情相册

			UserAlbumBean userAlbumBean = (UserAlbumBean) super.getBean(UserAlbumBean.class, params);
			if (userAlbumBean == null) {
				long albumId = albumManager.newAlbum("feeling","feeling",new String[]{});
				AlbumPhotoBean photoBean = (AlbumPhotoBean) super.newBean(AlbumPhotoBean.class);
				photoBean.setAlbumId(albumId);
				photoBean.setPhotoData(picData);
				photoBean.setCreateDate(TimeUtil.getLocalTimeString());

				photoId = (long) super.insertBean(photoBean);
				
				// 新建默认相册后在相册内增加照片
				userAlbumBean = (UserAlbumBean) super.newBean(UserAlbumBean.class);
				userAlbumBean.setUserId(userId);
				userAlbumBean.setAlbumId(albumId);
				userAlbumBean.setAlbumType("feeling");// 类型为心情相册
				userAlbumBean.setCreateDate(TimeUtil.getLocalTimeString());

				super.insertBean(userAlbumBean);
			} else {
				AlbumPhotoBean photoBean = (AlbumPhotoBean) super.newBean(AlbumPhotoBean.class);
				photoBean.setAlbumId(userAlbumBean.getAlbumId());
				photoBean.setPhotoData(picData);
				photoBean.setCreateDate(TimeUtil.getLocalTimeString());

				photoId = (long) super.insertBean(photoBean);
			}
		}

		
		// 保存心情
		UserFeelingBean feelingBean = (UserFeelingBean) super.newBean(UserFeelingBean.class);

		feelingBean.setUserId(userId);
		feelingBean.setFeelingText(feelingText);
		feelingBean.setImageId(photoId);
		feelingBean.setCreateDate(TimeUtil.getLocalTimeString());

		super.insertBean(feelingBean);
		
		// 赠送50积分
		this.topup(userId, "points", "50","参加心情签到送50积分活动!");
	}
	public void delFeeling(long feelingId) throws Exception{
		Map<String, Object> params = new HashMap();
		params.put("FEELING_ID", "" + feelingId);
		UserFeelingBean feelingBean = (UserFeelingBean) super.getBean(UserFeelingBean.class, params);
		if (feelingBean!=null){
			super.deleteBean(feelingBean);
			AlbumManagerDAO photoManager = new AlbumManagerDAO();
			photoManager.setDataSource(getDataSource());
			photoManager.delPhoto(feelingBean.getImageId());
		}
	}
	/**
	 * 返回用户心情
	 * @param userId
	 * @param page
	 * @return
	 */
	public JSONArray getUserFeelingList(long userId, int page) {
		JSONArray retList = new JSONArray();
		Map<String, Object> params = new HashMap();
		params.put("USER_ID", "" + userId);
		params.put("PAGE", "" + page);
		params.put("ORDERBY", "CREATE_DATE DESC");

		List<AbstractBean> feelList = super.getBeans(UserFeelingBean.class, params);
		if (feelList != null) {
			for (AbstractBean bean : feelList) {
				UserFeelingBean realBean = (UserFeelingBean) bean;
				JSONObject retObj = new JSONObject();
				retObj.put("feelingId", realBean.getFeelingId());
				retObj.put("feelingText", realBean.getFeelingText());
				retObj.put("feelingImage", realBean.getImageId());
				retObj.put("createDate", realBean.getCreateDate());
				retList.add(retObj);
			}
		}
		return retList;
	}

	/**返回指定类型的帐户信息及历史
	 * @param userId
	 * @param acctType
	 * @return
	 * @throws Exception
	 */
	public JSONObject getUserAcctInfo(long userId, String acctType) throws Exception {
		Map<String, Object> params = new HashMap();
		params.put("USER_ID", "" + userId);
		params.put("ACCT_TYPE", acctType);

		UserAcctBean userBean = (UserAcctBean) getBean(UserAcctBean.class, params);
		if (userBean == null) {
			throw new Exception("userId:" + userId + "没有类型为:"+acctType+"的帐户!");
		}

		// 返回用户基本信息
		JSONObject retJson = new JSONObject();
		retJson.put("acctId", userBean.getAcctId());
		retJson.put("acctValue", userBean.getValue()/100.0);

		// 返回第一页帐户变动历史
		JSONArray hisArray = getAcctHisList(userBean.getAcctId(), 1);
		if (hisArray != null && hisArray.size() > 0) {
			retJson.put("hisList", hisArray);
		}
		return retJson;
	}

	/**返回帐户操作历史
	 * @param acctId
	 * @param page
	 * @return
	 */
	public JSONArray getAcctHisList(long acctId, int page) {
		JSONArray retList = new JSONArray();
		Map<String, Object> params = new HashMap();
		params.put("ACCT_ID", "" + acctId);
		params.put("PAGE", "" + page);
		params.put("ORDERBY", "CREATE_DATE DESC");

		List<AbstractBean> feelList = super.getBeans(UserAcctHisBean.class, params);
		if (feelList != null) {
			for (AbstractBean bean : feelList) {
				UserAcctHisBean realBean = (UserAcctHisBean) bean;
				JSONObject retObj = new JSONObject();
				retObj.put("opType", realBean.getValueOPType());
				retObj.put("remark", realBean.getRemark());
				retObj.put("createDate", realBean.getCreateDate());
				retObj.put("value", realBean.getValue()/100.0);
				retList.add(retObj);
			}
		}
		return retList;
	}
	
	/**
	 * 帐户充值
	 * @param userId
	 * @param acctType
	 * @param value
	 * @throws Exception
	 */
	public void topup(long userId,String acctType,String value,String remark) throws Exception{
		Map<String, Object> params = new HashMap();
		params.put("USER_ID", "" + userId);
		params.put("ACCT_TYPE", acctType);

		UserAcctBean userBean = (UserAcctBean) getBean(UserAcctBean.class, params);
		if (userBean == null) {
			throw new Exception("userId:" + userId + "没有类型为:"+acctType+"的帐户!");
		}
		//更新帐户余额
		long oldValue = userBean.getValue();
		double dValue = Double.parseDouble(value);
		long dValueToLong = (long)(dValue*100);
		long newValue = oldValue + dValueToLong;
		userBean.setValue(newValue);
		
		super.updateBean(userBean);
		//记录历史
		UserAcctHisBean hisBean = (UserAcctHisBean) super.newBean(UserAcctHisBean.class);
		
		hisBean.setAcctId(userBean.getAcctId());
		hisBean.setValueOPType("topup");//操作类型为充值
		hisBean.setValue(dValueToLong);
		hisBean.setCreateDate(TimeUtil.getLocalTimeString());
		hisBean.setRemark(remark);
		
		super.insertBean(hisBean);
		
		
		//发送到帐通知
		StringBuffer msgType = new StringBuffer();
		StringBuffer msg = new StringBuffer();
		
		if (acctType.equals(AcctTypeConsts.POINTS)){//积分到帐通知
			msgType.append(MsgTypeConsts.POINTSIN);
			msg.append(value).append("积分到帐,备注:").append(remark);
		}else if (acctType.equals(AcctTypeConsts.PREPAY)){//预存到帐通知
			msgType.append(MsgTypeConsts.PREPAYIN);
			msg.append(value).append("元预存款到帐,备注:").append(remark);
		}else if (acctType.equals(AcctTypeConsts.DEPOSIT)){//保证金到帐通知
			msgType.append(MsgTypeConsts.DEPOSITIN);
			msg.append(value).append("元保证金到帐,备注:").append(remark);
		}
		
		sendMsg(0,userId,msg.toString(),msgType.toString());
	}
	
	/**
	 * 记录页面打开日志
	 * @param logInfo
	 * @throws Exception
	 */
	public void logPageOpenHis(JSONObject logInfo) throws Exception {
		long userId = logInfo.getLong("userId");
		String newPage = logInfo.getString("newPageId");
		String oldPage = logInfo.getString("oldPageId");
		
		UserPageHisBean hisBean = (UserPageHisBean) super.newBean(UserPageHisBean.class);
		
		hisBean.setUserId(userId);
		hisBean.setNewPageId(newPage);
		hisBean.setOldPageId(oldPage);
		hisBean.setCreateDate(TimeUtil.getLocalTimeString());
		
		super.insertBean(hisBean);
	}
	
	
	/**
	 * 新加好友
	 * @param userId
	 * @param friendId
	 * @throws Exception
	 */
	public void addFriend(long userId,long friendId) throws Exception{
		UserFriendsBean newBean = (UserFriendsBean) super.newBean(UserFriendsBean.class);
		
		newBean.setUserId(userId);
		newBean.setFriendId(friendId);
		newBean.setCreateDate(TimeUtil.getLocalTimeString());
		newBean.setState("auth");
		super.insertBean(newBean);
		
		
		//向friend发送加好友通知
		JSONObject userInfo = this.getUserBasicInfo(friendId);
		sendMsg(userId,friendId,userInfo.getString("userName")+"加你为好友!",MsgTypeConsts.ADDFRIEND);
	}
	
	
	/**
	 * 向friendId指定的用户发消息
	 * @param userId   消息发判断人
	 * @param friendId 消息接收人
	 * @param msg	   消息内容
	 * @param msgType	   消息类型
	 * @throws Exception
	 */
	public void sendMsg(long userId,long friendId,String msg,String msgType) throws Exception{
		UserFriendsMsgBean newBean = (UserFriendsMsgBean) super.newBean(UserFriendsMsgBean.class);
		
		newBean.setUserId(userId);
		newBean.setFriendId(friendId);
		newBean.setMsgContent(msg);
		newBean.setMsgType(msgType);
		newBean.setState("new");
		newBean.setCreateDate(TimeUtil.getLocalTimeString());
		super.insertBean(newBean);
	}
	/**
	 * 将消息标为已读
	 * @param msgId
	 * @throws Exception
	 */
	public void readFriendMsg(long[] msgIds) throws Exception{
		for (int i=0;i<msgIds.length;i++){
			Map<String, Object> params = new HashMap();
			params.put("MSG_ID", "" + msgIds[i]);
			
			UserFriendsMsgBean upBean = (UserFriendsMsgBean) super.getBean(UserFriendsMsgBean.class, params);
			
			if (upBean!=null){
				upBean.setState("read");
				super.updateBean(upBean);
			}
		}
	}
	
	/**
	 * 加载与指定好友的对话消息列表
	 * @param userId
	 * @param friendId
	 * @param state new 新消息(未读)/read 已读
	 * @return
	 * @throws Exception
	 */
	public JSONArray getFriendMsgList(long userId,long friendId,String msgType) throws Exception{
		Map<String, Object> params = new HashMap();

		String wheresql = "((user_id=-user_id- AND friend_id=-friend_id-) OR (user_id=-friend_id- AND friend_id=-user_id-))".replaceAll("-user_id-", ""+userId).replaceAll("-friend_id-", ""+friendId);
		params.put("WHERESQL", wheresql);
		params.put("MSG_TYPE", msgType);
		params.put("ORDERBY", "CREATE_DATE ASC");
		
		List<AbstractBean> msgList = super.getBeans(UserFriendsMsgBean.class, params);
		JSONArray retArray = new JSONArray();
		if (msgList!=null&&msgList.size()>0){
			for (AbstractBean bean :msgList){
				UserFriendsMsgBean msgBean = (UserFriendsMsgBean)bean;
				
				JSONObject jsonMsg = new JSONObject();
				if (userId==msgBean.getUserId()){//我发出的消息
					jsonMsg.put("direction", "fromMe");
				}else{//我收到的消息
					jsonMsg.put("direction", "toMe");
				}
				jsonMsg.put("msgContent", msgBean.getMsgContent());
				jsonMsg.put("createDate", msgBean.getCreateDate());
				
				retArray.add(jsonMsg);
				if (msgBean.getFriendId()==userId&&"new".equals(msgBean.getState())){//如果是别人发给自己的新消息则标为已读
					msgBean.setState("read");
					super.updateBean(msgBean);
				}
			}
		}
		
		return retArray;
	}
	/**
	 * 删除好友
	 * @param userId
	 * @param friendId
	 * @throws Exception
	 */
	public void delFriend(long userId,long[] friendIds) throws Exception{
		for (int i=0;i<friendIds.length;i++){
			Map<String, Object> params = new HashMap();
			params.put("USER_ID", "" + userId);
			params.put("FRIEND_ID", friendIds[i]);
			
			UserFriendsBean delBean = (UserFriendsBean) super.getBean(UserFriendsBean.class, params);
			
			if (delBean!=null){
				super.deleteBean(delBean);
			}
		}
	}
	
	/**
	 * 加载当月哪些天发过心情
	 * @param userId
	 * @param month
	 * @return
	 */
	public JSONArray getMonthFeeling(long userId,String month){
		Map<String, Object> params = new HashMap();
		params.put("USER_ID", "" + userId);
		params.put("EXTRACT(YEAR_MONTH FROM create_date)", month);
		
		List<AbstractBean> msgList = super.getBeans(UserFeelingBean.class, params);
		JSONArray retArray = new JSONArray();
		if (msgList!=null&&msgList.size()>0){
			for (AbstractBean bean :msgList){
				UserFeelingBean msgBean = (UserFeelingBean)bean;
				
				retArray.add(msgBean.getCreateDate().replaceAll("-", "").substring(0, 8));
			}
		}
		
		return retArray;
	}
	/**加载指定日期发表的心情
	 * @param userId
	 * @param day
	 * @return
	 */
	public JSONArray getDayFeeling(long userId,String day){
		Map<String, Object> params = new HashMap();
		params.put("USER_ID", "" + userId);
		params.put("DATE_FORMAT(create_date,'%Y%m%d')", day);
		
		List<AbstractBean> msgList = super.getBeans(UserFeelingBean.class, params);
		JSONArray retArray = new JSONArray();
		if (msgList!=null&&msgList.size()>0){
			for (AbstractBean bean :msgList){
				UserFeelingBean msgBean = (UserFeelingBean)bean;
				JSONObject json = new JSONObject();
				json.put("feelingText", msgBean.getFeelingText());
				json.put("feelingImageId", msgBean.getImageId());
				json.put("createDate", msgBean.getCreateDate());
				retArray.add(json);
			}
		}
		
		return retArray;
	}
	
	
	/**
	 * 加载新消息数量
	 * @param userId
	 * @return
	 */
	public long getNewMsgCount(long userId){
		Map<String, Object> params = new HashMap();
		params.put("USER_ID", "" + userId);
		params.put("STATE", "new");
		
		long msgCount = super.getBeanCount(UserFriendsMsgBean.class, params);
		
		return msgCount;
	}
	
	/**
	 * 取其它人发给指定用户的所有类型的最新消息
	 * @param userId
	 * @return
	 * @throws Exception 
	 */
	public JSONArray getAllTypeNewMsg(long userId) throws Exception{
		Map<String, Object> params = new HashMap();
		params.put("FRIEND_ID", new String[]{"0",String.valueOf(userId)} );
		
		List<AbstractBean> msgList = super.getBeans(VUserMsgBean.class, params);
		JSONArray retArray = new JSONArray();
		if (msgList!=null&&msgList.size()>0){
			for (AbstractBean bean :msgList){
				VUserMsgBean msgBean = (VUserMsgBean)bean;
				JSONObject json = new JSONObject();
				
				json.put("friendId", msgBean.getUserId());
				if (msgBean.getUserId()>0){
					JSONObject usrInfo = this.getUserBasicInfo(msgBean.getUserId());
					json.put("friendName", usrInfo.getString("userName"));
				}else{
					json.put("friendName", "系统");
				}
				//返回用户头像id
				long imageId = this.getHeadPic(msgBean.getUserId());
				if (imageId>0){
					json.put("friendHeadPicImageId", imageId);
				}else{
					json.put("friendHeadPicImageId", 1);
				}
				
				json.put("msgType", msgBean.getMsgType());
				json.put("msgContent", msgBean.getMsgContent());
				json.put("state", msgBean.getState());
				json.put("createDate", msgBean.getCreateDate());
				
				retArray.add(json);
			}
		}
		
		return retArray;
	}
	
	
	/**加载好友列表
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public JSONArray getFriendList(long userId) throws Exception{
		Map<String, Object> params = new HashMap();
		String wheresql = "(user_id=-user_id- OR  friend_id=-user_id-)".replaceAll("-user_id-", ""+userId);
		params.put("WHERESQL", wheresql);
		
		List<AbstractBean> msgList = super.getBeans(UserFriendsBean.class, params);
		JSONArray retArray = new JSONArray();
		if (msgList!=null&&msgList.size()>0){
			for (AbstractBean bean :msgList){
				UserFriendsBean msgBean = (UserFriendsBean)bean;
				JSONObject json = new JSONObject();
				long friendId = 0;
				if (msgBean.getFriendId()==userId){
					friendId = msgBean.getUserId();
				}else{
					friendId = msgBean.getFriendId();
				}
				json.put("friendId", friendId);
				
				JSONObject usrInfo = this.getUserBasicInfo(friendId);
				json.put("friendName", usrInfo.getString("userName"));
				json.put("friendType", usrInfo.getString("userType"));
				
				//返回用户头像id
				long imageId = this.getHeadPic(friendId);
				if (imageId>0){
					json.put("friendHeadPicImageId", imageId);
				}else{
					json.put("friendHeadPicImageId", 1);//默认头像
				}
				
				params.clear();
				params.put("USER_ID", "" + friendId);
				params.put("ORDERBY", "CREATE_DATE DESC");
				params.put("FIRSTROW", "TRUE");// 只按照时间倒序加载第一条

				UserFeelingBean feelingBean = (UserFeelingBean) super.getBean(UserFeelingBean.class, params);
				if (feelingBean != null) {
					json.put("feelingText", feelingBean.getFeelingText());// 心情文字
					json.put("feelingImage", feelingBean.getImageId());// 心情图片
				}
				
				//返回性别
				params.clear();
				params.put("USER_ID", "" + friendId);
				params.put("CHAR_ID", "2");//性别特征id
				UserCharBean charBean = (UserCharBean) super.getBean(UserCharBean.class,params);
				if (charBean!=null){
					json.put("sex", charBean.getValue());
				}
				//返回年龄
				params.clear();
				params.put("USER_ID", "" + friendId);
				params.put("CHAR_ID", "21");//年龄特征id
				charBean = (UserCharBean) super.getBean(UserCharBean.class,params);
				if (charBean!=null){
					json.put("age", charBean.getValue());
				}
				//返回与好友的距离
				double distance = CachedUserPostitionUtil.getDistance(userId,friendId);
				json.put("distance",  distance);
				
				retArray.add(json);
			}
		}
		
		return retArray;
	}
	
	/**
	 * 取附近的非好友用户列表
	 * @param userId
	 * @return
	 * @throws Exception 
	 */
	public JSONArray getNearbyUserList(List<JSONObject> users) throws Exception{
		JSONArray retArr = new JSONArray();
		if (users.size()>0){
			for (JSONObject user:users){
				long userId = user.getLong("userId");
				user.remove("userId");
				
				JSONObject usrInfo = this.getUserBasicInfo(userId);
				user.put("friendId", userId);
				user.put("friendName", usrInfo.getString("userName"));
				user.put("friendType", usrInfo.getString("userType"));
				//返回用户头像id
				long imageId = this.getHeadPic(userId);
				if (imageId>0){
					user.put("friendHeadPicImageId", imageId);
				}
				else{
					user.put("friendHeadPicImageId", 1);//默认头像
				}
				
				Map<String, Object> params = new HashMap();
				params.clear();
				params.put("USER_ID", "" + userId);
				params.put("ORDERBY", "CREATE_DATE DESC");
				params.put("FIRSTROW", "TRUE");// 只按照时间倒序加载第一条

				UserFeelingBean feelingBean = (UserFeelingBean) super.getBean(UserFeelingBean.class, params);
				if (feelingBean != null) {
					user.put("feelingText", feelingBean.getFeelingText());// 心情文字
					user.put("feelingImage", feelingBean.getImageId());// 心情图片
				}
				
				//返回性别
				params.clear();
				params.put("USER_ID", "" + userId);
				params.put("CHAR_ID", "2");//性别特征id
				UserCharBean charBean = (UserCharBean) super.getBean(UserCharBean.class,params);
				if (charBean!=null){
					user.put("sex", charBean.getValue());
				}
				//返回年龄
				params.clear();
				params.put("USER_ID", "" + userId);
				params.put("CHAR_ID", "21");//年龄特征id
				charBean = (UserCharBean) super.getBean(UserCharBean.class,params);
				if (charBean!=null){
					user.put("age", charBean.getValue());
				}
				retArr.add(user);
			}
		}
		return retArr;
	}
	
	/**修改用户手机
	 * @param userId
	 * @param newCellphone
	 * @throws Exception
	 */
	public void changeCellphone(long userId,String newCellphone) throws Exception{
		Map<String, Object> params = new HashMap();
		params.put("CHAR_ID", "1");//手机号特征
		params.put("VALUE", newCellphone);
		UserCharBean oldcharBean = (UserCharBean) super.getBean(UserCharBean.class,params);
		if (oldcharBean!=null){
			throw new Exception("新号码已被使用,请换个号码!");
		}
		params.clear();
		params.put("USER_ID", "" + userId);
		params.put("CHAR_ID", "1");//手机号特征
		UserCharBean charBean = (UserCharBean) super.getBean(UserCharBean.class,params);
		if (charBean!=null){
			String oldCellphone = charBean.getValue();
			if (newCellphone.equals(oldCellphone)){
				throw new Exception("新号码与旧号码一致,不需要更换!");
			}
			charBean.setValue(newCellphone);
			super.updateBean(charBean);
		}else{
			throw new Exception("无效的userId:"+userId);
		}
	}
	
	/**
	 * 根据用户名模糊查询
	 * @param searchKey
	 * @return
	 * @throws Exception
	 */
	public JSONArray getUserList(String searchKey,long page) throws Exception{
		Map<String, Object> params = new HashMap();

		Pattern pattern = Pattern.compile("[0-9]*");
		Boolean isDigital = pattern.matcher(searchKey).matches();
		if (isDigital) {
			params.put("USER_ID", searchKey);// 用户id查询
		} else {
			params.put("USER_NAME", searchKey);// 用户名模糊查询
			params.put("LIKECOL", "USER_NAME");
		}
		
		if (page>0){
			params.put("PAGE", "" + page);
		}
		JSONArray retArr = new JSONArray();
		List<AbstractBean> users = (List<AbstractBean>) super.getBeans(UserBean.class,params);
		if (users!=null&&users.size()>0){
			for (AbstractBean bean:users){
				UserBean userBean = (UserBean)bean;
				
				JSONObject userJson = this.getUserBasicInfo(userBean.getUserId());
				
				if (userJson!=null){
					retArr.add(userJson);
				}
			}
		}
		
		return retArr;
	}
	
	/**
	 * 增加好友关注
	 * @param userId
	 * @param friendId
	 */
	public void addFocus(long userId,long friendId){
		
	}
}
