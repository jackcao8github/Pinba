package com.webapp.user.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.ContextLoaderListener;

import com.webapp.common.servlet.AbstractServlet;
import com.webapp.common.servlet.WebSocketMessageInbound;
import com.webapp.common.servlet.WebSocketMessageInboundPool;
import com.webapp.common.util.CachedMsgVerifyCodeUtil;
import com.webapp.common.util.CachedUserPostitionUtil;
import com.webapp.common.util.ExceptionUtil;
import com.webapp.common.util.TimeUtil;
import com.webapp.user.dao.UserManagerDAO;

public class UserManagerServlet extends AbstractServlet {
	private UserManagerDAO userDao = null;

	public UserManagerServlet() {
		super();
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		userDao = (UserManagerDAO) ContextLoaderListener.getCurrentWebApplicationContext().getBean("userDAOProxy");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpServletRequest hreq = (HttpServletRequest) request;
		response.setContentType("text/html;charset=utf-8");
		String method = hreq.getParameter("method");
		if (StringUtils.isEmpty(method)) {
			returnFailResult("参数method不能为空", response);
			return;
		}

		try {
			if (method.equals("newUser")) {// 新增用户
				JSONObject userJson = super.getJsonFromReq(request,"userInfo");
				String msgVerifyCode = super.getStringFromJSON(userJson, "msgVerifyCode", true);
				String cellphone = super.getStringFromJSON(userJson, "cellphone", true);
				//将验证码和存储的验证码进行比较
				String cachedVerifyCode = CachedMsgVerifyCodeUtil.getCachedData(cellphone);
				if (!StringUtils.equalsIgnoreCase(msgVerifyCode, cachedVerifyCode)){
					returnFailResult("短信验证码不正确或已超时!", response);
					return;
				}
				// 新建用户
				long userId = userDao.newUser(userJson);
				
				// 立即登陆后返回
				JSONObject loginInfo = new JSONObject();
				loginInfo.put("loginCode", userId);
				loginInfo.put("password", userJson.getString("password"));

				JSONObject retJson = userDao.login(loginInfo);
				returnSuccessResult(retJson, response);
			} else if (method.equals("modUser")) {// 修改用户
				JSONObject userJson = super.getJsonFromReq(request,"userInfo");
				userDao.modUser(userJson);
				JSONObject retJson = new JSONObject();
				retJson.put("msg", "修改成功");
				returnSuccessResult(retJson, response);
			} else if (method.equals("login")) {// 登陆
				JSONObject userJson = super.getJsonFromReq(request,"userInfo");
				String verifyCode = userJson.getString("verifyCode");// 图片验证码
				Object verifyCodeObj = request.getSession().getAttribute("__SESSION_VERIFY__");
				String verifyCodeInSession = null;
				if (verifyCodeObj != null) {
					verifyCodeInSession = verifyCodeObj.toString();
				}

				if (StringUtils.equalsIgnoreCase(verifyCode, verifyCodeInSession)) {

				} else {
					returnFailResult("验证码不正确!", response);
					return;
				}
				// 使用私钥对密码进行解密
				/*
				 * String password = userJson.getString("password"); String
				 * privateKeyEx = (String)
				 * request.getSession().getAttribute("__SESSION_PRIVATE_KEY1__"
				 * ); String privateKeyMo = (String)
				 * request.getSession().getAttribute
				 * ("__SESSION_PRIVATE_KEY2__");
				 * 
				 * RSAPrivateKey privateKey =
				 * RSAUtils.getPrivateKey(privateKeyMo, privateKeyEx); String
				 * descrypedPwd = RSAUtils.decryptByPrivateKey(password,
				 * privateKey); //解密后的密码,password是提交过来的密码
				 * 
				 * int ind = descrypedPwd.lastIndexOf(" "); if (ind>0){
				 * descrypedPwd = descrypedPwd.substring(ind,
				 * descrypedPwd.length()); }
				 * userJson.put("password",descrypedPwd);
				 */
				JSONObject retJson = userDao.login(userJson);
				returnSuccessResult(retJson, response);
			} else if (method.equals("changePass")) {// 修改密码
				JSONObject userJson = super.getJsonFromReq(request,"userInfo");
				String msgVerifyCode = userJson.getString("msgVerifyCode");
				String newpass = userJson.getString("newPassword");
				String cellphone = userJson.getString("cellphone");
				
				//根据号码找到用户id
				long userId = userDao.getUserIdByCellPhone(cellphone);
				// 判断短信验证码
				//将验证码和存储的验证码进行比较
				String cachedVerifyCode = CachedMsgVerifyCodeUtil.getCachedData(cellphone);
				if (!StringUtils.equalsIgnoreCase(msgVerifyCode, cachedVerifyCode)){
					returnFailResult("短信验证码不正确或已超时!", response);
					return;
				}
				// 修改密码
				userJson.clear();
				userJson.put("password", newpass);
				userJson.put("userId", userId);

				userDao.modUser(userJson);
				JSONObject retJson = new JSONObject();
				retJson.put("msg", "修改成功");
				returnSuccessResult(retJson, response);
			} else if (method.equals("getUserInfo")) {// 加载用户信息
				JSONObject userJson = super.getJsonFromReq(request,"userInfo");
				long userId = userJson.getLong("userId");
				// String token = userJson.getString("token");
				// 先判断token是否正确，如果不正确则提示重新登陆

				// 加载用户信息
				JSONObject retJson = userDao.getUserInfo(userId);
				returnSuccessResult(retJson, response);
			} else if (method.equals("logUserPosition")){//记录用户坐标
				JSONObject userJson = super.getJsonFromReq(request,"userInfo");
				long userId = userJson.getLong("userId");
				String latitude = userJson.getString("latitude");
				String longitude = userJson.getString("longitude");
				String cityName = userJson.getString("cityName");

				CachedUserPostitionUtil.addCacheData(userId, cityName, latitude, longitude);
				// 记录用户位置
				//userDao.logUserPosition(userId,cityName,latitude,longitude);
				//向这个登陆用户推送新消息数量
				WebSocketMessageInbound websocket = WebSocketMessageInboundPool.getConnection(userId);
				if (websocket!=null){
					long msgCount = userDao.getNewMsgCount(userId);
					JSONObject retJsonMsg = new JSONObject();
					retJsonMsg.put("msgType", "newMsgCount");
					retJsonMsg.put("msgCount", msgCount);
					websocket.sendMessage(retJsonMsg);
				}
					
				JSONObject retJson = new JSONObject();
				retJson.put("msg", "记录成功!");
				returnSuccessResult(retJson, response);
			}else if (method.equals("saveHeadPic")){//保存用户头像
				JSONObject userJson = super.getJsonFromReq(request,"userInfo");
				long userId = userJson.getLong("userId");
				String userHeadPicData = userJson.getString("userHeadPicData");

				// 保存用户头像
				userDao.saveHeadPic(userId,userHeadPicData);
				JSONObject retJson = new JSONObject();
				retJson.put("msg", "记录成功!");
				returnSuccessResult(retJson, response);
			} else if (method.equals("getAuthPic")){//加载用户认证图片
				JSONObject userJson = super.getJsonFromReq(request,"userInfo");
				long userId = userJson.getLong("userId");
				
				JSONArray imageList = userDao.getAuthPic(userId);
				returnSuccessResult(imageList, response);
			}else if (method.equals("saveAuthPic")){//保存用户认证图片
				JSONObject userJson = super.getJsonFromReq(request,"userInfo");
				long userId = userJson.getLong("userId");
				String authPicData = userJson.getString("authPicData");
				
				userDao.saveAuthPic(userId,authPicData);
				
				JSONObject retJson = new JSONObject();
				retJson.put("msg", "记录成功!");
				returnSuccessResult(retJson, response);
			}else if (method.equals("saveUserFeeling")){//保存用户心情
				JSONObject userJson = super.getJsonFromReq(request,"userInfo");
				long userId = userJson.getLong("userId");
				String newfeeling = userJson.getString("newfeeling");
				String newfeelingImagData = userJson.getString("newfeelingImagData");
				userDao.saveUserFeeling(userId,newfeeling,newfeelingImagData);
				
				JSONObject retJson = new JSONObject();
				retJson.put("msg", "记录成功!");
				returnSuccessResult(retJson, response);
			}else if (method.equals("getUserFeelingList")){//加载用户心情
				JSONObject userJson = super.getJsonFromReq(request,"userInfo");
				long userId = userJson.getLong("userId");
				long pageNo = userJson.getLong("page");
				
				JSONArray imageList = userDao.getUserFeelingList(userId,(int)pageNo);
				returnSuccessResult(imageList, response);
			}else if (method.equals("getUserAcctInfo")) {// 加载用户帐户信息
				JSONObject userJson = super.getJsonFromReq(request,"userInfo");
				long userId = userJson.getLong("userId");
				String acctType = userJson.getString("acctType");
				// 先判断token是否正确，如果不正确则提示重新登陆

				// 加载用户信息
				JSONObject retJson = userDao.getUserAcctInfo(userId,acctType);
				returnSuccessResult(retJson, response);
			}else if (method.equals("getAcctHisList")) {// 加载用户帐户信息
				JSONObject userJson = super.getJsonFromReq(request,"userInfo");
				long userId = userJson.getLong("userId");
				String acctId = userJson.getString("acctId");
				String page = userJson.getString("page");
				// 先判断token是否正确，如果不正确则提示重新登陆

				// 加载用户信息
				JSONArray retJson = userDao.getAcctHisList(Long.valueOf(acctId),Integer.valueOf(page));
				returnSuccessResult(retJson, response);
			}else if (method.equals("topup")){//帐户充值
				JSONObject acctJson = super.getJsonFromReq(request,"acctInfo");
				long userId = acctJson.getLong("userId");
				String acctType = acctJson.getString("acctType");
				String value = acctJson.getString("value");
				
				JSONObject retJson = new JSONObject();
				userDao.topup(userId,acctType,value,"帐户充值!");
				retJson.put("msg", "充值成功!");
				returnSuccessResult(retJson, response);
			}else if (method.equals("logPageOpenHis")){//记录页面打开历史
				JSONObject logInfo = super.getJsonFromReq(request, "logInfo");
				
				userDao.logPageOpenHis(logInfo);
			}else if (method.equals("getMonthFeeling")){//取得指定月签到日列表
				JSONObject userJson = super.getJsonFromReq(request,"userInfo");
				long userId = userJson.getLong("userId");
				String month = userJson.getString("month");
				
				JSONArray retJson = userDao.getMonthFeeling(userId,month);
				returnSuccessResult(retJson, response);
			}else if (method.equals("getDayFeeling")){//取得指定天的签到列表
				JSONObject userJson = super.getJsonFromReq(request,"userInfo");
				long userId = userJson.getLong("userId");
				String day = userJson.getString("day");
				
				JSONArray retJson = userDao.getDayFeeling(userId,day);
				returnSuccessResult(retJson, response);
			}else if (method.equals("delFeeling")){//删除心情
				JSONObject userJson = super.getJsonFromReq(request,"userInfo");
				long feelingId = userJson.getLong("feelingId");
				
				userDao.delFeeling(feelingId);
				JSONObject retJson = new JSONObject();
				retJson.put("msg", "记录成功!");
				returnSuccessResult(retJson, response);
			}else if (method.equals("addFriend")){//增加好友
				JSONObject userJson = super.getJsonFromReq(request,"userInfo");
				long userId = userJson.getLong("userId");
				long friendId = 0 ;
				if (userJson.containsKey("friendCellphone")){//按手机号码加好友
					String cellphone = userJson.getString("friendCellphone");
					friendId = userDao.getUserIdByCellPhone(cellphone);
				}else{
					friendId = userJson.getLong("friendId");
				}
				
				if (userId==friendId){
					throw new Exception("不能加自己为好友!");
				}
				
				userDao.addFriend(userId, friendId);
				//向friendId用户发送通知
				WebSocketMessageInbound websocket = WebSocketMessageInboundPool.getConnection(friendId);
				if (websocket!=null){
					long msgCount = userDao.getNewMsgCount(friendId);
					JSONObject retJsonMsg = new JSONObject();
					retJsonMsg.put("msgType", "newMsgCount");
					retJsonMsg.put("msgCount", msgCount);
					websocket.sendMessage(retJsonMsg);
				}
				
				JSONObject retJson = new JSONObject();
				retJson.put("msg", "增加成功!");
				returnSuccessResult(retJson, response);
			}else if (method.equals("getAllTypeNewMsg")){//取得用户所有类型的第一条消息
				JSONObject userJson = super.getJsonFromReq(request,"userInfo");
				long userId = userJson.getLong("userId");
				
				JSONArray retJson = userDao.getAllTypeNewMsg(userId);
				returnSuccessResult(retJson, response);
			}else if (method.equals("getFriendList")){//加载好友列表
				JSONObject userJson = super.getJsonFromReq(request,"userInfo");
				long userId = userJson.getLong("userId");
				
				JSONArray retJson = userDao.getFriendList(userId);
				returnSuccessResult(retJson, response);
			}else if (method.equals("getFriendMsgList")){//加载好友消息列表
				JSONObject userJson = super.getJsonFromReq(request,"userInfo");
				long userId = userJson.getLong("userId");
				long friendId = userJson.getLong("friendId");
				String msgType = userJson.getString("msgType");
				
				JSONArray retJson = userDao.getFriendMsgList(userId, friendId, msgType);
				returnSuccessResult(retJson, response);
			}else if (method.equals("getHeadPic")){//取用户头像id
				JSONObject userJson = super.getJsonFromReq(request,"userInfo");
				long userId = userJson.getLong("userId");
				
				JSONObject retJson = new JSONObject();
				long picId = userDao.getHeadPic(userId);
				retJson.put("imageId", picId);
				returnSuccessResult(retJson, response);
			}else if (method.equals("getNearbyUserList")){//取附件的非好友用户列表
				JSONObject userJson = super.getJsonFromReq(request,"userInfo");
				long userId = userJson.getLong("userId");
				long page = userJson.getLong("page");//页号
				String cityName = userJson.getString("cityName");//当前城市名称
				
				List users = CachedUserPostitionUtil.getNearByUsers(userId, cityName);
				
				if (users.size()>0){
					List retUsers = new ArrayList();
					if (page==0){
						page = 1;
					}
					long begin = (page-1)*20;
					long end = (page-1)*20 +19;
					
					for (int i=0;i<users.size();i++){
						if (i>=begin&&i<=end){
							retUsers.add(users.get(i));
						}
					}
					JSONArray retJson = userDao.getNearbyUserList(retUsers);
					returnSuccessResult(retJson, response);
				}else{
					returnSuccessResult(new JSONArray(), response);
				}
			}else if (method.equals("changeCellphone")){//更改手机号码
				JSONObject userJson = super.getJsonFromReq(request,"userInfo");
				String msgVerifyCode = userJson.getString("msgVerifyCode");
				String newCellphone = userJson.getString("newCellphone");
				long userId = userJson.getLong("userId");
				// 判断短信验证码
				//将验证码和存储的验证码进行比较
				String cachedVerifyCode = CachedMsgVerifyCodeUtil.getCachedData(newCellphone);
				if (!StringUtils.equalsIgnoreCase(msgVerifyCode, cachedVerifyCode)){
					returnFailResult("短信验证码不正确或已超时!", response);
					return;
				}
				// 修改手机
				userDao.changeCellphone(userId,newCellphone);
				JSONObject retJson = new JSONObject();
				retJson.put("msg", "修改成功");
				returnSuccessResult(retJson, response);
			}else if (method.equals("getUserList")){//根据条件加载用户列表
				JSONObject userJson = super.getJsonFromReq(request,"userInfo");
				String searchKey = userJson.getString("searchKey");
				long page = userJson.getLong("page");
				JSONArray retJson = userDao.getUserList(searchKey,page);
				returnSuccessResult(retJson, response);
			}else if (method.equals("addFocus")){
				JSONObject userJson = super.getJsonFromReq(request,"userInfo");
				long userId = userJson.getLong("userId");
				long friendId = userJson.getLong("friendId");
				
				userDao.addFocus(userId,friendId);
				JSONObject retJson = new JSONObject();
				retJson.put("msg", "关注成功");
				returnSuccessResult(retJson, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
			returnFailResult(ExceptionUtil.getMessage(e), response);
			return;
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -5222960410864824004L;

}
